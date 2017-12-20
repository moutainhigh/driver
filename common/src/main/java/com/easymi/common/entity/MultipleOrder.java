package com.easymi.common.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.easymi.common.mvp.grab.GrabActivity;
import com.easymi.component.entity.BaseOrder;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by liuzihao on 2017/11/15.
 * <p>
 * 复合型订单，包含了所有业务订单的字段
 */

public class MultipleOrder extends BaseOrder implements Serializable, MultiItemEntity {

    /**
     * 新单
     */
    public static final int NEW_ORDER = 1;
    /**
     * 已派单
     */
    public static final int PAIDAN_ORDER = 5;

    /**
     * 已接单
     */
    public static final int TAKE_ORDER = 10;
    /**
     * 前往预约地
     */
    public static final int GOTO_BOOKPALCE_ORDER = 15;
    /**
     * 到达预约地
     */
    public static final int ARRIVAL_BOOKPLACE_ORDER = 20;
    /**
     * 前往目的地
     */
    public static final int GOTO_DESTINATION_ORDER = 25;
    /**
     * 中途等待
     */
    public static final int START_WAIT_ORDER = 28;
    /**
     * 到达目的地
     */
    public static final int ARRIVAL_DESTINATION_ORDER = 30;
    /**
     * 已结算
     */
    public static final int FINISH_ORDER = 35;
    /**
     * 已评价
     */
    public static final int RATED_ORDER = 40;
    /**
     * 已销单
     */
    public static final int CANCEL_ORDER = 45;
    /**
     * 是否是预约单
     */
    public int isBookOrder;//1是预约单 2是即时单

    @SerializedName("passenger_id")
    public long passengerId;

    @SerializedName("passenger_name")
    public String passengerName;

    @SerializedName("passenger_phone")
    public String passengerPhone;

    @SerializedName("company_id")
    public long companyId;

    @SerializedName("company_name")
    public String companyName;

    public int countTime = GrabActivity.GRAB_TOTAL_TIME;

    public List<Address> addresses;

    public static final int ITEM_HEADER = 1;
    public static final int ITEM_POSTER = 2;
    public static final int ITEM_DESC = 3;

    public int viewType;

    public MultipleOrder() {
    }

    public MultipleOrder(int type) {
        this.viewType = type;
    }

    @Override
    public int getItemType() {
        return viewType;
    }
}
