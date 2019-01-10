package com.easymi.common.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.easymi.common.R;
import com.easymi.common.entity.MultipleOrder;
import com.easymi.common.mvp.order.OrderActivity;
import com.easymi.common.util.DJStatus2Str;
import com.easymi.common.util.ZXStatus2Str;
import com.easymi.component.BusOrderStatus;
import com.easymi.component.Config;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc3;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.network.NoErrSubscriberListener;
import com.easymi.component.result.EmResult2;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.utils.TimeUtil;
import com.easymi.component.utils.ToastUtil;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
            baseViewHolder.setText(R.id.pinned_text, "订单信息");
            baseViewHolder.itemView.setOnClickListener(v -> {
                context.startActivity(new Intent(context, OrderActivity.class));
            });
        } else if (baseOrder.getItemType() == MultipleOrder.ITEM_POSTER) {

            baseViewHolder.setText(R.id.order_time, TimeUtil.getTime(context.getString(R.string.time_format), baseOrder.bookTime * 1000));
            baseViewHolder.setText(R.id.order_type, "" + baseOrder.getOrderType());
            baseViewHolder.setText(R.id.order_start_place, "" + baseOrder.bookAddress);
            baseViewHolder.setText(R.id.order_end_place, baseOrder.destination);
            if (TextUtils.equals(baseOrder.serviceType, Config.CITY_LINE)) {//专线
                baseViewHolder.setText(R.id.order_status, "" + baseOrder.getZXOrderStatusStr() + " >");
            } else if (TextUtils.equals(baseOrder.serviceType, Config.COUNTRY)) {
                baseViewHolder.setText(R.id.order_status, "" + BusOrderStatus.status2Str(baseOrder.scheduleStatus) + " >");
            } else {
                baseViewHolder.setText(R.id.order_status, "" + DJStatus2Str.int2Str(baseOrder.serviceType, baseOrder.status) + " >");
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
                        ARouter.getInstance()
                                .build("/chartered/FlowActivity")
                                .withLong("orderId", baseOrder.orderId).navigation();
                    } else if (baseOrder.serviceType.equals(Config.RENTAL)) {
                        ARouter.getInstance()
                                .build("/rental/FlowActivity")
                                .withLong("orderId", baseOrder.orderId).navigation();
                    } else if (baseOrder.serviceType.equals(Config.COUNTRY)) {
                        ARouter.getInstance()
                                .build("/passengerbus/BcFlowActivity")
                                .withLong("orderId", baseOrder.orderId)
                                .withLong("scheduleId", baseOrder.scheduleId).navigation();
                    }
                }
            });
        }
    }


}
