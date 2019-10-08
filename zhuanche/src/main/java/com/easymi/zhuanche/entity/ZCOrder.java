package com.easymi.zhuanche.entity;

import com.easymi.component.app.XApp;
import com.easymi.component.entity.BaseOrder;
import com.easymi.component.entity.DymOrder;
import com.easymi.zhuanche.R;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: ZCOrder
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description: 专车订单哪
 * History:
 */

public class ZCOrder implements Serializable {


    /**
     * 是否是预约单 1是预约单 2是即时单
     */
    public int isBookOrder;

    @SerializedName("id")
    public long orderId;

    @SerializedName("orderTypeName")
    public String orderDetailType;

    public String orderNo;

    /**
     * 专车类型
     */
    public String serviceType;

    /**
     * 专车订单子业务类型（出租车）
     */
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

    /**
     * 预算费用 定价时取这字段当钱
     */
    @SerializedName("budgetFee")
    public double budgetFee;

    /**
     * 预付金额
     */
    @SerializedName("prepaid")
    public double prepay;

    /**
     * 应付金额
     */
    @SerializedName("shouldPay")
    public double orderMoney;

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

    /**
     * 订单地址信息json字符串
     */
    public String orderAddress;

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

    /**
     * 是否是大额支付
     */
    public boolean prepayment;

    /**
     * 是否已经支付
     */
    public boolean paid;

    /**
     * 订单一口价
     */
    public boolean onePrice;

    public List<Address> orderAddressVos;
    /**
     * 获取订单预约起点
     * @return
     */
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
            start.addr = XApp.getInstance().getResources().getString(R.string.unknown_site);
        }
        return start;
    }

    /**
     * 获取订单预约终点
     * @return
     */
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
            end.addr = XApp.getInstance().getResources().getString(R.string.unknown_site);
        }
        return end;
    }

}
