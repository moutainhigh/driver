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

public class EmployStatusChangeReceiver extends BroadcastReceiver {

    private OnStatusChangeListener changeListener;

    public EmployStatusChangeReceiver(OnStatusChangeListener changeListener) {
        this.changeListener = changeListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (null != intent && null != intent.getAction()) {
            String action = intent.getAction();
            if (action.equals(Config.BROAD_CANCEL_ORDER)) {
                if (null != changeListener) {
                    String status = intent.getStringExtra("status");
                    changeListener.onStatusChange(status);
                }
            }
        }
    }

    public interface OnStatusChangeListener {
        void onStatusChange(String status);
    }
}
