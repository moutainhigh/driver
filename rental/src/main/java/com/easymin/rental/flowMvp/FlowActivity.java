package com.easymin.rental.flowMvp;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.utils.overlay.SmoothMoveMarker;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.RouteOverlayOptions;
import com.amap.api.navi.view.RouteOverLay;
import com.amap.api.services.route.DriveRouteResult;
import com.easymi.common.entity.Address;
import com.easymi.common.receiver.CancelOrderReceiver;
import com.easymi.component.Config;
import com.easymi.component.DJOrderStatus;
import com.easymi.component.ZCOrderStatus;
import com.easymi.component.ZXOrderStatus;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.BaseOrder;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.entity.EmLoc;
import com.easymi.component.loc.LocObserver;
import com.easymi.component.loc.LocReceiver;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.utils.CsSharedPreferences;
import com.easymi.component.utils.DensityUtil;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.Log;
import com.easymi.component.utils.MapUtil;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.LoadingButton;
import com.easymi.component.widget.overlay.DrivingRouteOverlay;

import com.easymin.rental.R;
import com.easymin.rental.activity.FinishActivity;
import com.easymin.rental.adapter.LeftWindowAdapter;
import com.easymin.rental.entity.RentalOrder;
import com.easymin.rental.fragment.ArriveStartFragment;
import com.easymin.rental.fragment.ConfirmOrderFragment;
import com.easymin.rental.fragment.NotStartFragment;
import com.easymin.rental.fragment.RunningFragment;
import com.easymin.rental.fragment.ToStartFragment;
import com.easymin.rental.receiver.OrderFinishReceiver;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: FlowActivity
 *@Author: shine
 * Date: 2018/12/18 下午1:52
 * Description:
 * History:
 */
@Route(path = "/rental/FlowActivity")
public class FlowActivity extends RxBaseActivity implements
        FlowContract.View,
        LocObserver,
        CancelOrderReceiver.OnCancelListener,
        AMap.OnMapTouchListener,
        OrderFinishReceiver.OnFinishListener, AMap.OnMarkerClickListener, AMap.OnMapClickListener {

    CusToolbar toolbar;
    MapView mapView;
    FrameLayout fragmentFrame;

    AMap aMap;

    private LatLng lastLatlng;
    private Marker myFirstMarker;

    FlowPresenter presenter;

    Fragment currentFragment;
    /**
     * activity和fragment的通信接口
     */
    private ActFraCommBridge bridge;

    DymOrder dymOrder;

    public static boolean isMapTouched = false;

    private boolean isOrderLoadOk = false;//订单查询是否完成

    private long orderId;

    private RentalOrder baseOrder;

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_chartered_flow;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        orderId = getIntent().getLongExtra("orderId", -1);
        if (orderId == -1) {
            finish();
            return;
        }

        presenter = new FlowPresenter(this, this);

        mapView = findViewById(R.id.map_view);
        fragmentFrame = findViewById(R.id.fragment_frame);

        mapView.onCreate(savedInstanceState);
        initMap();
    }

    @Override
    public void showOrder(RentalOrder baseOrder) {
        //初始进入界面查询订单信息
        if (null == baseOrder) {
            finish();
        } else {
            if (baseOrder.status > DJOrderStatus.FINISH_ORDER) {
                ToastUtil.showMessage(this, getResources().getString(R.string.order_finish));
                finish();
            }
            this.baseOrder = baseOrder;
            initBridget();
            showBottomFragment(baseOrder);
            showMapBounds();
        }
    }


    @Override
    public RxManager getManager() {
        return mRxManager;
    }

    @Override
    public void initToolBar() {
        super.initToolBar();
        toolbar = findViewById(R.id.cus_toolbar);
        toolbar.setLeftIcon(R.drawable.ic_arrow_back, v -> finish());
    }

    NotStartFragment notStartFragment;

    @Override
    protected void onResume() {
        super.onResume();
        EmLoc location = EmUtil.getLastLoc();
        if (location == null) {
            ToastUtil.showMessage(this, getString(R.string.loc_failed));
            finish();
            return;
        }
        mapView.onResume();
        lastLatlng = new LatLng(location.latitude, location.longitude);
        presenter.findOne(orderId);
//        showOrder(baseOrder);
    }

    /**
     * 地图设置
     */
    @Override
    public void initMap() {
        aMap = mapView.getMap();
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.getUiSettings().setRotateGesturesEnabled(false);
        aMap.getUiSettings().setRotateGesturesEnabled(false);
        aMap.getUiSettings().setTiltGesturesEnabled(false);//倾斜手势
        aMap.getUiSettings().setLogoBottomMargin(-50);//隐藏logo

        aMap.setOnMapTouchListener(this);

        aMap.setOnMarkerClickListener(this);
        aMap.setOnMapClickListener(this);

        aMap.setInfoWindowAdapter(new LeftWindowAdapter(this));

        String locStr = XApp.getMyPreferences().getString(Config.SP_LAST_LOC, "");
        EmLoc emLoc = new Gson().fromJson(locStr, EmLoc.class);
        if (null != emLoc) {
            lastLatlng = new LatLng(emLoc.latitude, emLoc.longitude);
            receiveLoc(emLoc);//手动调用上次位置 减少从北京跳过来的时间
            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLatlng, 19));//移动镜头，首次镜头快速跳到指定位置

            MarkerOptions markerOption = new MarkerOptions();
            markerOption.position(new LatLng(emLoc.latitude, emLoc.longitude));
            markerOption.draggable(false);//设置Marker可拖动
            markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(getResources(), R.mipmap.ic_flow_my_pos)));
            // 将Marker设置为贴地显示，可以双指下拉地图查看效果
            markerOption.setFlat(true);//设置marker平贴地图效果
            myFirstMarker = aMap.addMarker(markerOption);
        }
    }

    /**
     * 初始化fragment bridget
     */
    @Override
    public void initBridget() {
        bridge = new ActFraCommBridge() {

            @Override
            public void showBounds(List<LatLng> latLngs) {
                boundsZoom(latLngs);
            }

            @Override
            public void clearMap() {
                aMap.clear();
                smoothMoveMarker = null;
                initMap();
                //第一时间加上自身位置
                receiveLoc(EmUtil.getLastLoc());
            }

            @Override
            public void routePath(LatLng toLatlng) {
                presenter.routePlanByNavi(toLatlng.latitude, toLatlng.longitude);
            }

            @Override
            public void routePath(LatLng startLatlng, List<LatLng> passLatlngs, LatLng endLatlng) {
                presenter.routePlanByRouteSearch(startLatlng, passLatlngs, endLatlng);
            }

            @Override
            public void doRefresh() {
                isMapTouched = false;
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLatlng, 19));
            }

            @Override
            public void countStartOver() {

            }

            @Override
            public void navi(LatLng latLng, Long orderId) {
//                if (baseOrder.status == ZCOrderStatus.TAKE_ORDER
//                        || baseOrder.status == ZCOrderStatus.GOTO_BOOKPALCE_ORDER) {
//                    presenter.navi(new LatLng(getStartAddr().latitude, getStartAddr().longitude),  orderId);
//                } else if (baseOrder.status == ZCOrderStatus.ARRIVAL_BOOKPLACE_ORDER
//                        || baseOrder.status == ZCOrderStatus.GOTO_DESTINATION_ORDER) {
//                    presenter.navi(new LatLng(getEndAddr().latitude, getEndAddr().longitude), orderId);
//                }
                presenter.navi(latLng, orderId);
            }

            @Override
            public void toNotStart() {
                switchFragment(notStartFragment).commit();
            }

            @Override
            public void toStart() {
                presenter.changeStauts(baseOrder.id, DJOrderStatus.GOTO_BOOKPALCE_ORDER);
            }

            @Override
            public void arriveStart() {
                presenter.changeStauts(baseOrder.id, DJOrderStatus.ARRIVAL_BOOKPLACE_ORDER);
            }

            @Override
            public void doStartDrive() {
                presenter.changeStauts(baseOrder.id, DJOrderStatus.GOTO_DESTINATION_ORDER);
            }

            @Override
            public void arriveDestance() {
                presenter.changeStauts(baseOrder.id, DJOrderStatus.ARRIVAL_DESTINATION_ORDER);
            }

            @Override
            public void toFinish(LoadingButton button) {
                presenter.orderConfirm(baseOrder.id, baseOrder.version,button);
            }
        };
    }

    @Override
    public void showBottomFragment(BaseOrder baseOrder) {
        if (baseOrder.status == ZCOrderStatus.TAKE_ORDER) {
            toolbar.setTitle(R.string.status_no_start);
            NotStartFragment notStartFragment = new NotStartFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("baseOrder", baseOrder);
            notStartFragment.setArguments(bundle);
            notStartFragment.setBridge(bridge);

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
            transaction.replace(R.id.fragment_frame, notStartFragment);
            transaction.commit();
        } else if (baseOrder.status == ZCOrderStatus.GOTO_BOOKPALCE_ORDER) {
            toolbar.setTitle(R.string.status_to_start);
            ToStartFragment fragment = new ToStartFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("baseOrder", baseOrder);
            fragment.setArguments(bundle);
            fragment.setBridge(bridge);

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
            transaction.replace(R.id.fragment_frame, fragment);
            transaction.commit();
        } else if (baseOrder.status == ZCOrderStatus.ARRIVAL_BOOKPLACE_ORDER) {
            toolbar.setTitle(R.string.wait_arrive_start);
            ArriveStartFragment fragment = new ArriveStartFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("baseOrder", baseOrder);
            fragment.setArguments(bundle);
            fragment.setBridge(bridge);

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
            transaction.replace(R.id.fragment_frame, fragment);
            transaction.commit();
        } else if (baseOrder.status == ZCOrderStatus.GOTO_DESTINATION_ORDER) {
            toolbar.setTitle(R.string.status_to_end);
            RunningFragment fragment = new RunningFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("baseOrder", baseOrder);
            fragment.setArguments(bundle);
            fragment.setBridge(bridge);

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
            transaction.replace(R.id.fragment_frame, fragment);
            transaction.commit();
        } else if (baseOrder.status == ZCOrderStatus.ARRIVAL_DESTINATION_ORDER) {
            toolbar.setTitle(R.string.confirm_order);
            ConfirmOrderFragment fragment = new ConfirmOrderFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("baseOrder", baseOrder);
            fragment.setArguments(bundle);
            fragment.setBridge(bridge);

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
            transaction.replace(R.id.fragment_frame, fragment);
            transaction.commit();
        }
    }

    private Marker startMarker;
    private Marker endMarker;

    @Override
    public void showMapBounds() {
        List<LatLng> latLngs = new ArrayList<>();
        if (baseOrder.status == ZCOrderStatus.NEW_ORDER
                || baseOrder.status == ZCOrderStatus.PAIDAN_ORDER
                || baseOrder.status == ZCOrderStatus.TAKE_ORDER
                ) {
            if (null != getStartAddr()) {
                latLngs.add(new LatLng(getStartAddr().latitude, getStartAddr().longitude));
                presenter.routePlanByNavi(getStartAddr().latitude, getStartAddr().longitude);
            }
            if (null != getEndAddr()) {
                latLngs.add(new LatLng(getEndAddr().latitude, getEndAddr().longitude));
            }
            LatLngBounds bounds = MapUtil.getBounds(latLngs, lastLatlng);
            aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, (int) (DensityUtil.getDisplayWidth(this) / 1.5), (int) (DensityUtil.getDisplayWidth(this) / 1.5), 0));
        } else if (baseOrder.status == ZCOrderStatus.GOTO_BOOKPALCE_ORDER) {
            if (null != getStartAddr()) {
                latLngs.add(new LatLng(getStartAddr().latitude, getStartAddr().longitude));
                presenter.routePlanByNavi(getStartAddr().latitude, getStartAddr().longitude);
            } else {
                presenter.stopNavi();
//                leftTimeText.setText("");
            }
            LatLngBounds bounds = MapUtil.getBounds(latLngs, lastLatlng);
            aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, (int) (DensityUtil.getDisplayWidth(this) / 1.5), (int) (DensityUtil.getDisplayWidth(this) / 1.5), 0));
        } else if (baseOrder.status == ZCOrderStatus.ARRIVAL_BOOKPLACE_ORDER
                || baseOrder.status == ZCOrderStatus.GOTO_DESTINATION_ORDER
                || baseOrder.status == ZCOrderStatus.START_WAIT_ORDER
                || baseOrder.status == ZCOrderStatus.ARRIVAL_DESTINATION_ORDER
                ) {
            if (null != getEndAddr()) {
                latLngs.add(new LatLng(getEndAddr().latitude, getEndAddr().longitude));
                presenter.routePlanByNavi(getEndAddr().latitude, getEndAddr().longitude);
            }
            if (baseOrder.status == ZCOrderStatus.GOTO_DESTINATION_ORDER) {
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLatlng, 19));
            } else {
                LatLngBounds bounds = MapUtil.getBounds(latLngs, lastLatlng);
                aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, (int) (DensityUtil.getDisplayWidth(this) / 1.5), (int) (DensityUtil.getDisplayWidth(this) / 1.5), 0));
            }
        }
        if (null != getStartAddr()) {
            if (null == startMarker) {
                MarkerOptions markerOption = new MarkerOptions();
                markerOption.position(new LatLng(getStartAddr().latitude, getStartAddr().longitude));
                markerOption.draggable(false);//设置Marker可拖动
                markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(), R.mipmap.ic_start)));
                // 将Marker设置为贴地显示，可以双指下拉地图查看效果
                markerOption.setFlat(true);//设置marker平贴地图效果
                startMarker = aMap.addMarker(markerOption);
            } else {
                startMarker.setPosition(new LatLng(getStartAddr().latitude, getStartAddr().longitude));
            }
        }
        if (null != getEndAddr()) {
            if (null == endMarker) {
                MarkerOptions markerOption = new MarkerOptions();
                markerOption.position(new LatLng(getEndAddr().latitude, getEndAddr().longitude));
                markerOption.draggable(false);//设置Marker可拖动
                markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(), R.mipmap.ic_end)));
                // 将Marker设置为贴地显示，可以双指下拉地图查看效果
                markerOption.setFlat(true);//设置marker平贴地图效果
                endMarker = aMap.addMarker(markerOption);
            } else {
                endMarker.setPosition(new LatLng(getEndAddr().latitude, getEndAddr().longitude));
            }
        }
    }

    @Override
    public void toFinish() {
        DymOrder dymOrder = DymOrder.findByIDType(orderId, Config.RENTAL);
        if (null != dymOrder) {
            dymOrder.delete();
        }
        Intent intent = new Intent(FlowActivity.this, FinishActivity.class);
        startActivity(intent);
        finish();
    }

    private Address getStartAddr() {
        Address startAddress = null;
        if (baseOrder != null && baseOrder.orderAddressVos != null && baseOrder.orderAddressVos.size() != 0) {
            for (Address address : baseOrder.orderAddressVos) {
                if (address.type == 1) {
                    startAddress = address;
                    break;
                }
            }
        }
        return startAddress;
    }

    private Address getEndAddr() {
        Address endAddr = null;
        if (baseOrder != null && baseOrder.orderAddressVos != null && baseOrder.orderAddressVos.size() != 0) {
            for (Address address : baseOrder.orderAddressVos) {
                if (address.type == 3) {
                    endAddr = address;
                    break;
                }
            }
        }
        return endAddr;
    }

    /**
     * 区域缩放
     *
     * @param latLngs
     */
    @Override
    public void boundsZoom(List<LatLng> latLngs) {
        if (latLngs == null) {
            return;
        }
        latLngs.add(lastLatlng);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : latLngs) {
            builder.include(latLng);
        }
        LatLngBounds bounds = builder.build();
        if (currentFragment instanceof ArriveStartFragment) {
            int left = DensityUtil.dp2px(this, 10);
            int bottom = DensityUtil.dp2px(this, 260);
            int top = DensityUtil.dp2px(this, 45);
            aMap.animateCamera(CameraUpdateFactory.newLatLngBoundsRect(bounds, left, left, top, bottom));
        } else {

            aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
                    bounds,
                    (int) (DensityUtil.getDisplayWidth(this) / 1.5),
                    (int) (DensityUtil.getDisplayWidth(this) / 2),
                    0));
        }
        //后续可能会使用这个latLngs 所以移除加入的上次位置
        latLngs.remove(lastLatlng);
    }

    /**
     * 定位位置缩放，默认级别19
     *
     * @param level
     */
    @Override
    public void locZoom(int level) {
        if (null != lastLatlng) {
            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLatlng, level == 0 ? 19 : level));
        }
    }

    RouteOverLay routeOverLay;

    /**
     * 导航路径展示
     *
     * @param ints
     * @param path
     */
    @Override
    public void showPath(int[] ints, AMapNaviPath path) {
        if (null != routeOverLay) {
            routeOverLay.removeFromMap();
        }
        routeOverLay = new RouteOverLay(aMap, path, this);
        routeOverLay.setStartPointBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.yellow_dot_small));
        routeOverLay.setEndPointBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.blue_dot_small));
        routeOverLay.setTrafficLine(true);

        RouteOverlayOptions options = new RouteOverlayOptions();
        options.setSmoothTraffic(BitmapFactory.decodeResource(getResources(), com.easymi.component.R.mipmap.custtexture_green));
        options.setUnknownTraffic(BitmapFactory.decodeResource(getResources(), com.easymi.component.R.mipmap.custtexture_no));
        options.setSlowTraffic(BitmapFactory.decodeResource(getResources(), com.easymi.component.R.mipmap.custtexture_slow));
        options.setJamTraffic(BitmapFactory.decodeResource(getResources(), com.easymi.component.R.mipmap.custtexture_bad));
        options.setVeryJamTraffic(BitmapFactory.decodeResource(getResources(), com.easymi.component.R.mipmap.custtexture_grayred));

        routeOverLay.setRouteOverlayOptions(options);

        routeOverLay.addToMap();
    }

    private DrivingRouteOverlay drivingRouteOverlay;

    /**
     * 路径规划展示
     *
     * @param result
     */
    @Override
    public void showPath(DriveRouteResult result) {
        if (drivingRouteOverlay != null) {
            drivingRouteOverlay.removeFromMap();
        }

        drivingRouteOverlay = new DrivingRouteOverlay(this, aMap,
                result.getPaths().get(0), result.getStartPos()
                , result.getTargetPos(), null);
//        drivingRouteOverlay.setRouteWidth(5);
        drivingRouteOverlay.setIsColorfulline(false);
        drivingRouteOverlay.setNodeIconVisibility(false);//隐藏转弯的节点
        drivingRouteOverlay.addToMap();
    }

    String leftDis;
    String leftTime;

    @Override
    public void showLeft(int dis, int time) {
        int km = dis / 1000;
        if (km > 1) {
            String disKm = new DecimalFormat("#0.0").format((double) dis / 1000);
            leftDis = "距离" +
                    "<font color='orange'><b><tt>" +
                    disKm + "</tt></b></font>" + getString(R.string.km);
        } else {
            leftDis = "距离" +
                    "<font color='black'><b><tt>" +
                    dis + "</tt></b></font>"
                    + getString(R.string.meter);
        }

        int hour = time / 60 / 60;
        int minute = time / 60;
        if (hour > 0) {
            leftTime = "预计" +
                    "<font color='orange'><b><tt>" +
                    hour +
                    "</tt></b></font>"
                    + getString(R.string.hour_) +
                    "<font color='orange'><b><tt>" +
                    time / 60 % 60 +
                    "</tt></b></font>" +
                    getString(R.string.minute_) +
                    "到达";
        } else {
            leftTime = "预计" +
                    "<font color='black'><b><tt>" +
                    minute +
                    "</tt></b></font>" +
                    getString(R.string.minute_) +
                    "到达";
        }
        if (null != smoothMoveMarker) {
            Marker marker = smoothMoveMarker.getMarker();
            marker.setSnippet(leftDis);//leftDis
            marker.setTitle(leftTime);//leftTime
            marker.showInfoWindow();
        }

        if ( dis < 200) { //小于200米)
            if (baseOrder.status == DJOrderStatus.GOTO_BOOKPALCE_ORDER){
                XApp.getInstance().syntheticVoice("距离上车点还有" + dis + "米");
                XApp.getInstance().shake();
            }else if (baseOrder.status == DJOrderStatus.GOTO_DESTINATION_ORDER){
                XApp.getInstance().syntheticVoice("距离下车点还有" + dis + "米");
                XApp.getInstance().shake();
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        LocReceiver.getInstance().addObserver(this);//添加位置订阅
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocReceiver.getInstance().deleteObserver(this);//取消位置订阅
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
//        notStartFragment.cancelTimer();
//        finishFragment.cancelTimer();
//        acceptSendFragment.cancelTimer();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        presenter.stopNavi();
        super.onDestroy();
    }

    @Override
    public void onTouch(MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
            Log.e("mapTouch", "-----map onTouched-----");
            isMapTouched = true;
//
//            if (null != acceptSendFragment) {
//                acceptSendFragment.mapStatusChanged();
//            }
        }
    }

    SmoothMoveMarker smoothMoveMarker;

    boolean delayAnimate = true;

    @Override
    public void receiveLoc(EmLoc location) {
        if (null == location) {
            return;
        }
        Log.e("locPos", "bearing 2 >>>>" + location.bearing);
        LatLng latLng = new LatLng(location.latitude, location.longitude);

        if (null == smoothMoveMarker) {//首次进入
            smoothMoveMarker = new SmoothMoveMarker(aMap);
            smoothMoveMarker.setDescriptor(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(getResources(), R.mipmap.ic_flow_my_pos)));
            smoothMoveMarker.setPosition(lastLatlng);
            smoothMoveMarker.setRotate(location.bearing);

        } else {
            if (null != myFirstMarker) {//去除掉首次的位置marker
                myFirstMarker.remove();
            }
            List<LatLng> latLngs = new ArrayList<>();
            latLngs.add(lastLatlng);
            latLngs.add(latLng);
            smoothMoveMarker.setPosition(lastLatlng);
            smoothMoveMarker.setPoints(latLngs);
            smoothMoveMarker.setTotalDuration(Config.NORMAL_LOC_TIME / 1000);
            smoothMoveMarker.setRotate(location.bearing);
            smoothMoveMarker.startSmoothMove();
            Marker marker = smoothMoveMarker.getMarker();
            if (null != marker) {
                marker.setDraggable(false);
                marker.setInfoWindowEnable(true);
                marker.setClickable(false);
                marker.setAnchor(0.5f, 0.5f);
            }
        }

//        if (dymOrder == null) {
//            dymOrder = DymOrder.findByIDType(zxOrder.orderId, zxOrder.orderType);
//        }

        if (null != dymOrder) {
            if (dymOrder.orderStatus == ZXOrderStatus.ACCEPT_ING
                    || dymOrder.orderStatus == ZXOrderStatus.SEND_ING) {
//                if (!isMapTouched && currentFragment instanceof AcceptSendFragment) {
//                    aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19), delayAnimate ? Config.NORMAL_LOC_TIME : 0, null);
//                    delayAnimate = true;
//                }
            }
        }

        lastLatlng = latLng;
    }

    /**
     * fragment 切换
     *
     * @param targetFragment
     * @return
     */
    private FragmentTransaction switchFragment(Fragment targetFragment) {

        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        if (!targetFragment.isAdded()) {
            //第一次使用switchFragment()时currentFragment为null，所以要判断一下
            if (currentFragment != null) {
                transaction.hide(currentFragment);
            }
            transaction.add(R.id.fragment_frame, targetFragment, targetFragment.getClass().getName());
        } else {
            transaction
                    .hide(currentFragment)
                    .show(targetFragment);
        }
        currentFragment = targetFragment;

        initToolBar();
        return transaction;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMapClick(LatLng latLng) {
//        if (null != smoothMoveMarker) {
//            smoothMoveMarker.getMarker().hideInfoWindow();
//        }
    }

    @Override
    public void onBackPressed() {
        toolbar.leftIcon.callOnClick();
    }

    @Override
    public void onCancelOrder(long orderId, String orderType) {

    }

    @Override
    public void onFinishOrder(long orderId, String orderType) {

    }
}