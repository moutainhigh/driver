package com.easymi.component;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.easymi.component.app.XApp;
import com.easymi.component.entity.EmLoc;
import com.google.gson.Gson;

/**
 * Created by developerLzh on 2017/11/18 0018.
 */

public class LocService extends Service {

    public static final int Notification_ID = 0x998992;

    public static final String START_LOC = "com.easymi.eomponent.START_LOC";
    public static final String STOP_LOC = "com.easymi.eomponent.STOP_LOC";

    public static final String LOC_CHANGED = "com.easymi.eomponent.LOC_CHANGED";

    private int scanTime = 5000;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null != intent && intent.getAction().equals(START_LOC)) {
            showNotify(this, R.mipmap.ic_launcher);
            startLoc();
            return START_STICKY;
        } else {
            stopLoc();
        }
        return START_NOT_STICKY;
    }

    AMapLocationClient locClient;

    private void startLoc() {
        if (locClient == null) {
            locClient = new AMapLocationClient(this);
            locClient.setLocationListener(amapLocation -> {
                if (amapLocation != null) {
                    if (amapLocation.getErrorCode() == 0) {
                        EmLoc locationInfo = EmLoc.ALocToLoc(amapLocation);
                        XApp.getPreferencesEditor().putString(Config.SP_LAST_LOC, new Gson().toJson(locationInfo)).apply();//保存上次的位置信息 json格式字符创
                        Log.e("locPos", locationInfo.toString());
                        Intent intent = new Intent();
                        intent.setAction(LOC_CHANGED);
                        sendBroadcast(intent);//发送位置变化广播
                    } else {
                        //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                        Log.e("AmapError", "location Error, ErrCode:"
                                + amapLocation.getErrorCode() + ", errInfo:"
                                + amapLocation.getErrorInfo());
                    }
                }
            });
        } else {
            locClient.stopLocation();
        }
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption()
                .setInterval(scanTime)
                .setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy)
                .setNeedAddress(true)
                .setMockEnable(false)
                .setSensorEnable(true);
        locClient.setLocationOption(mLocationOption);

        locClient.startLocation();
    }

    private void stopLoc() {
        if (null != locClient) {
            locClient.stopLocation();
            locClient.onDestroy();
        }
    }

    private void showNotify(Context context, int largeIcon) {

        Intent intent = new Intent();
        intent.setClassName(context, "com.easymi.common.mvp.work.WorkActivity");
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context);

        builder.setSmallIcon(R.mipmap.role_driver);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), largeIcon));
        builder.setColor(getResources().getColor(R.color.colorPrimary));
        builder.setContentTitle(getResources().getString(R.string.app_name));
        builder.setContentText(getResources().getString(R.string.app_name)
                + context.getResources().getString(R.string.houtai));
        builder.setWhen(System.currentTimeMillis());
        builder.setContentIntent(pendingIntent);
        builder.setOngoing(true);
//        builder.setTicker(getResources().getString(R.string.app_name)
//                + "正在后台运行");

        Notification notification = builder.build();
        notification.flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;

        startForeground(Notification_ID, notification);

    }
}
