package com.easymi.common.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.easymi.common.R;
import com.easymi.common.entity.Order;
import com.easymi.component.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuzihao on 2017/11/14.
 */

public class LiuShuiAdapter extends  RecyclerView.Adapter<LiuShuiAdapter.Holder> {

    private Context context;

    private List<Order> orders;

    public LiuShuiAdapter(Context context) {
        this.context = context;
        orders = new ArrayList<>();
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.liushui_item,null);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        Order order = orders.get(position);
        holder.orderType.setText(order.orderType);
        holder.orderEndPlace.setText(order.orderEndPlace);
        holder.orderStartPlace.setText(order.orderStartPlace);
        holder.orderStatus.setText(order.orderStatus);
        holder.orderTime.setText(order.orderTime);
        holder.orderMoney.setText(order.orderPrice+"");
        holder.orderNumber.setText(order.orderNumber);
        holder.orderBaoxiao.setOnClickListener(view -> ToastUtil.showMessage(context,"点击了报销"));

    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    class Holder extends RecyclerView.ViewHolder{

        TextView orderTime;
        TextView orderStatus;
        TextView orderStartPlace;
        TextView orderEndPlace;
        TextView orderType;
        TextView orderNumber;
        TextView orderMoney;
        TextView orderBaoxiao;

        public Holder(View itemView) {
            super(itemView);
            orderTime = itemView.findViewById(R.id.order_time);
            orderStatus = itemView.findViewById(R.id.order_status);
            orderStartPlace = itemView.findViewById(R.id.order_start_place);
            orderEndPlace = itemView.findViewById(R.id.order_end_place);
            orderType = itemView.findViewById(R.id.order_type);
            orderNumber = itemView.findViewById(R.id.order_no);
            orderMoney = itemView.findViewById(R.id.order_money);
            orderBaoxiao = itemView.findViewById(R.id.order_baoxiao);

        }
    }
}
