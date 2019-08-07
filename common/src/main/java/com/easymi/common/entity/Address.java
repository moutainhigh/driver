package com.easymi.common.entity;

import java.io.Serializable;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 *
 * @Author: shine
 * Date: 2018/12/24 下午5:00
 * Description:
 * History:
 */
public class Address implements Serializable {
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

    public String startCityCode;
    public String startAdCode;

    public String endCityCode;

    public String endAdCode;

    /**
     * 城市名字
     */
    public String city;
}
