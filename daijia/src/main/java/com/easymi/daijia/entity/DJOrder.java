package com.easymi.daijia.entity;

import java.io.Serializable;

/**
 * Created by liuzihao on 2017/11/15.
 */

public class DJOrder implements Serializable{

    public String orderType;

    public String orderTime;

    public String orderStatus;

    public String orderStartPlace;

    public String orderEndPlace;

    public double orderPrice;

    public String orderNumber;

    public String passengerPhone;

    public double orderMoney;

    public double orderDistance;

    public int driveTime;//行驶时间

    public int waitTime;//等待时间

    public double waitFee;//

    /**
     * 本地订单状态约定：
     *   0：新单
     *   1：已结单
     *   2：前往预约地中
     *   3：到达预约地
     *   4：出发前等待
     *   5：前往目的地中
     *   6：中途等待
     *   7：已确认费用
     *
     */
    public int subStatus;
}
