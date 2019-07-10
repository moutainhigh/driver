package com.easymi.common.push;

import android.text.TextUtils;

import com.easymi.common.CommApiService;
import com.easymi.common.entity.BuildPushData;
import com.easymi.common.entity.PushMessage;
import com.easymi.common.entity.PushPojo;
import com.easymi.common.result.GetFeeResult;
import com.easymi.common.util.BuildPushUtil;
import com.easymi.component.Config;
import com.easymi.component.ZCOrderStatus;
import com.easymi.component.app.XApp;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.entity.EmLoc;
import com.easymi.component.loc.LocObserver;
import com.easymi.component.loc.LocReceiver;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.HttpResultFunc3;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.network.NoErrSubscriberListener;
import com.easymi.component.result.EmResult2;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.FileUtil;
import com.easymi.component.utils.Log;
import com.easymi.component.utils.NetUtil;
import com.easymi.component.utils.StringUtils;
import com.google.gson.Gson;
import com.tencent.bugly.crashreport.CrashReport;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

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

    private static String TAG = "MqttManager";

    // 单例
    private static MqttManager mInstance = null;

    private int qos = 2;

    /**
     * Private instance variables
     */
    private MqttAndroidClient client;
    private MqttConnectOptions conOpt;


    private String pullTopic;

    private RxManager rxManager;
    private boolean isWorking;

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
        Log.e("MqttManager", "onRelease");
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
    public void creatConnect() {

        if (!XApp.getMyPreferences().getBoolean(Config.SP_ISLOGIN, false)) {
            //未登陆 不连接
            return;
        }

        if (isConnected()) {
            //client连接起的  不连接
            return;
        }
        if (TextUtils.isEmpty(Config.MQTT_TOPIC)) {
            return;
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
        conOpt.setKeepAliveInterval(5);
        //自己处理了重连事件
        conOpt.setAutomaticReconnect(true);
        String message = "{\"terminal_uid\":\"" + clientId + "\"}";
        conOpt.setWill(pullTopic, message.getBytes(), qos, false);

        client = new MqttAndroidClient(XApp.getInstance(), brokerUrl, clientId);

        client.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                Log.e("MqttManager", "connectComplete  " + reconnect);
                try {
                    client.subscribe(pullTopic, qos, null, new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            Log.e("MqttManager", "subscribeSuccess");
                            if (reconnect) {
                                getOrderStatus();
                            }
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            Log.e("MqttManager", "subscribeFail");
                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void connectionLost(Throwable cause) {
                //失去连接
                Log.e(TAG, "connectionLost");
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
        });
        doConnect();
    }


    private void getOrderStatus() {
        Observable<EmResult2<PushPojo>> observable = null;
        List<DymOrder> data = DymOrder.findAll();
        if (data.size() > 0) {
            long orderId = 0;
            for (DymOrder datum : data) {
                if (TextUtils.equals(datum.orderType, Config.ZHUANCHE) && (datum.orderStatus == ZCOrderStatus.PAIDAN_ORDER
                        || datum.orderStatus == ZCOrderStatus.GOTO_BOOKPALCE_ORDER)) {
                    orderId = datum.orderId;
                    break;
                }
            }
            if (orderId != 0) {
                observable = ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                        .getOrderStatus(orderId);
            }
        } else {
            observable = ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                    .getNewOrder();
        }

        if (observable != null) {
            observable
                    .filter(new HttpResultFunc3<>())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new MySubscriber<>(null, false, false, new NoErrSubscriberListener<EmResult2<PushPojo>>() {
                        @Override
                        public void onNext(EmResult2<PushPojo> pushPojoEmResult2) {
                            HandlePush.getInstance().handPush(new Gson().toJson(pushPojoEmResult2), false);
                        }
                    }));
        }
    }

    /**
     * 建立连接
     *
     * @return
     */
    private void doConnect() {
        if (client != null) {
            try {
                client.connect(conOpt, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                        disconnectedBufferOptions.setBufferEnabled(true);
                        disconnectedBufferOptions.setBufferSize(100);
                        disconnectedBufferOptions.setPersistBuffer(false);
                        disconnectedBufferOptions.setDeleteOldestMessages(false);
                        client.setBufferOpts(disconnectedBufferOptions);
                        Log.e("MqttManager", "connectSuccess");
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.e("MqttManager", "connectFailure   " + exception.getMessage());
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "doConnect exception-->" + e.getMessage());
            }
        }
    }


    private void sendTemp(List<String> data) {
        if (client != null && client.isConnected()) {
            if (data == null || data.isEmpty()) {
                return;
            }
            Log.e("MqttManager", "sendTemp" + data.size());
            String content = data.get(0);
            if (StringUtils.isBlank(content)) {
                return;
            }
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(qos);
            try {
                client.publish(Config.MQTT_TOPIC, message, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.e("MqttManager", "sendTempSuccess");
                        PushMessage.delete(content);
                        List<String> temp = PushMessage.findAll();
                        if (temp.size() > 0) {
                            sendTemp(temp);
                        } else {
                            isWorking = false;
                        }
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.e("MqttManager", "sendTempFail");
                        isWorking = false;
                    }
                });
            } catch (MqttException e) {
                e.fillInStackTrace();
            }
        }
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
                client.publish(Config.MQTT_TOPIC, message, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.e("MqttManager", "sendSuccess");
                        List<String> temp = PushMessage.findAll();
                        if (temp.size() > 0 && !isWorking) {
                            isWorking = true;
                            sendTemp(temp);
                        }
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        PushMessage.save(pushStr);
                        Log.e("MqttManager", "sendFail");
                    }
                });
            } catch (MqttException e) {
                e.fillInStackTrace();
                Log.e("MqttManager", "Publishing msg exception " + e.getMessage());
            }
        } else {
            Log.e("MqttManager", "sendFail null");
            PushMessage.save(pushStr);
        }
    }


    public void publishAck(long orderId, int type) {
        if (orderId == 0) {
            return;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("orderId", orderId);
            jsonObject.put("appKey", Config.APP_KEY);
            jsonObject.put("msgTime", System.currentTimeMillis());
            jsonObject.put("ackType", type);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (client != null && client.isConnected()) {
            MqttMessage message = new MqttMessage(jsonObject.toString().getBytes());
            message.setQos(qos);
            try {
                client.publish(Config.ACK_TOPIC, message);
//                Log.e("MqttManager", "push loc data--->" + pushStr);
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
        if (data != null) {
            String pushStr = BuildPushUtil.buildPush(data, noLimit);

            if (pushStr == null) {
                Exception exception = new IllegalArgumentException("自定义异常：推送数据为空，可能是司机信息为空");
                CrashReport.postCatchedException(exception);
                return;
            }
            publish(pushStr);
            //上传后删除本地的缓存
            FileUtil.delete("v6driver", "pushCache.txt");
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
