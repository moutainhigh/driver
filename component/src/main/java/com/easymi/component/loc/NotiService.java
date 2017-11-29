package com.easymi.component.loc;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.easymi.component.ILocationHelperServiceAIDL;
import com.easymi.component.ILocationServiceAIDL;
import com.easymi.component.R;

/**
 * Created by liangchao_suxun on 17/1/16.
 * 利用双service进行notification绑定，进而将Service的OOM_ADJ提高到1
 * 同时利用LocationHelperService充当守护进程，在NotiService被关闭后，重启他。（如果LocationHelperService被停止，NotiService不负责唤醒)
 */


public class NotiService extends Service {

    /**
     * i
     * startForeground的 noti_id
     */
    private static int NOTI_ID = 123321;

    private Utils.CloseServiceReceiver mCloseReceiver;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mCloseReceiver = new Utils.CloseServiceReceiver(this);
        registerReceiver(mCloseReceiver, Utils.getCloseServiceFilter());
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        if (mCloseReceiver != null) {
            unregisterReceiver(mCloseReceiver);
            mCloseReceiver = null;
        }
        super.onDestroy();
    }


    private final String mHelperServiceName = LocationHelperService.ACTIVATE;

    /**
     * 触发利用notification增加进程优先级
     */
    protected void applyNotiKeepMech() {
        showNotify(this, R.mipmap.ic_launcher);
        startBindHelperService();
    }

    public void unApplyNotiKeepMech() {
        stopForeground(true);
    }

    public Binder mBinder;

    public class LocationServiceBinder extends ILocationServiceAIDL.Stub {
        public void onFinishBind() {
        }
    }

    private ILocationHelperServiceAIDL mHelperAIDL;

    private void startBindHelperService() {
        connection = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                //doing nothing
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                ILocationHelperServiceAIDL l = ILocationHelperServiceAIDL.Stub.asInterface(service);
                mHelperAIDL = l;
//                try {
//                    l.onFinishBind(NOTI_ID);
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                }
            }
        };
        Intent intent = new Intent();
        intent.setAction(mHelperServiceName);
        bindService(Utils.getExplicitIntent(getApplicationContext(), intent), connection, Service.BIND_AUTO_CREATE);
    }

    private ServiceConnection connection;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (mBinder == null) {
            mBinder = new LocationServiceBinder();
        }
        return mBinder;
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

        startForeground(NOTI_ID, notification);

    }

}
