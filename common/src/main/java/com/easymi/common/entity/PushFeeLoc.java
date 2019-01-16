package com.easymi.common.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author hufeng
 */
public class PushFeeLoc {

    @SerializedName("lat")
    public double lat;

    @SerializedName("lng")
    public double lng;

    @SerializedName("appKey")
    public String appKey;

    @SerializedName("positionTime")
    public long positionTime;

    @SerializedName("accuracy")
    public float accuracy;

    /**
     * 速度
     */
    @SerializedName("speed")
    public float speed;

    /**
     * 定位类型
     */
    @SerializedName("locationType")
    public int locationType;

    @SerializedName("orderInfo")
    public List<PushFeeOrder> orderInfo;

}
