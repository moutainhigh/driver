package com.easymi.component.loc;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.trace.LBSTraceClient;
import com.easymi.component.Config;
import com.easymi.component.DJOrderStatus;
import com.easymi.component.R;
import com.easymi.component.app.XApp;
import com.easymi.component.db.SqliteHelper;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.entity.EmLoc;
import com.easymi.component.trace.TraceUtil;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.Log;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by developerLzh on 2017/11/18 0018.
 */

public class LocService extends Service implements AMapLocationListener {

    public static final String START_LOC = "com.easymi.eomponent.START_LOC";
    public static final String STOP_LOC = "com.easymi.eomponent.STOP_LOC";

    public static final String LOC_CHANGED = "com.easymi.eomponent.LOC_CHANGED";
    public static final String BROAD_TRACE_SUC = "com.easymi.eomponent.BROAD_TRACE_SUC";

    private int scanTime = Config.NORMAL_LOC_TIME;

    private static final int NOTI_ID = 1011;

    /**
     * 记录是否需要对息屏关掉wifi的情况进行处理
     */
    private boolean mIsWifiCloseable = false;

    private static final String NOTIFICATION_CHANNEL_NAME = "BackgroundLocation";
//    private NotificationManager notificationManager = null;
//    boolean isCreateChannel = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null != intent.getAction() && intent.getAction().equals(START_LOC)) {
//            applyNotiKeepMech(); //开启利用notification提高进程优先级的机制
//            if (mWifiAutoCloseDelegate.isUseful(getApplicationContext())) {
//                mIsWifiCloseable = true;
//                mWifiAutoCloseDelegate.initOnServiceStarted(getApplicationContext());
//            }
            startLoc();
            return START_STICKY;
        } else {
            if (intent.getAction() != null && intent.getAction().equals(STOP_LOC)) {
                stopService();
            }
        }
        return START_NOT_STICKY;
    }

    AMapLocationClient locClient;

    private void startLoc() {
        if (locClient == null) {
            locClient = new AMapLocationClient(this);
            locClient.setLocationListener(this);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                locClient.disableBackgroundLocation(true);
            }
            locClient.stopLocation();
        }

//        if (isDriverBusy()) {//通过是否有订单在执行设置周期
//            scanTime = Config.BUSY_LOC_TIME;
//        } else {
//            scanTime = Config.FREE_LOC_TIME;
//        }
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption()
                .setInterval(scanTime)
                .setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.Transport)
                .setGpsFirst(true)
                .setWifiScan(true)
                .setLocationCacheEnable(false)
                .setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy)
                .setNeedAddress(true)
                .setMockEnable(false)
                .setSensorEnable(true);
        locClient.setLocationOption(mLocationOption);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            locClient.enableBackgroundLocation(NOTI_ID, buildNotification(this));
        }
        startForeground(NOTI_ID, buildNotification(this));


        locClient.startLocation();
    }

    private static SqliteHelper helper;

    /**
     * 司机是否忙碌
     */
    private boolean isDriverBusy() {
        boolean isBusy;
        if (helper == null) {
            SqliteHelper.init(this);
            helper = SqliteHelper.getInstance();
        }
        List<DymOrder> dymOrders = DymOrder.findAll();
        if (dymOrders.size() == 0) {
            isBusy = false;
        } else {
            isBusy = true;
        }
        return isBusy;
    }

    protected void stopService() {
        if (null != locClient) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                locClient.disableBackgroundLocation(true);
            }
            locClient.stopLocation();
            locClient.onDestroy();
            locClient = null;
            stopSelf();
        }
    }

    LBSTraceClient lbsTraceClient;

    private void startTrace() {

        Log.e("trace", "开始纠偏");
        if (null == lbsTraceClient) {
            lbsTraceClient = LBSTraceClient.getInstance(this);
            lbsTraceClient.startTrace((list, list1, s) -> {
                if (list1 != null && list1.size() != 0
                        && list != null && list.size() != 0) {
                    EmLoc emLoc = EmUtil.getLastLoc();
                    emLoc.speed = list.get(list1.size() - 1).getSpeed();
                    emLoc.accuracy = list.get(list1.size() - 1).getBearing();
                    emLoc.latitude = list1.get(list1.size() - 1).latitude;
                    emLoc.longitude = list1.get(list1.size() - 1).longitude;
                    Intent intent = new Intent();
                    intent.setAction(BROAD_TRACE_SUC);
                    intent.putExtra("traceLoc", new Gson().toJson(emLoc));
                    sendBroadcast(intent);
                    Log.e("TraceLoc", "纠偏后的最后一个点" + emLoc.toString());
                }
            });
        }
    }

    private void stopTrace() {
        Log.e("trace", "停止纠偏");
        if (null != lbsTraceClient) {
            Log.e("trace", "停止纠偏zz");
            lbsTraceClient.stopTrace();
            lbsTraceClient = null;
        }
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == AMapLocation.LOCATION_SUCCESS) {

//                FileUtil.saveLog(this, "loc suc \n\n");

                EmLoc locationInfo = EmLoc.ALocToLoc(amapLocation);

                Log.e("locPos", "emLoc>>>>" + locationInfo.toString());

                Intent intent = new Intent(LocService.this, LocReceiver.class);
                intent.setAction(LOC_CHANGED);
                intent.putExtra("locPos", new Gson().toJson(locationInfo));
                sendBroadcast(intent);//发送位置变化广播

//                if (needTrace()) {
//                    startTrace();
//                } else {
//                    stopTrace();
//                }

            } else {
                //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());

//                FileUtil.saveLog(this, "loc failed \n\n");
            }
            if (!mIsWifiCloseable) {
                return;
            }

        }
    }

    public static Notification buildNotification(Context context) {
        boolean isLogin = XApp.getMyPreferences().getBoolean(Config.SP_ISLOGIN, false);
        Intent intent = new Intent();

        if (isLogin) {
            intent.setClassName(context, "com.easymi.common.mvp.work.WorkActivity");

        } else {
            intent.setClassName(context, "com.easymi.common.activity.SplashActivity");
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                intent, 0);

        Notification.Builder builder = null;
        Notification notification = null;
        if (Build.VERSION.SDK_INT >= 26) {
            //Android O上对Notification进行了修改，如果设置的targetSDKVersion>=26建议使用此种方式创建通知栏
//            if (null == notificationManager) {
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//            }
            String channelId = context.getPackageName();
//            if (!isCreateChannel) {
                NotificationChannel notificationChannel = new NotificationChannel(channelId,
                        NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                notificationChannel.enableLights(true);//是否在桌面icon右上角展示小圆点
                notificationChannel.setLightColor(Color.BLUE); //小圆点颜色
                notificationChannel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
                notificationChannel.setSound(null, null);
                notificationManager.createNotificationChannel(notificationChannel);
//                isCreateChannel = true;
//            }
            builder = new Notification.Builder(context, channelId);
        } else {
            builder = new Notification.Builder(context);
        }

        builder.setSmallIcon(R.mipmap.role_driver);
        builder.setSound(null);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setColor(context.getResources().getColor(R.color.colorPrimary));
        }
        builder.setContentTitle(context.getResources().getString(R.string.app_name));
        builder.setContentText(context.getResources().getString(R.string.app_name)
                + context.getResources().getString(R.string.houtai));
        builder.setWhen(System.currentTimeMillis());
        builder.setContentIntent(pendingIntent);
        builder.setOngoing(true);
        builder.setSound(null);

        notification = builder.build();

        return notification;
    }

    public static boolean needTrace() {
        if (!Config.NEED_TRACE) {
            return Config.NEED_TRACE;
        }
        List<DymOrder> dymOrders = DymOrder.findAll();
        boolean needTrace = false;
        for (DymOrder dymOrder : dymOrders) {
            if (dymOrder.orderType.equals(Config.DAIJIA)) {
                if (dymOrder.orderStatus == DJOrderStatus.GOTO_DESTINATION_ORDER) {
                    needTrace = true;
                }
            }
        }
        return needTrace;
    }
}
