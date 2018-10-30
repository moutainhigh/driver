package com.easymi.personal.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by developerLzh on 2017/11/11 0011.
 */

public class Detail {
    public Long time;

    @SerializedName("style")
    public String purpose;

    @SerializedName("cost")
    public double money;
}
