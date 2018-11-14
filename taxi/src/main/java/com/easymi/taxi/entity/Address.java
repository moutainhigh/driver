package com.easymi.taxi.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by developerLzh on 2017/11/20 0020.
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
