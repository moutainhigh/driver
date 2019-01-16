package com.easymi.common.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easymi.common.R;
import com.easymi.common.entity.MultipleOrder;
import com.easymi.common.util.DJStatus2Str;
import com.easymi.component.BusOrderStatus;
import com.easymi.component.Config;
import com.easymi.component.entity.BaseOrder;
import com.easymi.component.utils.TimeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: MyOrderAdapter
 * Author: shine
 * Date: 2018/11/15 下午6:33
 * Description:
 * History:
 */
public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.Holder> {

    private Context context;

    private List<MultipleOrder> list;

    /**
     * tab类型 1我接的单，2指派订单，3可抢订单
     */
    private int mType;

    public MyOrderAdapter(Context context,int type) {
        this.context = context;
        this.mType = type;
        list = new ArrayList<>();
    }

    /**
     * 设置数据
     * @param orders
     */
    public void setBaseOrders(List<MultipleOrder> orders) {
        this.list = orders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyOrderAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_order, null);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyOrderAdapter.Holder holder, int position) {

        MultipleOrder baseOrder = list.get(position);

        holder.order_type.setText("" + baseOrder.getOrderType());
        holder.order_time.setText(TimeUtil.getTime(context.getString(R.string.time_five_format), baseOrder.bookTime * 1000));
        holder.order_start_place.setText(baseOrder.getStartSite().address);
        holder.order_end_place.setText(baseOrder.getEndSite().address);

        if (TextUtils.equals(baseOrder.serviceType, Config.CITY_LINE)) {
            holder.order_status.setText("" + baseOrder.getZXOrderStatusStr() + " >");
        } else if (TextUtils.equals(baseOrder.serviceType, Config.COUNTRY)) {
            holder.order_status.setText(BusOrderStatus.status2Str(baseOrder.status) + " >");
        }else {
            holder.order_status.setText(DJStatus2Str.int2Str(baseOrder.serviceType, baseOrder.status) + " >");
        }

        if (mType == 1){
            holder.tv_accept.setVisibility(View.GONE);
            holder.tv_grab.setVisibility(View.GONE);
            holder.tv_refuse.setVisibility(View.GONE);
        }else if (mType == 2){
            holder.tv_accept.setVisibility(View.GONE);
            holder.tv_grab.setVisibility(View.VISIBLE);
            holder.tv_refuse.setVisibility(View.GONE);
        }else if (mType == 3){
            holder.tv_accept.setVisibility(View.GONE);
            holder.tv_grab.setVisibility(View.GONE);
            holder.tv_refuse.setVisibility(View.GONE);
        }

        holder.root.setOnClickListener(v -> {
            if (itemClickListener!=null){
                itemClickListener.itemClick(v,baseOrder);
            }
        });
        holder.tv_accept.setOnClickListener(v -> {
            if (itemClickListener!=null){
                itemClickListener.itemClick(v,baseOrder);
            }
        });
        holder.tv_grab.setOnClickListener(v -> {
            if (itemClickListener!=null){
                itemClickListener.itemClick(v,baseOrder);
            }
        });
        holder.tv_refuse.setOnClickListener(v -> {
            if (itemClickListener!=null){
                itemClickListener.itemClick(v,baseOrder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        LinearLayout root;
        TextView order_type;
        TextView order_status;
        TextView order_time;
        TextView order_start_place;
        TextView order_end_place;

        TextView tv_accept;
        TextView tv_refuse;
        TextView tv_grab;

        public Holder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.root);
            order_type = itemView.findViewById(R.id.order_type);
            order_status = itemView.findViewById(R.id.order_status);
            order_time = itemView.findViewById(R.id.order_time);
            order_start_place = itemView.findViewById(R.id.order_start_place);
            order_end_place = itemView.findViewById(R.id.order_end_place);

            tv_accept = itemView.findViewById(R.id.tv_accept);
            tv_refuse = itemView.findViewById(R.id.tv_refuse);
            tv_grab = itemView.findViewById(R.id.tv_grab);
        }
    }



    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public interface ItemClickListener {
        /**
         * 列表单击监听
         * @param view
         * @param baseOrder
         */
        void itemClick(View view,BaseOrder baseOrder);
    }

}
