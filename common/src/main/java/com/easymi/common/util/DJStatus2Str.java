package com.easymi.common.util;

import com.easymi.component.Config;
import com.easymi.component.DJOrderStatus;

/**
 * Created by developerLzh on 2017/11/20 0020.
 */

public class DJStatus2Str {

    public static String int2Str(String business, int status) {
        if (business.equals(Config.DAIJIA)) {
            switch (status) {
                case DJOrderStatus.NEW_ORDER:
                    return "新单 >";
                case DJOrderStatus.PAIDAN_ORDER:
                    return "已派单 >";
                case DJOrderStatus.TAKE_ORDER:
                    return "已接单 >";
                case DJOrderStatus.GOTO_BOOKPALCE_ORDER:
                    return "前往预约地 >";
                case DJOrderStatus.ARRIVAL_BOOKPLACE_ORDER:
                    return "到达预约地 >";
                case DJOrderStatus.GOTO_DESTINATION_ORDER:
                    return "前往目的地 >";
                case DJOrderStatus.START_WAIT_ORDER:
                    return "等待中 >";
                case DJOrderStatus.ARRIVAL_DESTINATION_ORDER:
                    return "未支付 >";
                case DJOrderStatus.FINISH_ORDER:
                    return "已完成 >";
                case DJOrderStatus.CANCEL_ORDER:
                    return "已销单 >";
            }
        }
        return "";
    }
}
