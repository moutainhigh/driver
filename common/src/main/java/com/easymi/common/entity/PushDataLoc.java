package com.easymi.common.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 * @Author: shine
 * Date: 2018/12/24 下午5:00
 * Description:
 * History:
 */
public class PushDataLoc {

    @SerializedName("app_key")
    public String appKey;


    @SerializedName("position_time")
    public long positionTime;

    /**
     * 定位精度
     */
    public float accuracy;

    /**
     * 速度
     */
    public float speed;

    /**
     * 定位来源
     */
    public int locationType;

    /**
     * 订单数据
     */
    @SerializedName("orderInfo")
    public List<PushDataOrder> orderInfo;


    /**
     * 区域编码
     */
    public String adCode;

    /**
     * 城市编码
     */
    public String cityCode;

    /**
     * 海拔高度
     */
    public Double altitude;

    /**
     * 方向角
     */
    public Float bearing;

    /**
     * 经度
     */
    public Double longitude;

    /**
     * 纬度
     */
    public Double latitude;

    /**
     * 定位提供者
     */
    public String provider;

    /**
     * 时间
     */
    public Long time;

    public boolean isOffline;

    /**
     * 当前中文地址
     */
    public String address;

}
