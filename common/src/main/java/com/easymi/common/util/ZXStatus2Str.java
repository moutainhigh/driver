package com.easymi.common.util;

import com.easymi.common.R;
import com.easymi.component.ZXOrderStatus;
import com.easymi.component.app.XApp;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: ZXStatus2Str
 * @Author: shine
 * Date: 2018/11/22 下午5:03
 * Description:
 * History:
 */
public class ZXStatus2Str {

    /**
     * 根据专线状态获取状态值
     * @param business
     * @param status
     * @return
     */
    public static String int2Str(String business, int status) {
        switch (status) {
            case ZXOrderStatus.NEW_ORDER:
                return XApp.getInstance().getString(R.string.zx_new_order);
            case ZXOrderStatus.PAIDAN_ORDER:
                return XApp.getInstance().getString(R.string.zx_pai_order);
            case ZXOrderStatus.TAKE_ORDER:
                return XApp.getInstance().getString(R.string.zx_accept_order);
            case ZXOrderStatus.WAIT_START:
                return XApp.getInstance().getString(R.string.zx_wait_start);
            case ZXOrderStatus.ACCEPT_PLAN:
                return XApp.getInstance().getString(R.string.zx_accept_plan);
            case ZXOrderStatus.SEND_PLAN:
                return XApp.getInstance().getString(R.string.zx_send_plan);
            case ZXOrderStatus.ACCEPT_ING:
                return XApp.getInstance().getString(R.string.zx_accepting);
            case ZXOrderStatus.SEND_ING:
                return XApp.getInstance().getString(R.string.zx_sending);
            case ZXOrderStatus.SEND_OVER:
                return XApp.getInstance().getString(R.string.zx_send_over);
            case ZXOrderStatus.FINISH_TRIP:
                return XApp.getInstance().getString(R.string.zx_line_over);
            default:
                break;
        }
        return "";
    }
}
