package com.easymi.common.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by developerLzh on 2017/12/21 0021.
 * <p>
 * 上行数据的订单信息
 */

public class PushFeeOrder {

    @SerializedName("orderId")
    public long orderId;

    @SerializedName("orderType")
    public String orderType;

    //作弊增加的里程
    @SerializedName("darkMileage")
    public int addedKm; //数据源来自于本地调价 不会来自后端

    //作弊增加的费用
    @SerializedName("darkCost")
    public double addedFee;//数据源来自于本地调价 不会来自后端


    @SerializedName("status")
    public int status;//订单状态(出发前1，行驶中2，等待中3)

    @SerializedName("peakMile")
    public double peakMile;

    @SerializedName("nightTime")
    public int nightTime;

    @SerializedName("nightMile")
    public double nightMile;

    @SerializedName("nightTimePrice")
    public double nightTimePrice;

}
