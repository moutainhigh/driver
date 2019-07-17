package com.easymin.official.flowMvp;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
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
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.RouteOverlayOptions;
import com.amap.api.navi.view.RouteOverLay;
import com.amap.api.services.route.DriveRouteResult;
import com.easymi.common.CommApiService;
import com.easymi.common.entity.Pic;
import com.easymi.common.entity.QiNiuToken;
import com.easymi.common.push.FeeChangeObserver;
import com.easymi.common.push.HandlePush;
import com.easymi.component.Config;
import com.easymi.component.GWOrderStatus;
import com.easymi.component.ZCOrderStatus;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.entity.EmLoc;
import com.easymi.component.loc.LocObserver;
import com.easymi.component.loc.LocReceiver;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.utils.CountDownUtils;
import com.easymi.component.utils.DensityUtil;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.Log;
import com.easymi.component.utils.MapUtil;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.CusToolbar;
import com.easymin.official.R;
import com.easymin.official.activity.CancelNewActivity;
import com.easymin.official.entity.GovOrder;
import com.easymin.official.fragment.ArriveStartFragment;
import com.easymin.official.fragment.BookFragment;
import com.easymin.official.fragment.ConfirmFragment;
import com.easymin.official.fragment.RunningFragment;
import com.easymin.official.fragment.WaitFragment;
import com.easymin.official.receiver.CancelOrderReceiver;
import com.google.gson.Gson;
import com.luck.picture.lib.config.PictureConfig;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: FlowActivity
 * @Author: hufeng
 * @Date: 2019/3/25 下午2:50
 * @Description:
 * @History:
 */
@Route(path = "/official/FlowActivity")
public class FlowActivity extends RxBaseActivity implements FlowContract.View,
        AMap.OnMapTouchListener,
        LocObserver,
        FeeChangeObserver,
        CancelOrderReceiver.OnCancelListener {

    /**
     * 取消订单常量
     */
    public static final int CANCEL_ORDER = 0X01;

    private FlowPresenter presenter;
    /**
     * activity和fragment的通信接口
     */
    private ActFraCommBridge bridge;
    /**
     * 地图
     */
    private AMap aMap;
    /**
     * 司机位置
     */
    private LatLng lastLatlng;
    /**
     * 定位小蓝点
     */
    MyLocationStyle myLocationStyle;
    /**
     * 倒计时工具类
     */
    public CountDownUtils countDownUtils;
    /**
     * 设置地图是否触摸拖动
     */
    public boolean isMapTouched = false;

    /**
     * 公务用车订单
     */
    private GovOrder govOrder;

    /**
     * 前往目的地布局
     */
    RunningFragment runningFragment;

    /**
     * 确认费用布局
     */
    ConfirmFragment confirmFragment;

    /**
     * 订单id
     */
    private long orderId;
    /**
     * 确认单列表
     */
    ArrayList<String> images = new ArrayList<>();

    /**
     * 七牛云token
     */
    String qiniuToken;

    /**
     * 取消订单
     */
    private CancelOrderReceiver cancelOrderReceiver;

    CusToolbar toolbar;
    MapView map_view;
    LinearLayout top_layout;
    TextView go_text;
    TextView next_place;
    TextView left_time;
    LinearLayout navi_con;
    LinearLayout lin_navi;
    LinearLayout lin_time;
    TextView tv_time;
    TextView tv_time_hint;
    LinearLayout lin_start_countdown;
    TextView tv_day;
    TextView tv_hour;
    TextView tv_fen;
    ExpandableLayout expandable_layout;
    TextView tv_order_number;
    TextView tv_remark;
    LinearLayout drawer_frame;
    FrameLayout flow_frame;

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.gw_activity_flow;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        // 屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        findById();
        presenter = new FlowPresenter(this, this);

        map_view.onCreate(savedInstanceState);

        orderId = getIntent().getLongExtra("orderId", -1);

        presenter.findOne(orderId);
    }

    /**
     * 初始化控件
     */
    public void findById() {
        toolbar = findViewById(R.id.toolbar);
        map_view = findViewById(R.id.map_view);
        top_layout = findViewById(R.id.top_layout);
        go_text = findViewById(R.id.go_text);
        next_place = findViewById(R.id.next_place);
        left_time = findViewById(R.id.left_time);
        navi_con = findViewById(R.id.navi_con);
        lin_navi = findViewById(R.id.lin_navi);
        lin_time = findViewById(R.id.lin_time);
        tv_time = findViewById(R.id.tv_time);
        tv_time_hint = findViewById(R.id.tv_time_hint);
        lin_start_countdown = findViewById(R.id.lin_start_countdown);
        tv_day = findViewById(R.id.tv_day);
        tv_hour = findViewById(R.id.tv_hour);
        tv_fen = findViewById(R.id.tv_fen);
        expandable_layout = findViewById(R.id.expandable_layout);
        tv_order_number = findViewById(R.id.tv_order_number);
        tv_remark = findViewById(R.id.tv_remark);
        drawer_frame = findViewById(R.id.drawer_frame);
        flow_frame = findViewById(R.id.flow_frame);

        initToolbar();
        initMap();
    }

    @Override
    public void initToolbar() {
        toolbar.setLeftIcon(R.drawable.ic_arrow_back, v -> finish());
    }

    @Override
    public void initMap() {
        aMap = map_view.getMap();
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.getUiSettings().setRotateGesturesEnabled(false);
        aMap.getUiSettings().setRotateGesturesEnabled(false);
        //倾斜手势
        aMap.getUiSettings().setTiltGesturesEnabled(false);
        //隐藏logo
        aMap.getUiSettings().setLogoBottomMargin(-50);

        aMap.setOnMapTouchListener(this);

        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        // 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false
        aMap.setMyLocationEnabled(true);

        String locStr = XApp.getMyPreferences().getString(Config.SP_LAST_LOC, "");
        EmLoc emLoc = new Gson().fromJson(locStr, EmLoc.class);

        if (null != emLoc) {
            lastLatlng = new LatLng(emLoc.latitude, emLoc.longitude);
            //手动调用上次位置 减少从北京跳过来的时间
            receiveLoc(emLoc);
            //移动镜头，首次镜头快速跳到指定位置
            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLatlng, 17));

            myLocationStyle = new MyLocationStyle();
            // 设置圆形的边框颜色
            myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));
            // 设置圆形的填充颜色
            myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));

            myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(getResources(), R.mipmap.ic_flow_my_pos)));

            myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);

            aMap.setMyLocationStyle(myLocationStyle);
        }
    }

    @Override
    public void showTopView() {
        if (TextUtils.isEmpty(govOrder.remark)) {
            tv_remark.setText("无备注");
        } else {
            tv_remark.setText(govOrder.remark);
        }
        tv_order_number.setText(govOrder.orderNumber);

        drawer_frame.setOnClickListener(view -> {
            if (expandable_layout.isExpanded()) {
                expandable_layout.collapse();
            } else {
                expandable_layout.expand();
            }
        });

        if (govOrder.orderStatus == GWOrderStatus.TAKE_ORDER) {
            top_layout.setVisibility(View.GONE);
            lin_start_countdown.setVisibility(View.VISIBLE);

            countDownUtils = new CountDownUtils(this, (govOrder.bookTime + 60) * 1000, (day, hour, minute) -> {
                tv_day.setText("" + day);
                tv_hour.setText("" + hour);
                tv_fen.setText("" + minute);
            });

        } else if (govOrder.orderStatus == GWOrderStatus.GOTO_BOOKPALCE_ORDER) {
            lin_navi.setOnClickListener(view ->
                    presenter.navi(new LatLng(govOrder.getStartSite().lat, govOrder.getStartSite().lng), govOrder.getStartSite().poi, govOrder.orderId));
            go_text.setText("去");

            top_layout.setVisibility(View.VISIBLE);
            lin_time.setVisibility(View.GONE);
            lin_navi.setVisibility(View.VISIBLE);
            lin_start_countdown.setVisibility(View.GONE);

        } else if (govOrder.orderStatus == GWOrderStatus.ARRIVAL_BOOKPLACE_ORDER) {
            go_text.setText("已到");
            top_layout.setVisibility(View.VISIBLE);
            lin_time.setVisibility(View.VISIBLE);
            lin_navi.setVisibility(View.GONE);
            lin_start_countdown.setVisibility(View.GONE);
            //经过确认写死 10分钟
//            if ((ZCSetting.findOne().arriveCancel == 1)) {
            lin_time.setVisibility(View.VISIBLE);
            setWaitTime();
//            } else {
//                lin_time.setVisibility(View.GONE);
//            }
        } else if (govOrder.orderStatus == GWOrderStatus.GOTO_DESTINATION_ORDER) {
            if (XApp.getMyPreferences().getLong("" + govOrder.orderId, 0) != 0) {
                XApp.getEditor().remove("" + govOrder.orderId);
            }
            go_text.setText("去");
            top_layout.setVisibility(View.VISIBLE);
            lin_time.setVisibility(View.GONE);
            lin_navi.setVisibility(View.VISIBLE);
            lin_start_countdown.setVisibility(View.GONE);

            lin_navi.setOnClickListener(view -> {
                if (null != govOrder.getEndSite()) {
                    presenter.navi(new LatLng(govOrder.getEndSite().lat, govOrder.getEndSite().lng), govOrder.getEndSite().poi, govOrder.orderId);
                } else {
                    ToastUtil.showMessage(FlowActivity.this, getString(R.string.illegality_place));
                }
            });
        }

        if (govOrder.orderStatus == GWOrderStatus.GOTO_DESTINATION_ORDER) {
            next_place.setText(govOrder.getEndSite().addr);
        } else {
            next_place.setText(govOrder.getStartSite().addr);
        }
    }


    @Override
    public void initBridge() {
        bridge = new ActFraCommBridge() {

            @Override
            public void doToStart() {
                presenter.toStart(govOrder.orderId, govOrder.version);
            }

            @Override
            public void doArriveStart() {
                presenter.arriveStart(govOrder.orderId, govOrder.version);
                if (XApp.getMyPreferences().getLong("" + govOrder.orderId, 0) == 0) {
                    XApp.getEditor().putLong("" + govOrder.orderId, System.currentTimeMillis() + 10 * 60 * 1000).apply();
                }
            }

            @Override
            public void doStartDrive() {
                presenter.startDrive(govOrder.orderId, govOrder.version);
            }

            @Override
            public void doConfirm(ArrayList<String> image) {
                images.clear();
                images = image;
                getQiniuToken();
            }

            @Override
            public void doFinish() {
                finish();
            }

            @Override
            public void arriveDes(DymOrder dymOrder) {
                presenter.arriveDes(govOrder.orderId, govOrder.version, dymOrder);
            }

            @Override
            public void toFeeDetail() {

            }
        };
    }


    @Override
    public void showBottomFragment(GovOrder govOrder) {
        toolbar.setRightText("", null);
        if (govOrder.orderStatus == ZCOrderStatus.TAKE_ORDER) {
            toolbar.setTitle(R.string.gw_no_start);
            //产品确认 目前写死 后期迭代
            toolbar.setRightText(R.string.cancel_order, v -> {
                Intent intent = new Intent(this, CancelNewActivity.class);
                startActivityForResult(intent, CANCEL_ORDER);
            });

            BookFragment acceptFragment = new BookFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("govOrder", govOrder);
            acceptFragment.setArguments(bundle);
            acceptFragment.setBridge(bridge);

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
            transaction.replace(R.id.flow_frame, acceptFragment);
            transaction.commit();
        } else if (govOrder.orderStatus == ZCOrderStatus.GOTO_BOOKPALCE_ORDER) {
            toolbar.setTitle(R.string.gw_to_start);
//            if ((ZCSetting.findOne().goToCancel == 1)) {
            toolbar.setRightText(R.string.cancel_order, v -> {
                Intent intent = new Intent(this, CancelNewActivity.class);
                startActivityForResult(intent, CANCEL_ORDER);
            });
//            } else {
//                toolbar.setRightText("", null);
//            }

            ArriveStartFragment toStartFragment = new ArriveStartFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("govOrder", govOrder);
            toStartFragment.setArguments(bundle);
            toStartFragment.setBridge(bridge);

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
            transaction.replace(R.id.flow_frame, toStartFragment);
            transaction.commit();
        } else if (govOrder.orderStatus == ZCOrderStatus.ARRIVAL_BOOKPLACE_ORDER) {
            toolbar.setTitle(R.string.gw_up_car);

//            if ((ZCSetting.findOne().arriveCancel == 1)) {
            toolbar.setRightText(R.string.cancel_order, v -> {
                Intent intent = new Intent(this, CancelNewActivity.class);
                startActivityForResult(intent, CANCEL_ORDER);
            });
//            } else {
//                toolbar.setRightText("", null);
//            }
            WaitFragment fragment = new WaitFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("govOrder", govOrder);
            fragment.setArguments(bundle);
            fragment.setBridge(bridge);

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
            transaction.replace(R.id.flow_frame, fragment);
            transaction.commit();
        } else if (govOrder.orderStatus == ZCOrderStatus.GOTO_DESTINATION_ORDER) {
            toolbar.setTitle(R.string.gw_running);

            runningFragment = new RunningFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("govOrder", govOrder);
            runningFragment.setArguments(bundle);
            runningFragment.setBridge(bridge);

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
            transaction.replace(R.id.flow_frame, runningFragment);
            transaction.commit();
        } else if (govOrder.orderStatus == ZCOrderStatus.ARRIVAL_DESTINATION_ORDER) {

            toolbar.setTitle(R.string.gw_confirm_order);

            confirmFragment = new ConfirmFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("govOrder", govOrder);
            confirmFragment.setArguments(bundle);
            confirmFragment.setBridge(bridge);

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.flow_frame, confirmFragment);
            transaction.commit();
        }
    }

    @Override
    public void showOrder(GovOrder govOrder) {
        if (null == govOrder) {
            finish();
        } else {
            if (govOrder.orderStatus >= GWOrderStatus.FINISH_ORDER) {
                ToastUtil.showMessage(this, getResources().getString(R.string.order_finish));
                finish();
            }
            this.govOrder = govOrder;
            showTopView();
            initBridge();
            showBottomFragment(govOrder);
            showMapBounds();

        }
    }

    /**
     * 起点终点marker
     */
    private Marker startMarker;
    private Marker endMarker;

    @Override
    public void showMapBounds() {
        List<LatLng> latLngs = new ArrayList<>();
        latLngs.clear();
        if (govOrder.orderStatus == ZCOrderStatus.TAKE_ORDER) {
            if (null != govOrder.getStartSite()) {
                latLngs.add(new LatLng(govOrder.getStartSite().lat, govOrder.getStartSite().lng));
                presenter.routePlanByNavi(govOrder.getStartSite().lat, govOrder.getStartSite().lng);
            }
            latLngs.add(new LatLng(lastLatlng.latitude, lastLatlng.longitude));
            LatLngBounds bounds = MapUtil.getBounds(latLngs);
            aMap.moveCamera(CameraUpdateFactory.newLatLngBoundsRect(bounds,
                    DensityUtil.dp2px(this, 50),
                    DensityUtil.dp2px(this, 50),
                    DensityUtil.dp2px(this, 130),
                    DensityUtil.dp2px(this, 180)));
        } else if (govOrder.orderStatus == ZCOrderStatus.GOTO_BOOKPALCE_ORDER) {
            if (null != govOrder.getStartSite()) {
                latLngs.add(new LatLng(govOrder.getStartSite().lat, govOrder.getStartSite().lng));
                presenter.routePlanByNavi(govOrder.getStartSite().lat, govOrder.getStartSite().lng);
            } else {
                presenter.stopNavi();
                left_time.setText("");
            }
            latLngs.add(new LatLng(lastLatlng.latitude, lastLatlng.longitude));
            LatLngBounds bounds = MapUtil.getBounds(latLngs);
            aMap.moveCamera(CameraUpdateFactory.newLatLngBoundsRect(bounds,
                    DensityUtil.dp2px(this, 50),
                    DensityUtil.dp2px(this, 50),
                    DensityUtil.dp2px(this, 130),
                    DensityUtil.dp2px(this, 180)));
        } else if (govOrder.orderStatus == ZCOrderStatus.ARRIVAL_BOOKPLACE_ORDER
                || govOrder.orderStatus == ZCOrderStatus.GOTO_DESTINATION_ORDER
        ) {
            if (null != govOrder.getEndSite()) {
                latLngs.add(new LatLng(govOrder.getEndSite().lat, govOrder.getEndSite().lng));
                presenter.routePlanByNavi(govOrder.getEndSite().lat, govOrder.getEndSite().lng);
            }
            latLngs.add(new LatLng(lastLatlng.latitude, lastLatlng.longitude));
            LatLngBounds bounds = MapUtil.getBounds(latLngs);
            aMap.moveCamera(CameraUpdateFactory.newLatLngBoundsRect(bounds,
                    DensityUtil.dp2px(this, 50),
                    DensityUtil.dp2px(this, 50),
                    DensityUtil.dp2px(this, 130),
                    DensityUtil.dp2px(this, 180)));
        }
        if (govOrder.orderStatus == ZCOrderStatus.GOTO_BOOKPALCE_ORDER || govOrder.orderStatus == ZCOrderStatus.TAKE_ORDER) {
            if (endMarker != null) {
                endMarker.remove();
            }
            if (null == startMarker) {
                MarkerOptions markerOption = new MarkerOptions();
                markerOption.position(new LatLng(govOrder.getStartSite().lat, govOrder.getStartSite().lng));
                //设置Marker可拖动
                markerOption.draggable(false);
                markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(), R.mipmap.ic_start)));

                startMarker = aMap.addMarker(markerOption);
            } else {
                startMarker.setPosition(new LatLng(govOrder.getStartSite().lat, govOrder.getStartSite().lng));
            }
        }
        if (govOrder.orderStatus == ZCOrderStatus.ARRIVAL_BOOKPLACE_ORDER || govOrder.orderStatus == ZCOrderStatus.GOTO_DESTINATION_ORDER) {
            if (startMarker != null) {
                startMarker.remove();
            }
            if (null == endMarker) {
                MarkerOptions markerOption = new MarkerOptions();
                markerOption.position(new LatLng(govOrder.getEndSite().lat, govOrder.getEndSite().lng));
                //设置Marker可拖动
                markerOption.draggable(false);
                markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(), R.mipmap.ic_end)));
                endMarker = aMap.addMarker(markerOption);
            } else {
                endMarker.setPosition(new LatLng(govOrder.getEndSite().lat, govOrder.getEndSite().lng));
            }
        }
    }

    @Override
    public void cancelSuc() {
        finish();
    }

    RouteOverLay routeOverLay;

    @Override
    public void showPath(int[] ints, AMapNaviPath path) {
        RouteOverLay routeOverLay = new RouteOverLay(aMap, path, this);

        routeOverLay.setStartPointBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.yellow_dot_small));
        routeOverLay.setEndPointBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.blue_dot_small));
        routeOverLay.setTrafficLine(true);

        RouteOverlayOptions options = new RouteOverlayOptions();
        options.setSmoothTraffic(BitmapFactory.decodeResource(getResources(), com.easymi.component.R.mipmap.custtexture_green));
        options.setUnknownTraffic(BitmapFactory.decodeResource(getResources(), com.easymi.component.R.mipmap.custtexture_green));
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

    @Override
    public void showPath(DriveRouteResult result) {

    }

    @Override
    public void showLeft(int dis, int time) {
        String disKm;
        int km = dis / 1000;
        if (km >= 1) {
            disKm = new DecimalFormat("#0.0").format((double) dis / 1000) + getResources().getString(R.string.km);
        } else {
            disKm = dis + "米";
        }

        left_time.setText(getResources().getString(R.string.gw_destance)
                + disKm
                + "  " + (time / 60)
                + getResources().getString(R.string.minute_));
    }

    @Override
    public void showReCal() {
        if (govOrder != null) {
            if (govOrder.orderStatus == GWOrderStatus.GOTO_DESTINATION_ORDER) {
                if (null != govOrder.getEndSite()) {
                    presenter.routePlanByNavi(govOrder.getEndSite().lat, govOrder.getEndSite().lng);
                }
            } else if (govOrder.orderStatus == GWOrderStatus.GOTO_BOOKPALCE_ORDER) {
                if (null != govOrder.getStartSite()) {
                    presenter.routePlanByNavi(govOrder.getStartSite().lat, govOrder.getStartSite().lng);
                }
            }
        }
    }

    @Override
    public RxManager getManager() {
        return mRxManager;
    }

    @Override
    public void reRout() {
        if (govOrder != null) {
            if (govOrder.orderStatus == GWOrderStatus.GOTO_DESTINATION_ORDER) {
                if (null != govOrder.getEndSite()) {
                    presenter.routePlanByNavi(govOrder.getEndSite().lat, govOrder.getEndSite().lng);
                }
            } else if (govOrder.orderStatus == GWOrderStatus.GOTO_BOOKPALCE_ORDER) {
                if (null != govOrder.getStartSite()) {
                    presenter.routePlanByNavi(govOrder.getStartSite().lat, govOrder.getStartSite().lng);
                }
            }
        }
    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CANCEL_ORDER) {
                String reason = data.getStringExtra("reason");
                presenter.cancelOrder(govOrder.orderId, reason);
            } else if (requestCode == PictureConfig.CHOOSE_REQUEST) {
                if (confirmFragment != null) {
                    confirmFragment.onActivityResult(requestCode, resultCode, data);
                }
            }
        }
    }

////////////等待倒计时相关  开始
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

        if (null != timer) {
            timer.cancel();
            timer = null;
        }
        if (null != timerTask) {
            timerTask.cancel();
            timerTask = null;
        }

        long appoint = XApp.getMyPreferences().getLong("" + govOrder.orderId, 0);
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

//            if ((ZCSetting.findOne().arriveCancel == 1)) {
            if (govOrder.orderStatus == ZCOrderStatus.ARRIVAL_BOOKPLACE_ORDER) {
                if (timeSeq < 0) {
                    toolbar.setRightText(R.string.cancel_order, v -> {
                        Intent intent = new Intent(this, CancelNewActivity.class);
                        startActivityForResult(intent, CANCEL_ORDER);
                    });
                } else {
                    toolbar.setRightText("", null);
                }
            }
//            } else {
//                toolbar.setRightText("", null);
//            }
        });
    }
////////////等待倒计时相关  结束


    @Override
    public void onTouch(MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
            Log.e("mapTouch", "-----map onTouched-----");
            isMapTouched = true;
            XApp.getEditor().putLong(Config.DOWN_TIME, System.currentTimeMillis()).apply();
        }
    }

    @Override
    public void receiveLoc(EmLoc loc) {
        if (null == loc) {
            return;
        }
        Log.e("locPos", "bearing 2 >>>>" + loc.bearing);
        LatLng latLng = new LatLng(loc.latitude, loc.longitude);

        if (myLocationStyle == null) {
            myLocationStyle = new MyLocationStyle();
            // 设置圆形的边框颜色
            myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));
            // 设置圆形的填充颜色
            myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));
        }
        List<LatLng> latLngs = new ArrayList<>();
        if (!isMapTouched) {
            if (aMap != null) {
                aMap.setMyLocationStyle(myLocationStyle);
            }
            if (govOrder != null) {
                if (govOrder.orderStatus == ZCOrderStatus.TAKE_ORDER
                        || govOrder.orderStatus == ZCOrderStatus.GOTO_BOOKPALCE_ORDER) {
                    latLngs.add(new LatLng(govOrder.getStartSite().lat, govOrder.getStartSite().lng));
                } else if (govOrder.orderStatus == ZCOrderStatus.ARRIVAL_BOOKPLACE_ORDER
                        || govOrder.orderStatus == ZCOrderStatus.GOTO_DESTINATION_ORDER) {
                    latLngs.add(new LatLng(govOrder.getEndSite().lat, govOrder.getEndSite().lng));
                }

                latLngs.add(new LatLng(lastLatlng.latitude, lastLatlng.longitude));
                LatLngBounds bounds = MapUtil.getBounds(latLngs);
                aMap.moveCamera(CameraUpdateFactory.newLatLngBoundsRect(bounds,
                        DensityUtil.dp2px(this, 50),
                        DensityUtil.dp2px(this, 50),
                        DensityUtil.dp2px(this, 130),
                        DensityUtil.dp2px(this, 180)));
            }
        } else {
            if (aMap != null) {
                aMap.setMyLocationStyle(myLocationStyle);
            }
            if ((System.currentTimeMillis() - XApp.getMyPreferences().getLong(Config.DOWN_TIME, 0)) / 1000 > 5) {
                isMapTouched = false;
            }
        }
        lastLatlng = latLng;
    }

    @Override
    protected void onStart() {
        super.onStart();
        //添加位置订阅
        LocReceiver.getInstance().addObserver(this);
        //添加订单变化订阅
        HandlePush.getInstance().addObserver(this);

        cancelOrderReceiver = new CancelOrderReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Config.BROAD_CANCEL_ORDER);
        filter.addAction(Config.BROAD_BACK_ORDER);
        registerReceiver(cancelOrderReceiver, filter, EmUtil.getBroadCastPermission(), null);

    }

    @Override
    protected void onStop() {
        super.onStop();
        //取消位置订阅
        LocReceiver.getInstance().deleteObserver(this);
        //取消订单变化订阅
        HandlePush.getInstance().deleteObserver(this);

        unregisterReceiver(cancelOrderReceiver);
    }


    @Override
    public void feeChanged(long orderId, String orderType) {
        if (govOrder == null) {
            return;
        }
        if (orderId == govOrder.orderId && orderType.equals(Config.GOV)) {
            DymOrder dyo = DymOrder.findByIDType(orderId, orderType);
            if (null != runningFragment && runningFragment.isVisible()) {
                runningFragment.showFee(dyo);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        map_view.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        map_view.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        map_view.onPause();
    }

    @Override
    protected void onDestroy() {
        map_view.onDestroy();
        presenter.stopNavi();

        cancelTimer();

        if (countDownUtils != null) {
            countDownUtils.cancelTimer();
        }
        super.onDestroy();
    }

    /**
     * 签字单凭证
     */
    private String voucher;

    /**
     * 获取七牛云token
     */
    public void getQiniuToken() {
        Observable<QiNiuToken> observable = ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .getToken()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        observable.subscribe(new MySubscriber<>(this, false, false, qiNiuToken -> {
            if (qiNiuToken.getCode() == 1) {
                if (qiNiuToken.qiNiu == null) {
                    throw new IllegalArgumentException("token无效");
                } else {
                    qiniuToken = qiNiuToken.qiNiu;
                }
                updataIndex = 0;
                putPics();
            }
        }));
    }

    /**
     * 多张上传图片下标
     */
    int updataIndex = 0;

    /**
     * 上传图片到七牛云
     *
     * @param file
     * @param token
     * @return
     */
    public void putPic(File file, String token) {
        RequestBody photoRequestBody = RequestBody.create(MediaType.parse("image/jpg"), file);
        RequestBody tokenBody = RequestBody.create(MediaType.parse("multipart/form-data"), token);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), photoRequestBody);

        Observable<Pic> observable = ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .uploadPic(Config.HOST_UP_PIC, tokenBody, body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        observable.subscribe(new MySubscriber<>(FlowActivity.this, true, false, pic -> {
            updataIndex++;
            if (TextUtils.isEmpty(voucher)) {
                voucher = pic.hashCode;
            } else {
                voucher = voucher + "," + pic.hashCode;
            }
            putPics();
        }));
    }

    /**
     * 递归上传图片
     */
    public void putPics() {
        if (updataIndex < images.size()) {
            putPic(new File(images.get(updataIndex)), qiniuToken);
        } else {
            presenter.confirmOrder(govOrder.orderId, govOrder.version, voucher);
        }
    }

    /**
     * 取消订单弹窗
     */
    private AlertDialog cancelDialog;

    @Override
    public void onCancelOrder(long orderId, String orderType, String msg) {
        if (govOrder == null) {
            return;
        }
        if (orderId == govOrder.orderId
                && orderType.equals(govOrder.orderType)) {
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
}
