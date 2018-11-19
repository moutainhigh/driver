package com.easymi.common.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by developerLzh on 2017/12/21 0021.
 *
 * 上行数据的位置信息和订单列表
 */

public class PushDataLoc {
//    public double lat;
//    public double lng;

    @SerializedName("app_key")
    public String appKey;

//    @SerializedName("dark_cost")
//    public double darkCost;
//
//    @SerializedName("dark_mileage")
//    public int darkMileage;

    @SerializedName("position_time")
    public long positionTime;

    /**
     * 定位精度
     */
    public float accuracy;

    /**
     * 速度
     */
    public float speed;//速度

    /**
     * 定位来源
     */
//    @SerializedName("location_type")
    public int locationType;//定位类型

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

}
