package com.easymi.personal.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.easymi.personal.R;
import com.easymi.personal.entity.RecommendMoney;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: FinishActivity
 * Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class RecommendMoneyAdapter extends RecyclerView.Adapter<RecommendMoneyAdapter.RecommendMoneyHolder> {

    private Context context;

    private List<RecommendMoney> list;

    public RecommendMoneyAdapter(Context context) {
        this.context = context;
        list = new ArrayList<>();
    }

    public void setList(List<RecommendMoney> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public RecommendMoneyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recommend_money_item, null);

        return new RecommendMoneyHolder(view);
    }

    @Override
    public void onBindViewHolder(RecommendMoneyHolder holder, int position) {
        RecommendMoney recommend = list.get(position);
        holder.recommendTime.setText(recommend.time);
        holder.recommendMoney.setText("¥" + recommend.money);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class RecommendMoneyHolder extends RecyclerView.ViewHolder {

        TextView recommendTime;
        TextView recommendMoney;

        public RecommendMoneyHolder(View itemView) {
            super(itemView);
            recommendTime = itemView.findViewById(R.id.recommend_money_time);
            recommendMoney = itemView.findViewById(R.id.recommend_money_money);
        }
    }
}
