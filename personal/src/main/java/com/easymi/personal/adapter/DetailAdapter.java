package com.easymi.personal.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.easymi.component.utils.TimeUtil;
import com.easymi.personal.R;
import com.easymi.personal.entity.Detail;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: DetailAdapter
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.DetailHolder> {

    private Context context;

    private List<Detail> list;

    public DetailAdapter(Context context) {
        this.context = context;
        list = new ArrayList<>();
    }

    public void setList(List<Detail> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public DetailHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_item, parent, false);

        return new DetailHolder(view);
    }

    @Override
    public void onBindViewHolder(DetailHolder holder, int position) {
        Detail detail = list.get(position);
        holder.detailTime.setText(TimeUtil.getTime("yyyy-MM-dd HH:mm", detail.time * 1000));
        //ALIPAY_DRIVER_RECHARGE  支付宝充值
        //WECHAT_DRIVER_RECHARGE  微信充值
        //PAY_DRIVER_ROYALTY  提成
        //DRIVER_PUT_FORWARD  提现申請
        //REJECT_PUT_FORWARD 提现拒絕
        //ACCEPT_PUT_FORWARD 提现同意
        if (TextUtils.equals(detail.purpose, "DRIVER_RECHARGE")) {
            holder.detailPurpose.setText("司机充值");
        } else if (TextUtils.equals(detail.purpose, "PAY_DRIVER_BALANCE")) {
            holder.detailPurpose.setText("余额支付");
        } else if (TextUtils.equals(detail.purpose, "ADMIN_DRIVER_RECHARGE")) {
            holder.detailPurpose.setText("后台充值");
        } else if (TextUtils.equals(detail.purpose, "ALIPAY_DRIVER_RECHARGE")) {
            holder.detailPurpose.setText("支付宝充值");
        }else if (TextUtils.equals(detail.purpose, "WECHAT_DRIVER_RECHARGE")) {
            holder.detailPurpose.setText("微信充值");
        }else if (TextUtils.equals(detail.purpose, "PAY_DRIVER_ROYALTY")){
            holder.detailPurpose.setText("提成");
        }else if (TextUtils.equals(detail.purpose, "DRIVER_PUT_FORWARD")) {
            holder.detailPurpose.setText("提现申請");
        }else if (TextUtils.equals(detail.purpose, "REJECT_PUT_FORWARD")) {
            holder.detailPurpose.setText("提现拒絕");
        }else if (TextUtils.equals(detail.purpose, "ACCEPT_PUT_FORWARD")) {
            holder.detailPurpose.setText("提现通过");
        }else {
            holder.detailPurpose.setText("其他");
        }

        holder.detailMoney.setText("¥" + detail.money);
        if (detail.money > 0) {
            holder.detailMoney.setTextColor(context.getResources().getColor(R.color.yellow));
        } else {
            holder.detailMoney.setTextColor(context.getResources().getColor(R.color.colorAccent));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class DetailHolder extends RecyclerView.ViewHolder {

        TextView detailTime;
        TextView detailPurpose;
        TextView detailMoney;

        public DetailHolder(View itemView) {
            super(itemView);
            detailTime = itemView.findViewById(R.id.detail_time);
            detailPurpose = itemView.findViewById(R.id.detail_purpose);
            detailMoney = itemView.findViewById(R.id.detail_money);
        }
    }
}
