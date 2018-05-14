package com.easymi.daijia.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by liuzihao on 2018/2/12.
 */

public class ConsumerInfo {
    @SerializedName("passenger_name")
    public String consumerName;

    @SerializedName("passenger_type")
    public int consumerType;// 1个人客户  2企业客户

    public String consumerGrade;//客户等级

    @SerializedName("order_company")
    public String orderCompany;

    @SerializedName("passenger_company")
    public String consumerCompany;

    @SerializedName("passenger_balance")
    public double consumerBalance;

    @SerializedName("if_sign")
    public boolean canSign;// 0 是不允许 1是允许
}
