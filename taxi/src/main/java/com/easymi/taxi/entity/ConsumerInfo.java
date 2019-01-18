package com.easymi.taxi.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class ConsumerInfo {
    @SerializedName("passenger_name")
    public String consumerName;

    @SerializedName("passenger_type")
    public int consumerType;// 1个人客户  2企业客户

    @SerializedName("order_company")
    public String orderCompany;

    @SerializedName("passenger_company")
    public String consumerCompany;

    @SerializedName("passenger_balance")
    public double consumerBalance;

    @SerializedName("if_sign")
    public boolean canSign;// 0 是不允许 1是允许
}
