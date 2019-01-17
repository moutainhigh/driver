package com.easymi.common.receiver;

import android.content.Context;

import com.alibaba.sdk.android.push.MessageReceiver;
import com.alibaba.sdk.android.push.notification.CPushMessage;
import com.easymi.common.push.HandlePush;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.Log;
import com.easymi.component.utils.StringUtils;

import java.util.Map;

/**
 *
 * @author liuzihao
 * @date 2018/3/7
 */

public class AliPushReceiver extends MessageReceiver {
    private static final String TAG = "AliPushReceiver";
    @Override
    protected void onNotification(Context context, String s, String s1, Map<String, String> map) {
        Log.d(TAG, "onNotification -> ");
    }

    @Override
    protected void onMessage(Context context, CPushMessage cPushMessage) {
        if (null != cPushMessage) {
            String payload = cPushMessage.getContent();
            if (StringUtils.isNotBlank(payload)) {
                long driverId = EmUtil.getEmployId();
                if (driverId != 0) {
                    HandlePush.getInstance().handPush(payload);
                }
                Log.d(TAG, "ali receiver payload : " + payload);
            }
        }
        Log.d(TAG, "----------------------------------------------------------------------------------------------");
    }

    @Override
    protected void onNotificationOpened(Context context, String s, String s1, String s2) {
        Log.d(TAG, "onNotificationOpened -> ");
    }

    @Override
    protected void onNotificationClickedWithNoAction(Context context, String s, String s1, String s2) {
        Log.d(TAG, "onNotificationClickedWithNoAction -> ");
    }

    @Override
    protected void onNotificationRemoved(Context context, String s) {
        Log.d(TAG, "onNotificationRemoved -> ");
    }

    @Override
    protected void onNotificationReceivedInApp(Context context, String s, String s1, Map<String, String> map, int i, String s2, String s3) {
        Log.d(TAG, "onNotificationReceivedInApp -> ");
    }
}
