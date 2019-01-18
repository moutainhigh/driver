package com.easymi.common.daemon;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: FinishActivity
 *@Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

//import com.easymi.common.push.MQTTService;
import com.easymi.component.Config;
import com.easymi.component.app.XApp;
import com.easymi.component.loc.LocService;
import com.easymi.component.utils.Log;
import com.easymi.component.utils.PhoneUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author developerLzh
 * @date 2017/11/9 0009
 * <p>
 * 保活service
 */

public class DaemonService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("DaemonService", "onCreate");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("DaemonService", "onStartCommand");
        checkAlive();
        return START_STICKY;
    }

    /**
     * 定时器
     */
    private Timer timer;
    private TimerTask timerTask;

    /**
     * 检查保活时候在运行
     */
    private void checkAlive() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask.cancel();
        }

        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Log.e("DaemonService", "start daemon,check service is alive?");
                boolean isLogin = XApp.getMyPreferences().getBoolean(Config.SP_ISLOGIN, false);
                Log.e("DaemonService", "isLogin-->" + isLogin);
                if (isLogin) {
                    if (!PhoneUtil.isServiceRunning(LocService.class.getName(), DaemonService.this)) {
                        Log.e("DaemonService", "!isServiceRunning LocService");
                        XApp.getInstance().startLocService();
                    }
                }
            }
        };
        timer.schedule(timerTask, 0, 10 * 1000);
    }
}
