package com.easymi.common.push;

/**
 * Created by developerLzh on 2017/12/13 0013.
 */

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.easymi.common.entity.BuildPushData;
import com.easymi.common.entity.PushBean;
import com.easymi.common.entity.PushData;
import com.easymi.common.trace.TraceInterface;
import com.easymi.common.trace.TraceReceiver;
import com.easymi.common.util.BuildPushUtil;
import com.easymi.component.Config;
import com.easymi.component.app.XApp;
import com.easymi.component.entity.EmLoc;
import com.easymi.component.loc.LocObserver;
import com.easymi.component.loc.LocReceiver;
import com.easymi.component.loc.LocService;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.FileUtil;
import com.easymi.component.utils.Log;
import com.easymi.component.utils.TimeUtil;
import com.google.gson.Gson;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * MQTT长连接服务
 */
public class MQTTService extends Service implements LocObserver, TraceInterface {

    public static final String TAG = MQTTService.class.getSimpleName();

    private static MqttAndroidClient client;
    public MqttConnectOptions conOpt;

    //    private String host = "tcp://10.0.2.2:61613";
    private String host = Config.MQTT_HOST;
    private String userName = Config.MQTT_USER_NAME;
    private String passWord = Config.MQTT_PSW;
    private static String pushTopic = Config.MQTT_PUSH_TOPIC;
    private String pullTopic;
    private String clientId = "driver-" + EmUtil.getEmployId();//身份唯一码

    private TraceReceiver traceReceiver;

    private static MQTTService instance;

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
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        synchronized (this) {
            initConn();
        }
        return START_STICKY;
    }

    private void initConn() {
        instance = this;
        if (client != null || isConning) {
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
        conOpt.setKeepAliveInterval(20);
        // 用户名
        conOpt.setUserName(userName);
        // 密码
        conOpt.setPassword(passWord.toCharArray());

        // last will message
        boolean doConnect = true;
        String message = "{\"terminal_uid\":\"" + clientId + "\"}";
        Log.e("Mqtt", message);
        pullTopic = "/driver" + "/" + Config.APP_KEY + "/" + EmUtil.getEmployId();
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

        if (doConnect && !isConning) {
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
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy:");
        try {
            // 订阅myTopic话题
            LocReceiver.getInstance().deleteObserver(MQTTService.this);
            unregisterReceiver(traceReceiver);
            if (null != client) {
                client.disconnect();
            }
            client = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    private static boolean isConning = false;//是否正在连接中

    /**
     * 连接MQTT服务器
     */
    private void doClientConnection() {
        try {
            if (null != client) {
                if (!client.isConnected() && isConnectIsNomarl()) {
                    client.connect(conOpt, null, iMqttActionListener);
                    isConning = true;
                }
            }
        } catch (MqttException e) {
            e.printStackTrace();
            isConning = false;
        } catch (Exception e) {
            e.printStackTrace();
            isConning = false;
        }
    }

    private long lastSucTime = 0;

    // MQTT是否连接成功
    public IMqttActionListener iMqttActionListener = new IMqttActionListener() {

        @Override
        public void onSuccess(IMqttToken arg0) {
            Log.e(TAG, "连接成功 ");
            isConning = false;
            try {
                if (lastSucTime == 0) {
                    client.subscribe(pullTopic, 1);
                } else {
                    if (System.currentTimeMillis() - lastSucTime < 2000) {//小于2秒的回调
                    } else {
                        client.subscribe(pullTopic, 1);
                    }
                }
                lastSucTime = System.currentTimeMillis();
            } catch (MqttException e) {
                e.printStackTrace();
            } catch (NullPointerException e) { //在长时间失去网络连接后再连上mqtt，client有可能因为长时间限制而被回收，所以这里加上catch
                initConn();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(IMqttToken arg0, Throwable arg1) {
            isConning = false;
            arg1.printStackTrace();
            Log.e(TAG, "连接失败");
            // 连接失败，重连
//            doClientConnection();
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
            Log.e(TAG, "失去连接");
            if (null != client) {
                try {
                    client.unsubscribe(pullTopic);
                    Log.e(TAG, "取消订阅的topic");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
//            LocReceiver.getInstance().deleteObserver(MQTTService.this);
            // 失去连接，重连
//            doClientConnection();
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
//        if (lastUploadTime != 0) {
//            if (System.currentTimeMillis() - lastUploadTime < 5 * 1000) {
//                return;
//            }
//        }
        Log.e("MQTTService", "receiveLoc~~");
        pushLoc(new BuildPushData(loc));
//        lastUploadTime = System.currentTimeMillis();
    }

    public static void pushLoc(BuildPushData data) {
        if (data == null) {
            return;
        }

        if (!LocService.needTrace()) {
            if (client != null && client.isConnected()) {
                String pushStr = BuildPushUtil.buildPush(data);
                publish(pushStr);

                //上传后删除本地的缓存
                FileUtil.delete("v5driver", "pushCache.txt");

//                FileUtil.saveLog(XApp.getInstance(),
//                        TimeUtil.getTime("HH:mm:ss", System.currentTimeMillis()) + ":"
//                                + "client is enable and start push data : " + pushStr + "\n\n");
            } else {

                String pushStr = BuildPushUtil.buildPush(data);

                PushBean pushBean = new Gson().fromJson(pushStr, PushBean.class);
                List<PushData> beanList = new ArrayList<>();
                for (PushData datum : pushBean.data) {
                    if (datum.calc.orderInfo != null
                            || datum.calc.orderInfo.size() != 0) {//有订单时才需要保存
                        beanList.add(datum);
                    }
                }
                if (beanList.size() != 0) {
                    FileUtil.savePushCache(XApp.getInstance(), new Gson().toJson(beanList));//只保存位置的list
                }

//                FileUtil.saveLog(XApp.getInstance(),
//                        TimeUtil.getTime("HH:mm:ss", System.currentTimeMillis()) + ":"
//                                + "client is disable and start save data : " + new Gson().toJson(pushBean) + "\n\n");
                doConnected();
            }
        }
    }

    @Override
    public void showTraceAfter(EmLoc emLoc) {
        if (emLoc == null) {
            return;
        }

        Log.e(TAG, "trace receive");

        String pushStr = BuildPushUtil.buildPush(new BuildPushData(emLoc));

        if (client != null && client.isConnected()) {
            publish(pushStr);
        } else {
            FileUtil.savePushCache(this, pushStr);
            Intent intent = new Intent(XApp.getInstance(), MQTTService.class);
            intent.setPackage(XApp.getInstance().getPackageName());
            XApp.getInstance().startService(intent);//重启推送
        }
    }

    /**
     * 外部启用重连
     */
    private static void doConnected() {
        Log.e(TAG, "开始启用外部连接");
        if (client == null) {
            Log.e(TAG, "client == null 重启服务");
            Intent intent = new Intent(XApp.getInstance(), MQTTService.class);
            intent.setPackage(XApp.getInstance().getPackageName());
            XApp.getInstance().startService(intent);//重启推送
        } else {
            try {
                if (!client.isConnected() && isConnectIsNomarl() && !isConning) {
                    if (null != MQTTService.getInstance()) {
                        Log.e(TAG, "client != null 重新连接");
                        client.connect(MQTTService.getInstance().conOpt, null, MQTTService.getInstance().iMqttActionListener);
                        isConning = true;
                    }
                }
            } catch (MqttException e) {
                isConning = false;
                e.printStackTrace();
            } catch (Exception e) {
                isConning = false;
                e.printStackTrace();
            }
        }
    }

}
