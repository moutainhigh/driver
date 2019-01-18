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

public class AnnReceiver extends BroadcastReceiver {

    private OnReceiveAnn onReceiveAnn;

    public AnnReceiver(OnReceiveAnn onReceiveAnn) {
        this.onReceiveAnn = onReceiveAnn;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (null != intent && null != intent.getAction()) {
            String action = intent.getAction();
            if (action.equals(Config.BROAD_ANN)) {
                if (null != onReceiveAnn) {
                    String msg = intent.getStringExtra("ann");
                    onReceiveAnn.onReceiveAnn(msg);
                }
            }
        }
    }

    public interface OnReceiveAnn {
        /**
         * 公告接收
         * @param message
         */
        void onReceiveAnn(String message);
    }
}
