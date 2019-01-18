package com.easymi.taxi.flowMvp;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
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
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.utils.overlay.SmoothMoveMarker;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.RouteOverlayOptions;
import com.amap.api.navi.view.RouteOverLay;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.route.DriveRouteResult;
import com.easymi.common.entity.Address;
import com.easymi.common.entity.BuildPushData;
import com.easymi.common.entity.PassengerLocation;
import com.easymi.common.push.FeeChangeObserver;
import com.easymi.common.push.HandlePush;
//import com.easymi.common.push.MQTTService;
import com.easymi.common.push.MqttManager;
import com.easymi.common.push.PassengerLocObserver;
import com.easymi.common.trace.TraceInterface;
import com.easymi.common.trace.TraceReceiver;
import com.easymi.component.Config;
import com.easymi.component.ZCOrderStatus;
import com.easymi.component.activity.PlaceActivity;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.entity.EmLoc;
import com.easymi.component.entity.ZCSetting;
import com.easymi.component.loc.LocObserver;
import com.easymi.component.loc.LocReceiver;
import com.easymi.component.loc.LocService;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.utils.AesUtil;
import com.easymi.component.utils.DensityUtil;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.Log;
import com.easymi.component.utils.MapUtil;
import com.easymi.component.utils.PhoneUtil;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.CusBottomSheetDialog;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.LoadingButton;
import com.easymi.component.widget.overlay.DrivingRouteOverlay;
import com.easymi.taxi.R;
import com.easymi.taxi.activity.CancelActivity;
import com.easymi.taxi.activity.ConsumerInfoActivity;
import com.easymi.taxi.activity.TransferActivity;
import com.easymi.taxi.entity.ConsumerInfo;
import com.easymi.taxi.entity.TaxiOrder;
import com.easymi.taxi.flowMvp.oldCalc.OldRunningActivity;
import com.easymi.taxi.flowMvp.oldCalc.OldWaitActivity;
import com.easymi.taxi.fragment.AcceptFragment;
import com.easymi.taxi.fragment.ArriveStartFragment;
import com.easymi.taxi.fragment.RunningFragment;
//import com.easymi.taxi.fragment.SettleFragmentDialog;
import com.easymi.taxi.fragment.SlideArriveStartFragment;
import com.easymi.taxi.fragment.ToStartFragment;
import com.easymi.taxi.fragment.WaitFragment;
import com.easymi.taxi.receiver.CancelOrderReceiver;
import com.easymi.taxi.receiver.OrderFinishReceiver;
import com.easymi.taxi.widget.FlowPopWindow;
import com.easymi.taxi.widget.RefuseOrderDialog;
import com.easymi.taxi.widget.TaxiSettleDialog;
import com.easymin.driver.securitycenter.utils.AudioUtil;
import com.easymin.driver.securitycenter.utils.CenterUtil;
import com.google.gson.Gson;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: FinishActivity
 *@Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */
@Route(path = "/taxi/FlowActivity")
public class FlowActivity extends RxBaseActivity implements FlowContract.View,
        LocObserver,
        TraceInterface,
        FeeChangeObserver,
        PassengerLocObserver,
        CancelOrderReceiver.OnCancelListener,
        AMap.OnMapTouchListener,
        OrderFinishReceiver.OnFinishListener {
    public static final int CANCEL_ORDER = 0X01;
    public static final int CHANGE_END = 0X02;
    public static final int CHANGE_ORDER = 0X03;

    CusToolbar toolbar;
    TextView nextPlace;
    TextView leftTimeText;
    TextView orderNumberText;
    LinearLayout drawerFrame;
    private MapView mapView;
    private TextView tvMark;

    ExpandableLayout expandableLayout;

    FlowPopWindow popWindow;


    /**
     * 未接单top
     */
    RelativeLayout not_accept_layout;
    TextView left_time_dis;
    LinearLayout to_appoint_navi_con_1;

    /**
     * 前往终点top
     */
    RelativeLayout go_layout;
    TextView to_appoint_time;
    LinearLayout naviCon;


    private TaxiOrder taxiOrder;

    private FlowPresenter presenter;
    /**
     * activity和fragment的通信接口
     */
    private ActFraCommBridge bridge;

    private TraceReceiver traceReceiver;
    private CancelOrderReceiver cancelOrderReceiver;
    private OrderFinishReceiver orderFinishReceiver;

    private AMap aMap;

    private long orderId;

    private AlbumOrientationEventListener mAlbumOrientationEventListener;

    @Override
    public int getLayoutId() {
        return R.layout.taxi_activity_flow;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        // 屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mAlbumOrientationEventListener = new AlbumOrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL);
        if (mAlbumOrientationEventListener.canDetectOrientation()) {
            mAlbumOrientationEventListener.enable();
        }

        orderId = getIntent().getLongExtra("orderId", -1);
        if (orderId == -1) {
            finish();
            return;
        }

        presenter = new FlowPresenter(this, this);

        toolbar = findViewById(R.id.toolbar);
        nextPlace = findViewById(R.id.next_place);
        leftTimeText = findViewById(R.id.left_time);
        orderNumberText = findViewById(R.id.order_number_text);
        drawerFrame = findViewById(R.id.drawer_frame);
        expandableLayout = findViewById(R.id.expandable_layout);
        tvMark = findViewById(R.id.tvMark);

        /**
         * 未接单top
         */
        not_accept_layout = findViewById(R.id.not_accept_layout);
        left_time_dis = findViewById(R.id.left_time_dis);
        to_appoint_navi_con_1 = findViewById(R.id.to_appoint_navi_con_1);

        /**
         * 前往终点top
         */
        go_layout = findViewById(R.id.go_layout);
        to_appoint_time = findViewById(R.id.to_appoint_time);
        naviCon = findViewById(R.id.navi_con);

        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        initMap();
        initPop();
        initToolbar();
    }

    @Override
    public void initToolbar() {
        toolbar.setLeftIcon(R.drawable.ic_arrow_back, v -> finish());
        toolbar.setRightIcon(R.drawable.ic_more_horiz_white_24dp, v -> {
            if (popWindow.isShowing()) {
                popWindow.dismiss();
            } else {
                ZCSetting setting = ZCSetting.findOne();
                boolean notCancel = setting.canCancelOrder != 1;
                boolean notChangeOrder = setting.employChangeOrder != 1;
                if (notCancel || taxiOrder.status == ZCOrderStatus.NEW_ORDER || taxiOrder.status == ZCOrderStatus.PAIDAN_ORDER || taxiOrder.status >= ZCOrderStatus.GOTO_DESTINATION_ORDER) {
                    popWindow.hideCancel();
                } else {
                    popWindow.showCancel();
                }
//                if (taxiOrder.status == ZCOrderStatus.NEW_ORDER || taxiOrder.status == ZCOrderStatus.PAIDAN_ORDER) {
//                    popWindow.hideConsumer();
//                } else {
//                    popWindow.showConsumer();
//                }
//                if ((taxiOrder.status == ZCOrderStatus.TAKE_ORDER || taxiOrder.status == ZCOrderStatus.GOTO_BOOKPALCE_ORDER|| taxiOrder.status == ZCOrderStatus.ARRIVAL_BOOKPLACE_ORDER) && !notChangeOrder) {
//                    popWindow.showTransfer();
//                } else {
//                    popWindow.hideTransfer();
//                }
                popWindow.show(v);
            }
        });
    }

    @Override
    public void initPop() {
        popWindow = new FlowPopWindow(this);
        popWindow.setOnClickListener(view -> {
            int i = view.getId();
            if (i == R.id.pop_cancel_order) {
                Intent intent = new Intent(FlowActivity.this, CancelActivity.class);
                startActivityForResult(intent, CANCEL_ORDER);
            } else if (i == R.id.pop_contract_service) {
                String phone = taxiOrder.companyPhone;
//                String phone = "111111111";
                PhoneUtil.call(FlowActivity.this, phone);
            }
//            else if (i == R.id.pop_consumer_msg) {
//                Intent intent = new Intent(FlowActivity.this, ConsumerInfoActivity.class);
//                intent.putExtra("orderId", orderId);
//                startActivity(intent);
//            }
//            else if (i == R.id.pop_order_transfer) {
//                Intent intent = new Intent(FlowActivity.this, TransferActivity.class);
//                intent.putExtra("order", taxiOrder);
//                startActivityForResult(intent, CHANGE_ORDER);
//            }
        });
    }

    @Override
    public void showTopView() {
        if (TextUtils.isEmpty(taxiOrder.orderRemark)) {
            tvMark.setText("无备注");
        } else {
            tvMark.setText(taxiOrder.orderRemark);
        }
        orderNumberText.setText(taxiOrder.orderNo);
        drawerFrame.setOnClickListener(view -> {
            if (expandableLayout.isExpanded()) {
                expandableLayout.collapse();
            } else {
                expandableLayout.expand();
            }
        });

        if (taxiOrder.status == ZCOrderStatus.NEW_ORDER || taxiOrder.status == ZCOrderStatus.PAIDAN_ORDER) {
            hideTops();
            go_layout.setVisibility(View.VISIBLE);
            naviCon.setOnClickListener(view -> presenter.navi(new LatLng(getStartAddr().latitude,
                    getStartAddr().longitude), getStartAddr().poi, orderId));
//            not_accept_layout.setVisibility(View.VISIBLE);
//            to_appoint_navi_con_1.setOnClickListener(view -> presenter.navi(new LatLng(getStartAddr().latitude,
//                    getStartAddr().longitude), getStartAddr().poi, orderId));

        } else if (taxiOrder.status == ZCOrderStatus.TAKE_ORDER
                || taxiOrder.status == ZCOrderStatus.GOTO_BOOKPALCE_ORDER) {
            hideTops();
            go_layout.setVisibility(View.VISIBLE);
            naviCon.setOnClickListener(view -> presenter.navi(new LatLng(getStartAddr().latitude,
                    getStartAddr().longitude), getStartAddr().poi, orderId));

            to_appoint_time.setVisibility(View.GONE);
//            String time = getString(R.string.please_start_at)
//                    + TimeUtil.getTime("HH:mm", taxiOrder.bookTime * 1000)
//                    + getString(R.string.arrive_start);
//
//            SpannableString ss = new SpannableString(time);
//            ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#3c98e3"));
//            int startIndex = 2;
//            int endIndex = ss.length() - 7;
//            ss.setSpan(colorSpan, startIndex, endIndex, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
//            to_appoint_time.setText(ss);
        } else if (taxiOrder.status == ZCOrderStatus.ARRIVAL_BOOKPLACE_ORDER) {
            hideTops();
            go_layout.setVisibility(View.VISIBLE);
            naviCon.setOnClickListener(view -> presenter.navi(new LatLng(getEndAddr().latitude,
                    getEndAddr().longitude), getEndAddr().poi, orderId));

//            String time = getString(R.string.please_start_at)
//                    + TimeUtil.getTime("HH:mm", taxiOrder.bookTime * 1000)
//                    + getString(R.string.arrive_start);
//
//            SpannableString ss = new SpannableString(time);
//            ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#3c98e3"));
//            int startIndex = 2;
//            int endIndex = ss.length() - 7;
//            ss.setSpan(colorSpan, startIndex, endIndex, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
//            to_appoint_time.setText(ss);
            to_appoint_time.setVisibility(View.GONE);
        } else if (taxiOrder.status == ZCOrderStatus.START_WAIT_ORDER) {
            hideTops();
            go_layout.setVisibility(View.VISIBLE);
            naviCon.setOnClickListener(view -> presenter.navi(new LatLng(getStartAddr().latitude,
                    getStartAddr().longitude), getStartAddr().poi, orderId));

//            String time = getString(R.string.please_start_at)
//                    + TimeUtil.getTime("HH:mm", taxiOrder.bookTime * 1000)
//                    + getString(R.string.arrive_start);
//            SpannableString ss = new SpannableString(time);
//            ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#3c98e3"));
//            int startIndex = 2;
//            int endIndex = ss.length() - 7;
//            ss.setSpan(colorSpan, startIndex, endIndex, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            to_appoint_time.setText("中途等待");
            to_appoint_time.setVisibility(View.VISIBLE);
        } else {
            hideTops();
            go_layout.setVisibility(View.VISIBLE);
            to_appoint_time.setVisibility(View.GONE);
            naviCon.setOnClickListener(view -> {
                if (null != getEndAddr()) {
                    presenter.navi(new LatLng(getEndAddr().latitude, getEndAddr().longitude), getEndAddr().address, orderId);
                } else {
                    ToastUtil.showMessage(FlowActivity.this, getString(R.string.illegality_place));
                }
            });
        }
        if (taxiOrder.status == ZCOrderStatus.NEW_ORDER ||
                taxiOrder.status == ZCOrderStatus.PAIDAN_ORDER ||
                taxiOrder.status == ZCOrderStatus.TAKE_ORDER ||
                taxiOrder.status == ZCOrderStatus.GOTO_BOOKPALCE_ORDER ||
                taxiOrder.status == ZCOrderStatus.ARRIVAL_BOOKPLACE_ORDER) {
            nextPlace.setText(taxiOrder.getStartSite().address);
        } else {
            nextPlace.setText(taxiOrder.getEndSite().address);
        }

    }

    WaitFragment waitFragment;
    RunningFragment runningFragment;
//    SettleFragmentDialog settleFragmentDialog;

    @Override
    public void showBottomFragment(TaxiOrder taxiOrder) {

//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//动态设置为竖屏

        if (taxiOrder.status == ZCOrderStatus.PAIDAN_ORDER || taxiOrder.status == ZCOrderStatus.NEW_ORDER) {
            toolbar.setTitle(R.string.status_pai);
            AcceptFragment acceptFragment = new AcceptFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("taxiOrder", taxiOrder);
            acceptFragment.setArguments(bundle);
            acceptFragment.setBridge(bridge);

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
            transaction.replace(R.id.flow_frame, acceptFragment);
            transaction.commit();
        } else if (taxiOrder.status == ZCOrderStatus.TAKE_ORDER) {
            toolbar.setTitle(R.string.status_jie);
            ToStartFragment toStartFragment = new ToStartFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("taxiOrder", taxiOrder);
            toStartFragment.setArguments(bundle);
            toStartFragment.setBridge(bridge);

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
            transaction.replace(R.id.flow_frame, toStartFragment);
            transaction.commit();
        } else if (taxiOrder.status == ZCOrderStatus.GOTO_BOOKPALCE_ORDER) {
            toolbar.setTitle(R.string.status_to_start);
            SlideArriveStartFragment fragment = new SlideArriveStartFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("taxiOrder", taxiOrder);
            fragment.setArguments(bundle);
            fragment.setBridge(bridge);

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
            transaction.replace(R.id.flow_frame, fragment);
            transaction.commit();
        } else if (taxiOrder.status == ZCOrderStatus.ARRIVAL_BOOKPLACE_ORDER) {
            toolbar.setTitle(R.string.status_arrive_start);
            ArriveStartFragment fragment = new ArriveStartFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("taxiOrder", taxiOrder);
            fragment.setArguments(bundle);
            fragment.setBridge(bridge);

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
            transaction.replace(R.id.flow_frame, fragment);
            transaction.commit();
        } else if (taxiOrder.status == ZCOrderStatus.START_WAIT_ORDER) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);//动态设置为遵循传感器
            toolbar.setTitle(R.string.wait_consumer);
            waitFragment = new WaitFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("taxiOrder", DymOrder.findByIDType(orderId, Config.ZHUANCHE));
            waitFragment.setArguments(bundle);
            waitFragment.setBridge(bridge);

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
            transaction.replace(R.id.flow_frame, waitFragment);
            transaction.commit();
        } else if (taxiOrder.status == ZCOrderStatus.GOTO_DESTINATION_ORDER) {
            showToEndFragment();
//            if (isToFeeDetail) {
//                if (settleFragmentDialog != null && settleFragmentDialog.isShowing()) {
//                    settleFragmentDialog.setDjOrder(taxiOrder);
//                } else {
//                    settleFragmentDialog = new SettleFragmentDialog(FlowActivity.this, taxiOrder, bridge);
//                    settleFragmentDialog.show();
//                }
//                isToFeeDetail = false;
//            }
        } else if (taxiOrder.status == ZCOrderStatus.ARRIVAL_DESTINATION_ORDER) {
            toolbar.setTitle(R.string.settle);
            runningFragment = new RunningFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("taxiOrder", DymOrder.findByIDType(orderId, Config.ZHUANCHE));
            runningFragment.setArguments(bundle);
            runningFragment.setBridge(bridge);

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
            transaction.replace(R.id.flow_frame, runningFragment);
            transaction.commit();

//            if (settleFragmentDialog != null && settleFragmentDialog.isShowing() && !isToFeeDetail) {
//                settleFragmentDialog.setDjOrder(taxiOrder);
//
//                DymOrder dymOrder = DymOrder.findByIDType(orderId, Config.ZHUANCHE);//确认费用后直接弹出支付页面
//                if (null != dymOrder) {
//                    bridge.doPay(dymOrder.orderShouldPay);
//                }
//            } else {
//                if (settleFragmentDialog != null && settleFragmentDialog.isShowing()) {
//                    settleFragmentDialog.setDjOrder(taxiOrder);
//                } else {
//                    settleFragmentDialog = new SettleFragmentDialog(FlowActivity.this, taxiOrder, bridge);
//                    settleFragmentDialog.show();
//                }
//                isToFeeDetail = false;
//            }
        }

        boolean forceOre = XApp.getMyPreferences().getBoolean(Config.SP_ALWAYS_OREN, false);
//        if (forceOre && !fromOld) {//始终横屏计价将自动跳转到横屏界面
//            toWhatOldByOrder(taxiOrder);
//        }
    }

    @Override
    public void showOrder(TaxiOrder taxiOrder) {
        if (null == taxiOrder) {
            finish();
        } else {
            this.taxiOrder = taxiOrder;
            showTopView();
            initBridge();
            showBottomFragment(taxiOrder);
            showMapBounds();
        }
    }

    @Override
    public void initMap() {
        aMap = mapView.getMap();
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.getUiSettings().setRotateGesturesEnabled(false);
        aMap.getUiSettings().setRotateGesturesEnabled(false);
        aMap.getUiSettings().setTiltGesturesEnabled(false);//倾斜手势
        aMap.getUiSettings().setLogoBottomMargin(-50);//隐藏logo

        aMap.setOnMapTouchListener(this);

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

    private Marker startMarker;
    private Marker endMarker;

    private Marker myFirstMarker;//首次进入时默认位置的marker

    @Override
    public void showMapBounds() {
        List<LatLng> latLngs = new ArrayList<>();
        if (taxiOrder.status == ZCOrderStatus.NEW_ORDER
                || taxiOrder.status == ZCOrderStatus.PAIDAN_ORDER
                || taxiOrder.status == ZCOrderStatus.TAKE_ORDER
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
        } else if (taxiOrder.status == ZCOrderStatus.GOTO_BOOKPALCE_ORDER) {
            if (null != getStartAddr()) {
                latLngs.add(new LatLng(getStartAddr().latitude, getStartAddr().longitude));
                presenter.routePlanByNavi(getStartAddr().latitude, getStartAddr().longitude);
            } else {
                presenter.stopNavi();
                leftTimeText.setText("");
            }
            LatLngBounds bounds = MapUtil.getBounds(latLngs, lastLatlng);
            aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, (int) (DensityUtil.getDisplayWidth(this) / 1.5), (int) (DensityUtil.getDisplayWidth(this) / 1.5), 0));
        } else if (taxiOrder.status == ZCOrderStatus.ARRIVAL_BOOKPLACE_ORDER
                || taxiOrder.status == ZCOrderStatus.GOTO_DESTINATION_ORDER
                || taxiOrder.status == ZCOrderStatus.START_WAIT_ORDER
                || taxiOrder.status == ZCOrderStatus.ARRIVAL_DESTINATION_ORDER
                ) {
            if (null != getEndAddr()) {
                latLngs.add(new LatLng(getEndAddr().latitude, getEndAddr().longitude));
                presenter.routePlanByNavi(getEndAddr().latitude, getEndAddr().longitude);
            }
            if (taxiOrder.status == ZCOrderStatus.GOTO_DESTINATION_ORDER) {
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
    public void cancelSuc() {
        ToastUtil.showMessage(this, getString(R.string.cancel_suc));
        finish();
    }

    @Override
    public void refuseSuc() {
        ToastUtil.showMessage(this, getString(R.string.refuse_suc));
        finish();
    }

    RouteOverLay routeOverLay;

    /**
     * 绘制路径规划结果
     *
     * @param path AMapNaviPath
     */
    @Override
    public void showPath(int[] ints, AMapNaviPath path) {
        if (null != routeOverLay) {
            routeOverLay.removeFromMap();
        }
//        aMap.moveCamera(CameraUpdateFactory.changeTilt(0));
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

    @Override
    public void showPath(DriveRouteResult result) {
        if (drivingRouteOverlay != null) {
            drivingRouteOverlay.removeFromMap();
        }
        drivingRouteOverlay = new DrivingRouteOverlay(this, aMap,
                result.getPaths().get(0), result.getStartPos()
                , result.getTargetPos(), null);
        drivingRouteOverlay.setRouteWidth(0);
        drivingRouteOverlay.setIsColorfulline(false);
        drivingRouteOverlay.setNodeIconVisibility(false);//隐藏转弯的节点
        drivingRouteOverlay.addToMap();
        drivingRouteOverlay.zoomToSpan();
        List<LatLng> latLngs = new ArrayList<>();
        latLngs.add(new LatLng(result.getStartPos().getLatitude(), result.getStartPos().getLongitude()));
        latLngs.add(new LatLng(result.getTargetPos().getLatitude(), result.getTargetPos().getLongitude()));
        EmLoc lastLoc = EmUtil.getLastLoc();
        latLngs.add(new LatLng(lastLoc.latitude, lastLoc.longitude));
        LatLngBounds bounds = MapUtil.getBounds(latLngs);
        aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, (int) (DensityUtil.getDisplayWidth(this) / 1.5), (int) (DensityUtil.getDisplayWidth(this) / 1.5), 0));
    }

    private CusBottomSheetDialog bottomSheetDialog;//支付弹窗

    @Override
    public void showPayType(double money, ConsumerInfo consumerInfo) {

//        if (null != settleFragmentDialog) {
//            settleFragmentDialog.dismiss();
//        }

        bottomSheetDialog = new CusBottomSheetDialog(this);

        View view = LayoutInflater.from(this).inflate(R.layout.taxi_pay_type_dialog, null, false);

        ImageView pay1Img = view.findViewById(R.id.pay_1_img);
        ImageView pay2Img = view.findViewById(R.id.pay_2_img);
        ImageView pay3Img = view.findViewById(R.id.pay_3_img);
        ImageView pay4Img = view.findViewById(R.id.pay_4_img);

        TextView pay1Text = view.findViewById(R.id.pay_1_text);
        TextView pay2Text = view.findViewById(R.id.pay_2_text);
        TextView pay3Text = view.findViewById(R.id.pay_3_text);
        TextView pay4Text = view.findViewById(R.id.pay_4_text);

        View pay1Empty = view.findViewById(R.id.pay_1_empty);
        View pay2Empty = view.findViewById(R.id.pay_2_empty);
        View pay3Empty = view.findViewById(R.id.pay_3_empty);
        View pay4Empty = view.findViewById(R.id.pay_4_empty);

        RadioButton pay1Btn = view.findViewById(R.id.pay_1_btn);
        RadioButton pay2Btn = view.findViewById(R.id.pay_2_btn);
        RadioButton pay3Btn = view.findViewById(R.id.pay_3_btn);
        RadioButton pay4Btn = view.findViewById(R.id.pay_4_btn);

        if (consumerInfo.consumerBalance < money) {
            pay2Text.setVisibility(View.GONE);
            pay2Empty.setVisibility(View.GONE);
            pay2Btn.setVisibility(View.GONE);
            pay2Img.setVisibility(View.GONE);
        }
        if (!consumerInfo.canSign) {
            pay3Text.setVisibility(View.GONE);
            pay3Empty.setVisibility(View.GONE);
            pay3Btn.setVisibility(View.GONE);
            pay3Img.setVisibility(View.GONE);
        }
        boolean canDaifu = ZCSetting.findOne().isPaid == 1;
        if (!canDaifu) {
            pay4Text.setVisibility(View.GONE);
            pay4Empty.setVisibility(View.GONE);
            pay4Btn.setVisibility(View.GONE);
            pay4Img.setVisibility(View.GONE);
        }

        pay1Btn.setOnClickListener(view13 -> bottomSheetDialog.dismiss());
        pay1Text.setOnClickListener(view13 -> bottomSheetDialog.dismiss());
        pay1Empty.setOnClickListener(view13 -> bottomSheetDialog.dismiss());
        pay1Img.setOnClickListener(view13 -> bottomSheetDialog.dismiss());

        pay2Empty.setOnClickListener(view14 -> pay2Btn.setChecked(true));
        pay2Text.setOnClickListener(view14 -> pay2Btn.setChecked(true));
        pay2Img.setOnClickListener(view14 -> pay2Btn.setChecked(true));

        pay3Empty.setOnClickListener(view14 -> pay3Btn.setChecked(true));
        pay3Text.setOnClickListener(view14 -> pay3Btn.setChecked(true));
        pay3Img.setOnClickListener(view14 -> pay3Btn.setChecked(true));

        pay4Empty.setOnClickListener(view14 -> pay4Btn.setChecked(true));
        pay4Text.setOnClickListener(view14 -> pay4Btn.setChecked(true));
        pay4Img.setOnClickListener(view14 -> pay4Btn.setChecked(true));

        Button sure = view.findViewById(R.id.pay_button);
        ImageView close = view.findViewById(R.id.ic_close);

        sure.setText(getString(R.string.pay_money) + money + getString(R.string.yuan));

        sure.setOnClickListener(view12 -> {
            if (pay2Btn.isChecked() || pay3Btn.isChecked() || pay4Btn.isChecked()) {
                if (pay4Btn.isChecked()) {
                    presenter.payOrder(orderId, "helppay");
                } else if (pay3Btn.isChecked()) {
                    presenter.payOrder(orderId, "sign");
                } else if (pay2Btn.isChecked()) {
                    presenter.payOrder(orderId, "balance");
                }
            } else {
                ToastUtil.showMessage(FlowActivity.this, getString(R.string.please_pay_title));
            }
        });

        close.setOnClickListener(view1 -> bottomSheetDialog.dismiss());

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setOnDismissListener(dialogInterface -> finish());
        bottomSheetDialog.show();
    }

    @Override
    public void paySuc() {
        if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
            bottomSheetDialog.dismiss();
        }
        ToastUtil.showMessage(this, getString(R.string.pay_suc));
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void showLeft(int dis, int time) {

        if (taxiOrder.status == ZCOrderStatus.NEW_ORDER) {
            String disStr = getString(R.string.to_start_about);
            int km = dis / 1000;
            if (km > 1) {
                String disKm = new DecimalFormat("#0.0").format((double) dis / 1000);
                disStr += "<font color='blue'><b><tt>" +
                        disKm + "</tt></b></font>" + getString(R.string.km);
            } else {
                disStr += getString(R.string.left) +
                        "<font color='blue'><b><tt>" +
                        dis + "</tt></b></font>"
                        + getString(R.string.meter);
            }

            String timeStr;
            int hour = time / 60 / 60;
            int minute = time / 60;
            if (hour > 0) {
                timeStr = "<font color='blue'><b><tt>" +
                        hour +
                        "</tt></b></font>"
                        + getString(R.string.hour_) +
                        "<font color='black'><b><tt>" +
                        time / 60 % 60 +
                        "</tt></b></font>" +
                        getString(R.string.minute_);
            } else {
                timeStr = "<font color='blue'><b><tt>" +
                        minute +
                        "</tt></b></font>" +
                        getString(R.string.minute_);
            }
//            left_time_dis.setText(Html.fromHtml(disStr + timeStr));
            leftTimeText.setText(Html.fromHtml(disStr + timeStr));
        } else {
            String disStr;
            int km = dis / 1000;
            if (km > 1) {
                String disKm = new DecimalFormat("#0.0").format((double) dis / 1000);
                disStr = getString(R.string.left) +
                        "<font color='black'><b><tt>" +
                        disKm + "</tt></b></font>" + getString(R.string.km);
            } else {
                disStr = getString(R.string.left) +
                        "<font color='black'><b><tt>" +
                        dis + "</tt></b></font>"
                        + getString(R.string.meter);
            }

            String timeStr;
            int hour = time / 60 / 60;
            int minute = time / 60;
            if (hour > 0) {
                timeStr = getString(R.string.about_) +
                        "<font color='black'><b><tt>" +
                        hour +
                        "</tt></b></font>"
                        + getString(R.string.hour_) +
                        "<font color='black'><b><tt>" +
                        time / 60 % 60 +
                        "</tt></b></font>" +
                        getString(R.string.minute_);
            } else {
                timeStr = getString(R.string.about_) +
                        "<font color='black'><b><tt>" +
                        minute +
                        "</tt></b></font>" +
                        getString(R.string.minute_);
            }
//            to_appoint_time.setText(Html.fromHtml(disStr + timeStr));
            leftTimeText.setText(Html.fromHtml(disStr + timeStr));
        }
    }

//    boolean isRecalc = false;

    @Override
    public void showReCal() {
//        isRecalc = true;
    }

    private Address getStartAddr() {
        Address startAddress = null;
        if (taxiOrder != null && taxiOrder.orderAddressVos != null && taxiOrder.orderAddressVos.size() != 0) {
            for (Address address : taxiOrder.orderAddressVos) {
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
        if (taxiOrder != null && taxiOrder.orderAddressVos != null && taxiOrder.orderAddressVos.size() != 0) {
            for (Address address : taxiOrder.orderAddressVos) {
                if (address.type == 3) {
                    endAddr = address;
                    break;
                }
            }
        }
        return endAddr;
    }

    @Override
    public RxManager getManager() {
        return mRxManager;
    }

    @Override
    public void showToPlace(String toPlace) {
        nextPlace.setText(toPlace);
    }

    @Override
    public void showLeftTime(String leftTime) {
        leftTimeText.setText(leftTime);
    }

    @Override
    public void initBridge() {
        bridge = new ActFraCommBridge() {
            @Override
            public void doAccept(LoadingButton btn) {
                presenter.acceptOrder(taxiOrder.id, btn);
                //todo 一键报警
//                CenterUtil centerUtil = new CenterUtil(FlowActivity.this,Config.APP_KEY,
//                        XApp.getMyPreferences().getString(Config.AES_PASSWORD, AesUtil.AAAAA),
//                        XApp.getMyPreferences().getString(Config.SP_TOKEN, ""));
//                centerUtil.smsShareAuto( taxiOrder.orderId, EmUtil.getEmployInfo().companyId,  taxiOrder.passengerId,  taxiOrder.passengerPhone,  taxiOrder.serviceType);
//                centerUtil.checkingAuth( taxiOrder.passengerId);
            }

            @Override
            public void doRefuse() {
                RefuseOrderDialog.Builder builder = new RefuseOrderDialog.Builder(FlowActivity.this);
                builder.setApplyClick(reason -> presenter.refuseOrder(taxiOrder.id, taxiOrder.serviceType, reason));
                RefuseOrderDialog dialog1 = builder.create();
                dialog1.setCanceledOnTouchOutside(true);
                dialog1.show();
            }

            @Override
            public void doToStart(LoadingButton btn) {
//                presenter.toStart(taxiOrder.id, btn);
                presenter.changeOrderStatus(EmUtil.getEmployInfo().companyId, EmUtil.getLastLoc().address, EmUtil.getEmployId(), EmUtil.getLastLoc().latitude,
                        EmUtil.getLastLoc().longitude, taxiOrder.id, 15, btn);
            }

            @Override
            public void doArriveStart() {
//                presenter.arriveStart(taxiOrder.id);
                presenter.changeOrderStatus(EmUtil.getEmployInfo().companyId, EmUtil.getLastLoc().address, EmUtil.getEmployId(), EmUtil.getLastLoc().latitude,
                        EmUtil.getLastLoc().longitude, taxiOrder.id, 20, null);
            }

            @Override
            public void doStartWait(LoadingButton btn) {
                presenter.startWait(taxiOrder.id, btn);
            }

            @Override
            public void doStartWait() {
                presenter.startWait(taxiOrder.id);
            }

            @Override
            public void doStartDrive(LoadingButton btn) {
//                presenter.startDrive(taxiOrder.id, btn);
                presenter.changeOrderStatus(EmUtil.getEmployInfo().companyId, EmUtil.getLastLoc().address, EmUtil.getEmployId(), EmUtil.getLastLoc().latitude,
                        EmUtil.getLastLoc().longitude, taxiOrder.id, 25, null);
            }

            @Override
            public void changeEnd() {
                Intent intent = new Intent(FlowActivity.this, PlaceActivity.class);
                intent.putExtra("hint", getString(R.string.please_end));
                startActivityForResult(intent, CHANGE_END);
            }

            @Override
            public void doFinish() {
                finish();
            }

            @Override
            public void doQuanlan() {
                List<LatLng> latLngs = new ArrayList<>();
                LatLng start = new LatLng(getStartAddr().latitude, getStartAddr().longitude);
                latLngs.add(start);
                if (null != getEndAddr()) {
                    LatLng end = new LatLng(getEndAddr().latitude, getEndAddr().longitude);
                    latLngs.add(end);
                }
                latLngs.add(lastLatlng);
                LatLngBounds bounds = MapUtil.getBounds(latLngs);
                aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, (int) (DensityUtil.getDisplayWidth(FlowActivity.this) / 1.5),
                        (int) (DensityUtil.getDisplayWidth(FlowActivity.this) / 1.5), 0));
            }

            @Override
            public void doRefresh() {
                isMapTouched = false;
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLatlng, 19));
            }

            @Override
            public void doUploadOrder() {
                BuildPushData pushData = new BuildPushData(EmUtil.getLastLoc());
                MqttManager.getInstance().pushLoc(pushData);
                showDrive();
            }

            @Override
            public void showDrive() {
                showToEndFragment();
            }

            @Override
            public void showCheating() {
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//动态设置为竖屏
//                toolbar.setTitle(R.string.zc_status_to_end);
//                CheatingFragment cheatingFragment = new CheatingFragment();
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("taxiOrder", DymOrder.findByIDType(orderId, Config.ZHUANCHE));
//                cheatingFragment.setArguments(bundle);
//                cheatingFragment.setBridge(bridge);
//
//                FragmentManager manager = getSupportFragmentManager();
//                FragmentTransaction transaction = manager.beginTransaction();
//                transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
//                transaction.replace(R.id.flow_frame, cheatingFragment);
//                transaction.commit();
            }

            @Override
            public void showSettleDialog() {
                TaxiSettleDialog dialog = new TaxiSettleDialog(FlowActivity.this);
                dialog.setOnMyClickListener((view, string) -> {
                    if (TextUtils.isEmpty(string)) {
                        ToastUtil.showMessage(FlowActivity.this, "请输入结算金额");
                    } else if (Double.parseDouble(string) == 0) {
                        ToastUtil.showMessage(FlowActivity.this, "请输入正确结算金额");
                    } else {
                        presenter.taxiSettlement(orderId, taxiOrder.orderNo, Double.parseDouble(string));
                    }
                });
                dialog.show();
            }
        };
    }

    @Override
    public void showToEndFragment() {
        DymOrder order = DymOrder.findByIDType(orderId, Config.TAXI);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);//动态设置为遵循传感器
        toolbar.setTitle(R.string.zc_status_to_end);
        runningFragment = new RunningFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("taxiOrder", order);
        runningFragment.setArguments(bundle);
        runningFragment.setBridge(bridge);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
        transaction.replace(R.id.flow_frame, runningFragment);
        transaction.commit();
    }

    @Override
    public void showConsumer(ConsumerInfo consumerInfo) {
//        showPayType(payMoney, consumerInfo);
    }

    @Override
    public void hideTops() {
        not_accept_layout.setVisibility(View.GONE);
        go_layout.setVisibility(View.GONE);
    }

    @Override
    public void settleSuc() {
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocReceiver.getInstance().addObserver(this);//添加位置订阅
        HandlePush.getInstance().addObserver(this);//添加订单变化订阅
        HandlePush.getInstance().addPLObserver(this);//添加订单变化订阅

        traceReceiver = new TraceReceiver(this);
        IntentFilter filter2 = new IntentFilter();
        filter2.addAction(LocService.BROAD_TRACE_SUC);
        registerReceiver(traceReceiver, filter2);

        cancelOrderReceiver = new CancelOrderReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Config.BROAD_CANCEL_ORDER);
        filter.addAction(Config.BROAD_BACK_ORDER);
        registerReceiver(cancelOrderReceiver, filter);

        orderFinishReceiver = new OrderFinishReceiver(this);
        registerReceiver(orderFinishReceiver, new IntentFilter(Config.BROAD_FINISH_ORDER));
    }

    boolean canGoOld = false;

    @Override
    protected void onResume() {
        super.onResume();
        canGoOld = true;
        EmLoc location = EmUtil.getLastLoc();
        if (location == null) {
            ToastUtil.showMessage(this, getString(R.string.loc_failed));
            finish();
            return;
        }
        mapView.onResume();
        lastLatlng = new LatLng(location.latitude, location.longitude);
        presenter.findOne(orderId);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        canGoOld = false;
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        mAlbumOrientationEventListener.disable();
        mapView.onDestroy();
        presenter.stopNavi();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocReceiver.getInstance().deleteObserver(this);//取消位置订阅
        HandlePush.getInstance().deleteObserver(this);//取消订单变化订阅
        HandlePush.getInstance().deletePLObserver(this);//取消订单变化订阅

        unregisterReceiver(traceReceiver);
        unregisterReceiver(cancelOrderReceiver);
        unregisterReceiver(orderFinishReceiver);
    }

    SmoothMoveMarker smoothMoveMarker;

    private LatLng lastLatlng;

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
                marker.setClickable(false);
                marker.setAnchor(0.5f, 0.5f);
            }
        }

        if (null != taxiOrder) {
            if (taxiOrder.status == ZCOrderStatus.GOTO_DESTINATION_ORDER
                    || taxiOrder.status == ZCOrderStatus.GOTO_BOOKPALCE_ORDER) {
                if (!isMapTouched) {
                    aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19), Config.NORMAL_LOC_TIME, null);
                }
            }
        }
        lastLatlng = latLng;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CANCEL_ORDER) {
                String reason = data.getStringExtra("reason");
                presenter.cancelOrder(taxiOrder.id, reason);
            } else if (requestCode == CHANGE_END) {
                PoiItem poiItem = data.getParcelableExtra("poiItem");
                presenter.changeEnd(orderId, poiItem.getLatLonPoint().getLatitude(), poiItem.getLatLonPoint().getLongitude(), poiItem.getTitle());
            } else if (requestCode == CHANGE_ORDER) {
                DymOrder dymOrder = DymOrder.findByIDType(orderId, Config.DAIJIA);
                if (null != dymOrder) {
                    dymOrder.delete();
                }
                ToastUtil.showMessage(this, "转单成功");
                finish();
            }
        }
    }

    private Polyline tracePolyLine;
    private Polyline orignialPolyLine;

//    @Override
//    public void showTraceAfter(List<LatLng> before, List<LatLng> after) {
//

//    }


    private List<LatLng> traceLatlngs = new ArrayList<>();

    @Override
    public void showTraceAfter(EmLoc traceLoc) {
//        if (null != before && before.size() != 0) {
//            if (null == orignialPolyLine) {
//                orignialPolyLine = aMap.addPolyline(new PolylineOptions().
//                        addAll(before).width(10).color(Color.rgb(255, 0, 0)));
//            } else {
//                orignialPolyLine.setPoints(before);
//            }
//        }
//
//
        traceLatlngs.add(new LatLng(traceLoc.latitude, traceLoc.longitude));
        if (null != traceLatlngs && traceLatlngs.size() != 0) {
            if (null == tracePolyLine) {
                tracePolyLine = aMap.addPolyline(new PolylineOptions().
                        addAll(traceLatlngs).width(10).color(Color.rgb(0, 255, 0)));
            } else {
                tracePolyLine.setPoints(traceLatlngs);
            }
        }
    }

    @Override
    public void onCancelOrder(long orderId, String orderType, String msg) {
        if (taxiOrder == null) {
            return;
        }
        if (orderId == taxiOrder.id
                && orderType.equals(taxiOrder.serviceType)) {
            //todo 一键报警
//            AudioUtil audioUtil = new AudioUtil();
//            audioUtil.onRecord(this, false);
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setMessage(msg)
                    .setPositiveButton(R.string.ok, (dialog1, which) -> {
                        dialog1.dismiss();
                        finish();
                    })
                    .setOnDismissListener(dialog12 -> finish())
                    .create();
            dialog.show();
        }
    }

    private Marker plMaker;

    @Override
    public void plChange(PassengerLocation plocation) {
        if (plMaker != null){
            plMaker.remove();
        }
        if (taxiOrder.status < ZCOrderStatus.GOTO_DESTINATION_ORDER){
            if (null != plocation) {
                LatLng plLatlng = new LatLng(plocation.latitude, plocation.longitude);
//            receiveLoc(plocation);//手动调用上次位置 减少从北京跳过来的时间
//            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(plLatlng, 19));//移动镜头，首次镜头快速跳到指定位置

                MarkerOptions markerOption = new MarkerOptions();
                markerOption.position(new LatLng(plocation.latitude, plocation.longitude));
                markerOption.draggable(false);//设置Marker可拖动
                markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(), R.mipmap.blue_dot)));
                // 将Marker设置为贴地显示，可以双指下拉地图查看效果
                markerOption.setFlat(true);//设置marker平贴地图效果
                plMaker = aMap.addMarker(markerOption);
            }
        }
    }

    @Override
    public void feeChanged(long orderId, String orderType) {
        if (taxiOrder == null) {
            return;
        }

        if (orderId == taxiOrder.id && orderType.equals(Config.ZHUANCHE)) {
            DymOrder dyo = DymOrder.findByIDType(orderId, orderType);
            if (null != waitFragment && waitFragment.isVisible()) {
                waitFragment.showFee(dyo);
            } else if (null != runningFragment && runningFragment.isVisible()) {
                runningFragment.showFee(dyo);
            }
//            if (null != settleFragmentDialog && settleFragmentDialog.isShowing()) {
//                settleFragmentDialog.setDymOrder(dyo);
//            }
        }
    }

    public static boolean isMapTouched = false;

    @Override
    public void onTouch(MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
            Log.e("mapTouch", "-----map onTouched-----");
            if (taxiOrder.status == ZCOrderStatus.GOTO_DESTINATION_ORDER) {
                isMapTouched = true;
            }
            if (null != runningFragment) {
                runningFragment.mapStatusChanged();
            }
        }
    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        android.util.Log.e("lifecycle", "onConfigurationChanged()");
//        super.onConfigurationChanged(newConfig);
//        if (System.currentTimeMillis() - lastChangeTime > 1000) {
//            lastChangeTime = System.currentTimeMillis();
//        } else {//有的胎神手机这个方法要回调两次
//            return;
//        }
//        DisplayMetrics dm = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(dm);
//        int width = dm.widthPixels;
//        int height = dm.heightPixels;
//        if (width > height) {//横屏
//            toWhatOldByOrder(taxiOrder);
//        } else {//竖屏
//
//        }
//    }

    private void toWhatOldByOrder(TaxiOrder taxiOrder) {
        if (taxiOrder == null || !canGoOld) {
            return;
        }
        if (taxiOrder.status == ZCOrderStatus.GOTO_DESTINATION_ORDER) {
            canGoOld = false;
            Intent intent = new Intent(FlowActivity.this, OldRunningActivity.class);
            intent.putExtra("orderId", taxiOrder.id);
            startActivity(intent);
            finish();
        } else if (taxiOrder.status == ZCOrderStatus.START_WAIT_ORDER) {
            canGoOld = false;
            Intent intent = new Intent(FlowActivity.this, OldWaitActivity.class);
            intent.putExtra("orderId", taxiOrder.id);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onFinishOrder(long orderId, String orderType) {
        if (orderId == this.orderId && orderType.equals(Config.ZHUANCHE)) {
            ToastUtil.showMessage(this, getString(R.string.finished_order));
            finish();
        }
    }

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    private class AlbumOrientationEventListener extends OrientationEventListener {
        private int mOrientation;

        public AlbumOrientationEventListener(Context context, int rate) {
            super(context, rate);
        }

        @Override
        public void onOrientationChanged(int orientation) {
//            if (settleFragmentDialog != null && settleFragmentDialog.isShowing()) {
//                //已经显示结算对话框不显示
//                return;
//            }
//            if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN || !canGoOld) {
//                return;
//            }
//            android.util.Log.e("TAG", "orientation = " + orientation);
//            if ((orientation > 70 && orientation < 110) || (orientation > 250 && orientation < 290)) {
//                toWhatOldByOrder(taxiOrder);
//            }
        }
    }


}
