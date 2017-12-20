package com.easymi.common.adapter;

import android.content.Context;

import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.easymi.common.R;
import com.easymi.common.entity.MultipleOrder;
import com.easymi.common.util.DJStatus2Str;
import com.easymi.component.Config;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.utils.TimeUtil;

import java.util.List;

/**
 * Created by hiwhitley on 2016/10/17.
 */

public class OrderAdapter extends BaseMultiItemQuickAdapter<MultipleOrder> {
    private Context context;

    public OrderAdapter(List<MultipleOrder> data, Context context) {
        super(data);
        this.context = context;
        addItemType(MultipleOrder.ITEM_HEADER, R.layout.order_pinned_layout);
        addItemType(MultipleOrder.ITEM_POSTER, R.layout.order_item);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, MultipleOrder baseOrder) {
        if (baseOrder.getItemType() == MultipleOrder.ITEM_HEADER) {
            if (baseOrder.isBookOrder == 2) {
                baseViewHolder.setText(R.id.pinned_text, context.getString(R.string.jishi_order));
            } else {
                baseViewHolder.setText(R.id.pinned_text, context.getString(R.string.yuyue_order));
            }
        } else if (baseOrder.getItemType() == MultipleOrder.ITEM_POSTER) {
            baseViewHolder.setText(R.id.order_time, TimeUtil.getTime(context.getString(R.string.time_format), baseOrder.orderTime));
            baseViewHolder.setText(R.id.order_start_place, "" + baseOrder.startPlace);
            baseViewHolder.setText(R.id.order_end_place, "" + baseOrder.endPlace);
            baseViewHolder.setText(R.id.order_status, "" + DJStatus2Str.int2Str(baseOrder.orderType, baseOrder.orderStatus));
            baseViewHolder.setText(R.id.order_type, "" + baseOrder.orderDetailType);
            baseViewHolder.setOnClickListener(R.id.root, v -> {
                if (StringUtils.isNotBlank(baseOrder.orderType)) {

                    if (baseOrder.orderType.equals(Config.DAIJIA)) {
                        if (baseOrder.orderStatus == 1) {
//                            ARouter.getInstance().build("/daijia/GrabActivity").withLong("orderId", baseOrder.orderId).navigation();
                        } else {
                            ARouter.getInstance()
                                    .build("/daijia/FlowActivity")
                                    .withLong("orderId", baseOrder.orderId).navigation();
                        }
                    }
                }
            });
        }
    }
}
