package com.easymi.common.push;

import android.os.Handler;

import com.easymi.common.CommApiService;
import com.easymi.common.entity.BuildPushData;
import com.easymi.common.entity.PullFeeEntity;
import com.easymi.common.entity.PushBean;
import com.easymi.common.entity.PushData;
import com.easymi.common.result.GetFeeResult;
import com.easymi.common.util.BuildPushUtil;
import com.easymi.component.Config;
import com.easymi.component.app.XApp;
import com.easymi.component.entity.DymOrder;
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

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * Private instance variables
     */
    private MqttAndroidClient client;
    private MqttConnectOptions conOpt;

    private Handler handler;

    private boolean isConnecting = false;

    /**
     * 订阅topic
     */
    String pullTopic;
    String configTopic;

    private RxManager rxManager;

    /**
     * 初始化
     */
    private MqttManager() {
        handler = new Handler();
        rxManager = new RxManager();
        LocReceiver.getInstance().addObserver(MqttManager.this);
    }

    /**
     * 实例化
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
        configTopic = "/driver" + "/" + EmUtil.getAppKey() + "/config";
        pullTopic = "/trip/driver" + "/" + EmUtil.getAppKey() + "/" + EmUtil.getEmployId();

        if (!XApp.getMyPreferences().getBoolean(Config.SP_ISLOGIN, false)) {
            //未登陆 不连接
            return false;
        }

        if (isConnecting) {
            //正在连接，不连接
            return false;
        }
        if (client != null && client.isConnected()) {
            //client连接起的  不连接
            return false;
        }
        String brokerUrl = Config.MQTT_HOST;
        String userName = Config.MQTT_USER_NAME;
        String password = Config.MQTT_PSW;
        //身份唯一码
        String clientId = "driver-" + EmUtil.getEmployId();

        // Construct the connection options object that contains connection parameters
        // such as cleanSession and LWT
        conOpt = new MqttConnectOptions();
        conOpt.setCleanSession(true);
        conOpt.setPassword(password.toCharArray());
        conOpt.setUserName(userName);
        // 设置超时时间，单位：秒
        conOpt.setConnectionTimeout(5);
        // 心跳包发送间隔，单位：秒
        conOpt.setKeepAliveInterval(10);

        Integer qos = 1;
        Boolean retained = false;
        String pullTopic = "/trip/driver" + "/" + EmUtil.getAppKey() + "/" + EmUtil.getEmployId();
        String message = "{\"terminal_uid\":\"" + clientId + "\"}";
        conOpt.setWill(pullTopic, message.getBytes(), qos, retained);

        // Construct an MQTT blocking mode client
        client = new MqttAndroidClient(XApp.getInstance(), brokerUrl, clientId);

        // Set this wrapper as the callback handler
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
                client.connect(conOpt, null, iMqttActionListener);
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
    public boolean publish(String pushStr) {
        if (StringUtils.isBlank(pushStr)) {
            return false;
        }
        //上行topic
        String topicName = Config.MQTT_PUSH_TOPIC;
        //与后台约定为1
        int qos = 1;

        boolean flag = false;

        if (client != null && client.isConnected()) {

            Log.e(TAG, "Publishing to topic \"" + topicName + "\" qos " + qos);

            // Create and configure a message
            MqttMessage message = new MqttMessage(pushStr.getBytes());
            message.setQos(qos);

            // Send the message to the server, control is not returned until
            // it has been delivered to the server meeting the specified
            // quality of service.
            try {
                client.publish(topicName, message);
                flag = true;
            } catch (MqttException e) {
                Log.e(TAG, "Publishing msg exception " + e.getMessage());
            }
        }

        return flag;
    }

    /**
     * Subscribe to a topic on an MQTT server
     * Once subscribed this method waits for the messages to arrive from the server
     * that match the subscription. It continues listening for messages until the enter key is
     * pressed.
     *
     * @param topicName to subscribe to (can be wild carded)
     * @return boolean
     */
    public boolean subscribe(String topicName) {
        int qos = 1;
        boolean flag = false;

        if (client != null && client.isConnected()) {
            // Subscribe to the requested topic
            // The QoS specified is the maximum level that messages will be sent to the client at.
            // For instance if QoS 1 is specified, any messages originally published at QoS 2 will
            // be downgraded to 1 when delivering to the client but messages published at 1 and 0
            // will be received at the same level they were published at.
            Log.e(TAG, "Subscribing to topic \"" + topicName + "\" qos " + qos);
            try {
                client.subscribe(topicName, qos);
                flag = true;
            } catch (MqttException e) {
                Log.e(TAG, "subscribe  exception--> " + e.getMessage());
            }
        }
        return flag;

    }

    /**
     * mqtt是否连接
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

    private long lastSucTime = 0;

    /**
     * MQTT是否连接成功
     */
    private IMqttActionListener iMqttActionListener = new IMqttActionListener() {

        @Override
        public void onSuccess(IMqttToken arg0) {
            //会话连接成功，就开始订阅消息
            isConnecting = false;
            Log.e("hufeng", "连接成功");
            try {
                if (lastSucTime == 0) {
                    client.subscribe(pullTopic, 1);
                    client.subscribe(configTopic, 1);
                } else {
                    if (System.currentTimeMillis() - lastSucTime < 2000) {
                        //小于2秒的回调
                    } else {
                        client.subscribe(pullTopic, 1);
                        client.subscribe(configTopic, 1);
                    }
                }
                lastSucTime = System.currentTimeMillis();
            } catch (MqttException e) {

            } catch (NullPointerException e) { //在长时间失去网络连接后再连上mqtt，client有可能因为长时间限制而被回收，所以这里加上catch

            } catch (Exception e) {

            }
        }

        @Override
        public void onFailure(IMqttToken arg0, Throwable arg1) {
            isConnecting = false;
            Log.e(TAG, "连接失败");
            if (null != client) {
                try {
                    client.unsubscribe(pullTopic);
                    client.unsubscribe(configTopic);
                    Log.e(TAG, "取消订阅的topic");
                } catch (Exception e) {
                    CrashReport.postCatchedException(e);
                }
            }
            handler.postDelayed(() -> {
                creatConnect();
            }, 5000);
        }
    };

    /**
     *  回调
     */
    private MqttCallback mCallback = new MqttCallback() {
        @Override
        public void connectionLost(Throwable cause) {
            //失去连接
            handler.postDelayed(() -> creatConnect(), 5000);
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
     *  不限制推送数据
     * @param data
     */
    public void pushLocNoLimit(BuildPushData data) {
        pushInternalLoc(data, true);
    }

    /**
     * 推送司机及其定位信息
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
                FileUtil.delete("v5driver", "pushCache.txt");
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
            XApp.getPreferencesEditor().putLong(Config.SP_LAST_GPS_PUSH_TIME, System.currentTimeMillis()).apply();

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
            FileUtil.delete("v5driver", "pushCache.txt");
        }
    }

}
