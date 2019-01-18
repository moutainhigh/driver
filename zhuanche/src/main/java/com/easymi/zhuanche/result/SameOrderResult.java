package com.easymi.zhuanche.result;

import com.easymi.component.result.EmResult;
import com.easymi.zhuanche.entity.SameOrder;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class SameOrderResult extends EmResult {
    @SerializedName("groupDrivers")
    public List<SameOrder> sameOrders;
}
