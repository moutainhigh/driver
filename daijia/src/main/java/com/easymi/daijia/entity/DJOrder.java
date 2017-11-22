package com.easymi.daijia.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by liuzihao on 2017/11/15.
 */

public class DJOrder implements Serializable{

    /**
     * 新单
     */
    public static final int NEW_ORDER = 1;
    /**
     * 已派单
     */
    public static final int PAIDAN_ORDER = 5;

    /**
     * 已接单
     */
    public static final int TAKE_ORDER = 10;
    /**
     * 前往预约地
     */
    public static final int GOTO_BOOKPALCE_ORDER = 15;
    /**
     * 到达预约地
     */
    public static final int ARRIVAL_BOOKPLACE_ORDER = 20;
    /**
     * 前往目的地
     */
    public static final int GOTO_DESTINATION_ORDER = 25;
    /**
     * 中途等待
     */
    public static final int START_WAIT_ORDER = 28;
    /**
     * 到达目的地
     */
    public static final int ARRIVAL_DESTINATION_ORDER = 30;
    /**
     * 已结算
     */
    public static final int FINISH_ORDER = 35;
    /**
     * 已评价
     */
    public static final int RATED_ORDER = 40;
    /**
     * 已销单
     */
    public static final int CANCEL_ORDER = 45;
    /**
     * 是否是预约单
     */
    public int isBookOrder;//1是预约单 2是即时单

    @SerializedName("id")
    public long orderId;

    public String orderDetailType;

    @SerializedName("order_no")
    public String orderNumber;

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

    public double orderDistance;

    public int driveTime;//行驶时间

    public int waitTime;//等待时间

    public double waitFee;//

    public List<Address> addresses;

}
