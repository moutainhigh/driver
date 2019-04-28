package com.easymi.common.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 * @Author: shine
 * Date: 2018/12/24 下午5:00
 * Description:
 * History:
 */

public class PushDataOrder {
    @SerializedName("orderId")
    public long orderId;

    @SerializedName("orderType")
    public String orderType;

    /**
     * 作弊增加的里程 数据源来自于本地调价 不会来自后端
     */
    @SerializedName("dark_mileage")
    public int addedKm;

    /**
     * 作弊增加的费用 数据源来自于本地调价 不会来自后端
     */
    @SerializedName("dark_cost")
    public double addedFee;

    /**
     * 订单状态 (出发前1，行驶中2，等待中3)
     */
    public int status;

    @SerializedName("peak_mile")
    public double peakMile;

    @SerializedName("night_time")
    public int nightTime;

    @SerializedName("night_mile")
    public double nightMile;

    @SerializedName("night_time_price")
    public double nightTimePrice;

    /**
     * 订单业务
     */
    public String business;

    /**
     * 用户id
     */
    public long passengerId;

    /**
     * 订单编号
     */
    public String orderNo;

}
