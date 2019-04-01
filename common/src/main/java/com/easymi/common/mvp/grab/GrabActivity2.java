package com.easymi.common.mvp.grab;

import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.DriveRouteResult;
import com.easymi.common.R;
import com.easymi.common.adapter.GrabFragmentAdapter;
import com.easymi.common.entity.Address;
import com.easymi.common.entity.MultipleOrder;
import com.easymi.common.push.HandlePush;
import com.easymi.component.Config;
import com.easymi.component.DJOrderStatus;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.utils.CsSharedPreferences;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.PhoneUtil;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.widget.HLoadView;
import com.easymi.component.widget.RotateImageView;
import com.easymi.component.widget.overlay.DrivingRouteOverlay;

import net.cachapa.expandablelayout.ExpandableLayout;
import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.circlenavigator.CircleNavigator;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 * @Author: shine
 * Date: 2018/12/24 下午5:00
 * Description:
 * History:
 */

public class GrabActivity2 extends RxBaseActivity implements GrabContract.View {

    /**
     * 加上预览订单的时间
     */
    public static final int GRAB_TOTAL_TIME = 18;
    /**
     * 可以抢单的时间
     */
    public static final int GRAB_VALID_TIME = 15;

    LinearLayout expandBtnCon;
    MapView mapView;
    ExpandableLayout expandableLayout;
    ViewPager viewPager;

    FrameLayout shadeFrame;
    RotateImageView rotateImage;
    TextView shadeCountdown;

    HLoadView loadView;
    RelativeLayout grabCon;
    TextView grabCountdown;

    GrabFragmentAdapter adapter;
    GrabPresenter presenter;

    MagicIndicator magicIndicator;

    TextView bottomText;

    ImageView topArrow;
    ImageView bottomArrow;

    private MultipleOrder showIngOrder = null;
    private Fragment showIngFragment = null;

    private AMap aMap;

    List<MultipleOrder> multipleOrders = new ArrayList<>();

    /**
     * page索引
     */
    private int pageIndex = 0;

    private List<Fragment> fragments;

    /**
     * 起点marker
     */
    private Marker startMarker;
    /**
     * 途经点marker
     */
    private List<Marker> passMarkers;
    /**
     * 终点marker
     */
    private Marker endMarker;

    @Override
    public void onBackPressed() {
        finishActivity();
    }

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_grab;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        showIngOrder = (MultipleOrder) getIntent().getSerializableExtra("order");
        if (showIngOrder == null) {
            finish();
            return;
        }

        shake();

        fragments = new ArrayList<>();

        presenter = new GrabPresenter(this, this);
        multipleOrders.add(showIngOrder);
        //添加一个fragment
        buildFragments(showIngOrder, true);

        expandBtnCon = findViewById(R.id.expand_btn_con);
        mapView = findViewById(R.id.map_view);
        expandableLayout = findViewById(R.id.expand_layout);

        viewPager = findViewById(R.id.view_pager);

        shadeFrame = findViewById(R.id.shade_frame);
        rotateImage = findViewById(R.id.time_ring);
        shadeCountdown = findViewById(R.id.count_down_start);

        loadView = findViewById(R.id.load_view);
        grabCon = findViewById(R.id.grab_con);
        grabCountdown = findViewById(R.id.count_down_grab);

        topArrow = findViewById(R.id.top_arrow);
        bottomArrow = findViewById(R.id.bottom_arrow);

        bottomText = findViewById(R.id.button_text);

        magicIndicator = findViewById(R.id.magic_indicator);

        mapView.onCreate(savedInstanceState);

        initMap();

        initExpand();

        initViewPager();

    }

    /**
     * 加载计数指示器
     */
    private void initIndicator() {
        CircleNavigator circleNavigator = new CircleNavigator(this);
        circleNavigator.setCircleCount(multipleOrders.size());
        circleNavigator.setCircleColor(Color.WHITE);
        magicIndicator.setNavigator(circleNavigator);
        magicIndicator.onPageSelected(pageIndex);
        ViewPagerHelper.bind(magicIndicator, viewPager);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        MultipleOrder newOrder = (MultipleOrder) intent.getSerializableExtra("order");

        boolean haveSame = false;
        for (MultipleOrder order : multipleOrders) {
            if (newOrder.orderId == order.orderId
                    && (newOrder.status == DJOrderStatus.NEW_ORDER || newOrder.status == DJOrderStatus.PAIDAN_ORDER)
                    ) {
                //重置时间
                order.countTime = GRAB_TOTAL_TIME;
                haveSame = true;
            }
        }
        if (haveSame) { //推送任务中有相同订单时就不再新增进去
//            adapter.notifyDataSetChanged();
            return;
        }

        shake();
//        String voiceStr = getIntent().getStringExtra("voiceStr");
//        XApp.getInstance().syntheticVoice(voiceStr);
//        XApp.getInstance().shake();

        multipleOrders.add(newOrder);
        //添加一个fragment
        buildFragments(newOrder, false);
        adapter.setData(fragments);
        initIndicator();
    }

    /**
     * 初始化地图
     */
    private void initMap() {
        aMap = mapView.getMap();
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.getUiSettings().setRotateGesturesEnabled(false);
        aMap.getUiSettings().setRotateGesturesEnabled(false);
        //倾斜手势
        aMap.getUiSettings().setTiltGesturesEnabled(false);

        MarkerOptions options = new MarkerOptions();
        options.position(new LatLng(EmUtil.getLastLoc().latitude, EmUtil.getLastLoc().longitude));
        options.anchor(0.5f, 0.5f);
        options.rotateAngle(EmUtil.getLastLoc().bearing);
        options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(), R.mipmap.ic_flow_my_pos)));
        aMap.addMarker(options);
    }

    /**
     * 初始化收缩框
     */
    private void initExpand() {
        expandBtnCon.setOnClickListener(v -> {
            if (expandableLayout.isExpanded()) {
                expandableLayout.collapse();
                topArrow.setVisibility(View.VISIBLE);
                bottomArrow.setVisibility(View.GONE);
            } else {
                expandableLayout.expand();
                if (null != showIngOrder) {
                    topArrow.setVisibility(View.GONE);
                    bottomArrow.setVisibility(View.VISIBLE);
                    List<LatLonPoint> pass = new ArrayList<>();
                    LatLonPoint end = null;
                    boolean hasEnd = false;
                    removeAllOrderMarker();//移除订单的位置信息marker

                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(new LatLng(EmUtil.getLastLoc().latitude, EmUtil.getLastLoc().longitude));

                    for (Address address : showIngOrder.orderAddressVos) {
                        if (address.type == 1) {
                            LatLonPoint point = new LatLonPoint(address.latitude, address.longitude);
                            pass.add(point);
                            showStartMarker(point);
                        } else if (address.type == 2) {
                            LatLonPoint point = new LatLonPoint(address.latitude, address.longitude);
                            pass.add(point);
                        } else if (address.type == 3) {
                            end = new LatLonPoint(address.latitude, address.longitude);
                            hasEnd = true;
                            showEndMarker(end);
                        }
                        builder.include(new LatLng(address.latitude, address.longitude));
                    }
                    if (!hasEnd) {
                        //没有终点时，起点就是路径规划的终点
                        end = pass.get(0);
                        presenter.routePlanByRouteSearch(end, null);
                    } else {
                        presenter.routePlanByRouteSearch(end, pass);
                    }
                    pass.remove(0);
                    //这是起点的位置
                    showPassMarker(pass);

                    aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 50));
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mapView != null) {
            mapView.onDestroy();
        }
        XApp.getInstance().stopVoice();
        NotificationManager mNotificationManager = (NotificationManager) XApp
                .getInstance().getSystemService(NOTIFICATION_SERVICE);
        if (mNotificationManager != null) {
            mNotificationManager.cancel(HandlePush.NOTIFY_ID);
        }
        super.onDestroy();
    }

    @Override
    public void showBase(MultipleOrder multipleOrder) {

    }

    /**
     * 计时器
     */
    private Timer timer;
    private TimerTask timerTask;

    @Override
    public void showShade() {
        shadeFrame.setVisibility(View.VISIBLE);
        rotateImage.startRotate();
    }

    /**
     * 抢单接单倒计时
     */
    private void countTime() {
        cancelTimer();
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                showIngOrder.countTime--;
                runOnUiThread(() -> {
                    if (showIngOrder.countTime > GRAB_VALID_TIME) {
                        showShade();
                        shadeCountdown.setText(String.valueOf(showIngOrder.countTime - GRAB_VALID_TIME));
                    } else {
                        if (showIngOrder.countTime <= 0) {
                            cancelTimer();
                            //移除当前订单
                            multipleOrders.remove(showIngOrder);
                            fragments.remove(showIngFragment);
                            if (multipleOrders.size() == 0) {
                                finish();
                            } else {
                                adapter.setData(fragments);
                                initIndicator();
                                if (pageIndex > multipleOrders.size() - 1) {
                                    pageIndex = multipleOrders.size() - 1;
                                }

                                viewPager.setCurrentItem(pageIndex, true);

                                //setCurrentItem不触发onPageSelected,手动调用里面的内容
                                showIngOrder = multipleOrders.get(pageIndex);
                                showIngFragment = fragments.get(pageIndex);
                                showBottomByStatus();
                                countTime();
                            }

                        } else {
                            showGrabCountDown();
                            grabCountdown.setText(String.valueOf(showIngOrder.countTime));
                        }
                    }
                });
            }
        };
        timer.schedule(timerTask, 1000, 1000);
    }

    /**
     * 取消计时器
     */
    private void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }

    @Override
    public void showGrabCountDown() {
        rotateImage.pauseRotate();
        shadeFrame.setVisibility(View.GONE);
    }

    @Override
    public void initViewPager() {
        showBottomByStatus();

        viewPager.setOffscreenPageLimit(Integer.MAX_VALUE);
        adapter = new GrabFragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pageIndex = position;
                showIngOrder = multipleOrders.get(position);
                showIngFragment = fragments.get(position);
                showBottomByStatus();
                countTime();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        adapter.setData(fragments);
        initIndicator();
        countTime();
    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    public void showPath(DriveRouteResult result) {
        DrivingRouteOverlay overlay = new DrivingRouteOverlay(this, aMap,
                result.getPaths().get(0), result.getStartPos()
                , result.getTargetPos(), null);

        overlay.setRouteWidth(50);
        overlay.setIsColorfulline(false);
        //隐藏转弯的节点
        overlay.setNodeIconVisibility(false);
        overlay.addToMap();
        overlay.zoomToSpan();
        aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(
                new LatLng(result.getStartPos().getLatitude(), result.getStartPos().getLongitude()),
                new LatLng(result.getTargetPos().getLatitude(), result.getTargetPos().getLongitude())), 50));
    }

    @Override
    public void showStartMarker(LatLonPoint start) {

        MarkerOptions options = new MarkerOptions();
        options.position(new LatLng(start.getLatitude(), start.getLongitude()));
        options.anchor(0.5f, 1f);
        options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(), R.mipmap.ic_start)));
        //设置marker平贴地图效果
        options.setFlat(true);
        startMarker = aMap.addMarker(options);
    }

    @Override
    public void showPassMarker(List<LatLonPoint> pass) {

        if (null != pass && pass.size() != 0) {
            for (LatLonPoint latLonPoint : pass) {
                MarkerOptions options = new MarkerOptions();
                options.position(new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude()));
                options.anchor(0.5f, 1f);
                options.rotateAngle(EmUtil.getLastLoc().bearing);
                options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(), R.mipmap.ic_pass)));
                Marker marker = aMap.addMarker(options);
                if (null == passMarkers) {
                    passMarkers = new ArrayList<>();
                    passMarkers.add(marker);
                }
            }
        }
    }

    @Override
    public void showEndMarker(LatLonPoint end) {

        MarkerOptions options = new MarkerOptions();
        options.position(new LatLng(end.getLatitude(), end.getLongitude()));
        options.anchor(0.5f, 1f);
        options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(), R.mipmap.ic_end)));
        //设置marker平贴地图效果
        options.setFlat(true);
        endMarker = aMap.addMarker(options);
    }

    @Override
    public void removeAllOrderMarker() {
        if (startMarker != null) {
            startMarker.remove();
        }
        if (endMarker != null) {
            endMarker.remove();
        }
        if (null != passMarkers && passMarkers.size() != 0) {
            for (Marker passMarker : passMarkers) {
                passMarker.remove();
            }
        }
    }

    @Override
    public void removerOrderById(long orderId) {
        if (showIngOrder != null && showIngOrder.orderId == orderId) {
            //采用将时间置位1的方式移除订单
            showIngOrder.countTime = 1;
        }
    }

    @Override
    public RxManager getManager() {
        return mRxManager;
    }

    /**
     * 根据订单状态判断是接单还是抢单
     */
    private void showBottomByStatus() {
        if (showIngOrder.status == DJOrderStatus.NEW_ORDER) {
            bottomText.setText(R.string.grab_order);
            grabCon.setOnClickListener(v -> presenter.grabOrder(showIngOrder));
        } else if (showIngOrder.status == DJOrderStatus.PAIDAN_ORDER) {
            bottomText.setText(R.string.accept_order);
            grabCon.setOnClickListener(v -> presenter.takeOrder(showIngOrder));
        }
    }

    /**
     * 初始化fragment
     * @param order
     * @param showing
     */
    private void buildFragments(MultipleOrder order, boolean showing) {
        try {
            Class clazz;
            switch (order.serviceType) {
                case Config.DAIJIA:
                    //乘以1000
                    clazz = Class.forName("com.easymi.daijia.fragment.grab.DJGrabFragment");
                    break;
                case Config.ZHUANCHE:
                    clazz = Class.forName("com.easymi.zhuanche.fragment.grab.ZCGrabFragment");
                    break;
                case Config.TAXI:
                    clazz = Class.forName("com.easymi.taxi.fragment.grab.TaxiGrabFragment");
                    break;
                default:
                    return;
            }
            order.bookTime = order.bookTime * 1000;
            Fragment fragment = (Fragment) clazz.newInstance();
            Bundle bundle = new Bundle();
            bundle.putSerializable("order", order);
            fragment.setArguments(bundle);
            fragments.add(fragment);
            if (showing) {
                showIngFragment = fragment;
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭抢单接单页面
     * @param view
     */
    public void closeGrab(View view) {
        finishActivity();
    }

    /**
     * 判断是否震动手机
     */
    private void shake() {
        boolean shakeAble = new CsSharedPreferences().getBoolean(Config.SP_SHAKE_ABLE, true);
        if (shakeAble) {
            //震动
            PhoneUtil.vibrate(XApp.getInstance(), false);
        }
    }

}
