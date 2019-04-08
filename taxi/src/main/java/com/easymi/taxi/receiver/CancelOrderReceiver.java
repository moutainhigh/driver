package com.easymi.taxi.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.easymi.component.Config;
import com.easymi.taxi.R;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: CancelOrderReceiver
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description: 取消订单或者收回广播接收者
 * History:
 */

public class CancelOrderReceiver extends BroadcastReceiver {

    private OnCancelListener cancelListener;

    public CancelOrderReceiver(OnCancelListener cancelListener) {
        this.cancelListener = cancelListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (null != intent && null != intent.getAction()) {
            String action = intent.getAction();
            if (action.equals(Config.BROAD_CANCEL_ORDER) || action.equals(Config.BROAD_BACK_ORDER)) {
                if (null != cancelListener) {
                    Long orderId = intent.getLongExtra("orderId", -1);
                    String orderType = intent.getStringExtra("orderType");
                    if(action.equals(Config.BROAD_CANCEL_ORDER)){
                        cancelListener.onCancelOrder(orderId, orderType,context.getString(R.string.canceled_order));
                    } else {
                        cancelListener.onCancelOrder(orderId, orderType,context.getString(R.string.backed_order));
                    }
                }
            }
        }
    }

    /**
     * 用于分发广播消息
     */
    public interface OnCancelListener {
        /**
         * 取消收回监听
         * @param orderId
         * @param orderType
         * @param msg
         */
        void onCancelOrder(long orderId, String orderType, String msg);
    }
}
