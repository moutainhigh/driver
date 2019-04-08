package com.easymin.custombus.entity;

import java.io.Serializable;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: Station
 * @Author: hufeng
 * @Date: 2019/2/18 下午2:26
 * @Description: 站点信息
 * @History:
 */
public class Station implements Serializable{

    /**
     * 站点ID
     */
    public long stationId;

    /**
     * 站点名称
     */
    public String name;

    /**
     * 站点地址
     */
    public String address;

    /**
     * 经度
     */
    public double longitude;

    /**
     * 纬度
     */
    public double latitude;

    /**
     * 验票数(真实乘车的人数)
     */
    public long checkNumber;
    /**
     * 未验票数(真实乘车的人数)
     */
    public long unCheckNumber;

    /**
     * 站点状态 1 未前往 2 前往中 3 已到达 4 已离开
     */
    public int status;




}
