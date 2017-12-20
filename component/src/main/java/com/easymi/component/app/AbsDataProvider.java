package com.easymi.component.app;

import com.easymi.component.entity.BaseOrder;

/**
 * Created by developerLzh on 2017/12/20 0020.
 */

public abstract class AbsDataProvider {

    public abstract BaseOrder providBaseOrder(long orderId);
}
