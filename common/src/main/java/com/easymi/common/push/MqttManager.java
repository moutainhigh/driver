package com.easymi.common.push;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.easymi.common.CommApiService;
import com.easymi.common.entity.MqttReconnectEvent;
import com.easymi.common.entity.PushBean;
import com.easymi.common.entity.PushMessage;
import com.easymi.common.entity.PushPojo;
import com.easymi.common.util.BuildPushUtil;
import com.easymi.component.Config;
import com.easymi.component.ZCOrderStatus;
import com.easymi.component.app.XApp;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.entity.EmLoc;
import com.easymi.component.loc.LocObserver;
import com.easymi.component.loc.LocReceiver;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc3;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.network.NoErrSubscriberListener;
import com.easymi.component.result.EmResult2;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.Log;
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
import org.greenrobot.eventbus.EventBus;
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
    private Handler handler;
    private boolean isConnecting = false;//是否正在连接

    /**
     * 初始化
     */
    private MqttManager() {
        rxManager = new RxManager();
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == 0) {
                    publish();
                }
                return true;
            }
        });
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
        if (mInstance != null) {
            mInstance.disConnect();
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
            Log.e("MqttManager", "SP_ISLOGIN");
            return;
        }
        if (isConnecting) {//重连时不连
            Log.e("MqttManager", "isConnecting");
            return;
        }
        if (isConnected()) {
            //client连接起的  不连接
            Log.e("MqttManager", "isConnected");
            return;
        }
        if (TextUtils.isEmpty(Config.MQTT_TOPIC)) {
            Log.e("MqttManager", "MQTT_TOPIC");
            return;
        }
        if (client != null) {
            client.unregisterResources();
        }

        Log.e("MqttManager", "creatConnect");

        pullTopic = "/trip/driver" + "/" + EmUtil.getAppKey() + "/" + EmUtil.getEmployId();
        String brokerUrl = "tcp://" + Config.MQTT_HOST + ":" + Config.PORT_TCP;
        //身份唯一码
        String clientId = "driver-" + EmUtil.getEmployId();

        if (conOpt == null) {
            conOpt = new MqttConnectOptions();
            conOpt.setCleanSession(true);
            conOpt.setUserName(Config.MQTT_USER_NAME);
            conOpt.setPassword(Config.MQTT_PSW.toCharArray());
            // 设置超时时间，单位：秒
            conOpt.setConnectionTimeout(20);
            // 心跳包发送间隔，单位：秒
            conOpt.setKeepAliveInterval(20);
//        conOpt.setAutomaticReconnect(true);
            String message = "{\"terminal_uid\":\"" + clientId + "\"}";
            conOpt.setWill(pullTopic, message.getBytes(), qos, false);
        }
        client = new MqttAndroidClient(XApp.getInstance(), brokerUrl, clientId);
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.e(TAG, "connectionLost");
                //失去连接后延时3秒重连
                isConnecting = false;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        creatConnect();
                    }
                }, 3000);
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
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

    private void notifySendDelayed() {
        if (mInstance == null) {
            return;
        }
        if (handler == null) {
            return;
        }
        removeNotify(0);
        handler.sendEmptyMessageDelayed(0, 10000);
    }

    private void notifySend() {
        if (mInstance == null) {
            return;
        }
        if (handler == null) {
            return;
        }
        removeNotify(0);
        handler.sendEmptyMessage(0);
    }

    private void removeNotify(int what) {
        if (mInstance == null) {
            return;
        }
        if (handler == null) {
            return;
        }
        if (handler.hasMessages(what)) {
            handler.removeMessages(what);
        }
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
        Log.e("MqttManager", "doConnect ");
        try {
            LocReceiver.getInstance().addObserver(this);
            isConnecting = true;
            client.connect(conOpt, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    isConnecting = false;
                    Log.e("MqttManager", "connectSuccess");
                    try {
                        client.unsubscribe(pullTopic);//订阅topic前先取消订阅，防止重复订阅
                        client.subscribe(pullTopic, qos, null, new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) {
                                isConnecting = false;
                                Log.e("MqttManager", "subscribeSuccess");
                                //连上后走接口看有没有余冗数据
                                getOrderStatus();
                                //连接后立即发送
                                notifySend();
                            }

                            @Override
                            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                                Log.e("MqttManager", "subscribeFail");
                                sendReconnectEvent();
                            }
                        });
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e("MqttManager", "connectFailure   " + exception.getMessage());
                    sendReconnectEvent();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "doConnect exception-->" + e.getMessage());
        }

    }

    private void sendReconnectEvent() {
        EventBus.getDefault().post(new MqttReconnectEvent());
    }

    /**
     * 推送消息
     *
     * @return boolean
     */
    public void publish() {
        if (client != null && client.isConnected()) {
            List<PushMessage> pushList;
            Log.e("MqttManager", "sendTotalSize  " + PushMessage.findAll().size());
            if (PushMessage.findAll().size() > 20) {
                pushList = PushMessage.findAll().subList(0, 20);
            } else {
                pushList = PushMessage.findAll();
            }

            if (pushList == null || pushList.size() == 0) {
                //没有数据 等待下次发送
                notifySendDelayed();
                return;
            }

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("[");
            for (int i = 0; i < pushList.size(); i++) {
                stringBuilder.append(pushList.get(i).data);
                if (i != pushList.size() - 1) {
                    stringBuilder.append(",");
                }
            }
            stringBuilder.append("]");

            Log.e("MqttManager", "sendContent  " + pushList.size());
            MqttMessage message = new MqttMessage(stringBuilder.toString().getBytes());
            message.setQos(qos);

            try {
                client.publish(Config.MQTT_TOPIC, message, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        //删除已发送数据
                        PushMessage.delete(pushList);
                        Log.e("MqttManager", "sendSuccess   restSize==  " + PushMessage.findAll().size() + "      " + stringBuilder.toString());
                        if (PushMessage.findAll().size() > 0) {
                            //发现有多余数据立即发送
                            notifySend();
                        } else {
                            //等待下次发送
                            notifySendDelayed();
                        }
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.e("MqttManager", "sendFail");
                        sendReconnectEvent();
                    }
                });
            } catch (MqttException e) {
                e.fillInStackTrace();
            }
        } else {
            //重连
            sendReconnectEvent();
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
    private void disConnect() {
        rxManager.clear();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        isConnecting = false;
        if (client == null) {
            return;
        }
        try {
            if (client.isConnected()) {
                client.disconnect();
            }
        } catch (MqttException e) {
            Log.e("MqttManager", "MqttException " + e.getMessage());
            e.fillInStackTrace();
        } catch (Exception e) {
            Log.e("MqttManager", "Exception " + e.getMessage());
            e.fillInStackTrace();
        } finally {
            client.unregisterResources();
            client = null;
        }
    }

    @Override
    public void receiveLoc(EmLoc loc) {
        savePushMessage(loc);
    }

    /**
     * 推送司机及其定位信息
     *
     * @param data
     */
    public void savePushMessage(EmLoc data) {
        if (data != null) {
            PushBean pushBean = BuildPushUtil.buildPush(data);
            if (pushBean == null) {
                Exception exception = new IllegalArgumentException("自定义异常：推送数据为空，可能是司机信息为空");
                CrashReport.postCatchedException(exception);
                return;
            }
            PushMessage.save(pushBean);
        }
    }
}