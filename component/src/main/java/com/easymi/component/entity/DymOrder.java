package com.easymi.component.entity;

/**
 * Created by liuzihao on 2017/12/20.
 * <p>
 * 本地保存的费用信息字段 保存到数据库的
 */

public class DymOrder {

    public long orderId;

    public String orderType;

    //起步费
    public double startFee;

    //等候时间 分
    public int waitTime;

    //等候费
    public double waitTimeFee;

    //行驶时间 分
    public int travelTime;

    //行驶费
    public double travelFee;

    //订单状态
    public int orderStatus;
}
