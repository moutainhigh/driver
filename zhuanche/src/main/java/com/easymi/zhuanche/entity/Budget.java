package com.easymi.zhuanche.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: FinishActivity
 *@Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
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
