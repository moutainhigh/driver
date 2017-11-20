package com.easymi.common.mvp.work;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.easymi.common.R;
import com.easymi.common.activity.CreateActivity;
import com.easymi.common.adapter.OrderAdapter;
import com.easymi.common.entity.BaseOrder;
import com.easymi.common.mvp.grab.GrabActivity;
import com.easymi.component.Config;
import com.easymi.component.LocService;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.EmLoc;
import com.easymi.component.entity.Employ;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.widget.BottomBehavior;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.pinned.PinnedHeaderDecoration;
import com.google.gson.Gson;
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

    ImageView refreshImg;
    FrameLayout loadingFrame;
    ImageView loadingImg;

    private WorkPresenter presenter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_work;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {

        presenter = new WorkPresenter(this, this);

        findById();
        initToolbar();

        initMap();

        mapView.onCreate(savedInstanceState);

        behavior = BottomBehavior.from(bottomBar);

        rippleBackground.startRippleAnimation();

        rippleBackground.setOnClickListener(v -> startActivity(new Intent(WorkActivity.this, GrabActivity.class)));

        createOrder.setOnClickListener(v -> {
            Intent intent = new Intent(WorkActivity.this, CreateActivity.class);
            startActivity(intent);
        });

        initRecycler();

        Employ employ = Employ.findByID(XApp.getMyPreferences().getLong("driverId", -1));
//        Log.e("employ", employ.toString());
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

        loadingFrame = findViewById(R.id.loading_frame);
        loadingImg = findViewById(R.id.spinnerImageView);
        refreshImg = findViewById(R.id.refresh_img);
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

    private AMap aMap;

    @Override
    public void initMap() {
        aMap = mapView.getMap();
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.getUiSettings().setRotateGesturesEnabled(false);
        aMap.getUiSettings().setRotateGesturesEnabled(false);
        aMap.getUiSettings().setTiltGesturesEnabled(false);//倾斜手势

        initRefreshBtn();

        refreshImg.callOnClick();
    }

    @Override
    public void initRefreshBtn() {
        refreshImg.setOnClickListener(v -> {
            refreshImg.setVisibility(View.INVISIBLE);
            loadingFrame.setVisibility(View.VISIBLE);
            AnimationDrawable spinner = (AnimationDrawable) loadingImg.getBackground();
            spinner.start();
            presenter.startLocService(this);
        });

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

    LocReceiver locReceiver;

    @Override
    protected void onStart() {
        super.onStart();
        locReceiver = new LocReceiver();
        IntentFilter filter = new IntentFilter(LocService.LOC_CHANGED);
        registerReceiver(locReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(locReceiver);
    }

    public void mapHideShow(View view) {
        if (behavior.getState() == BottomBehavior.STATE_COLLAPSED) {
            behavior.setState(BottomBehavior.STATE_EXPANDED);
            RotateAnimation rotateAnimation = new RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF, 0.5F,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setDuration(500);
            rotateAnimation.setFillAfter(true); //旋转后停留在这个状态
            pullIcon.startAnimation(rotateAnimation);
        } else {
            behavior.setState(BottomBehavior.STATE_COLLAPSED);
            RotateAnimation rotateAnimation = new RotateAnimation(180f, 0f, Animation.RELATIVE_TO_SELF, 0.5F,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setDuration(500);
            rotateAnimation.setFillAfter(true); //旋转后停留在这个状态
            pullIcon.startAnimation(rotateAnimation);
        }
    }

    private EmLoc location;

    private Marker myLocMarker;


    class LocReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            location = new Gson().fromJson(XApp.getMyPreferences().getString(Config.SP_LAST_LOC, ""), EmLoc.class);
            LatLng latLng = new LatLng(location.latitude, location.longitude);
            if (null == myLocMarker) {
                MarkerOptions markerOption = new MarkerOptions();
                markerOption.position(latLng);
                markerOption.draggable(false);//设置Marker可拖动
                markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(), R.mipmap.ic_my_loc)));
                // 将Marker设置为贴地显示，可以双指下拉地图查看效果
                markerOption.setFlat(true);//设置marker平贴地图效果
                myLocMarker = aMap.addMarker(markerOption);
            } else {
                myLocMarker.setPosition(latLng);
            }

            if (loadingFrame.getVisibility() == View.VISIBLE) {
                ((AnimationDrawable) loadingImg.getBackground()).stop();
                loadingFrame.setVisibility(View.INVISIBLE);
                refreshImg.setVisibility(View.VISIBLE);

                aMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        }
    }


}
