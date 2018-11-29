package com.easymi.zhuanche.entity;

import com.easymi.component.entity.BaseOrder;
import com.easymi.component.entity.DymOrder;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by liuzihao on 2017/11/15.
 */

public class ZCOrder implements Serializable {
    /**
     * 是否是预约单
     */
    public int isBookOrder;//1是预约单 2是即时单

    @SerializedName("id")
    public long orderId;

    @SerializedName("orderTypeName")
    public String orderDetailType;

    @SerializedName("orderNo")
    public String orderNumber;

    @SerializedName("serviceType")
    public String orderType;

    @SerializedName("status")
    public int orderStatus;

    @SerializedName("bookTime")
    public long bookTime;

    @SerializedName("passengerId")
    public long passengerId;

    @SerializedName("passengerName")
    public String passengerName;

    @SerializedName("passengerPhone")
    public String passengerPhone;

    @SerializedName("user_phone")
    public String userPhone;

    @SerializedName("companyId")
    public long companyId;

    @SerializedName("companyName")
    public String companyName;

    @SerializedName("bookAddress")
    public String startPlace;

    @SerializedName("destination")
    public String endPlace;

    @SerializedName("budgetFee")
    public double budgetFee;//预算费用 定价时取这字段当钱

    @SerializedName("prepaid")
    public double prepay;//预付金额

    @SerializedName("shouldPay")
    public double orderMoney;//应付金额

    @SerializedName("group_id")
    public String groupId;

    @SerializedName("passenger_tags")
    public String passengerTags;

    public String carNo;//车牌号

    public DymOrder orderFee;

    @SerializedName("salesCouponVo")
    public Coupon coupon;

    @SerializedName("orderRemark")
    public String remark;

//    /**
//     * 订单主键
//     */
//    public long id;

//    /**
//     *订单编号
//     */
//    public String orderNo;

//    /**
//     * 公司主键
//     */
//    public Long companyId;

//    /**
//     * 公司名称
//     */
//    public String companyName;

//    /**
//     * 预约时间
//     */
//    public long bookTime;
//
//    /**
//     * 乘客主键
//     */
//    public Long passengerId;
//
//    /**
//     * 乘客姓名
//     */
//    public String passengerName;
//
//    /**
//     * 乘客电话
//     */
//    public String passengerPhone;

    /**
     * 司机主键
     */
    public long driverId;

    /**
     * 司机姓名
     */
    public String driverName;

    /**
     * 司机电话
     */
    public String driverPhone;

    /**
     * 订单渠道名称
     */
    public String channelAlias;

//    /**
//     * 服务类型
//     */
//    public String serviceType;

//    /**
//     * 预约地
//     */
//    public String bookAddress;

//    /**
//     * 目的地
//     */
//    public String destination;

//    /**
//     * 预算金额
//     */
//    public double budgetFee;

//    /**
//     * 预付费
//     */
//    public double prepaid;

    /**
     * 优惠券主键
     */
    public long couponId;

    /**
     * 预计时间
     */
    public int time;

    /**
     * 预计距离
     */
    public double distance;

//    /**
//     * 订单备注
//     */
//    public String orderRemark;

//    /**
//     * 应付金额
//     */
//    public double shouldPay;

//    /**
//     * 是否是预约订单 1是 2否
//     */
//    public int isBookOrder;

    /**
     * 订单地址信息json字符串
     */
    public String orderAddress;

//    /**
//     *订单状态
//     */
//    public int status;

    /**
     *乐观锁
     */
    public long version;

    /**
     * 下单人
     */
    public String operator;

    /**
     * 派单方式
     */
    public String assignType;

    /**
     * 接单方式
     */
    public String takeType;

    /**
     * 支付人
     */
    public String payer;


    //hf
    /**
     * 专车业务id
     */
    private Long businessId;

    /**
     * 专车车型id
     */
    private Long modelId;

    /**
     * 客服电话
     */
    public String companyPhone;

    /**
     * 客户头像
     */
    public String avatar;


    public List<Address> orderAddressVos;

    public Address getStartSite(){
        Address start = null;
        if (orderAddressVos != null && orderAddressVos.size() != 0){
            for (Address address : orderAddressVos) {
                if (address.addrType == 1) {
                    return address;
                }
            }
        }else {
            start = new Address();
            start.addr = "未知位置";
        }
        return start;
    }


    public Address getEndSite(){
        Address end = null;
        if (orderAddressVos != null && orderAddressVos.size() != 0){
            for (Address address : orderAddressVos) {
                if (address.addrType == 3) {
                    return address;
                }
            }
        }else {
            end = new Address();
            end.addr = "未知位置";
        }
        return end;
    }

}
