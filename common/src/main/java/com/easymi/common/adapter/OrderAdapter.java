package com.easymi.common.adapter;

import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.easymi.common.R;
import com.easymi.common.entity.BaseOrder;
import com.easymi.common.util.DJStatus2Str;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.utils.TimeUtil;

import java.util.List;

/**
 * Created by hiwhitley on 2016/10/17.
 */

public class OrderAdapter extends BaseMultiItemQuickAdapter<BaseOrder> {
    public OrderAdapter(List<BaseOrder> data) {
        super(data);
        addItemType(BaseOrder.ITEM_HEADER, R.layout.order_pinned_layout);
        addItemType(BaseOrder.ITEM_POSTER, R.layout.order_item);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, BaseOrder baseOrder) {
        if (baseOrder.getItemType() == BaseOrder.ITEM_HEADER) {
            if (baseOrder.isBookOrder == 2) {
                baseViewHolder.setText(R.id.pinned_text, "即时订单");
            } else {
                baseViewHolder.setText(R.id.pinned_text, "预约订单");
            }
        } else if (baseOrder.getItemType() == BaseOrder.ITEM_POSTER) {
            baseViewHolder.setText(R.id.order_time, TimeUtil.getTime("MM月dd日 HH:mm", baseOrder.orderTime));
            baseViewHolder.setText(R.id.order_start_place, "" + baseOrder.orderStartPlace);
            baseViewHolder.setText(R.id.order_end_place, "" + baseOrder.orderEndPlace);
            baseViewHolder.setText(R.id.order_status, "" + DJStatus2Str.int2Str(baseOrder.orderType, baseOrder.orderStatus));
            baseViewHolder.setText(R.id.order_type, "" + baseOrder.orderDetailType);
            baseViewHolder.setOnClickListener(R.id.root, v -> {
                if (StringUtils.isNotBlank(baseOrder.orderType)) {

                    if (baseOrder.orderType.equals("daijia")) {
                        if (baseOrder.orderStatus == 1) {
                            ARouter.getInstance().build("/daijia/GrabActivity").withLong("orderId", baseOrder.orderId).navigation();
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
