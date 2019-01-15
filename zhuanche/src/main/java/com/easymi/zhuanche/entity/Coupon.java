package com.easymi.zhuanche.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by liuzihao on 2018/4/4.
 */

public class Coupon implements Serializable{
    @SerializedName("couponType")
    public int couponType;// 1  折扣卷     2  抵扣卷

    public double discount;//折扣卷

    public double deductible;//抵扣卷
    //满减额
    @SerializedName("couponTypeVoucherSubtractionMoney")
    public double SubtractionMoney;
    //封顶额
    @SerializedName("couponTypeVoucherTopMoney")
    public double TopMoney;

 }
