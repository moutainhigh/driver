package com.easymi.taxi.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: ConsumerInfo
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class ConsumerInfo {
    @SerializedName("passenger_name")
    public String consumerName;

    /**
     * 1个人客户  2企业客户
     */
    @SerializedName("passenger_type")
    public int consumerType;

    @SerializedName("order_company")
    public String orderCompany;

    @SerializedName("passenger_company")
    public String consumerCompany;

    @SerializedName("passenger_balance")
    public double consumerBalance;

    /**
     * 0 是不允许 1是允许
     */
    @SerializedName("if_sign")
    public boolean canSign;
}
