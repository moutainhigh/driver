package com.easymi.common.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by developerLzh on 2017/12/21 0021.
 */

public class PushDataLoc {
    public double lat;
    public double lng;

    @SerializedName("app_key")
    public String appKey;

    @SerializedName("dark_cost")
    public double darkCost;

    @SerializedName("dark_mileage")
    public int darkMileage;

    @SerializedName("position_time")
    public long positionTime;
    public float accuracy;

    public float speed;//速度

    @SerializedName("order_info")
    public List<PushDataOrder> orderInfo;
}
