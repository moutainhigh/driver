package com.easymin.daijia.driver.zyziyunsjdaijia;

import android.content.Context;
import android.text.TextUtils;

import com.easymi.common.daemon.DaemonService;
import com.easymi.common.daemon.PuppetReceiver1;
import com.easymi.common.daemon.PuppetReceiver2;
import com.easymi.common.daemon.PuppetService;
import com.easymi.component.Config;
import com.easymi.component.app.XApp;
import com.easymi.component.entity.EnvironmentPojo;
import com.easymi.component.utils.Log;
import com.easymin.daijia.driver.zyziyunsjdaijia.config.MainConfig;
import com.google.gson.Gson;
import com.marswin89.marsdaemon.DaemonClient;
import com.marswin89.marsdaemon.DaemonConfigurations;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: DriverApp
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:  Application实现类
 * History:
 */
public class DriverApp extends XApp {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        new MainConfig(this);
        //保活client
        DaemonClient daemonClient = new DaemonClient(getDaemonConfigurations());
        daemonClient.onAttachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (!isAppProcess()) {
            //防止多次调用onCreate()
            return;
        }
        changeConfig();
    }

    private void changeConfig() {
        String data = XApp.getMyPreferences().getString("environment_setting", "");
        if (!TextUtils.isEmpty(data)) {
            EnvironmentPojo environmentPojo = new Gson().fromJson(data, EnvironmentPojo.class);
            Config.HOST = environmentPojo.host;
            Config.IS_ENCRYPT = environmentPojo.encryption;
            Config.H5_HOST = environmentPojo.h5Host;
            Config.IMG_SERVER = environmentPojo.server;
            Config.APP_KEY = environmentPojo.appKey;
        }
    }

    /**
     * 保活进程
     * @return
     */
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

    /**
     * 进程监听
     */
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
