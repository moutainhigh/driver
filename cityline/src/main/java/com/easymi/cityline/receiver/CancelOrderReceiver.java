package com.easymi.cityline.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.easymi.cityline.R;
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

    public interface OnCancelListener {
        void onCancelOrder(long orderId, String orderType, String msg);
    }
}
