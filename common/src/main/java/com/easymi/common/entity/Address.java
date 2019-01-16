package com.easymi.common.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 *
 * @author developerLzh
 * @date 2017/11/20 0020
 */

public class Address implements Serializable{
    public long id;

 //hf

    public String address;
    public double latitude;
    public double longitude;

    /**
     * 地址类型 1代表起点 2代表途经点 3代表终点
     */
    public double type;

    public String poi;
    /**
     * 地址排序 从小到大
     */
    public int sort;
    /**
     * 订单id
     */
    public long orderId;
    public long created;
}
