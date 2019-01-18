package com.easymi.taxi.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: Coupon
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class Coupon implements Serializable{
    /**
     *  1  折扣卷     2  抵扣卷
     */
    @SerializedName("coupon_type")
    public int couponType;

    /**
     * //折扣卷
     */
    public double discount;

    /**
     * //抵扣卷
     */
    public double deductible;
}
