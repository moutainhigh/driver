package com.easymi.personal.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.easymi.component.utils.TimeUtil;
import com.easymi.personal.R;
import com.easymi.personal.entity.TixianRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: TixianRecordAdapter
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class TixianRecordAdapter extends RecyclerView.Adapter<TixianRecordAdapter.TixianRecordHolder> {

    private Context context;

    private List<TixianRecord> list;

    public TixianRecordAdapter(Context context) {
        this.context = context;
        list = new ArrayList<>();
    }

    public void setList(List<TixianRecord> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public TixianRecordHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tixian_record_item, parent, false);

        return new TixianRecordHolder(view);
    }

    @Override
    public void onBindViewHolder(TixianRecordHolder holder, int position) {
        TixianRecord tixianRecord = list.get(position);
        holder.tixianRecordTime.setText(TimeUtil.getTime("yyyy-MM-dd HH:mm", tixianRecord.time * 1000));
        holder.tixianRecordStatus.setText(tixianRecord.getStatusStr(context));
        holder.tixianRecordMoney.setText(tixianRecord.money + context.getString(R.string.yuan));

        if (tixianRecord.status == 2) {
            holder.tixianRecordStatus.setTextColor(context.getResources().getColor(R.color.dark_gray));
        } else {
            holder.tixianRecordStatus.setTextColor(context.getResources().getColor(R.color.colorAccent));
//            if (tixianRecord.status == 3) {
//                holder.tixianRecordRefuse.setVisibility(View.VISIBLE);
//                holder.tixianRecordRefuse.setText(tixianRecord.refuseReason);
//            }
        }

        holder.dian1.setVisibility(View.VISIBLE);
        holder.dian2.setVisibility(View.VISIBLE);
        holder.dian3.setVisibility(View.VISIBLE);
        holder.dian4.setVisibility(View.VISIBLE);

        if (position == 0) {
            holder.dian1.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class TixianRecordHolder extends RecyclerView.ViewHolder {

        TextView tixianRecordTime;
        TextView tixianRecordStatus;
        TextView tixianRecordMoney;
        TextView tixianRecordRefuse;

        ImageView dian1;
        ImageView dian2;
        ImageView dian3;
        ImageView dian4;

        public TixianRecordHolder(View itemView) {
            super(itemView);
            tixianRecordTime = itemView.findViewById(R.id.tixian_time);
            tixianRecordStatus = itemView.findViewById(R.id.tixian_status);
            tixianRecordMoney = itemView.findViewById(R.id.tixian_money);
            tixianRecordRefuse = itemView.findViewById(R.id.tixian_refuse_reason);

            dian1 = itemView.findViewById(R.id.ic_dian_1);
            dian2 = itemView.findViewById(R.id.ic_dian_2);
            dian3 = itemView.findViewById(R.id.ic_dian_3);
            dian4 = itemView.findViewById(R.id.ic_dian_4);
        }
    }
}
