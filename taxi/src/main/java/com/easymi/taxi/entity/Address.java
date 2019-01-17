package com.easymi.taxi.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: FinishActivity
 * Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class Address implements Serializable{
    public long id;
    public String addr;
    public double lat;
    public double lng;

    @SerializedName("addr_type")
    public double addrType;//地址类型 1代表起点 2代表途经点 3代表终点

    public String poi;
    public int seq;//地址排序 从小到大
}
