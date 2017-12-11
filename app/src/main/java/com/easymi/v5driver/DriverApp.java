package com.easymi.v5driver;

import android.content.Context;
import android.util.Log;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.easymi.common.push.AliDetailService;
import com.easymi.component.app.XApp;

/**
 * Created by developerLzh on 2017/11/3 0003.
 */

public class DriverApp extends XApp {
    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {           // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog();     // 打印日志
            ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(this); // 尽可能早，推荐在Application中初始化
        initCloudChannel(this);
    }

    /**
     * 初始化云推送通道
     *
     * @param applicationContext
     */
    private void initCloudChannel(Context applicationContext) {
        PushServiceFactory.init(applicationContext);
        CloudPushService pushService = PushServiceFactory.getCloudPushService();
        pushService.register(applicationContext, new CommonCallback() {
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

}
