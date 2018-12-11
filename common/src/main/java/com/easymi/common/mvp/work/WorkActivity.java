package com.easymi.common.mvp.work;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
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
import com.easymi.common.activity.ModelSetActivity;
import com.easymi.common.adapter.CityLineAdapter;
import com.easymi.common.adapter.OrderAdapter;
import com.easymi.common.entity.AnnAndNotice;
import com.easymi.common.entity.BuildPushData;
import com.easymi.common.entity.CityLine;
import com.easymi.common.entity.MultipleOrder;
import com.easymi.common.entity.NearDriver;
import com.easymi.common.mvp.order.OrderActivity;
import com.easymi.common.push.CountEvent;
import com.easymi.common.push.MqttManager;
import com.easymi.common.receiver.AnnReceiver;
import com.easymi.common.receiver.CancelOrderReceiver;
import com.easymi.common.receiver.EmployStatusChangeReceiver;
import com.easymi.common.receiver.NoticeReceiver;
import com.easymi.common.register.InfoActivity;
import com.easymi.common.util.CommonUtil;
import com.easymi.common.widget.NearInfoWindowAdapter;
import com.easymi.common.widget.RegisterDialog;
import com.easymi.component.Config;
import com.easymi.component.EmployStatus;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.EmLoc;
import com.easymi.component.entity.Employ;
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
import com.easymi.component.widget.pinned.PinnedHeaderDecoration;
import com.skyfishjy.library.RippleBackground;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import cn.projcet.hf.securitycenter.dialog.MainDialog;


/**
 * Created by developerLzh on 2017/11/3 0003.
 */

@Route(path = "/common/WorkActivity")
public class WorkActivity extends RxBaseActivity implements WorkContract.View, LocObserver, CancelOrderReceiver.OnCancelListener, EmployStatusChangeReceiver.OnStatusChangeListener, AMap.OnMarkerClickListener, AMap.OnMapClickListener, NoticeReceiver.OnReceiveNotice, AnnReceiver.OnReceiveAnn {

    LinearLayout bottomBar;

    MapView mapView;

    RippleBackground rippleBackground;

    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;

    CusToolbar toolbar;

    LinearLayout createOrder;

    ImageView pullIcon;
    LinearLayout peek_con;

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

    LinearLayout guideFrame;
    ImageView gotoSet;

    private CancelOrderReceiver cancelOrderReceiver;
    private EmployStatusChangeReceiver employStatusChangeReceiver;

    private NoticeReceiver noticeReceiver;
    private AnnReceiver annReceiver;

    private WorkPresenter presenter;

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

//        initGuide();
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
            presenter.online(onLineBtn);
        });
//        offlineCon.setOnClickListener(v -> presenter.offline());
        listenOrderCon.setOnClickListener(v -> {
            presenter.offline();
        });

        EmLoc emLoc = EmUtil.getLastLoc();
        if (emLoc != null) {
            receiveLoc(emLoc);
        }
    }

    private RegisterDialog registerDialog;
    private int lastType = -1;

    @Override
    public void showRegisterDialog(String companyPhone, int type, String reason) {
        if (registerDialog == null || lastType != type) {
            if (registerDialog != null) {
                registerDialog.dismiss();
            }
            if (type == 2) {
                registerDialog = RegisterDialog.newInstance("亲爱的师傅您好：",
                        "    您的资料已提交成功，管理人员正在审核，如有疑问请及时联后台管理人员，审核通过后您可开启接单旅程了。",
                        "联系客服",
                        true)
                        .setOnClickListener(() ->
                                PhoneUtil.call(WorkActivity.this, companyPhone));
            } else if (type == 3) {
                //没完善资料
                registerDialog = RegisterDialog.newInstance("亲爱的师傅您好：",
                        "    您的资料还未完善，请尽快完善提交资料，管理员审核通过之后您就可以开启接单旅程了。",
                        "完善资料",
                        false)
                        .setOnClickListener(() ->
                                startActivity(new Intent(WorkActivity.this, InfoActivity.class)));

            } else if (type == 4) {
                //驳回
                registerDialog = RegisterDialog.newInstance("审核不通过原因：",
                        "    " + reason,
                        "重新完善",
                        false)
                        .setOnClickListener(() ->
                                startActivity(new Intent(WorkActivity.this, InfoActivity.class)));
            }
            lastType = type;
        }
        if (registerDialog != null && !registerDialog.isShow()) {
            registerDialog.show(getSupportFragmentManager(), "registerDialog");
        }
    }

    @Override
    public void hideRegisterDialog() {
        if (registerDialog != null) {
            registerDialog.dismiss();
        }
    }

    private void initGuide() {
        boolean showGuide = XApp.getMyPreferences().getBoolean(Config.SP_SHOW_GUIDE, true);
        if (showGuide) {
            guideFrame.setVisibility(View.VISIBLE);
            gotoSet.setOnClickListener(v -> {
                guideFrame.setVisibility(View.GONE);
                XApp.getPreferencesEditor().putBoolean(Config.SP_SHOW_GUIDE, false).apply();
                try {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse("http://help.xiaokayun.cn");
                    intent.setData(content_url);
                    startActivity(intent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        } else {
            guideFrame.setVisibility(View.GONE);
        }
    }

    private void initNotifity() {
        notifityClose.setOnClickListener(v -> notifityCon.setVisibility(View.GONE));
    }

    private OrderAdapter adapter;

//    private NoticeAdapter noticeAdapter;

    @Override
    public void findById() {
        bottomBar = findViewById(R.id.bottom_bar);
        mapView = findViewById(R.id.map_view);
        rippleBackground = findViewById(R.id.ripple_ground);
        recyclerView = findViewById(R.id.recyclerView);
        createOrder = findViewById(R.id.create_order);
        swipeRefreshLayout = findViewById(R.id.swipe_layout);
        pullIcon = findViewById(R.id.pull_icon);
        peek_con = findViewById(R.id.peek_con);

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

        finishNo = findViewById(R.id.finish_no);
        onLineHour = findViewById(R.id.online_time_hour);
        onLineMonute = findViewById(R.id.online_time_minute);
        todayIncome = findViewById(R.id.today_income);

        noOrderText = findViewById(R.id.no_order_img);

        guideFrame = findViewById(R.id.guide_frame);
        gotoSet = findViewById(R.id.guide_go_to_set);

        bottomBtnCon = findViewById(R.id.bottom_btn_con);

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
            ARouter.getInstance()
                    .build("/personal/MoreActivity")
                    .navigation();
//            new MainDialog(WorkActivity.this,
//                    105,
//                    Config.APP_KEY,XApp.getMyPreferences().getString(Config.AES_PASSWORD,""),
//                    XApp.getMyPreferences().getString(Config.SP_TOKEN,""),
//                    0,
//                    "",
//                    "18180635910");
        });
    }

    /**
     * 初始化recycler
     */
    @Override
    public void initRecycler() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        swipeRefreshLayout.setOnRefreshListener(() -> {
            noOrderText.setVisibility(View.GONE);
            presenter.loadEmploy(EmUtil.getEmployId());
        });

        swipeRefreshLayout.setRefreshing(true);
    }

    //全业务订单列表
    List<MultipleOrder> orders = new ArrayList<>();

    @Override
    public void showOrders(List<MultipleOrder> MultipleOrders) {
        orders.clear();

        if (MultipleOrders == null || MultipleOrders.size() == 0) {
            showEmpty(0);
        } else {
            orders.addAll(MultipleOrders);
            hideEmpty();
        }

        adapter = new OrderAdapter(orders, this);
        recyclerView.setAdapter(adapter);
        PinnedHeaderDecoration pinnedHeaderDecoration = new PinnedHeaderDecoration();
        //设置只有RecyclerItem.ITEM_HEADER的item显示标签
        pinnedHeaderDecoration.setPinnedTypeHeader(MultipleOrder.ITEM_HEADER);
        pinnedHeaderDecoration.registerTypePinnedHeader(MultipleOrder.ITEM_HEADER, (parent, adapterPosition) -> true);
        pinnedHeaderDecoration.registerTypePinnedHeader(MultipleOrder.ITEM_DESC, (parent, adapterPosition) -> true);
        recyclerView.addItemDecoration(pinnedHeaderDecoration);

//        if (orders.size() == 0) {
//            setHeaderView(recyclerView);
//        } else {
//            setHeaderView(null);
//        }
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
        MqttManager.getInstance().pushLocNoLimit(new BuildPushData(EmUtil.getLastLoc()));
        presenter.indexOrders();
        swipeRefreshLayout.setRefreshing(true);
        hideEmpty();
        recyclerView.setVisibility(View.VISIBLE);
        presenter.loadEmploy(EmUtil.getEmployId());
    }

    @Override
    public void offlineSuc() {
        XApp.getInstance().syntheticVoice("", XApp.OFF_LINE);
        listenOrderCon.setVisibility(View.GONE);
        rippleBackground.stopRippleAnimation();
        bottomBtnCon.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(false);
        showEmpty(0);
        recyclerView.setVisibility(View.GONE);
        presenter.loadEmploy(EmUtil.getEmployId());
    }

    @Override
    public void showNotify(AnnAndNotice notifity) {
        notifityCon.setVisibility(View.VISIBLE);
        notifityContent.setText(getString(R.string.new_notify) + notifity.noticeContent);
        XApp.getInstance().syntheticVoice(getString(R.string.new_notify) + notifity.noticeContent, true);
        notifityCon.setOnClickListener(v -> {
            notifityCon.setVisibility(View.GONE);
            ARouter.getInstance().build("/personal/NotifityActivity")
                    .navigation();
        });
    }

    List<Marker> markers = new ArrayList<>();
    List<NearDriver> drivers = new ArrayList<>();

    @Override
    public void showDrivers(List<NearDriver> drivers) {
        for (Marker marker : markers) {
            marker.remove();
        }
        markers.clear();
        this.drivers = drivers;
        MarkerOptions options = new MarkerOptions();
        options.draggable(false);//设置Marker可拖动
        // 将Marker设置为贴地显示，可以双指下拉地图查看效果
        options.setFlat(true);//设置marker平贴地图效果
        List<LatLng> latLngs = new ArrayList<>();


        for (NearDriver driver : drivers) {
            if (driver.id == EmUtil.getEmployId()) {
                continue;//自己就不显示marker
            }
            options.position(new LatLng(driver.latitude, driver.longitude));
            options.icon(BitmapDescriptorFactory.fromView(getMarkerView(driver)));
            Marker marker = aMap.addMarker(options);
            marker.setInfoWindowEnable(true);
            marker.setSnippet(driver.name);
            marker.setTitle(driver.phone);
            markers.add(marker);

            LatLng latLng = new LatLng(driver.latitude, driver.longitude);
            latLngs.add(latLng);
        }
        LatLng center = new LatLng(EmUtil.getLastLoc().latitude, EmUtil.getLastLoc().longitude);
        LatLngBounds bounds = MapUtil.getBounds(latLngs, center);
        aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
    }

    @Override
    public void showAnn(AnnAndNotice announcement) {
        notifityCon.setVisibility(View.VISIBLE);
        notifityContent.setText(getString(R.string.new_ann) + announcement.annMessage);
//        XApp.getInstance().syntheticVoice(getString(R.string.new_ann) + announcement.message, true);
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
        if (countEvent.income >= 0) {
            todayIncome.setText(String.valueOf(countEvent.income));
        }
    }

    @Override
    public void showOnline() {
        listenOrderCon.setVisibility(View.VISIBLE);
        rippleBackground.startRippleAnimation();
        bottomBtnCon.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void showOffline() {
        listenOrderCon.setVisibility(View.GONE);
        rippleBackground.stopRippleAnimation();
        bottomBtnCon.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void stopRefresh() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showDriverStatus() {
        Employ employ = EmUtil.getEmployInfo();
        if (String.valueOf(employ.status).equals(EmployStatus.FROZEN)) {
            EmUtil.employLogout(this);
        } else if (String.valueOf(employ.status).equals(EmployStatus.OFFLINE) || employ.status == 0) {
            showOffline();//非听单状态
            presenter.initDaemon();
        } else {
            showOnline();//听单状态
            presenter.indexOrders();
            presenter.initDaemon();
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

    private boolean isFront = false;

    @Override
    protected void onResume() {
        super.onResume();
        isFront = true;
        boolean isLogin = XApp.getMyPreferences().getBoolean(Config.SP_ISLOGIN, false);
        if (!isLogin) {
            ARouter.getInstance().build("/personal/LoginActivity")/*.withFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)*/.navigation();
            finish();
        }
        mapView.onResume();
        presenter.loadDataOnResume();
        MqttManager.getInstance().pushLocNoLimit(new BuildPushData(EmUtil.getLastLoc()));
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
        presenter.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        getRxManager().clear();
        mapView.onDestroy();
        super.onDestroy();

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
        registerReceiver(cancelOrderReceiver, filter);

        employStatusChangeReceiver = new EmployStatusChangeReceiver(this);
        registerReceiver(employStatusChangeReceiver, new IntentFilter(Config.BROAD_EMPLOY_STATUS_CHANGE));

        noticeReceiver = new NoticeReceiver(this);
        registerReceiver(noticeReceiver, new IntentFilter(Config.BROAD_NOTICE));

        annReceiver = new AnnReceiver(this);
        registerReceiver(annReceiver, new IntentFilter(Config.BROAD_ANN));
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocReceiver.getInstance().deleteObserver(this);//取消位置改变的订阅
        unregisterReceiver(cancelOrderReceiver);
        unregisterReceiver(employStatusChangeReceiver);

        unregisterReceiver(noticeReceiver);
        unregisterReceiver(annReceiver);
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
            presenter.queryNearDriver(location.latitude, location.longitude);
        } else {
            if (this.location != null) {
                LatLng last = new LatLng(this.location.latitude, this.location.longitude);
                LatLng current = new LatLng(location.latitude, location.longitude);
                double dis = AMapUtils.calculateLineDistance(last, current);
                if (dis > 30) {//大于30米重新加载司机
                    if (isFront) {//activity可见时才调用该接口
                        presenter.queryNearDriver(location.latitude, location.longitude);
                    }
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
            //模拟home键
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        }
    }

    @Override
    public void onCancelOrder(long orderId, String orderType) {
        swipeRefreshLayout.setRefreshing(true);
        presenter.indexOrders();
    }

    @Override
    public void onStatusChange(String status) {
        if (status.equals(EmployStatus.ONLINE)) {
            showOffline();
        } else {
            showOnline();
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
        Intent intent = new Intent(WorkActivity.this, ModelSetActivity.class);
        startActivity(intent);
    }

    public void tongji(View view) {
        ARouter.getInstance().build("/personal/StatsActivity")
                .navigation();
    }

    private View getMarkerView(NearDriver driver) {
        View v = getLayoutInflater().inflate(R.layout.map_overly, null);
        RelativeLayout overly_bg = v.findViewById(R.id.overly_bg);

        if (driver.status.equals(EmployStatus.FREE)) {
            overly_bg.setBackgroundResource(R.mipmap.map__free_maker_bg);
        } else {
            overly_bg.setBackgroundResource(R.mipmap.map__busy_maker_bg);
        }
        TextView driverName = v.findViewById(R.id.overly_driver_name);
        driverName.setText(driver.name);

        return v;
    }
}
