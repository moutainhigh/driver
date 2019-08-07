package com.easymin.custombus.entity;

import java.io.Serializable;

public class DZBusLine implements Serializable {


    /**
     * 新班次
     */
    public static final int SCHEDULE_STATUS_NEW = 1;
    /**
     * 班次已出发
     */
    public static final int SCHEDULE_STATUS_RUNNING = 5;
    /**
     * 班次已结束
     */
    public static final int SCHEDULE_STATUS_FINISH = 10;

    /**
     * 班次站点前往中
     */
    public static final int SCHEDULE_STATION_RUNNING = 1;

    /**
     * 班次站点已经到达
     */
    public static final int SCHEDULE_STATION_ARRIVED = 2;


    public long id;
    public long lineId;
    public String lineName;
    public String startStation;
    public String endStation;
    public long time;
    public int seats;
    public int saleSeat;
    public int status;
    public int restrict;
    public int throughTicket;
    public double throughMoney;

}
