package com.easymin.carpooling.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: PincheOrder
 * @Author: hufeng
 * @Date: 2019/2/21 下午4:42
 * @Description:  拼车班车信息
 * @History:
 */
public class PincheOrder implements Serializable {

    /**
     * 等待行程开始
     */
    public static final int SCHEDULE_STATUS_NEW = 1;
    /**
     * 司机接人
     */
    public static final int SCHEDULE_STATUS_TAKE = 5;
    /**
     * 司机送人
     */
    public static final int SCHEDULE_STATUS_RUN = 10;
    /**
     * 已结束
     */
    public static final int SCHEDULE_STATUS_FINISH = 15;

    /**
     * 根据班次状态获取状态文字
     * @return
     */
    public String getOrderStatusStr() {
        if (status == SCHEDULE_STATUS_NEW) {
            return "未开始";
        } else if (status == SCHEDULE_STATUS_TAKE) {
            return "正在接人";
        } else if (status == SCHEDULE_STATUS_RUN) {
            return "正在送人";
        } else if (status == SCHEDULE_STATUS_FINISH) {
            return "已完成";
        } else {
            return "";
        }
    }

    /**
     * 班次ID
     */
    public long id;

    /**
     * 订单id
     */
    public long orderId;

    /**
     * 订单类型
     */
    public String orderType;

    /**
     * 起点地址
     */
    public String startAddress;

    /**
     * 终点地址
     */
    public String endAddress;

    /**
     * 开始出发时间
     */
    public long bookTime;

    /**
     * xx分钟前开始接人
     */
    public int minute;

    /**
     * 开始接人时间
     */
    public long startJierenTime;

    /**
     * 起点纬度
     */
    public double startLatitude;

    /**
     * 起点精度
     */
    public double startLongitude;

    /**
     * 终点纬度
     */
    public double endLatitude;

    /**
     * 终点精度
     */
    public double endLongitude;

    /**
     * 线路id
     */
    public long lineId;

    /**
     * 线路名称
     */
    public String lineName;

    /**
     * 班次id
     */
    public long scheduleId;

    /**
     * 时间段ID
     */
    public long timeSlotId;

    /**
     * 司机ID
     */
    public long driverId;
    /**
     * 司机姓名
     */
    public String driverName;
    /**
     * 司机电话
     */
    public String driverPhone;

    /**
     * 车辆ID
     */
    public long vehicleId;

    /**
     * 车牌号
     */
    public String vehicleNo;

    /**
     * 座位数量(余票数量)
     */
    public int seats;

    /**
     * 已售票数
     */
    public int saleSeat;

    /**
     * 总金额
     */
    public double totalMoney;

    /**
     * 班次状态
     */
    public int status;

//补单添加字端
    /**
     * 班次开始时间
     */
    public long time;

    /**
     * 起点站名字
     */
    public String startStation;

    /**
     * 终点站名字
     */
    public String endStation;

    /**
     * 班次服务时间段
     */
    public String timeSlot;

    /**
     * 本班次票价
     */
    public double money;



    public PincheOrder() {

    }
}
