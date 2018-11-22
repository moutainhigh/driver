package com.easymi.component.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by developerLzh on 2017/11/3 0003.
 * 所有订单的基本信息
 */
public class BaseOrder implements Serializable {

    public String orderDetailType;

    public int baoxiaoStatus;//1未报销，2申请中，3已报销

    public String passengerTags;

//hf
    /**
     * 订单主键
     */
    public long id;

    /**
     *订单编号
     */
    public String orderNo;

    /**
     * 公司主键
     */
    public Long companyId;

    /**
     * 公司名称
     */
    public String companyName;

    /**
     * 预约时间
     */
    public long bookTime;

    /**
     * 乘客主键
     */
    public Long passengerId;

    /**
     * 乘客姓名
     */
    public String passengerName;

    /**
     * 乘客电话
     */
    public String passengerPhone;

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
     * 服务类型
     */
    public String serviceType;

    /**
     * 预约地
     */
    public String bookAddress;

    /**
     * 目的地
     */
    public String destination;

    /**
     * 预算金额
     */
    public double budgetFee;

    /**
     * 预付费
     */
    public double prepaid;

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
     * 订单备注
     */
    public String orderRemark;

    /**
     * 应付金额
     */
    public double shouldPay;

    /**
     * 是否是预约订单 1是 2否
     */
    public int isBookOrder;

    /**
     * 订单地址信息json字符串
     */
    public String orderAddress;

    /**
     *订单状态
     */
    public int status;

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



}
