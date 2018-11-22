package com.easymi.cityline.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by liuzihao on 2018/11/15.
 */

public class ZXOrder implements Serializable {
    /**
     * 售票中
     */
    public static final int SCHEDULE_STATUS_SALE = 1;
    /**
     * 等待行程开始
     */
    public static final int SCHEDULE_STATUS_PREPARE = 5;
    /**
     * 司机接人
     */
    public static final int SCHEDULE_STATUS_TAKE = 10;
    /**
     * 司机送人
     */
    public static final int SCHEDULE_STATUS_RUN = 15;
    /**
     * 已结束
     */
    public static final int SCHEDULE_STATUS_FINISH = 20;


    @SerializedName("id")
    public long orderId;
    public String orderType;

    @SerializedName("startAddress")
    public String startSite;

    @SerializedName("endAddress")
    public String endSite;

    @SerializedName("time")
    public long startOutTime;//开始出发时间

    public int minute;//xx分钟前开始接人

    public long startJierenTime;//开始接人时间

    @SerializedName("startLatitude")
    public double startLat;

    @SerializedName("startLongitude")
    public double startLng;

    @SerializedName("endLatitude")
    public double endLat;

    @SerializedName("endLongitude")
    public double endLng;

    public int status;

    public long lineId;//线路Id
    public String lineName;//线路Id

    public int seats;//剩余票数

    public ZXOrder() {

    }

    public String getOrderStatusStr() {
        if (status == SCHEDULE_STATUS_SALE) {
            return "售票中";
        } else if (status == SCHEDULE_STATUS_PREPARE) {
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
}
