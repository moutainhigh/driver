package com.easymin.carpooling.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.easymi.common.entity.CarpoolOrder;
import com.easymi.common.entity.OrderCustomer;
import com.easymi.component.Config;
import com.easymi.component.utils.GlideCircleTransform;
import com.easymin.carpooling.R;

import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 * @Author: hufeng
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class CusListAdapter extends RecyclerView.Adapter<CusListAdapter.ViewHolder> {

    private List<CarpoolOrder> carpoolOrders;
    private Context context;

    private CarpoolOrder lastCustomer;

    /**
     * 构造器
     * @param context
     */
    public CusListAdapter(Context context) {
        this.context = context;
    }

    /**
     * 设置数据
     * @param carpoolOrders
     */
    public void setOrderCustomers(List<CarpoolOrder> carpoolOrders) {
        this.carpoolOrders = carpoolOrders;
        lastCustomer = null;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cus_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CarpoolOrder carpoolOrder = carpoolOrders.get(position);
        holder.sequenceNum.setText(String.valueOf(carpoolOrder.num));
        holder.cusName.setText(carpoolOrder.passengerName);
        holder.status.setText(carpoolOrder.getOrderStatus());

        if (carpoolOrder.customeStatus == 0 || carpoolOrder.customeStatus == 3) {
            holder.status.setBackgroundResource(R.drawable.corners_accent_8dp);
        } else {
            holder.status.setBackgroundResource(R.drawable.corners_gray_8dp);
        }

        if (lastCustomer == null) {
            if (carpoolOrder.customeStatus == 0 || carpoolOrder.customeStatus == 3) {
                holder.status.setText("当前");
                holder.status.setBackgroundResource(R.drawable.corners_orange_8dp);
            }
        } else {
            if (lastCustomer.status != 0 && lastCustomer.status != 3) {
                if (carpoolOrder.customeStatus == 0 || carpoolOrder.customeStatus == 3) {
                    holder.status.setText("当前");
                    holder.status.setBackgroundResource(R.drawable.corners_orange_8dp);
                }
            }
        }

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .transform(new GlideCircleTransform())
                .placeholder(R.mipmap.photo_default)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(context)
                .load(Config.IMG_SERVER + carpoolOrder.avatar + Config.IMG_PATH)
                .apply(options)
                .into(holder.cusPhoto);

        lastCustomer = carpoolOrder;
    }

    @Override
    public int getItemCount() {
        return carpoolOrders.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        //序号
        TextView sequenceNum;
        ImageView cusPhoto;
        TextView cusName;
        TextView status;

        public ViewHolder(View itemView) {
            super(itemView);
            sequenceNum = itemView.findViewById(R.id.sequence_num);
            cusPhoto = itemView.findViewById(R.id.cus_photo);
            cusName = itemView.findViewById(R.id.cus_name);
            status = itemView.findViewById(R.id.status_text);
        }
    }
}
