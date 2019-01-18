package com.easymi.zhuanche.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class Coupon implements Serializable{
    /**
     * / 1  折扣卷     2  抵扣卷
     */
    @SerializedName("couponType")
    public int couponType;

    /**
     * 折扣卷
     */
    public double discount;
    /**
     * 抵扣卷
     */
    public double deductible;
    /**
     * 满减额
     */
    @SerializedName("couponTypeVoucherSubtractionMoney")
    public double SubtractionMoney;
    /**
     * 封顶额
     */
    @SerializedName("couponTypeVoucherTopMoney")
    public double TopMoney;

 }
