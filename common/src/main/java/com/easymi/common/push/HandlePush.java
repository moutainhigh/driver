package com.easymi.common.push;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;

import com.alibaba.android.arouter.launcher.ARouter;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.easymi.common.CommApiService;
import com.easymi.common.R;
import com.easymi.common.entity.Address;
import com.easymi.common.entity.MultipleOrder;
import com.easymi.common.entity.PassengerLocation;
import com.easymi.common.entity.PushAnnouncement;
import com.easymi.common.mvp.grab.GrabActivity2;
import com.easymi.common.mvp.work.WorkActivity;
import com.easymi.common.result.MultipleOrderResult;
import com.easymi.common.result.NotitfyResult;
import com.easymi.common.result.SettingResult;
import com.easymi.component.Config;
import com.easymi.component.DJOrderStatus;
import com.easymi.component.app.XApp;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.entity.Employ;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.GsonUtil;
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.utils.AesUtil;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.Log;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.EmployStatus;
import com.easymin.driver.securitycenter.utils.CenterUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/1/11.
 */

public class HandlePush implements FeeChangeSubject, PassengerLocSubject {

    private RxManager rxManager;

    public static int NOTIFY_ID = 1993;

    private static List<FeeChangeObserver> observers;
    private static List<PassengerLocObserver> plObservers;
    private static HandlePush instance;

    private HandlePush() {
        rxManager = new RxManager();
    }

    public static HandlePush getInstance() {
        if (instance == null) {
            instance = new HandlePush();
        }
        return instance;
    }

    public void handPush(String jsonStr) {

        rxManager = new RxManager();
        try {
            JSONObject jb = new JSONObject(jsonStr);
            String msg = jb.optString("msg");

            if (msg.equals("robbing")) { //抢单
                MultipleOrder order = new MultipleOrder();
                order.orderId = jb.optJSONObject("data").optLong("orderId");
                order.serviceType = jb.optJSONObject("data").optString("serviceType");
                if (!DymOrder.exists(order.orderId, order.serviceType)) {
                    loadOrder(order);
                }
//                XApp.getInstance().syntheticVoice();
            } else if (msg.equals("sendorders")) {//派单
                MultipleOrder order = new MultipleOrder();
                order.orderId = jb.optJSONObject("data").optLong("orderId");
                order.serviceType = jb.optJSONObject("data").optString("serviceType");
                if (!DymOrder.exists(order.orderId, order.serviceType)) {
                    loadOrder(order);
                }
                newShowNotify(XApp.getInstance(), "", XApp.getInstance().getString(R.string.send_order), XApp.getInstance().getString(R.string.send_order_content));
            } else if (msg.equals("cancel")) {//取消订单
                MultipleOrder order = new MultipleOrder();
                order.orderId = jb.optJSONObject("data").optLong("orderId");
                order.serviceType = jb.optJSONObject("data").optString("serviceType");

                Message message = new Message();
                message.what = 1;
                Bundle bundle = new Bundle();
                bundle.putSerializable("order", order);
                message.setData(bundle);
                handler.sendMessage(message);
            } else if (msg.equals("driver_status")) { //司机状态
                String status = jb.optJSONObject("data").optString("status");

                Message message = new Message();
                message.what = 2;
                Bundle bundle = new Bundle();
                bundle.putSerializable("status", status);
                message.setData(bundle);
                handler.sendMessage(message);
            } else if (msg.equals("notice")) {//通知   （改为公告）
//                long id = jb.optJSONObject("data").optLong("id");
//                String data = jb.optString("data");
//                JSONObject dt = new JSONObject(data);
                long id = jb.optLong("data");

                loadAnn(id);
            } else if (msg.equals("message")) {//公告    （改为通知）
//                long id = jb.optJSONObject("data").optLong("id");
                String data = jb.optString("data");
                JSONObject dt = new JSONObject(data);

                loadNotice(dt.optLong("id"));
            } else if (msg.equals("thaw")) {//冻结
                XApp.getInstance().shake();
                XApp.getInstance().syntheticVoice(XApp.getInstance().getString(R.string.freezed));
                EmUtil.employLogout(XApp.getInstance());
            } else if (msg.equals("force_offline")) {//强制下线
                XApp.getInstance().shake();
                XApp.getInstance().syntheticVoice(XApp.getInstance().getString(R.string.force_offline));
                EmUtil.employLogout(XApp.getInstance());
            } else if (msg.equals("unbunding")) {//解绑
                XApp.getInstance().shake();
                XApp.getInstance().syntheticVoice("您的账户已被管理员解绑");
//                XApp.getInstance().syntheticVoice(XApp.getInstance().getString(R.string.unbunding));
                EmUtil.employLogout(XApp.getInstance());
            } else if (msg.equals("finish")) { //支付成功
                MultipleOrder order = new MultipleOrder();
                order.orderId = jb.optJSONObject("data").optLong("orderId");
                order.serviceType = jb.optJSONObject("data").optString("serviceType");
                loadOrder(order);
            } else if (msg.equals("reAssign")) {//订单被改派
                MultipleOrder order = new MultipleOrder();
                order.orderId = jb.optJSONObject("data").optLong("orderId");
                order.serviceType = jb.optJSONObject("data").optString("serviceType");

                Message message = new Message();
                message.what = 3;
                Bundle bundle = new Bundle();
                bundle.putSerializable("order", order);
                message.setData(bundle);
                handler.sendMessage(message);
            } else if (msg.equals("setting_change")) {
                loadSetting();
            } else if (msg.equals("http_costInfo")) { //费用信息
                String data = jb.optString("data");
                JSONObject jbData = new JSONObject(data.replaceAll("\\\\\\\"", "--"));
                long orderId = jbData.optLong("OrderId");
                String orderType = jbData.optString("OrderType");
                DymOrder dymOrder = DymOrder.findByIDType(orderId, orderType);
                if (dymOrder != null) {
                    if (dymOrder.distance > jbData.optDouble("Mileges")) {
                        return;
                    }
                    dymOrder.startFee = jbData.optDouble("StartPrice");
                    dymOrder.waitTime = jbData.optInt("WaitTime") / 60;
                    dymOrder.waitTimeFee = jbData.optDouble("WaitTimeFee");
                    dymOrder.travelTime = jbData.optInt("DriverTime") / 60;
                    dymOrder.travelFee = jbData.optDouble("DriveTimeCost");
                    dymOrder.totalFee = jbData.optDouble("TotalAmount");

                    dymOrder.minestMoney = jbData.optDouble("MinCost");

                    dymOrder.disFee = jbData.optDouble("MileageCost");
                    dymOrder.distance = jbData.optDouble("Mileges");

                    if (Config.ZHUANCHE.equals(orderType)) {
                        dymOrder.peakCost = jbData.optDouble("PeakCost");
                        dymOrder.nightPrice = jbData.optDouble("NightPrice");
                        dymOrder.lowSpeedCost = jbData.optDouble("LowSpeedCost");
                        dymOrder.lowSpeedTime = jbData.getInt("LowSpeedTime") / 60;
                        dymOrder.peakMile = jbData.optDouble("PeakMile");
                        dymOrder.nightTime = jbData.getInt("NightTime") / 60;
                        dymOrder.nightMile = jbData.optDouble("NightMile");
                        dymOrder.nightTimePrice = jbData.optDouble("NightTimePrice");
                    }

                    DecimalFormat decimalFormat = new DecimalFormat("#0.0");
                    decimalFormat.setRoundingMode(RoundingMode.DOWN);
                    dymOrder.distance = Double.parseDouble(decimalFormat.format(dymOrder.distance));
                    //公里数保留一位小数。。

                    dymOrder.updateFee();
                    notifyObserver(orderId, orderType);
                }
            } else if (msg.equals("realFee")) { //费用信息
                String data = jb.optString("data");
                JSONObject jbData = new JSONObject(data.replaceAll("\\\\\\\"", "--"));
                long orderId = jbData.optLong("orderId");
                String orderType = jbData.optString("orderType");
                DymOrder dymOrder = DymOrder.findByIDType(orderId, orderType);
                if (dymOrder != null) {
//                    if(dymOrder.distance > jbData.optDouble("Mileges")){
//                        return;
//                    }
                    dymOrder.startFee = jbData.optDouble("startFee");
                    dymOrder.waitTime = jbData.optInt("waitTime") / 60;
                    dymOrder.waitTimeFee = jbData.optDouble("waitFee");
                    dymOrder.travelTime = jbData.optInt("time") / 60;
                    dymOrder.travelFee = jbData.optDouble("timeFee");
                    dymOrder.totalFee = jbData.optDouble("totalFee");

                    dymOrder.minestMoney = jbData.optDouble("minCost");

                    dymOrder.disFee = jbData.optDouble("distanceFee");
                    dymOrder.distance = new BigDecimal(jbData.optDouble("distance") / 1000).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    //add
                    dymOrder.addDistance = jbData.optDouble("addDistance");
                    dymOrder.addFee = jbData.optDouble("addFee");


                    if (Config.ZHUANCHE.equals(orderType)) {
                        dymOrder.peakCost = jbData.optDouble("peakFee");
                        dymOrder.nightPrice = jbData.optDouble("nightMileFee"); //**不祥的预感
                        dymOrder.lowSpeedCost = jbData.optDouble("lowSpeedFee");
                        dymOrder.lowSpeedTime = jbData.getInt("lowSpeedTime") / 60;
                        dymOrder.peakMile = jbData.optDouble("peakMile");
                        dymOrder.nightTime = jbData.getInt("nightTime") / 60;
                        dymOrder.nightMile = jbData.optDouble("nightMile");
                        dymOrder.nightTimePrice = jbData.optDouble("nightTimeFee");
                    }

//                    DecimalFormat decimalFormat = new DecimalFormat("#0.0");
//                    decimalFormat.setRoundingMode(RoundingMode.DOWN);
//                    dymOrder.distance = Double.parseDouble(decimalFormat.format(dymOrder.distance));
                    //公里数保留一位小数。。

                    dymOrder.updateFee();
                    notifyObserver(orderId, orderType);
                }
            } else if (msg.equals("tiredDriverNotice")) {
                String data = jb.optString("data");
                Intent intent = new Intent();
                intent.setAction(Config.TIRED_NOTICE);
                intent.putExtra("data", data);
                XApp.getInstance().sendBroadcast(intent);
            } else if (msg.equals("passenger_gps")) {
                String data = jb.optString("data");
                PassengerLocation location = GsonUtil.parseJson(data, PassengerLocation.class);
                notifyPLObserver(location);
            } else if (msg.equals("flashAssign")) {
                MultipleOrder order = new MultipleOrder();
                order.orderId = jb.optJSONObject("data").optLong("orderId");
                order.serviceType = jb.optJSONObject("data").optString("serviceType");

                order.passengerId = jb.optJSONObject("data").optLong("passengerId");
                order.passengerPhone = jb.optJSONObject("data").optString("userPhone");

                XApp.getInstance().shake();
                XApp.getInstance().syntheticVoice("您有快速指派订单需要处理");

                //一键报警 //todo 一键报警
//                CenterUtil centerUtil = new CenterUtil(XApp.getInstance(),Config.APP_KEY,
//                        XApp.getMyPreferences().getString(Config.AES_PASSWORD, AesUtil.AAAAA),
//                        XApp.getMyPreferences().getString(Config.SP_TOKEN, ""));

                if (StringUtils.isNotBlank(order.serviceType)) {
                    if (order.serviceType.equals(Config.ZHUANCHE)) {
                        ARouter.getInstance()
                                .build("/zhuanche/FlowActivity")
                                .withLong("orderId", order.orderId).navigation();
//                        centerUtil.smsShareAuto(order.orderId, EmUtil.getEmployInfo().companyId, order.passengerId, order.passengerPhone, order.serviceType);
//                        centerUtil.checkingAuth(order.passengerId);
                    } else if (order.serviceType.equals(Config.TAXI)) {
                        ARouter.getInstance()
                                .build("/taxi/FlowActivity")
                                .withLong("orderId", order.orderId).navigation();
//                        centerUtil.smsShareAuto(order.orderId, EmUtil.getEmployInfo().companyId, order.passengerId, order.passengerPhone, order.serviceType);
//                        centerUtil.checkingAuth(order.passengerId);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadOrder(MultipleOrder multipleOrder) {
        if (StringUtils.isNotBlank(multipleOrder.serviceType)) {
            if (multipleOrder.serviceType.equals(Config.DAIJIA)) {
                loadDJOrder(multipleOrder.orderId, Config.DAIJIA);
            } else if (multipleOrder.serviceType.equals(Config.ZHUANCHE)) {
                loadZCOrder(multipleOrder.orderId, Config.ZHUANCHE);
            } else if (multipleOrder.serviceType.equals(Config.TAXI)) {
                loadTaxiOrder(multipleOrder.orderId, Config.TAXI);
            }
        }
    }

    /**
     * 查询设置
     */
    private void loadSetting() {
        Observable<SettingResult> observable = ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .getAppSetting(EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        rxManager.add(observable.subscribe(new MySubscriber<>(XApp.getInstance(), false,
                false, new HaveErrSubscriberListener<SettingResult>() {
            @Override
            public void onNext(SettingResult result) {
//                List<SubSetting> settingList = GsonUtil.parseToList(result.appSetting, SubSetting[].class);
//                if (settingList != null) {
//                    for (SubSetting sub : settingList) {
//                        if (Config.ZHUANCHE.equals(sub.businessType)) {
//                            ZCSetting zcSetting = GsonUtil.parseJson(sub.subJson, ZCSetting.class);
//                            if (zcSetting != null) {
//                                ZCSetting.deleteAll();
//                                zcSetting.save();
//                            }
//                        } else if ("daijia".equals(sub.businessType)) {
//                            TaxiSetting djSetting = GsonUtil.parseJson(sub.subJson, TaxiSetting.class);
//                            if (djSetting != null) {
//                                TaxiSetting.deleteAll();
//                                djSetting.save();
//                            }
//                        }
//                    }
//                }
            }

            @Override
            public void onError(int code) {
                rxManager.clear();
            }
        })));
    }

    private void loadDJOrder(long orderId, String orderType) {
        Observable<MultipleOrderResult> observable = ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .queryDJOrder(orderId, EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        rxManager.add(observable.subscribe(new MySubscriber<>(XApp.getInstance(), false,
                false, new HaveErrSubscriberListener<MultipleOrderResult>() {
            @Override
            public void onNext(MultipleOrderResult multipleOrderResult) {
                loadOrderCallback.callback(multipleOrderResult, orderType);
            }

            @Override
            public void onError(int code) {
                rxManager.clear();
            }
        })));
    }

    private void loadZCOrder(long orderId, String orderType) {
        Observable<MultipleOrderResult> observable = ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .queryZCOrder(orderId, EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        rxManager.add(observable.subscribe(new MySubscriber<>(XApp.getInstance(), false,
                false, new HaveErrSubscriberListener<MultipleOrderResult>() {
            @Override
            public void onNext(MultipleOrderResult multipleOrderResult) {
                loadOrderCallback.callback(multipleOrderResult, orderType);
            }

            @Override
            public void onError(int code) {
                rxManager.clear();
            }
        })));
    }

    private void loadTaxiOrder(long orderId, String orderType) {
        Observable<MultipleOrderResult> observable = ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .queryTaxiOrder(orderId, EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        rxManager.add(observable.subscribe(new MySubscriber<>(XApp.getInstance(), false,
                false, new HaveErrSubscriberListener<MultipleOrderResult>() {
            @Override
            public void onNext(MultipleOrderResult multipleOrderResult) {
                loadOrderCallback.callback(multipleOrderResult, orderType);
            }

            @Override
            public void onError(int code) {
                rxManager.clear();
            }
        })));
    }

    /**
     * 加载通知
     *
     * @param id
     */
    private void loadNotice(long id) {
        Observable<NotitfyResult> observable = ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .loadNotice(id, EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        rxManager.add(observable.subscribe(new MySubscriber<>(XApp.getInstance(), false,
                false, new HaveErrSubscriberListener<NotitfyResult>() {
            @Override
            public void onNext(NotitfyResult multipleOrderResult) {
                XApp.getInstance().shake();
                XApp.getInstance().syntheticVoice(multipleOrderResult.data.noticeContent, XApp.NEW_MSG);
                Intent intent = new Intent();
                intent.setAction(Config.BROAD_NOTICE);
                intent.putExtra("notice", multipleOrderResult.data.noticeContent);
                intent.setPackage(XApp.getInstance().getPackageName());
                XApp.getInstance().sendBroadcast(intent);
            }

            @Override
            public void onError(int code) {
                rxManager.clear();
            }
        })));
    }

    /**
     * 查询公告
     *
     * @param id
     */
    private void loadAnn(long id) {
        Observable<PushAnnouncement> observable = ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .employAfficheById(id, EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        rxManager.add(observable.subscribe(new MySubscriber<>(XApp.getInstance(), false,
                false, new HaveErrSubscriberListener<PushAnnouncement>() {
            @Override
            public void onNext(PushAnnouncement announcementResult) {
                if (announcementResult.request != null) {
                    XApp.getInstance().shake();
                    XApp.getInstance().syntheticVoice(XApp.getInstance().getString(R.string.new_ann) + announcementResult.request.title, XApp.NEW_ANN);
                    Intent intent = new Intent();
                    intent.setAction(Config.BROAD_ANN);
                    intent.putExtra("ann", announcementResult.request.title);
                    intent.setPackage(XApp.getInstance().getPackageName());
                    XApp.getInstance().sendBroadcast(intent);
                }
            }

            @Override
            public void onError(int code) {
                rxManager.clear();
            }
        })));
    }


    private void newShowNotify(Context context, String tips, String title,
                               String content) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) XApp
                .getInstance().getSystemService(ns);

        Intent notificationIntent = new Intent(context, WorkActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(
                XApp.getInstance(), 0, notificationIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                XApp.getInstance());
        builder.setSmallIcon(com.easymi.component.R.mipmap.role_driver);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
        builder.setColor(context.getResources().getColor(R.color.colorPrimary));
        builder.setTicker(tips);
        builder.setWhen(System.currentTimeMillis());
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setContentIntent(contentIntent);

        Notification notification = builder.build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(NOTIFY_ID, notification);
    }

    private OnLoadOrderCallback loadOrderCallback = new OnLoadOrderCallback() {
        @Override
        public void callback(MultipleOrderResult multipleOrderResult, String orderType) {
            MultipleOrder order = multipleOrderResult.data;
            if (order != null) {
                if (order.status == DJOrderStatus.FINISH_ORDER) { //已完成订单
                    String weihao = order.passengerPhone;
                    if (weihao.length() > 4) {
                        weihao = weihao.substring(weihao.length() - 4, weihao.length());
                    }
                    XApp.getInstance().shake();
                    XApp.getInstance().syntheticVoice(
                            XApp.getMyString(R.string.pay_suc_1) +
                                    weihao +
                                    XApp.getMyString(R.string.pay_suc_2));//语音播报xx客户已完成支付

                    Intent intent1 = new Intent();
                    intent1.setAction(Config.BROAD_FINISH_ORDER);
                    intent1.putExtra("orderId", order.id);
                    intent1.putExtra("orderType", order.serviceType);
                    XApp.getInstance().sendBroadcast(intent1);
                    return;
                }
                if (order.status != DJOrderStatus.NEW_ORDER && order.status != DJOrderStatus.PAIDAN_ORDER) {
                    return;
                }
//                order.orderAddressVos = multipleOrderResult.address;
                String voiceStr = "";
                if (order.status == DJOrderStatus.NEW_ORDER) {
                    voiceStr += XApp.getInstance().getString(R.string.grab_order) + ",";//抢单
                } else {
                    voiceStr += XApp.getInstance().getString(R.string.send_order) + ",";//派单
                }
                if (orderType.equals(Config.DAIJIA)) {
                    voiceStr += XApp.getInstance().getString(R.string.create_daijia)
                            + XApp.getInstance().getString(R.string.order) + ",";//代驾订单
                } else if (orderType.equals(Config.ZHUANCHE)) {
                    voiceStr += XApp.getInstance().getString(R.string.create_zhuanche)
                            + XApp.getInstance().getString(R.string.order) + ",";//专车订单
                } else if (orderType.equals(Config.TAXI)) {
                    voiceStr += XApp.getInstance().getString(R.string.create_taxi)
                            + XApp.getInstance().getString(R.string.order) + ",";//专车订单
                }
                String dis = 0 + XApp.getInstance().getString(R.string.meter);
                if (EmUtil.getLastLoc() != null && order.orderAddressVos != null && order.orderAddressVos.size() != 0) {
                    LatLng my = new LatLng(EmUtil.getLastLoc().latitude, EmUtil.getLastLoc().longitude);

                    for (Address address : order.orderAddressVos) {
                        if (address.type == 1) {
                            LatLng start = new LatLng(address.latitude, address.longitude);
                            double meter = AMapUtils.calculateLineDistance(my, start);
                            DecimalFormat format;
                            if (meter > 1000) {
                                format = new DecimalFormat("#0.0");
                                dis = format.format(meter / (double) 1000) + XApp.getInstance().getString(R.string.k_meter);
                            } else {
                                format = new DecimalFormat("#0");
                                dis = format.format(meter) + XApp.getInstance().getString(R.string.meter);
                            }
                        }
                    }
                }
                voiceStr +=
//                        order.orderDetailType//酒后代驾
//                        + ","
//                        +
                        XApp.getInstance().getString(R.string.to_you)//距您
                                + dis //0.5公里
                                + ","
                                + XApp.getInstance().getString(R.string.from)//从
                                + order.getStartSite().address //xxx
                                + XApp.getInstance().getString(R.string.out)//出发
                                + ",";
                if (StringUtils.isNotBlank(order.destination)) {
                    voiceStr += XApp.getInstance().getString(R.string.to) + order.destination;//到xxx
                }
                Message message = new Message();
                message.what = 0;
                Bundle bundle = new Bundle();
                bundle.putSerializable("order", order);
                bundle.putString("voiceStr", voiceStr);
                message.setData(bundle);
                handler.sendMessage(message);
            }
        }
    };

    private Handler handler = new Handler(msg -> {
        switch (msg.what) {
            case 0:
                Bundle bundle = msg.getData();
                MultipleOrder order = (MultipleOrder) bundle.getSerializable("order");
                String voiceStr = bundle.getString("voiceStr");
                Intent intent = new Intent(XApp.getInstance(), GrabActivity2.class);
                intent.putExtra("order", order);
                intent.putExtra("voiceStr", voiceStr);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                XApp.getInstance().startActivity(intent);
                break;
            case 1:
                Bundle bundle1 = msg.getData();
                MultipleOrder order1 = (MultipleOrder) bundle1.getSerializable("order");
                if (order1 != null) {
                    Intent intent1 = new Intent();
                    intent1.setAction(Config.BROAD_CANCEL_ORDER);
                    intent1.putExtra("orderId", order1.orderId);
                    intent1.putExtra("orderType", order1.serviceType);
                    XApp.getInstance().sendBroadcast(intent1);
                }
                XApp.getInstance().shake();
                XApp.getInstance().syntheticVoice(XApp.getInstance().getString(R.string.you_have_order_cancel), XApp.CANCEL);
                newShowNotify(XApp.getInstance(), "", XApp.getInstance().getString(R.string.cancel_order)
                        , XApp.getInstance().getString(R.string.you_have_order_cancel));
                break;
            case 2:
                Bundle bundle2 = msg.getData();
                String status = bundle2.getString("status");
                Employ employ = EmUtil.getEmployInfo();
                if (null != employ) {
//                    if (employ.status != EmployStatus.OFFLINE
//                            && employ.status != EmployStatus.FROZEN
//                            && status != employ.status) {
//                        XApp.getInstance().syntheticVoice(XApp.getInstance().getString(R.string.force_offline));
//                    }
                    employ.status = Integer.parseInt(status);
                    employ.updateBase();

                    if (status != null) {
                        if (status.equals(EmployStatus.FROZEN)) {
                            XApp.getInstance().shake();
                            XApp.getInstance().syntheticVoice(XApp.getInstance().getString(R.string.please_admin));
                            EmUtil.employLogout(XApp.getInstance());
                        } else if (status.equals(EmployStatus.ONLINE)) {
                            XApp.getInstance().shake();
                            XApp.getInstance().syntheticVoice(XApp.getInstance().getString(R.string.force_offline));
                            EmUtil.employLogout(XApp.getInstance());
                        } else {
                            Intent intent2 = new Intent();
                            intent2.setAction(Config.BROAD_EMPLOY_STATUS_CHANGE);
                            intent2.putExtra("status", employ.status + "");
                            XApp.getInstance().sendBroadcast(intent2);
                        }
                    }
                }
                break;
            case 3:
                Bundle bundle3 = msg.getData();
                MultipleOrder order3 = (MultipleOrder) bundle3.getSerializable("order");
                if (order3 != null) {
                    Intent intent3 = new Intent();
                    intent3.setAction(Config.BROAD_BACK_ORDER);
                    intent3.putExtra("orderId", order3.orderId);
                    intent3.putExtra("orderType", order3.serviceType);
                    XApp.getInstance().sendBroadcast(intent3);
                }
                XApp.getInstance().shake();
                XApp.getInstance().syntheticVoice(XApp.getInstance().getString(R.string.you_have_order_back), XApp.CANCEL);
                newShowNotify(XApp.getInstance(), "", XApp.getInstance().getString(R.string.back_order)
                        , XApp.getInstance().getString(R.string.you_have_order_back));

                break;
        }
        return true;
    });

    @Override
    public void addObserver(FeeChangeObserver obj) {
        if (null == observers) {
            observers = new ArrayList<>();
        }
        boolean hasd = false;
        for (FeeChangeObserver observer : observers) {
            if (obj == observer) {
                hasd = true;
            }
        }
        if (!hasd) {//避免重复添加观察者
            observers.add(obj);
        }
    }

    @Override
    public void deleteObserver(FeeChangeObserver obj) {
        if (null == observers) {
            return;
        }
        observers.remove(obj);
    }

    @Override
    public void notifyObserver(long orderId, String orderType) {
        if (null == observers) {
            return;
        }
        for (FeeChangeObserver observer : observers) {
            observer.feeChanged(orderId, orderType);
        }
    }

    @Override
    public void addPLObserver(PassengerLocObserver obj) {
        if (null == plObservers) {
            plObservers = new ArrayList<>();
        }
        boolean hasd = false;
        for (PassengerLocObserver observer : plObservers) {
            if (obj == observer) {
                hasd = true;
            }
        }
        if (!hasd) {//避免重复添加观察者
            plObservers.add(obj);
        }
    }

    @Override
    public void deletePLObserver(PassengerLocObserver obj) {
        if (null == plObservers) {
            return;
        }
        plObservers.remove(obj);
    }

    @Override
    public void notifyPLObserver(PassengerLocation passengerLocation) {
        if (null == plObservers) {
            return;
        }
        for (PassengerLocObserver observer : plObservers) {
            observer.plChange(passengerLocation);
        }
    }

    interface OnLoadOrderCallback {
        void callback(MultipleOrderResult multipleOrderResult, String orderType);
    }

}
