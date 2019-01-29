package com.easymi.common.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;

import com.easymi.component.Config;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: OrderRefreshReceiver
 * @Author: shine
 * Date: 2018/12/24 下午8:46
 * Description:
 * History:
 */
public class OrderRefreshReceiver extends BroadcastReceiver {

    private OnRefreshOrderListener onRefreshOrderListener;

    public OrderRefreshReceiver(OnRefreshOrderListener onRefreshOrderListener) {
        this.onRefreshOrderListener = onRefreshOrderListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (null != intent && null != intent.getAction()) {
            String action = intent.getAction();
            if (action.equals(Config.ORDER_REFRESH)) {
                if (null != onRefreshOrderListener) {
                    onRefreshOrderListener.onRefreshOrder();
                }
            }
        }
    }

    public interface OnRefreshOrderListener {
        /**
         * 订单刷新接收
         */
        void onRefreshOrder();
    }
}