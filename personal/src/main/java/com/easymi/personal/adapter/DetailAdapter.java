package com.easymi.personal.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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
 * Created by developerLzh on 2017/11/10 0010.
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_item, parent,false);

        return new DetailHolder(view);
    }

    @Override
    public void onBindViewHolder(DetailHolder holder, int position) {
        Detail detail = list.get(position);
        holder.detailTime.setText(TimeUtil.getTime("yyyy-MM-dd HH:mm", detail.time * 1000));
        holder.detailPurpose.setText(detail.purpose);
        holder.detailMoney.setText("¥" + detail.money);
        if(detail.money > 0){
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
