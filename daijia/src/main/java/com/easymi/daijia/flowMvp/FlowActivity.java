package com.easymi.daijia.flowMvp;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.easymi.common.push.FeeChangeObserver;
import com.easymi.common.push.HandlePush;
import com.easymi.common.trace.TraceInterface;
import com.easymi.common.trace.TraceReceiver;
import com.easymi.component.Config;
import com.easymi.component.DJOrderStatus;
import com.easymi.component.activity.PlaceActivity;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.entity.EmLoc;
import com.easymi.component.loc.LocObserver;
import com.easymi.component.loc.LocReceiver;
import com.easymi.component.loc.LocService;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.utils.DensityUtil;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.Log;
import com.easymi.component.utils.MapUtil;
import com.easymi.component.utils.PhoneUtil;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.CusBottomSheetDialog;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.LoadingButton;
import com.easymi.component.widget.overlay.DrivingRouteOverlay;
import com.easymi.daijia.R;
import com.easymi.daijia.activity.CancelActivity;
import com.easymi.daijia.activity.ConsumerInfoActivity;
import com.easymi.daijia.activity.SameOrderActivity;
import com.easymi.daijia.entity.Address;
import com.easymi.daijia.entity.DJOrder;
import com.easymi.daijia.flowMvp.oldCalc.OldRunningActivity;
import com.easymi.daijia.flowMvp.oldCalc.OldWaitActivity;
import com.easymi.daijia.fragment.AcceptFragment;
import com.easymi.daijia.fragment.ArriveStartFragment;
import com.easymi.daijia.fragment.RunningFragment;
import com.easymi.daijia.fragment.SettleFragmentDialog;
import com.easymi.daijia.fragment.SlideArriveStartFragment;
import com.easymi.daijia.fragment.ToStartFragment;
import com.easymi.daijia.fragment.WaitFragment;
import com.easymi.daijia.receiver.CancelOrderReceiver;
import com.easymi.daijia.receiver.OrderFinishReceiver;
import com.easymi.daijia.widget.FlowPopWindow;
import com.easymi.daijia.widget.RefuseOrderDialog;
import com.google.gson.Gson;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;

/**
 * Created by developerLzh on 2017/11/13 0013.
 */
@Route(path = "/daijia/FlowActivity")
public class FlowActivity extends RxBaseActivity implements FlowContract.View,
        LocObserver,
        TraceInterface,
        FeeChangeObserver,
        CancelOrderReceiver.OnCancelListener, AMap.OnMapTouchListener, OrderFinishReceiver.OnFinishListener {
    public static final int CANCEL_ORDER = 0X01;
    public static final int CHANGE_END = 0X02;

    CusToolbar toolbar;
    TextView nextPlace;
    TextView leftTimeText;
    TextView orderNumberText;
    TextView orderTypeText;
    TagContainerLayout tagContainerLayout;
    LinearLayout drawerFrame;
    private MapView mapView;

    LinearLayout naviCon;

    ExpandableLayout expandableLayout;

    FlowPopWindow popWindow;

    private DJOrder djOrder;

    private FlowPresenter presenter;

    private ActFraCommBridge bridge;

    private TraceReceiver traceReceiver;
    private CancelOrderReceiver cancelOrderReceiver;
    private OrderFinishReceiver orderFinishReceiver;

    private AMap aMap;

    private long orderId;

    private boolean fromOld = false;

    @Override
    public int getLayoutId() {
        return R.layout.activity_flow;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        // 屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        orderId = getIntent().getLongExtra("orderId", -1);
        fromOld = getIntent().getBooleanExtra("fromOld", false);//是否是从计价器过来的
        if (orderId == -1) {
            finish();
            return;
        }

        presenter = new FlowPresenter(this, this);

        toolbar = findViewById(R.id.toolbar);
        nextPlace = findViewById(R.id.next_place);
        leftTimeText = findViewById(R.id.left_time);
        orderNumberText = findViewById(R.id.order_number_text);
        orderTypeText = findViewById(R.id.order_type);
        tagContainerLayout = findViewById(R.id.tag_container);
        drawerFrame = findViewById(R.id.drawer_frame);
        naviCon = findViewById(R.id.navi_con);
        expandableLayout = findViewById(R.id.expandable_layout);

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
                if (djOrder.orderStatus >= DJOrderStatus.GOTO_DESTINATION_ORDER) {
                    popWindow.hideCancel();
                } else {
                    popWindow.showCancel();
                }
                if (StringUtils.isBlank(djOrder.groupId)) {
                    popWindow.hideSame();
                } else {
                    popWindow.showSame();
                }
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
                String phone = EmUtil.getEmployInfo().company_phone;
                PhoneUtil.call(FlowActivity.this, phone);
            } else if (i == R.id.pop_same_order) {
                Intent intent = new Intent(FlowActivity.this, SameOrderActivity.class);
                intent.putExtra("groupId", djOrder.groupId);
                startActivity(intent);
            } else if (i == R.id.pop_consumer_msg) {
                Intent intent = new Intent(FlowActivity.this, ConsumerInfoActivity.class);
                intent.putExtra("orderId", orderId);
                startActivity(intent);
            }
        });
    }

    @Override
    public void showTopView() {
        orderNumberText.setText(djOrder.orderNumber);
        orderTypeText.setText(djOrder.orderDetailType);
        tagContainerLayout.removeAllTags();
        tagContainerLayout.addTag("你好");
        tagContainerLayout.addTag("我好");
        tagContainerLayout.addTag("大家好");
        tagContainerLayout.addTag("广州好迪");
        drawerFrame.setOnClickListener(view -> {
            if (expandableLayout.isExpanded()) {
                expandableLayout.collapse();
            } else {
                expandableLayout.expand();
            }
        });
        if (djOrder.orderStatus == DJOrderStatus.NEW_ORDER
                || djOrder.orderStatus == DJOrderStatus.PAIDAN_ORDER
                || djOrder.orderStatus == DJOrderStatus.TAKE_ORDER
                || djOrder.orderStatus == DJOrderStatus.GOTO_BOOKPALCE_ORDER) {
            nextPlace.setText(djOrder.startPlace);
        } else {
            if (StringUtils.isNotBlank(djOrder.endPlace)) {
                nextPlace.setText(djOrder.endPlace);
            } else {
                nextPlace.setText(getString(R.string.des_place));
            }
        }
        tagContainerLayout.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, String text) {

            }

            @Override
            public void onTagLongClick(int position, String text) {

            }

            @Override
            public void onTagCrossClick(int position) {

            }
        });
    }

    WaitFragment waitFragment;
    RunningFragment runningFragment;
    SettleFragmentDialog settleFragmentDialog;

    @Override
    public void showBottomFragment(DJOrder djOrder) {

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//动态设置为竖屏

        if (djOrder.orderStatus == DJOrderStatus.PAIDAN_ORDER || djOrder.orderStatus == DJOrderStatus.NEW_ORDER) {
            toolbar.setTitle(R.string.status_pai);
            AcceptFragment acceptFragment = new AcceptFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("djOrder", djOrder);
            acceptFragment.setArguments(bundle);
            acceptFragment.setBridge(bridge);

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
            transaction.replace(R.id.flow_frame, acceptFragment);
            transaction.commit();
        } else if (djOrder.orderStatus == DJOrderStatus.TAKE_ORDER) {
            toolbar.setTitle(R.string.status_jie);
            ToStartFragment toStartFragment = new ToStartFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("djOrder", djOrder);
            toStartFragment.setArguments(bundle);
            toStartFragment.setBridge(bridge);

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
            transaction.replace(R.id.flow_frame, toStartFragment);
            transaction.commit();
        } else if (djOrder.orderStatus == DJOrderStatus.GOTO_BOOKPALCE_ORDER) {
            toolbar.setTitle(R.string.status_to_start);
            SlideArriveStartFragment fragment = new SlideArriveStartFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("djOrder", djOrder);
            fragment.setArguments(bundle);
            fragment.setBridge(bridge);

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
            transaction.replace(R.id.flow_frame, fragment);
            transaction.commit();
        } else if (djOrder.orderStatus == DJOrderStatus.ARRIVAL_BOOKPLACE_ORDER) {
            toolbar.setTitle(R.string.status_arrive_start);
            ArriveStartFragment fragment = new ArriveStartFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("djOrder", djOrder);
            fragment.setArguments(bundle);
            fragment.setBridge(bridge);

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
            transaction.replace(R.id.flow_frame, fragment);
            transaction.commit();
        } else if (djOrder.orderStatus == DJOrderStatus.START_WAIT_ORDER) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);//动态设置为遵循传感器
            toolbar.setTitle(R.string.status_wait);
            waitFragment = new WaitFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("djOrder", DymOrder.findByIDType(orderId, Config.DAIJIA));
            waitFragment.setArguments(bundle);
            waitFragment.setBridge(bridge);

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
            transaction.replace(R.id.flow_frame, waitFragment);
            transaction.commit();
        } else if (djOrder.orderStatus == DJOrderStatus.GOTO_DESTINATION_ORDER) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);//动态设置为遵循传感器
            toolbar.setTitle(R.string.status_to_end);
            runningFragment = new RunningFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("djOrder", DymOrder.findByIDType(orderId, Config.DAIJIA));
            runningFragment.setArguments(bundle);
            runningFragment.setBridge(bridge);

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
            transaction.replace(R.id.flow_frame, runningFragment);
            transaction.commit();
        } else if (djOrder.orderStatus == DJOrderStatus.ARRIVAL_DESTINATION_ORDER) {
            toolbar.setTitle(R.string.settle);
            runningFragment = new RunningFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("djOrder", DymOrder.findByIDType(orderId, Config.DAIJIA));
            runningFragment.setArguments(bundle);
            runningFragment.setBridge(bridge);

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
            transaction.replace(R.id.flow_frame, runningFragment);
            transaction.commit();

            if (settleFragmentDialog != null && settleFragmentDialog.isShowing()) {
                settleFragmentDialog.setDjOrder(djOrder);

                DymOrder dymOrder = DymOrder.findByIDType(orderId, Config.DAIJIA);//确认费用后直接弹出支付页面
                if (null != dymOrder) {
                    bridge.doPay(dymOrder.orderShouldPay);
                }
            } else {
                settleFragmentDialog = new SettleFragmentDialog(this, djOrder, bridge);
                settleFragmentDialog.show();
            }
        }

        boolean forceOre = XApp.getMyPreferences().getBoolean(Config.SP_ALWAYS_OREN, false);
        if (forceOre && !fromOld) {//始终横屏计价将自动跳转到横屏界面
            toWhatOldByOrder(djOrder);
        }
    }

    @Override
    public void showOrder(DJOrder djOrder) {
        if (null == djOrder) {
            finish();
        } else {
            this.djOrder = djOrder;
            showTopView();
            initBridge();
            showBottomFragment(djOrder);
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
        if (djOrder.orderStatus == DJOrderStatus.NEW_ORDER
                || djOrder.orderStatus == DJOrderStatus.PAIDAN_ORDER
                || djOrder.orderStatus == DJOrderStatus.TAKE_ORDER
                ) {
            if (null != getStartAddr()) {
                latLngs.add(new LatLng(getStartAddr().lat, getStartAddr().lng));
                naviCon.setOnClickListener(view -> presenter.navi(new LatLng(getStartAddr().lat, getStartAddr().lng), getStartAddr().poi, orderId));
                presenter.routePlanByNavi(getStartAddr().lat, getStartAddr().lng);
            } else {
                naviCon.setOnClickListener(v -> ToastUtil.showMessage(FlowActivity.this, getString(R.string.illegality_place)));
            }
            if (null != getEndAddr()) {
                latLngs.add(new LatLng(getEndAddr().lat, getEndAddr().lng));
            }
            LatLngBounds bounds = MapUtil.getBounds(latLngs, lastLatlng);
            aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, DensityUtil.getDisplayWidth(this) / 2, DensityUtil.getDisplayWidth(this) / 2, 120));
        } else if (djOrder.orderStatus == DJOrderStatus.GOTO_BOOKPALCE_ORDER) {
            if (null != getStartAddr()) {
                latLngs.add(new LatLng(getStartAddr().lat, getStartAddr().lng));
                naviCon.setOnClickListener(view -> presenter.navi(new LatLng(getStartAddr().lat, getStartAddr().lng), getStartAddr().poi, orderId));
                presenter.routePlanByNavi(getStartAddr().lat, getStartAddr().lng);
            } else {
                naviCon.setOnClickListener(v -> ToastUtil.showMessage(FlowActivity.this, getString(R.string.illegality_place)));
            }
            LatLngBounds bounds = MapUtil.getBounds(latLngs, lastLatlng);
            aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, DensityUtil.getDisplayWidth(this) / 2, DensityUtil.getDisplayWidth(this) / 2, 120));
        } else if (djOrder.orderStatus == DJOrderStatus.ARRIVAL_BOOKPLACE_ORDER
                || djOrder.orderStatus == DJOrderStatus.GOTO_DESTINATION_ORDER
                || djOrder.orderStatus == DJOrderStatus.START_WAIT_ORDER
                || djOrder.orderStatus == DJOrderStatus.ARRIVAL_DESTINATION_ORDER
                ) {
            if (null != getEndAddr()) {
                latLngs.add(new LatLng(getEndAddr().lat, getEndAddr().lng));
                naviCon.setOnClickListener(view -> presenter.navi(new LatLng(getEndAddr().lat, getEndAddr().lng), getEndAddr().poi, orderId));
                presenter.routePlanByNavi(getEndAddr().lat, getEndAddr().lng);
            } else {
                naviCon.setOnClickListener(v -> ToastUtil.showMessage(FlowActivity.this, getString(R.string.illegality_place)));
            }
            if (djOrder.orderStatus == DJOrderStatus.GOTO_DESTINATION_ORDER) {
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLatlng, 19));
            } else {
                LatLngBounds bounds = MapUtil.getBounds(latLngs, lastLatlng);
                aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, DensityUtil.getDisplayWidth(this) / 2, DensityUtil.getDisplayWidth(this) / 2, 120));
            }
        }
        if (null != getStartAddr()) {
            if (null == startMarker) {
                MarkerOptions markerOption = new MarkerOptions();
                markerOption.position(new LatLng(getStartAddr().lat, getStartAddr().lng));
                markerOption.draggable(false);//设置Marker可拖动
                markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(), R.mipmap.ic_start)));
                // 将Marker设置为贴地显示，可以双指下拉地图查看效果
                markerOption.setFlat(true);//设置marker平贴地图效果
                startMarker = aMap.addMarker(markerOption);
            } else {
                startMarker.setPosition(new LatLng(getStartAddr().lat, getStartAddr().lng));
            }
        }
        if (null != getEndAddr()) {
            if (null == endMarker) {
                MarkerOptions markerOption = new MarkerOptions();
                markerOption.position(new LatLng(getEndAddr().lat, getEndAddr().lng));
                markerOption.draggable(false);//设置Marker可拖动
                markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(), R.mipmap.ic_end)));
                // 将Marker设置为贴地显示，可以双指下拉地图查看效果
                markerOption.setFlat(true);//设置marker平贴地图效果
                endMarker = aMap.addMarker(markerOption);
            } else {
                endMarker.setPosition(new LatLng(getEndAddr().lat, getEndAddr().lng));
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
        aMap.moveCamera(CameraUpdateFactory.changeTilt(0));
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
        drivingRouteOverlay.setRouteWidth(50);
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
        aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, DensityUtil.getDisplayWidth(this) / 2, DensityUtil.getDisplayWidth(this) / 2, 80));
    }

    @Override
    public void showPayType(double money) {

        if (null != settleFragmentDialog) {
            settleFragmentDialog.dismiss();
        }

        CusBottomSheetDialog bottomSheetDialog = new CusBottomSheetDialog(this);

        View view = LayoutInflater.from(this).inflate(R.layout.pay_type_dialog, null, false);
        CheckBox payBalance = view.findViewById(R.id.pay_balance);
        CheckBox payHelpPay = view.findViewById(R.id.pay_help_pay);
        RelativeLayout balanceCon = view.findViewById(R.id.balance_con);
        RelativeLayout helppayCon = view.findViewById(R.id.helppay_con);
        Button sure = view.findViewById(R.id.pay_button);
        ImageView close = view.findViewById(R.id.ic_close);

        sure.setText(getString(R.string.pay_money) + money + getString(R.string.yuan));

        balanceCon.setOnClickListener(view13 -> payBalance.setChecked(true));

        helppayCon.setOnClickListener(view14 -> payHelpPay.setChecked(true));

        payBalance.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                payHelpPay.setChecked(false);
            }
        });

        payHelpPay.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                payBalance.setChecked(false);
            }
        });

        boolean canDaifu = XApp.getMyPreferences().getBoolean(Config.SP_DAIFU, true);
        if (!canDaifu) {
            helppayCon.setVisibility(View.GONE);
            payHelpPay.setChecked(false);
        }

        sure.setOnClickListener(view12 -> {
            if (payBalance.isChecked() || payHelpPay.isChecked()) {
                if (payHelpPay.isChecked()) {
                    presenter.payOrder(orderId, "helppay");
                } else if (payBalance.isChecked()) {
                    presenter.payOrder(orderId, "balance");
                }
                bottomSheetDialog.dismiss();
            } else {
                ToastUtil.showMessage(FlowActivity.this, getString(R.string.please_pay_title));
            }
        });

        close.setOnClickListener(view1 -> bottomSheetDialog.dismiss());

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setOnDismissListener(dialogInterface -> {
            finish();
        });
        bottomSheetDialog.show();
    }

    @Override
    public void paySuc() {
        ToastUtil.showMessage(this, getString(R.string.pay_suc));
        finish();
    }

    @Override
    public void showLeft(int dis, int time) {
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
        leftTimeText.setText(Html.fromHtml(disStr + timeStr));
    }

//    boolean isRecalc = false;

    @Override
    public void showReCal() {
//        isRecalc = true;
    }

    private Address getStartAddr() {
        Address startAddress = null;
        if (djOrder.addresses != null && djOrder.addresses.size() != 0) {
            for (Address address : djOrder.addresses) {
                if (address.addrType == 1) {
                    startAddress = address;
                    break;
                }
            }
        }
        return startAddress;
    }

    private Address getEndAddr() {
        Address endAddr = null;
        if (djOrder.addresses != null && djOrder.addresses.size() != 0) {
            for (Address address : djOrder.addresses) {
                if (address.addrType == 3) {
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
                presenter.acceptOrder(djOrder.orderId, btn);
            }

            @Override
            public void doRefuse() {
                RefuseOrderDialog.Builder builder = new RefuseOrderDialog.Builder(FlowActivity.this);
                builder.setApplyClick(reason -> presenter.refuseOrder(djOrder.orderId, reason));
                RefuseOrderDialog dialog1 = builder.create();
                dialog1.setCanceledOnTouchOutside(true);
                dialog1.show();
            }

            @Override
            public void doToStart(LoadingButton btn) {
                presenter.toStart(djOrder.orderId, btn);
            }

            @Override
            public void doArriveStart() {
                presenter.arriveStart(djOrder.orderId);
            }

            @Override
            public void doStartWait(LoadingButton btn) {
                presenter.startWait(djOrder.orderId, btn);
            }

            @Override
            public void doStartWait() {
                presenter.startWait(djOrder.orderId);
            }

            @Override
            public void doStartDrive(LoadingButton btn) {
                presenter.startDrive(djOrder.orderId, btn);
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
                LatLng start = new LatLng(getStartAddr().lat, getStartAddr().lng);
                latLngs.add(start);
                if (null != getEndAddr()) {
                    LatLng end = new LatLng(getEndAddr().lat, getEndAddr().lng);
                    latLngs.add(end);
                }
                latLngs.add(lastLatlng);
                LatLngBounds bounds = MapUtil.getBounds(latLngs);
                aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, DensityUtil.getDisplayWidth(FlowActivity.this) / 2,
                        DensityUtil.getDisplayWidth(FlowActivity.this) / 2, 120));
            }

            @Override
            public void doRefresh() {
                isMapTouched = false;
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLatlng, 19));
            }

            @Override
            public void doConfirmMoney(LoadingButton btn, DymOrder dymOrder) {
                presenter.arriveDes(btn, dymOrder);
            }

            @Override
            public void doPay(double money) {
                showPayType(money);
            }

            @Override
            public void showSettleDialog() {
                settleFragmentDialog = new SettleFragmentDialog(FlowActivity.this, djOrder, bridge);
                settleFragmentDialog.show();
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocReceiver.getInstance().addObserver(this);//添加位置订阅
        HandlePush.getInstance().addObserver(this);//添加订单变化订阅

        traceReceiver = new TraceReceiver(this);
        IntentFilter filter2 = new IntentFilter();
        filter2.addAction(LocService.BROAD_TRACE_SUC);
        registerReceiver(traceReceiver, filter2);

        cancelOrderReceiver = new CancelOrderReceiver(this);
        registerReceiver(cancelOrderReceiver, new IntentFilter(Config.BROAD_CANCEL_ORDER));

        orderFinishReceiver = new OrderFinishReceiver(this);
        registerReceiver(orderFinishReceiver, new IntentFilter(Config.BROAD_FINISH_ORDER));
    }

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
        presenter.onDestory();
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocReceiver.getInstance().deleteObserver(this);//取消位置订阅
        HandlePush.getInstance().deleteObserver(this);//取消订单变化订阅

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
            if (djOrder != null) {
                smoothMoveMarker.setTotalDuration(djOrder.orderStatus == DJOrderStatus.GOTO_DESTINATION_ORDER ?
                        Config.BUSY_LOC_TIME / 1000 : Config.FREE_LOC_TIME / 1000);
            } else {
                smoothMoveMarker.setTotalDuration(Config.FREE_LOC_TIME / 1000);
            }
            smoothMoveMarker.setRotate(location.bearing);
            smoothMoveMarker.startSmoothMove();
            Marker marker = smoothMoveMarker.getMarker();
            if (null != marker) {
                marker.setDraggable(false);
                marker.setClickable(false);
            }
        }

        if (null != djOrder) {
            if (djOrder.orderStatus == DJOrderStatus.GOTO_DESTINATION_ORDER
                    || djOrder.orderStatus == DJOrderStatus.GOTO_BOOKPALCE_ORDER) {
                if (!isMapTouched) {
                    aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19),
                            djOrder.orderStatus == DJOrderStatus.GOTO_DESTINATION_ORDER ? Config.BUSY_LOC_TIME : Config.FREE_LOC_TIME, null);
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
                presenter.cancelOrder(djOrder.orderId, reason);
            } else if (requestCode == CHANGE_END) {
                PoiItem poiItem = data.getParcelableExtra("poiItem");
                presenter.changeEnd(orderId, poiItem.getLatLonPoint().getLatitude(), poiItem.getLatLonPoint().getLongitude(), poiItem.getTitle());
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
    public void onCancelOrder(long orderId, String orderType) {
        if (djOrder == null) {
            return;
        }
        if (orderId == djOrder.orderId
                && orderType.equals(djOrder.orderType)) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.canceled_order))
                    .setPositiveButton(R.string.ok, (dialog1, which) -> {
                        dialog1.dismiss();
                        finish();
                    })
                    .setOnDismissListener(dialog12 -> finish())
                    .create();
            dialog.show();
        }
    }

    @Override
    public void feeChanged(long orderId, String orderType) {
        if (djOrder == null) {
            return;
        }
        if (orderId == djOrder.orderId && orderType.equals(Config.DAIJIA)) {
            if (null != waitFragment && waitFragment.isVisible()) {
                waitFragment.showFee(DymOrder.findByIDType(orderId, orderType));
            } else if (null != runningFragment && runningFragment.isVisible()) {
                runningFragment.showFee(DymOrder.findByIDType(orderId, orderType));
            } else if (null != settleFragmentDialog && settleFragmentDialog.isShowing()) {
                settleFragmentDialog.setDymOrder(DymOrder.findByIDType(orderId, orderType));
            }
        }
    }

    public static boolean isMapTouched = false;

    @Override
    public void onTouch(MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
            Log.e("mapTouch", "-----map onTouched-----");
            if (djOrder.orderStatus == DJOrderStatus.GOTO_DESTINATION_ORDER) {
                isMapTouched = true;
            }
            if (null != runningFragment) {
                runningFragment.mapStatusChanged();
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        android.util.Log.e("lifecycle", "onConfigurationChanged()");
        super.onConfigurationChanged(newConfig);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        if (width > height) {//横屏
            toWhatOldByOrder(djOrder);
        } else {//竖屏

        }
    }

    private void toWhatOldByOrder(DJOrder djOrder) {
        if (djOrder == null) {
            return;
        }
        if (djOrder.orderStatus == DJOrderStatus.GOTO_DESTINATION_ORDER) {
            Intent intent = new Intent(FlowActivity.this, OldRunningActivity.class);
            intent.putExtra("orderId", djOrder.orderId);
            startActivity(intent);
            finish();
        } else if (djOrder.orderStatus == DJOrderStatus.START_WAIT_ORDER) {
            Intent intent = new Intent(FlowActivity.this, OldWaitActivity.class);
            intent.putExtra("orderId", djOrder.orderId);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onFinishOrder(long orderId, String orderType) {
        ToastUtil.showMessage(this, getString(R.string.finished_order));
        finish();
    }
}
