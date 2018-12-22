package com.easymin.chartered.flowMvp;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

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
import com.easymi.common.entity.OrderCustomer;
import com.easymi.common.receiver.CancelOrderReceiver;
import com.easymi.component.Config;
import com.easymi.component.ZXOrderStatus;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.BaseOrder;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.entity.EmLoc;
import com.easymi.component.loc.LocObserver;
import com.easymi.component.loc.LocReceiver;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc3;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.result.EmResult2;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.utils.DensityUtil;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.Log;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.overlay.DrivingRouteOverlay;
import com.easymin.R;
import com.easymin.chartered.adapter.LeftWindowAdapter;
import com.easymin.chartered.receiver.OrderFinishReceiver;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: FlowActivity
 * Author: shine
 * Date: 2018/12/18 下午1:52
 * Description:
 * History:
 */
@Route(path = "/chartered/FlowActivity")
public class FlowActivity extends RxBaseActivity implements
        FlowContract.View,
        LocObserver,
        CancelOrderReceiver.OnCancelListener,
        AMap.OnMapTouchListener,
        OrderFinishReceiver.OnFinishListener, AMap.OnMarkerClickListener, AMap.OnMapClickListener {

    CusToolbar cusToolbar;
    MapView mapView;
    FrameLayout fragmentFrame;

    AMap aMap;

    private LatLng lastLatlng;
    private Marker myFirstMarker;

    FlowPresenter presenter;

    Fragment currentFragment;

    private ActFraCommBridge bridge;

    DymOrder dymOrder;

    public static boolean isMapTouched = false;

    private boolean isOrderLoadOk = false;//订单查询是否完成

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
        BaseOrder baseOrder = (BaseOrder) getIntent().getSerializableExtra("baseOrder");

        if (null == baseOrder) {
            finish();
            return;
        }

        presenter = new FlowPresenter(this, this);

        mapView = findViewById(R.id.map_view);
        fragmentFrame = findViewById(R.id.fragment_frame);

        mapView.onCreate(savedInstanceState);
        initMap();
        initBridget();
        initFragment();


    }

    @Override
    public void changeToolbar(int flag) {
//        DymOrder dymOrder = DymOrder.findByIDType(zxOrder.orderId, zxOrder.orderType);
//        if (null == dymOrder) {
//            return;
//        }
//        if (flag == StaticVal.TOOLBAR_NOT_START) {
            cusToolbar.setLeftBack(view -> finish());
            cusToolbar.setTitle("行程未开始");
            cusToolbar.setRightGone();
//        } else if (flag == StaticVal.TOOLBAR_CHANGE_ACCEPT) {
//            cusToolbar.setLeftBack(view -> {
//                if (dymOrder.orderStatus <= ZXOrderStatus.WAIT_START) {
//                    bridge.toNotStart();
//                } else if (dymOrder.orderStatus == ZXOrderStatus.ACCEPT_PLAN) {
//                    finish();
//                } else if (dymOrder.orderStatus == ZXOrderStatus.ACCEPT_ING) {
//                    bridge.toCusList(StaticVal.PLAN_ACCEPT);
//                }
//            });
//            cusToolbar.setTitle("行程规划");
//            cusToolbar.setRightGone();
//        } else if (flag == StaticVal.TOOLBAR_CHANGE_SEND) {
//            cusToolbar.setLeftBack(view -> {
//                if (dymOrder.orderStatus <= ZXOrderStatus.WAIT_START) {
//                    bridge.toNotStart();
//                } else if (dymOrder.orderStatus == ZXOrderStatus.SEND_PLAN) {
//                    finish();
//                } else if (dymOrder.orderStatus == ZXOrderStatus.ACCEPT_ING) {
//                    bridge.toCusList(StaticVal.PLAN_ACCEPT);
//                } else if (dymOrder.orderStatus == ZXOrderStatus.SEND_ING) {
//                    bridge.toCusList(StaticVal.PLAN_SEND);
//                }
//            });
//            cusToolbar.setTitle("行程规划");
//            cusToolbar.setRightGone();
//        } else if (flag == StaticVal.TOOLBAR_ACCEPT_ING) {
//            cusToolbar.setLeftBack(view -> bridge.toAcSend());
//            cusToolbar.setTitle("正在接人");
//            cusToolbar.setRightText(R.string.change_sequence, view -> {
//                //展示修改顺序的pop
//                changePopWindow = new ChangePopWindow(FlowActivity.this);
//                changePopWindow.setOnClickListener(view1 -> {
//                    long id = view1.getId();
//                    if (id == R.id.pop_change_accept) {
//                        bridge.toChangeSeq(StaticVal.PLAN_ACCEPT);
//                    } else if (id == R.id.pop_change_send) {
//                        bridge.toChangeSeq(StaticVal.PLAN_SEND);
//                    }
//                });
//                changePopWindow.show(view);
//            });
//        } else if (flag == StaticVal.TOOLBAR_SEND_ING) {
//            cusToolbar.setLeftBack(view -> bridge.toAcSend());
//            cusToolbar.setTitle("正在送人");
//            cusToolbar.setRightText(R.string.change_sequence, view -> {
//                //展示修改顺序的pop
//                changePopWindow = new ChangePopWindow(FlowActivity.this);
//                changePopWindow.hideAccept();
//                changePopWindow.setOnClickListener(view1 -> {
//                    long id = view1.getId();
//                    if (id == R.id.pop_change_accept) {
//                        bridge.toChangeSeq(StaticVal.PLAN_ACCEPT);
//                    } else if (id == R.id.pop_change_send) {
//                        bridge.toChangeSeq(StaticVal.PLAN_SEND);
//                    }
//                });
//                changePopWindow.show(view);
//            });
//        } else if (flag == StaticVal.TOOLBAR_FLOW) {
//            cusToolbar.setLeftBack(view -> finish());
//            cusToolbar.setRightText("查看规划", view -> {
//                if (dymOrder.orderStatus == ZXOrderStatus.ACCEPT_ING) {
//                    bridge.toCusList(StaticVal.PLAN_ACCEPT);
//                } else {
//                    bridge.toCusList(StaticVal.PLAN_SEND);
//                }
//            });
//            OrderCustomer current = acceptSendFragment.getCurrent();
//            if (current.status == 0) { //未接
//                if (current.subStatus == 0) {
//                    cusToolbar.setTitle("前往预约地");
//                } else if (current.subStatus == 1) {
//                    cusToolbar.setTitle("等待中");
//                }
//            } else if (current.status == 3) {
//                cusToolbar.setTitle("行程中");
//            }
//        } else if (flag == StaticVal.TOOLBAR_FINISH) {
//            cusToolbar.setLeftBack(view -> finish());
//            cusToolbar.setTitle("行程结束");
//            cusToolbar.setRightGone();
//        }
    }



    @Override
    public RxManager getManager() {
        return mRxManager;
    }

    @Override
    public void initToolBar() {
        super.initToolBar();
        cusToolbar = findViewById(R.id.cus_toolbar);
    }

    /**
     * 初始化fragment
     */
    @Override
    public void initFragment() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        if (isOrderLoadOk) {
            showFragmentByStatus();
        }
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
            public void addMarker(LatLng latLng, int flag) {
                FlowActivity.this.addMarker(latLng, flag);
            }

            @Override
            public void toOrderList() {
                FlowActivity.this.finish();
            }

            @Override
            public void changeToolbar(int flag) {
                FlowActivity.this.changeToolbar(flag);
            }

            @Override
            public void clearMap() {
                aMap.clear();
                smoothMoveMarker = null;
                initMap();
                receiveLoc(EmUtil.getLastLoc());//第一时间加上自身位置
            }

            @Override
            public void routePath(LatLng toLatlng) {
                presenter.routeLineByNavi(lastLatlng, null, toLatlng);
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
//                presenter.navi(latLng, orderId);
            }
        };
    }


    /**
     * 添加起终种类的marker
     *
     * @param latLng
     * @param flag
     */
    @Override
    public void addMarker(LatLng latLng, int flag) {
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(latLng);
        markerOption.draggable(false);//设置Marker可拖动
//        if (flag == StaticVal.MARKER_FLAG_START) {
//            markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
//                    .decodeResource(getResources(), R.mipmap.ic_start)));
//        } else if (flag == StaticVal.MARKER_FLAG_END) {
//            markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
//                    .decodeResource(getResources(), R.mipmap.ic_end)));
//        }
        // 将Marker设置为贴地显示，可以双指下拉地图查看效果
        markerOption.setFlat(true);//设置marker平贴地图效果
        aMap.addMarker(markerOption);

    }

    @Override
    public void addMarker(LatLng latLng, int flag, int num) {

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
//        if (currentFragment instanceof ChangeSeqFragment) {
//            int left = DensityUtil.dp2px(this, 10);
//            int bottom = DensityUtil.dp2px(this, 260);
//            int top = DensityUtil.dp2px(this, 45);
//            aMap.animateCamera(CameraUpdateFactory.newLatLngBoundsRect(bounds, left, left, top, bottom));
//        } else {
//
//            aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
//                    bounds,
//                    (int) (DensityUtil.getDisplayWidth(this) / 1.5),
//                    (int) (DensityUtil.getDisplayWidth(this) / 2),
//                    0));
//        }


        latLngs.remove(lastLatlng);//后续可能会使用这个latLngs 所以移除加入的上次位置
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
//        acceptSendFragment.showLeft(dis);
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
    }


    @Override
    public void showFragmentByStatus() {
//        DymOrder dymOrder = DymOrder.findByIDType(zxOrder.orderId, zxOrder.orderType);
//        if (null != dymOrder) {
//            if (dymOrder.orderStatus <= ZXOrderStatus.WAIT_START) {
//                bridge.toNotStart();
//            } else if (dymOrder.orderStatus == ZXOrderStatus.ACCEPT_PLAN) {
//                bridge.toChangeSeq(StaticVal.PLAN_ACCEPT);
//            } else if (dymOrder.orderStatus == ZXOrderStatus.SEND_PLAN) {
//                bridge.toChangeSeq(StaticVal.PLAN_SEND);
//            } else if (dymOrder.orderStatus == ZXOrderStatus.ACCEPT_ING
//                    || dymOrder.orderStatus == ZXOrderStatus.SEND_ING) {
//                bridge.toAcSend();
//            } else if (dymOrder.orderStatus == ZXOrderStatus.SEND_OVER) {
//
//            }
//        }
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
        cusToolbar.leftIcon.callOnClick();
    }

    @Override
    public void onCancelOrder(long orderId, String orderType) {

    }

    @Override
    public void onFinishOrder(long orderId, String orderType) {

    }
}