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

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.easymi.common.CommApiService;
import com.easymi.common.R;
import com.easymi.common.entity.MultipleOrder;
import com.easymi.common.mvp.grab.GrabActivity;
import com.easymi.common.mvp.work.WorkActivity;
import com.easymi.common.result.MultipleOrderResult;
import com.easymi.component.Config;
import com.easymi.component.app.XApp;
import com.easymi.component.loc.LocObserver;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/1/11.
 */

public class HandlePush implements FeeChangeSubject {

    private RxManager rxManager;

    private static List<FeeChangeObserver> observers;
    private static HandlePush instance;

    private HandlePush() {
        rxManager = new RxManager();
    }

    public static HandlePush getInstance() {
        if (instance == null) {
            instance = new HandlePush();
            observers = new ArrayList<>();
        }
        return instance;
    }

    public void handPush(String jsonStr) {

        rxManager = new RxManager();
        try {
            JSONObject jb = new JSONObject(jsonStr);
            String msg = jb.optString("msg");
            if (msg.equals("grabOrder")) { //派单
                MultipleOrder order = new MultipleOrder();
                order.orderId = jb.optJSONObject("data").optLong("id");
                order.orderType = jb.optJSONObject("data").optString("business");
                loadOrder(order);
//                XApp.getInstance().syntheticVoice();
            } else if (msg.equals("sendOrder")) {//抢单
                MultipleOrder order = new MultipleOrder();
                order.orderId = jb.optJSONObject("data").optLong("id");
                order.orderType = jb.optJSONObject("data").optString("business");
                loadOrder(order);
                newShowNotify(XApp.getInstance(), "", XApp.getInstance().getString(R.string.send_order), XApp.getInstance().getString(R.string.send_order_content));
            } else if (msg.equals("cancelOrder")) {
                MultipleOrder order = new MultipleOrder();
                order.orderId = jb.optJSONObject("data").optLong("id");
                order.orderType = jb.optJSONObject("data").optString("business");

                Message message = new Message();
                message.what = 1;
                Bundle bundle = new Bundle();
                bundle.putSerializable("order", order);
                message.setData(bundle);
                handler.sendMessage(message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadOrder(MultipleOrder MultipleOrder) {
        if (StringUtils.isNotBlank(MultipleOrder.orderType)) {
            if (MultipleOrder.orderType.equals(Config.DAIJIA)) {
                loadDJOrder(MultipleOrder.orderId, Config.DAIJIA);
            }
        }
    }

    private void loadDJOrder(long orderId, String orderType) {
        Observable<MultipleOrderResult> observable = ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .queryDJOrder(orderId, Config.APP_KEY)
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
        mNotificationManager.notify(1, notification);
    }

    private OnLoadOrderCallback loadOrderCallback = new OnLoadOrderCallback() {
        @Override
        public void callback(MultipleOrderResult multipleOrderResult, String orderType) {
            MultipleOrder order = multipleOrderResult.order;
            if (order != null) {
                if (order.orderStatus != MultipleOrder.NEW_ORDER && order.orderStatus != MultipleOrder.PAIDAN_ORDER) {
                    return;
                }
                order.addresses = multipleOrderResult.address;
                String voiceStr = "";
                if (order.orderStatus == MultipleOrder.NEW_ORDER) {
                    voiceStr += XApp.getInstance().getString(R.string.grab_order) + ",";//抢单
                } else {
                    voiceStr += XApp.getInstance().getString(R.string.send_order) + ",";//派单
                }
                if (orderType.equals(Config.DAIJIA)) {
                    voiceStr += XApp.getInstance().getString(R.string.create_daijia)
                            + XApp.getInstance().getString(R.string.order) + ",";//代驾订单
                }
                String dis = 0 + XApp.getInstance().getString(R.string.meter);
                if (EmUtil.getLastLoc() != null && order.addresses != null && order.addresses.size() != 0) {
                    LatLng my = new LatLng(EmUtil.getLastLoc().latitude, EmUtil.getLastLoc().longitude);
                    LatLng start = new LatLng(order.addresses.get(0).lat, order.addresses.get(0).lat);
                    double meter = AMapUtils.calculateLineDistance(my, start);
                    DecimalFormat format = new DecimalFormat("#0.0");
                    if (meter > 1000) {
                        dis = format.format(meter / (double) 1000) + XApp.getInstance().getString(R.string.k_meter);
                    } else {
                        dis = format.format(meter) + XApp.getInstance().getString(R.string.meter);
                    }
                }
                voiceStr += order.orderDetailType//酒后代驾
                        + ","
                        + XApp.getInstance().getString(R.string.to_you)//距您
                        + dis //0.5公里
                        + ","
                        + XApp.getInstance().getString(R.string.from)//从
                        + order.startPlace //xxx
                        + XApp.getInstance().getString(R.string.out)//出发
                        + ",";
                if (StringUtils.isNotBlank(order.endPlace)) {
                    voiceStr += XApp.getInstance().getString(R.string.to) + order.endPlace;//到xxx
                }
                XApp.getInstance().syntheticVoice(voiceStr);
                Message message = new Message();
                message.what = 0;
                Bundle bundle = new Bundle();
                bundle.putSerializable("order", order);
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
                Intent intent = new Intent(XApp.getInstance(), GrabActivity.class);
                intent.putExtra("order", order);
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
                    intent1.putExtra("orderType", order1.orderType);
                    XApp.getInstance().sendBroadcast(intent1);
                }
                XApp.getInstance().syntheticVoice(XApp.getInstance().getString(R.string.you_have_order_cancel));
                newShowNotify(XApp.getInstance(), "", XApp.getInstance().getString(R.string.cancel_order)
                        , XApp.getInstance().getString(R.string.you_have_order_cancel));

                break;
        }
        return true;
    });

    @Override
    public void addObserver(FeeChangeObserver obj) {
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
        observers.remove(obj);
    }

    @Override
    public void notifyObserver(long orderId, String orderType) {
        for (FeeChangeObserver observer : observers) {
            observer.feeChanged(orderId, orderType);
        }
    }

    interface OnLoadOrderCallback {
        void callback(MultipleOrderResult multipleOrderResult, String orderType);
    }
}
