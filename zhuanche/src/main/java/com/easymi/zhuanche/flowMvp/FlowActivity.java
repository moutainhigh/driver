package com.easymi.zhuanche.flowMvp;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
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
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.RouteOverlayOptions;
import com.amap.api.navi.view.RouteOverLay;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.route.DriveRouteResult;
import com.easymi.common.entity.BuildPushData;
import com.easymi.common.push.FeeChangeObserver;
import com.easymi.common.push.HandlePush;
import com.easymi.common.push.MqttManager;
import com.easymi.common.push.PassengerLocObserver;
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
import com.easymi.component.entity.PassengerLcResult;
import com.easymi.component.entity.PassengerLocation;
import com.easymi.component.entity.ZCSetting;
import com.easymi.component.loc.LocObserver;
import com.easymi.component.loc.LocReceiver;
import com.easymi.component.loc.LocService;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.push.PushEvent;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.utils.DensityUtil;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.MapUtil;
import com.easymi.component.utils.SensorEventHelper;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.CusBottomSheetDialog;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.LoadingButton;
import com.easymi.component.widget.overlay.DrivingRouteOverlay;
import com.easymi.zhuanche.R;
import com.easymi.zhuanche.ZCApiService;
import com.easymi.zhuanche.activity.CancelNewActivity;
import com.easymi.zhuanche.entity.Address;
import com.easymi.zhuanche.entity.ConsumerInfo;
import com.easymi.zhuanche.entity.ZCOrder;
import com.easymi.zhuanche.fragment.AcceptFragment;
import com.easymi.zhuanche.fragment.ArriveStartFragment;
import com.easymi.zhuanche.fragment.RunningFragment;
import com.easymi.zhuanche.fragment.SettleFragmentDialog;
import com.easymi.zhuanche.fragment.SlideArriveStartFragment;
import com.easymi.zhuanche.fragment.ToStartFragment;
import com.easymi.zhuanche.fragment.WaitFragment;
import com.easymi.zhuanche.receiver.CancelOrderReceiver;
import com.easymi.zhuanche.receiver.OrderFinishReceiver;
import com.easymi.zhuanche.widget.RefuseOrderDialog;
import com.google.gson.Gson;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: FinishActivity
 *
 * @Author: hufeng
 * Date: 2018/10/24 下午1:10
 * Description:
 * History:
 */
@Route(path = "/zhuanche/FlowActivity")
public class FlowActivity extends RxBaseActivity implements FlowContract.View,
        LocObserver,
        TraceInterface,
        FeeChangeObserver,
        PassengerLocObserver,
        AMap.OnMapTouchListener,
        CancelOrderReceiver.OnCancelListener,
        OrderFinishReceiver.OnFinishListener {

    public static final int CANCEL_ORDER = 0X01;
    public static final int CHANGE_END = 0X02;
    public static final int CHANGE_ORDER = 0X03;

    CusToolbar toolbar;
    TextView orderNumberText;
    TextView orderTypeText;
    TagContainerLayout tagContainerLayout;
    LinearLayout drawerFrame;
    TextView tvMark;
    ExpandableLayout expandableLayout;
    /**
     * 顶部布局
     */
    LinearLayout top_layout;

    /**
     * 地点状态
     */
    TextView go_text;

    /**
     * 前往地点
     */
    TextView next_place;

    /**
     * 剩余时间里程
     */
    TextView left_time;
    /**
     * 导航
     */
    LinearLayout lin_navi;
    /**
     * 等待时间
     */
    LinearLayout lin_time;
    /**
     * 等待计时
     */
    TextView tv_time;
    /**
     * 等待状态提示
     */
    TextView tv_time_hint;

    private ZCOrder zcOrder;

    private FlowPresenter presenter;
    /**
     * activity和fragment的通信接口
     */
    private ActFraCommBridge bridge;

    /**
     * 疲劳驾驶接收器
     */
    private TraceReceiver traceReceiver;
    /**
     * 取消订单
     */
    private CancelOrderReceiver cancelOrderReceiver;
    /**
     * 订单结束
     */
    private OrderFinishReceiver orderFinishReceiver;

    private AMap aMap;

    /**
     * 订单id
     */
    private long orderId;

    /**
     * 支付费用
     */
    private double payMoney;


    private Marker myLocationMarker;
    private MapView mapView;
    private SensorEventHelper helper;
    private boolean flashAssign;

    @Override
    public int getLayoutId() {
        return R.layout.zc_activity_flow;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        // 屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        orderId = getIntent().getLongExtra("orderId", -1);

        flashAssign = getIntent().getBooleanExtra("flashAssign", false);
        if (orderId == -1) {
            finish();
            return;
        }

        presenter = new FlowPresenter(this, this);

        toolbar = findViewById(R.id.toolbar);
        orderNumberText = findViewById(R.id.order_number_text);
        orderTypeText = findViewById(R.id.order_type);
        tagContainerLayout = findViewById(R.id.tag_container);
        drawerFrame = findViewById(R.id.drawer_frame);
        expandableLayout = findViewById(R.id.expandable_layout);
        tvMark = findViewById(R.id.tvMark);

        top_layout = findViewById(R.id.top_layout);
        go_text = findViewById(R.id.go_text);
        next_place = findViewById(R.id.next_place);
        left_time = findViewById(R.id.left_time);
        lin_navi = findViewById(R.id.lin_navi);
        lin_time = findViewById(R.id.lin_time);
        tv_time = findViewById(R.id.tv_time);
        tv_time_hint = findViewById(R.id.tv_time_hint);

        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        initToolbar();
        initMap();
    }

    @Override
    public void initToolbar() {
        toolbar.setLeftIcon(R.drawable.ic_arrow_back, v -> finish());
    }

    /**
     * 倒计时计时器
     */
    Timer timer;
    TimerTask timerTask;

    /**
     * 取消定时器
     */
    public void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }

    /**
     * 等待时间
     */
    private long timeSeq = 0;

    /**
     * 等待倒计时
     */
    public void setWaitTime() {
        lin_time.setVisibility(View.VISIBLE);
        lin_navi.setVisibility(View.GONE);

        if (XApp.getMyPreferences().getLong("" + zcOrder.orderId, 0) == 0) {
            XApp.getEditor().putLong("" + zcOrder.orderId, System.currentTimeMillis() + ZCSetting.findOne().arriveTime * 60 * 1000).apply();
        }

        if (null != timer) {
            timer.cancel();
            timer = null;
        }
        if (null != timerTask) {
            timerTask.cancel();
            timerTask = null;
        }

        long appoint = XApp.getMyPreferences().getLong("" + zcOrder.orderId, 0);
        timeSeq = (appoint - System.currentTimeMillis()) / 1000;
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                timeSeq--;
                setTimeText();
            }
        };
        timer.schedule(timerTask, 0, 1000);
        setTimeText();
    }

    /**
     * 显示对应格式等待时间
     */
    private void setTimeText() {
        runOnUiThread(() -> {
            StringBuilder sb = new StringBuilder();
            int minute = (int) (Math.abs(timeSeq) / 60);
            int sec = (int) (Math.abs(timeSeq) % 60);
            if (minute < 10) {
                sb.append("0");
            }
            sb.append(minute).append(":");
            if (sec < 10) {
                sb.append("0");
            }
            sb.append(sec);
            if (timeSeq < 0) {
                //超时
                tv_time_hint.setText("等待已超时");
                tv_time.setText(sb.toString());
                tv_time.setTextColor(getResources().getColor(R.color.color_red));
                tv_time_hint.setTextColor(getResources().getColor(R.color.color_red));
            } else {
                //正常计时
                tv_time_hint.setText("等待倒计时");
                tv_time.setText(sb.toString());
                tv_time.setTextColor(getResources().getColor(R.color.color_3c98e3));
                tv_time_hint.setTextColor(getResources().getColor(R.color.color_999999));
            }

            if ((ZCSetting.findOne().arriveCancel == 1)) {
                if (timeSeq < 0) {
                    toolbar.setRightText(R.string.cancel_order, v -> {
                        Intent intent = new Intent(this, CancelNewActivity.class);
                        startActivityForResult(intent, CANCEL_ORDER);
                    });
                } else {
                    toolbar.setRightText("", null);
                }
            } else {
                toolbar.setRightText("", null);
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
            lin_navi.setOnClickListener(view ->
                    presenter.navi(new LatLng(getStartAddr().lat, getStartAddr().lng), getStartAddr().poi, orderId));
            go_text.setText("去");
            lin_time.setVisibility(View.GONE);
            lin_navi.setVisibility(View.VISIBLE);
        } else if (zcOrder.orderStatus == ZCOrderStatus.TAKE_ORDER
                || zcOrder.orderStatus == ZCOrderStatus.GOTO_BOOKPALCE_ORDER) {
            lin_navi.setOnClickListener(view ->
                    presenter.navi(new LatLng(getStartAddr().lat, getStartAddr().lng), getStartAddr().poi, orderId));
            go_text.setText("去");
            lin_time.setVisibility(View.GONE);
            lin_navi.setVisibility(View.VISIBLE);
        } else if (zcOrder.orderStatus == ZCOrderStatus.ARRIVAL_BOOKPLACE_ORDER) {
            go_text.setText("已到");
            if ((ZCSetting.findOne().arriveCancel == 1)) {
                lin_time.setVisibility(View.VISIBLE);
                setWaitTime();
            } else {
                lin_time.setVisibility(View.GONE);
            }
        } else if (zcOrder.orderStatus == ZCOrderStatus.START_WAIT_ORDER) {
            go_text.setText("已到");
            if ((ZCSetting.findOne().arriveCancel == 1)) {
                lin_time.setVisibility(View.VISIBLE);
                setWaitTime();
            } else {
                lin_time.setVisibility(View.GONE);
            }
        } else {
            if (XApp.getMyPreferences().getLong("" + zcOrder.orderId, 0) != 0) {
                XApp.getEditor().remove("" + zcOrder);
            }
            go_text.setText("去");
            lin_time.setVisibility(View.GONE);
            lin_navi.setVisibility(View.VISIBLE);
            lin_navi.setOnClickListener(view -> {
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
            next_place.setText(zcOrder.getEndSite().addr);
        } else {
            next_place.setText(zcOrder.getStartSite().addr);
        }
    }

    WaitFragment waitFragment;
    RunningFragment runningFragment;
    SettleFragmentDialog settleFragmentDialog;

    @Override
    public void showBottomFragment(ZCOrder zcOrder) {
        toolbar.setRightText("", null);
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
            if ((ZCSetting.findOne().goToCancel == 1)) {
                toolbar.setRightText(R.string.cancel_order, v -> {
                    Intent intent = new Intent(this, CancelNewActivity.class);
                    startActivityForResult(intent, CANCEL_ORDER);
                });
            } else {
                toolbar.setRightText("", null);
            }
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
            if ((ZCSetting.findOne().arriveCancel == 1)) {
                toolbar.setRightText(R.string.cancel_order, v -> {
                    Intent intent = new Intent(this, CancelNewActivity.class);
                    startActivityForResult(intent, CANCEL_ORDER);
                });
            } else {
                toolbar.setRightText("", null);
            }
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
            toolbar.setTitle(R.string.wait_consumer);
            if ((ZCSetting.findOne().arriveCancel == 1)) {
                toolbar.setRightText(R.string.cancel_order, v -> {
                    Intent intent = new Intent(this, CancelNewActivity.class);
                    startActivityForResult(intent, CANCEL_ORDER);
                });
            } else {
                toolbar.setRightText("", null);
            }
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
//                if (settleFragmentDialog != null && settleFragmentDialog.isShowing()) {
//                    settleFragmentDialog.setDjOrder(zcOrder);
//                } else {
//                    settleFragmentDialog = new SettleFragmentDialog(FlowActivity.this, zcOrder, bridge);
//                    settleFragmentDialog.show();
//                }
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

            if (settleFragmentDialog != null && settleFragmentDialog.isShowing()) {
                settleFragmentDialog.setDjOrder(zcOrder);
                //确认费用后直接弹出支付页面
                DymOrder dymOrder = DymOrder.findByIDType(orderId, Config.ZHUANCHE);
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
            }
        }
    }


    private void addLocationMarker() {
        LatLng latLng = new LatLng(EmUtil.getLastLoc().latitude, EmUtil.getLastLoc().longitude);
        if (null == myLocationMarker) {
            MarkerOptions markerOption = new MarkerOptions();
            markerOption.anchor(0.5f, 0.5f);
            markerOption.position(latLng);
            markerOption.draggable(false);
            markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(getResources(), R.mipmap.location_mine)));
            myLocationMarker = aMap.addMarker(markerOption);
            helper.setMarker(myLocationMarker);
        } else {
            myLocationMarker.setPosition(latLng);
        }
    }

    private void flashAssign() {
        XApp.getInstance().syntheticVoice("您有快速指派订单需要处理，客户起点为" + zcOrder.getStartSite().addr + ",终点为" + zcOrder.getEndSite().addr);
        flashAssign = false;
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
            if (zcSetting.isPaid == 2 || (zcOrder.prepayment && zcOrder.paid)) {
                if (zcOrder.orderStatus == DJOrderStatus.ARRIVAL_DESTINATION_ORDER) {
                    finish();
                }
            }
            this.zcOrder = zcOrder;
            if (flashAssign &&zcOrder.orderStatus ==DJOrderStatus.GOTO_BOOKPALCE_ORDER){
                flashAssign();
            }
            showTopView();
            initBridge();
            showBottomFragment(zcOrder);
            showMapBounds();
            addLocationMarker();

            if (zcOrder.orderStatus < ZCOrderStatus.GOTO_DESTINATION_ORDER) {
                Log.e("FlowActivity", "showOrder" + (mPlocation == null));
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

    @Override
    public void initMap() {

        aMap = mapView.getMap();
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.getUiSettings().setRotateGesturesEnabled(false);
        aMap.getUiSettings().setTiltGesturesEnabled(false);//倾斜手势
        aMap.getUiSettings().setCompassEnabled(false);//倾斜手势
        aMap.getUiSettings().setTiltGesturesEnabled(false);//倾斜手势
        aMap.getUiSettings().setLogoBottomMargin(-50);//隐藏logo

        aMap.setOnMapTouchListener(this);

        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        // 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false
        aMap.setMyLocationEnabled(false);
        helper = new SensorEventHelper(this);
        helper.registerSensorListener();

        String locStr = XApp.getMyPreferences().getString(Config.SP_LAST_LOC, "");
        EmLoc emLoc = new Gson().fromJson(locStr, EmLoc.class);
        if (null != emLoc) {
            lastLatlng = new LatLng(emLoc.latitude, emLoc.longitude);
            //手动调用上次位置 减少从北京跳过来的时间
            receiveLoc(emLoc);
            //移动镜头，首次镜头快速跳到指定位置
            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLatlng, 17));
            addLocationMarker();
        }
    }

    /**
     * 起点终点marker
     */
    private Marker startMarker;
    private Marker endMarker;

    private void routePlan() {
        if (zcOrder == null) {
            return;
        }
        List<LatLng> latLngs = new ArrayList<>();
        if (zcOrder.orderStatus == ZCOrderStatus.NEW_ORDER
                || zcOrder.orderStatus == ZCOrderStatus.PAIDAN_ORDER
                || zcOrder.orderStatus == ZCOrderStatus.TAKE_ORDER) {
            if (null != getStartAddr()) {
                latLngs.add(new LatLng(getStartAddr().lat, getStartAddr().lng));
                presenter.routePlanByNavi(getStartAddr().lat, getStartAddr().lng);
            }
            LatLngBounds bounds = MapUtil.getBounds(latLngs, lastLatlng);
            aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, (int) (DensityUtil.getDisplayWidth(this) / 1.5), (int) (DensityUtil.getDisplayWidth(this) / 1.5), 0));
        } else if (zcOrder.orderStatus == ZCOrderStatus.GOTO_BOOKPALCE_ORDER) {
            if (null != getStartAddr()) {
                latLngs.add(new LatLng(getStartAddr().lat, getStartAddr().lng));
                presenter.routePlanByNavi(getStartAddr().lat, getStartAddr().lng);
            } else {
                presenter.stopNavi();
                left_time.setText("");
            }
            LatLngBounds bounds = MapUtil.getBounds(latLngs, lastLatlng);
            aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, (int) (DensityUtil.getDisplayWidth(this) / 1.5), (int) (DensityUtil.getDisplayWidth(this) / 1.5), 0));
        } else if (zcOrder.orderStatus == ZCOrderStatus.ARRIVAL_BOOKPLACE_ORDER
                || zcOrder.orderStatus == ZCOrderStatus.GOTO_DESTINATION_ORDER
                || zcOrder.orderStatus == ZCOrderStatus.START_WAIT_ORDER
                || zcOrder.orderStatus == ZCOrderStatus.ARRIVAL_DESTINATION_ORDER) {
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
    }

    @Override
    public void showMapBounds() {
        routePlan();
        if (null != getStartAddr()) {
            if (null == startMarker) {
                MarkerOptions markerOption = new MarkerOptions();
                markerOption.position(new LatLng(getStartAddr().lat, getStartAddr().lng));
                markerOption.draggable(false);//设置Marker可拖动
                markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(), R.mipmap.ic_start)));
                // 将Marker设置为贴地显示，可以双指下拉地图查看效果
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
        RouteOverLay routeOverLay = new RouteOverLay(aMap, path, this);

        routeOverLay.setStartPointBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.yellow_dot_small));
        routeOverLay.setEndPointBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.blue_dot_small));
        routeOverLay.setTrafficLine(true);

        RouteOverlayOptions options = new RouteOverlayOptions();
        options.setSmoothTraffic(BitmapFactory.decodeResource(getResources(), com.easymi.component.R.mipmap.custtexture_green));
        options.setUnknownTraffic(BitmapFactory.decodeResource(getResources(), com.easymi.component.R.mipmap.custtexture_green));
//        options.setUnknownTraffic(BitmapFactory.decodeResource(getResources(), com.easymi.component.R.mipmap.custtexture_no));
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

    /**
     * 驾车路径覆盖物
     */
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

    /**
     * /支付弹窗
     */
    private CusBottomSheetDialog bottomSheetDialog;

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
            left_time.setText(Html.fromHtml(disStr + timeStr));
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
            left_time.setText(Html.fromHtml(disStr + timeStr));
        }
    }

    @Override
    public void showReCal() {
        routePlan();
    }

    /**
     * 获取起点
     *
     * @return
     */
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

    /**
     * 获取终点
     *
     * @return
     */
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
    public void showToPlace(String toPlace) {
        next_place.setText(toPlace);
    }

    @Override
    public void showLeftTime(String leftTime) {
        left_time.setText(leftTime);
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
            public void doStartDrive() {
                presenter.startDrive(zcOrder.orderId, zcOrder.version);
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

            }

            @Override
            public void toFeeDetail() {
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
            if (settleFragmentDialog != null) {
                settleFragmentDialog.dismiss();
            }
            finish();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        LocReceiver.getInstance().addObserver(this);//添加位置订阅
        HandlePush.getInstance().addObserver(this);//添加订单变化订阅
        HandlePush.getInstance().addPLObserver(this);//乘客位置变化订阅

        traceReceiver = new TraceReceiver(this);
        IntentFilter filter2 = new IntentFilter();
        filter2.addAction(LocService.BROAD_TRACE_SUC);
        registerReceiver(traceReceiver, filter2, EmUtil.getBroadCastPermission(), null);

        cancelOrderReceiver = new CancelOrderReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Config.BROAD_CANCEL_ORDER);
        filter.addAction(Config.BROAD_BACK_ORDER);
        registerReceiver(cancelOrderReceiver, filter, EmUtil.getBroadCastPermission(), null);

        orderFinishReceiver = new OrderFinishReceiver(this);
        registerReceiver(orderFinishReceiver, new IntentFilter(Config.BROAD_FINISH_ORDER), EmUtil.getBroadCastPermission(), null);
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
        cancelTimer();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        presenter.stopNavi();
        if (mPlocation != null) {
            mPlocation = null;
        }
        if (helper != null) {
            helper.unRegisterSensorListener();
            helper = null;
        }

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

    private LatLng lastLatlng;

    @Override
    public void receiveLoc(EmLoc location) {
        if (null == location) {
            return;
        }
        LatLng latLng = new LatLng(location.latitude, location.longitude);

        addLocationMarker();

        if (!isMapTouched) {
            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
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

    /**
     * 纠偏路线
     */
    private Polyline tracePolyLine;
    /**
     * 纠偏点集合
     */
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
            finish();
        }
    }

    /**
     * 乘客位置marker
     */
    private Marker plMaker;
    private PassengerLocation mPlocation;

    @Override
    public void plChange(PassengerLocation plocation) {
        //订单只有在前往目的地前显示
        Log.e("FlowActivity", "plChange" + plocation.toString());
        EventBus.getDefault().post(new PushEvent(plocation));
        if (zcOrder != null && zcOrder.orderStatus < ZCOrderStatus.GOTO_DESTINATION_ORDER) {
            if (plocation != null) {
                if (zcOrder.orderId == plocation.orderId) {
                    mPlocation = plocation;
                    addPlMaker();
                }
            }
        } else {
            //到达目的地后就清理掉
            if (plMaker != null) {
                plMaker.remove();
            }
        }
    }

    /**
     * 添加乘客位置marker
     */
    public void addPlMaker() {
        if (mPlocation == null) {
            return;
        }
        LatLng latLng = new LatLng(mPlocation.latitude, mPlocation.longitude);
        if (plMaker == null) {
            MarkerOptions markerOption = new MarkerOptions();
            markerOption.position(latLng);
            markerOption.draggable(false);//设置Marker可拖动
            markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(getResources(), R.mipmap.ic_passenger_location)));
            plMaker = aMap.addMarker(markerOption);
        } else {
            plMaker.setPosition(latLng);
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
            if (null != settleFragmentDialog) {
                settleFragmentDialog.setDymOrder(dyo);
            }
        }
    }

    /**
     * 设置地图是否触摸拖动
     */
    public static boolean isMapTouched = false;

    @Override
    public void onTouch(MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
            isMapTouched = true;
            if (zcOrder.orderStatus == ZCOrderStatus.GOTO_DESTINATION_ORDER || zcOrder.orderStatus == ZCOrderStatus.GOTO_BOOKPALCE_ORDER) {
                XApp.getEditor().putLong(Config.DOWN_TIME, System.currentTimeMillis()).apply();
            }
            if (null != runningFragment) {
                runningFragment.mapStatusChanged();
            }
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            isMapTouched = false;
        } else if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            isMapTouched = true;
        } else if (motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
            isMapTouched = false;
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


    /**
     * 获取乘客位置
     *
     * @param orderId
     */
    public void passengerLoc(long orderId) {
        Observable<PassengerLcResult> observable = ApiManager.getInstance().createApi(Config.HOST, ZCApiService.class)
                .passengerLoc(orderId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        observable.subscribe(new MySubscriber<>(this, false, false, passengerLcResult -> {
            if (passengerLcResult.getCode() == 1) {
                plChange(passengerLcResult.data);
            }
        }));
    }

}
