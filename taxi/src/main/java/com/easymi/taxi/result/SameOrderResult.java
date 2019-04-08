package com.easymi.taxi.result;

import com.easymi.component.result.EmResult;
import com.easymi.taxi.entity.SameOrder;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: SameOrderResult
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description: 同单订单
 * History:
 */

public class SameOrderResult extends EmResult {
    /**
     * 同单订单
     */
    @SerializedName("groupDrivers")
    public List<SameOrder> sameOrders;
}
