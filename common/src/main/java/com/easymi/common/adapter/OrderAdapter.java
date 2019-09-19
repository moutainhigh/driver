package com.easymi.common.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.easymi.common.R;
import com.easymi.common.entity.MultipleOrder;
import com.easymi.common.mvp.order.OrderActivity;
import com.easymi.common.util.DJStatus2Str;
import com.easymi.component.BusOrderStatus;
import com.easymi.component.Config;
import com.easymi.component.GWOrderStatus;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.utils.TimeUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 *
 * @Author: hufeng
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
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
            baseViewHolder.setText(R.id.pinned_text, "订单信息");
            baseViewHolder.itemView.setOnClickListener(v -> {
                context.startActivity(new Intent(context, OrderActivity.class));
            });
        } else if (baseOrder.getItemType() == MultipleOrder.ITEM_POSTER) {


            baseViewHolder.setGone(R.id.order_ll_info, false);
            baseViewHolder.setGone(R.id.order_ll_count, false);

            baseViewHolder.setText(R.id.order_time, TimeUtil.getTime(context.getString(R.string.time_format), baseOrder.bookTime * 1000));
            baseViewHolder.setText(R.id.order_type, "" + baseOrder.getOrderType());
            baseViewHolder.setText(R.id.order_start_place, "" + baseOrder.bookAddress);
            baseViewHolder.setText(R.id.order_end_place, baseOrder.destination);

            if (TextUtils.equals(baseOrder.serviceType, Config.CITY_LINE)) {
                //专线
                baseViewHolder.setText(R.id.order_status, "" + baseOrder.getZXOrderStatusStr() + " >");
            } else if (TextUtils.equals(baseOrder.serviceType, Config.CARPOOL)) {
                //城际拼车
                baseViewHolder.setText(R.id.order_status, "" + baseOrder.getPCOrderStatusStr() + " >");
                baseViewHolder.setGone(R.id.order_ll_info, true)
                        .setGone(R.id.order_ll_count, true)
                        .setGone(R.id.order_tv_new, baseOrder.haveNewPassenger == 1)
                        .setText(R.id.order_tv_order_count, "订单: " + baseOrder.orderNum)
                        .setText(R.id.order_tv_passenger_count, "乘客: " + baseOrder.ticketNum)
                        .setText(R.id.order_tv_rest_count, "余座: " + baseOrder.seats)
                        .setVisible(R.id.order_tv_unpay_count, baseOrder.noPay > 0)
                        .setText(R.id.order_tv_unpay_count, "未支付订单: " + baseOrder.noPay)
                        .setOnClickListener(R.id.order_ll_count, createOnClickListener(baseOrder))
                        .setOnClickListener(R.id.order_ll_info, createOnClickListener(baseOrder));
            } else if (TextUtils.equals(baseOrder.serviceType, Config.COUNTRY)) {
                baseViewHolder.setText(R.id.order_status, "" + BusOrderStatus.status2Str(baseOrder.scheduleStatus) + " >");
            } else if (TextUtils.equals(baseOrder.serviceType, Config.GOV)) {
                baseViewHolder.setText(R.id.order_status, "" + GWOrderStatus.status2Str(baseOrder.status) + " >");
            } else {
                baseViewHolder.setText(R.id.order_status, "" + DJStatus2Str.int2Str(baseOrder.serviceType, baseOrder.status) + " >");
            }

            if (TextUtils.equals(baseOrder.serviceType, Config.CARPOOL) && baseOrder.orderChange == 1) {
                baseViewHolder.getView(R.id.tv_turn).setVisibility(View.VISIBLE);
            } else {
                baseViewHolder.getView(R.id.tv_turn).setVisibility(View.GONE);
            }

            baseViewHolder.itemView.setOnClickListener(v -> {
                if (StringUtils.isNotBlank(baseOrder.serviceType)) {
                    if (baseOrder.serviceType.equals(Config.ZHUANCHE)) {
                        ARouter.getInstance()
                                .build("/zhuanche/FlowActivity")
                                .withLong("orderId", baseOrder.orderId).navigation();
                    } else if (baseOrder.serviceType.equals(Config.TAXI)) {
                        ARouter.getInstance()
                                .build("/taxi/FlowActivity")
                                .withLong("orderId", baseOrder.orderId).navigation();
                    } else if (baseOrder.serviceType.equals(Config.CITY_LINE)) {
                        ARouter.getInstance()
                                .build("/cityline/FlowActivity")
                                .withSerializable("baseOrder", baseOrder).navigation();
                    } else if (baseOrder.serviceType.equals(Config.CHARTERED)) {
                    } else if (baseOrder.serviceType.equals(Config.RENTAL)) {
                    } else if (baseOrder.serviceType.equals(Config.COUNTRY)) {
                        ARouter.getInstance()
                                .build("/custombus/CbRunActivity")
                                .withLong("scheduleId", baseOrder.scheduleId).navigation();
                    } else if (baseOrder.serviceType.equals(Config.CARPOOL)) {
                        ARouter.getInstance()
                                .build("/carpooling/FlowActivity")
                                .withSerializable("baseOrder", baseOrder).navigation();
                    } else if (baseOrder.serviceType.equals(Config.GOV)) {
//                        ARouter.getInstance()
//                                .build("/official/FlowActivity")
//                                .withLong("orderId", baseOrder.orderId).navigation();
                    }
                }
            });
        }
    }

    @NotNull
    private View.OnClickListener createOnClickListener(MultipleOrder baseOrder) {
        return v -> ARouter.getInstance()
                .build("/carpooling/FlowActivity")
                .withBoolean("needJump", true)
                .withSerializable("baseOrder", baseOrder).navigation();
    }

}
