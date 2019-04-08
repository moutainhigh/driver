package com.easymi.taxi.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.easymi.component.Config;
import com.easymi.component.ZCOrderStatus;
import com.easymi.component.utils.GlideCircleTransform;
import com.easymi.component.utils.PhoneUtil;
import com.easymi.component.utils.StringUtils;
import com.easymi.taxi.R;
import com.easymi.taxi.entity.SameOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: SameOrderAdapter
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description: 未使用
 * History:
 */

public class SameOrderAdapter extends RecyclerView.Adapter<SameOrderAdapter.Holder> {

    private Context context;

    private List<SameOrder> sameOrders;

    public SameOrderAdapter(Context context) {
        this.context = context;
        sameOrders = new ArrayList<>();
    }

    public void setSameOrders(List<SameOrder> sameOrders) {
        this.sameOrders = sameOrders;
        notifyDataSetChanged();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.taxi_same_order_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        SameOrder sameOrder = sameOrders.get(position);
        holder.driver_name.setText(sameOrder.driverName);
        holder.driver_gonghao.setText(sameOrder.driverGonghao);
        if (sameOrder.isCaptain) {
            holder.is_captain.setVisibility(View.VISIBLE);
        } else {
            holder.is_captain.setVisibility(View.GONE);
        }
        if (StringUtils.isNotBlank(sameOrder.photoUrl)) {
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .transform(new GlideCircleTransform())
                    .placeholder(R.mipmap.photo_default_1)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(context)
                    .load(Config.IMG_SERVER + sameOrder.photoUrl + Config.IMG_PATH)
                    .apply(options)
                    .into(holder.driver_photo);
        }
        if (sameOrder.orderStatus == ZCOrderStatus.TAKE_ORDER) {
            holder.order_status.setText(context.getString(R.string.order_accepted));
            holder.order_status.setBackground(context.getResources().getDrawable(R.drawable.bg_blue_stroke));
        } else if (sameOrder.orderStatus == ZCOrderStatus.START_WAIT_ORDER) {
            holder.order_status.setText(context.getString(R.string.order_waiting));
            holder.order_status.setBackground(context.getResources().getDrawable(R.drawable.bg_yellow_stroke));
        } else {
            holder.order_status.setText(context.getString(R.string.order_running));
            holder.order_status.setBackground(context.getResources().getDrawable(R.drawable.bg_green_stroke));
        }
        holder.call_phone.setOnClickListener(view -> PhoneUtil.call((Activity) context, sameOrder.driverPhone));
    }

    @Override
    public int getItemCount() {
        return sameOrders.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        ImageView driver_photo;
        ImageView is_captain;
        TextView driver_name;
        TextView driver_gonghao;
        TextView order_status;
        ImageView call_phone;

        public Holder(View itemView) {
            super(itemView);
            driver_photo = itemView.findViewById(R.id.driver_photo);
            is_captain = itemView.findViewById(R.id.is_captain);
            driver_name = itemView.findViewById(R.id.driver_name);
            driver_gonghao = itemView.findViewById(R.id.driver_gonghao);
            order_status = itemView.findViewById(R.id.order_status);
            call_phone = itemView.findViewById(R.id.call_phone);
        }
    }
}
