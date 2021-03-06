package com.easymi.personal.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.PoiItem;
import com.easymi.component.activity.NaviActivity;
import com.easymi.component.utils.EmUtil;
import com.easymi.personal.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: NearWcAdapter
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class NearWcAdapter extends RecyclerView.Adapter<NearWcAdapter.Holder> {

    private Context context;
    private List<PoiItem> items;

    public interface OnItemClickLis{
        /**
         * 点击监听
         * @param poiItem
         */
        void onItemClick(PoiItem poiItem);
    }

    public OnItemClickLis onItemClickLis;

    public void setOnItemClickLis(OnItemClickLis onItemClickLis) {
        this.onItemClickLis = onItemClickLis;
    }

    public NearWcAdapter(Context context) {
        this.context = context;
        items = new ArrayList<>();
    }

    /**
     * 设置适配器数据
     * @param items
     */
    public void setItems(List<PoiItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wc, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        PoiItem item = items.get(position);
        holder.wcName.setText(item.getTitle());
        holder.wcAddress.setText(item.getSnippet());

        String dis = "";
        if (item.getDistance() < 1000) {
            dis = item.getDistance() + context.getString(R.string.meter);
        } else {
            double km = (double) item.getDistance() / (double) 1000;
            dis = new DecimalFormat("0.0").format(km) + context.getString(R.string.k_meter);
        }
        holder.wcDis.setText(dis);
        holder.naviIcon.setOnClickListener(view -> {
            NaviLatLng start = new NaviLatLng(EmUtil.getLastLoc().latitude, EmUtil.getLastLoc().longitude);
            NaviLatLng end = new NaviLatLng(item.getLatLonPoint().getLatitude(), item.getLatLonPoint().getLongitude());
            Intent intent = new Intent(context, NaviActivity.class);
            intent.putExtra("startLatlng", start);
            intent.putExtra("endLatlng", end);
            context.startActivity(intent);
        });
        if(null != onItemClickLis){
            holder.root.setOnClickListener(view -> {
                if(null != onItemClickLis){
                    onItemClickLis.onItemClick(item);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        TextView wcName;
        TextView wcAddress;
        TextView wcDis;
        ImageView naviIcon;
        View root;

        public Holder(View itemView) {
            super(itemView);
            root = itemView;
            wcName = itemView.findViewById(R.id.wc_name);
            wcAddress = itemView.findViewById(R.id.wc_address);
            wcDis = itemView.findViewById(R.id.wc_dis);
            naviIcon = itemView.findViewById(R.id.navi_icon);
        }
    }
}
