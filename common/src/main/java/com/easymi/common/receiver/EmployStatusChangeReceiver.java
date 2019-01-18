package com.easymi.common.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.easymi.component.Config;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 * @Author: shine
 * Date: 2018/12/24 下午5:00
 * Description:
 * History:
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
        /**
         * 司机状态改变接收
         * @param status
         */
        void onStatusChange(String status);
    }
}
