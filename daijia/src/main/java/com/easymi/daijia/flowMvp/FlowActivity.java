package com.easymi.daijia.flowMvp;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
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
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.utils.overlay.SmoothMoveMarker;
import com.amap.api.navi.AMapNaviException;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.view.RouteOverLay;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.route.DriveRouteResult;
import com.easymi.component.Config;
import com.easymi.component.activity.PlaceActivity;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.EmLoc;
import com.easymi.component.loc.LocObserver;
import com.easymi.component.loc.LocReceiver;
import com.easymi.component.loc.LocService;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.utils.DensityUtil;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.MapUtil;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.LoadingButton;
import com.easymi.component.widget.overlay.DrivingRouteOverlay;
import com.easymi.daijia.R;
import com.easymi.daijia.activity.CancelActivity;
import com.easymi.daijia.activity.grab.GrabActivity;
import com.easymi.daijia.entity.Address;
import com.easymi.daijia.entity.DJOrder;
import com.easymi.daijia.fragment.AcceptFragment;
import com.easymi.daijia.fragment.ArriveStartFragment;
import com.easymi.daijia.fragment.RunningFragment;
import com.easymi.daijia.fragment.SettleFragmentDialog;
import com.easymi.daijia.fragment.SlideArriveStartFragment;
import com.easymi.daijia.fragment.ToStartFragment;
import com.easymi.daijia.fragment.WaitFragment;
import com.easymi.daijia.trace.TraceInterface;
import com.easymi.daijia.trace.TraceReceiver;
import com.easymi.daijia.widget.FlowPopWindow;
import com.easymi.daijia.widget.InputRemarkDialog;
import com.google.gson.Gson;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.List;

import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;

/**
 * Created by developerLzh on 2017/11/13 0013.
 */
@Route(path = "/daijia/FlowActivity")
public class FlowActivity extends RxBaseActivity implements FlowContract.View, LocObserver, TraceInterface {
    public static final int CANCEL_ORDER = 0X01;
    public static final int CHANGE_END = 0X02;

    CusToolbar toolbar;
    TextView nextPlace;
    TextView leftTimeText;
    TextView orderNumberText;
    TextView orderTypeText;
    TagContainerLayout tagContainerLayout;
    LinearLayout drawerFrame;
    private MapView mapView;

    LinearLayout naviCon;

    ExpandableLayout expandableLayout;

    FlowPopWindow popWindow;

    private DJOrder djOrder;

    private FlowPresenter presenter;

    private ActFraCommBridge bridge;

    private TraceReceiver traceReceiver;

    private AMap aMap;

    private long orderId;

    @Override
    public int getLayoutId() {
        return R.layout.activity_flow;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        orderId = getIntent().getLongExtra("orderId", -1);
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
        naviCon = findViewById(R.id.navi_con);
        expandableLayout = findViewById(R.id.expandable_layout);

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

            } else if (i == R.id.pop_same_order) {

            } else if (i == R.id.pop_consumer_msg) {

            }
        });
    }

    @Override
    public void showTopView() {
        orderNumberText.setText(djOrder.orderNumber);
        orderTypeText.setText(djOrder.orderDetailType);
        tagContainerLayout.removeAllTags();
        tagContainerLayout.addTag("嘻嘻");
        tagContainerLayout.addTag("哈哈");
        tagContainerLayout.addTag("傻逼");
        tagContainerLayout.addTag("智障");
        drawerFrame.setOnClickListener(view -> {
            if (expandableLayout.isExpanded()) {
                expandableLayout.collapse();
            } else {
                expandableLayout.expand();
            }
        });
        if (djOrder.orderStatus == DJOrder.NEW_ORDER
                || djOrder.orderStatus == DJOrder.PAIDAN_ORDER
                || djOrder.orderStatus == DJOrder.TAKE_ORDER
                || djOrder.orderStatus == DJOrder.GOTO_BOOKPALCE_ORDER) {
            nextPlace.setText(djOrder.startPlace);
        }
        tagContainerLayout.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, String text) {
                if (position == 1) {
                    Intent intent = new Intent(FlowActivity.this, GrabActivity.class);
                    intent.putExtra("order", djOrder);
                    startActivity(intent);
                    startActivity(intent);
                    startActivity(intent);
                    startActivity(intent);
                    startActivity(intent);
                    startActivity(intent);
                }
            }

            @Override
            public void onTagLongClick(int position, String text) {

            }

            @Override
            public void onTagCrossClick(int position) {

            }
        });
    }

    @Override
    public void showBottomFragment(DJOrder djOrder) {
        if (djOrder.orderStatus == DJOrder.PAIDAN_ORDER || djOrder.orderStatus == DJOrder.NEW_ORDER) {
            AcceptFragment acceptFragment = new AcceptFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("djOrder", djOrder);
            acceptFragment.setArguments(bundle);
            acceptFragment.setBridge(bridge);

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
            transaction.replace(R.id.flow_frame, acceptFragment);
            transaction.commit();
        } else if (djOrder.orderStatus == DJOrder.TAKE_ORDER) {
            ToStartFragment toStartFragment = new ToStartFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("djOrder", djOrder);
            toStartFragment.setArguments(bundle);
            toStartFragment.setBridge(bridge);

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
            transaction.replace(R.id.flow_frame, toStartFragment);
            transaction.commit();
        } else if (djOrder.orderStatus == DJOrder.GOTO_BOOKPALCE_ORDER) {
            SlideArriveStartFragment fragment = new SlideArriveStartFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("djOrder", djOrder);
            fragment.setArguments(bundle);
            fragment.setBridge(bridge);

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
            transaction.replace(R.id.flow_frame, fragment);
            transaction.commit();
        } else if (djOrder.orderStatus == DJOrder.ARRIVAL_BOOKPLACE_ORDER) {
            ArriveStartFragment fragment = new ArriveStartFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("djOrder", djOrder);
            fragment.setArguments(bundle);
            fragment.setBridge(bridge);

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
            transaction.replace(R.id.flow_frame, fragment);
            transaction.commit();
        } else if (djOrder.orderStatus == DJOrder.START_WAIT_ORDER) {
            WaitFragment fragment = new WaitFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("djOrder", djOrder);
            fragment.setArguments(bundle);
            fragment.setBridge(bridge);

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
            transaction.replace(R.id.flow_frame, fragment);
            transaction.commit();
        } else if (djOrder.orderStatus == DJOrder.GOTO_DESTINATION_ORDER) {
            RunningFragment fragment = new RunningFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("djOrder", djOrder);
            fragment.setArguments(bundle);
            fragment.setBridge(bridge);

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
            transaction.replace(R.id.flow_frame, fragment);
            transaction.commit();
        } else if (djOrder.orderStatus == DJOrder.ARRIVAL_DESTINATION_ORDER) {
            RunningFragment fragment = new RunningFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("djOrder", djOrder);
            fragment.setArguments(bundle);
            fragment.setBridge(bridge);

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
            transaction.replace(R.id.flow_frame, fragment);
            transaction.commit();

            SettleFragmentDialog dialog = new SettleFragmentDialog(this, djOrder, bridge);
            dialog.show();
        }
    }

    @Override
    public void showOrder(DJOrder djOrder) {
        if (null == djOrder) {
            finish();
        } else {
            this.djOrder = djOrder;
            showTopView();
            initBridge();
            showBottomFragment(djOrder);
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

        String locStr = XApp.getMyPreferences().getString(Config.SP_LAST_LOC, "");
        receiveLoc(new Gson().fromJson(locStr, EmLoc.class));//手动调用上次位置 减少从北京跳过来的时间
    }

    private Marker startMarker;
    private Marker endMarker;

    @Override
    public void showMapBounds() {
        List<LatLng> latLngs = new ArrayList<>();
        if (djOrder.orderStatus == DJOrder.NEW_ORDER
                || djOrder.orderStatus == DJOrder.PAIDAN_ORDER
                || djOrder.orderStatus == DJOrder.TAKE_ORDER
                ) {
            if (null != getStartAddr()) {
                latLngs.add(new LatLng(getStartAddr().lat, getStartAddr().lng));
                naviCon.setOnClickListener(view -> presenter.navi(new LatLng(getStartAddr().lat, getStartAddr().lng), getStartAddr().poi));
                presenter.routePlanByRouteSearch(getStartAddr().lat, getStartAddr().lng);
            } else {
                naviCon.setOnClickListener(v -> ToastUtil.showMessage(FlowActivity.this, getString(R.string.illegality_place)));
            }
            if (null != getEndAddr()) {
                latLngs.add(new LatLng(getEndAddr().lat, getEndAddr().lng));
            }
            LatLngBounds bounds = MapUtil.getBounds(latLngs, new LatLng(lastLatlng.latitude, lastLatlng.longitude));
            aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, DensityUtil.getDisplayWidth(this)/2, DensityUtil.getDisplayWidth(this)/2, 20));
        } else if (djOrder.orderStatus == DJOrder.GOTO_BOOKPALCE_ORDER) {
            if (null != getStartAddr()) {
                latLngs.add(new LatLng(getStartAddr().lat, getStartAddr().lng));
                naviCon.setOnClickListener(view -> presenter.navi(new LatLng(getStartAddr().lat, getStartAddr().lng), getStartAddr().poi));
                presenter.routePlanByRouteSearch(getStartAddr().lat, getStartAddr().lng);
            } else {
                naviCon.setOnClickListener(v -> ToastUtil.showMessage(FlowActivity.this, getString(R.string.illegality_place)));
            }
            LatLngBounds bounds = MapUtil.getBounds(latLngs, new LatLng(lastLatlng.latitude, lastLatlng.longitude));
            aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, DensityUtil.getDisplayWidth(this)/2, DensityUtil.getDisplayWidth(this)/2, 20));
        } else if (djOrder.orderStatus == DJOrder.ARRIVAL_BOOKPLACE_ORDER
                || djOrder.orderStatus == DJOrder.GOTO_DESTINATION_ORDER
                || djOrder.orderStatus == DJOrder.START_WAIT_ORDER
                ) {
            if (null != getEndAddr()) {
                latLngs.add(new LatLng(getEndAddr().lat, getEndAddr().lng));
                naviCon.setOnClickListener(view -> presenter.navi(new LatLng(getEndAddr().lat, getEndAddr().lng), getEndAddr().poi));
                presenter.routePlanByRouteSearch(getEndAddr().lat, getEndAddr().lng);
            } else {
                naviCon.setOnClickListener(v -> ToastUtil.showMessage(FlowActivity.this, getString(R.string.illegality_place)));
            }
            LatLngBounds bounds = MapUtil.getBounds(latLngs, new LatLng(lastLatlng.latitude, lastLatlng.longitude));
            aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, DensityUtil.getDisplayWidth(this)/2, DensityUtil.getDisplayWidth(this)/2, 20));
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
        aMap.moveCamera(CameraUpdateFactory.changeTilt(0));
        routeOverLay = new RouteOverLay(aMap, path, this);
        routeOverLay.setStartPointBitmap(BitmapFactory.decodeResource(getResources(), R.layout.empty));
        routeOverLay.setEndPointBitmap(BitmapFactory.decodeResource(getResources(), R.layout.empty));
        try {
            routeOverLay.setWidth(60f);
        } catch (AMapNaviException e) {
            e.printStackTrace();
        }
        routeOverLay.setTrafficLine(true);
        routeOverLay.addToMap();
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

    private Address getStartAddr() {
        Address startAddress = null;
        if (djOrder.addresses != null && djOrder.addresses.size() != 0) {
            for (Address address : djOrder.addresses) {
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
        if (djOrder.addresses != null && djOrder.addresses.size() != 0) {
            for (Address address : djOrder.addresses) {
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
                presenter.acceptOrder(djOrder.orderId, btn);
            }

            @Override
            public void doRefuse() {
                InputRemarkDialog.Builder inputBuilder = new InputRemarkDialog.Builder(FlowActivity.this);
                inputBuilder.setPositiveButton(getString(R.string.ok), (dialog, which) -> {
                    dialog.dismiss();
                    presenter.refuseOrder(djOrder.orderId, inputBuilder.getEditStr());
                });
                InputRemarkDialog dialog = inputBuilder.create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
            }

            @Override
            public void doToStart(LoadingButton btn) {
                presenter.toStart(djOrder.orderId, btn);
            }

            @Override
            public void doArriveStart() {
                presenter.arriveStart(djOrder.orderId);
            }

            @Override
            public void doStartWait(LoadingButton btn) {
                presenter.startWait(djOrder.orderId, btn);
            }

            @Override
            public void doStartDrive(LoadingButton btn) {
                presenter.startDrive(djOrder.orderId, btn);
            }

            @Override
            public void changeEnd() {
                Intent intent = new Intent(FlowActivity.this, PlaceActivity.class);
                intent.putExtra("hint", getString(R.string.please_end));
                startActivityForResult(intent, CHANGE_END);
            }

            @Override
            public void doConfirmMoney(LoadingButton btn) {
                presenter.arriveDes(djOrder, btn);
            }

            @Override
            public void doPay() {

            }

            @Override
            public void showSettleDialog() {
                SettleFragmentDialog dialog = new SettleFragmentDialog(FlowActivity.this, djOrder, bridge);
                dialog.show();
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocReceiver.getInstance().addObserver(this);//添加位置订阅

        traceReceiver = new TraceReceiver(this);
        IntentFilter filter2 = new IntentFilter();
        filter2.addAction(LocService.BROAD_TRACE_SUC);
        registerReceiver(traceReceiver, filter2);
    }

    //是否退出到了重新进来，如果是则要CameraUpdate
    private boolean onResumeIn = true;

    @Override
    protected void onResume() {
        super.onResume();
        EmLoc location = EmUtil.getLastLoc();
        if (location == null) {
            ToastUtil.showMessage(this, getString(R.string.loc_failed));
            finish();
            return;
        }
        mapView.onResume();
        onResumeIn = true;
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
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocReceiver.getInstance().deleteObserver(this);//取消位置订阅

        unregisterReceiver(traceReceiver);
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
            List<LatLng> latLngs = new ArrayList<>();
            latLngs.add(lastLatlng);
            latLngs.add(latLng);
            smoothMoveMarker.setPoints(latLngs);
            smoothMoveMarker.setTotalDuration(LocService.scanTime / DensityUtil.getDisplayWidth(this));
            smoothMoveMarker.setRotate(location.bearing);
            smoothMoveMarker.startSmoothMove();
        }

//        if (null == myLocMarker) {
//            MarkerOptions markerOption = new MarkerOptions();
//            markerOption.position(latLng);
//            markerOption.rotateAngle(location.bearing);
//            markerOption.anchor(0.5f, 0.5f);
//            markerOption.draggable(false);//设置Marker可拖动
//            markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
//                    .decodeResource(getResources(), R.mipmap.ic_flow_my_pos)));
//            // 将Marker设置为贴地显示，可以双指下拉地图查看效果
//            markerOption.setFlat(true);//设置marker平贴地图效果
//            myLocMarker = aMap.addMarker(markerOption);
//        } else {
//            myLocMarker.setPosition(latLng);
//            myLocMarker.setRotateAngle(location.bearing);
//            myLocMarker.setAnchor(0.5f, 0.5f);
//        }
        if (onResumeIn) {
            if (djOrder == null) {
                aMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            } else {
                showMapBounds();
            }
            onResumeIn = false;
        }
        if (null != djOrder) {
            if (djOrder.orderStatus == DJOrder.GOTO_DESTINATION_ORDER
                    || djOrder.orderStatus == DJOrder.GOTO_BOOKPALCE_ORDER) {
                aMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
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
                presenter.cancelOrder(djOrder.orderId, reason);
            } else if (requestCode == CHANGE_END) {
                PoiItem poiItem = data.getParcelableExtra("poiItem");
                presenter.changeEnd(orderId, poiItem.getLatLonPoint().getLatitude(), poiItem.getLatLonPoint().getLongitude(), poiItem.getTitle());
            }
        }
    }

    private Polyline tracePolyLine;
    private Polyline orignialPolyLine;

    @Override
    public void showTraceAfter(List<LatLng> before, List<LatLng> after) {

        if (null != before && before.size() != 0) {
            if (null == orignialPolyLine) {
                orignialPolyLine = aMap.addPolyline(new PolylineOptions().
                        addAll(before).width(10).color(Color.rgb(255, 0, 0)));
            } else {
                orignialPolyLine.setPoints(before);
            }
        }


        if (null != after && after.size() != 0) {
            if (null == tracePolyLine) {
                tracePolyLine = aMap.addPolyline(new PolylineOptions().
                        addAll(after).width(10).color(Color.rgb(0, 255, 0)));
            } else {
                tracePolyLine.setPoints(after);
            }
        }


    }
}
