package com.easymi.common.mvp.work;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.amap.api.maps.MapView;
import com.easymi.common.R;
import com.easymi.common.activity.CreateActivity;
import com.easymi.common.adapter.OrderAdapter;
import com.easymi.common.entity.BaseOrder;
import com.easymi.common.mvp.grab.GrabActivity;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.Employ;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.widget.BottomBehavior;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.pinned.PinnedHeaderDecoration;
import com.skyfishjy.library.RippleBackground;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by developerLzh on 2017/11/3 0003.
 */

@Route(path = "/common/WorkActivity")
public class WorkActivity extends RxBaseActivity implements WorkContract.View {

    LinearLayout bottomBar;

    MapView mapView;

    RippleBackground rippleBackground;

    BottomBehavior behavior;

    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;

    CusToolbar toolbar;

    LinearLayout createOrder;

    ImageView pullIcon;

    private WorkPresenter presenter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_work;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {

        findById();
        initToolbar();

        mapView.onCreate(savedInstanceState);

        behavior = BottomBehavior.from(bottomBar);

        rippleBackground.startRippleAnimation();

        rippleBackground.setOnClickListener(v -> startActivity(new Intent(WorkActivity.this, GrabActivity.class)));

        createOrder.setOnClickListener(v -> {
            Intent intent = new Intent(WorkActivity.this, CreateActivity.class);
            startActivity(intent);
        });

        presenter = new WorkPresenter(this, this);

        initRecycler();

        Employ employ = Employ.findByID(XApp.getMyPreferences().getLong("driverId", -1));
        Log.e("employ", employ.toString());
    }

    private OrderAdapter adapter;

    @Override
    public void findById() {
        toolbar = findViewById(R.id.toolbar);
        bottomBar = findViewById(R.id.bottom_bar);
        mapView = findViewById(R.id.map_view);
        rippleBackground = findViewById(R.id.ripple_ground);
        recyclerView = findViewById(R.id.recyclerView);
        createOrder = findViewById(R.id.create_order);
        swipeRefreshLayout = findViewById(R.id.swipe_layout);
        pullIcon = findViewById(R.id.pull_icon);
    }

    /**
     * 初始化Toolbar
     */
    @Override
    public void initToolbar() {
        toolbar.setLeftIcon(View.VISIBLE, R.mipmap.drawer_icon, view -> {
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
    }

    /**
     * 初始化recycler
     */
    @Override
    public void initRecycler() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        swipeRefreshLayout.setOnRefreshListener(() -> presenter.indexOrders());
        swipeRefreshLayout.setRefreshing(true);
        presenter.indexOrders();
    }

    List<BaseOrder> orders = new ArrayList<>();

    @Override
    public void showOrders(List<BaseOrder> baseOrders) {
        swipeRefreshLayout.setRefreshing(false);
        orders.clear();
        if (baseOrders == null) {
            BaseOrder header1 = new BaseOrder(BaseOrder.ITEM_HEADER);
            header1.isBookOrder = 1;
            orders.add(header1);

            //即时header
            BaseOrder header2 = new BaseOrder(BaseOrder.ITEM_HEADER);
            header1.isBookOrder = 2;
            orders.add(header2);
        } else {
            orders.addAll(baseOrders);
        }
        if (null == adapter) {
            adapter = new OrderAdapter(baseOrders);
            recyclerView.setAdapter(adapter);
            PinnedHeaderDecoration pinnedHeaderDecoration = new PinnedHeaderDecoration();
            //设置只有RecyclerItem.ITEM_HEADER的item显示标签
            pinnedHeaderDecoration.setPinnedTypeHeader(BaseOrder.ITEM_HEADER);
            pinnedHeaderDecoration.registerTypePinnedHeader(BaseOrder.ITEM_HEADER, (parent, adapterPosition) -> true);
            pinnedHeaderDecoration.registerTypePinnedHeader(BaseOrder.ITEM_DESC, (parent, adapterPosition) -> true);
            recyclerView.addItemDecoration(pinnedHeaderDecoration);
        } else {
            adapter.setNewData(orders);
        }
    }

    @Override
    public void initMap() {

    }

    @Override
    public RxManager getRxManager() {
        return mRxManager;
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
            RotateAnimation animation = new RotateAnimation(0f,180f);
            animation.setDuration(1000);
            pullIcon.startAnimation(animation);
        } else {
            behavior.setState(BottomBehavior.STATE_COLLAPSED);
            RotateAnimation animation = new RotateAnimation(180f,0f);
            animation.setDuration(1000);
            pullIcon.startAnimation(animation);
        }
    }

}
