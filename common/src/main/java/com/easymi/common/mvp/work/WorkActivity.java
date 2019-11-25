package com.easymi.common.mvp.work;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
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
import com.easymi.common.entity.AnnAndNotice;
import com.easymi.common.entity.ManualConfigBean;
import com.easymi.common.entity.MqttReconnectEvent;
import com.easymi.common.entity.MultipleOrder;
import com.easymi.common.entity.NearDriver;
import com.easymi.common.mvp.order.OrderActivity;
import com.easymi.common.push.CountEvent;
import com.easymi.common.push.MqttManager;
import com.easymi.common.receiver.AnnReceiver;
import com.easymi.common.receiver.CancelOrderReceiver;
import com.easymi.common.receiver.EmployStatusChangeReceiver;
import com.easymi.common.receiver.NoticeReceiver;
import com.easymi.common.receiver.OrderRefreshReceiver;
import com.easymi.common.widget.CommonDialog;
import com.easymi.common.widget.NearInfoWindowAdapter;
import com.easymi.component.Config;
import com.easymi.component.EmployStatus;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.EmLoc;
import com.easymi.component.entity.Employ;
import com.easymi.component.entity.Vehicle;
import com.easymi.component.loc.LocObserver;
import com.easymi.component.loc.LocReceiver;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.Log;
import com.easymi.component.utils.MapUtil;
import com.easymi.component.utils.PhoneUtil;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.LoadingButton;
import com.easymi.component.widget.SwipeRecyclerView;
import com.easymi.component.widget.pinned.PinnedHeaderDecoration;
import com.google.gson.Gson;
import com.skyfishjy.library.RippleBackground;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 *
 * @Author: hufeng
 * Date: 2018/9/24 下午5:00
 * Description: 工作台主界面
 * History:
 */

@Route(path = "/common/WorkActivity")
public class WorkActivity extends RxBaseActivity implements WorkContract.View,
        LocObserver, CancelOrderReceiver.OnCancelListener,
        EmployStatusChangeReceiver.OnStatusChangeListener,
        AMap.OnMarkerClickListener, AMap.OnMapClickListener,
        NoticeReceiver.OnReceiveNotice,
        AnnReceiver.OnReceiveAnn,
        OrderRefreshReceiver.OnRefreshOrderListener {

    LinearLayout bottomBar;
    MapView mapView;
    RippleBackground rippleBackground;
    SwipeRecyclerView swipeRefreshLayout;
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
    TextView finishNo;
    TextView onLineHour;
    TextView onLineMonute;
    TextView todayIncome;
    TextView noOrderText;
    LinearLayout bottomBtnCon;
    Button btCreate;

    /**
     * 取消订单
     */
    private CancelOrderReceiver cancelOrderReceiver;
    /**
     * 司机状态
     */
    private EmployStatusChangeReceiver employStatusChangeReceiver;

    /**
     * 通知
     */
    private NoticeReceiver noticeReceiver;
    /**
     * 公告
     */
    private AnnReceiver annReceiver;
    /**
     * 刷新订单
     */
    private OrderRefreshReceiver orderRefreshReceiver;

    private WorkPresenter presenter;
    private TextView tvTitle;
    private TextView moneyDesc;
    private ImageView workIvManual;
    private TextView workTvOffline;

    @Override
    public int getLayoutId() {
        return R.layout.activity_work;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        // 屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        presenter = new WorkPresenter(this, this);

        findById();

        initMap();
        initNotifity();
        XApp.getInstance().initBaiduTTs();
        mapView.onCreate(savedInstanceState);

        createOrder.setOnClickListener(v -> {
            Intent intent = new Intent(WorkActivity.this, CreateActivity.class);
            startActivity(intent);
        });

        initRecycler();

        onLineBtn.setOnClickListener(view -> {
            if (TextUtils.equals(EmUtil.getEmployInfo().serviceType,Config.CARPOOL) && EmUtil.getEmployInfo().countNoSchedule > 0){
                presenter.queryPCLine(1);
            }else {
                onLineBtn.setClickable(false);
                onLineBtn.setStatus(LoadingButton.STATUS_LOADING);
                presenter.online(onLineBtn);
            }
        });
        workTvOffline.setOnClickListener(v -> {
            if (Config.IS_ENCRYPT && TextUtils.equals(Config.APP_KEY, "1HAcient1kLqfeX7DVTV0dklUkpGEnUC")) {
                presenter.doLogOut();
            } else {
                if (TextUtils.equals(EmUtil.getEmployInfo().serviceType,Config.CARPOOL) && EmUtil.getEmployInfo().countNoSchedule>0){
                    new CommonDialog(this, R.layout.dialog_offline) {
                        @Override
                        public void initData(View view) {
                            Button btn_cancel = view.findViewById(R.id.btn_cancel);
                            Button btn_sure = view.findViewById(R.id.btn_sure);
                            btn_cancel.setOnClickListener(v12 -> dismiss());
                            btn_sure.setOnClickListener(v1 -> {
                                presenter.queueOrOffline(null,2);
                                dismiss();
                            });
                        }
                    }.show();
                }else {
                    presenter.offline();
                }
            }
        });
        EmLoc emLoc = EmUtil.getLastLoc();
        if (emLoc != null) {
            receiveLoc(emLoc);
        }
    }


    @Override
    public void setTitleStatus(String content) {
        if (TextUtils.equals(EmUtil.getEmployInfo().serviceType, Config.ZHUANCHE) && EmUtil.getEmployInfo().sex == 2) {
            tvTitle.setVisibility(TextUtils.equals(content, "1") ? View.VISIBLE : View.GONE);
        } else {
            tvTitle.setVisibility(View.GONE);
        }
    }

    private void initNotifity() {
        notifityClose.setOnClickListener(v -> notifityCon.setVisibility(View.GONE));
    }

    private OrderAdapter adapter;

    @Override
    public void findById() {
        workIvManual = findViewById(R.id.workIvManual);
        bottomBar = findViewById(R.id.bottom_bar);
        mapView = findViewById(R.id.map_view);
        rippleBackground = findViewById(R.id.ripple_ground);
        createOrder = findViewById(R.id.create_order);
        swipeRefreshLayout = findViewById(R.id.swipe_layout);
        pullIcon = findViewById(R.id.pull_icon);
        tvTitle = findViewById(R.id.tv_title);
        tvTitle.setSelected(true);
        listenOrderCon = findViewById(R.id.listen_order_con);
        workTvOffline = findViewById(R.id.workTvOffline);
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

        finishNo = findViewById(R.id.finish_no);
        onLineHour = findViewById(R.id.online_time_hour);
        onLineMonute = findViewById(R.id.online_time_minute);
        todayIncome = findViewById(R.id.today_income);
        moneyDesc = findViewById(R.id.money_desc);
        noOrderText = findViewById(R.id.no_order_img);
        bottomBtnCon = findViewById(R.id.bottom_btn_con);
        btCreate = findViewById(R.id.btn_create);

        Employ employ = Employ.findByID(XApp.getMyPreferences().getLong(Config.SP_DRIVERID, -1));
        Log.e("employ", "" + employ);
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
//            XApp.getInstance().syntheticVoice("内容");
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
        adapter = new OrderAdapter(new ArrayList<>(), this);
        swipeRefreshLayout.getRecyclerView().setAdapter(adapter);
        swipeRefreshLayout.setLoadMoreEnable(false);
        swipeRefreshLayout.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        swipeRefreshLayout.setOnLoadListener(new SwipeRecyclerView.OnLoadListener() {
            @Override
            public void onRefresh() {
                presenter.loadEmploy(EmUtil.getEmployId());
                presenter.indexOrders();
                presenter.workStatistics();
            }

            @Override
            public void onLoadMore() {
            }
        });
    }

    /**
     * 全业务订单列表
     */
    List<MultipleOrder> orders = new ArrayList<>();

    @Override
    public void showOrders(List<MultipleOrder> MultipleOrders) {
        orders.clear();
        orders.addAll(MultipleOrders);
        if (orders.size() == 1) {
            showEmpty(0);
        } else {
            hideEmpty();
        }

        adapter = new OrderAdapter(orders, this);
        swipeRefreshLayout.getRecyclerView().setAdapter(adapter);
        PinnedHeaderDecoration pinnedHeaderDecoration = new PinnedHeaderDecoration();
        //设置只有RecyclerItem.ITEM_HEADER的item显示标签
        pinnedHeaderDecoration.setPinnedTypeHeader(MultipleOrder.ITEM_HEADER);
        pinnedHeaderDecoration.registerTypePinnedHeader(MultipleOrder.ITEM_HEADER, (parent, adapterPosition) -> true);
        pinnedHeaderDecoration.registerTypePinnedHeader(MultipleOrder.ITEM_DESC, (parent, adapterPosition) -> true);
        swipeRefreshLayout.getRecyclerView().addItemDecoration(pinnedHeaderDecoration);
    }

    @Override
    public void onManualCreateConfigSuc(ManualConfigBean manualConfigBean) {
        XApp.getEditor().putString(Config.SP_MANUAL_DATA, new Gson().toJson(manualConfigBean)).apply();
        if (manualConfigBean.showView == 1) {
            workIvManual.setVisibility(View.VISIBLE);
            workIvManual.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("WorkActivity", "onClick");
                    ARouter.getInstance().build("/custombus/ManualCreateActivity").navigation();
                }
            });
        } else {
            workIvManual.setVisibility(View.GONE);
        }
    }

    private void setHeaderView(RecyclerView view) {
        View header = LayoutInflater.from(this).inflate(R.layout.order_pinned_layout, view, false);
        header.setOnClickListener(v -> {
            startActivity(new Intent(this, OrderActivity.class));
        });
        adapter.setHeaderView(header);
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

        aMap.setOnMarkerClickListener(this);
        aMap.setOnMapClickListener(this);
        aMap.setInfoWindowAdapter(new NearInfoWindowAdapter(this));
    }

    @Override
    public void initRefreshBtn() {
        refreshImg.setOnClickListener(v -> {
            refreshImg.setVisibility(View.INVISIBLE);
            loadingFrame.setVisibility(View.VISIBLE);
            AnimationDrawable spinner = (AnimationDrawable) loadingImg.getBackground();
            spinner.start();
            presenter.startLocService();
        });
    }

    @Override
    public void onlineSuc() {
        XApp.getInstance().syntheticVoice("", XApp.ON_LINE);
        listenOrderCon.setVisibility(View.VISIBLE);
        rippleBackground.startRippleAnimation();
        bottomBtnCon.setVisibility(View.GONE);
        MqttManager.getInstance().savePushMessage(EmUtil.getLastLoc());
        refreshData();
//        hideEmpty();
    }


    @Override
    public void offlineSuc() {
        XApp.getInstance().syntheticVoice("", XApp.OFF_LINE);
        listenOrderCon.setVisibility(View.GONE);
        rippleBackground.stopRippleAnimation();
        bottomBtnCon.setVisibility(View.VISIBLE);
        refreshData();
    }

    @Override
    public void showNotify(AnnAndNotice notifity) {
        notifityCon.setVisibility(View.VISIBLE);
        notifityContent.setText(getString(R.string.new_notify) + notifity.noticeContent);
//        XApp.getInstance().syntheticVoice(getString(R.string.new_notify) + notifity.noticeContent, true);
        notifityCon.setOnClickListener(v -> {
            notifityCon.setVisibility(View.GONE);
            ARouter.getInstance().build("/personal/NotifityActivity")
                    .navigation();
        });
    }

    List<Marker> markers = new ArrayList<>();

    @Override
    public void showDrivers(List<NearDriver> drivers) {
        if (drivers == null || drivers.isEmpty()) {
            for (Marker marker : markers) {
                marker.remove();
            }
            markers.clear();
            return;
        }
        for (Marker marker : markers) {
            marker.remove();
        }
        markers.clear();
        MarkerOptions options = new MarkerOptions();
        //设置Marker可拖动
        options.draggable(false);
        // 将Marker设置为贴地显示，可以双指下拉地图查看效果
        options.setFlat(true);
        List<LatLng> latLngs = new ArrayList<>();


        for (NearDriver driver : drivers) {
            if (driver.id == EmUtil.getEmployId()) {
                continue;//自己就不显示marker
            }
            options.position(new LatLng(driver.latitude, driver.longitude));
            options.icon(BitmapDescriptorFactory.fromView(getMarkerView(driver)));
            Marker marker = aMap.addMarker(options);
            markers.add(marker);

            LatLng latLng = new LatLng(driver.latitude, driver.longitude);
            latLngs.add(latLng);
        }
        LatLng center = new LatLng(EmUtil.getLastLoc().latitude, EmUtil.getLastLoc().longitude);
        LatLngBounds bounds = MapUtil.getBounds(latLngs, center);
        aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));
    }

    @Override
    public void showAnn(AnnAndNotice announcement) {
        notifityCon.setVisibility(View.VISIBLE);
        notifityContent.setText(getString(R.string.new_ann) + announcement.annMessage);
        notifityCon.setOnClickListener(v -> {
            notifityCon.setVisibility(View.GONE);
            ARouter.getInstance().build("/personal/AnnouncementActivity")
                    .navigation();
        });
    }

    @Override
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showStatis(CountEvent countEvent) {
        if (countEvent == null) {
            return;
        }

        if (countEvent.minute >= 0) {
            int minutes = countEvent.minute;
            int hour = minutes / 60;
            int minute = minutes % 60;
            onLineHour.setText(String.valueOf(hour));
            onLineMonute.setText(String.valueOf(minute));
        }

        if (countEvent.finishCount >= 0) {
            finishNo.setText(String.valueOf(countEvent.finishCount));
        }

        if (countEvent.income == -1) {
            return;
        }
        if (TextUtils.equals(EmUtil.getEmployInfo().serviceType, Config.ZHUANCHE)) {
            if (EmUtil.getEmployInfo().driverType == 2) {
                todayIncome.setText(String.valueOf(countEvent.orderTotalAmount));
                moneyDesc.setText("订单总金额");
            } else {
                todayIncome.setText(String.valueOf(countEvent.income));
                moneyDesc.setText("今日收入");
            }
        } else {
            if (EmUtil.getEmployInfo().commissionStatus == 1) {
                todayIncome.setText(String.valueOf(countEvent.income));
                moneyDesc.setText("今日收入");
            } else {
                todayIncome.setText(String.valueOf(countEvent.orderTotalAmount));
                moneyDesc.setText("订单总金额");
            }
        }
    }

    @Override
    public void showOnline() {
        listenOrderCon.setVisibility(View.VISIBLE);
        rippleBackground.startRippleAnimation();
        bottomBtnCon.setVisibility(View.GONE);
    }

    @Override
    public void showOffline() {
        listenOrderCon.setVisibility(View.GONE);
        rippleBackground.stopRippleAnimation();
        bottomBtnCon.setVisibility(View.VISIBLE);
        stopRefresh();
        /**
         * 专车补单在下线状态
         */
        if (EmUtil.getEmployInfo().serviceType.equals(Config.ZHUANCHE)) {
            btCreate.setVisibility(View.VISIBLE);
            if (Vehicle.exists(EmUtil.getEmployId()) && (Vehicle.findByEmployId(EmUtil.getEmployId()).isTaxiNormal == 1)){
                btCreate.setVisibility(View.GONE);
            }
        } else {
            btCreate.setVisibility(View.GONE);
        }
    }

    @Override
    public void stopRefresh() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showDriverStatus() {
        Employ employ = EmUtil.getEmployInfo();
        if (String.valueOf(employ.status).equals(EmployStatus.FROZEN) || employ.status == 1) {
            EmUtil.employLogout(this);
        } else {
            presenter.driverehicle(employ);
            presenter.getSetting();
            if (String.valueOf(employ.status).equals(EmployStatus.ONLINE)) {
                showOffline();//非听单状态
            } else {
                showOnline();//听单状态
            }
        }

        if (TextUtils.equals(EmUtil.getEmployInfo().serviceType, Config.COUNTRY)) {
            presenter.getManualConfig();
        } else {
            XApp.getEditor().remove(Config.SP_MANUAL_DATA).apply();
        }
    }

    @Override
    public void showHomeAnnAndNotice(List<AnnAndNotice> annAndNoticeList) {
//        stopRefresh();
//        if (null != annAndNoticeList && annAndNoticeList.size() != 0) {
//            hideEmpty();
//            noticeAdapter = new NoticeAdapter(annAndNoticeList);
//            noticeAdapter.setOnDeleteNoticeListener(id -> presenter.deleteNotice(id));
//            recyclerView.setAdapter(noticeAdapter);
//            PinnedHeaderDecoration pinnedHeaderDecoration = new PinnedHeaderDecoration();
//            //设置只有RecyclerItem.ITEM_HEADER的item显示标签
//            pinnedHeaderDecoration.setPinnedTypeHeader(AnnAndNotice.ITEM_HEADER);
//            pinnedHeaderDecoration.registerTypePinnedHeader(AnnAndNotice.ITEM_HEADER, (parent, adapterPosition) -> true);
//            pinnedHeaderDecoration.registerTypePinnedHeader(AnnAndNotice.ITEM_DESC, (parent, adapterPosition) -> true);
//            recyclerView.addItemDecoration(pinnedHeaderDecoration);
//        } else {
//            showEmpty(1);
//        }
    }

    @Override
    public void hideEmpty() {
        noOrderText.setVisibility(View.GONE);
    }


    /**
     * @param type 0订单  1通知公告
     */
    @Override
    public void showEmpty(int type) {
        if (type == 0) {
            noOrderText.setText(R.string.no_order);
            Drawable drawable = getResources().getDrawable(R.mipmap.ic_no_order);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            noOrderText.setCompoundDrawables(null, drawable, null, null);
        } else {
            noOrderText.setText(R.string.no_work);
            Drawable drawable = getResources().getDrawable(R.mipmap.ic_no_order);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            noOrderText.setCompoundDrawables(null, drawable, null, null);
        }
        noOrderText.setVisibility(View.VISIBLE);
    }


    @Override
    public RxManager getRxManager() {
        return mRxManager;
    }

    @Override
    public void hintCreatOrder() {

    }

    private boolean isFront = false;

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        isFront = true;
        boolean isLogin = XApp.getMyPreferences().getBoolean(Config.SP_ISLOGIN, false);
        if (!isLogin) {
            ARouter.getInstance().build("/personal/LoginActivity").navigation();
            finish();
        }
        refreshData();
        if (presenter != null) {
            presenter.loadDataOnResume();
        }
    }

    private void refreshData() {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isFront = false;
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        getRxManager().clear();
        MqttManager.release();
        mapView.onDestroy();
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReconnectEvent(MqttReconnectEvent reconnectEvent) {
        presenter.resetMqtt();
    }


    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocReceiver.getInstance().addObserver(this);//添加位置改变的订阅
        refreshImg.callOnClick();

        cancelOrderReceiver = new CancelOrderReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Config.BROAD_CANCEL_ORDER);
        filter.addAction(Config.BROAD_BACK_ORDER);
        registerReceiver(cancelOrderReceiver, filter, EmUtil.getBroadCastPermission(), null);

        employStatusChangeReceiver = new EmployStatusChangeReceiver(this);
        registerReceiver(employStatusChangeReceiver, new IntentFilter(Config.BROAD_EMPLOY_STATUS_CHANGE), EmUtil.getBroadCastPermission(), null);

        noticeReceiver = new NoticeReceiver(this);
        registerReceiver(noticeReceiver, new IntentFilter(Config.BROAD_NOTICE), EmUtil.getBroadCastPermission(), null);

        annReceiver = new AnnReceiver(this);
        registerReceiver(annReceiver, new IntentFilter(Config.BROAD_ANN), EmUtil.getBroadCastPermission(), null);

        orderRefreshReceiver = new OrderRefreshReceiver(this);
        registerReceiver(orderRefreshReceiver, new IntentFilter(Config.ORDER_REFRESH), EmUtil.getBroadCastPermission(), null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopRefresh();
        LocReceiver.getInstance().deleteObserver(this);//取消位置改变的订阅
        unregisterReceiver(cancelOrderReceiver);
        unregisterReceiver(employStatusChangeReceiver);

        unregisterReceiver(noticeReceiver);
        unregisterReceiver(annReceiver);
        unregisterReceiver(orderRefreshReceiver);
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

    /**
     * 当前司机位置marker
     */
    private Marker myLocMarker;

    @Override
    public void receiveLoc(EmLoc location) {
        if (null == location) {
            return;
        }
        LatLng latLng = new LatLng(location.latitude, location.longitude);
        currentPlace.setText(getString(R.string.current_place)
                + location.street + "  " + location.poiName);
        if (null == myLocMarker) {
            MarkerOptions markerOption = new MarkerOptions();
            markerOption.position(latLng);
            markerOption.infoWindowEnable(false);
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
        }
        presenter.queryNearDriver(location.latitude, location.longitude);
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
            //模拟home键
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        }
    }

    @Override
    public void onCancelOrder(long orderId, String orderType) {
        refreshData();
    }

    @Override
    public void onStatusChange(String status) {
        if (status.equals(EmployStatus.WORK)) {
            showOnline();
            refreshData();
        } else {
            showOffline();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (StringUtils.isNotBlank(marker.getTitle()) && presenter.canCallPhone) {
            PhoneUtil.call(this, marker.getTitle());
        }
        return true;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        for (Marker marker : markers) {
            marker.hideInfoWindow();
        }
    }

    @Override
    public void onReceiveNotice(String message) {
        AnnAndNotice notifity = new AnnAndNotice();
        notifity.noticeContent = message;
        showNotify(notifity);
    }

    @Override
    public void onReceiveAnn(String message) {
        AnnAndNotice announcement = new AnnAndNotice();
        announcement.annMessage = message;
        showAnn(announcement);
    }

    public void modelSet(View view) {
        Intent intent = new Intent(WorkActivity.this, CreateActivity.class);
        intent.putExtra("type", EmUtil.getEmployInfo().serviceType);
        startActivity(intent);
    }

    private View getMarkerView(NearDriver driver) {
        View v = getLayoutInflater().inflate(R.layout.map_overly, null);
        RelativeLayout overly_bg = v.findViewById(R.id.overly_bg);

        if (driver.status.equals(EmployStatus.WORK)) {
            overly_bg.setBackgroundResource(R.mipmap.map__free_maker_bg);
        } else {
            overly_bg.setBackgroundResource(R.mipmap.map__busy_maker_bg);
        }
        TextView driverName = v.findViewById(R.id.overly_driver_name);
        driverName.setText(driver.name);

        return v;
    }

    @Override
    public void onRefreshOrder() {
        refreshData();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == 0x99){
                presenter.online();
            }
        }
    }
}
