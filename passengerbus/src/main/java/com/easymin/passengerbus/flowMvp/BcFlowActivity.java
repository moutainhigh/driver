package com.easymin.passengerbus.flowMvp;


import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.NinePatch;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
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
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.utils.SpatialRelationUtil;
import com.amap.api.maps.utils.overlay.SmoothMoveMarker;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.easymi.common.entity.OrderCustomer;
import com.easymi.common.push.CountEvent;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.EmLoc;
import com.easymi.component.loc.LocObserver;
import com.easymi.component.loc.LocReceiver;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.utils.DensityUtil;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.Log;
import com.easymi.component.utils.MapUtil;
import com.easymi.component.widget.CusToolbar;
import com.easymin.passengerbus.R;
import com.easymin.passengerbus.entity.BusStationResult;
import com.easymin.passengerbus.entity.ChangeTitleEvent;
import com.easymin.passengerbus.fragment.BcRuningFragment;
import com.easymin.passengerbus.fragment.BcStartFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
@Route(path = "/passengerbus/BcFlowActivity")
public class BcFlowActivity extends RxBaseActivity implements AMap.OnMapTouchListener, FlowContract.View, LocObserver, AMap.OnMarkerClickListener, RouteSearch.OnRouteSearchListener {

    private MapView mapView;

    private AMap aMap;

    private FrameLayout fragmentFrame;

    private TextView tvTipLayout;

    private CusToolbar toolbar;

    private ActFraCommBridge bridge;

    private FlowPresenter presenter;

    /**
     * 地图相关
     */

    private BusStationResult busRouteResult;
    private MarkerOptions markerOption;

    /**
     * 路径规划相关
     */

    private RouteSearch mRouteSearch;
    private DriveRouteResult mDriveRouteResult;

    //规划的是驾车路径
    private final int ROUTE_TYPE_DRIVE = 2;

    /**
     * 行程状态
     */
    public final int STARTRUNNING = 1;
    public final int RUNNING = 2;
    public final int ENDRUNING = 3;

    LatLng mylocation;

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_bus_flow;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        toolbar = findViewById(R.id.cus_toolbar);
        fragmentFrame = findViewById(R.id.fragment_frame);
        tvTipLayout = findViewById(R.id.tv_tip_layout);

        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        toolbar.setTitle("线路站点");
        initMap();

        initPresnter();

    }

    private void initPresnter() {
        presenter = new FlowPresenter(this, this);
        presenter.findBusOrderById(11);
    }


    @Override
    public void initFragment() {

    }


    @Override
    public void initMap() {
        aMap = mapView.getMap();
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.getUiSettings().setRotateGesturesEnabled(false);
        aMap.getUiSettings().setRotateGesturesEnabled(false);
        aMap.getUiSettings().setTiltGesturesEnabled(false);//倾斜手势
        aMap.getUiSettings().setLogoBottomMargin(-50);//隐藏logo
        aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器

        aMap.setOnMapTouchListener(this);

        mRouteSearch = new RouteSearch(this);
        mRouteSearch.setRouteSearchListener(this);
    }

    @Override
    public void initBridget() {
        bridge = new ActFraCommBridge() {

            @Override
            public void toFinished() {

            }

            @Override
            public void toOrderList() {

            }

            @Override
            public void changeToolbar(int flag) {

            }

            @Override
            public void clearMap() {

            }

            @Override
            public void routePath(LatLng toLatlng) {

            }

            @Override
            public void routePath(LatLng startLatlng, List<LatLng> passLatlngs, LatLng endLatlng) {

            }

            @Override
            public void arriveStart(OrderCustomer orderCustomer) {

            }

            @Override
            public void acceptCustomer(OrderCustomer orderCustomer) {

            }

            @Override
            public void jumpAccept(OrderCustomer orderCustomer) {

            }

            @Override
            public void arriveEnd(OrderCustomer orderCustomer) {

            }

            @Override
            public void jumpSend(OrderCustomer orderCustomer) {

            }

            @Override
            public void doRefresh() {

            }

            @Override
            public void countStartOver() {

            }

            @Override
            public void slideToNext() {

                //调用滑动到达下一站接口

//                changeToolbar(int flag)
            }

        };
    }

    @Override
    public void showFragmentByStatus() {

    }

    @Override
    public void changeToolbar(int flag) {
        if (flag == STARTRUNNING) {
            toolbar.setTitle("线路站点");
        } else if (flag == RUNNING) {
            toolbar.setTitle("行程中");
        } else if (flag == ENDRUNING) {
            toolbar.setTitle("到达站点");
        }
        toolbar.setBackgroundColor(Color.parseColor("#ffffff"));
    }


    @Override
    public void showBusLineInfo(BusStationResult busStationResult) {
        busRouteResult = busStationResult;
//        aMap.clear();// 清理地图上的所有覆盖物
        //显示正在行驶的路线

        tvTipLayout.setText("行程：" + busStationResult.startStation + "到" + busStationResult.endStation);
        showBottonFragment(busRouteResult);

        //设置bound
        List<LatLng> latLngs = new ArrayList<>();
        latLngs.add(new LatLng(busStationResult.stations.get(0).latitude, busStationResult.stations.get(0).longitude));
        latLngs.add(new LatLng((busStationResult.stations.get(busStationResult.stations.size()-1).latitude),
                (busStationResult.stations.get(busStationResult.stations.size()-1).longitude)));

        LatLngBounds bounds = MapUtil.getBounds(latLngs);
        aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, (int) (DensityUtil.getDisplayWidth(BcFlowActivity.this) / 1.5),
                (int) (DensityUtil.getDisplayWidth(BcFlowActivity.this) / 1.5), 0));


        //添加站点marker
        List<LatLng> stations = new ArrayList<LatLng>();

        for (int i= 0; i <= busRouteResult.stations.size(); i ++) {
            stations.add(new LatLng(busStationResult.stations.get(i).latitude, busStationResult.stations.get(i).longitude));


            markerOption = new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bus_dot))
                    .title(busRouteResult.stations.get(i).address )
                    .position(new LatLng(busStationResult.stations.get(i).latitude, busStationResult.stations.get(i).longitude))
                    .setInfoWindowOffset(0,-5)
                    .draggable(true);


            //绘制路线
            aMap.addPolyline(new PolylineOptions().
                    addAll(stations)
                    .width(15)
                    .color(Color.parseColor("#3C98E3")));
            aMap.addMarker(markerOption);
        }

    }

    public void showBottonFragment(BusStationResult result) {
        BcStartFragment bcStartFragment = new BcStartFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("busLineResult", result);
        bcStartFragment.setArguments(bundle);
        bcStartFragment.setBridge(bridge);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
        transaction.replace(R.id.fragment_frame, bcStartFragment);
        transaction.commit();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changeTitle(ChangeTitleEvent event) {
        if (event.getChange() == true) {
            toolbar.setTitle("行程中");
            showRuningLayout();
        }
    }

    public void showRuningLayout() {
        BcRuningFragment bcRuningFragment = new BcRuningFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("busLineResult", busRouteResult);
        bcRuningFragment.setArguments(bundle);
        bcRuningFragment.setBridge(bridge);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
        transaction.replace(R.id.fragment_frame, bcRuningFragment);
        transaction.commit();
    }

    @Override
    public RxManager getManager() {
        return mRxManager;
    }

    @Override
    public void onTouch(MotionEvent motionEvent) {

    }


    @Override
    protected void onStart() {
        super.onStart();
        LocReceiver.getInstance().addObserver(this);//添加位置订阅
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        mRxManager.clear();
        mapView.onDestroy();
        super.onStop();
        LocReceiver.getInstance().deleteObserver(this);//取消位置订阅

    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (aMap != null) {
            // 清理地图上的所有覆盖物
            aMap.clear();
        }
    }



    @Override
    public void receiveLoc(EmLoc location) {
        if (null == location) {
            return;
        }
        Log.e("locPos", "bearing 2 >>>>" + location.bearing);

        mylocation = new LatLng(location.latitude, location.longitude);
//        markerOption = new MarkerOptions()
//                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.amap_bus))
//                .position(mylocation).title("小车")
//                .draggable(true)
//                .period(10);
//        aMap.addMarker(markerOption);
//        aMap.addMarker(new MarkerOptions());


        moveToShowStationName(mylocation);
    }

    /**
     * 显示移动后 到达站点的名字
     */
    private void moveToShowStationName(LatLng mylocation) {
        for (int i = 0; i <= busRouteResult.stations.size()-1; i ++) {
            LatLng stationLatLng = new LatLng(busRouteResult.stations.get(i).latitude, busRouteResult.stations.get(i).longitude);
            if (mylocation == stationLatLng) {
                markerOption = new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.amap_bus))
                        .position(mylocation).title(busRouteResult.stations.get(i).name)
                        .draggable(true)
                        .period(10);
                aMap.addMarker(markerOption);
                aMap.addMarker(new MarkerOptions());

            }

        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        return false;
    }


    /**
     * 开始搜索路径规划方案
     */
    private void searchRouteResult(int route_type_drive, int drivingDefault) {
//        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
//                mStartPoint, mEndPoint);
//        if (routeType == ROUTE_TYPE_DRIVE) {// 驾车路径规划
//            RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, mode, null,
//                    null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
//            mRouteSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
//        }
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {

    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }
}
