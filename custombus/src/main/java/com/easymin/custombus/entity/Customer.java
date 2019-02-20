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

    public long id;
    /**
     * 客户名字
     */
    public String name;
    /**
     * 乘客状态 1 未验票 2 已验票 3 已跳过
     */
    public int status;
    /**
     * 几个人
     */
    public int tickets;
    /**
     * 电话号码
     */
    public String phone;
    /**
     * t头像
     */
    public String pic;
    /**
     * 起点
     */
    public String startAddr;
    /**
     * 终点
     */
    public String endAddr;
    /**
     * 备注
     */
    public String remark;

}
