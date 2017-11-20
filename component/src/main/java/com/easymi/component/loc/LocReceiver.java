package com.easymi.component.loc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by developerLzh on 2017/11/20 0020.
 */

public class LocReceiver extends BroadcastReceiver {

    private ReceiveLocInterface receiveLocInterface;

    public LocReceiver(ReceiveLocInterface receiveLocInterface) {
        this.receiveLocInterface = receiveLocInterface;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (null != receiveLocInterface) {
            receiveLocInterface.receiveLoc();
        }
    }
}
