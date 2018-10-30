package com.easymi.common.push;

/**
 * Created by developerLzh on 2017/12/21 0021.
 */

public interface FeeChangeObserver {
    void feeChanged(long orderId, String orderType);
}
