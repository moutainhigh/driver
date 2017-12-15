package com.easymi.common.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by developerLzh on 2017/12/15 0015.
 */

public class WorkStatistics {

    @SerializedName("finish_count")
    public int finishCount;

    @SerializedName("total_amount")
    public int totalAmount;

    public int income;

    public int minute;//在线时长
}
