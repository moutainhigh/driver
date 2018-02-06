package com.easymi.common.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.easymi.common.R;
import com.easymi.common.activity.BaoxiaoActivity;
import com.easymi.common.util.DJStatus2Str;
import com.easymi.component.Config;
import com.easymi.component.DJOrderStatus;
import com.easymi.component.entity.BaseOrder;
import com.easymi.component.utils.TimeUtil;
import com.easymi.component.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuzihao on 2017/11/14.
 */

public class LiuShuiAdapter extends RecyclerView.Adapter<LiuShuiAdapter.Holder> {

    private Context context;

    private List<BaseOrder> baseOrders;

    public LiuShuiAdapter(Context context) {
        this.context = context;
        baseOrders = new ArrayList<>();
    }

    public void setBaseOrders(List<BaseOrder> baseOrders) {
        this.baseOrders = baseOrders;
        notifyDataSetChanged();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.liushui_item, null);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        BaseOrder baseOrder = baseOrders.get(position);
        String typeStr = "";
        if (baseOrder.orderType.equals(Config.DAIJIA)) {
            typeStr = context.getString(R.string.create_daijia);
        }
        holder.orderType.setText(typeStr);
        holder.orderEndPlace.setText(baseOrder.endPlace);
        holder.orderStartPlace.setText(baseOrder.startPlace);
        holder.orderStatus.setText(DJStatus2Str.int2Str(baseOrder.orderType, baseOrder.orderStatus));
        holder.orderTime.setText(TimeUtil.getTime("yyyy-MM-dd HH:mm", baseOrder.orderTime * 1000));
        holder.orderNumber.setText(baseOrder.orderNumber);
        holder.orderMoney.setText(String.valueOf(baseOrder.orderMoney));
        if (baseOrder.baoxiaoStatus == 1) {
            holder.orderBaoxiao.setClickable(true);
            holder.orderBaoxiao.setTextSize(14);
            holder.orderBaoxiao.setText(context.getString(R.string.liushui_baoxiao));
            holder.orderBaoxiao.setOnClickListener(view -> {
                Intent intent = new Intent(context, BaoxiaoActivity.class);
                intent.putExtra("orderId", baseOrder.orderId);
                context.startActivity(intent);
            });
        } else if (baseOrder.baoxiaoStatus == 2) {
            holder.orderBaoxiao.setClickable(false);
            holder.orderBaoxiao.setTextSize(10);
            holder.orderBaoxiao.setText(context.getString(R.string.liushui_baoxiao_sheheing));
        } else if (baseOrder.baoxiaoStatus == 3) {
            holder.orderBaoxiao.setClickable(false);
            holder.orderBaoxiao.setTextSize(14);
            holder.orderBaoxiao.setText(context.getString(R.string.liushui_baoxiao_done));
        }

        if (baseOrder.orderType.equals(Config.DAIJIA) &&
                baseOrder.orderStatus == DJOrderStatus.ARRIVAL_DESTINATION_ORDER) {
            holder.rootView.setOnClickListener(v -> {
                ARouter.getInstance().build("/daijia/FlowActivity")
                        .withLong("orderId", baseOrder.orderId)
                        .navigation();
            });
            holder.orderBaoxiao.setVisibility(View.GONE);
        } else {
            holder.orderBaoxiao.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return baseOrders.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        TextView orderTime;
        TextView orderStatus;
        TextView orderStartPlace;
        TextView orderEndPlace;
        TextView orderType;
        TextView orderNumber;
        TextView orderMoney;
        TextView orderBaoxiao;
        View rootView;

        public Holder(View itemView) {
            super(itemView);
            rootView = itemView;
            orderTime = itemView.findViewById(R.id.order_time);
            orderStatus = itemView.findViewById(R.id.order_status);
            orderStartPlace = itemView.findViewById(R.id.order_start_place);
            orderEndPlace = itemView.findViewById(R.id.order_end_place);
            orderType = itemView.findViewById(R.id.order_type);
            orderNumber = itemView.findViewById(R.id.order_no);
            orderMoney = itemView.findViewById(R.id.order_money);
            orderBaoxiao = itemView.findViewById(R.id.order_baoxiao);

        }
    }
}
