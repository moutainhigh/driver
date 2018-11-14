package com.easymi.taxi.entity;

import com.easymi.common.entity.Address;
import com.easymi.component.entity.BaseOrder;
import com.easymi.component.entity.DymOrder;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by liuzihao on 2017/11/15.
 */

public class TaxiOrder extends BaseOrder implements Serializable {
//    /**
//     * 是否是预约单
//     */
//    public int isBookOrder;//1是预约单 2是即时单
//
//    @SerializedName("id")
//    public long orderId;
//
//    @SerializedName("orderTypeName")
//    public String orderDetailType;
//
//    @SerializedName("order_no")
//    public String orderNumber;
//
//    @SerializedName("business")
//    public String orderType;
//
//    @SerializedName("status")
//    public int orderStatus;
//
//    @SerializedName("book_time")
//    public long bookTime;
//
//    @SerializedName("passenger_id")
//    public long passengerId;
//
//    @SerializedName("passenger_name")
//    public String passengerName;
//
//    @SerializedName("passenger_phone")
//    public String passengerPhone;
//
//    @SerializedName("user_phone")
//    public String userPhone;
//
//    @SerializedName("company_id")
//    public long companyId;
//
//    @SerializedName("company_name")
//    public String companyName;
//
//    @SerializedName("book_address")
//    public String startPlace;
//
//    @SerializedName("destination")
//    public String endPlace;
//
//    @SerializedName("budget_fee")
//    public double budgetFee;//预算费用 定价时取这字段当钱
//
//    @SerializedName("pre_pay")
//    public double prepay;//预付金额
//
//    @SerializedName("order_money")
//    public double orderMoney;//应付金额
//
//    @SerializedName("group_id")
//    public String groupId;
//
//    @SerializedName("passenger_tags")
//    public String passengerTags;
//
//    public String carNo;//车牌号
//
//    public List<Address> addresses;
//
//    public DymOrder orderFee;
//
//    public Coupon coupon;
//
//    @SerializedName("order_remark")
//    public String remark;

    public List<Address> orderAddressVos;


    public Address getStartSite(){
        Address start = null;
        if (orderAddressVos != null && orderAddressVos.size() != 0){
            for (Address address : orderAddressVos) {
                if (address.type == 1) {
                    return address;
                }
            }
        }else {
            start = new Address();
            start.address = "未知位置";
        }
        return start;
    }


    public Address getEndSite(){
        Address end = null;
        if (orderAddressVos != null && orderAddressVos.size() != 0){
            for (Address address : orderAddressVos) {
                if (address.type == 3) {
                    return address;
                }
            }
        }else {
            end = new Address();
            end.address = "未知位置";
        }
        return end;
    }


//    /**
//     * 新订单状态
//     */
//    public static final int STATUS_NEW = 1;
//    /**
//     * 已派单状态
//     */
//    public static final int STATUS_ASSIGN = 5;
//    /**
//     * 已接单状态
//     */
//    public static final int STATUS_TAKE = 10;
//    /**
//     * 前往预约地状态
//     */
//    public static final int STATUS_GO_TO_BOOK_PLACE = 15;
//    /**
//     * 到达预约地状态
//     */
//    public static final int STATUS_ARRIVAL_BOOK_PLACE = 20;
//    /**
//     * 前往目的地状态
//     */
//    public static final int STATUS_GO_TO_DESTINATION = 25;
//    /**
//     * 中途等待状态
//     */
//    public static final int STATUS_WAIT = 28;
//    /**
//     * 到达目的地状态
//     */
//    public static final int STATUS_ARRIVAL_DESTINATION = 30;
//    /**
//     * 已结算状态
//     */
//    public static final int STATUS_FINISH = 35;
//    /**
//     * 已评价状态
//     */
//    public static final int STATUS_RATED = 40;
//    /**
//     * 已销单状态
//     */
//    public static final int STATUS_CANCEL = 45;
}
