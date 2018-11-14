package com.easymi.taxi.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by liuzihao on 2018/4/4.
 */

public class Coupon implements Serializable{
    @SerializedName("coupon_type")
    public int couponType;// 1  折扣卷     2  抵扣卷

    public double discount;//折扣卷

    public double deductible;//抵扣卷
}
