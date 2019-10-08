package com.easymi.taxi.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.easymi.component.Config;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: OrderFinishReceiver
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */
public class OrderFinishReceiver extends BroadcastReceiver {
    private OnFinishListener finishListener;

    public OrderFinishReceiver(OnFinishListener finishListener) {
        this.finishListener = finishListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (null != intent && null != intent.getAction()) {
            String action = intent.getAction();
            if (action.equals(Config.BROAD_FINISH_ORDER)) {
                if (null != finishListener) {
                    Long orderId = intent.getLongExtra("orderId", -1);
                    String orderType = intent.getStringExtra("serviceType");
                    finishListener.onFinishOrder(orderId, orderType);
                }
            }
        }
    }

    /**
     * 用于分发广播消息
     */
    public interface OnFinishListener {
        /**
         * 完成订单监听
         * @param orderId
         * @param orderType
         */
        void onFinishOrder(long orderId, String orderType);
    }
}
