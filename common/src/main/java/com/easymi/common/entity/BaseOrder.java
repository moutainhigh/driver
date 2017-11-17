package com.easymi.common.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.google.gson.annotations.SerializedName;

/**
 * Created by developerLzh on 2017/11/3 0003.
 */

public class BaseOrder implements MultiItemEntity {

    @SerializedName("id")
    public long orderId;

    @SerializedName("orderTypeName")
    public String orderDetailType;

    @SerializedName("business")
    public String orderType; // daijia

    @SerializedName("book_time")
    public long orderTime;

    @SerializedName("status")
    public int orderStatus;

    @SerializedName("book_address")
    public String orderStartPlace;

    @SerializedName("destination")
    public String orderEndPlace;

    @SerializedName("order_no")
    public String orderNumber;

    public int isBookOrder;//1是预约单 2是即时单

    public static final int ITEM_HEADER = 1;
    public static final int ITEM_POSTER = 2;
    public static final int ITEM_DESC = 3;

    public int viewType;

    public BaseOrder() {
    }

    public BaseOrder(int type) {
        this.viewType = type;
    }

    @Override
    public int getItemType() {
        return viewType;
    }
}
