package com.easymin.custombus.entity;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: Station
 * @Author: hufeng
 * @Date: 2019/2/18 下午2:26
 * @Description:
 * @History:
 */
public class Station {

    public long id;
    /**
     * 站点名字
     */
    public String name;
    /**
     * 站点状态 1 未前往 2 前往中 3 已到达 4 已离开
     */
    public int status;
    /**
     * 预估到达时间
     */
    public long bookTime;
    /**
     * 详细地址
     */
    public String address;
    /**
     * 总人数
     */
    public int number;
    /**
     * 已验票人数
     */
    public int checkNumber;

    /**
     * 纬度
     */
    public double lat;
    /**
     * 经度
     */
    public double lng;
}
