package com.easymi.personal.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.easymi.personal.R;
import com.easymi.personal.entity.Recommend;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: RecommendAdapter
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class RecommendAdapter extends RecyclerView.Adapter<RecommendAdapter.RecommendHolder> {

    private Context context;

    private List<Recommend> list;

    public RecommendAdapter(Context context) {
        this.context = context;
        list = new ArrayList<>();
    }

    public void setList(List<Recommend> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public RecommendHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recommend_item, parent, false);

        return new RecommendHolder(view);
    }

    @Override
    public void onBindViewHolder(RecommendHolder holder, int position) {
        Recommend recommend = list.get(position);
        holder.recommendName.setText(recommend.name);
        holder.recommendPhone.setText(recommend.phone);
        holder.recommendTime.setText(recommend.time);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class RecommendHolder extends RecyclerView.ViewHolder {

        TextView recommendTime;
        TextView recommendPhone;
        TextView recommendName;

        public RecommendHolder(View itemView) {
            super(itemView);
            recommendTime = itemView.findViewById(R.id.recommend_time);
            recommendPhone = itemView.findViewById(R.id.recommend_phone);
            recommendName = itemView.findViewById(R.id.recommend_name);
        }
    }
}
