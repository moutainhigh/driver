package com.easymi.cityline.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: ZXOrder
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
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

    /**
     * 开始出发时间
     */
    @SerializedName("time")
    public long startOutTime;

    /**
     * xx分钟前开始接人
     */
    public int minute;

    /**
     * 开始接人时间
     */
    public long startJierenTime;

    @SerializedName("startLatitude")
    public double startLat;

    @SerializedName("startLongitude")
    public double startLng;

    @SerializedName("endLatitude")
    public double endLat;

    @SerializedName("endLongitude")
    public double endLng;

    public int status;

    //线路Id
    public long lineId;
    //线路Id
    public String lineName;

    //剩余票数
    public int seats;

    public ZXOrder() {

    }

    /**
     * 更具班次状态获取状态文字
     * @return
     */
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
