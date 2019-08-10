package com.easymin.custombus.entity;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: Customer
 * @Author: hufeng
 * @Date: 2019/2/18 下午8:37
 * @Description: 乘客订单信息
 * @History:
 */
public class Customer {

    /**
     * 订单id
     */
    public long id;
    /**
     * 客户名字
     */
    public String passengerName;
    /**
     * 订单状态
     */
    public int status;
    /**
     * 几个人
     */
    public int ticketNumber;
    /**
     * 电话号码
     */
    public String passengerPhone;
    /**
     * 头像
     */
    public String avatar;
    /**
     * 起点
     */
    public String startStationName;
    /**
     * 终点
     */
    public String endStationName;
    /**
     * 备注
     */
    public String orderRemark;

    public double money;

    /**
     * 客运班车订单未支付
     */
    public static final int CITY_COUNTRY_STATUS_PAY = 1;
    /**
     * 客运班车订单未开始
     */
    public static final int CITY_COUNTRY_STATUS_NEW = 5;
    /**
     * 等待上车(车辆未到达站点)
     */
    public static final int CITY_COUNTRY_STATUS_COMING = 6;
    /**
     * 验票中(车辆已经到达站点)
     */
    public static final int CITY_COUNTRY_STATUS_ARRIVED = 8;
    /**
     * 客运班车订单行程中
     */
    public static final int CITY_COUNTRY_STATUS_RUNNING = 10;
    /**
     * 客运班车订单已到达
     */
    public static final int CITY_COUNTRY_STATUS_FINISH = 15;
    /**
     * 客运班车订单已失效(跳过)
     */
    public static final int CITY_COUNTRY_STATUS_INVALID = 20;
    /**
     * 客运班车订单已评价
     */
    public static final int CITY_COUNTRY_STATUS_REVIEW = 25;
    /**
     * 客运班车订单已消单
     */
    public static final int CITY_COUNTRY_STATUS_CANCEL = 30;

}
