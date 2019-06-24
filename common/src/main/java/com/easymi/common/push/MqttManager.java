package com.easymi.common.push;

import android.text.TextUtils;

import com.easymi.common.CommApiService;
import com.easymi.common.entity.BuildPushData;
import com.easymi.common.result.GetFeeResult;
import com.easymi.common.util.BuildPushUtil;
import com.easymi.component.Config;
import com.easymi.component.app.XApp;
import com.easymi.component.entity.EmLoc;
import com.easymi.component.loc.LocObserver;
import com.easymi.component.loc.LocReceiver;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.FileUtil;
import com.easymi.component.utils.Log;
import com.easymi.component.utils.NetUtil;
import com.easymi.component.utils.StringUtils;
import com.tencent.bugly.crashreport.CrashReport;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.concurrent.Executors;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 管理mqtt的连接,发布,订阅,断开连接, 断开重连等操作
 *
 * @author LichFaker on 16/3/24.
 * @Email lichfaker@gmail.com
 */
public class MqttManager implements LocObserver {

    private static String TAG = MqttManager.class.getSimpleName();

    // 单例
    private static MqttManager mInstance = null;

    private int qos = 2;

    /**
     * Private instance variables
     */
    private MqttAndroidClient client;
    private MqttConnectOptions conOpt;

    private boolean isConnecting = false;

    private String pullTopic;

    private RxManager rxManager;
    private boolean isLosingConnect;

    public boolean isLosingConnect() {
        return isLosingConnect;
    }

    /**
     * 初始化
     */
    private MqttManager() {
        rxManager = new RxManager();
        LocReceiver.getInstance().addObserver(MqttManager.this);
    }

    /**
     * 实例化
     *
     * @return
     */
    public static MqttManager getInstance() {
        if (null == mInstance) {
            mInstance = new MqttManager();
        }
        return mInstance;
    }

    /**
     * 释放单例, 及其所引用的资源
     */
    public static void release() {
        LocReceiver.getInstance().deleteObserver(getInstance());
        try {
            if (mInstance != null) {
                mInstance.disConnect();
                mInstance = null;
            }
        } catch (Exception e) {

        }
    }

    /**
     * 创建Mqtt 连接
     *
     * @return
     */
    public synchronized boolean creatConnect() {

        if (!XApp.getMyPreferences().getBoolean(Config.SP_ISLOGIN, false)) {
            //未登陆 不连接
            return false;
        }

        if (isConnecting) {
            //正在连接，不连接
            return false;
        }
        if (isConnected()) {
            //client连接起的  不连接
            return false;
        }
        if (TextUtils.isEmpty(Config.MQTT_TOPIC)) {
            return false;
        }

        pullTopic = "/trip/driver" + "/" + EmUtil.getAppKey() + "/" + EmUtil.getEmployId();
        String brokerUrl = "tcp://" + Config.MQTT_HOST + ":" + Config.PORT_TCP;
        //身份唯一码
        String clientId = "driver-" + EmUtil.getEmployId();

        conOpt = new MqttConnectOptions();
        conOpt.setCleanSession(true);
        conOpt.setUserName(Config.MQTT_USER_NAME);
        conOpt.setPassword(Config.MQTT_PSW.toCharArray());
        // 设置超时时间，单位：秒
        conOpt.setConnectionTimeout(5);
        // 心跳包发送间隔，单位：秒
        conOpt.setKeepAliveInterval(10);
        conOpt.setAutomaticReconnect(true);

        String message = "{\"terminal_uid\":\"" + clientId + "\"}";
        conOpt.setWill(pullTopic, message.getBytes(), qos, false);

        client = new MqttAndroidClient(XApp.getInstance(), brokerUrl, clientId);

        client.setCallback(mCallback);
        doConnect();

        return true;
    }

    /**
     * 建立连接
     *
     * @return
     */
    private synchronized boolean doConnect() {
        isConnecting = false;

        if (client != null) {
            try {
                client.connect(conOpt);
                Log.e(TAG, "Connected to " + client.getServerURI() + " with client ID " + client.getClientId());
                isConnecting = true;
            } catch (Exception e) {
                Log.e(TAG, "doConnect exception-->" + e.getMessage());
            }
        }
        return isConnecting;
    }

    /**
     * 推送消息
     *
     * @return boolean
     */
    public void publish(String pushStr) {
        if (StringUtils.isBlank(pushStr)) {
            return;
        }

        if (client != null && client.isConnected()) {
            MqttMessage message = new MqttMessage(pushStr.getBytes());
            message.setQos(qos);
            try {
                client.publish(Config.MQTT_TOPIC, message);
                Log.e("MqttManager", "push loc data--->" + pushStr);
            } catch (MqttException e) {
                Log.e(TAG, "Publishing msg exception " + e.getMessage());
            }
        }
    }


    /**
     * mqtt是否连接
     *
     * @return
     */
    public boolean isConnected() {
        if (client != null && client.isConnected()) {
            return true;
        }
        return false;
    }

    /**
     * 取消连接
     *
     * @throws MqttException
     */
    public void disConnect() throws MqttException {
        rxManager.clear();
        if (client != null && client.isConnected()) {
            client.disconnect();
        }
    }

    /**
     * 回调
     */
    private MqttCallback mCallback = new MqttCallbackExtended() {
        @Override
        public void connectComplete(boolean reconnect, String serverURI) {
            Executors.newSingleThreadExecutor().submit(() -> {
                try {
                    isLosingConnect = false;
                    client.subscribe(pullTopic, qos);
                } catch (MqttException e) {
                    e.printStackTrace();
                }

            });
        }

        @Override
        public void connectionLost(Throwable cause) {
            //失去连接
            if (null != client && !isLosingConnect) {
                try {
                    client.unsubscribe(pullTopic);
                    isLosingConnect = true;
                    Log.e(TAG, "取消订阅的topic");
                } catch (Exception e) {
                    e.fillInStackTrace();
                }
            }
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            String str1 = new String(message.getPayload());
            Log.e(TAG, "MqttReceivePull:" + str1);
            HandlePush.getInstance().handPush(str1);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {

        }
    };

    @Override
    public void receiveLoc(EmLoc loc) {
        pushLoc(new BuildPushData(loc));
    }

    public void pushLoc(BuildPushData data) {
        pushInternalLoc(data, false);
    }

    /**
     * 不限制推送数据
     *
     * @param data
     */
    public void pushLocNoLimit(BuildPushData data) {
        pushInternalLoc(data, true);
    }

    /**
     * 推送司机及其定位信息
     *
     * @param data
     * @param noLimit
     */
    private void pushInternalLoc(BuildPushData data, boolean noLimit) {
        String pushStr = BuildPushUtil.buildPush(data, noLimit);

        if (client != null && client.isConnected()) {
            if (data != null) {
                if (pushStr == null) {
                    Exception exception = new IllegalArgumentException("自定义异常：推送数据为空，可能是司机信息为空");
                    CrashReport.postCatchedException(exception);
                    return;
                }
                publish(pushStr);
                //上传后删除本地的缓存
                FileUtil.delete("v6driver", "pushCache.txt");
            }
        } else {
            if (data != null) {
                if (pushStr == null) {
                    Exception exception = new IllegalArgumentException("自定义异常：推送数据为空，可能是司机信息为空");
                    CrashReport.postCatchedException(exception);
                    return;
                }
                //todo 为了张鹏，数组改成了单个实体
//                PushBean pushBean = new Gson().fromJson(pushStr, PushBean.class);
//                List<PushData> beanList = new ArrayList<>();
//                for (PushData datum : pushBean.data) {
//                    if (datum.location.orderInfo != null
//                            && datum.location.orderInfo.size() != 0) {//有订单时才需要保存
//                        beanList.add(datum);
//                    }
//                }
//                if (beanList.size() != 0) {
//                    FileUtil.savePushCache(XApp.getInstance(), new Gson().toJson(beanList));//只保存位置的list
//                }
            }
            creatConnect();
            //更改到断连就http上报
            gpsPush(pushStr);
        }
    }

    /**
     * http 上传位置信息
     */
    private void gpsPush(String pushStr) {
        if (NetUtil.getNetWorkState(XApp.getInstance()) == NetUtil.NETWORK_NONE) {
            return; //没网
        }
//        if (DymOrder.findAll().size() == 0) {
//            return;
//        }
        long lastPushTime = XApp.getMyPreferences().getLong(Config.SP_LAST_GPS_PUSH_TIME, 0);
        if (System.currentTimeMillis() - lastPushTime > 5 * 1000) {
            XApp.getEditor().putLong(Config.SP_LAST_GPS_PUSH_TIME, System.currentTimeMillis()).apply();

            Observable<GetFeeResult> observable = ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                    .gpsPush(Config.APP_KEY, pushStr)
                    .filter(new HttpResultFunc<>())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());

            rxManager.add(observable.subscribe(new MySubscriber<>(XApp.getInstance(),
                    false,
                    false,
                    new HaveErrSubscriberListener<GetFeeResult>() {
                        @Override
                        public void onNext(GetFeeResult getFeeResult) {
//                            PullFeeEntity entity = new PullFeeEntity();
//                            entity.msg = "http_costInfo";
//                            entity.data = getFeeResult.data;
//                            HandlePush.getInstance().handPush(new Gson().toJson(entity));
                        }

                        @Override
                        public void onError(int code) {

                        }
                    })));
            FileUtil.delete("v6driver", "pushCache.txt");
        }
    }

}
