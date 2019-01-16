package com.easymi.common.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by developerLzh on 2017/12/15 0015.
 */

public class WorkStatistics implements Serializable{

    /**
     * 完成单量
     */
    @SerializedName("orderCount")
    public int finishCount;

    @SerializedName("total_amount")
    public double totalAmount;

    /**
     * 今日收益
     */
    @SerializedName("orderProfit")
    public double income;

    /**
     * 在线时长
     */
    public int minute;
}
