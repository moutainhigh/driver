package com.easymin.custombus.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.easymi.component.utils.TimeUtil;
import com.easymin.custombus.R;
import com.easymin.custombus.entity.DZBusLine;

import java.util.ArrayList;
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

public class BanciAdapter extends RecyclerView.Adapter<BanciAdapter.Holder> {

    private Context context;

    private List<DZBusLine> baseOrders;

    OnItemClickListener onItemClickListener;

    /**
     * 设置列表点击监听
     *
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 构造器
     *
     * @param context
     */
    public BanciAdapter(Context context) {
        this.context = context;
        baseOrders = new ArrayList<>();
    }

    /**
     * 加载数据
     *
     * @param baseOrders
     */
    public void setBaseOrders(List<DZBusLine> baseOrders) {
        this.baseOrders = baseOrders;
        notifyDataSetChanged();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dzbus_banci_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        DZBusLine dzBusLine = baseOrders.get(position);
        holder.orderDesc.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
        holder.orderType.setText("客运班车");
        holder.orderTime.setText(TimeUtil.getTime("yyyy-MM-dd HH:mm", dzBusLine.time * 1000));
        holder.orderStartPlace.setText(dzBusLine.startStation);
        holder.orderEndPlace.setText(dzBusLine.endStation);
        holder.orderStatus.setText("售票中 >");
        if (null != onItemClickListener) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onClick(dzBusLine);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return baseOrders.size();
    }

    /**
     * ViewHolder
     */
    class Holder extends RecyclerView.ViewHolder {

        TextView orderTime;
        TextView orderStatus;
        TextView orderStartPlace;
        TextView orderEndPlace;
        TextView orderType;
        View rootView;
        TextView orderDesc;

        public Holder(View itemView) {
            super(itemView);
            rootView = itemView;
            orderDesc = itemView.findViewById(R.id.order_desc);
            orderTime = itemView.findViewById(R.id.order_time);
            orderStatus = itemView.findViewById(R.id.order_status);
            orderStartPlace = itemView.findViewById(R.id.order_start_place);
            orderEndPlace = itemView.findViewById(R.id.order_end_place);
            orderType = itemView.findViewById(R.id.order_type);
        }
    }

    /**
     * 子项点击接口
     */
    public interface OnItemClickListener {
        /**
         * 单击监听方法
         *
         * @param zxOrder
         */
        void onClick(DZBusLine zxOrder);
    }
}
