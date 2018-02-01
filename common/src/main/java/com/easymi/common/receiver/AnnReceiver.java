package com.easymi.common.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.easymi.component.Config;

/**
 * Created by developerLzh on 2017/12/19 0019.
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
        void onReceiveAnn(String message);
    }
}
