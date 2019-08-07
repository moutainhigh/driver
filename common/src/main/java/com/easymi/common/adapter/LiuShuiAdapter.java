package com.easymi.common.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.easymi.common.CommApiService;
import com.easymi.common.R;
import com.easymi.common.activity.BaoxiaoActivity;
import com.easymi.common.activity.LiushuiActivity;
import com.easymi.common.util.DJStatus2Str;
import com.easymi.common.util.ZXStatus2Str;
import com.easymi.common.widget.CommonDialog;
import com.easymi.component.BusOrderStatus;
import com.easymi.component.Config;
import com.easymi.component.DJOrderStatus;
import com.easymi.component.GWOrderStatus;
import com.easymi.component.PCOrderStatus;
import com.easymi.component.entity.BaseOrder;
import com.easymi.component.entity.ZCSetting;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.network.NoErrSubscriberListener;
import com.easymi.component.result.EmResult;
import com.easymi.component.utils.TimeUtil;
import com.easymi.component.widget.SwipeMenuLayout;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 *
 * @Author: hufeng
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class LiuShuiAdapter extends RecyclerView.Adapter<LiuShuiAdapter.Holder> {

    private Context context;

    private List<BaseOrder> baseOrders;

    public LiuShuiAdapter(Context context) {
        this.context = context;
        baseOrders = new ArrayList<>();
    }

    /**
     * 设置数据
     *
     * @param baseOrders
     */
    public void setBaseOrders(List<BaseOrder> baseOrders) {
        this.baseOrders = baseOrders;
        notifyDataSetChanged();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.liushui_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        BaseOrder baseOrder = baseOrders.get(position);
        String typeStr = null;
        if (baseOrder.serviceType.equals(Config.ZHUANCHE)) {
            typeStr = context.getResources().getString(R.string.create_zhuanche);
            holder.orderStatus.setText(DJStatus2Str.int2Str(baseOrder.serviceType, baseOrder.status));
        } else if (baseOrder.serviceType.equals(Config.TAXI)) {
            typeStr = context.getResources().getString(R.string.create_taxi);
            holder.orderStatus.setText(DJStatus2Str.int2Str(baseOrder.serviceType, baseOrder.status));
        } else if (baseOrder.serviceType.equals(Config.CITY_LINE)) {
            typeStr = context.getResources().getString(R.string.create_zhuanxian);
            holder.orderStatus.setText(ZXStatus2Str.int2Str(baseOrder.serviceType, baseOrder.status));
        } else if (baseOrder.serviceType.equals(Config.CHARTERED)) {
            typeStr = context.getResources().getString(R.string.create_chartered);
            holder.orderStatus.setText(DJStatus2Str.int2Str(baseOrder.serviceType, baseOrder.status));
        } else if (baseOrder.serviceType.equals(Config.RENTAL)) {
            typeStr = context.getResources().getString(R.string.create_rental);
            holder.orderStatus.setText(DJStatus2Str.int2Str(baseOrder.serviceType, baseOrder.status));
        } else if (baseOrder.serviceType.equals(Config.COUNTRY)) {
            typeStr = context.getResources().getString(R.string.create_bus_country);
            holder.orderStatus.setText(BusOrderStatus.orderStatus2Str(baseOrder.status));
        } else if (baseOrder.serviceType.equals(Config.CARPOOL)) {
            typeStr = context.getResources().getString(R.string.create_carpool);
            holder.orderStatus.setText(PCOrderStatus.status2Str(baseOrder.status));
        } else if (baseOrder.serviceType.equals(Config.GOV)) {
            typeStr = context.getResources().getString(R.string.create_gov);
            holder.orderStatus.setText(GWOrderStatus.status2Str(baseOrder.status));
        }
        holder.orderType.setText(typeStr);
        holder.orderEndPlace.setText(baseOrder.destination);
        holder.orderStartPlace.setText(baseOrder.bookAddress);
        holder.orderTime.setText(TimeUtil.getTime("yyyy-MM-dd HH:mm", baseOrder.bookTime * 1000));
        holder.orderNumber.setText(baseOrder.orderNo);
        holder.orderMoney.setText(String.valueOf(baseOrder.budgetFee));

        if (baseOrder.baoxiaoStatus == 0) {
            holder.orderBaoxiao.setClickable(true);
            holder.orderBaoxiao.setTextSize(14);
            holder.orderBaoxiao.setText(context.getString(R.string.liushui_baoxiao));
            holder.orderBaoxiao.setOnClickListener(view -> {
                LiushuiActivity.CLICK_POS = position + 1;
                Intent intent = new Intent(context, BaoxiaoActivity.class);
                intent.putExtra("orderId", baseOrder.orderId);
                ((Activity) context).startActivityForResult(intent, LiushuiActivity.CLICK_POS);
            });
        } else if (baseOrder.baoxiaoStatus == 1) {
            holder.orderBaoxiao.setClickable(false);
            holder.orderBaoxiao.setTextSize(10);
            holder.orderBaoxiao.setText(context.getString(R.string.liushui_baoxiao_sheheing));
        } else if (baseOrder.baoxiaoStatus == 2) {
            holder.orderBaoxiao.setClickable(false);
            holder.orderBaoxiao.setTextSize(14);
            holder.orderBaoxiao.setText(context.getString(R.string.liushui_baoxiao_done));
        } else if (baseOrder.baoxiaoStatus == 3) {
            holder.orderBaoxiao.setClickable(true);
            holder.orderBaoxiao.setTextSize(10);
            holder.orderBaoxiao.setText(context.getString(R.string.liushui_baoxiao_refuse));
            holder.orderBaoxiao.setOnClickListener(view -> {
                LiushuiActivity.CLICK_POS = position + 1;
                Intent intent = new Intent(context, BaoxiaoActivity.class);
                intent.putExtra("orderId", baseOrder.orderId);
                ((Activity) context).startActivityForResult(intent, LiushuiActivity.CLICK_POS);
            });
        }

        if (baseOrder.status == DJOrderStatus.ARRIVAL_DESTINATION_ORDER) {
            holder.orderLl.setClickable(true);
            holder.orderLl.setOnClickListener(v -> {
                LiushuiActivity.CLICK_POS = position + 1;
                if (baseOrder.serviceType.equals(Config.DAIJIA)) {
                    ARouter.getInstance().build("/daijia/FlowActivity")
                            .withLong("orderId", baseOrder.orderId)
                            .navigation((Activity) context, LiushuiActivity.CLICK_POS);
                } else if (baseOrder.serviceType.equals(Config.ZHUANCHE)) {
                    ARouter.getInstance().build("/zhuanche/FlowActivity")
                            .withLong("orderId", baseOrder.orderId)
                            .navigation((Activity) context, LiushuiActivity.CLICK_POS);
                }
            });
            holder.orderBaoxiao.setVisibility(View.GONE);
        } else {
            holder.orderLl.setClickable(false);

            if (ZCSetting.findOne().isExpenses == 1) {
                holder.orderBaoxiao.setVisibility(View.VISIBLE);
            } else {
                holder.orderBaoxiao.setVisibility(View.GONE);
            }
        }

        holder.orderTvDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.orderSml.smoothClose();
                createDialog(baseOrder.id);
            }
        });
    }

    private void createDialog(long id) {
        new CommonDialog(context, R.layout.dialog_del) {
            @Override
            public void initData(View view) {
                TextView dialogDelPos = view.findViewById(R.id.dialog_del_pos);
                TextView dialogDelNega = view.findViewById(R.id.dialog_del_nega);
                dialogDelNega.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });
                dialogDelPos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteData(id);
                        dismiss();
                    }
                });
            }
        }.show();
    }

    private void deleteData(long id) {
        ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .deleteOrder(id)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MySubscriber<EmResult>(context, true, false, new NoErrSubscriberListener<EmResult>() {
                    @Override
                    public void onNext(EmResult emResult) {
                        for (BaseOrder baseOrder : baseOrders) {
                            if (baseOrder.id == id) {
                                baseOrders.remove(baseOrder);
                                notifyDataSetChanged();
                                break;
                            }
                        }
                    }
                }));
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
        TextView orderNumber;
        TextView orderMoney;
        TextView orderBaoxiao;
        TextView orderTvDel;
        SwipeMenuLayout orderSml;
        LinearLayout orderLl;

        public Holder(View itemView) {
            super(itemView);
            orderLl = itemView.findViewById(R.id.order_ll);
            orderSml = itemView.findViewById(R.id.order_sml);
            orderTvDel = itemView.findViewById(R.id.order_tv_del);
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
