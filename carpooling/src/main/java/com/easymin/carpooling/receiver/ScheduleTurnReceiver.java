package com.easymin.carpooling.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.easymi.component.Config;
import com.easymin.carpooling.R;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: ScheduleTurnReceiver
 * @Author: hufeng
 * @Date: 2019/3/21 下午2:12
 * @Description:
 * @History:
 */
public class ScheduleTurnReceiver  extends BroadcastReceiver {


    private OnTurnListener turnListener;

    public ScheduleTurnReceiver(OnTurnListener turnListener) {
        this.turnListener = turnListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (null != intent && null != intent.getAction()) {
            String action = intent.getAction();
            if (action.equals(Config.SCHEDULE_FINISH)) {
                if (null != turnListener) {
                    Long scheduleId = intent.getLongExtra("scheduleId", -1);
                    String orderType = intent.getStringExtra("orderType");

                    turnListener.onTurnOrder(scheduleId, orderType,"");
                }
            }
        }
    }

    public interface OnTurnListener {
        /**
         * 取消订单和收回监听
         * @param scheduleId
         * @param orderType
         * @param msg
         */
        void onTurnOrder(long scheduleId, String orderType, String msg);
    }
}
