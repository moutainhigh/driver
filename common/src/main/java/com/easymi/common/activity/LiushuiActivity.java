package com.easymi.common.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.easymi.common.R;
import com.easymi.common.adapter.LiuShuiAdapter;
import com.easymi.common.entity.Order;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.widget.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuzihao on 2017/11/14.
 */
@Route(path = "/common/LiushuiActivity")
public class LiushuiActivity extends RxBaseActivity {

    SwipeRecyclerView recyclerView;

    LiuShuiAdapter adapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_liushui;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new LiuShuiAdapter(this);

        recyclerView.getRecyclerView().setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        recyclerView.setAdapter(adapter);

        recyclerView.setOnLoadListener(new SwipeRecyclerView.OnLoadListener() {
            @Override
            public void onRefresh() {
                adapter.setOrders(initRecyclerData());
                recyclerView.complete();
            }

            @Override
            public void onLoadMore() {

            }
        });
    }

    private List<Order> initRecyclerData(){
        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Order order = new Order();
            order.orderEndPlace = "锦绣大道南段99号";
            order.orderStartPlace = "花样年花样城5期";
            order.orderStatus = "执行中 >";
            order.orderTime = "11月14日 19:00";
            order.orderType = "代驾";
            order.orderNumber = "DJ101111";
            order.orderPrice = 12.5;
            orders.add(order);
        }
        return orders;
    }
}
