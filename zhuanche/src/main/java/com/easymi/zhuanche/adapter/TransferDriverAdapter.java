package com.easymi.zhuanche.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.easymi.component.Config;
import com.easymi.component.utils.GlideCircleTransform;
import com.easymi.component.widget.RatingBar;
import com.easymi.zhuanche.R;
import com.easymi.zhuanche.entity.TransferList;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: TransferDriverAdapter
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description: 未使用
 * History:
 */

public class TransferDriverAdapter extends RecyclerView.Adapter<TransferDriverAdapter.THolder>{

    private List<TransferList.Emploie> mList = new ArrayList<>();
    private Context context;
    private OnTransferListener listener;

    public TransferDriverAdapter(Context context, List<TransferList.Emploie> emploies) {
        if (emploies != null && !emploies.isEmpty()) {
            mList.addAll(emploies);
        }
        this.context = context;
    }

    @Override
    public THolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.zc_item_transfer, parent, false);
        return new THolder(view);
    }

    @Override
    public void onBindViewHolder(THolder holder, int position) {
        TransferList.Emploie emploie = mList.get(position);

        holder.name.setText(emploie.employName);
        holder.tvDis.setText(String.valueOf(emploie.distance)+"km");
        holder.ratingBar.setStarMark(emploie.score);
        holder.carNo.setText(emploie.carNo);
        holder.carName.setText(emploie.carName);

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .transform(new GlideCircleTransform())
                .placeholder(R.mipmap.photo_default_1)
                .error(R.mipmap.photo_default_1)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(context)
                .load(Config.IMG_SERVER + emploie.photo)
                .apply(options)
                .into(holder.photo);


        holder.transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onTransfer(emploie);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public interface OnTransferListener {
        void onTransfer(TransferList.Emploie emploie);
    }

    public void setOnTransferListener(OnTransferListener listener) {
        this.listener = listener;
    }

    public class THolder extends RecyclerView.ViewHolder {
        private TextView name;
        private ImageView photo;
        private TextView tvDis;
        private RatingBar ratingBar;
        private View transfer;
        private TextView carNo;
        private TextView carName;

        public THolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            photo = itemView.findViewById(R.id.driver_photo);
            tvDis = itemView.findViewById(R.id.dis);
            ratingBar = itemView.findViewById(R.id.rating_bar);
            transfer = itemView.findViewById(R.id.transfer);
            carNo = itemView.findViewById(R.id.car_no);
            carName = itemView.findViewById(R.id.car_name);

        }
    }



}
