package com.easymi.zhuanche.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: FinishActivity
 *@Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class Address implements Serializable{

    public long id;
    @SerializedName("address")
    public String addr;
    @SerializedName("latitude")
    public double lat;
    @SerializedName("longitude")
    public double lng;

    @SerializedName("type")
    public double addrType;//地址类型 1代表起点 2代表途经点 3代表终点

    public String poi;

    @SerializedName("sort")
    public int seq;//地址排序 从小到大

    public long orderId;

    public long created;
}
