package com.easymi.common.entity;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 * @Author: shine
 * Date: 2018/12/24 下午5:00
 * Description:
 * History:
 */
public class NearDriver {
    public long id;
    public String name;
    public String phone;
    public double distance;
    public double latitude;
    public double longitude;
    public String status;
    public float bearing;//方位角

    @Override
    public String toString() {
        return "NearDriver{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", distance=" + distance +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", status='" + status + '\'' +
                ", bearing=" + bearing +
                '}';
    }
}
