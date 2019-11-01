package com.easymin.carpooling.entity;

import java.io.Serializable;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: TimeSlotBean
 * @Author: hufeng
 * @Date: 2019/10/31 下午2:16
 * @Description:
 * @History:
 */
public class TimeSlotBean implements Serializable {

    /**
     * 余票 null代表不限票， 数字就是余票数
     */
    public Integer tickets;

    /**
     * 班次id
     */
    public long id;

    /**
     * 线路id
     */
    public long lineId;

    /**
     * 线路名称
     */
    public  String lineName;

    /**
     * 班次排班时间
     */
    public String day;

    /**
     * 排班时间段
     */
    public String timeSlot;

    /**
     * ?????
     */
    public int isLimitTicket;

    /**
     * 是否限制余票 -1不限制
     */
    public  int limitTicket;

    /**
     * ?????
     */
    public int model;

    /**
     * 已经售票数
     */
    public int saleTicket;

}
