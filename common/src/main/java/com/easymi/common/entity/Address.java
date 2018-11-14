package com.easymi.common.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by developerLzh on 2017/11/20 0020.
 */

public class Address implements Serializable{
    public long id;
//    public String addr;
//    public double lat;
//    public double lng;
//
//    @SerializedName("addr_type")
//    public double addrType;//地址类型 1代表起点 2代表途经点 3代表终点
//
//    public String poi;
//    public int seq;//地址排序 从小到大

    //hf
    public String address;
    public double latitude;
    public double longitude;

    public double type;//地址类型 1代表起点 2代表途经点 3代表终点

    public String poi;
    public int sort;//地址排序 从小到大
    public long orderId;//订单id
    public long created;
}
