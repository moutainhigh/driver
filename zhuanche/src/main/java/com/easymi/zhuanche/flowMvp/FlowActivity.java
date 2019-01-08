package com.easymi.zhuanche.flowMvp;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
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
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.utils.overlay.SmoothMoveMarker;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.RouteOverlayOptions;
import com.amap.api.navi.view.RouteOverLay;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.route.DriveRouteResult;
import com.easymi.common.entity.BuildPushData;
import com.easymi.common.entity.PassengerLocation;
import com.easymi.common.push.FeeChangeObserver;
import com.easymi.common.push.HandlePush;
//import com.easymi.common.push.MQTTService;
import com.easymi.common.push.MqttManager;
import com.easymi.common.push.PassengerLocObserver;
import com.easymi.common.result.SettingResult;
import com.easymi.common.trace.TraceInterface;
import com.easymi.common.trace.TraceReceiver;
import com.easymi.component.Config;
import com.easymi.component.DJOrderStatus;
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
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.utils.AesUtil;
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
import com.easymi.zhuanche.ZCApiService;
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
import com.easymi.zhuanche.result.PassengerLcResult;
import com.easymi.zhuanche.widget.FlowPopWindow;
import com.easymi.zhuanche.widget.RefuseOrderDialog;
import com.easymin.driver.securitycenter.utils.AudioUtil;
import com.easymin.driver.securitycenter.utils.CenterUtil;
import com.google.gson.Gson;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by developerLzh on 2017/11/13 0013.
 */
@Route(path = "/zhuanche/FlowActivity")
public class FlowActivity extends RxBaseActivity implements FlowContract.View,
        LocObserver,
        TraceInterface,
        FeeChangeObserver,
        PassengerLocObserver,
        CancelOrderReceiver.OnCancelListener,
        AMap.OnMapTouchListener,
        OrderFinishReceiver.OnFinishListener
//        , AMapLocationListener
{
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
     * 已接单top
     */
    RelativeLayout to_appoint_layout;
    TextView to_appoint_time;
    TextView to_appoint_left_time;
    LinearLayout to_appoint_navi_con;

    /**
     * 到达预约地top
     */
    RelativeLayout arrive_start_layout;

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
        tvMark = findViewById(R.id.tvMark);

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
        arrive_start_layout = findViewById(R.id.arrive_start_layout);

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
                if (notCancel || zcOrder.orderStatus == ZCOrderStatus.NEW_ORDER || zcOrder.orderStatus == ZCOrderStatus.PAIDAN_ORDER || zcOrder.orderStatus >= ZCOrderStatus.GOTO_DESTINATION_ORDER) {
                    popWindow.hideCancel();
                } else {
                    popWindow.showCancel();
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
                String phone = zcOrder.companyPhone;
                PhoneUtil.call(FlowActivity.this, phone);
            }
        });
    }

    @Override
    public void showTopView() {
        if (TextUtils.isEmpty(zcOrder.remark)) {
            tvMark.setText("无备注");
        } else {
            tvMark.setText(zcOrder.remark);
        }
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
            to_appoint_navi_con_1.setOnClickListener(view ->
                    presenter.navi(new LatLng(getStartAddr().lat, getStartAddr().lng), getStartAddr().poi, orderId));
        } else if (zcOrder.orderStatus == ZCOrderStatus.TAKE_ORDER
                || zcOrder.orderStatus == ZCOrderStatus.GOTO_BOOKPALCE_ORDER) {
            hideTops();
            to_appoint_layout.setVisibility(View.VISIBLE);
            to_appoint_navi_con.setOnClickListener(view ->
                    presenter.navi(new LatLng(getStartAddr().lat, getStartAddr().lng), getStartAddr().poi, orderId));

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
            arrive_start_layout.setVisibility(View.VISIBLE);
        } else if (zcOrder.orderStatus == ZCOrderStatus.START_WAIT_ORDER) {
            hideTops();
            middle_wait_layout.setVisibility(View.VISIBLE);
        } else {
            hideTops();
            go_layout.setVisibility(View.VISIBLE);
            naviCon.setOnClickListener(view -> {
                if (null != getEndAddr()) {
                    presenter.navi(new LatLng(getEndAddr().lat, getEndAddr().lng), getEndAddr().poi, orderId);
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
            nextPlace.setText(zcOrder.getEndSite().addr);
        } else {
            nextPlace.setText(zcOrder.getStartSite().addr);
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
                    bridge.doPay(dymOrder.totalFee);
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

    }

    @Override
    public void showOrder(ZCOrder zcOrder) {
        if (null == zcOrder) {
            finish();
        } else {
            if (zcOrder.orderStatus >= DJOrderStatus.FINISH_ORDER) {
                ToastUtil.showMessage(this, getResources().getString(R.string.order_finish));
                finish();
            }
            ZCSetting zcSetting = ZCSetting.findOne();
            if (zcSetting.isPaid == 2) {
                if (zcOrder.orderStatus == DJOrderStatus.ARRIVAL_DESTINATION_ORDER) {
                    finish();
                }
            }
            this.zcOrder = zcOrder;
            initMap();
            showTopView();
            initBridge();
            showBottomFragment(zcOrder);
            showMapBounds();

            if (zcOrder.orderStatus < ZCOrderStatus.GOTO_DESTINATION_ORDER) {
                if (mPlocation == null) {
                    passengerLoc(zcOrder.orderId);
                }
            } else {
                if (plMaker != null) {
                    plMaker.remove();
                }
            }
        }
    }

    MyLocationStyle myLocationStyle;

    @Override
    public void initMap() {
        aMap = mapView.getMap();
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.getUiSettings().setRotateGesturesEnabled(false);
        aMap.getUiSettings().setRotateGesturesEnabled(false);
        aMap.getUiSettings().setTiltGesturesEnabled(false);//倾斜手势
        aMap.getUiSettings().setLogoBottomMargin(-50);//隐藏logo

        aMap.setOnMapTouchListener(this);

        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        // 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false
        aMap.setMyLocationEnabled(true);

        String locStr = XApp.getMyPreferences().getString(Config.SP_LAST_LOC, "");
        EmLoc emLoc = new Gson().fromJson(locStr, EmLoc.class);
        if (null != emLoc) {
            lastLatlng = new LatLng(emLoc.latitude, emLoc.longitude);
            receiveLoc(emLoc);//手动调用上次位置 减少从北京跳过来的时间
            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLatlng, 17));//移动镜头，首次镜头快速跳到指定位置

            myLocationStyle = new MyLocationStyle();

            myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 设置圆形的边框颜色
            myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色

            if (zcOrder.orderStatus == ZCOrderStatus.GOTO_BOOKPALCE_ORDER || zcOrder.orderStatus == ZCOrderStatus.GOTO_DESTINATION_ORDER) {
                myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
            } else {
                myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
            }
            myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(getResources(), R.mipmap.ic_flow_my_pos)));
            aMap.setMyLocationStyle(myLocationStyle);

//        location();
        }
    }

//    //声明AMapLocationClient类对象，定位发起端
//    private AMapLocationClient mLocationClient = null;
//    //声明mLocationOption对象，定位参数
//    public AMapLocationClientOption mLocationOption = null;
//
//    private void location() {
//        //初始化定位
//        mLocationClient = new AMapLocationClient(getApplicationContext());
//        //设置定位回调监听
//        mLocationClient.setLocationListener(this);
//        //初始化定位参数
//        mLocationOption = new AMapLocationClientOption();
//        //设置定位模式为Hight_Accuracy高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
//        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//        //设置是否返回地址信息（默认返回地址信息）
//        mLocationOption.setNeedAddress(true);
//        //设置是否只定位一次,默认为false
//        mLocationOption.setOnceLocation(false);
//        //设置是否允许模拟位置,默认为false，不允许模拟位置  true方法开启允许位置模拟
//        mLocationOption.setMockEnable(true);
//        //设置定位间隔,单位毫秒,默认为2000ms
//        mLocationOption.setInterval(2000);
//        //给定位客户端对象设置定位参数
//        mLocationClient.setLocationOption(mLocationOption);
//        //启动定位
//        mLocationClient.startLocation();
//    }

//    @Override
//    public void onLocationChanged(AMapLocation aMapLocation) {
//        if (!isMapTouched) {
//            myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
//            aMap.setMyLocationStyle(myLocationStyle);
//            if (zcOrder.orderStatus == ZCOrderStatus.GOTO_BOOKPALCE_ORDER || zcOrder.orderStatus == ZCOrderStatus.GOTO_DESTINATION_ORDER) {
//                aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
//            }
//        } else {
//            //todo 地图滑动第二次才生效的问题
//            myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
//            aMap.setMyLocationStyle(myLocationStyle);
//            if ((System.currentTimeMillis() - XApp.getMyPreferences().getLong(Config.DOWN_TIME, 0)) / 1000 > 5) {
//                isMapTouched = false;
//            }
//        }
//    }

    private Marker startMarker;
    private Marker endMarker;

//    private Marker myFirstMarker;//首次进入时默认位置的marker

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
//            if (null != getEndAddr()) {
//                latLngs.add(new LatLng(getEndAddr().lat, getEndAddr().lng));
//            }
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
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLatlng, 17));
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
//                markerOption.setFlat(true);//设置marker平贴地图效果
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
//                markerOption.setFlat(true);//设置marker平贴地图效果
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
//        if (null != routeOverLay) {
//            routeOverLay.removeFromMap();
//        }
//        aMap.moveCamera(CameraUpdateFactory.changeTilt(0));

        RouteOverLay routeOverLay = new RouteOverLay(aMap, path, this);

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

        if (this.routeOverLay != null) {
            this.routeOverLay.removeFromMap();
        }
        this.routeOverLay = routeOverLay;
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
        latLngs.add(new LatLng(lastLoc.latitude, lastLoc.latitude));

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

        pay1Text.setVisibility(View.GONE);
        pay1Empty.setVisibility(View.GONE);
        pay1Btn.setVisibility(View.GONE);
        pay1Img.setVisibility(View.GONE);
//        if (consumerInfo.consumerBalance < money) {
        pay2Text.setVisibility(View.GONE);
        pay2Empty.setVisibility(View.GONE);
        pay2Btn.setVisibility(View.GONE);
        pay2Img.setVisibility(View.GONE);
//        }
//        if (!consumerInfo.canSign) {
        pay3Text.setVisibility(View.GONE);
        pay3Empty.setVisibility(View.GONE);
        pay3Btn.setVisibility(View.GONE);
        pay3Img.setVisibility(View.GONE);
//        }
        boolean canDaifu = (ZCSetting.findOne().isPaid == 1);
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

        pay4Empty.setOnClickListener(view14 -> {
            if (pay4Btn.isChecked()) {
                pay4Btn.setChecked(false);
                pay3Btn.setChecked(true);
                pay2Btn.setChecked(false);
                pay1Btn.setChecked(false);
            } else {
                pay4Btn.setChecked(true);
                pay3Btn.setChecked(false);
                pay2Btn.setChecked(false);
                pay1Btn.setChecked(false);
            }
        });
        pay4Text.setOnClickListener(view14 -> {
            if (pay4Btn.isChecked()) {
                pay4Btn.setChecked(false);
                pay3Btn.setChecked(true);
                pay2Btn.setChecked(false);
                pay1Btn.setChecked(false);
            } else {
                pay4Btn.setChecked(true);
                pay3Btn.setChecked(false);
                pay2Btn.setChecked(false);
                pay1Btn.setChecked(false);
            }
        });
        pay4Img.setOnClickListener(view14 -> {
            if (pay4Btn.isChecked()) {
                pay4Btn.setChecked(false);
                pay3Btn.setChecked(true);
                pay2Btn.setChecked(false);
                pay1Btn.setChecked(false);
            } else {
                pay4Btn.setChecked(true);
                pay3Btn.setChecked(false);
                pay2Btn.setChecked(false);
                pay1Btn.setChecked(false);
            }
        });

        Button sure = view.findViewById(R.id.pay_button);
        ImageView close = view.findViewById(R.id.ic_close);

        sure.setText(getString(R.string.pay_money) + money + getString(R.string.yuan));

        sure.setOnClickListener(view12 -> {
            if (pay4Btn.isChecked()) {
                if (ZCSetting.findOne().driverRepLowBalance == 2) {
                    if (money > EmUtil.getEmployInfo().balance) {
                        ToastUtil.showMessage(this, getResources().getString(R.string.no_balance));
                    } else {
                        presenter.payOrder(orderId, "PAY_DRIVER_BALANCE", zcOrder.version);
                    }
                } else {
                    presenter.payOrder(orderId, "PAY_DRIVER_BALANCE", zcOrder.version);
                }
            } else {
                ToastUtil.showMessage(this, "请选择支付方式");
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
            if (km >= 1) {
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
            if (km >= 1) {
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

    @Override
    public void showReCal() {
        if (zcOrder != null) {
            if (zcOrder.orderStatus == ZCOrderStatus.GOTO_DESTINATION_ORDER) {
                if (null != getEndAddr()) {
                    presenter.routePlanByNavi(getEndAddr().lat, getEndAddr().lng);
                }
            } else if (zcOrder.orderStatus == ZCOrderStatus.GOTO_BOOKPALCE_ORDER) {
                if (null != getEndAddr()) {
                    presenter.routePlanByNavi(getStartAddr().lat, getStartAddr().lng);
                }
            }
        }
    }

    private Address getStartAddr() {
        Address startAddress = null;
        if (zcOrder != null && zcOrder.orderAddressVos != null && zcOrder.orderAddressVos.size() != 0) {
            for (Address address : zcOrder.orderAddressVos) {
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
        if (zcOrder != null && zcOrder.orderAddressVos != null && zcOrder.orderAddressVos.size() != 0) {
            for (Address address : zcOrder.orderAddressVos) {
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
    public void reRout() {
        if (zcOrder.orderStatus == ZCOrderStatus.GOTO_DESTINATION_ORDER) {
            if (null != getEndAddr()) {
                presenter.routePlanByNavi(getEndAddr().lat, getEndAddr().lng);
            }
        } else if (zcOrder.orderStatus == ZCOrderStatus.GOTO_BOOKPALCE_ORDER) {
            if (null != getEndAddr()) {
                presenter.routePlanByNavi(getStartAddr().lat, getStartAddr().lng);
            }
        }
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
                presenter.acceptOrder(zcOrder.orderId, zcOrder.version, btn);
                //todo 一键报警
//                CenterUtil centerUtil = new CenterUtil(FlowActivity.this,Config.APP_KEY,
//                        XApp.getMyPreferences().getString(Config.AES_PASSWORD, AesUtil.AAAAA),
//                        XApp.getMyPreferences().getString(Config.SP_TOKEN, ""));
//                centerUtil.smsShareAuto( zcOrder.orderId, EmUtil.getEmployInfo().companyId,  zcOrder.passengerId,  zcOrder.passengerPhone,  zcOrder.orderType);
//                centerUtil.checkingAuth( zcOrder.passengerId);
            }

            @Override
            public void doRefuse() {
                RefuseOrderDialog.Builder builder = new RefuseOrderDialog.Builder(FlowActivity.this);
                builder.setApplyClick(reason -> presenter.refuseOrder(zcOrder.orderId, zcOrder.orderType, reason));
                RefuseOrderDialog dialog1 = builder.create();
                dialog1.setCanceledOnTouchOutside(true);
                dialog1.show();
            }

            @Override
            public void doToStart(LoadingButton btn) {
                presenter.toStart(zcOrder.orderId, zcOrder.version, btn);
            }

            @Override
            public void doArriveStart() {
                presenter.arriveStart(zcOrder.orderId, zcOrder.version);
            }

            @Override
            public void doStartWait(LoadingButton btn) {
                //前往目的地的中途等待
                presenter.startWait(zcOrder.orderId, btn);
            }

            @Override
            public void doStartWait() {
                //到达预约地的中途等待
                presenter.startWait(zcOrder.orderId);
            }

            @Override
            public void doStartDrive(LoadingButton btn) {
                presenter.startDrive(zcOrder.orderId, zcOrder.version, btn);
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
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLatlng, 17));
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
                presenter.arriveDes(zcOrder, zcOrder.version, btn, dymOrder);
            }

            @Override
            public void doPay(double money) {
                payMoney = money;
                boolean canDaifu = (ZCSetting.findOne().isPaid == 1);
                if (canDaifu) {
                    showPayType(payMoney, null);
                } else {
//                    ToastUtil.showMessage(FlowActivity.this,"费用信息已发送到客户");
                    if (settleFragmentDialog != null) {
                        settleFragmentDialog.dismiss();
                    }
                    finish();
                }
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
        boolean canDaifu = (ZCSetting.findOne().isPaid == 1);
        if (canDaifu) {
            showPayType(payMoney, consumerInfo);
        } else {
//            ToastUtil.showMessage(this,"费用信息已发送到客户");
            if (settleFragmentDialog != null) {
                settleFragmentDialog.dismiss();
            }
            finish();
        }
    }

    @Override
    public void hideTops() {
        not_accept_layout.setVisibility(View.GONE);
        arrive_start_layout.setVisibility(View.GONE);
        to_appoint_layout.setVisibility(View.GONE);
        go_layout.setVisibility(View.GONE);
        middle_wait_layout.setVisibility(View.GONE);
    }

//    @Override
//    public ZCOrder getOrder() {
//        return zcOrder;
//    }

    @Override
    protected void onStart() {
        super.onStart();
        LocReceiver.getInstance().addObserver(this);//添加位置订阅
        HandlePush.getInstance().addObserver(this);//添加订单变化订阅
        HandlePush.getInstance().addPLObserver(this);//乘客位置变化订阅

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

        if (mPlocation!= null){
            mPlocation = null;
        }

//        //add
//        if (mLocationClient != null) {
//            mLocationClient.stopLocation();//停止定位
//            mLocationClient.onDestroy();//销毁定位客户端。
//        }
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

//    SmoothMoveMarker smoothMoveMarker;

    private LatLng lastLatlng;

    @Override
    public void receiveLoc(EmLoc location) {
        if (null == location) {
            return;
        }
        Log.e("locPos", "bearing 2 >>>>" + location.bearing);
        LatLng latLng = new LatLng(location.latitude, location.longitude);

        if (myLocationStyle == null) {
            myLocationStyle = new MyLocationStyle();

            myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 设置圆形的边框颜色
            myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色
        }
        if (!isMapTouched) {
            if (zcOrder != null) {
                if (zcOrder.orderStatus == ZCOrderStatus.GOTO_BOOKPALCE_ORDER || zcOrder.orderStatus == ZCOrderStatus.GOTO_DESTINATION_ORDER) {
                    aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17), Config.NORMAL_LOC_TIME, null);
                    myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
                }
            }
            if (aMap != null) {
                aMap.setMyLocationStyle(myLocationStyle);
            }
        } else {
            //todo 地图滑动第二次才生效的问题待处理
            myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
            if (aMap != null) {
                aMap.setMyLocationStyle(myLocationStyle);
            }
            if ((System.currentTimeMillis() - XApp.getMyPreferences().getLong(Config.DOWN_TIME, 0)) / 1000 > 5) {
                isMapTouched = false;
            }
        }

        if (zcOrder != null) {
            if (zcOrder.orderStatus == ZCOrderStatus.GOTO_DESTINATION_ORDER) {
                if (null != getEndAddr()) {
                    presenter.routePlanByNavi(getEndAddr().lat, getEndAddr().lng);
                }
            } else if (zcOrder.orderStatus == ZCOrderStatus.GOTO_BOOKPALCE_ORDER) {
                if (null != getEndAddr()) {
                    presenter.routePlanByNavi(getStartAddr().lat, getStartAddr().lng);
                }
            }
        }

        lastLatlng = latLng;

//        if (null == smoothMoveMarker) {//首次进入
//            smoothMoveMarker = new SmoothMoveMarker(aMap);
//            smoothMoveMarker.setDescriptor(BitmapDescriptorFactory.fromBitmap(BitmapFactory
//                    .decodeResource(getResources(), R.mipmap.ic_flow_my_pos)));
//            smoothMoveMarker.setPosition(lastLatlng);
//            smoothMoveMarker.setRotate(location.bearing);
//        } else {
//            if (null != myFirstMarker) {//去除掉首次的位置marker
//                myFirstMarker.remove();
//            }
//            List<LatLng> latLngs = new ArrayList<>();
//            latLngs.add(lastLatlng);
//            latLngs.add(latLng);
//            smoothMoveMarker.setPosition(lastLatlng);
//            smoothMoveMarker.setPoints(latLngs);
//
//            smoothMoveMarker.setTotalDuration(Config.NORMAL_LOC_TIME / 1000);
//
//            smoothMoveMarker.setRotate(location.bearing);
//            smoothMoveMarker.startSmoothMove();
//            Marker marker = smoothMoveMarker.getMarker();
//            if (null != marker) {
//                marker.setDraggable(false);
//                marker.setClickable(false);
//                marker.setAnchor(0.5f, 0.5f);
//            }
//        }

//        if (zcOrder != null) {
//            if (zcOrder.orderStatus == ZCOrderStatus.GOTO_DESTINATION_ORDER) {
//                if (null != getEndAddr()) {
//                    presenter.routePlanByNavi(getEndAddr().lat, getEndAddr().lng);
//                }
//            } else if (zcOrder.orderStatus == ZCOrderStatus.GOTO_BOOKPALCE_ORDER) {
//                if (null != getEndAddr()) {
//                    presenter.routePlanByNavi(getStartAddr().lat, getStartAddr().lng);
//                }
//            }
//        }
//
//        if (null != zcOrder) {
//            if (zcOrder.orderStatus == ZCOrderStatus.GOTO_DESTINATION_ORDER
//                    || zcOrder.orderStatus == ZCOrderStatus.GOTO_BOOKPALCE_ORDER) {
//                if (!isMapTouched) {
//                    aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17), Config.NORMAL_LOC_TIME, null);
//                }
//            }
//        }
//        lastLatlng = latLng;
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

    private List<LatLng> traceLatlngs = new ArrayList<>();

    @Override
    public void showTraceAfter(EmLoc traceLoc) {
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

    private AlertDialog cancelDialog;

    @Override
    public void onCancelOrder(long orderId, String orderType, String msg) {
        if (zcOrder == null) {
            return;
        }
        if (orderId == zcOrder.orderId
                && orderType.equals(zcOrder.orderType)) {
            //todo 一键报警
//            AudioUtil audioUtil = new AudioUtil();
//            audioUtil.onRecord(this, false);
            if (cancelDialog == null) {
                cancelDialog = new AlertDialog.Builder(this)
                        .setMessage(msg)
                        .setPositiveButton(R.string.ok, (dialog1, which) -> {
                            dialog1.dismiss();
                            finish();
                        })
                        .setOnDismissListener(dialog12 -> finish())
                        .create();
                cancelDialog.show();
            }
        }
    }

    private Marker plMaker;
    private PassengerLocation mPlocation;

    @Override
    public void plChange(PassengerLocation plocation) {
        if (zcOrder != null && zcOrder.orderStatus < ZCOrderStatus.GOTO_DESTINATION_ORDER) {
            if (plocation != null) {
                if (null != mPlocation) {
                    if (zcOrder.orderId == plocation.orderId) {
                        if (plocation.latitude != mPlocation.latitude && plocation.longitude != mPlocation.longitude) {
                            mPlocation = plocation;
                            addPlMaker();
                        }
                    } else {
                        if (plMaker != null) {
                            plMaker.remove();
                        }
                    }
                } else {
                    mPlocation = plocation;
                    addPlMaker();
                }
            }else {
                if (plMaker != null) {
                    plMaker.remove();
                }
            }
        } else {
            if (plMaker != null) {
                plMaker.remove();
            }
        }
    }

    public void addPlMaker() {
        if (plMaker != null) {
            plMaker.remove();
        }
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(new LatLng(mPlocation.latitude, mPlocation.longitude));
        markerOption.draggable(false);//设置Marker可拖动
        markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(), R.mipmap.ic_passenger_location)));
        // 将Marker设置为贴地显示，可以双指下拉地图查看效果
        markerOption.setFlat(true);//设置marker平贴地图效果
        plMaker = aMap.addMarker(markerOption);
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
            if (zcOrder.orderStatus == ZCOrderStatus.GOTO_DESTINATION_ORDER || zcOrder.orderStatus == ZCOrderStatus.GOTO_BOOKPALCE_ORDER) {
                isMapTouched = true;
                XApp.getPreferencesEditor().putLong(Config.DOWN_TIME, System.currentTimeMillis()).apply();
            }
            if (null != runningFragment) {
                runningFragment.mapStatusChanged();
            }
        }
    }


    @Override
    public void onFinishOrder(long orderId, String orderType) {
        if (orderId == this.orderId && orderType.equals(Config.ZHUANCHE)) {
            ToastUtil.showMessage(this, getString(R.string.finished_order));
            if (bottomSheetDialog != null) {
                bottomSheetDialog.dismiss();
            }
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
//
//            if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN || !canGoOld) {
//                return;
//            }
//            android.util.Log.e("TAG", "orientation = " + orientation);
//            if ((orientation > 70 && orientation < 110) || (orientation > 250 && orientation < 290)) {
//                toWhatOldByOrder(zcOrder);
//            }
        }
    }

    public void passengerLoc(long orderId) {
        Observable<PassengerLcResult> observable = ApiManager.getInstance().createApi(Config.HOST, ZCApiService.class)
                .passengerLoc(orderId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        observable.subscribe(new MySubscriber<>(this, false, false, passengerLcResult -> {
            if (passengerLcResult.getCode() == 1) {
                plChange(passengerLcResult.data);
            }
//            else if (passengerLcResult.getCode() == 40005) {
//                ToastUtil.showMessage(this, "客户退出app或微信下单，未获取到客户位置");
//            }
        }));
    }

}
