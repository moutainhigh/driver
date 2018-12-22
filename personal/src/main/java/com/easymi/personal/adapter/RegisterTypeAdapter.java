package com.easymi.personal.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.easymi.common.entity.CompanyList;
import com.easymi.personal.R;
import com.easymi.personal.entity.BusinessType;
import com.easymi.personal.entity.Company;
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

    public void setList(List<?> list) {
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
        if (list.get(position) instanceof BusinessType){
            holder.tv_name.setText(((BusinessType) list.get(position)).name);
        }else if (list.get(position) instanceof CompanyList.Company){
            holder.tv_name.setText(((CompanyList.Company) list.get(position)).companyName);
        }
        if (position == _position){
            holder.iv_check.setVisibility(View.VISIBLE);
        }else {
            holder.iv_check.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(v -> {
            if (itemClickListener != null){
                itemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        TextView tv_name;
        ImageView iv_check;

        public Holder(View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            iv_check = itemView.findViewById(R.id.iv_check);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private OnItemClickListener itemClickListener;

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
