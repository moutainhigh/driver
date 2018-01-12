package com.easymi.common.mvp.grab;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
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
import com.easymi.component.Config;
import com.easymi.component.DJOrderStatus;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.widget.HLoadView;
import com.easymi.component.widget.RotateImageView;
import com.easymi.component.widget.overlay.DrivingRouteOverlay;

import net.cachapa.expandablelayout.ExpandableLayout;
import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.circlenavigator.CircleNavigator;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by developerLzh on 2017/11/2 0002.
 */

public class GrabActivity2 extends RxBaseActivity implements GrabContract.View {

    public static final int GRAB_TOTAL_TIME = 18;//加上预览订单的时间
    public static final int GRAB_VALID_TIME = 15;//可以抢单的时间

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

    private int pageIndex = 0; //page索引

    private List<Fragment> fragments;

    private Marker startMarker;//起点marker
    private List<Marker> passMarkers;//途经点marker
    private Marker endMarker;//终点marker

    @Override
    public int getLayoutId() {
        return R.layout.activity_grab;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        showIngOrder = (MultipleOrder) getIntent().getSerializableExtra("order");
        if (showIngOrder == null) {
            finish();
            return;
        }

        fragments = new ArrayList<>();

        presenter = new GrabPresenter(this, this);
        multipleOrders.add(showIngOrder);
        buildFragments(showIngOrder, true);//添加一个fragment

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
        MultipleOrder multipleOrder = (MultipleOrder) intent.getSerializableExtra("order");
        multipleOrders.add(multipleOrder);
        buildFragments(showIngOrder, false);//添加一个fragment
        adapter.setData(fragments);
        initIndicator();
    }

    private void initMap() {
        aMap = mapView.getMap();
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.getUiSettings().setRotateGesturesEnabled(false);
        aMap.getUiSettings().setRotateGesturesEnabled(false);
        aMap.getUiSettings().setTiltGesturesEnabled(false);//倾斜手势

        MarkerOptions options = new MarkerOptions();
        options.position(new LatLng(EmUtil.getLastLoc().latitude, EmUtil.getLastLoc().longitude));
        options.anchor(0.5f, 0.5f);
        options.rotateAngle(EmUtil.getLastLoc().bearing);
        options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(), R.mipmap.ic_flow_my_pos)));
        aMap.addMarker(options);
    }

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
                    for (Address address : showIngOrder.addresses) {
                        if (address.addrType == 1) {
                            LatLonPoint point = new LatLonPoint(address.lat, address.lng);
                            pass.add(point);
                            showStartMarker(point);
                        } else if (address.addrType == 2) {
                            LatLonPoint point = new LatLonPoint(address.lat, address.lng);
                            pass.add(point);
                        } else if (address.addrType == 3) {
                            end = new LatLonPoint(address.lat, address.lng);
                            hasEnd = true;
                            showEndMarker(end);
                        }
                    }
                    if (!hasEnd) {//没有终点时，起点就是路径规划的终点
                        end = pass.get(0);
                        presenter.routePlanByRouteSearch(end, null);
                    } else {
                        presenter.routePlanByRouteSearch(end, pass);
                    }
                    pass.remove(0);//这是起点的位置
                    showPassMarker(pass);
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
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void showBase(MultipleOrder multipleOrder) {

    }

    private Timer timer;
    private TimerTask timerTask;

    @Override
    public void showShade() {
        shadeFrame.setVisibility(View.VISIBLE);
        shadeFrame.setClickable(true);
        rotateImage.startRotate();
    }

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
                            multipleOrders.remove(showIngOrder);//移除当前订单
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
        timer.schedule(timerTask, 0, 1000);
    }

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
        grabCon.setOnClickListener(v -> presenter.grabOrder(showIngOrder.orderId));
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
        overlay.setNodeIconVisibility(false);//隐藏转弯的节点
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
        options.anchor(0.5f, 0.5f);
        options.rotateAngle(EmUtil.getLastLoc().bearing);
        options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(), R.mipmap.ic_start)));
        startMarker = aMap.addMarker(options);
    }

    @Override
    public void showPassMarker(List<LatLonPoint> pass) {

        if (null != pass && pass.size() != 0) {
            for (LatLonPoint latLonPoint : pass) {
                MarkerOptions options = new MarkerOptions();
                options.position(new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude()));
                options.anchor(0.5f, 0.5f);
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
        options.anchor(0.5f, 0.5f);
        options.rotateAngle(EmUtil.getLastLoc().bearing);
        options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(), R.mipmap.ic_end)));
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
    public RxManager getManager() {
        return mRxManager;
    }

    private void showBottomByStatus() {
        if (showIngOrder.orderStatus == DJOrderStatus.NEW_ORDER) {
            bottomText.setText(R.string.grab_order);
            grabCon.setOnClickListener(v -> presenter.grabOrder(showIngOrder.orderId));
        } else if (showIngOrder.orderStatus == DJOrderStatus.PAIDAN_ORDER) {
            bottomText.setText(R.string.accept_order);
            grabCon.setOnClickListener(v -> presenter.takeOrder(showIngOrder.orderId));
        }
    }

    private void buildFragments(MultipleOrder order, boolean showing) {
        try {
            if (order.orderType.equals(Config.DAIJIA)) {
                Class clazz = Class.forName("com.easymi.daijia.fragment.grab.DJGrabFragment");
                Fragment fragment = (Fragment) clazz.newInstance();
                Bundle bundle = new Bundle();
                bundle.putSerializable("order", order);
                fragment.setArguments(bundle);
                fragments.add(fragment);
                if (showing) {
                    showIngFragment = fragment;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        }

    }

    public void closeGrab(View view) {
        finishActivity();
    }

}
