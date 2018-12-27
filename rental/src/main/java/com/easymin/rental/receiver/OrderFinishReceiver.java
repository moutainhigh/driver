package com.easymin.rental.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.easymi.component.Config;

/**
 * Created by liuzihao on 2018/3/6.
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
                    String orderType = intent.getStringExtra("orderType");
                    finishListener.onFinishOrder(orderId, orderType);
                }
            }
        }
    }

    public interface OnFinishListener {
        void onFinishOrder(long orderId, String orderType);
    }
}
