package com.easymi.common.adapter;

import android.content.Context;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.easymi.common.R;
import com.easymi.common.entity.MultipleOrder;
import com.easymi.common.util.DJStatus2Str;
import com.easymi.component.Config;
import com.easymi.component.rxmvp.BaseView;
import com.easymi.component.utils.Log;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.utils.TimeUtil;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by hiwhitley on 2016/10/17.
 */

public class OrderAdapter extends BaseMultiItemQuickAdapter<MultipleOrder, BaseViewHolder> {
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
            baseViewHolder.setText(R.id.order_time, TimeUtil.getTime(context.getString(R.string.time_format), baseOrder.bookTime * 1000));
            baseViewHolder.setText(R.id.order_start_place, "" + baseOrder.getStartSite().address);
            baseViewHolder.setText(R.id.order_end_place, StringUtils.isBlank(baseOrder.destination) ? "" : baseOrder.destination);
            baseViewHolder.setText(R.id.order_status, "" + DJStatus2Str.int2Str(baseOrder.serviceType, baseOrder.status));
            baseViewHolder.setText(R.id.order_type, "" + baseOrder.orderDetailType);
            baseViewHolder.itemView.setOnClickListener(v -> {
                if (StringUtils.isNotBlank(baseOrder.serviceType)) {
                    if (baseOrder.serviceType.equals(Config.DAIJIA)) {
                        if (baseOrder.status == 1) {
//                            ARouter.getInstance().build("/daijia/GrabActivity").withLong("orderId", baseOrder.id).navigation();
                        } else {
                            ARouter.getInstance()
                                    .build("/daijia/FlowActivity")
                                    .withLong("orderId", baseOrder.id).navigation();
                        }
                    } else if (baseOrder.serviceType.equals(Config.ZHUANCHE)) {
//                        if (baseOrder.status == 1) {
////                            ARouter.getInstance().build("/daijia/GrabActivity").withLong("orderId", baseOrder.id).navigation();
//                        } else {
                            ARouter.getInstance()
                                    .build("/zhuanche/FlowActivity")
                                    .withLong("orderId", baseOrder.id).navigation();
//                        }
                    }else if (baseOrder.serviceType.equals(Config.TAXI)){
//                        if (baseOrder.status == 1) {
////                            ARouter.getInstance().build("/daijia/GrabActivity").withLong("orderId", baseOrder.id).navigation();
//                        } else {
                            ARouter.getInstance()
                                    .build("/taxi/FlowActivity")
                                    .withLong("orderId", baseOrder.id).navigation();
//                        }
                    }
                }
            });
        }
    }
}
