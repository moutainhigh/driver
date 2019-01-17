package com.easymi.common.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.easymi.component.Config;

/**
 *
 * @author developerLzh
 * @date 2017/12/19 0019
 */

public class NoticeReceiver extends BroadcastReceiver {

    private OnReceiveNotice onReceiveNotice;

    public NoticeReceiver(OnReceiveNotice onReceiveNotice) {
        this.onReceiveNotice = onReceiveNotice;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (null != intent && null != intent.getAction()) {
            String action = intent.getAction();
            if (action.equals(Config.BROAD_NOTICE)) {
                if (null != onReceiveNotice) {
                    String msg = intent.getStringExtra("notice");
                    onReceiveNotice.onReceiveNotice(msg);
                }
            }
        }
    }

    public interface OnReceiveNotice {
        /**
         * 通知接收
         * @param message
         */
        void onReceiveNotice(String message);
    }
}
