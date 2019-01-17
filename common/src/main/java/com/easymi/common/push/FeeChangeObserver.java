package com.easymi.common.push;

/**
 *
 * @author developerLzh
 * @date 2017/12/21 0021
 */

public interface FeeChangeObserver {
    /**
     * 专车费用信息观察者
     * @param orderId
     * @param orderType
     */
    void feeChanged(long orderId, String orderType);
}
