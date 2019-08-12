package com.easymin.custombus.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easymin.custombus.R;
import com.easymin.custombus.entity.StationBean;

import java.util.ArrayList;
import java.util.List;

public class DialogQueryStationAdapter extends RecyclerView.Adapter<DialogQueryStationAdapter.ViewHolder> {

    private Context context;
    private List<StationBean> list;
    private boolean isStart;
    private StationBean startBean;
    private StationBean endBean;


    public DialogQueryStationAdapter(Context context, boolean start) {
        this.context = context;
        list = new ArrayList<>();
        isStart = start;
    }

    public void setData(List<StationBean> list) {
        if (isStart) {
            if (startBean != null) {
                for (StationBean stationBean : list) {
                    if (stationBean.id == startBean.id) {
                        stationBean.chooseStatus = 1;
                    } else {
                        stationBean.chooseStatus = 0;
                    }
                }
            } else {
                for (int i = 0; i < list.size(); i++) {
                    if (i == 0) {
                        list.get(i).chooseStatus = 1;
                    } else {
                        list.get(i).chooseStatus = 0;
                    }
                }
            }
        } else {
            if (endBean != null) {
                for (StationBean stationBean : list) {
                    if (stationBean.id == endBean.id) {
                        stationBean.chooseStatus = 2;
                    } else {
                        stationBean.chooseStatus = 0;
                    }
                }
            } else {
                for (int i = 0; i < list.size(); i++) {
                    if (i == list.size() - 1) {
                        list.get(i).chooseStatus = 2;
                    } else {
                        list.get(i).chooseStatus = 0;
                    }
                }
            }
        }
        this.list = list;
        notifyDataSetChanged();
    }

    public void setBean(StationBean startBean, StationBean endBean) {
        this.startBean = startBean;
        this.endBean = endBean;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.dialog_query_station_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StationBean bean = list.get(position);
        int holderPosition = holder.getAdapterPosition();
        holder.dialogQueryStationItemRl.setOnClickListener(v -> {
            for (StationBean stationBean : list) {
                stationBean.chooseStatus = 0;
            }
            if (isStart) {
                bean.chooseStatus = 1;
            } else {
                bean.chooseStatus = 2;
            }
            notifyDataSetChanged();
        });
        if ((isStart && bean.onOff == 2)
                || (!isStart && bean.onOff == 1)
                || (!isStart && startBean != null && bean.sequence <= startBean.sequence)) {
            holder.dialogQueryStationItemTvTitle.setTextColor(ContextCompat.getColor(context, R.color.colorDesc));
            holder.dialogQueryStationItemTvSubtitle.setTextColor(ContextCompat.getColor(context, R.color.colorDesc));
            holder.dialogQueryStationItemRl.setClickable(false);
        } else {
            holder.dialogQueryStationItemTvTitle.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
            holder.dialogQueryStationItemTvSubtitle.setTextColor(ContextCompat.getColor(context, R.color.colorSub));
            holder.dialogQueryStationItemRl.setClickable(true);
        }
        holder.dialogQueryStationItemTvTitle.setText(bean.name);
        holder.dialogQueryStationItemTvSubtitle.setText(bean.address);


        if (((isStart && bean.chooseStatus == 1) || (!isStart && bean.chooseStatus == 2))) {
            holder.dialogQueryStationItemIv.setImageResource(isStart ? R.mipmap.c_up_station : R.mipmap.c_down_station);
        } else {
            holder.dialogQueryStationItemIv.setImageBitmap(null);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView dialogQueryStationItemTvTitle;
        TextView dialogQueryStationItemTvSubtitle;
        ImageView dialogQueryStationItemIv;
        RelativeLayout dialogQueryStationItemRl;

        public ViewHolder(View itemView) {
            super(itemView);
            dialogQueryStationItemRl = itemView.findViewById(R.id.dialog_query_station_item_rl);
            dialogQueryStationItemTvTitle = itemView.findViewById(R.id.dialog_query_station_item_tv_title);
            dialogQueryStationItemTvSubtitle = itemView.findViewById(R.id.dialog_query_station_item_tv_subtitle);
            dialogQueryStationItemIv = itemView.findViewById(R.id.dialog_query_station_item_iv);
        }
    }

}