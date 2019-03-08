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

import com.easymin.custombus.R;
import com.easymin.custombus.entity.Station;

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

    private List<Station> listStation;

    private boolean checkStatus;
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
    public void setDatas(List<Station> listStation) {
        this.listStation = listStation;
        notifyDataSetChanged();
    }

    public void setCheckStatus(boolean checkStatus){
        this.checkStatus = checkStatus;
    }

    @NonNull
    @Override
    public StationAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_station, parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(StationAdapter.Holder holder, int position) {
        if (position == 0){
            holder.lin_head.setVisibility(View.VISIBLE);
            holder.lin_foot.setVisibility(View.GONE);
            holder.lin_car.setVisibility(View.GONE);
            holder.iv_station_top.setVisibility(View.INVISIBLE);
            holder.iv_station_bottom.setVisibility(View.VISIBLE);
            holder.iv_station.setImageResource(R.mipmap.cb_station_check);
        }else if (position == listStation.size()-1){
            holder.lin_head.setVisibility(View.GONE);
            holder.lin_foot.setVisibility(View.VISIBLE);
            holder.lin_car.setVisibility(View.VISIBLE);
            holder.iv_station_top.setVisibility(View.VISIBLE);
            holder.iv_station_bottom.setVisibility(View.INVISIBLE);
            holder.iv_station.setImageResource(R.mipmap.cb_station_check);
        }else {
            holder.lin_head.setVisibility(View.GONE);
            holder.lin_foot.setVisibility(View.GONE);
            holder.lin_car.setVisibility(View.VISIBLE);
            holder.iv_station_top.setVisibility(View.VISIBLE);
            holder.iv_station_bottom.setVisibility(View.VISIBLE);
            holder.iv_station.setImageResource(R.mipmap.cb_station_nomal);
        }

        Station station = listStation.get(position);
        if (station.status == 2){
            if (position == 0 || position == listStation.size()-1){
                holder.iv_station.setImageResource(R.mipmap.cb_station_check);
            }else {
                holder.iv_station.setImageResource(R.mipmap.cb_station_nomal);
            }
            holder.iv_car.setVisibility(View.VISIBLE);
        }else if (station.status == 3){
            holder.iv_station.setImageResource(R.mipmap.cb_car);
            holder.iv_car.setVisibility(View.GONE);
        }else {
            holder.iv_car.setVisibility(View.GONE);
            if (position == 0 || position == listStation.size()-1){
                holder.iv_station.setImageResource(R.mipmap.cb_station_check);
            }else {
                holder.iv_station.setImageResource(R.mipmap.cb_station_nomal);
            }
        }

        holder.tv_station_name.setText(station.name);
        holder.tv_station_addr.setText(station.address);

        if ((station.checkNumber + station.unCheckNumber) == 0){
            holder.tv_pass_number.setVisibility(View.GONE);
            holder.tv_chcke_status.setVisibility(View.GONE);
        }else {
            if (station.status == 1){
                holder.tv_pass_number.setText((station.checkNumber + station.unCheckNumber) +"");
                holder.tv_pass_number.setVisibility(View.VISIBLE);
                holder.tv_chcke_status.setVisibility(View.GONE);
            }else if (station.status == 4){
                holder.tv_pass_number.setVisibility(View.GONE);
                holder.tv_chcke_status.setText(context.getResources().getString(R.string.cb_alredy_check) +"  "+ station.checkNumber +
                        context.getResources().getString(R.string.cb_no_check) +"  "+ station.unCheckNumber);
                holder.tv_chcke_status.setVisibility(View.VISIBLE);
            }else if (station.status == 2){
                holder.tv_pass_number.setText((station.checkNumber + station.unCheckNumber) +"");
                holder.tv_pass_number.setVisibility(View.VISIBLE);
                holder.tv_chcke_status.setVisibility(View.GONE);
            }else if (station.status == 3){
                if (checkStatus){
                    holder.tv_pass_number.setVisibility(View.GONE);
                    holder.tv_chcke_status.setVisibility(View.VISIBLE);
                    holder.tv_chcke_status.setText(context.getResources().getString(R.string.cb_alredy_check) +"  "+ station.checkNumber +
                            context.getResources().getString(R.string.cb_no_check) +"  "+ station.unCheckNumber);
                }else {
                    holder.tv_pass_number.setText((station.checkNumber + station.unCheckNumber) +"");
                    holder.tv_pass_number.setVisibility(View.VISIBLE);
                    holder.tv_chcke_status.setVisibility(View.GONE);
                }
            }
            holder.tv_pass_number.setOnClickListener(v -> {
                if (onItemClickListener !=null){
                    onItemClickListener.onClick(position);
                }
            });
            holder.tv_chcke_status.setOnClickListener(v -> {
                if (onItemClickListener !=null){
                    onItemClickListener.onClick(position);
                }
            });
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
        TextView tv_chcke_status;

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
            tv_chcke_status= itemView.findViewById(R.id.tv_chcke_status);
        }
    }

    OnItemClickListener onItemClickListener;

    /**
     * 设置列表点击监听
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 子项点击接口
     */
    public interface OnItemClickListener {
        /**
         * 单击监听方法
         */
        void onClick(int position);
    }
}
