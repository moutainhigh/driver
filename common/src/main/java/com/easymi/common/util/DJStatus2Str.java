package com.easymi.common.util;

import com.easymi.component.Config;

/**
 * Created by developerLzh on 2017/11/20 0020.
 */

public class DJStatus2Str {
    /**
     * 新单
     */
    public static final int NEW_ORDER = 1;
    /**
     * 已派单
     */
    public static final int PAIDAN_ORDER = 5;

    /**
     * 已接单
     */
    public static final int TAKE_ORDER = 10;
    /**
     * 前往预约地
     */
    public static final int GOTO_BOOKPALCE_ORDER = 15;
    /**
     * 到达预约地
     */
    public static final int ARRIVAL_BOOKPLACE_ORDER = 20;
    /**
     * 前往目的地
     */
    public static final int GOTO_DESTINATION_ORDER = 25;
    /**
     * 中途等待
     */
    public static final int START_WAIT_ORDER = 28;
    /**
     * 到达目的地
     */
    public static final int ARRIVAL_DESTINATION_ORDER = 30;
    /**
     * 已结算
     */
    public static final int FINISH_ORDER = 35;
    /**
     * 已评价
     */
    public static final int RATED_ORDER = 40;
    /**
     * 已销单
     */
    public static final int CANCEL_ORDER = 45;

    public static String int2Str(String business, int status) {
        if (business.equals(Config.DAIJIA)) {
            switch (status) {
                case NEW_ORDER:
                    return "新单 >";
                case PAIDAN_ORDER:
                    return "已派单 >";
                case TAKE_ORDER:
                    return "已接单 >";
                case GOTO_BOOKPALCE_ORDER:
                    return "前往预约地 >";
                case ARRIVAL_BOOKPLACE_ORDER:
                    return "到达预约地 >";
                case GOTO_DESTINATION_ORDER:
                    return "前往目的地 >";
                case START_WAIT_ORDER:
                    return "等待中 >";
                case ARRIVAL_DESTINATION_ORDER:
                    return "已确认 >";
            }
        }
        return "";
    }
}
