package com.easymi.common.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: FinishActivity
 * Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
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
