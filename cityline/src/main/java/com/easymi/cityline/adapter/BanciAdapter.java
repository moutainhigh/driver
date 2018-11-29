package com.easymi.cityline.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.easymi.cityline.R;
import com.easymi.cityline.entity.ZXOrder;
import com.easymi.component.entity.TaxiSetting;
import com.easymi.component.entity.ZCSetting;
import com.easymi.component.utils.TimeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuzihao on 2017/11/14.
 */

public class BanciAdapter extends RecyclerView.Adapter<BanciAdapter.Holder> {

    private final boolean canBaoxiaoDJ;
    private final boolean canBaoxiaoZC;
    private Context context;

    private List<ZXOrder> baseOrders;

    OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public BanciAdapter(Context context) {
        this.context = context;
        baseOrders = new ArrayList<>();
        canBaoxiaoDJ = TaxiSetting.findOne().isExpenses == 1;
        canBaoxiaoZC = ZCSetting.findOne().isExpenses == 1;
    }

    public void setBaseOrders(List<ZXOrder> baseOrders) {
        this.baseOrders = baseOrders;
        notifyDataSetChanged();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.banci_item, null);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        ZXOrder zxOrder = baseOrders.get(position);
        holder.orderType.setText("城际专线");
        holder.orderTime.setText(TimeUtil.getTime("yyyy-MM-dd HH:mm", zxOrder.startOutTime * 1000));
        holder.orderStartPlace.setText(zxOrder.startSite);
        holder.orderEndPlace.setText(zxOrder.endSite);
        holder.orderStatus.setText(zxOrder.getOrderStatusStr());
        if(null != onItemClickListener){
            holder.itemView.setOnClickListener(view -> onItemClickListener.onClick(zxOrder));
        }
    }

    @Override
    public int getItemCount() {
        return baseOrders.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        TextView orderTime;
        TextView orderStatus;
        TextView orderStartPlace;
        TextView orderEndPlace;
        TextView orderType;
        View rootView;

        public Holder(View itemView) {
            super(itemView);
            rootView = itemView;
            orderTime = itemView.findViewById(R.id.order_time);
            orderStatus = itemView.findViewById(R.id.order_status);
            orderStartPlace = itemView.findViewById(R.id.order_start_place);
            orderEndPlace = itemView.findViewById(R.id.order_end_place);
            orderType = itemView.findViewById(R.id.order_type);

        }
    }

    public interface OnItemClickListener {
        void onClick(ZXOrder zxOrder);
    }
}
