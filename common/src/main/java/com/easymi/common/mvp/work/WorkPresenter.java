package com.easymi.common.mvp.work;

import android.content.Context;
import android.content.SharedPreferences;

import com.easymi.common.R;
import com.easymi.common.entity.AnnAndNotice;
import com.easymi.common.entity.MultipleOrder;
import com.easymi.common.entity.NearDriver;
import com.easymi.common.push.MqttManager;
import com.easymi.common.push.WorkTimeCounter;
import com.easymi.common.result.AnnouncementResult;
import com.easymi.common.result.LoginResult;
import com.easymi.common.result.NearDriverResult;
import com.easymi.common.result.NotitfyResult;
import com.easymi.common.result.QueryOrdersResult;
import com.easymi.common.result.SettingResult;
import com.easymi.common.result.SystemResult;
import com.easymi.component.Config;
import com.easymi.component.EmployStatus;
import com.easymi.component.app.XApp;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.entity.Employ;
import com.easymi.component.entity.Setting;
import com.easymi.component.entity.SubSetting;
import com.easymi.component.entity.SystemConfig;
import com.easymi.component.entity.ZCSetting;
import com.easymi.component.network.ErrCode;
import com.easymi.component.network.GsonUtil;
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.result.EmResult;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.Log;
import com.easymi.component.utils.PhoneUtil;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.LoadingButton;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * Created by developerLzh on 2017/11/17 0017.
 */

public class WorkPresenter implements WorkContract.Presenter {

    private Context context;

    private WorkContract.View view;
    private WorkContract.Model model;

    public static WorkTimeCounter timeCounter;

    public WorkPresenter(Context context, WorkContract.View view) {
        this.context = context;
        this.view = view;
        model = new WorkModel();
    }

    /**
     * 开启保活服务
     */
    @Override
    public void initDaemon() {
//        if (Build.VERSION.SDK_INT > 21) {//21版本以上使用JobScheduler
//            try {
//                ComponentName mServiceComponent = new ComponentName(context, JobKeepLiveService.class);
//                JobInfo.Builder builder = new JobInfo.Builder(0x139888, mServiceComponent);
//                builder.setPersisted(true);//持续的
//                builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);//任何网络情况下
//                builder.setRequiresDeviceIdle(false);//是否需要设备闲置
//                builder.setRequiresCharging(false);//是否需要充电状态下
//
//                JobScheduler tm = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
//                if (tm != null) {
//                    tm.schedule(builder.build());
//                }
//            } catch (Exception e) {
//                Log.e("DriverApp", "初始化失败JobScheduler失败");
//            }
//        } else {//21版本以下使用native保活
//            //开起保活service
//            Intent daemonIntent = new Intent(context, DaemonService.class);
//            daemonIntent.setPackage(context.getPackageName());
//            context.startService(daemonIntent);
//        }
    }

    @Override
    public void indexOrders() {
        view.showOrders(null);

        long driverId = EmUtil.getEmployId();
        String serviceType = EmUtil.getEmployInfo().serviceType;

        Observable<QueryOrdersResult> observable = model.indexOrders(driverId, EmUtil.getAppKey());
        view.getRxManager().add(observable.subscribe(new MySubscriber<>(context, false, false, new HaveErrSubscriberListener<QueryOrdersResult>() {
            @Override
            public void onNext(QueryOrdersResult emResult) {
                view.stopRefresh();
                List<MultipleOrder> orders = emResult.data;
                List<MultipleOrder> nowOrders = new ArrayList<>();
                List<MultipleOrder> yuyueOrders = new ArrayList<>();
                if (orders != null) {
                    for (MultipleOrder order : orders) {
                        DymOrder dymOrder;
                        if (DymOrder.exists(order.id, order.serviceType)) {//校验本地订单与服务器订单
                            dymOrder = DymOrder.findByIDType(order.id, order.serviceType);
                            dymOrder.orderStatus = order.status;
                            dymOrder.updateStatus();
                        } else {//服务器有本地没得 创建数据
                            dymOrder = new DymOrder(order.id, order.serviceType,
                                    order.passengerId, order.status);
                            dymOrder.save();
                        }

                        order.viewType = MultipleOrder.ITEM_POSTER;
                        if (order.isBookOrder == 2) {
                            nowOrders.add(order);
                        } else {
                            yuyueOrders.add(order);
                        }
                    }

                    List<DymOrder> allDym = DymOrder.findAll();
                    for (DymOrder dymOrder : allDym) {
                        boolean isExist = false;
                        for (MultipleOrder order : orders) {
                            if (dymOrder.orderId == order.id
                                    && dymOrder.orderType.equals(order.serviceType)) {
                                isExist = true;
                                break;
                            }
                        }
                        if (!isExist) {
                            dymOrder.delete();
                        }
                    }
                    orders.clear();
                    //预约header
                    if (yuyueOrders.size() != 0) {
                        MultipleOrder header1 = new MultipleOrder(MultipleOrder.ITEM_HEADER);
                        header1.isBookOrder = 1;
                        orders.add(header1);
                    }

                    //预约单
                    orders.addAll(yuyueOrders);

                    //即时header
                    if (nowOrders.size() != 0) {
                        MultipleOrder header2 = new MultipleOrder(MultipleOrder.ITEM_HEADER);
                        header2.isBookOrder = 2;
                        orders.add(header2);
                    }
                    //即时单
                    orders.addAll(nowOrders);
                } else {
                    DymOrder.deleteAll();
                    orders = new ArrayList<>();
                }

                startLocService();//重启定位更改定位周期

                view.showOrders(orders);
            }

            @Override
            public void onError(int code) {
                view.stopRefresh();
                startLocService();//重启定位更改定位周期
                view.showOrders(null);
            }
        })));
//        view.showOrders(initRecyclerData());

//        Observable<QueryOrdersResult> observable1;
//        if (serviceType.equals(Config.ZHUANCHE)){
//            observable1 = model.indexOrders(driverId, EmUtil.getAppKey());
//        }else if (serviceType.equals(Config.TAXI)){
//            observable1 = model.indexOrders(driverId, EmUtil.getAppKey());
//        }
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
            view.onlineSuc();
            uploadTime(2);
        })));
    }

    @Override
    public void offline() {
        long driverId = EmUtil.getEmployId();
        Observable<EmResult> observable = model.offline(driverId, EmUtil.getAppKey());
        view.getRxManager().add(observable.subscribe(new MySubscriber<>(context, true,
                true, emResult -> {
            view.offlineSuc();
            uploadTime(1);
        })));
    }

    @Override
    public void queryNearDriver(Double lat, Double lng) {
        long driverId = EmUtil.getEmployId();
        double dis = 0;
        if (Config.ZHUANCHE.equals(employType)) {
            dis = zcDriverKm;
        } else if ("daijia".equals(employType)) {
            dis = driverKm;
        }

        if (dis <= 0) {
            //距离为0时不调用接口.
            return;
        }

        Observable<NearDriverResult> observable = model.queryNearDriver(driverId, lat, lng, dis, employType);
        view.getRxManager().add(observable.subscribe(new MySubscriber<>(context, false,
                true, nearDriverResult -> {
            if (null != nearDriverResult.emploies && nearDriverResult.emploies.size() != 0) {
                List<NearDriver> nearDrivers = new ArrayList<>();
                for (NearDriver employ : nearDriverResult.emploies) {
                    if (!employ.status.equals(EmployStatus.ONLINE)
                            && !employ.status.equals(EmployStatus.OFFLINE)
                            && !employ.status.equals(EmployStatus.FROZEN)) {
                        nearDrivers.add(employ);
                    }
                }
                view.showDrivers(nearDrivers);
            } else {
                view.showDrivers(nearDriverResult.emploies);
            }
        })));
    }

    @Override
    public void loadDataOnResume() {
        long driverId = EmUtil.getEmployId();
        loadEmploy(driverId);
        getAppSetting(driverId);//获取配置信息
        uploadTime(-1);
        PhoneUtil.checkGps(context);
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

    @Override
    public void onPause() {
    }

    //表示司机业务
    private String employType;

    @Override
    public void loadEmploy(long id) {
        Observable<LoginResult> observable = model.getEmploy(id, EmUtil.getAppKey());
        view.getRxManager().add(observable.subscribe(new MySubscriber<>(context, false,
                true, new HaveErrSubscriberListener<LoginResult>() {
            @Override
            public void onNext(LoginResult result) {
                Employ employ = result.getEmployInfo();
//                if (employ.auditType == 2 || employ.auditType == 3 || employ.auditType == 4) {
//                    view.stopRefresh();
//                    view.showRegisterDialog(employ.company_phone, employ.auditType, employ.reject);
//                    return;
//                }
//
//                view.hideRegisterDialog();

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
                SharedPreferences.Editor editor = XApp.getPreferencesEditor();
                editor.putLong(Config.SP_DRIVERID, employ.id);
                editor.apply();
                view.showDriverStatus();
                MqttManager.getInstance().creatConnect();//在查询完服务人员后初始化mqtt
            }

            @Override
            public void onError(int code) {
                view.stopRefresh();
                if (code == ErrCode.QUERY_ERROR.getCode()) {
                    EmUtil.employLogout(context);
                }
            }
        })));
    }

    //查询附近司机的距离
    private double driverKm = 0;
    private double zcDriverKm = 0;

    //能拨打电话
    boolean canCallPhone = true;

    @Override
    public void getAppSetting(long driverId) {
        Observable<SettingResult> observable = model.getAppSetting(driverId);
        view.getRxManager().add(observable.subscribe(new MySubscriber<>(context, false,
                true, result -> {

            //解析业务配置
            List<SubSetting> settingList = GsonUtil.parseToList(result.appSetting, SubSetting[].class);
            if (settingList != null) {
                for (SubSetting sub : settingList) {
                    if (Config.ZHUANCHE.equals(sub.businessType)) {
                        ZCSetting zcSetting = GsonUtil.parseJson(sub.subJson, ZCSetting.class);
                        if (zcSetting != null) {
                            ZCSetting.deleteAll();
                            zcSetting.save();
                            zcDriverKm = zcSetting.emploiesKm;
                        }
                    } else if ("daijia".equals(sub.businessType)) {
                        Setting djSetting = GsonUtil.parseJson(sub.subJson, Setting.class);
                        if (djSetting != null) {
                            Setting.deleteAll();
                            djSetting.save();
                            driverKm = djSetting.emploiesKm;
                        }
                    }
                }
            }


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
        Employ employ = EmUtil.getEmployInfo();
        if (null == employ) {
            return;
        }
        Observable<AnnouncementResult> annObservable = model.loadAnn(employ.company_id);
        Observable<NotitfyResult> notObservable = model.loadNotice(employ.id);

        Observable.zip(annObservable, notObservable, (announcementResult, notitfyResult) -> {
            List<AnnAndNotice> list = new ArrayList<>();

            if (null != announcementResult && announcementResult.employAffiches != null
                    && announcementResult.employAffiches.size() != 0) {
                AnnAndNotice annHeader = new AnnAndNotice();
                annHeader.type = 0;
                annHeader.viewType = AnnAndNotice.ITEM_HEADER;
                list.add(annHeader);
                for (AnnAndNotice employAffich : announcementResult.employAffiches) {
                    employAffich.type = 0;
                    employAffich.viewType = MultipleOrder.ITEM_POSTER;
                    list.add(employAffich);
                }
            }

            if (null != notitfyResult && notitfyResult.employNoticeRecords != null
                    && notitfyResult.employNoticeRecords.size() != 0) {
                AnnAndNotice noticeHeader = new AnnAndNotice();
                noticeHeader.type = 1;
                noticeHeader.viewType = AnnAndNotice.ITEM_HEADER;
                list.add(noticeHeader);
                boolean have = false;
                for (AnnAndNotice record : notitfyResult.employNoticeRecords) {
                    if (record.state == 1) {
                        have = true;
                        record.type = 1;
                        record.viewType = MultipleOrder.ITEM_POSTER;
                        list.add(record);
                    }
                }

                if (!have) {
                    list.remove(list.size() - 1);
                }
            }

            return list;
        })
                .subscribe(new MySubscriber<>(context, false, false, new HaveErrSubscriberListener<List<AnnAndNotice>>() {
                    @Override
                    public void onNext(List<AnnAndNotice> annAndNotices) {
                        view.stopRefresh();
                        view.showHomeAnnAndNotice(annAndNotices);
                    }

                    @Override
                    public void onError(int code) {
                        view.stopRefresh();
                        view.showHomeAnnAndNotice(null);
                    }
                }));
    }


    public void deleteNotice(long id) {
        if (id == 0) {
            return;
        }
        Observable<EmResult> observable = model.readOne(id);
        view.getRxManager().add(observable.subscribe(new MySubscriber<>(context, false,
                true, result -> {
            //do nothing
        })));
    }

}
