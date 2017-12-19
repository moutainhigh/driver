package com.easymi.common.mvp.grab;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.DriveRouteResult;
import com.easymi.common.R;
import com.easymi.common.adapter.GrabAdapter;
import com.easymi.common.entity.Address;
import com.easymi.common.entity.MultipleOrder;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.widget.HLoadView;
import com.easymi.component.widget.RotateImageView;
import com.easymi.component.widget.overlay.DrivingRouteOverlay;
import com.itsronald.widget.ViewPagerIndicator;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by developerLzh on 2017/11/2 0002.
 */

public class GrabActivity extends RxBaseActivity implements GrabContract.View {

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

    ViewPagerIndicator pagerIndicator;

    GrabAdapter adapter;
    GrabPresenter presenter;

    TextView bottomText;

    private MultipleOrder showIngOrder = null;

    private AMap aMap;

    List<MultipleOrder> multipleOrders = new ArrayList<>();

    private int pageIndex = 0; //page索引

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
        presenter = new GrabPresenter(this, this);
        multipleOrders.add(showIngOrder);

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

        bottomText = findViewById(R.id.button_text);

        pagerIndicator = findViewById(R.id.view_pager_indicator);

        mapView.onCreate(savedInstanceState);

        initMap();

        initExpand();

        initViewPager();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        MultipleOrder multipleOrder = (MultipleOrder) intent.getSerializableExtra("order");
        multipleOrders.add(multipleOrder);
        adapter.setDJOrderList(multipleOrders);
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
            } else {
                expandableLayout.expand();
                if (null != showIngOrder) {
                    List<LatLonPoint> pass = new ArrayList<>();
                    LatLonPoint end = null;
                    for (Address address : showIngOrder.addresses) {
                        if (address.addrType == 1 || address.addrType == 2) {
                            LatLonPoint point = new LatLonPoint(address.lat, address.lng);
                            pass.add(point);
                        } else if (address.addrType == 3) {
                            end = new LatLonPoint(address.lat, address.lng);
                        }
                    }
                    presenter.routePlanByRouteSearch(end, pass);
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
                        showGrabCountDown();
                        grabCountdown.setText(String.valueOf(showIngOrder.countTime));
                        if (showIngOrder.countTime == 0) {
                            cancelTimer();

                            if (pageIndex >= 1) { //在移除时，如果pageIndex大于1就减去1
                                pageIndex -= 1;
                            } else { //为0时+1
                                pageIndex += 1;
                            }
                            if (pageIndex >= multipleOrders.size()) {
                                finish();
                            } else {
                                viewPager.setCurrentItem(pageIndex);
                                multipleOrders.remove(showIngOrder);
                                adapter.setDJOrderList(multipleOrders);
                                pageIndex = viewPager.getCurrentItem();
                            }
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
        adapter = new GrabAdapter(this);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                showIngOrder = multipleOrders.get(position);
                showBottomByStatus();
                countTime();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        adapter.setDJOrderList(multipleOrders);
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
    public RxManager getManager() {
        return mRxManager;
    }

    private void showBottomByStatus(){
        if(showIngOrder.orderStatus == MultipleOrder.NEW_ORDER){
            bottomText.setText(R.string.grab_order);
            grabCon.setOnClickListener(v -> presenter.grabOrder(showIngOrder.orderId));
        } else if(showIngOrder.orderStatus == MultipleOrder.PAIDAN_ORDER){
            bottomText.setText(R.string.send_order);
            grabCon.setOnClickListener(v -> presenter.takeOrder(showIngOrder.orderId));
        }
    }

    public void closeGrab(View view){
        finishActivity();
    }
}
