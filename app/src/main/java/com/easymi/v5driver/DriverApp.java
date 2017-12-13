package com.easymi.v5driver;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.easymi.common.daemon.DaemonService;
import com.easymi.common.daemon.PuppetReceiver1;
import com.easymi.common.daemon.PuppetReceiver2;
import com.easymi.common.daemon.PuppetService;
import com.easymi.common.push.AliDetailService;
import com.easymi.common.push.MQTTService;
import com.easymi.component.app.XApp;
import com.marswin89.marsdaemon.DaemonClient;
import com.marswin89.marsdaemon.DaemonConfigurations;

/**
 * Created by developerLzh on 2017/11/3 0003.
 */

public class DriverApp extends XApp {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        DaemonClient daemonClient = new DaemonClient(getDaemonConfigurations());//保活client
        daemonClient.onAttachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (!isAppProcess()) {//防止多次调用onCreate()
            return;
        }
        if (BuildConfig.DEBUG) {           // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog();     // 打印日志
            ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(this); // 尽可能早，推荐在Application中初始化
        initCloudChannel();
        initMQTT();
    }

    /**
     * 开启阿里云推送服务
     */
    public void initCloudChannel() {
        PushServiceFactory.init(this);
        CloudPushService pushService = PushServiceFactory.getCloudPushService();
        pushService.register(this, new CommonCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d("DriverApp", "init cloudchannel success");
                PushServiceFactory.getCloudPushService().turnOnPushChannel(null);//打开推送通道
                PushServiceFactory.getCloudPushService().setPushIntentService(AliDetailService.class);
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                Log.d("DriverApp", "init cloudchannel failed -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);
            }
        });
    }

    /**
     * 开启MQTT服务
     */
    public void initMQTT() {
        Intent mqtt = new Intent(this, MQTTService.class);
        mqtt.setPackage(this.getPackageName());
        this.startService(mqtt);
    }

    protected DaemonConfigurations getDaemonConfigurations() {
        DaemonConfigurations.DaemonConfiguration configuration1 = new DaemonConfigurations.DaemonConfiguration(
                getPackageName() + ":process1",
                DaemonService.class.getCanonicalName(),
                PuppetReceiver1.class.getCanonicalName());

        DaemonConfigurations.DaemonConfiguration configuration2 = new DaemonConfigurations.DaemonConfiguration(
                getPackageName() + ":process2",
                PuppetService.class.getCanonicalName(),
                PuppetReceiver2.class.getCanonicalName());

        DaemonConfigurations.DaemonListener listener = new MyDaemonListener();
        //return new DaemonConfigurations(configuration1, configuration2);//listener can be null
        return new DaemonConfigurations(configuration1, configuration2, listener);
    }

    class MyDaemonListener implements DaemonConfigurations.DaemonListener {
        @Override
        public void onPersistentStart(Context context) {
            Log.e("daemon", "--onPersistentStart--");
        }

        @Override
        public void onDaemonAssistantStart(Context context) {
            Log.e("daemon", "--onDaemonAssistantStart--");
        }

        @Override
        public void onWatchDaemonDaed() {
            Log.e("daemon", "--onWatchDaemonDaed--");
        }
    }

}
