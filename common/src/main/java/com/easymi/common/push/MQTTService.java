package com.easymi.common.push;

/**
 * Created by developerLzh on 2017/12/13 0013.
 */

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import com.easymi.common.CommApiService;
import com.easymi.common.entity.BuildPushData;
import com.easymi.common.entity.PushBean;
import com.easymi.common.entity.PushData;
import com.easymi.common.trace.TraceInterface;
import com.easymi.common.trace.TraceReceiver;
import com.easymi.common.util.BuildPushUtil;
import com.easymi.component.Config;
import com.easymi.component.DJOrderStatus;
import com.easymi.component.app.XApp;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.entity.EmLoc;
import com.easymi.component.loc.LocObserver;
import com.easymi.component.loc.LocReceiver;
import com.easymi.component.loc.LocService;
import com.easymi.component.loc.TrackHelper;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.network.NoErrSubscriberListener;
import com.easymi.component.receiver.NetWorkChangeReceiver;
import com.easymi.component.result.EmResult;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.trace.TraceUtil;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.FileUtil;
import com.easymi.component.utils.Log;
import com.easymi.component.utils.NetUtil;
import com.easymi.component.utils.TimeUtil;
import com.google.gson.Gson;
import com.tencent.bugly.crashreport.CrashReport;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * MQTT长连接服务
 * <p>
 * 注意在调用client.isConnected()时可能会抛出异常，最好try-catch
 */
public class MQTTService extends Service implements LocObserver, TraceInterface, NetWorkChangeReceiver.OnNetChange {

    public static final String TAG = MQTTService.class.getSimpleName();

    @SuppressLint("StaticFieldLeak")
    public static MqttAndroidClient client;
    public MqttConnectOptions conOpt;

    //    private String host = "tcp://10.0.2.2:61613";
    private String host = Config.MQTT_HOST;
    private String userName = Config.MQTT_USER_NAME;
    private String passWord = Config.MQTT_PSW;
    private static String pushTopic = Config.MQTT_PUSH_TOPIC;
    private String pullTopic;
    private String configTopic;
    private String clientId = "driver-" + EmUtil.getEmployId();//身份唯一码

    private TraceReceiver traceReceiver;
    private NetWorkChangeReceiver netWorkChangeReceiver;

    private static MQTTService instance;

    private WorkTimeCounter workTimeCounter;
    private OrderPushDisTimer orderPushDisTimer;

    private static RxManager mRxManager;

    public void startPushDisTimer(Context context, long orderId, String orderType) {
        if (null != orderPushDisTimer) {
            orderPushDisTimer.cancelTimer();
            orderPushDisTimer = null;
        }
        orderPushDisTimer = new OrderPushDisTimer(context, orderId, orderType);
        orderPushDisTimer.startTimer();
    }

    public void stopPushTimer() {
        if (null != orderPushDisTimer) {
            orderPushDisTimer.cancelTimer();
        }
    }

    public static MQTTService getInstance() {
        if (instance == null) {
            Intent intent = new Intent(XApp.getInstance(), MQTTService.class);
            intent.setPackage(XApp.getInstance().getPackageName());
            XApp.getInstance().startService(intent);//重启推送
            return null;
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "MQTTService onCreate~~");
        traceReceiver = new TraceReceiver(this);
        registerReceiver(traceReceiver, new IntentFilter(LocService.BROAD_TRACE_SUC));

        netWorkChangeReceiver = new NetWorkChangeReceiver();
        netWorkChangeReceiver.setEvent(this);
        IntentFilter netFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkChangeReceiver, netFilter);

        mRxManager = new RxManager();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        synchronized (this) {
            initConn();
        }
        if (workTimeCounter == null) {
            workTimeCounter = new WorkTimeCounter(this);
        }
        return START_STICKY;
    }

    private void initConn() {
        instance = this;
        if (client != null) {
            return;
        }
        // 订阅myTopic话题
        LocReceiver.getInstance().addObserver(MQTTService.this);
        // 服务器地址（协议+地址+端口号）
        String uri = host;
        client = new MqttAndroidClient(this, uri, clientId);
        // 设置MQTT监听并且接受消息
        client.setCallback(mqttCallback);

        conOpt = new MqttConnectOptions();
        // 清除缓存
        conOpt.setCleanSession(true);
        // 设置超时时间，单位：秒
        conOpt.setConnectionTimeout(10);
        // 心跳包发送间隔，单位：秒
        conOpt.setKeepAliveInterval(10);
        // 用户名
        conOpt.setUserName(userName);
        // 密码
        conOpt.setPassword(passWord.toCharArray());

        // last will message
        boolean doConnect = true;
        String message = "{\"terminal_uid\":\"" + clientId + "\"}";
        Log.e("Mqtt", message);
        pullTopic = "/driver" + "/" + EmUtil.getAppKey() + "/" + EmUtil.getEmployId();
        configTopic = "/driver" + "/" + EmUtil.getAppKey() + "/config";

        Integer qos = 0;
        Boolean retained = false;
        if ((!message.equals("")) || (!pullTopic.equals(""))) {
            try {
                conOpt.setWill(pullTopic, message.getBytes(), qos, retained);
            } catch (Exception e) {
                Log.e(TAG, "Exception Occured", e);
                doConnect = false;
                iMqttActionListener.onFailure(null, e);
            }
        }

        if (doConnect) {
            doClientConnection();
        }
    }

    /**
     * 推消息
     *
     * @param msg
     */
    public static void publish(String msg) {
        Integer qos = 1;//与后端约定为1
        Boolean retained = true;
        try {
            if (null != client && client.isConnected()) {
                client.publish(pushTopic, msg.getBytes(), qos, retained);
            }
        } catch (Exception e) {

        }
    }

    /**
     * 上传时间。
     *
     * @param statues 小于等于0时用默认状态，大于0时用给定的状态。
     */
    public void uploadTime(int statues) {
        if (workTimeCounter != null) {
            workTimeCounter.forceUpload(statues);
        }
    }

    @Override
    public void onDestroy() {
        uploadTime(-1);
//        isConning = false;
        Log.e(TAG, "onDestroy:");
        try {
            // 订阅myTopic话题
            LocReceiver.getInstance().deleteObserver(MQTTService.this);
            unregisterReceiver(traceReceiver);
            unregisterReceiver(netWorkChangeReceiver);
            if (null != client) {
                client.unregisterResources();
                client.disconnect();
                client.close();
            }

        } catch (Exception e) {

        } finally {
            client = null;
        }
        if (workTimeCounter != null) {
            workTimeCounter.destroy();
        }
        workTimeCounter = null;
        if (null != mRxManager) {
            mRxManager.clear();
        }
        super.onDestroy();
    }

//    private static boolean isConning = false;//是否正在连接中

    /**
     * 连接MQTT服务器
     */
    private void doClientConnection() {
        try {
            if (null != client) {
                if (!client.isConnected() && isConnectIsNomarl()) {
                    client.connect(conOpt, null, iMqttActionListener);
                }
            }
        } catch (MqttException e) {

        } catch (Exception e) {

        }
    }

    private long lastSucTime = 0;

    // MQTT是否连接成功
    public IMqttActionListener iMqttActionListener = new IMqttActionListener() {

        @Override
        public void onSuccess(IMqttToken arg0) {
            Log.e(TAG, "连接成功 ");
            //
            try {
                if (lastSucTime == 0) {
                    client.subscribe(pullTopic, 1);
                    client.subscribe(configTopic, 1);
                } else {
                    if (System.currentTimeMillis() - lastSucTime < 2000) {//小于2秒的回调
                    } else {
                        client.subscribe(pullTopic, 1);
                        client.subscribe(configTopic, 1);
                    }
                }
                lastSucTime = System.currentTimeMillis();
            } catch (MqttException e) {
            } catch (NullPointerException e) { //在长时间失去网络连接后再连上mqtt，client有可能因为长时间限制而被回收，所以这里加上catch
                initConn();
            } catch (Exception e) {
            }
        }

        @Override
        public void onFailure(IMqttToken arg0, Throwable arg1) {
            CrashReport.postCatchedException(arg1);
            arg1.printStackTrace();
            Log.e(TAG, "连接失败");
            // 连接失败，重连
        }
    };

    // MQTT监听并且接受消息
    private MqttCallback mqttCallback = new MqttCallback() {

        @Override
        public void messageArrived(String topic, MqttMessage message) {

            String str1 = new String(message.getPayload());

            Log.e(TAG, "MqttReceivePull:" + str1);

            HandlePush.getInstance().handPush(str1);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken arg0) {

        }

        @Override
        public void connectionLost(Throwable arg0) {
            CrashReport.postCatchedException(arg0);
            Log.e(TAG, "失去连接");
            if (null != client) {
                try {
                    client.unsubscribe(pullTopic);
                    client.unsubscribe(configTopic);
                    Log.e(TAG, "取消订阅的topic");
                } catch (Exception e) {
                    CrashReport.postCatchedException(e);
                }
            }
        }
    };

    /**
     * 判断网络是否连接
     */
    private static boolean isConnectIsNomarl() {
        ConnectivityManager connectivityManager = (ConnectivityManager) XApp.getInstance().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = null;
        if (connectivityManager != null) {
            info = connectivityManager.getActiveNetworkInfo();
        }
        if (info != null && info.isAvailable()) {
            String name = info.getTypeName();
            Log.e(TAG, "MQTT当前网络名称：" + name);
            return true;
        } else {
            Log.e(TAG, "MQTT 没有可用网络");
            return false;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void receiveLoc(EmLoc loc) {

        if (Config.SAVE_LOGO) {
            if (DymOrder.findAll().size() != 0) {
                List<DymOrder> dymOrders = DymOrder.findAll();
                for (DymOrder dymOrder : dymOrders) {
                    if (dymOrder.orderStatus == DJOrderStatus.GOTO_DESTINATION_ORDER) {
                        try {
                            FileUtil.write(this, "xiaoka", "order-" + dymOrder.orderId + ".txt",
                                    new Gson().toJson(loc) + ",", true);
                        } catch (IOException e) {

                        }
                    }
                }
            }
        }

        if (!LocService.needTrace()) {
            Log.e("MQTTService", "receiveLoc~~");
            pushLoc(new BuildPushData(loc));
        }
    }

    public static void pushLoc(BuildPushData data) {
        pushInternalLoc(data, false);
    }

    public static void pushLocNoLimit(BuildPushData data) {
        pushInternalLoc(data, true);
    }

    private static void pushInternalLoc(BuildPushData data, boolean noLimit) {
        try {
            String pushStr = BuildPushUtil.buildPush(data, noLimit);
            if (client != null && client.isConnected()) {
                if (data != null) {
                    if (pushStr == null) {
                        Exception exception = new IllegalArgumentException("自定义异常：推送数据为空，可能是司机信息为空");
                        CrashReport.postCatchedException(exception);
                        return;
                    }
                    publish(pushStr);
                    //上传后删除本地缓存
                    FileUtil.delete("v5driver", "pushCache.txt");
                }
            } else {
                //考虑到专车订单并没有使用猎鹰 所以离线时还是保存位置信息
                if (data != null) {
                    if (pushStr == null) {
                        Exception exception = new IllegalArgumentException("自定义异常：推送数据为空，可能是司机信息为空");
                        CrashReport.postCatchedException(exception);
                        return;
                    }
                    PushBean pushBean = new Gson().fromJson(pushStr, PushBean.class);
                    List<PushData> beanList = new ArrayList<>();
                    for (PushData datum : pushBean.data) {
                        if (datum.calc.orderInfo != null
                                && datum.calc.orderInfo.size() != 0) { //有订单时才需要保存
                            beanList.add(datum);
                        }
                    }
                    if (beanList.size() != 0) {
                        FileUtil.savePushCache(XApp.getInstance(), new Gson().toJson(beanList));//只保存位置的list
                    }
                }
                pushLocByHttp(pushStr);
                doConnected();
            }
        } catch (Exception e) {

        }

    }

    @Override
    public void showTraceAfter(EmLoc emLoc) {
        Log.e("MQTTService", "traceLoc~~");
        pushLoc(new BuildPushData(emLoc));
    }

    /**
     * 外部启用重连
     */
    private static void doConnected() {
        if (NetUtil.getNetWorkState(XApp.getInstance()) == NetUtil.NETWORK_NONE) {
            return;
        }
        Log.e(TAG, "开始重启推送服务");
        Intent intentStop = new Intent(XApp.getInstance(), MQTTService.class);
        intentStop.setPackage(XApp.getInstance().getPackageName());
        XApp.getInstance().stopService(intentStop);

        Intent intentStart = new Intent(XApp.getInstance(), MQTTService.class);
        intentStart.setPackage(XApp.getInstance().getPackageName());
        XApp.getInstance().startService(intentStart);//重启推送
    }

    @Override
    public void onNetChange(int status) {
        if (status != NetUtil.NETWORK_NONE) {
            List<DymOrder> dymOrders = DymOrder.findAll();
            if (null != dymOrders && dymOrders.size() != 0) {
                for (DymOrder dymOrder : dymOrders) {
                    if (dymOrder.orderType.equals(Config.DAIJIA)) {
                        if (dymOrder.orderStatus == DJOrderStatus.GOTO_BOOKPALCE_ORDER) {
                            TrackHelper.getInstance().startTrack(dymOrder.toStartTrackId, dymOrder);
                        } else if (dymOrder.orderStatus == DJOrderStatus.START_WAIT_ORDER
                                || dymOrder.orderStatus == DJOrderStatus.GOTO_DESTINATION_ORDER) {
                            TrackHelper.getInstance().startTrack(dymOrder.toEndTrackId, dymOrder);
                            MQTTService.getInstance().startPushDisTimer(this, dymOrder.orderId, dymOrder.orderType);
                        }
                    }
                }
            }
        }
    }

    /**
     * @param pushStr
     */
    private static void pushLocByHttp(String pushStr) {
        long lastPushTime = XApp.getMyPreferences().getLong(Config.SP_LAST_HTTP_PUSH_TIME, 0);
        if (System.currentTimeMillis() - lastPushTime < 60 * 1000) {//至少间隔30秒才能调用一次
            return;
        }
        if (mRxManager == null) {
            return;
        }

        XApp.getPreferencesEditor().putLong(Config.SP_LAST_HTTP_PUSH_TIME, System.currentTimeMillis()).apply();

        Observable<EmResult> observable = ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .gpsPush(pushStr, Config.APP_KEY)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(XApp.getInstance(),
                false,
                false,
                new HaveErrSubscriberListener<EmResult>() {
                    @Override
                    public void onNext(EmResult emResult) {
                        Log.e(TAG, "通过http方式上传位置成功");
                    }

                    @Override
                    public void onError(int code) {

                        Log.e(TAG, "通过http方式上传位置失败");
                    }
                })));
    }

    /**
     * 获取mqtt推送状态
     *
     * @return
     */
    public static boolean getMqttStatus() {
        try {
            if (client != null &&
                    client.isConnected()) {
                return true;
            }
        } catch (Exception e) {

        }
        doConnected();
        return false;
    }
}
