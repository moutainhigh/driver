package com.easymi.common.daemon;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.easymi.common.push.MQTTService;
import com.easymi.component.Config;
import com.easymi.component.app.XApp;
import com.easymi.component.loc.LocService;
import com.easymi.component.utils.Log;
import com.easymi.component.utils.PhoneUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by developerLzh on 2017/12/14 0014.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class JobKeepLiveService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        checkAlive();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.e("JobKeepLiveService", "stop job?");
        return false;
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
                Log.e("JobKeepLiveService", "start job,check service is alive?");
                boolean isLogin = XApp.getMyPreferences().getBoolean(Config.SP_ISLOGIN, false);
                Log.e("JobKeepLiveService", "isLogin-->" + isLogin);
                if (isLogin) {
                    if (!PhoneUtil.isServiceRunning(MQTTService.class.getName(), JobKeepLiveService.this)) {
                        Log.e("JobKeepLiveService", "!isServiceRunning MQTTService");
                        Intent mqtt = new Intent(JobKeepLiveService.this, MQTTService.class);
                        mqtt.setPackage(JobKeepLiveService.this.getPackageName());
                        startService(mqtt);
                    }
                    if (!PhoneUtil.isServiceRunning(LocService.class.getName(), JobKeepLiveService.this)) {
                        Log.e("JobKeepLiveService", "!isServiceRunning LocService");
                        XApp.getInstance().startLocService();
                    }
                }
            }
        };
        timer.schedule(timerTask, 0, 60 * 1000);
    }
}
