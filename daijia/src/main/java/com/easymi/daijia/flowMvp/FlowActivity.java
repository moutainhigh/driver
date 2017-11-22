package com.easymi.daijia.flowMvp;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
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
import com.easymi.component.Config;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.EmLoc;
import com.easymi.component.loc.LocReceiver;
import com.easymi.component.loc.LocService;
import com.easymi.component.loc.ReceiveLocInterface;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.utils.MapUtil;
import com.easymi.component.utils.ToastUtil;
import com.easymi.daijia.R;
import com.easymi.daijia.activity.CancelActivity;
import com.easymi.daijia.entity.Address;
import com.easymi.daijia.entity.DJOrder;
import com.easymi.daijia.fragment.AcceptFragment;
import com.easymi.daijia.fragment.ArriveStartFragment;
import com.easymi.daijia.fragment.RunningFragment;
import com.easymi.daijia.fragment.SettleFragmentDialog;
import com.easymi.daijia.fragment.SlideArriveStartFragment;
import com.easymi.daijia.fragment.ToStartFragment;
import com.easymi.daijia.fragment.WaitFragment;
import com.google.gson.Gson;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.List;

import co.lujun.androidtagview.TagContainerLayout;

/**
 * Created by developerLzh on 2017/11/13 0013.
 */
@Route(path = "/daijia/FlowActivity")
public class FlowActivity extends RxBaseActivity implements FlowContract.View, ReceiveLocInterface {

    TextView nextPlace;
    TextView leftTimeText;
    TextView orderNumberText;
    TextView orderTypeText;
    TagContainerLayout tagContainerLayout;
    LinearLayout drawerFrame;
    private MapView mapView;

    LinearLayout naviCon;

    ExpandableLayout expandableLayout;

    private DJOrder djOrder;

    private FlowPresenter presenter;

    private ActFraCommBridge bridge;

    private LocReceiver locReceiver;

    private AMap aMap;

    @Override
    public int getLayoutId() {
        return R.layout.activity_flow;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        long orderId = getIntent().getLongExtra("orderId", -1);
        if (orderId == -1) {
            finish();
            return;
        }

        presenter = new FlowPresenter(this, this);

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

        presenter.findOne(orderId);
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

        receiveLoc();//手动调用上次位置 减少从北京跳过来的时间
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
            } else {
                naviCon.setOnClickListener(v -> ToastUtil.showMessage(FlowActivity.this, getString(R.string.illegality_place)));
            }
            if (null != getEndAddr()) {
                latLngs.add(new LatLng(getEndAddr().lat, getEndAddr().lng));
            }
            LatLngBounds bounds = MapUtil.getBounds(latLngs, new LatLng(location.latitude, location.longitude));
            aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));
        } else if (djOrder.orderStatus == DJOrder.GOTO_BOOKPALCE_ORDER) {
            if (null != getStartAddr()) {
                latLngs.add(new LatLng(getStartAddr().lat, getStartAddr().lng));
                naviCon.setOnClickListener(view -> presenter.navi(new LatLng(getStartAddr().lat, getStartAddr().lng), getStartAddr().poi));
            } else {
                naviCon.setOnClickListener(v -> ToastUtil.showMessage(FlowActivity.this, getString(R.string.illegality_place)));
            }
            LatLngBounds bounds = MapUtil.getBounds(latLngs, new LatLng(location.latitude, location.longitude));
            aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));
        } else if (djOrder.orderStatus == DJOrder.ARRIVAL_BOOKPLACE_ORDER
                || djOrder.orderStatus == DJOrder.GOTO_DESTINATION_ORDER
                || djOrder.orderStatus == DJOrder.START_WAIT_ORDER
                ) {
            if (null != getEndAddr()) {
                latLngs.add(new LatLng(getEndAddr().lat, getEndAddr().lng));
                naviCon.setOnClickListener(view -> presenter.navi(new LatLng(getEndAddr().lat, getEndAddr().lng), getEndAddr().poi));
            } else {
                naviCon.setOnClickListener(v -> ToastUtil.showMessage(FlowActivity.this, getString(R.string.illegality_place)));
            }
            LatLngBounds bounds = MapUtil.getBounds(latLngs, new LatLng(location.latitude, location.longitude));
            aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 40));
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
            public void doAccept() {
                presenter.acceptOrder(djOrder.orderId);
            }

            @Override
            public void doRefuse() {
                Intent intent = new Intent(FlowActivity.this, CancelActivity.class);
                startActivityForResult(intent, 0);
            }

            @Override
            public void doToStart() {
                presenter.toStart(djOrder.orderId);
            }

            @Override
            public void doArriveStart() {
                presenter.arriveStart(djOrder.orderId);
            }

            @Override
            public void doStartWait() {
                presenter.startWait(djOrder.orderId);
            }

            @Override
            public void doStartDrive() {
                presenter.startDrive(djOrder.orderId);
            }

            @Override
            public void changeEnd() {

            }

            @Override
            public void doConfirmMoney() {
                presenter.arriveDes(djOrder.orderId);
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
        locReceiver = new LocReceiver(this);
        IntentFilter filter = new IntentFilter(LocService.LOC_CHANGED);
        registerReceiver(locReceiver, filter);
    }

    //是否退出到了重新进来，如果是则要CameraUpdate
    private boolean onResumeIn = true;

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        onResumeIn = true;
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
        unregisterReceiver(locReceiver);
    }

    private EmLoc location;
    private Marker myLocMarker;

    @Override
    public void receiveLoc() {
        location = new Gson().fromJson(XApp.getMyPreferences().getString(Config.SP_LAST_LOC, ""), EmLoc.class);
        LatLng latLng = new LatLng(location.latitude, location.longitude);
        if (null == myLocMarker) {
            MarkerOptions markerOption = new MarkerOptions();
            markerOption.position(latLng);
            markerOption.rotateAngle(location.bearing);
            markerOption.draggable(false);//设置Marker可拖动
            markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(getResources(), R.mipmap.ic_flow_my_pos)));
            // 将Marker设置为贴地显示，可以双指下拉地图查看效果
            markerOption.setFlat(true);//设置marker平贴地图效果
            myLocMarker = aMap.addMarker(markerOption);
        } else {
            myLocMarker.setPosition(latLng);
            myLocMarker.setRotateAngle(location.bearing);
        }
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String reason = data.getStringExtra("reason");

                presenter.refuseOrder(djOrder.orderId, reason);
            }
        }
    }
}
