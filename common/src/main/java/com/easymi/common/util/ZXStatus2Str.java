package com.easymi.common.util;

import com.easymi.common.R;
import com.easymi.common.entity.OrderCustomer;
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
            case OrderCustomer.CITY_LINE_STATUS_PAY:
                return XApp.getInstance().getString(R.string.zx_no_pay);
            case OrderCustomer.CITY_LINE_STATUS_NEW:
                return XApp.getInstance().getString(R.string.zx_pay);
            case OrderCustomer.CITY_LINE_STATUS_TAKE:
                return XApp.getInstance().getString(R.string.zx_accpet);
            case OrderCustomer.CITY_LINE_STATUS_RUN:
                return XApp.getInstance().getString(R.string.zx_running);
            case OrderCustomer.CITY_LINE_STATUS_SKIP:
                return XApp.getInstance().getString(R.string.zx_skip);
            case OrderCustomer.CITY_LINE_STATUS_FINISH:
                return XApp.getInstance().getString(R.string.zx_arrive);
            case OrderCustomer.CITY_LINE_STATUS_REVIEW:
                return XApp.getInstance().getString(R.string.zx_evaluate);
            case OrderCustomer.CITY_LINE_STATUS_CANCEL:
                return XApp.getInstance().getString(R.string.zx_back);
            default:
                break;
        }
        return "";
    }
}
