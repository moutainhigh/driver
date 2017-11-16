package com.easymi.common.mvp.work;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.amap.api.maps.MapView;
import com.easymi.common.R;
import com.easymi.common.activity.CreateActivity;
import com.easymi.common.adapter.OrderAdapter;
import com.easymi.common.entity.Order;
import com.easymi.common.mvp.grab.GrabActivity;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.Employ;
import com.easymi.component.rxmvp.BaseView;
import com.easymi.component.widget.BottomBehavior;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.SwipeRecyclerView;
import com.skyfishjy.library.RippleBackground;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by developerLzh on 2017/11/3 0003.
 */

@Route(path = "/common/WorkActivity")
public class WorkActivity extends RxBaseActivity implements BaseView {

    LinearLayout bottomBar;

    MapView mapView;

    RippleBackground rippleBackground;

    BottomBehavior behavior;

    SwipeRecyclerView recyclerView;

    CusToolbar toolbar;

    LinearLayout createOrder;

    @Override
    public int getLayoutId() {
        return R.layout.activity_work;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {

        toolbar = findViewById(R.id.toolbar);
        bottomBar = findViewById(R.id.bottom_bar);
        mapView = findViewById(R.id.map_view);
        rippleBackground = findViewById(R.id.ripple_ground);
        recyclerView = findViewById(R.id.recyclerView);
        createOrder = findViewById(R.id.create_order);

        toolbar.setLeftIcon(View.VISIBLE, R.mipmap.drawer_icon, view -> {
//跳转到启动页面
            ARouter.getInstance()
                    .build("/personal/PersonalActivity")
                    .navigation();
        });
        toolbar.setTitle(R.string.work_title);
        toolbar.setRightIcon(View.VISIBLE, R.mipmap.menu_icon, view -> {
            ARouter.getInstance()
                    .build("/personal/SetActivity")
                    .navigation();
        });

        behavior = BottomBehavior.from(bottomBar);

        mapView.onCreate(savedInstanceState);

        rippleBackground.startRippleAnimation();

        rippleBackground.setOnClickListener(v -> startActivity(new Intent(WorkActivity.this, GrabActivity.class)));

        createOrder.setOnClickListener(v -> {
            Intent intent = new Intent(WorkActivity.this, CreateActivity.class);
            startActivity(intent);
        });


        initRecycler();

        Employ employ = Employ.findByID(XApp.getMyPreferences().getLong("driverId",-1));
        Log.e("employ",employ.toString());
    }

    private OrderAdapter adapter;

    private void initRecycler() {
        adapter = new OrderAdapter(this);
        recyclerView.setAdapter(adapter);

        recyclerView.getRecyclerView().setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
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
        recyclerView.setLoadMoreEnable(false);
        adapter.setOrders(initRecyclerData());
    }

    private List<Order> initRecyclerData() {
        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Order order = new Order();
            order.orderId = 1L;
            order.orderEndPlace = "锦绣大道南段99号";
            order.orderStartPlace = "花样年花样城5期";
            order.orderStatus = "执行中 >";
            order.orderTime = "11月14日 19:00";
            order.orderType = "日常代驾";
            orders.add(order);
        }
        return orders;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    public void mapHideShow(View view) {
        if (behavior.getState() == BottomBehavior.STATE_COLLAPSED) {
            behavior.setState(BottomBehavior.STATE_EXPANDED);
        } else {
            behavior.setState(BottomBehavior.STATE_COLLAPSED);
        }
    }

}
