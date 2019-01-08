package com.easymin.passengerbus.flowMvp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
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
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.DriveStep;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.EmLoc;
import com.easymi.component.loc.LocObserver;
import com.easymi.component.loc.LocReceiver;
import com.easymi.component.network.GsonUtil;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.utils.DensityUtil;
import com.easymi.component.utils.Log;
import com.easymi.component.utils.MapUtil;
import com.easymi.component.widget.CusToolbar;
import com.easymin.passengerbus.R;
import com.easymin.passengerbus.entity.BusStationResult;
import com.easymin.passengerbus.entity.BusStationsBean;
import com.easymin.passengerbus.fragment.BcEndFragment;
import com.easymin.passengerbus.fragment.BcRuningFragment;
import com.easymin.passengerbus.fragment.BcStartFragment;

import java.util.ArrayList;
import java.util.List;

@Route(path = "/passengerbus/BcFlowActivity")
public class BcFlowActivity extends RxBaseActivity implements AMap.OnMapTouchListener, FlowContract.View, LocObserver, AMap.OnMarkerClickListener {

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
    //路径相关
    private List<BusStationsBean> listLine = new ArrayList<>();
    private MarkerOptions markerOption;


    /**
     * 行程状态
     */
    public static final int STARTRUNNING = 1;
    public static final int RUNNING = 2;
    public static final int ENDRUNING = 3;

    LatLng mylocation;


    Fragment currentFragment;

    BcStartFragment bcStartFragment;

    BcRuningFragment bcRuningFragment;

    BcEndFragment bcEndFragment;

    private Long scheduleId;

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
        toolbar = findViewById(R.id.cus_toolbar);
        fragmentFrame = findViewById(R.id.fragment_frame);
        tvTipLayout = findViewById(R.id.tv_tip_layout);

        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        toolbar.setTitle("线路站点");
        scheduleId = getIntent().getLongExtra("scheduleId", 0);

        initMap();
        initBridget();
        initFragment();

        initPresnter();
    }

    @Override
    public void initToolBar() {
        super.initToolBar();
        toolbar.setLeftIcon(R.drawable.ic_arrow_back, v -> finish());
    }

    public void initFragment() {
        bcStartFragment = new BcStartFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("scheduleId", scheduleId);
        bcStartFragment.setArguments(bundle);
        bcStartFragment.setBridge(bridge);

        bcRuningFragment = new BcRuningFragment();
        Bundle bundle2 = new Bundle();
        bundle2.putLong("scheduleId", scheduleId);
        bcRuningFragment.setArguments(bundle2);
        bcRuningFragment.setBridge(bridge);

        bcEndFragment = new BcEndFragment();
        Bundle bundle3 = new Bundle();
        bundle3.putLong("scheduleId", scheduleId);
        bcEndFragment.setArguments(bundle3);
        bcEndFragment.setBridge(bridge);
    }

    private void initPresnter() {
        presenter = new FlowPresenter(this, this);
        if (scheduleId != null && scheduleId != 0) {
            presenter.findBusOrderById(scheduleId);
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
        aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器

        aMap.setOnMapTouchListener(this);

    }

    @Override
    public void initBridget() {
        bridge = new ActFraCommBridge() {
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
            public void arriveStart() {
                //调用出发接口
                presenter.startStation(scheduleId);
            }

            @Override
            public void arriveEnd() {
                //到达
                presenter.endStation(scheduleId);
            }

            @Override
            public void slideToNext(int index) {
                //前往下一站
                presenter.toNextStation(scheduleId, listLine.get(index).id);

            }

            @Override
            public void sideToArrived(int index) {
                //滑动到达下一站
                presenter.arriveStation(scheduleId, listLine.get(index).id);
                initPop(index);
            }

            @Override
            public void showEndFragment() {
                switchFragment(bcEndFragment).commit();
            }
        };
    }

    public void initPop(int index){
        if (listMarker != null){
            listMarker.get(index).setTitle(listLine.get(index).address);
            listMarker.get(index).setSnippet("");
            listMarker.get(index).showInfoWindow();
        }
    }

    @Override
    public void showBusLineInfo(BusStationResult busStationResult) {
        listLine = BusStationsBean.findByScheduleId(busStationResult.id);

        Log.e("hufeng", GsonUtil.toJson(listLine));

//        aMap.clear();// 清理地图上的所有覆盖物

        //显示正在行驶的路线
        tvTipLayout.setText("行程：" + busStationResult.stationVos.get(0).address
                + "到" + busStationResult.stationVos.get(listLine.size() - 1).address);

        showLines();
        //进入班车跑单界面后根据本地数据展示对应的fragment
        showFragmentByStatus();
    }

    public void showFragmentByStatus() {
        for (int i = 0; i < listLine.size(); i++) {
            if (listLine.get(0).status != BusStationsBean.LEAVE_STATION) {
                switchFragment(bcStartFragment).commit();
                break;
            } else if (listLine.get(listLine.size() - 1).status != BusStationsBean.TO_STATION) {
                switchFragment(bcRuningFragment).commit();
                break;
            } else {
                switchFragment(bcEndFragment).commit();
                break;
            }
        }
    }

    ArrayList<Marker> listMarker = new ArrayList<>();

    public void showLines() {
        listMarker.clear();
        //第一个未开始加载的地图
        //设置bound
        List<LatLng> latLngs = new ArrayList<>();
        latLngs.add(new LatLng(listLine.get(0).latitude, listLine.get(0).longitude));
        latLngs.add(new LatLng((listLine.get(listLine.size() - 1).latitude),
                (listLine.get(listLine.size() - 1).longitude)));

        LatLngBounds bounds = MapUtil.getBounds(latLngs);
        aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, (int) (DensityUtil.getDisplayWidth(BcFlowActivity.this) / 1.5),
                (int) (DensityUtil.getDisplayWidth(BcFlowActivity.this) / 1.5), 0));

        //添加站点marker
        List<LatLng> stationVos = new ArrayList<LatLng>();

        for (int i = 0; i < listLine.size(); i++) {

            stationVos.add(new LatLng(listLine.get(i).latitude, listLine.get(i).longitude));

            markerOption = new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bus_dot))
                    .title(listLine.get(i).address)
                    .position(new LatLng(listLine.get(i).latitude, listLine.get(i).longitude))

                    .draggable(true);

            //绘制路线
            aMap.addPolyline(new PolylineOptions()
                    .addAll(stationVos)
                    .width(15)
                    .color(Color.parseColor("#3C98E3")));
            Marker marker = aMap.addMarker(markerOption);
            listMarker.add(marker);
        }
    }

    @Override
    public void showNext() {
        listLine.get(0).status = BusStationsBean.LEAVE_STATION;
        listLine.get(1).status = BusStationsBean.TO_STATION;
        listLine.get(0).updateStatus();
        listLine.get(1).updateStatus();
        switchFragment(bcRuningFragment).commit();
    }

    @Override
    public void finishFragment() {
        finish();
    }


    /**
     * fragment 切换
     *
     * @param targetFragment
     * @return
     */
    private FragmentTransaction switchFragment(Fragment targetFragment) {

        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        if (!targetFragment.isAdded()) {
            //第一次使用switchFragment()时currentFragment为null，所以要判断一下
            if (currentFragment != null) {
                transaction.hide(currentFragment);
            }
            transaction.add(R.id.fragment_frame, targetFragment, targetFragment.getClass().getName());
        } else {
            transaction
                    .hide(currentFragment)
                    .show(targetFragment);
        }
        currentFragment = targetFragment;

        initToolBar();
        return transaction;
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
        mRxManager.clear();
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

//        moveToShowStationName(mylocation);
    }

    /**
     * 显示移动后 到达站点的名字
     */
    private void moveToShowStationName(LatLng mylocation) {

        if (listLine == null) {
            return;
        }
        for (int i = 0; i <= listLine.size() - 1; i++) {
            LatLng stationLatLng = new LatLng(listLine.get(i).latitude, listLine.get(i).longitude);
            if (mylocation == stationLatLng) {
                markerOption = new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.amap_bus))
                        .position(mylocation).title(listLine.get(i).name)
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



}
