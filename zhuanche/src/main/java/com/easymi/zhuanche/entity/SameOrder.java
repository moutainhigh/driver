package com.easymi.zhuanche.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
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
