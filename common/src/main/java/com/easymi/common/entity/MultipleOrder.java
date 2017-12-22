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
     * 是否是预约单
     */
    public int isBookOrder;//1是预约单 2是即时单

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
