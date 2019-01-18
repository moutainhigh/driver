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

public class PushFeeOrder {

    @SerializedName("orderId")
    public long orderId;

    /**
     * 订单类型
     */
    @SerializedName("orderType")
    public String orderType;

    /**
     * 作弊增加的里程 数据源来自于本地调价 不会来自后端
     */
    @SerializedName("darkMileage")
    public int addedKm;

    /**
     * 作弊增加的费用 数据源来自于本地调价 不会来自后端
     */
    @SerializedName("darkCost")
    public double addedFee;

    /**
     * 订单状态(出发前1，行驶中2，等待中3)
     */
    @SerializedName("status")
    public int status;

    @SerializedName("peakMile")
    public double peakMile;

    @SerializedName("nightTime")
    public int nightTime;

    @SerializedName("nightMile")
    public double nightMile;

    @SerializedName("nightTimePrice")
    public double nightTimePrice;

}
