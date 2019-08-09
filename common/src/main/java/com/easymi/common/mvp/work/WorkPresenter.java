package com.easymi.common.mvp.work;

import android.content.Context;
import android.text.TextUtils;

import com.easymi.common.CommApiService;
import com.easymi.common.R;
import com.easymi.common.entity.MqttConfig;
import com.easymi.common.entity.MqttResult;
import com.easymi.common.entity.MultipleOrder;
import com.easymi.common.entity.NearDriver;
import com.easymi.common.entity.NewToken;
import com.easymi.common.entity.PushMessage;
import com.easymi.common.push.CountEvent;
import com.easymi.common.push.MqttManager;
import com.easymi.common.push.WorkTimeCounter;
import com.easymi.common.result.LoginResult;
import com.easymi.common.result.QueryOrdersResult;
import com.easymi.common.result.SettingResult;
import com.easymi.common.result.SystemResult;
import com.easymi.common.result.VehicleResult;
import com.easymi.common.result.WorkStatisticsResult;
import com.easymi.component.Config;
import com.easymi.component.ZXOrderStatus;
import com.easymi.component.app.XApp;
import com.easymi.component.entity.BaseOrder;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.entity.Employ;
import com.easymi.component.entity.HandleBean;
import com.easymi.component.entity.SystemConfig;
import com.easymi.component.entity.Vehicle;
import com.easymi.component.entity.ZCSetting;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.ErrCode;
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.HttpResultFunc2;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.network.NoErrSubscriberListener;
import com.easymi.component.result.EmResult;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.utils.AesUtil;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.PhoneUtil;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.LoadingButton;
import com.easymin.driver.securitycenter.utils.CenterUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: FinishActivity
 *
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class WorkPresenter implements WorkContract.Presenter {

    private Context context;

    private WorkContract.View view;
    private WorkContract.Model model;

    public static WorkTimeCounter timeCounter;
    private Subscription subscription;
    private boolean isFirstLoadToken;
    private Subscription titleSubscription;

    public WorkPresenter(Context context, WorkContract.View view) {
        this.context = context;
        this.view = view;
        model = new WorkModel();
    }

    @Override
    public void indexOrders() {
        Observable<QueryOrdersResult> observable = model.indexOrders(EmUtil.getEmployId(), EmUtil.getAppKey());
        view.getRxManager().add(observable.subscribe(
                new MySubscriber<>(context, false, false, new HaveErrSubscriberListener<QueryOrdersResult>() {
                    @Override
                    public void onNext(QueryOrdersResult emResult) {
                        view.stopRefresh();
                        List<MultipleOrder> orders = new ArrayList<>();
                        MultipleOrder header = new MultipleOrder(MultipleOrder.ITEM_HEADER);
                        orders.add(header);
                        if (emResult.data != null) {
                            if (emResult.data.size() != 0) {
                                for (MultipleOrder order : emResult.data) {
                                    DymOrder dymOrder = null;
                                    //校验本地订单与服务器订单
                                    if (TextUtils.equals(order.serviceType, Config.ZHUANCHE)
                                            || TextUtils.equals(order.serviceType, Config.TAXI)
                                            || TextUtils.equals(order.serviceType, Config.CHARTERED)
                                            || TextUtils.equals(order.serviceType, Config.RENTAL)
                                            || TextUtils.equals(order.serviceType, Config.GOV)
                                            || TextUtils.equals(order.serviceType, Config.COUNTRY)
                                            || TextUtils.equals(order.serviceType, Config.CUSTOMBUS)) {
                                        if (DymOrder.exists(order.orderId, order.serviceType)) {
                                            //非专线 本地有
                                            dymOrder = DymOrder.findByIDType(order.orderId, order.serviceType);
                                            dymOrder.orderId = order.orderId;
                                            dymOrder.passengerId = order.passengerId;
                                            dymOrder.orderStatus = order.status;

                                            dymOrder.orderNo = order.orderNo;

                                            dymOrder.updateAll();
                                        } else {
                                            //非专线  本地没有
                                            dymOrder = new DymOrder(order.orderId, order.serviceType,
                                                    order.passengerId, order.status);
                                            dymOrder.id = order.id;

                                            dymOrder.orderNo = order.orderNo;

                                            dymOrder.saveOrUpdate();
                                        }
                                    } else if (TextUtils.equals(order.serviceType, Config.CITY_LINE)) {
                                        if (DymOrder.exists(order.scheduleId, order.serviceType)) {
                                            //专线 本地有 状态同步
                                            dymOrder = DymOrder.findByIDType(order.scheduleId, order.serviceType);
                                            if (order.scheduleStatus <= BaseOrder.ZX_SCHEDULE_STATUS_PREPARE) {
                                                dymOrder.orderStatus = ZXOrderStatus.WAIT_START;
                                            } else if (order.scheduleStatus == BaseOrder.ZX_SCHEDULE_STATUS_TAKE) {
                                                dymOrder.orderStatus = ZXOrderStatus.ACCEPT_ING;
                                            } else if (order.scheduleStatus == BaseOrder.ZX_SCHEDULE_STATUS_RUN) {
                                                dymOrder.orderStatus = ZXOrderStatus.SEND_ING;
                                            } else if (order.scheduleStatus == BaseOrder.ZX_SCHEDULE_STATUS_FINISH) {
                                                dymOrder.orderStatus = ZXOrderStatus.SEND_OVER;
                                            }
                                            dymOrder.updateStatus();
                                        } else {
                                            //专线 本地没有
                                            dymOrder = new DymOrder();
                                            dymOrder.id = order.id;
                                            dymOrder.orderStatus = ZXOrderStatus.WAIT_START;
                                            dymOrder.orderId = order.scheduleId;
                                            dymOrder.orderType = order.serviceType;
                                            dymOrder.saveOrUpdate();
                                        }
                                    } else if (TextUtils.equals(order.serviceType, Config.CARPOOL)) {
                                        if (DymOrder.exists(order.scheduleId, order.serviceType)) {
                                            //专线 本地有 状态同步
                                            dymOrder = DymOrder.findByIDType(order.scheduleId, order.serviceType);
                                            if (order.scheduleStatus <= BaseOrder.PC_SCHEDULE_STATUS_NEW) {
                                                dymOrder.orderStatus = ZXOrderStatus.WAIT_START;
                                            } else if (order.scheduleStatus == BaseOrder.PC_SCHEDULE_STATUS_TAKE) {
                                                dymOrder.orderStatus = ZXOrderStatus.ACCEPT_ING;
                                            } else if (order.scheduleStatus == BaseOrder.PC_SCHEDULE_STATUS_RUN) {
                                                dymOrder.orderStatus = ZXOrderStatus.SEND_ING;
                                            } else if (order.scheduleStatus == BaseOrder.PC_SCHEDULE_STATUS_FINISH) {
                                                dymOrder.orderStatus = ZXOrderStatus.SEND_OVER;
                                            }
                                            dymOrder.updateStatus();
                                        } else {
                                            //专线 本地没有
                                            dymOrder = new DymOrder();
                                            dymOrder.id = order.id;
                                            dymOrder.orderStatus = ZXOrderStatus.WAIT_START;
                                            dymOrder.orderId = order.scheduleId;
                                            dymOrder.orderType = order.serviceType;
                                            dymOrder.saveOrUpdate();
                                        }
                                    }
                                    order.viewType = MultipleOrder.ITEM_POSTER;
                                    orders.add(order);
                                }

                                List<DymOrder> allDym = DymOrder.findAll();
                                for (DymOrder dymOrder : allDym) {
                                    boolean isExist = false;
                                    for (MultipleOrder order : orders) {
                                        if (dymOrder.orderType.equals(Config.CITY_LINE)
                                                || dymOrder.orderType.equals(Config.CARPOOL)) {
                                            if ((dymOrder.orderId == order.scheduleId)) {
                                                isExist = true;
                                                break;
                                            }
                                        } else if (dymOrder.orderType.equals(Config.ZHUANCHE)
                                                || dymOrder.orderType.equals(Config.TAXI)
                                                || TextUtils.equals(order.serviceType, Config.CHARTERED)
                                                || TextUtils.equals(order.serviceType, Config.RENTAL)
                                                || TextUtils.equals(order.serviceType, Config.GOV)
                                                || TextUtils.equals(order.serviceType, Config.COUNTRY)
                                                || TextUtils.equals(order.serviceType, Config.CUSTOMBUS)) {
                                            if (dymOrder.orderId == order.orderId) {
                                                isExist = true;
                                                break;
                                            }
                                        }
                                    }
                                    if (!isExist) {
                                        dymOrder.delete();
                                    }
                                }
                            } else {
                                DymOrder.deleteAll();
                            }
                            startLocService();//重启定位更改定位周期
                            view.showOrders(orders);
                        } else {
                            DymOrder.deleteAll();
                            startLocService();//重启定位更改定位周期
                            view.showOrders(orders);
                        }
                    }

                    @Override
                    public void onError(int code) {
                        view.stopRefresh();
                    }
                })));
//        }
    }

    private boolean isStartMqtt;

    /**
     * 检查clentid是否存活
     */
    private void checkTopic() {
        view.getRxManager().add(ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .getCurrentTopic(Config.MQTT_CONNECTION_URL + Config.MQTT_CLIENT_ID)
                .map(new HttpResultFunc2<>(0))
                .doOnNext(new Action1<List<MqttResult>>() {
                    @Override
                    public void call(List<MqttResult> mqttResults) {
                        if (!(mqttResults != null && mqttResults.isEmpty())) {
                            throw new RuntimeException();
                        }
                    }
                })
                .retryWhen(observable -> observable.delay(30, TimeUnit.SECONDS))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MySubscriber<>(context, false, false, new NoErrSubscriberListener<List<MqttResult>>() {
                    @Override
                    public void onNext(List<MqttResult> mqttResults) {
                        if (mqttResults != null && mqttResults.isEmpty()) {
                            MqttManager.release();
                            isStartMqtt = false;
                            getMqttConfig();
                        }
                    }
                })));
    }

    public void getMqttConfig() {
        if (isStartMqtt) {
            return;
        }
        isStartMqtt = true;
        view.getRxManager().add(ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .getConfig()
                .map(new HttpResultFunc2<>())
                .doOnNext(new Action1<MqttConfig>() {
                    @Override
                    public void call(MqttConfig mqttConfig) {
                        if (TextUtils.isEmpty(mqttConfig.connectionsUrl) || TextUtils.isEmpty(mqttConfig.clientId)) {
                            throw new RuntimeException();
                        }
                    }
                })
                .retryWhen(observable -> observable.delay(5, TimeUnit.SECONDS))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MySubscriber<MqttConfig>(context, false, false, new NoErrSubscriberListener<MqttConfig>() {
                    @Override
                    public void onNext(MqttConfig mqttConfig) {
                        Config.MQTT_USER_NAME = mqttConfig.userName;
                        Config.MQTT_PSW = mqttConfig.password;
                        Config.MQTT_HOST = mqttConfig.broker;
                        Config.PORT_HTTP = mqttConfig.portHttp;
                        Config.PORT_TCP = mqttConfig.portTcp;
                        Config.MQTT_TOPIC = mqttConfig.topic;
                        Config.ACK_TOPIC = mqttConfig.ackTopic;
                        Config.MQTT_CONNECTION_URL = mqttConfig.connectionsUrl;
                        Config.MQTT_CLIENT_ID = mqttConfig.clientId;
                        MqttManager.getInstance().creatConnect();
                        checkTopic();
                    }
                })));
    }

    @Override
    public void startLocService() {
        XApp.getInstance().startLocService();
    }

    @Override
    public void online(LoadingButton btn) {
        long driverId = EmUtil.getEmployId();
        Observable<EmResult> observable = model.online(driverId, EmUtil.getAppKey());
        view.getRxManager().add(observable.subscribe(new MySubscriber<>(context, btn, emResult -> {
            //一键报警 上线
            CenterUtil centerUtil = new CenterUtil(context, Config.APP_KEY,
                    XApp.getMyPreferences().getString(Config.AES_PASSWORD, AesUtil.AAAAA),
                    XApp.getMyPreferences().getString(Config.SP_TOKEN, ""));
            centerUtil.driverUp(driverId, EmUtil.getEmployInfo().companyId, EmUtil.getEmployInfo().userName, EmUtil.getEmployInfo().realName,
                    EmUtil.getEmployInfo().phone, System.currentTimeMillis() / 1000, EmUtil.getEmployInfo().serviceType);

            view.onlineSuc();
            XApp.getEditor().putLong(Config.ONLINE_TIME, System.currentTimeMillis()).apply();
            uploadTime(2);

        })));
    }

    @Override
    public void offline() {
        long driverId = EmUtil.getEmployId();
        Observable<EmResult> observable = model.offline(driverId, EmUtil.getAppKey());
        view.getRxManager().add(observable.subscribe(new MySubscriber<>(context, true,
                true, emResult -> {
            //一键报警  下线
            CenterUtil centerUtil = new CenterUtil(context, Config.APP_KEY,
                    XApp.getMyPreferences().getString(Config.AES_PASSWORD, AesUtil.AAAAA),
                    XApp.getMyPreferences().getString(Config.SP_TOKEN, ""));
            centerUtil.driverDown(driverId, EmUtil.getEmployInfo().companyId, EmUtil.getEmployInfo().userName, EmUtil.getEmployInfo().realName,
                    EmUtil.getEmployInfo().phone, System.currentTimeMillis() / 1000, EmUtil.getEmployInfo().serviceType);
            HandleBean.deleteAll();
            PushMessage.deleteAll();
            XApp.getEditor()
                    .putLong(Config.ONLINE_TIME, 0)
                    .apply();
            if (Config.IS_ENCRYPT && TextUtils.equals(Config.APP_KEY, "1HAcient1kLqfeX7DVTV0dklUkpGEnUC")) {
            } else {
                uploadTime(1);
            }
            view.offlineSuc();

        })));
    }

    @Override
    public void queryNearDriver(Double lat, Double lng) {
        if (subscription != null || employType == null || ZCSetting.findOne().emploiesKm <= 0) {
            return;
        }
        subscription = model.queryNearDriver(lat, lng, ZCSetting.findOne().emploiesKm, employType)
                .subscribe(new MySubscriber<List<NearDriver>>(context, false, false,
                        new NoErrSubscriberListener<List<NearDriver>>() {
                            @Override
                            public void onNext(List<NearDriver> nearDrivers) {
                                view.showDrivers(nearDrivers);
                            }
                        }));
        view.getRxManager().add(subscription);
    }

    @Override
    public void loadDataOnResume() {
        uploadTime(-1);
        PhoneUtil.checkGps(context);
        getMqttConfig();
        refreshToken();
    }

    public void refreshToken() {
        if (isFirstLoadToken) {
            return;
        }
        view.getRxManager().add(ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .refreshToken(XApp.getMyPreferences().getString(Config.SP_TOKEN, ""))
                .map(new HttpResultFunc2<>())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new MySubscriber<NewToken>(context, false, false, companyInfo -> {
                    XApp.getEditor()
                            .putString(Config.SP_TOKEN, companyInfo.token)
                            .apply();
                    isFirstLoadToken = true;
                })));
    }

    /**
     * 强制推送数据。
     */
    private void uploadTime(int statues) {
        if (null == timeCounter) {
            timeCounter = new WorkTimeCounter(context);
        }
        timeCounter.forceUpload(statues);
    }

    //表示司机业务
    private String employType;


    public void getSetting() {
        Observable<SettingResult> observable = ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .getAppSetting(EmUtil.getEmployInfo().companyId)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        view.getRxManager().add(observable.subscribe(new MySubscriber<>(context, false, false, settingResult -> {
            if (settingResult.data != null) {
                for (ZCSetting sub : settingResult.data) {
                    if (EmUtil.getEmployInfo().serviceType.equals(sub.serviceType)) {
                        ZCSetting.deleteAll();
                        sub.save();
                    }
                }
            }
        })));
    }

    @Override
    public void loadEmploy(long id) {
        Observable<LoginResult> observable = model.getEmploy(id, EmUtil.getAppKey());
        view.getRxManager().add(observable.subscribe(new MySubscriber<>(context, false,
                true, new HaveErrSubscriberListener<LoginResult>() {
            @Override
            public void onNext(LoginResult result) {
                Employ employ = result.getEmployInfo();
                employType = employ.serviceType;
                String udid = XApp.getMyPreferences().getString(Config.SP_UDID, "");
                if (StringUtils.isNotBlank(employ.deviceNo)
                        && StringUtils.isNotBlank(udid)) {
                    if (!employ.deviceNo.equals(udid)) {
                        ToastUtil.showMessage(context, context.getString(R.string.unbunding));
                        EmUtil.employLogout(context);
                        return;
                    }
                }
                employ.saveOrUpdate();
                getTitleStatus();
                XApp.getEditor()
                        .putLong(Config.SP_DRIVERID, employ.id)
                        .apply();
                view.showDriverStatus();
            }

            @Override
            public void onError(int code) {
                if (code == ErrCode.QUERY_ERROR.getCode()) {
                    EmUtil.employLogout(context);
                }
            }
        })));
    }

    public void driverehicle(Employ employ) {
        Observable<VehicleResult> observable = ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .driverehicle()
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        view.getRxManager().add(observable.subscribe(new MySubscriber<>(context, false,
                true, result -> {
            if (result == null || result.getCode() != 1) {
                ToastUtil.showMessage(context, "未绑定该业务车辆，不能接单");
            } else {
                String driverService = EmUtil.getEmployInfo().serviceType;
                if (result.data != null && result.data.size() > 0) {
                    for (Vehicle vehicle : result.data) {
                        if (TextUtils.equals(vehicle.serviceType, driverService)) {
                            vehicle.saveOrUpdate(employ.id);
                        }
                    }
                } else {
                    if (employ.serviceType.contains(Config.ZHUANCHE)
                            || employ.serviceType.contains(Config.TAXI)
                    )
                        ToastUtil.showMessage(context, "未绑定该业务车辆，不能接单");
                }
            }
        })));
    }


    //能拨打电话
    boolean canCallPhone = true;

    @Override
    public void getAppSetting(long driverId) {
        Observable<SettingResult> observable = model.getAppSetting(driverId);
        view.getRxManager().add(observable.subscribe(new MySubscriber<>(context, false,
                true, result -> {

//            //解析业务配置
//            List<SubSetting> settingList = GsonUtil.parseToList(result.appSetting, SubSetting[].class);
//            if (settingList != null) {
//                for (SubSetting sub : settingList) {
//                    if (Config.ZHUANCHE.equals(sub.businessType)) {
//                        ZCSetting zcSetting = GsonUtil.parseJson(sub.subJson, ZCSetting.class);
//                        if (zcSetting != null) {
//                            ZCSetting.deleteAll();
//                            zcSetting.save();
//                            zcDriverKm = zcSetting.emploiesKm;
//                        }
//                    } else if ("daijia".equals(sub.businessType)) {
//                        TaxiSetting djSetting = GsonUtil.parseJson(sub.subJson, TaxiSetting.class);
//                        if (djSetting != null) {
//                            TaxiSetting.deleteAll();
//                            djSetting.save();
//                            driverKm = djSetting.emploiesKm;
//                        }
//                    }
//                }
//            }
        })));


        Observable<SystemResult> observable2 = model.getSysConfig();
        view.getRxManager().add(observable2.subscribe(new MySubscriber<>(context, false,
                true, result -> {
            SystemConfig.deleteAll();
            SystemConfig systemConfig = result.system;
            systemConfig.payType = result.driverPayType;
            if (systemConfig.payMoney1 == 0 || systemConfig.payMoney2 == 0 || systemConfig.payMoney3 == 0) {
                systemConfig.payMoney1 = 50;
                systemConfig.payMoney2 = 100;
                systemConfig.payMoney3 = 200;
            }

            canCallPhone = systemConfig.canCallDriver == 1;

            systemConfig.save();
        })));
    }

    @Override
    public void loadNoticeAndAnn() {
//        Employ employ = EmUtil.getEmployInfo();
//        if (null == employ) {
//            return;
//        }
//        Observable<AnnouncementResult> annObservable = model.loadAnn(employ.companyId);
//        Observable<NotitfyResult> notObservable = model.loadNotice(employ.id);
//
//        Observable.zip(annObservable, notObservable, (announcementResult, notitfyResult) -> {
//            List<AnnAndNotice> list = new ArrayList<>();
//
//            if (null != announcementResult && announcementResult.employAffiches != null
//                    && announcementResult.employAffiches.size() != 0) {
//                AnnAndNotice annHeader = new AnnAndNotice();
//                annHeader.type = 0;
//                annHeader.viewType = AnnAndNotice.ITEM_HEADER;
//                list.add(annHeader);
//                for (AnnAndNotice employAffich : announcementResult.employAffiches) {
//                    employAffich.type = 0;
//                    employAffich.viewType = MultipleOrder.ITEM_POSTER;
//                    list.add(employAffich);
//                }
//            }
//
//            if (null != notitfyResult && notitfyResult.employNoticeRecords != null
//                    && notitfyResult.employNoticeRecords.size() != 0) {
//                AnnAndNotice noticeHeader = new AnnAndNotice();
//                noticeHeader.type = 1;
//                noticeHeader.viewType = AnnAndNotice.ITEM_HEADER;
//                list.add(noticeHeader);
//                boolean have = false;
//                for (AnnAndNotice record : notitfyResult.employNoticeRecords) {
//                    if (record.state == 1) {
//                        have = true;
//                        record.type = 1;
//                        record.viewType = MultipleOrder.ITEM_POSTER;
//                        list.add(record);
//                    }
//                }
//
//                if (!have) {
//                    list.remove(list.size() - 1);
//                }
//            }
//
//            return list;
//        })
//                .subscribe(new MySubscriber<>(context, false, false, new HaveErrSubscriberListener<List<AnnAndNotice>>() {
//                    @Override
//                    public void onNext(List<AnnAndNotice> annAndNotices) {
//                        view.stopRefresh();
//                        view.showHomeAnnAndNotice(annAndNotices);
//                    }
//
//                    @Override
//                    public void onError(int code) {
//                        view.stopRefresh();
//                        view.showHomeAnnAndNotice(null);
//                    }
//                }));
    }

    @Override
    public void getTitleStatus() {
        if (titleSubscription == null) {
            titleSubscription = model.getTitleStatus()
                    .subscribe(new MySubscriber<>(context, false, false,
                            s -> view.setTitleStatus(s)));
            view.getRxManager().add(titleSubscription);
        }
    }

    public void workStatistics() {
        CommApiService api = ApiManager.getInstance().createApi(Config.HOST, CommApiService.class);

        Observable<WorkStatisticsResult> observable = api
                .workStatistics()
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        new RxManager().add(observable.subscribe(new MySubscriber<>(context, false,
                true, result -> {
            if (result != null && result.workStatistics != null) {

                CountEvent event = new CountEvent();
                event.finishCount = result.workStatistics.finishCount;
                event.income = result.workStatistics.income;
                event.orderTotalAmount = result.workStatistics.orderTotalAmount;
                event.minute = -1;
                EventBus.getDefault().post(event);
            }
        })));
    }

}
