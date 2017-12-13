package com.easymi.common.mvp.work;

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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.easymi.common.R;
import com.easymi.common.activity.CreateActivity;
import com.easymi.common.adapter.OrderAdapter;
import com.easymi.common.entity.Announcement;
import com.easymi.common.entity.BaseOrder;
import com.easymi.common.entity.NearDriver;
import com.easymi.common.entity.Notifity;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.EmLoc;
import com.easymi.component.loc.LocReceiver;
import com.easymi.component.loc.LocService;
import com.easymi.component.loc.ReceiveLocInterface;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.MapUtil;
import com.easymi.component.widget.BottomBehavior;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.LoadingButton;
import com.easymi.component.widget.pinned.PinnedHeaderDecoration;
import com.skyfishjy.library.RippleBackground;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by developerLzh on 2017/11/3 0003.
 */

@Route(path = "/common/WorkActivity")
public class WorkActivity extends RxBaseActivity implements WorkContract.View, ReceiveLocInterface {

    LinearLayout bottomBar;

    MapView mapView;

    RippleBackground rippleBackground;

    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;

    CusToolbar toolbar;

    LinearLayout createOrder;

    ImageView pullIcon;

    ImageView refreshImg;
    FrameLayout loadingFrame;
    ImageView loadingImg;

    LinearLayout offlineCon;

    LoadingButton onLineBtn;

    RelativeLayout listenOrderCon;

    RelativeLayout notifityCon;
    TextView notifityContent;
    ImageView notifityClose;

    TextView currentPlace;

    ExpandableLayout expandableLayout;

    private WorkPresenter presenter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_work;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {

        presenter = new WorkPresenter(this, this);

        findById();

        initMap();
        initNotifity();

        mapView.onCreate(savedInstanceState);

        createOrder.setOnClickListener(v -> {
            Intent intent = new Intent(WorkActivity.this, CreateActivity.class);
            startActivity(intent);
        });

        initRecycler();

        onLineBtn.setOnClickListener(view -> {
            onLineBtn.setClickable(false);
            onLineBtn.setStatus(LoadingButton.STATUS_LOADING);
            presenter.online();
        });
        offlineCon.setOnClickListener(v -> presenter.offline());

    }

    private void initNotifity() {
        notifityClose.setOnClickListener(v -> notifityCon.setVisibility(View.GONE));

        //模拟收到推送去查询通知
        presenter.loadNotice(1);

        //模拟收到推送去查询公告
        presenter.loadAnn(1);

    }

    private OrderAdapter adapter;

    @Override
    public void findById() {
        bottomBar = findViewById(R.id.bottom_bar);
        mapView = findViewById(R.id.map_view);
        rippleBackground = findViewById(R.id.ripple_ground);
        recyclerView = findViewById(R.id.recyclerView);
        createOrder = findViewById(R.id.create_order);
        swipeRefreshLayout = findViewById(R.id.swipe_layout);
        pullIcon = findViewById(R.id.pull_icon);

        listenOrderCon = findViewById(R.id.listen_order_con);
        onLineBtn = findViewById(R.id.online_btn);

        loadingFrame = findViewById(R.id.loading_frame);
        loadingImg = findViewById(R.id.spinnerImageView);
        refreshImg = findViewById(R.id.refresh_img);

        notifityCon = findViewById(R.id.notifity_con);
        notifityContent = findViewById(R.id.notifity_content);
        notifityClose = findViewById(R.id.ic_close);

        offlineCon = findViewById(R.id.offline);
        currentPlace = findViewById(R.id.current_place);

        expandableLayout = findViewById(R.id.map_expand);
    }

    @Override
    public void initToolBar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setLeftIcon(R.drawable.ic_person_white_24dp, view -> {
            ARouter.getInstance()
                    .build("/personal/PersonalActivity")
                    .navigation();
        });
        toolbar.setTitle(R.string.work_title);
        toolbar.setRightIcon(R.drawable.ic_more_icon, view -> {
            ARouter.getInstance()
                    .build("/personal/MoreActivity")
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
    public void onlineSuc() {
        onLineBtn.setClickable(true);
        onLineBtn.setStatus(LoadingButton.STATUS_NORMAL);
        listenOrderCon.setVisibility(View.VISIBLE);
        rippleBackground.startRippleAnimation();
        onLineBtn.setVisibility(View.GONE);
    }

    @Override
    public void offlineSuc() {
        listenOrderCon.setVisibility(View.GONE);
        rippleBackground.stopRippleAnimation();
        onLineBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void showNotify(Notifity notifity) {
        notifityCon.setVisibility(View.VISIBLE);
        notifityContent.setText(getString(R.string.new_notify) + notifity.message);
        XApp.getInstance().syntheticVoice(getString(R.string.new_notify) + notifity.message, true);
        notifityCon.setOnClickListener(v -> {
            notifityCon.setVisibility(View.GONE);
            ARouter.getInstance().build("/personal/NotifityActivity")
                    .navigation();
        });
    }

    List<Marker> markers = new ArrayList<>();

    @Override
    public void showDrivers(List<NearDriver> drivers) {
        for (Marker marker : markers) {
            marker.remove();
        }
        markers.clear();
        MarkerOptions options = new MarkerOptions();
        options.draggable(false);//设置Marker可拖动
        options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(), R.mipmap.ic_driver)));
        // 将Marker设置为贴地显示，可以双指下拉地图查看效果
        options.setFlat(true);//设置marker平贴地图效果
        for (NearDriver driver : drivers) {
            options.position(new LatLng(driver.lat, driver.lng));
            Marker marker = aMap.addMarker(options);
            markers.add(marker);
        }
        List<LatLng> latLngs = new ArrayList<>();
        for (NearDriver driver : drivers) {
            LatLng latLng = new LatLng(driver.lat, driver.lng);
            latLngs.add(latLng);
        }
        LatLng center = new LatLng(EmUtil.getLastLoc().latitude, EmUtil.getLastLoc().longitude);
        LatLngBounds bounds = MapUtil.getBounds(latLngs, center);
        aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
    }

    @Override
    public void showAnn(Announcement announcement) {
        notifityCon.setVisibility(View.VISIBLE);
        notifityContent.setText(getString(R.string.new_ann) + announcement.message);
        XApp.getInstance().syntheticVoice(getString(R.string.new_ann) + announcement.message, true);
        notifityCon.setOnClickListener(v -> {
            notifityCon.setVisibility(View.GONE);
            ARouter.getInstance().build("/personal/AnnouncementActivity")
                    .navigation();
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
        refreshImg.callOnClick();
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
        locReceiver = new LocReceiver(this);
        IntentFilter filter = new IntentFilter(LocService.LOC_CHANGED);
        registerReceiver(locReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(locReceiver);
    }

    public void mapHideShow(View view) {
        if (!expandableLayout.isExpanded()) {
            expandableLayout.expand();
            RotateAnimation rotateAnimation = new RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF, 0.5F,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setDuration(500);
            rotateAnimation.setFillAfter(true); //旋转后停留在这个状态
            pullIcon.startAnimation(rotateAnimation);
        } else {
            expandableLayout.collapse();
            RotateAnimation rotateAnimation = new RotateAnimation(180f, 0f, Animation.RELATIVE_TO_SELF, 0.5F,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setDuration(500);
            rotateAnimation.setFillAfter(true); //旋转后停留在这个状态
            pullIcon.startAnimation(rotateAnimation);
        }
    }

    private EmLoc location;

    private Marker myLocMarker;

    @Override
    public void receiveLoc() {
        EmLoc location = EmUtil.getLastLoc();
        if (null == location) {
            return;
        }
        LatLng latLng = new LatLng(location.latitude, location.longitude);
        currentPlace.setText(getString(R.string.current_place)
                + location.street + "  " + location.poiName);
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

        if (loadingFrame.getVisibility() == View.VISIBLE) { //重新启动了定位服务
            ((AnimationDrawable) loadingImg.getBackground()).stop();
            loadingFrame.setVisibility(View.INVISIBLE);
            refreshImg.setVisibility(View.VISIBLE);

            aMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            presenter.queryNearDriver(location.latitude, location.longitude);
        } else {
            if (this.location != null) {
                LatLng last = new LatLng(this.location.latitude, this.location.longitude);
                LatLng current = new LatLng(location.latitude, location.longitude);
                double dis = AMapUtils.calculateLineDistance(last, current);
                if (dis > 30) {//大于30米重新加载司机
                    presenter.queryNearDriver(location.latitude, location.longitude);
                }
            }
        }

        this.location = location;
    }

    @Override
    public void onBackPressed() {
        if (expandableLayout.isExpanded()) {
            expandableLayout.collapse();
            RotateAnimation rotateAnimation = new RotateAnimation(180f, 0f, Animation.RELATIVE_TO_SELF, 0.5F,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setDuration(500);
            rotateAnimation.setFillAfter(true); //旋转后停留在这个状态
            pullIcon.startAnimation(rotateAnimation);
        } else {
            super.onBackPressed();
        }
    }
}
