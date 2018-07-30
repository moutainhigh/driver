package com.easymi.common.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by developerLzh on 2017/12/21 0021.
 *
 * 上行数据的订单信息
 */

public class PushDataOrder {
    @SerializedName("order_id")
    public long orderId;

    @SerializedName("order_type")
    public String orderType;

    //作弊增加的里程
    @SerializedName("dark_mileage")
    public int addedKm; //数据源来自于本地调价 不会来自后端

    //作弊增加的费用
    @SerializedName("dark_cost")
    public double addedFee;//数据源来自于本地调价 不会来自后端


    public int status;//订单状态(出发前1，行驶中2，等待中3)

    @SerializedName("peak_mile")
    public double peakMile;

    @SerializedName("night_time")
    public int nightTime;

    @SerializedName("night_mile")
    public double nightMile;

    @SerializedName("night_time_price")
    public double nightTimePrice;




}
