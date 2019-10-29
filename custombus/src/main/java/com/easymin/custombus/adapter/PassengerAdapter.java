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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.easymi.component.Config;
import com.easymi.component.utils.CommonUtil;
import com.easymi.component.utils.DensityUtil;
import com.easymi.component.utils.GlideCircleTransform;
import com.easymin.custombus.R;
import com.easymin.custombus.entity.Customer;

import java.util.ArrayList;
import java.util.List;

import static com.easymin.custombus.entity.Customer.CITY_COUNTRY_STATUS_PAY;

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
    private int inspectTicket;

    public void setOnDialogShowingListener(OnDialogShowingListener onDialogShowingListener) {
        this.onDialogShowingListener = onDialogShowingListener;
    }

    private OnConfirmBoardingListener onConfirmBoardingListener;

    public void setOnConfirmBoarding(OnConfirmBoardingListener onConfirmBoardingListener) {
        this.onConfirmBoardingListener = onConfirmBoardingListener;
    }

    /**
     * 构造器
     *
     * @param context
     */
    public PassengerAdapter(Context context, int inspectTicket) {
        this.context = context;
        listPassenger = new ArrayList<>();
        this.inspectTicket = inspectTicket;
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

        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) holder.cus_ll.getLayoutParams();
        if (position == 0) {
            layoutParams.topMargin = DensityUtil.dp2px(context, 0);
        } else {
            layoutParams.topMargin = DensityUtil.dp2px(context, 12);
        }
        holder.cus_ll.setLayoutParams(layoutParams);

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
        if (TextUtils.isEmpty(customer.sorts)) {
            holder.tv_pass_number.setText("车票数量: " + customer.ticketNumber);
        } else {
            holder.tv_pass_number.setText(CommonUtil.getPassengerDescAndType(customer.type, customer.sorts, customer.sortsType));
        }

        holder.iv_call_phone.setOnClickListener(v -> {
            //跳转到拨号界面，同时传递电话号码
            Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + customer.passengerPhone));
            context.startActivity(dialIntent);
        });
        holder.cusDesc.setText("备注: " + (TextUtils.isEmpty(customer.orderRemark) ? "暂无备注" : customer.orderRemark));

        if (inspectTicket == 2) {
            holder.cusRl.setVisibility(customer.status == CITY_COUNTRY_STATUS_PAY
                    || customer.status == Customer.CITY_COUNTRY_STATUS_ARRIVED ? View.VISIBLE : View.GONE);
        } else {
            holder.cusRl.setVisibility(customer.status == CITY_COUNTRY_STATUS_PAY ? View.VISIBLE : View.GONE);
        }

        if (inspectTicket == 2 && customer.status == Customer.CITY_COUNTRY_STATUS_ARRIVED) {
            holder.cusTvCancel.setVisibility(View.GONE);
            holder.cusTvPay.setVisibility(View.VISIBLE);
            holder.cusTvPay.setText("确认上车");
        } else {
            holder.cusTvCancel.setVisibility(View.VISIBLE);
            holder.cusTvPay.setVisibility(View.VISIBLE);
            holder.cusTvPay.setText("代付");
        }

        holder.cusTvPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inspectTicket == 2 && customer.status == Customer.CITY_COUNTRY_STATUS_ARRIVED) {
                    if (onConfirmBoardingListener != null) {
                        onConfirmBoardingListener.onConfirm(customer.id);
                    }
                } else {
                    if (onDialogShowingListener != null) {
                        onDialogShowingListener.onShowing(true, customer.id, customer.money);
                    }
                }
            }
        });
        holder.cusTvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDialogShowingListener != null) {
                    onDialogShowingListener.onShowing(false, customer.id, customer.money);
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
        LinearLayout cus_ll;


        public Holder(View itemView) {
            super(itemView);
            rootView = itemView;
            cus_ll = itemView.findViewById(R.id.cus_ll);
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
        void onShowing(boolean isPay, long orderId, double money);
    }

    public interface OnConfirmBoardingListener {
        void onConfirm(long orderId);
    }

}
