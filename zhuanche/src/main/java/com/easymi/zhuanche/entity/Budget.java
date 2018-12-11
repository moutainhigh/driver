package com.easymi.zhuanche.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by developerLzh on 2017/11/27 0027.
 */

public class Budget {

    @SerializedName("budgetFee")
    public double total;
    public double startPrice;
    public double distancePrice;
    public double timePrice;
    public String memo;
    public double couponFee;
}
