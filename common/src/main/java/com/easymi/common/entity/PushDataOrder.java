package com.easymi.common.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by developerLzh on 2017/12/21 0021.
 */

public class PushDataOrder {
    @SerializedName("order_id")
    public long orderId;

    @SerializedName("order_type")
    public String orderType;


    public int status;//订单状态(出发前1，行驶中2，等待中3)
}
