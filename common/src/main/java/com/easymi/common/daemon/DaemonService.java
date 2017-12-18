package com.easymi.common.daemon;

/**
 * Created by developerLzh on 2017/12/13 0013.
 */

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.easymi.common.push.MQTTService;
import com.easymi.component.Config;
import com.easymi.component.app.XApp;
import com.easymi.component.loc.LocService;
import com.easymi.component.utils.PhoneUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by developerLzh on 2017/11/9 0009.
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

    private Timer timer;
    private TimerTask timerTask;

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
//                    if (!PhoneUtil.isServiceRunning(MQTTService.class.getName(), DaemonService.this)) {
                        Log.e("DaemonService", "!isServiceRunning MQTTService");
                        Intent mqtt = new Intent(DaemonService.this, MQTTService.class);
                        mqtt.setPackage(DaemonService.this.getPackageName());
                        startService(mqtt);
//                    }
//                    if (!PhoneUtil.isServiceRunning(LocService.class.getName(), DaemonService.this)) {
                        Log.e("DaemonService", "!isServiceRunning LocService");
                        XApp.getInstance().startLocService();
//                    }
                }
            }
        };
        timer.schedule(timerTask, 0, 10 * 1000);
    }
}
