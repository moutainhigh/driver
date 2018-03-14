package com.easymi.component.loc;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.trace.LBSTraceClient;
import com.easymi.component.Config;
import com.easymi.component.app.XApp;
import com.easymi.component.db.SqliteHelper;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.entity.EmLoc;
import com.easymi.component.utils.Log;
import com.easymi.component.utils.StringUtils;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by developerLzh on 2017/11/18 0018.
 * -------------注意注意！！！！！！-------------
 * 这是在另外一个进程里面，不要直接改变其他进程的数据
 */

public class LocService extends NotiService implements AMapLocationListener {

    public static final String START_LOC = "com.easymi.eomponent.START_LOC";
    public static final String STOP_LOC = "com.easymi.eomponent.STOP_LOC";

    public static final String LOC_CHANGED = "com.easymi.eomponent.LOC_CHANGED";
    public static final String BROAD_TRACE_SUC = "com.easymi.eomponent.BROAD_TRACE_SUC";

    private int scanTime = Config.FREE_LOC_TIME;

    private static final int NOTI_ID = 1011;

    /**
     * 处理息屏关掉wifi的delegate类
     */
    private IWifiAutoCloseDelegate mWifiAutoCloseDelegate = new WifiAutoCloseDelegate();
    /**
     * 记录是否需要对息屏关掉wifi的情况进行处理
     */
    private boolean mIsWifiCloseable = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null != intent.getAction() && intent.getAction().equals(START_LOC)) {
            applyNotiKeepMech(); //开启利用notification提高进程优先级的机制
            if (mWifiAutoCloseDelegate.isUseful(getApplicationContext())) {
                mIsWifiCloseable = true;
                mWifiAutoCloseDelegate.initOnServiceStarted(getApplicationContext());
            }
            showNotify(this);
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
            locClient.stopLocation();
        }

        if (isDriverBusy()) {//通过是否有订单在执行设置周期
            scanTime = Config.BUSY_LOC_TIME;
        } else {
            scanTime = Config.FREE_LOC_TIME;
        }
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption()
                .setInterval(scanTime)
                .setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.Transport)
                .setGpsFirst(true)
                .setWifiScan(false)
                .setLocationCacheEnable(false)
                .setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy)
                .setNeedAddress(true)
                .setMockEnable(false)
                .setSensorEnable(true);
        locClient.setLocationOption(mLocationOption);

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

    @Override
    protected void stopService() {
        super.stopService();
        if (null != locClient) {
            locClient.stopLocation();
            locClient.onDestroy();
            locClient = null;
            stopSelf();
        }
    }

    LBSTraceClient lbsTraceClient;

    private void startTrace() {

        if (null == lbsTraceClient) {
            lbsTraceClient = LBSTraceClient.getInstance(this);
            lbsTraceClient.startTrace((list, list1, s) -> {
                if (list1 != null && list1.size() != 0
                        && list != null && list.size() != 0) {
                    EmLoc emLoc = new EmLoc();
                    emLoc.speed = list.get(list1.size() - 1).getSpeed();
                    emLoc.accuracy = list.get(list1.size() - 1).getBearing();
                    emLoc.latitude = list1.get(list1.size() - 1).latitude;
                    emLoc.longitude = list1.get(list1.size() - 1).longitude;
                    Intent intent = new Intent();
                    intent.setAction(BROAD_TRACE_SUC);
                    intent.putExtra("traceLoc", new Gson().toJson(emLoc));
                    sendBroadcast(intent);
                }
            });
        }
    }

    private void stopTrace() {
        if (null != lbsTraceClient) {
            lbsTraceClient.stopTrace();
            lbsTraceClient = null;
        }
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == AMapLocation.LOCATION_SUCCESS) {
                EmLoc locationInfo = EmLoc.ALocToLoc(amapLocation);

                if(StringUtils.isBlank(locationInfo.poiName)){//只有经纬度信息 没有地理位置信息的就不广播出来，作废
                    return;
                }

                if (needTrace()) {
                    startTrace();
                } else {
                    stopTrace();
                }

                Log.e("locPos", "bearing>>>>" + locationInfo.bearing);
                Intent intent = new Intent(LocService.this, LocReceiver.class);
                intent.setAction(LOC_CHANGED);
                intent.putExtra("locPos", new Gson().toJson(locationInfo));
                sendBroadcast(intent);//发送位置变化广播

            } else {
                //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
            if (!mIsWifiCloseable) {
                return;
            }

            if (amapLocation.getErrorCode() == AMapLocation.LOCATION_SUCCESS) {
                mWifiAutoCloseDelegate.onLocateSuccess(getApplicationContext(), PowerManagerUtil.getInstance().isScreenOn(getApplicationContext()), NetUtil.getInstance().isMobileAva(getApplicationContext()));
            } else {
                mWifiAutoCloseDelegate.onLocateFail(getApplicationContext(), amapLocation.getErrorCode(), PowerManagerUtil.getInstance().isScreenOn(getApplicationContext()), NetUtil.getInstance().isWifiCon(getApplicationContext()));
            }
        }
    }

    public static boolean needTrace() {
//        List<DymOrder> dymOrders = DymOrder.findAll();
        boolean needTrace = false;
//        for (DymOrder dymOrder : dymOrders) {
//            if (dymOrder.orderType.equals(Config.DAIJIA)) {
//                if (dymOrder.orderStatus == DJOrderStatus.GOTO_DESTINATION_ORDER) {
//                    needTrace = true;
//                }
//            }
//        }
        return needTrace;
    }

    private void showNotify(Context context) {


        boolean isLogin = XApp.getMyPreferences().getBoolean(Config.SP_ISLOGIN, false);
        Intent intent = new Intent();

        if (isLogin) {
            intent.setClassName(context, "com.easymi.common.mvp.work.WorkActivity");

        } else {
            intent.setClassName(context, "com.easymi.common.activity.SplashActivity");
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context, "1011");

        builder.setSmallIcon(com.easymi.component.R.mipmap.role_driver);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), com.easymi.component.R.mipmap.ic_launcher));
        builder.setColor(getResources().getColor(com.easymi.component.R.color.colorPrimary));
        builder.setContentTitle(getResources().getString(com.easymi.component.R.string.app_name));
        builder.setContentText(getResources().getString(com.easymi.component.R.string.app_name)
                + context.getResources().getString(com.easymi.component.R.string.houtai));
        builder.setWhen(System.currentTimeMillis());
        builder.setContentIntent(pendingIntent);
        builder.setOngoing(true);
//        builder.setTicker(getResources().getString(R.string.app_name)
//                + "正在后台运行");

        Notification notification = builder.build();
        notification.flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;

        startForeground(NOTI_ID, notification);

    }
}
