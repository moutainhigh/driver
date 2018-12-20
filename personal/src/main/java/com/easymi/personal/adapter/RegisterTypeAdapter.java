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
 * FileName: RegisterTypeAdapter
 * Author: shine
 * Date: 2018/12/19 上午11:05
 * Description:
 * History:
 */
public class RegisterTypeAdapter extends RecyclerView.Adapter<RegisterTypeAdapter.Holder> {

    private Context context;

    private List<?> list;

    public RegisterTypeAdapter(Context context) {
        this.context = context;
        list = new ArrayList<>();
    }

    public void setList(List<Recommend> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    private int _position = -1;

    public void setSelect(int position){
        this._position = position;
        notifyDataSetChanged();
    }

    @Override
    public RegisterTypeAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_register_type, parent, false);

        return new RegisterTypeAdapter.Holder(view);
    }

    @Override
    public void onBindViewHolder(RegisterTypeAdapter.Holder holder, int position) {

        if (position == _position){
            holder.iv_check.setVisibility(View.VISIBLE);
        }else {
            holder.iv_check.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        TextView tv_name;
        TextView iv_check;

        public Holder(View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            iv_check = itemView.findViewById(R.id.iv_check);
        }
    }
}
