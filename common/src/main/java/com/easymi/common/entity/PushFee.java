package com.easymi.common.entity;

import com.google.gson.annotations.SerializedName;

/**
 * 通过接口推送数据.
 */
public class PushFee {
    @SerializedName("calc")
    public PushFeeLoc calc;

    @SerializedName("employ")
    public PushFeeEmploy employ;
}
