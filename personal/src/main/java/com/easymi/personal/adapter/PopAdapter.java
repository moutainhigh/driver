package com.easymi.personal.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easymi.component.app.XApp;
import com.easymi.component.utils.StringUtils;
import com.easymi.personal.R;

import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: PopAdapter
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class PopAdapter extends RecyclerView.Adapter {
    private List<String> mData;
    private String showingStr;

    private OnItemClick onItemClick;

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public void setData(List<String> data, String showingStr) {
        mData = data;
        this.showingStr = showingStr;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.pop_item, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;

        viewHolder.mTextView.setText(mData.get(position));
        if (StringUtils.isNotBlank(showingStr) && showingStr.equals(mData.get(position))) {
            viewHolder.mTextView.setTextColor(XApp.getInstance().getResources().getColor(R.color.white));
            viewHolder.linearLayout.setBackgroundResource(R.color.colorAccent);
            viewHolder.imageView.setImageResource(R.drawable.ic_check_white_24dp);
            viewHolder.imageView.setOnClickListener(null);
        } else {
            viewHolder.mTextView.setTextColor(XApp.getInstance().getResources().getColor(R.color.text_default));
            viewHolder.linearLayout.setBackgroundResource(R.color.white);
            viewHolder.imageView.setImageResource(R.mipmap.ic_qiye_close);
            viewHolder.imageView.setOnClickListener(v -> {
                onItemClick.onDataDelete(mData.get(position), position);
            });
        }
        if (null != onItemClick) {
            viewHolder.linearLayout.setOnClickListener(v -> onItemClick.onItemClick(mData.get(position)));
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        LinearLayout linearLayout;
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.text_content);
            linearLayout = itemView.findViewById(R.id.pop_back);
            imageView = itemView.findViewById(R.id.ic_pop_close);
        }
    }

    public interface OnItemClick {
        void onItemClick(String qiye);

        void onDataDelete(String qiye, int pos);
    }
}
