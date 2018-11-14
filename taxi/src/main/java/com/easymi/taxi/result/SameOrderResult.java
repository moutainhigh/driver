package com.easymi.taxi.result;

import com.easymi.component.result.EmResult;
import com.easymi.taxi.entity.SameOrder;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by liuzihao on 2018/2/12.
 */

public class SameOrderResult extends EmResult {
    @SerializedName("groupDrivers")
    public List<SameOrder> sameOrders;
}
