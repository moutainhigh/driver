package com.easymin.custombus.adapter;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easymin.custombus.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: StationAdapter
 * @Author: hufeng
 * @Date: 2019/2/16 下午3:03
 * @Description: 站点适配器
 * @History:
 */
public class StationAdapter extends RecyclerView.Adapter<StationAdapter.Holder>{

    private Context context;

    private List<String> listStation;

    /**
     * 构造器
     * @param context
     */
    public StationAdapter(Context context) {
        this.context = context;
        listStation = new ArrayList<>();
    }

    /**
     * 加载数据
     * @param listStation
     */
    public void setDatas(List<String> listStation) {
        this.listStation = listStation;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public StationAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_station, null);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(StationAdapter.Holder holder, int position) {
        if (position == 0){
            holder.lin_head.setVisibility(View.VISIBLE);
            holder.lin_foot.setVisibility(View.GONE);
            holder.lin_car.setVisibility(View.VISIBLE);
            holder.iv_station_top.setVisibility(View.GONE);
            holder.iv_station_bottom.setVisibility(View.VISIBLE);
            holder.iv_station.setImageResource(R.mipmap.cb_station_check);
        }else if (position == listStation.size()-1){
            holder.lin_head.setVisibility(View.GONE);
            holder.lin_foot.setVisibility(View.VISIBLE);
            holder.lin_car.setVisibility(View.GONE);
            holder.iv_station_top.setVisibility(View.VISIBLE);
            holder.iv_station_bottom.setVisibility(View.GONE);
            holder.iv_station.setImageResource(R.mipmap.cb_station_check);
        }else {
            holder.lin_head.setVisibility(View.GONE);
            holder.lin_foot.setVisibility(View.GONE);
            holder.lin_car.setVisibility(View.VISIBLE);
            holder.iv_station_top.setVisibility(View.VISIBLE);
            holder.iv_station_bottom.setVisibility(View.VISIBLE);
            holder.iv_station.setImageResource(R.mipmap.cb_station_nomal);
        }

        if (position == 2){
            holder.iv_car.setVisibility(View.VISIBLE);
        }else {
            holder.iv_car.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return listStation.size();
    }

    /**
     * ViewHolder
     */
    class Holder extends RecyclerView.ViewHolder {

        LinearLayout lin_head;
        ImageView iv_station_top;
        ImageView iv_station;
        ImageView iv_station_bottom;
        TextView tv_station_name;
        TextView tv_station_addr;

        TextView tv_pass_number;
        LinearLayout lin_car;
        ImageView iv_car_top;
        ImageView iv_car;
        ImageView iv_car_bottom;
        LinearLayout lin_foot;

        View rootView;

        public Holder(View itemView) {
            super(itemView);
            rootView = itemView;
            lin_head = itemView.findViewById(R.id.lin_head);
            iv_station_top = itemView.findViewById(R.id.iv_station_top);
            iv_station = itemView.findViewById(R.id.iv_station);
            iv_station_bottom = itemView.findViewById(R.id.iv_station_bottom);
            tv_station_name = itemView.findViewById(R.id.tv_station_name);
            tv_station_addr = itemView.findViewById(R.id.tv_station_addr);

            tv_pass_number = itemView.findViewById(R.id.tv_pass_number);
            lin_car = itemView.findViewById(R.id.lin_car);
            iv_car_top = itemView.findViewById(R.id.iv_car_top);
            iv_car = itemView.findViewById(R.id.iv_car);
            iv_car_bottom = itemView.findViewById(R.id.iv_car_bottom);
            lin_foot = itemView.findViewById(R.id.lin_foot);
        }
    }
}
