package com.easymi.daijia.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by liuzihao on 2018/4/4.
 */

public class Coupon {
    @SerializedName("coupon_type")
    public int couponType;// 1  折扣卷     2  抵扣卷

    public double discount;//折扣卷

    public double deductible;//抵扣卷
}
