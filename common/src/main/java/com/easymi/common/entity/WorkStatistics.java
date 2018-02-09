package com.easymi.common.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by developerLzh on 2017/12/15 0015.
 */

public class WorkStatistics implements Serializable{

    @SerializedName("finish_count")
    public int finishCount;

    @SerializedName("total_amount")
    public double totalAmount;

    public double income;

    public int minute;//在线时长
}
