package com.easymi.common.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.easymi.component.Config;

/**
 * Created by developerLzh on 2017/12/19 0019.
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
            if (action.equals(Config.BROAD_CANCEL_ORDER)) {
                if (null != cancelListener) {
                    Long orderId = intent.getLongExtra("orderId", -1);
                    String orderType = intent.getStringExtra("orderType");
                    cancelListener.onCancelOrder(orderId, orderType);
                }
            }
        }
    }

    public interface OnCancelListener {
        void onCancelOrder(long orderId, String orderType);
    }
}
