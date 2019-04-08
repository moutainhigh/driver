package com.easymi.common.push;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: FeeChangeObserver
 * @Author: shine
 * Date: 2018/12/24 下午5:00
 * Description: 专车费用信息观察者
 * History:
 */

public interface FeeChangeObserver {
    /**
     * 专车费用信息观察者
     * @param orderId
     * @param orderType
     */
    void feeChanged(long orderId, String orderType);
}
