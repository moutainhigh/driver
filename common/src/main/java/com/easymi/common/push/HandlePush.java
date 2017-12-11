package com.easymi.common.push;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.easymi.common.R;
import com.easymi.common.entity.PushMessage;
import com.easymi.common.mvp.work.WorkActivity;
import com.easymi.component.app.XApp;

/**
 * Created by Administrator on 2017/1/11.
 */

public class HandlePush {
    public HandlePush(Context context, PushMessage pushMessage) {

    }


    private void newShowNotify(Context context, String tips, String title,
                               String content, int icon) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) XApp
                .getInstance().getSystemService(ns);

        Intent notificationIntent = new Intent(context, WorkActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(
                XApp.getInstance(), 0, notificationIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                XApp.getInstance());
        builder.setSmallIcon(R.mipmap.bar_icon);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
        builder.setColor(context.getResources().getColor(R.color.colorPrimary));
        builder.setTicker(tips);
        builder.setWhen(System.currentTimeMillis());
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setContentIntent(contentIntent);

        Notification notification = builder.build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(1, notification);
    }
}
