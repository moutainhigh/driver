package com.easymi.common.util;

import com.easymi.common.R;
import com.easymi.component.Config;
import com.easymi.component.DJOrderStatus;
import com.easymi.component.app.XApp;

/**
 * Created by developerLzh on 2017/11/20 0020.
 */

public class DJStatus2Str {

    public static String int2Str(String business, int status) {
        switch (status) {
            case DJOrderStatus.NEW_ORDER:
                return XApp.getInstance().getString(R.string.new_order);
            case DJOrderStatus.PAIDAN_ORDER:
                return XApp.getInstance().getString(R.string.pai_order);
            case DJOrderStatus.TAKE_ORDER:
                return XApp.getInstance().getString(R.string.jie_order);
            case DJOrderStatus.GOTO_BOOKPALCE_ORDER:
                return XApp.getInstance().getString(R.string.to_start_order);
            case DJOrderStatus.ARRIVAL_BOOKPLACE_ORDER:
                return XApp.getInstance().getString(R.string.arrive_start_order);
            case DJOrderStatus.GOTO_DESTINATION_ORDER:
                return XApp.getInstance().getString(R.string.to_end_order);
            case DJOrderStatus.START_WAIT_ORDER:
                return XApp.getInstance().getString(R.string.wait_order);
            case DJOrderStatus.ARRIVAL_DESTINATION_ORDER:
                return XApp.getInstance().getString(R.string.not_pay_order);
            case DJOrderStatus.FINISH_ORDER:
                return XApp.getInstance().getString(R.string.finish_order);
            case DJOrderStatus.CANCEL_ORDER:
                return XApp.getInstance().getString(R.string.des_order);
            case DJOrderStatus.RATED_ORDER:
                return XApp.getInstance().getString(R.string.rate_order);
        }
//        if (business.equals(Config.DAIJIA)) {
//        }
        return "";
    }
}
