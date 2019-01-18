package com.easymi.taxi.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class Coupon implements Serializable{
    @SerializedName("coupon_type")
    public int couponType;// 1  折扣卷     2  抵扣卷

    public double discount;//折扣卷

    public double deductible;//抵扣卷
}
