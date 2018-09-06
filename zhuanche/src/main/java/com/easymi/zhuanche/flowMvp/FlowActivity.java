package com.easymi.zhuanche.flowMvp;

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
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
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
import com.easymi.common.entity.BuildPushData;
import com.easymi.common.push.FeeChangeObserver;
import com.easymi.common.push.HandlePush;
import com.easymi.common.push.MQTTService;
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
import com.easymi.component.utils.DensityUtil;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.Log;
import com.easymi.component.utils.MapUtil;
import com.easymi.component.utils.PhoneUtil;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.utils.TimeUtil;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.CusBottomSheetDialog;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.LoadingButton;
import com.easymi.component.widget.overlay.DrivingRouteOverlay;
import com.easymi.zhuanche.R;
import com.easymi.zhuanche.activity.CancelActivity;
import com.easymi.zhuanche.activity.ConsumerInfoActivity;
import com.easymi.zhuanche.activity.SameOrderActivity;
import com.easymi.zhuanche.activity.TransferActivity;
import com.easymi.zhuanche.entity.Address;
import com.easymi.zhuanche.entity.ConsumerInfo;
import com.easymi.zhuanche.entity.ZCOrder;
import com.easymi.zhuanche.flowMvp.oldCalc.OldRunningActivity;
import com.easymi.zhuanche.flowMvp.oldCalc.OldWaitActivity;
import com.easymi.zhuanche.fragment.AcceptFragment;
import com.easymi.zhuanche.fragment.ArriveStartFragment;
import com.easymi.zhuanche.fragment.RunningFragment;
import com.easymi.zhuanche.fragment.SettleFragmentDialog;
import com.easymi.zhuanche.fragment.SlideArriveStartFragment;
import com.easymi.zhuanche.fragment.ToStartFragment;
import com.easymi.zhuanche.fragment.WaitFragment;
import com.easymi.zhuanche.receiver.CancelOrderReceiver;
import com.easymi.zhuanche.receiver.OrderFinishReceiver;
import com.easymi.zhuanche.widget.FlowPopWindow;
import com.easymi.zhuanche.widget.RefuseOrderDialog;
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
@Route(path = "/zhuanche/FlowActivity")
public class FlowActivity extends RxBaseActivity implements FlowContract.View,
        LocObserver,
        TraceInterface,
        FeeChangeObserver,
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
    TextView orderTypeText;
    TagContainerLayout tagContainerLayout;
    LinearLayout drawerFrame;
    private MapView mapView;

    ExpandableLayout expandableLayout;

    FlowPopWindow popWindow;


    /**
     * 未接单top
     */
    RelativeLayout not_accept_layout;
    TextView left_time_dis;
    LinearLayout to_appoint_navi_con_1;

    /**
     * 已接单top
     */
    RelativeLayout to_appoint_layout;
    TextView to_appoint_time;
    TextView to_appoint_left_time;
    LinearLayout to_appoint_navi_con;

    /**
     * 到达预约地top
     */
    RelativeLayout arrive_start_wait_layout;

    /**
     * 前往终点top
     */
    RelativeLayout go_layout;
    LinearLayout naviCon;

    /**
     * 中途等待top
     */
    RelativeLayout middle_wait_layout;

    private ZCOrder zcOrder;

    private FlowPresenter presenter;

    private ActFraCommBridge bridge;

    private TraceReceiver traceReceiver;
    private CancelOrderReceiver cancelOrderReceiver;
    private OrderFinishReceiver orderFinishReceiver;

    private AMap aMap;

    private long orderId;

    private boolean fromOld = false;//是否从横屏那边过来

    private double payMoney;

    private boolean isToFeeDetail = true;//是否是前往过费用详情界面

    private AlbumOrientationEventListener mAlbumOrientationEventListener;

    @Override
    public int getLayoutId() {
        return R.layout.zc_activity_flow;
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
        fromOld = getIntent().getBooleanExtra("fromOld", false);//是否是从计价器过来的
        isToFeeDetail = getIntent().getBooleanExtra("showSettle", false);//是否是从计价器过来的
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
        expandableLayout = findViewById(R.id.expandable_layout);

        /**
         * 未接单top
         */
        not_accept_layout = findViewById(R.id.not_accept_layout);
        left_time_dis = findViewById(R.id.left_time_dis);
        to_appoint_navi_con_1 = findViewById(R.id.to_appoint_navi_con_1);

        /**
         * 已接单top
         */
        to_appoint_layout = findViewById(R.id.to_appoint_layout);
        to_appoint_time = findViewById(R.id.to_appoint_time);
        to_appoint_left_time = findViewById(R.id.to_appoint_left_time);
        to_appoint_navi_con = findViewById(R.id.to_appoint_navi_con);

        /**
         * 到达预约地top
         */
        arrive_start_wait_layout = findViewById(R.id.arrive_start_wait_layout);

        /**
         * 前往终点top
         */
        go_layout = findViewById(R.id.go_layout);
        naviCon = findViewById(R.id.navi_con);

        /**
         * 中途等待top
         */
        middle_wait_layout = findViewById(R.id.middle_wait_layout);

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
                if (notCancel || zcOrder.orderStatus == ZCOrderStatus.NEW_ORDER || zcOrder.orderStatus == ZCOrderStatus.PAIDAN_ORDER || zcOrder.orderStatus >= ZCOrderStatus.GOTO_DESTINATION_ORDER) {
                    popWindow.hideCancel();
                } else {
                    popWindow.showCancel();
                }
                if (StringUtils.isBlank(zcOrder.groupId)) {
                    popWindow.hideSame();
                } else {
                    popWindow.showSame();
                }
                if (zcOrder.orderStatus == ZCOrderStatus.NEW_ORDER || zcOrder.orderStatus == ZCOrderStatus.PAIDAN_ORDER) {
                    popWindow.hideConsumer();
                } else {
                    popWindow.showConsumer();
                }
                if ((zcOrder.orderStatus == ZCOrderStatus.TAKE_ORDER || zcOrder.orderStatus == ZCOrderStatus.GOTO_BOOKPALCE_ORDER) && !notChangeOrder) {
                    popWindow.showTransfer();
                } else {
                    popWindow.hideTransfer();
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
                intent.putExtra("groupId", zcOrder.groupId);
                startActivity(intent);
            } else if (i == R.id.pop_consumer_msg) {
                Intent intent = new Intent(FlowActivity.this, ConsumerInfoActivity.class);
                intent.putExtra("orderId", orderId);
                startActivity(intent);
            } else if (i == R.id.pop_order_transfer) {
                Intent intent = new Intent(FlowActivity.this, TransferActivity.class);
                intent.putExtra("order", zcOrder);
                startActivityForResult(intent, CHANGE_ORDER);
            }
        });
    }

    @Override
    public void showTopView() {
        orderNumberText.setText(zcOrder.orderNumber);
        orderTypeText.setText(zcOrder.orderDetailType);
        tagContainerLayout.removeAllTags();
        if (StringUtils.isNotBlank(zcOrder.passengerTags)) {
            if (zcOrder.passengerTags.contains(",")) {
                String[] tags = zcOrder.passengerTags.split(",");
                for (String tag : tags) {
                    tagContainerLayout.addTag(tag);
                }
            } else {
                tagContainerLayout.addTag(zcOrder.passengerTags);
            }
        }
        drawerFrame.setOnClickListener(view -> {
            if (expandableLayout.isExpanded()) {
                expandableLayout.collapse();
            } else {
                expandableLayout.expand();
            }
        });

        if (zcOrder.orderStatus == ZCOrderStatus.NEW_ORDER) {
            hideTops();
            not_accept_layout.setVisibility(View.VISIBLE);
            to_appoint_navi_con_1.setOnClickListener(view -> presenter.navi(new LatLng(getStartAddr().lat,
                    getStartAddr().lng), getStartAddr().poi, orderId));
        } else if (zcOrder.orderStatus == ZCOrderStatus.TAKE_ORDER
                || zcOrder.orderStatus == ZCOrderStatus.GOTO_BOOKPALCE_ORDER) {
            hideTops();
            to_appoint_layout.setVisibility(View.VISIBLE);
            to_appoint_navi_con.setOnClickListener(view -> presenter.navi(new LatLng(getStartAddr().lat,
                    getStartAddr().lng), getStartAddr().poi, orderId));

            String time = getString(R.string.please_start_at)
                    + TimeUtil.getTime("HH:mm", zcOrder.bookTime * 1000)
                    + getString(R.string.arrive_start);

            SpannableString ss = new SpannableString(time);
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#3c98e3"));
            int startIndex = 2;
            int endIndex = ss.length() - 7;
            ss.setSpan(colorSpan, startIndex, endIndex, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            to_appoint_time.setText(ss);
        } else if (zcOrder.orderStatus == ZCOrderStatus.ARRIVAL_BOOKPLACE_ORDER) {
            hideTops();
            arrive_start_wait_layout.setVisibility(View.VISIBLE);
        } else if (zcOrder.orderStatus == ZCOrderStatus.START_WAIT_ORDER) {
            hideTops();
            middle_wait_layout.setVisibility(View.VISIBLE);
        } else {
            hideTops();
            go_layout.setVisibility(View.VISIBLE);
            naviCon.setOnClickListener(view -> {
                if (null != getEndAddr()) {
                    presenter.navi(new LatLng(getEndAddr().lat, getEndAddr().lat), getEndAddr().addr, orderId);
                } else {
                    ToastUtil.showMessage(FlowActivity.this, getString(R.string.illegality_place));
                }
            });
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

        if (zcOrder.orderStatus == ZCOrderStatus.GOTO_DESTINATION_ORDER) {
            nextPlace.setText(zcOrder.endPlace);
        }

    }

    WaitFragment waitFragment;
    RunningFragment runningFragment;
    SettleFragmentDialog settleFragmentDialog;

    @Override
    public void showBottomFragment(ZCOrder zcOrder) {

//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//动态设置为竖屏

        if (zcOrder.orderStatus == ZCOrderStatus.PAIDAN_ORDER || zcOrder.orderStatus == ZCOrderStatus.NEW_ORDER) {
            toolbar.setTitle(R.string.status_pai);
            AcceptFragment acceptFragment = new AcceptFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("zcOrder", zcOrder);
            acceptFragment.setArguments(bundle);
            acceptFragment.setBridge(bridge);

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
            transaction.replace(R.id.flow_frame, acceptFragment);
            transaction.commit();
        } else if (zcOrder.orderStatus == ZCOrderStatus.TAKE_ORDER) {
            toolbar.setTitle(R.string.status_jie);
            ToStartFragment toStartFragment = new ToStartFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("zcOrder", zcOrder);
            toStartFragment.setArguments(bundle);
            toStartFragment.setBridge(bridge);

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
            transaction.replace(R.id.flow_frame, toStartFragment);
            transaction.commit();
        } else if (zcOrder.orderStatus == ZCOrderStatus.GOTO_BOOKPALCE_ORDER) {
            toolbar.setTitle(R.string.status_to_start);
            SlideArriveStartFragment fragment = new SlideArriveStartFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("zcOrder", zcOrder);
            fragment.setArguments(bundle);
            fragment.setBridge(bridge);

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
            transaction.replace(R.id.flow_frame, fragment);
            transaction.commit();
        } else if (zcOrder.orderStatus == ZCOrderStatus.ARRIVAL_BOOKPLACE_ORDER) {
            toolbar.setTitle(R.string.status_arrive_start);
            ArriveStartFragment fragment = new ArriveStartFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("zcOrder", zcOrder);
            fragment.setArguments(bundle);
            fragment.setBridge(bridge);

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
            transaction.replace(R.id.flow_frame, fragment);
            transaction.commit();
        } else if (zcOrder.orderStatus == ZCOrderStatus.START_WAIT_ORDER) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);//动态设置为遵循传感器
            toolbar.setTitle(R.string.wait_consumer);
            waitFragment = new WaitFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("zcOrder", DymOrder.findByIDType(orderId, Config.ZHUANCHE));
            waitFragment.setArguments(bundle);
            waitFragment.setBridge(bridge);

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
            transaction.replace(R.id.flow_frame, waitFragment);
            transaction.commit();
        } else if (zcOrder.orderStatus == ZCOrderStatus.GOTO_DESTINATION_ORDER) {
            showToEndFragment();
            if (isToFeeDetail) {
                if (settleFragmentDialog != null && settleFragmentDialog.isShowing()) {
                    settleFragmentDialog.setDjOrder(zcOrder);
                } else {
                    settleFragmentDialog = new SettleFragmentDialog(FlowActivity.this, zcOrder, bridge);
                    settleFragmentDialog.show();
                }
                isToFeeDetail = false;
            }
        } else if (zcOrder.orderStatus == ZCOrderStatus.ARRIVAL_DESTINATION_ORDER) {
            toolbar.setTitle(R.string.settle);
            runningFragment = new RunningFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("zcOrder", DymOrder.findByIDType(orderId, Config.ZHUANCHE));
            runningFragment.setArguments(bundle);
            runningFragment.setBridge(bridge);

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
            transaction.replace(R.id.flow_frame, runningFragment);
            transaction.commit();

            if (settleFragmentDialog != null && settleFragmentDialog.isShowing() && !isToFeeDetail) {
                settleFragmentDialog.setDjOrder(zcOrder);

                DymOrder dymOrder = DymOrder.findByIDType(orderId, Config.ZHUANCHE);//确认费用后直接弹出支付页面
                if (null != dymOrder) {
                    bridge.doPay(dymOrder.orderShouldPay);
                }
            } else {
                if (settleFragmentDialog != null && settleFragmentDialog.isShowing()) {
                    settleFragmentDialog.setDjOrder(zcOrder);
                } else {
                    settleFragmentDialog = new SettleFragmentDialog(FlowActivity.this, zcOrder, bridge);
                    settleFragmentDialog.show();
                }
                isToFeeDetail = false;
            }
        }

        boolean forceOre = XApp.getMyPreferences().getBoolean(Config.SP_ALWAYS_OREN, false);
        if (forceOre && !fromOld) {//始终横屏计价将自动跳转到横屏界面
            toWhatOldByOrder(zcOrder);
        }
    }

    @Override
    public void showOrder(ZCOrder zcOrder) {
        if (null == zcOrder) {
            finish();
        } else {
            this.zcOrder = zcOrder;
            showTopView();
            initBridge();
            showBottomFragment(zcOrder);
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
        if (zcOrder.orderStatus == ZCOrderStatus.NEW_ORDER
                || zcOrder.orderStatus == ZCOrderStatus.PAIDAN_ORDER
                || zcOrder.orderStatus == ZCOrderStatus.TAKE_ORDER
                ) {
            if (null != getStartAddr()) {
                latLngs.add(new LatLng(getStartAddr().lat, getStartAddr().lng));
                presenter.routePlanByNavi(getStartAddr().lat, getStartAddr().lng);
            }
            if (null != getEndAddr()) {
                latLngs.add(new LatLng(getEndAddr().lat, getEndAddr().lng));
            }
            LatLngBounds bounds = MapUtil.getBounds(latLngs, lastLatlng);
            aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, (int) (DensityUtil.getDisplayWidth(this) / 1.5), (int) (DensityUtil.getDisplayWidth(this) / 1.5), 0));
        } else if (zcOrder.orderStatus == ZCOrderStatus.GOTO_BOOKPALCE_ORDER) {
            if (null != getStartAddr()) {
                latLngs.add(new LatLng(getStartAddr().lat, getStartAddr().lng));
                presenter.routePlanByNavi(getStartAddr().lat, getStartAddr().lng);
            } else {
                presenter.stopNavi();
                leftTimeText.setText("");
            }
            LatLngBounds bounds = MapUtil.getBounds(latLngs, lastLatlng);
            aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, (int) (DensityUtil.getDisplayWidth(this) / 1.5), (int) (DensityUtil.getDisplayWidth(this) / 1.5), 0));
        } else if (zcOrder.orderStatus == ZCOrderStatus.ARRIVAL_BOOKPLACE_ORDER
                || zcOrder.orderStatus == ZCOrderStatus.GOTO_DESTINATION_ORDER
                || zcOrder.orderStatus == ZCOrderStatus.START_WAIT_ORDER
                || zcOrder.orderStatus == ZCOrderStatus.ARRIVAL_DESTINATION_ORDER
                ) {
            if (null != getEndAddr()) {
                latLngs.add(new LatLng(getEndAddr().lat, getEndAddr().lng));
                presenter.routePlanByNavi(getEndAddr().lat, getEndAddr().lng);
            }
            if (zcOrder.orderStatus == ZCOrderStatus.GOTO_DESTINATION_ORDER) {
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLatlng, 19));
            } else {
                LatLngBounds bounds = MapUtil.getBounds(latLngs, lastLatlng);
                aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, (int) (DensityUtil.getDisplayWidth(this) / 1.5), (int) (DensityUtil.getDisplayWidth(this) / 1.5), 0));
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

        if (null != settleFragmentDialog) {
            settleFragmentDialog.dismiss();
        }

        bottomSheetDialog = new CusBottomSheetDialog(this);

        View view = LayoutInflater.from(this).inflate(R.layout.zc_pay_type_dialog, null, false);

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

        if (zcOrder.orderStatus == ZCOrderStatus.NEW_ORDER) {
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
            left_time_dis.setText(Html.fromHtml(disStr + timeStr));
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
            to_appoint_left_time.setText(Html.fromHtml(disStr + timeStr));
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
        if (zcOrder != null && zcOrder.addresses != null && zcOrder.addresses.size() != 0) {
            for (Address address : zcOrder.addresses) {
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
        if (zcOrder != null && zcOrder.addresses != null && zcOrder.addresses.size() != 0) {
            for (Address address : zcOrder.addresses) {
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
                presenter.acceptOrder(zcOrder.orderId, btn);
            }

            @Override
            public void doRefuse() {
                RefuseOrderDialog.Builder builder = new RefuseOrderDialog.Builder(FlowActivity.this);
                builder.setApplyClick(reason -> presenter.refuseOrder(zcOrder.orderId, reason));
                RefuseOrderDialog dialog1 = builder.create();
                dialog1.setCanceledOnTouchOutside(true);
                dialog1.show();
            }

            @Override
            public void doToStart(LoadingButton btn) {
                presenter.toStart(zcOrder.orderId, btn);
            }

            @Override
            public void doArriveStart() {
                presenter.arriveStart(zcOrder.orderId);
            }

            @Override
            public void doStartWait(LoadingButton btn) {
                presenter.startWait(zcOrder.orderId, btn);
            }

            @Override
            public void doStartWait() {
                presenter.startWait(zcOrder.orderId);
            }

            @Override
            public void doStartDrive(LoadingButton btn) {
                presenter.startDrive(zcOrder.orderId, btn);
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
                MQTTService.pushLoc(pushData);
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
//                bundle.putSerializable("zcOrder", DymOrder.findByIDType(orderId, Config.ZHUANCHE));
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
            public void toFeeDetail() {
                isToFeeDetail = true;
            }

            @Override
            public void doConfirmMoney(LoadingButton btn, DymOrder dymOrder) {
                presenter.arriveDes(zcOrder, btn, dymOrder);
            }

            @Override
            public void doPay(double money) {
                payMoney = money;
                presenter.getConsumerInfo(orderId);
            }

            @Override
            public void showSettleDialog() {
                settleFragmentDialog = new SettleFragmentDialog(FlowActivity.this, zcOrder, bridge);
                settleFragmentDialog.show();
            }
        };
    }

    @Override
    public void showToEndFragment() {
        DymOrder order = DymOrder.findByIDType(orderId, Config.ZHUANCHE);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);//动态设置为遵循传感器
        toolbar.setTitle(R.string.zc_status_to_end);
        runningFragment = new RunningFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("zcOrder", order);
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
        showPayType(payMoney, consumerInfo);
    }

    @Override
    public void hideTops() {
        not_accept_layout.setVisibility(View.GONE);
        arrive_start_wait_layout.setVisibility(View.GONE);
        to_appoint_layout.setVisibility(View.GONE);
        go_layout.setVisibility(View.GONE);
        middle_wait_layout.setVisibility(View.GONE);
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

        if (null != zcOrder) {
            if (zcOrder.orderStatus == ZCOrderStatus.GOTO_DESTINATION_ORDER
                    || zcOrder.orderStatus == ZCOrderStatus.GOTO_BOOKPALCE_ORDER) {
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
                presenter.cancelOrder(zcOrder.orderId, reason);
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
        if (zcOrder == null) {
            return;
        }
        if (orderId == zcOrder.orderId
                && orderType.equals(zcOrder.orderType)) {
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

    @Override
    public void feeChanged(long orderId, String orderType) {
        if (zcOrder == null) {
            return;
        }

        if (orderId == zcOrder.orderId && orderType.equals(Config.ZHUANCHE)) {
            DymOrder dyo = DymOrder.findByIDType(orderId, orderType);
            if (null != waitFragment && waitFragment.isVisible()) {
                waitFragment.showFee(dyo);
            } else if (null != runningFragment && runningFragment.isVisible()) {
                runningFragment.showFee(dyo);
            }
            if (null != settleFragmentDialog && settleFragmentDialog.isShowing()) {
                settleFragmentDialog.setDymOrder(dyo);
            }
        }
    }

    public static boolean isMapTouched = false;

    @Override
    public void onTouch(MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
            Log.e("mapTouch", "-----map onTouched-----");
            if (zcOrder.orderStatus == ZCOrderStatus.GOTO_DESTINATION_ORDER) {
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
//            toWhatOldByOrder(zcOrder);
//        } else {//竖屏
//
//        }
//    }

    private void toWhatOldByOrder(ZCOrder zcOrder) {
        if (zcOrder == null || !canGoOld) {
            return;
        }
        if (zcOrder.orderStatus == ZCOrderStatus.GOTO_DESTINATION_ORDER) {
            canGoOld = false;
            Intent intent = new Intent(FlowActivity.this, OldRunningActivity.class);
            intent.putExtra("orderId", zcOrder.orderId);
            startActivity(intent);
            finish();
        } else if (zcOrder.orderStatus == ZCOrderStatus.START_WAIT_ORDER) {
            canGoOld = false;
            Intent intent = new Intent(FlowActivity.this, OldWaitActivity.class);
            intent.putExtra("orderId", zcOrder.orderId);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onFinishOrder(long orderId, String orderType) {
        ToastUtil.showMessage(this, getString(R.string.finished_order));
        finish();
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
            if (settleFragmentDialog != null && settleFragmentDialog.isShowing()) {
                //已经显示结算对话框不显示
                return;
            }

            if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN || !canGoOld) {
                return;
            }
            android.util.Log.e("TAG", "orientation = " + orientation);
            if ((orientation > 70 && orientation < 110) || (orientation > 250 && orientation < 290)) {
                toWhatOldByOrder(zcOrder);
            }
        }

    }

}
