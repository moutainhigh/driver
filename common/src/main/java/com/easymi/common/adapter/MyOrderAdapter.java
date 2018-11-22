package com.easymi.common.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easymi.common.R;
import com.easymi.common.entity.MultipleOrder;
import com.easymi.common.util.DJStatus2Str;
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

    private List<?> list;

    public MyOrderAdapter(Context context) {
        this.context = context;
        list = new ArrayList<>();
    }

    public void setBaseOrders(List<?> orders) {
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
        if (list.get(position) instanceof MultipleOrder){
            MultipleOrder multipleOrder = (MultipleOrder) list.get(position);

            holder.order_type.setText("" + multipleOrder.getOrderType());
            holder.order_status.setText(DJStatus2Str.int2Str(multipleOrder.serviceType, multipleOrder.status));
            holder.order_time.setText(TimeUtil.getTime(context.getString(R.string.time_format), multipleOrder.bookTime * 1000));
            holder.order_start_place.setText(multipleOrder.getStartSite().address);
            holder.order_end_place.setText(multipleOrder.getEndSite().address);
        }
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

        public Holder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.root);
            order_type = itemView.findViewById(R.id.order_type);
            order_status = itemView.findViewById(R.id.order_status);
            order_time = itemView.findViewById(R.id.order_time);
            order_start_place = itemView.findViewById(R.id.order_start_place);
            order_end_place = itemView.findViewById(R.id.order_end_place);
        }
    }
}
