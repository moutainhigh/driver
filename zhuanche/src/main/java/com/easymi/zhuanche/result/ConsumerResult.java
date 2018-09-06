package com.easymi.zhuanche.result;

import com.easymi.component.result.EmResult;
import com.easymi.zhuanche.entity.ConsumerInfo;
import com.google.gson.annotations.SerializedName;

/**
 * Created by liuzihao on 2018/2/12.
 */

public class ConsumerResult extends EmResult {

    @SerializedName("passengerInfo")
    public ConsumerInfo consumerInfo;
}