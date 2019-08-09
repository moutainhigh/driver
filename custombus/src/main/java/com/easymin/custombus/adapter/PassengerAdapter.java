package com.easymin.custombus.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.easymi.component.Config;
import com.easymi.component.utils.GlideCircleTransform;
import com.easymin.custombus.R;
import com.easymin.custombus.entity.Customer;

import java.util.ArrayList;
import java.util.List;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: PassengerAdapter
 * @Author: hufeng
 * @Date: 2019/2/16 下午4:29
 * @Description: 乘客信息适配器
 * @History:
 */
public class PassengerAdapter extends RecyclerView.Adapter<PassengerAdapter.Holder> {

    private Context context;

    private List<Customer> listPassenger;

    RequestOptions options = new RequestOptions()
            .centerCrop()
            .transform(new GlideCircleTransform())
            .placeholder(R.mipmap.com_head_up)
            .diskCacheStrategy(DiskCacheStrategy.ALL);

    private OnDialogShowingListener onDialogShowingListener;

    public void setOnDialogShowingListener(OnDialogShowingListener onDialogShowingListener) {
        this.onDialogShowingListener = onDialogShowingListener;
    }

    /**
     * 构造器
     *
     * @param context
     */
    public PassengerAdapter(Context context) {
        this.context = context;
        listPassenger = new ArrayList<>();
    }

    /**
     * 加载数据
     *
     * @param listPassenger
     */
    public void setDatas(List<Customer> listPassenger) {
        this.listPassenger = listPassenger;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PassengerAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_passenger, parent, false);
        return new PassengerAdapter.Holder(view);
    }

    @Override
    public void onBindViewHolder(PassengerAdapter.Holder holder, int position) {
        Customer customer = listPassenger.get(position);

        if (customer.status <= Customer.CITY_COUNTRY_STATUS_ARRIVED) {
            holder.iv_call_phone.setVisibility(View.VISIBLE);
            holder.tv_status.setVisibility(View.GONE);
        } else if (customer.status != Customer.CITY_COUNTRY_STATUS_INVALID) {
            holder.iv_call_phone.setVisibility(View.GONE);
            holder.tv_status.setVisibility(View.VISIBLE);
            holder.tv_status.setText(context.getResources().getString(R.string.cb_alredy_check));
        } else {
            holder.iv_call_phone.setVisibility(View.GONE);
            holder.tv_status.setVisibility(View.VISIBLE);
            holder.tv_status.setText(context.getResources().getString(R.string.cb_alredy_jump));
        }
        Glide.with(context)
                .load(Config.IMG_SERVER + customer.avatar + Config.IMG_PATH)
                .apply(options)
                .into(holder.iv_head);

        holder.tv_pass_name.setText(customer.passengerName);
        holder.tv_pass_number.setText("车票数量: " + customer.ticketNumber);

        holder.iv_call_phone.setOnClickListener(v -> {
            //跳转到拨号界面，同时传递电话号码
            Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + customer.passengerPhone));
            context.startActivity(dialIntent);
        });
        holder.cusDesc.setText("备注: " + (TextUtils.isEmpty(customer.orderRemark) ? "暂无备注" : customer.orderRemark));

        holder.cusRl.setVisibility(customer.status == 1 ? View.VISIBLE : View.GONE);
        holder.cusTvPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDialogShowingListener != null) {
                    onDialogShowingListener.onShowing(true, customer.id);
                }
            }
        });
        holder.cusTvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDialogShowingListener != null) {
                    onDialogShowingListener.onShowing(false, customer.id);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listPassenger.size();
    }

    /**
     * ViewHolder
     */
    class Holder extends RecyclerView.ViewHolder {

        ImageView iv_head;
        TextView tv_pass_name;
        TextView tv_pass_number;
        ImageView iv_call_phone;
        TextView tv_status;

        View rootView;
        TextView cusDesc;
        RelativeLayout cusRl;
        TextView cusTvCancel;
        TextView cusTvPay;


        public Holder(View itemView) {
            super(itemView);
            rootView = itemView;
            iv_head = itemView.findViewById(R.id.cus_photo);
            tv_pass_name = itemView.findViewById(R.id.cus_name);
            tv_pass_number = itemView.findViewById(R.id.cp_ticket_num);
            iv_call_phone = itemView.findViewById(R.id.call_phone);
            tv_status = itemView.findViewById(R.id.status_text);

            cusDesc = itemView.findViewById(R.id.cus_desc);
            cusRl = itemView.findViewById(R.id.cus_rl);
            cusTvCancel = itemView.findViewById(R.id.cus_tv_cancel);
            cusTvPay = itemView.findViewById(R.id.cus_tv_pay);
        }
    }

    public interface OnDialogShowingListener {
        void onShowing(boolean isPay, long orderId);
    }

}
