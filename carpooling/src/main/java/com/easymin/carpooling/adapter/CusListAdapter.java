package com.easymin.carpooling.adapter;

import android.content.Context;
import android.graphics.Color;
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
import com.easymi.component.Config;
import com.easymi.component.utils.GlideCircleTransform;
import com.easymi.component.utils.PhoneUtil;
import com.easymin.carpooling.R;

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

public class CusListAdapter extends RecyclerView.Adapter<CusListAdapter.ViewHolder> {

    private List<CarpoolOrder> carpoolOrders;
    private Context context;

    private CarpoolOrder lastCustomer;
    private int flag;//标志位，区分PasTicketsFragment和CusListFragment

    private OnCallClickListener onCallClickListener;

    public void setOnCallClickListener(OnCallClickListener onCallClickListener) {
        this.onCallClickListener = onCallClickListener;
    }

    /**
     * @param context
     * @param flag    0代表PasTicketsFragment，1代表CusListFragment
     */
    public CusListAdapter(Context context, int flag) {
        this.context = context;
        this.flag = flag;
    }

    /**
     * 设置数据
     *
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cp_cus_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CarpoolOrder carpoolOrder = carpoolOrders.get(position);
        holder.sequenceNum.setText(String.valueOf(carpoolOrder.num));
        holder.cusName.setText(carpoolOrder.passengerName);
        holder.ticketNum.setText("车票:"+carpoolOrder.ticketNumber);
        if (flag == 0) {//PasTickets
            if (carpoolOrder.isContract == 1) {
                holder.status.setVisibility(View.VISIBLE);
                holder.status.setText("已联系");
                holder.status.setBackgroundResource(R.drawable.corner_status_called);
                holder.status.setTextColor(Color.parseColor("#999999"));
            } else {
                holder.status.setVisibility(View.GONE);
            }
            holder.callPhone.setVisibility(View.VISIBLE);
        } else if (flag == 1) { //CusList
            holder.status.setVisibility(View.VISIBLE);
            holder.status.setText(carpoolOrder.getOrderStatus());
            holder.callPhone.setVisibility(View.VISIBLE);
            if (carpoolOrder.customeStatus == 0 || carpoolOrder.customeStatus == 3) {
                holder.status.setBackgroundResource(R.drawable.corner_status_not_accept);
                holder.status.setTextColor(Color.parseColor("#FFFFFF"));
            } else if (carpoolOrder.customeStatus == 1 || carpoolOrder.customeStatus == 4) {
                holder.status.setBackgroundResource(R.drawable.corner_status_called);
                holder.status.setTextColor(Color.parseColor("#999999"));
                holder.callPhone.setVisibility(View.GONE);//已接、已送隐藏拨打电话
            } else if (carpoolOrder.customeStatus == 2 || carpoolOrder.customeStatus == 5) {
                holder.status.setBackgroundResource(R.drawable.corner_status_jumped);
                holder.status.setTextColor(Color.parseColor("#FFFFFF"));
            }

            if (lastCustomer == null) {
                if (carpoolOrder.customeStatus == 0 || carpoolOrder.customeStatus == 3) {
                    holder.status.setText("当前");
                    holder.status.setBackgroundResource(R.drawable.corner_status_current);
                    holder.status.setTextColor(Color.parseColor("#FFFFFF"));
                }
            } else {
                if (lastCustomer.customeStatus != 0 && lastCustomer.customeStatus != 3) {
                    if (carpoolOrder.customeStatus == 0 || carpoolOrder.customeStatus == 3) {
                        holder.status.setText("当前");
                        holder.status.setBackgroundResource(R.drawable.corner_status_current);
                        holder.status.setTextColor(Color.parseColor("#FFFFFF"));
                    }
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

        holder.callPhone.setOnClickListener(v -> {
            if (flag == 0) {
                if(null != onCallClickListener){
                    onCallClickListener.onCallClick(carpoolOrder,position);
                }
            }
        });


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
        TextView ticketNum;
        TextView status;
        ImageView callPhone;

        public ViewHolder(View itemView) {
            super(itemView);
            sequenceNum = itemView.findViewById(R.id.sequence_num);
            cusPhoto = itemView.findViewById(R.id.cus_photo);
            cusName = itemView.findViewById(R.id.cus_name);
            status = itemView.findViewById(R.id.status_text);
            ticketNum = itemView.findViewById(R.id.cp_ticket_num);
            callPhone = itemView.findViewById(R.id.call_phone);
        }
    }

    public interface OnCallClickListener{
        void onCallClick(CarpoolOrder order,int position);
    }

}
