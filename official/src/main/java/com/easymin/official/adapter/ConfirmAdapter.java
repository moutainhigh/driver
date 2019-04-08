package com.easymin.official.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.easymi.component.Config;
import com.easymi.component.utils.TimeUtil;
import com.easymin.official.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: ConfirmAdapter
 * @Author: hufeng
 * @Date: 2019/3/28 下午5:16
 * @Description:
 * @History:
 */
public class ConfirmAdapter extends RecyclerView.Adapter<ConfirmAdapter.Holder> {

    private Context context;

    private List<String> images;

    OnItemClickListener onItemClickListener;

    /**
     * 设置列表点击监听
     *
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 构造器
     *
     * @param context
     */
    public ConfirmAdapter(Context context) {
        this.context = context;
        images = new ArrayList<>();
    }

    /**
     * 加载数据
     *
     * @param baseOrders
     */
    public void setBaseOrders(List<String> baseOrders) {
        this.images = baseOrders;
        notifyDataSetChanged();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gw_item_confirm, null);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        if (images.size() == 0) {
            holder.iv_delete.setVisibility(View.GONE);
            holder.iv_image.setVisibility(View.GONE);
            holder.lin_empty.setVisibility(View.VISIBLE);

            if (position == images.size()) {
                holder.lin_empty.setOnClickListener(view -> {
                    if (onItemClickListener != null) {
                        onItemClickListener.onClick(view, position);
                    }
                });
            }
        } else {
            if (position == images.size()) {
                holder.iv_delete.setVisibility(View.GONE);
                holder.iv_image.setVisibility(View.GONE);
                holder.lin_empty.setVisibility(View.VISIBLE);

                holder.lin_empty.setOnClickListener(view -> {
                    if (onItemClickListener != null) {
                        onItemClickListener.onClick(view, position);
                    }
                });
            } else {
                holder.iv_delete.setVisibility(View.VISIBLE);
                holder.iv_image.setVisibility(View.VISIBLE);
                Glide.with(context).load(images.get(position)).into(holder.iv_image);
                holder.iv_delete.setOnClickListener(view -> {
                    if (onItemClickListener != null) {
                        onItemClickListener.onClick(view, position);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        if (images.size() == 9){
            return images.size();
        }
        return images.size() + 1;
    }

    /**
     * ViewHolder
     */
    class Holder extends RecyclerView.ViewHolder {

        LinearLayout lin_empty;
        ImageView iv_image;
        ImageView iv_delete;
        View rootView;

        public Holder(View itemView) {
            super(itemView);
            rootView = itemView;
            lin_empty = itemView.findViewById(R.id.lin_empty);
            iv_image = itemView.findViewById(R.id.iv_image);
            iv_delete = itemView.findViewById(R.id.iv_delete);
        }
    }

    /**
     * 子项点击接口
     */
    public interface OnItemClickListener {
        /**
         * 单击监听方法
         *
         * @param position
         */
        void onClick(View view, int position);
    }


}
