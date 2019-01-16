package com.easymi.cityline.flowMvp;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
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
import com.amap.api.maps.utils.overlay.SmoothMoveMarker;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.RouteOverlayOptions;
import com.amap.api.navi.view.RouteOverLay;
import com.amap.api.services.route.DriveRouteResult;
import com.easymi.cityline.CLService;
import com.easymi.cityline.R;
import com.easymi.cityline.StaticVal;
import com.easymi.cityline.adapter.LeftWindowAdapter;
import com.easymi.common.entity.OrderCustomer;
import com.easymi.cityline.entity.ZXOrder;
import com.easymi.cityline.flowMvp.fragment.AcceptSendFragment;
import com.easymi.cityline.flowMvp.fragment.ChangeSeqFragment;
import com.easymi.cityline.flowMvp.fragment.CusListFragment;
import com.easymi.cityline.flowMvp.fragment.FinishFragment;
import com.easymi.cityline.flowMvp.fragment.NotStartFragment;
import com.easymi.cityline.receiver.CancelOrderReceiver;
import com.easymi.cityline.receiver.OrderFinishReceiver;
import com.easymi.cityline.widget.ChangePopWindow;
import com.easymi.component.Config;
import com.easymi.component.ZXOrderStatus;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.BaseOrder;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.entity.EmLoc;
import com.easymi.component.loc.LocObserver;
import com.easymi.component.loc.LocReceiver;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc3;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.network.NoErrSubscriberListener;
import com.easymi.component.result.EmResult2;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.utils.DensityUtil;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.Log;
import com.easymi.component.utils.TimeUtil;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.overlay.DrivingRouteOverlay;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 *
 * @author liuzihao
 * @date 2018/11/15
 * <p>
 * 订单执行流程
 */
@Route(path = "/cityline/FlowActivity")
public class FlowActivity extends RxBaseActivity implements
        FlowContract.View,
        LocObserver,
        CancelOrderReceiver.OnCancelListener,
        AMap.OnMapTouchListener,
        OrderFinishReceiver.OnFinishListener, AMap.OnMarkerClickListener, AMap.OnMapClickListener {

    CusToolbar cusToolbar;
    MapView mapView;
    FrameLayout fragmentFrame;

    AMap aMap;

    private LatLng lastLatlng;
    private Marker myFirstMarker;

    FlowPresenter presenter;

    private ZXOrder zxOrder;
    Fragment currentFragment;

    private ActFraCommBridge bridge;

    DymOrder dymOrder;

    public static boolean isMapTouched = false;

    private boolean isOrderLoadOk = false;//订单查询是否完成

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_zx_flow;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        BaseOrder baseOrder = (BaseOrder) getIntent().getSerializableExtra("baseOrder");

        if (null == baseOrder) {
            finish();
            return;
        }

        baseToZX(baseOrder);
        presenter = new FlowPresenter(this, this);

        mapView = findViewById(R.id.map_view);
        fragmentFrame = findViewById(R.id.fragment_frame);

        mapView.onCreate(savedInstanceState);
        initMap();
        initBridget();
        initFragment();

        getCustomers(zxOrder);

    }

    private void getCustomers(ZXOrder zxOrder) {
        Observable<EmResult2<List<OrderCustomer>>> observable = ApiManager.getInstance().createApi(Config.HOST, CLService.class)
                .getOrderCustomers(zxOrder.orderId, "5,10,15,20")
                .filter(new HttpResultFunc3<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, true, false, result2 -> {
            if (result2.getData() == null || result2.getData().size() == 0){
                ToastUtil.showMessage(this,"当前班次没有任何乘客");
                finish();
            }
            isOrderLoadOk = true;
            List<OrderCustomer> orderCustomers = result2.getData();

            for (int i = 0; i < orderCustomers.size(); i++) {
                OrderCustomer orderCustomer = orderCustomers.get(i);

//                List<OrderCustomer> allCus = OrderCustomer.findByIDTypeOrderByAcceptSeq(zxOrder.orderId, zxOrder.orderType);
//                for (OrderCustomer cusOrder : allCus) {
//                    boolean isExist = false;
//
//                    if ((cusOrder.id == orderCustomer.id)) {
//                        isExist = true;
//                        break;
//                    }
//
//                    if (!isExist) {
//                        cusOrder.delete(cusOrder.id);
//                    }
//                }

                orderCustomer.appointTime = orderCustomer.appointTime * 1000;
                orderCustomer.num = i + 1;
                orderCustomer.acceptSequence = i;
                orderCustomer.sendSequence = i;
                //后端状态与本地状态衔接 这些仅仅针对于本地数据库首次创建时
                if (orderCustomer.status <= OrderCustomer.CITY_LINE_STATUS_NEW) {
                    orderCustomer.status = 0;
                    orderCustomer.subStatus = 0;
                } else if (orderCustomer.status == OrderCustomer.CITY_LINE_STATUS_TAKE) {
                    orderCustomer.status = 0;
                    orderCustomer.subStatus = 1;
                } else if (orderCustomer.status == OrderCustomer.CITY_LINE_STATUS_RUN) {
                    orderCustomer.status = 3;
                    orderCustomer.subStatus = 1;
                } else if (orderCustomer.status == OrderCustomer.CITY_LINE_STATUS_SKIP) {
                    orderCustomer.status = 5;
                    orderCustomer.subStatus = 1;
                } else if (orderCustomer.status == OrderCustomer.CITY_LINE_STATUS_FINISH) {
                    orderCustomer.status = 4;
                    orderCustomer.subStatus = 1;
                }

                orderCustomer.orderId = zxOrder.orderId;
                orderCustomer.orderType = zxOrder.orderType;

                for (OrderCustomer.OrderAddressVo orderAddressVo : orderCustomer.orderAddressVos) {
                    if (orderAddressVo.type == 1) { //起点
                        orderCustomer.startAddr = orderAddressVo.address;
                        orderCustomer.startLat = orderAddressVo.latitude;
                        orderCustomer.startLng = orderAddressVo.longitude;
                    } else { //终点
                        orderCustomer.endAddr = orderAddressVo.address;
                        orderCustomer.endLat = orderAddressVo.latitude;
                        orderCustomer.endLng = orderAddressVo.longitude;
                    }
                }

                orderCustomer.saveOrUpdate();
            }
            showFragmentByStatus();
        })));
    }

    public void baseToZX(BaseOrder baseOrder) {
        zxOrder = new ZXOrder();

        zxOrder.orderId = baseOrder.scheduleId;
        zxOrder.orderType = baseOrder.serviceType;
        zxOrder.startSite = baseOrder.bookAddress;
        zxOrder.endSite = baseOrder.destination;
        zxOrder.startOutTime = baseOrder.bookTime * 1000;//开始出发时间
        zxOrder.minute = baseOrder.minute;//xx分钟前开始接人
        zxOrder.startJierenTime = baseOrder.bookTime * 1000 - baseOrder.minute * 60 * 1000;//开始接人时间
        zxOrder.startLat = baseOrder.startLatitude;
        zxOrder.startLng = baseOrder.startLongitude;
        zxOrder.endLat = baseOrder.endLatitude;
        zxOrder.endLng = baseOrder.endLongitude;
        zxOrder.status = baseOrder.scheduleStatus;
        zxOrder.lineId = baseOrder.lineId;//线路Id
        zxOrder.lineName = baseOrder.lineName;//线路Id
        zxOrder.seats = baseOrder.seats;//剩余票数
    }

    ChangePopWindow changePopWindow;

    @Override
    public void changeToolbar(int flag) {
        DymOrder dymOrder = DymOrder.findByIDType(zxOrder.orderId, zxOrder.orderType);
        if (null == dymOrder) {
            return;
        }
        if (flag == StaticVal.TOOLBAR_NOT_START) {
            cusToolbar.setLeftBack(view -> finish());
            cusToolbar.setTitle("行程未开始");
            cusToolbar.setRightGone();
        } else if (flag == StaticVal.TOOLBAR_CHANGE_ACCEPT) {
            cusToolbar.setLeftBack(view -> {
                if (dymOrder.orderStatus <= ZXOrderStatus.WAIT_START) {
                    bridge.toNotStart();
                } else if (dymOrder.orderStatus == ZXOrderStatus.ACCEPT_PLAN) {
                    finish();
                } else if (dymOrder.orderStatus == ZXOrderStatus.ACCEPT_ING) {
                    bridge.toCusList(StaticVal.PLAN_ACCEPT);
                }
            });
            cusToolbar.setTitle("行程规划");
            cusToolbar.setRightGone();
        } else if (flag == StaticVal.TOOLBAR_CHANGE_SEND) {
            cusToolbar.setLeftBack(view -> {
                if (dymOrder.orderStatus <= ZXOrderStatus.WAIT_START) {
                    bridge.toNotStart();
                } else if (dymOrder.orderStatus == ZXOrderStatus.SEND_PLAN) {
                    finish();
                } else if (dymOrder.orderStatus == ZXOrderStatus.ACCEPT_ING) {
                    bridge.toCusList(StaticVal.PLAN_ACCEPT);
                } else if (dymOrder.orderStatus == ZXOrderStatus.SEND_ING) {
                    bridge.toCusList(StaticVal.PLAN_SEND);
                }
            });
            cusToolbar.setTitle("行程规划");
            cusToolbar.setRightGone();
        } else if (flag == StaticVal.TOOLBAR_ACCEPT_ING) {
            cusToolbar.setLeftBack(view -> bridge.toAcSend());
            cusToolbar.setTitle("正在接人");
            cusToolbar.setRightText(R.string.change_sequence, view -> {
                //展示修改顺序的pop
                changePopWindow = new ChangePopWindow(FlowActivity.this);
                changePopWindow.setOnClickListener(view1 -> {
                    long id = view1.getId();
                    if (id == R.id.pop_change_accept) {
                        bridge.toChangeSeq(StaticVal.PLAN_ACCEPT);
                    } else if (id == R.id.pop_change_send) {
                        bridge.toChangeSeq(StaticVal.PLAN_SEND);
                    }
                });
                changePopWindow.show(view);
            });
        } else if (flag == StaticVal.TOOLBAR_SEND_ING) {
            cusToolbar.setLeftBack(view -> bridge.toAcSend());
            cusToolbar.setTitle("正在送人");
            cusToolbar.setRightText(R.string.change_sequence, view -> {
                //展示修改顺序的pop
                changePopWindow = new ChangePopWindow(FlowActivity.this);
                changePopWindow.hideAccept();
                changePopWindow.setOnClickListener(view1 -> {
                    long id = view1.getId();
                    if (id == R.id.pop_change_accept) {
                        bridge.toChangeSeq(StaticVal.PLAN_ACCEPT);
                    } else if (id == R.id.pop_change_send) {
                        bridge.toChangeSeq(StaticVal.PLAN_SEND);
                    }
                });
                changePopWindow.show(view);
            });
        } else if (flag == StaticVal.TOOLBAR_FLOW) {
            cusToolbar.setLeftBack(view -> finish());
            cusToolbar.setRightText("查看规划", view -> {
                if (dymOrder.orderStatus == ZXOrderStatus.ACCEPT_ING) {
                    bridge.toCusList(StaticVal.PLAN_ACCEPT);
                } else {
                    bridge.toCusList(StaticVal.PLAN_SEND);
                }
            });
            OrderCustomer current = acceptSendFragment.getCurrent();
            if (current.status == 0) { //未接
                if (current.subStatus == 0) {
                    cusToolbar.setTitle("前往预约地");
                } else if (current.subStatus == 1) {
                    cusToolbar.setTitle("等待中");
                }
            } else if (current.status == 3) {
                cusToolbar.setTitle("行程中");
            }
        } else if (flag == StaticVal.TOOLBAR_FINISH) {
            cusToolbar.setLeftBack(view -> finish());
            cusToolbar.setTitle("行程结束");
            cusToolbar.setRightGone();
        }

    }

    @Override
    public void startOutSuc() {

        DymOrder dymOrder = DymOrder.findByIDType(zxOrder.orderId, zxOrder.orderType);
        if (null != dymOrder) {
            dymOrder.orderStatus = ZXOrderStatus.ACCEPT_ING;
            dymOrder.updateStatus();
        }
        bridge.toAcSend();

    }

    @Override
    public void startSendSuc() {
        dymOrder = DymOrder.findByIDType(zxOrder.orderId, zxOrder.orderType);
        if (null != dymOrder) {
            dymOrder.orderStatus = ZXOrderStatus.SEND_ING;
            dymOrder.updateStatus();
        }
        List<OrderCustomer> customers = OrderCustomer.findByIDTypeOrderBySendSeq(zxOrder.orderId, zxOrder.orderType);
        for (OrderCustomer customer : customers) {
            //接完后把所有的订单置为未送
            if (customer.status == 1) {
                //只有已接的才置为未送状态
                customer.status = 3;
                customer.updateStatus();
            } else {
                //跳过接的直接置为跳过送状态
                customer.status = 5;
                customer.updateStatus();
            }
        }
        acceptSendFragment.showWhatByStatus();
        acceptSendFragment.resetSpeakedHint();
    }

    @Override
    public void finishTaskSuc() {
        dymOrder = DymOrder.findByIDType(zxOrder.orderId, zxOrder.orderType);
        if (null != dymOrder) {
            dymOrder.orderStatus = ZXOrderStatus.SEND_OVER;
            dymOrder.updateStatus();

            bridge.toFinished();
        }
    }

    @Override
    public RxManager getManager() {
        return mRxManager;
    }

    @Override
    public void initToolBar() {
        super.initToolBar();
        cusToolbar = findViewById(R.id.cus_toolbar);
    }

    CusListFragment cusListFragment;
    ChangeSeqFragment changeSeqFragment;
    NotStartFragment notStartFragment;
    AcceptSendFragment acceptSendFragment;
    FinishFragment finishFragment;

    /**
     * 初始化fragment
     */
    @Override
    public void initFragment() {
        cusListFragment = new CusListFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("orderId", zxOrder.orderId);
        bundle.putString("orderType", zxOrder.orderType);
        cusListFragment.setArguments(bundle);

        changeSeqFragment = new ChangeSeqFragment();
        changeSeqFragment.setArguments(bundle);

        notStartFragment = new NotStartFragment();
        Bundle orderBundle = new Bundle();
        orderBundle.putSerializable("zxOrder", zxOrder);
        notStartFragment.setArguments(orderBundle);
        notStartFragment.setBridge(bridge);

        acceptSendFragment = new AcceptSendFragment();
        acceptSendFragment.setArguments(bundle);
        acceptSendFragment.setBridge(bridge);

        finishFragment = new FinishFragment();
        finishFragment.setBridge(bridge);

//        switchFragment(cusListFragment).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        if (isOrderLoadOk) {
            showFragmentByStatus();
        }
        Log.e("hufeng/onResume", TimeUtil.getTime("HH:mm:ss:SSS",System.currentTimeMillis()));
    }

    /**
     * 地图设置
     */
    @Override
    public void initMap() {
        aMap = mapView.getMap();
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.getUiSettings().setRotateGesturesEnabled(false);
        aMap.getUiSettings().setRotateGesturesEnabled(false);
        aMap.getUiSettings().setTiltGesturesEnabled(false);//倾斜手势
        aMap.getUiSettings().setLogoBottomMargin(-50);//隐藏logo

        aMap.setOnMapTouchListener(this);

        aMap.setOnMarkerClickListener(this);
        aMap.setOnMapClickListener(this);

        aMap.setInfoWindowAdapter(new LeftWindowAdapter(this));

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

    /**
     * 初始化fragment bridget
     */
    @Override
    public void initBridget() {
        bridge = new ActFraCommBridge() {

            @Override
            public void showBounds(List<LatLng> latLngs) {
                boundsZoom(latLngs);
            }

            @Override
            public void addMarker(LatLng latLng, int flag) {
                FlowActivity.this.addMarker(latLng, flag);
            }

            @Override
            public void addMarker(LatLng latLng, int flag, int num) {
                FlowActivity.this.addMarker(latLng, flag, num);
            }

            @Override
            public void toCusList(int flag) {
                cusListFragment.setParam(bridge, flag);
                switchFragment(cusListFragment).commit();
            }

            @Override
            public void toNotStart() {
                switchFragment(notStartFragment).commit();
            }

            @Override
            public void toAcSend() {
                delayAnimate = false;
                switchFragment(acceptSendFragment).commit();
            }

            @Override
            public void toChangeSeq(int flag) {
                changeSeqFragment.setParam(bridge, flag);
                switchFragment(changeSeqFragment).commit();
            }

            @Override
            public void toFinished() {
                switchFragment(finishFragment).commit();
                presenter.deleteDb(zxOrder.orderId, zxOrder.orderType);
            }

            @Override
            public void toOrderList() {
                FlowActivity.this.finish();
            }

            @Override
            public void changeToolbar(int flag) {
                FlowActivity.this.changeToolbar(flag);
            }

            @Override
            public void clearMap() {
                aMap.clear();
                smoothMoveMarker = null;
                initMap();
                receiveLoc(EmUtil.getLastLoc());//第一时间加上自身位置
            }

            @Override
            public void routePath(LatLng toLatlng) {
                presenter.routeLineByNavi(lastLatlng, null, toLatlng);
            }

            @Override
            public void routePath(LatLng startLatlng, List<LatLng> passLatlngs, LatLng endLatlng) {
                presenter.routePlanByRouteSearch(startLatlng, passLatlngs, endLatlng);
            }

            @Override
            public void startOutSet() { //时间到了 开始出发接人
                presenter.startOutSet(zxOrder.orderId);
            }

            @Override
            public void arriveStart(OrderCustomer orderCustomer) {
                //TODO 到达预约地
                presenter.arriveStart(orderCustomer);
            }

            @Override
            public void acceptCustomer(OrderCustomer orderCustomer) {
                //TODO 接到客户
                presenter.acceptCustomer(orderCustomer);
            }

            @Override
            public void jumpAccept(OrderCustomer orderCustomer) {
                //TODO 跳过接
                presenter.jumpAccept(orderCustomer);
            }

            @Override
            public void arriveEnd(OrderCustomer orderCustomer) {
                //TODO 到达目的地
                presenter.arriveEnd(orderCustomer);
            }

            @Override
            public void jumpSend(OrderCustomer orderCustomer) {
                //TODO 跳过送
                presenter.jumpSend(orderCustomer);
            }

            @Override
            public void doRefresh() {
                isMapTouched = false;
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLatlng, 19));
            }

            @Override
            public void countStartOver() {
                if (currentFragment instanceof ChangeSeqFragment) {
                    changeSeqFragment.setCountStratOver(true);
                }
            }

            @Override
            public void navi(LatLng latLng, Long orderId) {
                presenter.navi(latLng, orderId);
            }
        };
    }


    /**
     * 添加起终种类的marker
     *
     * @param latLng
     * @param flag
     */
    @Override
    public void addMarker(LatLng latLng, int flag) {
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(latLng);
        markerOption.draggable(false);//设置Marker可拖动
        if (flag == StaticVal.MARKER_FLAG_START) {
            markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(getResources(), R.mipmap.ic_start)));
        } else if (flag == StaticVal.MARKER_FLAG_END) {
            markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(getResources(), R.mipmap.ic_end)));
        }
        // 将Marker设置为贴地显示，可以双指下拉地图查看效果
        markerOption.setFlat(true);//设置marker平贴地图效果
        aMap.addMarker(markerOption);

    }

    /**
     * 添加数字标号种类的marker
     *
     * @param latLng
     * @param flag
     */
    @Override
    public void addMarker(LatLng latLng, int flag, int num) {
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(latLng);
        markerOption.draggable(false);//设置Marker可拖动


        View view = LayoutInflater.from(this).inflate(R.layout.sequence_marker, null);
        TextView tv = view.findViewById(R.id.seq_num);
        tv.setText(String.valueOf(num));
        if (flag == StaticVal.MARKER_FLAG_PASS_ENABLE) {
            tv.setBackgroundResource(R.drawable.circle_accent);
        } else {
            tv.setBackgroundResource(R.drawable.circle_gray);
        }
        markerOption.icon(BitmapDescriptorFactory.fromView(view));

        // 将Marker设置为贴地显示，可以双指下拉地图查看效果
        markerOption.setFlat(true);//设置marker平贴地图效果
        aMap.addMarker(markerOption);

    }

    /**
     * 区域缩放
     *
     * @param latLngs
     */
    @Override
    public void boundsZoom(List<LatLng> latLngs) {
        if (latLngs == null) {
            return;
        }
        latLngs.add(lastLatlng);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : latLngs) {
            builder.include(latLng);
        }
        LatLngBounds bounds = builder.build();
        if (currentFragment instanceof ChangeSeqFragment) {
            int left = DensityUtil.dp2px(this, 10);
            int bottom = DensityUtil.dp2px(this, 260);
            int top = DensityUtil.dp2px(this, 45);
            aMap.animateCamera(CameraUpdateFactory.newLatLngBoundsRect(bounds, left, left, top, bottom));
        } else {

            aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
                    bounds,
                    (int) (DensityUtil.getDisplayWidth(this) / 1.5),
                    (int) (DensityUtil.getDisplayWidth(this) / 2),
                    0));
        }


        latLngs.remove(lastLatlng);//后续可能会使用这个latLngs 所以移除加入的上次位置
    }

    /**
     * 定位位置缩放，默认级别19
     *
     * @param level
     */
    @Override
    public void locZoom(int level) {
        if (null != lastLatlng) {
            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLatlng, level == 0 ? 19 : level));
        }
    }

    RouteOverLay routeOverLay;

    /**
     * 导航路径展示
     *
     * @param ints
     * @param path
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

    /**
     * 路径规划展示
     *
     * @param result
     */
    @Override
    public void showPath(DriveRouteResult result) {
        if (drivingRouteOverlay != null) {
            drivingRouteOverlay.removeFromMap();
        }

        drivingRouteOverlay = new DrivingRouteOverlay(this, aMap,
                result.getPaths().get(0), result.getStartPos()
                , result.getTargetPos(), null);
//        drivingRouteOverlay.setRouteWidth(5);
        drivingRouteOverlay.setIsColorfulline(false);
        drivingRouteOverlay.setNodeIconVisibility(false);//隐藏转弯的节点
        drivingRouteOverlay.addToMap();
//        drivingRouteOverlay.zoomToSpan();
//        List<LatLng> latLngs = new ArrayList<>();
//        latLngs.add(new LatLng(result.getStartPos().getLatitude(), result.getStartPos().getLongitude()));
//        latLngs.add(new LatLng(result.getTargetPos().getLatitude(), result.getTargetPos().getLongitude()));
//        EmLoc lastLoc = EmUtil.getLastLoc();
//        latLngs.add(new LatLng(lastLoc.latitude, lastLoc.longitude));
//
//        boundsZoom(latLngs);
    }

    String leftDis;
    String leftTime;

    @Override
    public void showLeft(int dis, int time) {
        acceptSendFragment.showLeft(dis);
        int km = dis / 1000;
        if (km > 1) {
            String disKm = new DecimalFormat("#0.0").format((double) dis / 1000);
            leftDis = "距离" +
                    "<font color='orange'><b><tt>" +
                    disKm + "</tt></b></font>" + getString(R.string.km);
        } else {
            leftDis = "距离" +
                    "<font color='black'><b><tt>" +
                    dis + "</tt></b></font>"
                    + getString(R.string.meter);
        }

        int hour = time / 60 / 60;
        int minute = time / 60;
        if (hour > 0) {
            leftTime = "预计" +
                    "<font color='orange'><b><tt>" +
                    hour +
                    "</tt></b></font>"
                    + getString(R.string.hour_) +
                    "<font color='orange'><b><tt>" +
                    time / 60 % 60 +
                    "</tt></b></font>" +
                    getString(R.string.minute_) +
                    "到达";
        } else {
            leftTime = "预计" +
                    "<font color='black'><b><tt>" +
                    minute +
                    "</tt></b></font>" +
                    getString(R.string.minute_) +
                    "到达";
        }
        if (null != smoothMoveMarker) {
            Marker marker = smoothMoveMarker.getMarker();
            marker.setSnippet(leftDis);//leftDis
            marker.setTitle(leftTime);//leftTime
            marker.showInfoWindow();
        }
    }

    @Override
    public void arriveStartSuc(OrderCustomer orderCustomer) {
        orderCustomer.subStatus = 1;
        orderCustomer.appointTime = System.currentTimeMillis() + 10 * 60 * 1000;
        orderCustomer.updateSubStatus();
        acceptSendFragment.showWhatByStatus();
    }

    @Override
    public void acceptCustomerSuc(OrderCustomer orderCustomer) {
        List<OrderCustomer> customers = OrderCustomer.findByIDTypeOrderByAcceptSeq(zxOrder.orderId, zxOrder.orderType);
        orderCustomer.status = 1;
        orderCustomer.updateStatus();
        if (orderCustomer.acceptSequence == customers.size() - 1) { //接完最后一个更新订单状态
            presenter.startSend(zxOrder.orderId);
        } else {
            acceptSendFragment.showWhatByStatus();
            acceptSendFragment.resetSpeakedHint();
        }
    }


    @Override
    public void jumpAcceptSuc(OrderCustomer orderCustomer) {
        orderCustomer.status = 2;
        orderCustomer.updateStatus();
        List<OrderCustomer> customers = OrderCustomer.findByIDTypeOrderByAcceptSeq(zxOrder.orderId, zxOrder.orderType);

        if (orderCustomer.id == customers.get(customers.size() - 1).id) { //接完最后一个更新订单状态
            if (customers.size() == 1) {
                //add hf  只有一个客户，并且跳过了。直接结束
                presenter.finishTask(zxOrder.orderId);
            } else {
                //多个客户全部跳过问题
                boolean isAllJump = true;
                for (int i = 0; i < (customers.size() - 1); i++) {
                    if (customers.get(i).status != 2) {
                        isAllJump = false;
                    }
                }
                if (!isAllJump) {
                    presenter.startSend(zxOrder.orderId);
                } else {
                    presenter.finishTask(zxOrder.orderId);
                }
            }
        } else {
            acceptSendFragment.showWhatByStatus();
            acceptSendFragment.resetSpeakedHint();
        }
    }

    @Override
    public void arriveEndSuc(OrderCustomer orderCustomer) {
        orderCustomer.status = 4;
        orderCustomer.updateStatus();
        List<OrderCustomer> customers = OrderCustomer.findByIDTypeOrderBySendSeq(zxOrder.orderId, zxOrder.orderType);
        if (orderCustomer.id == customers.get(customers.size() - 1).id) { //送完最后一个更新订单状态
            presenter.finishTask(zxOrder.orderId);
        } else {
            acceptSendFragment.showWhatByStatus();
            acceptSendFragment.resetSpeakedHint();
        }
    }

    @Override
    public void jumpSendSuc(OrderCustomer orderCustomer) {
        orderCustomer.status = 5;
        orderCustomer.updateStatus();
        List<OrderCustomer> customers = OrderCustomer.findByIDTypeOrderBySendSeq(zxOrder.orderId, zxOrder.orderType);
        if (orderCustomer.sendSequence == customers.size() - 1) { //送完最后一个更新订单状态
            presenter.finishTask(zxOrder.orderId);
        } else {
            acceptSendFragment.showWhatByStatus();
            acceptSendFragment.resetSpeakedHint();
        }

    }

    @Override
    public void showFragmentByStatus() {
        DymOrder dymOrder = DymOrder.findByIDType(zxOrder.orderId, zxOrder.orderType);
        if (null != dymOrder) {
            if (dymOrder.orderStatus <= ZXOrderStatus.WAIT_START) {
                bridge.toNotStart();
            } else if (dymOrder.orderStatus == ZXOrderStatus.ACCEPT_PLAN) {
                bridge.toChangeSeq(StaticVal.PLAN_ACCEPT);
            } else if (dymOrder.orderStatus == ZXOrderStatus.SEND_PLAN) {
                bridge.toChangeSeq(StaticVal.PLAN_SEND);
            } else if (dymOrder.orderStatus == ZXOrderStatus.ACCEPT_ING
                    || dymOrder.orderStatus == ZXOrderStatus.SEND_ING) {
                bridge.toAcSend();
            } else if (dymOrder.orderStatus == ZXOrderStatus.SEND_OVER) {

            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocReceiver.getInstance().addObserver(this);//添加位置订阅
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocReceiver.getInstance().deleteObserver(this);//取消位置订阅
        Log.e("hufeng/onStop", TimeUtil.getTime("HH:mm:ss:SSS",System.currentTimeMillis()));
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        notStartFragment.cancelTimer();
        finishFragment.cancelTimer();
        acceptSendFragment.cancelTimer();
        Log.e("hufeng/onPause", TimeUtil.getTime("HH:mm:ss:SSS",System.currentTimeMillis()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        presenter.stopNavi();
        Log.e("hufeng/onDestroy", TimeUtil.getTime("HH:mm:ss:SSS",System.currentTimeMillis()));
    }

    @Override
    public void onTouch(MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
            Log.e("mapTouch", "-----map onTouched-----");
            isMapTouched = true;

            if (null != acceptSendFragment) {
                acceptSendFragment.mapStatusChanged();
            }
        }
    }

    SmoothMoveMarker smoothMoveMarker;

    boolean delayAnimate = true;

    @Override
    public void receiveLoc(EmLoc location) {
        if (null == location) {
            return;
        }
        Log.e("locPos", "bearing 2 >>>>" + location.bearing);
        LatLng latLng = new LatLng(location.latitude, location.longitude);

        if (null == smoothMoveMarker) {
            //首次进入
            smoothMoveMarker = new SmoothMoveMarker(aMap);
            smoothMoveMarker.setDescriptor(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(getResources(), R.mipmap.ic_flow_my_pos)));
            smoothMoveMarker.setPosition(lastLatlng);
            smoothMoveMarker.setRotate(location.bearing);
//            smoothMoveMarker.getMarker().
        } else {
            //去除掉首次的位置marker
            if (null != myFirstMarker) {
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

//                if ((dymOrder.orderStatus == ZXOrderStatus.SEND_ING
//                        || dymOrder.orderStatus == ZXOrderStatus.ACCEPT_ING)
//                        && currentFragment instanceof AcceptSendFragment) {
//                    marker.setSnippet(leftDis);//leftDis
//                    marker.setTitle(leftTime);//leftTime
                marker.setInfoWindowEnable(true);
//                    marker.setClickable(true);
//                } else {
//                    marker.setInfoWindowEnable(false);
                marker.setClickable(false);
//                }
                marker.setAnchor(0.5f, 0.5f);
            }
        }

        if (dymOrder == null) {
            dymOrder = DymOrder.findByIDType(zxOrder.orderId, zxOrder.orderType);
        }

        if (null != dymOrder) {
            if (dymOrder.orderStatus == ZXOrderStatus.ACCEPT_ING
                    || dymOrder.orderStatus == ZXOrderStatus.SEND_ING) {
                if (!isMapTouched && currentFragment instanceof AcceptSendFragment) {
                    aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19), delayAnimate ? Config.NORMAL_LOC_TIME : 0, null);
                    delayAnimate = true;
                }
            }
        }

        lastLatlng = latLng;
    }

    @Override
    public void onFinishOrder(long orderId, String orderType) {
        presenter.deleteDb(orderId, orderType);
        finish();
    }

    @Override
    public void onCancelOrder(long orderId, String orderType, String msg) {
        presenter.deleteDb(orderId, orderType);
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
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMapClick(LatLng latLng) {
//        if (null != smoothMoveMarker) {
//            smoothMoveMarker.getMarker().hideInfoWindow();
//        }
    }

    @Override
    public void onBackPressed() {
        cusToolbar.leftIcon.callOnClick();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mapView.onSaveInstanceState(outState);
    }
}
