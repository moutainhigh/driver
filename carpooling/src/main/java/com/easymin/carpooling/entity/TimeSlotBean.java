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
     * 1限制票数 0 不限制
     */
    public int isLimitTicket;

    /**
     * 限制的票数 -1不限制 其余为后台设置的限制票数
     */
    public  int limitTicket;

    /**
     * 选座模式或者普通模式
     */
    public int model;

    /**
     * 已经售票数
     */
    public int saleTicket;

}
