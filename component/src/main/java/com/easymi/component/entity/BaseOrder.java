package com.easymi.component.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by developerLzh on 2017/11/3 0003.
 * 所有订单的基本信息
 */
public class BaseOrder implements Serializable {

    @SerializedName("id")
    public long orderId;

    @SerializedName("orderTypeName")
    public String orderDetailType;

    @SerializedName("business")
    public String orderType; // daijia

    @SerializedName("book_time")
    public long orderTime;//预约时间

    @SerializedName("status")
    public int orderStatus;

    @SerializedName("book_address")
    public String startPlace;

    @SerializedName("destination")
    public String endPlace;

    @SerializedName("order_no")
    public String orderNumber;

    @SerializedName("passenger_id")
    public long passengerId;

    @SerializedName("passenger_name")
    public String passengerName;

    @SerializedName("passenger_phone")
    public String passengerPhone;

    @SerializedName("total_fee")
    public double orderMoney;

    @SerializedName("wipe_out_type")
    public int baoxiaoStatus;//1未报销，2申请中，3已报销

    @SerializedName("passenger_tags")
    public String passengerTags;

}
