package com.easymi.common.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by liuzihao on 2018/3/6.
 */

public class Setting {
    @SerializedName("is_paid")
    public int isPaid;//代付（1开启，2关闭)

    @SerializedName("is_expenses")
    public int isExpenses;//报销（1开启，2关闭）
}
