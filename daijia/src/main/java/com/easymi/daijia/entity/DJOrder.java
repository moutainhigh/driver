package com.easymi.daijia.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by liuzihao on 2017/11/15.
 */

public class DJOrder implements Serializable {
    /**
     * 是否是预约单
     */
    public int isBookOrder;//1是预约单 2是即时单

    @SerializedName("id")
    public long orderId;

    public String orderDetailType;

    @SerializedName("order_no")
    public String orderNumber;

    @SerializedName("business")
    public String orderType;

    @SerializedName("status")
    public int orderStatus;

    @SerializedName("book_time")
    public long bookTime;

    @SerializedName("passenger_id")
    public long passengerId;

    @SerializedName("passenger_name")
    public String passengerName;

    @SerializedName("passenger_phone")
    public String passengerPhone;

    @SerializedName("company_id")
    public long companyId;

    @SerializedName("company_name")
    public String companyName;

    @SerializedName("book_address")
    public String startPlace;

    @SerializedName("destination")
    public String endPlace;

    @SerializedName("budget_fee")
    public double budgetFee;

    public double orderMoney;

    public String carNo;//车牌号

    public List<Address> addresses;

}
