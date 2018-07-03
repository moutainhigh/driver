package com.easymi.daijia.entity;

import com.easymi.component.entity.DymOrder;
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

    @SerializedName("orderTypeName")
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

    @SerializedName("user_phone")
    public String userPhone;

    @SerializedName("company_id")
    public long companyId;

    @SerializedName("company_name")
    public String companyName;

    @SerializedName("book_address")
    public String startPlace;

    @SerializedName("destination")
    public String endPlace;

    @SerializedName("budget_fee")
    public double budgetFee;//预算费用 定价时取这字段当钱

    @SerializedName("pre_pay")
    public double prepay;//预付金额

    @SerializedName("order_money")
    public double orderMoney;//应付金额

    @SerializedName("group_id")
    public String groupId;

    @SerializedName("passenger_tags")
    public String passengerTags;

    @SerializedName("channel_name")
    public String orderSource;//订单来源

    //同单司机,队长电话,如果自己是队长则为空
    @SerializedName("captain_phone")
    public String captainPhone;

    public String carNo;//车牌号

    public List<Address> addresses;

    public DymOrder orderFee;

    public Coupon coupon;

}
