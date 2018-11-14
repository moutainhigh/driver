package com.easymi.taxi.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by liuzihao on 2018/2/12.
 */

public class SameOrder {
    @SerializedName("head_path")
    public String photoUrl;

    @SerializedName("driver_name")
    public String driverName;

    @SerializedName("driver_no")
    public String driverGonghao;

    @SerializedName("is_captain")
    public boolean isCaptain;

    @SerializedName("order_status")
    public int orderStatus;

    @SerializedName("driver_phone")
    public String driverPhone;
}
