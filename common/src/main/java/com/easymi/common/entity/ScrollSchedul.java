package com.easymi.common.entity;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: ScrollSchedul
 * @Author: hufeng
 * @Date: 2019/11/21 下午1:09
 * @Description:
 * @History:
 */
public class ScrollSchedul {

    public long id;

    /**
     * 1-时间模式 2-选座模式 3-无排班模式
     */
    public  int model;

    /**
     * 线路全称
     */
    public String name;

    /**
     * 起点精度
     */
    public double startLongitude;

    /**
     * 起点纬度
     */
    public double startLatitude;

    /**
     * 司机听单距离-整数公里
     */
    public int driverListeningDistance;

    ///////本地字段

    /**
     * 司机距离起点经纬度距离-直线距离
     */
    public double destance;

    /**
     * 是否选中
     */
    public boolean select;

}
