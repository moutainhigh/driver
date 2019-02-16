package com.easymin.custombus.adapter;

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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.easymi.component.Config;
import com.easymi.component.utils.GlideCircleTransform;
import com.easymin.custombus.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: PassengerAdapter
 * @Author: hufeng
 * @Date: 2019/2/16 下午4:29
 * @Description:  乘客信息适配器
 * @History:
 */
public class PassengerAdapter extends RecyclerView.Adapter<PassengerAdapter.Holder>{


    private Context context;

    private List<String> listPassenger;

    RequestOptions options = new RequestOptions()
            .centerCrop()
            .transform(new GlideCircleTransform())
            .placeholder(R.mipmap.com_head_up)
            .diskCacheStrategy(DiskCacheStrategy.ALL);

    /**
     * 构造器
     * @param context
     */
    public PassengerAdapter(Context context) {
        this.context = context;
        listPassenger = new ArrayList<>();
    }

    /**
     * 加载数据
     * @param listPassenger
     */
    public void setDatas(List<String> listPassenger) {
        this.listPassenger = listPassenger;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PassengerAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_passenger, parent,false);
        return new PassengerAdapter.Holder(view);
    }

    @Override
    public void onBindViewHolder(PassengerAdapter.Holder holder, int position) {
        Glide.with(context)
//                .load(Config.IMG_SERVER + baseOrder.avatar + Config.IMG_PATH)
                .load("http://img1.3lian.com/img013/v5/21/d/84.jpg")
                .apply(options)
                .into(holder.iv_head);

        if (position == listPassenger.size()-1){
            holder.iv_line.setVisibility(View.GONE);
        }
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
        ImageView iv_line;
        TextView tv_status;

        View rootView;

        public Holder(View itemView) {
            super(itemView);
            rootView = itemView;
            iv_head = itemView.findViewById(R.id.iv_head);
            tv_pass_name = itemView.findViewById(R.id.tv_pass_name);
            tv_pass_number = itemView.findViewById(R.id.tv_pass_number);
            iv_call_phone = itemView.findViewById(R.id.iv_call_phone);
            iv_line = itemView.findViewById(R.id.iv_line);
            tv_status = itemView.findViewById(R.id.tv_status);
        }
    }
}
