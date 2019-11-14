package com.easymin.carpooling.flowmvp;

import android.app.Dialog;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
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
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.DriveStep;
import com.easymi.common.CommApiService;
import com.easymi.common.entity.CarpoolOrder;
import com.easymi.common.receiver.OrderRefreshReceiver;
import com.easymi.component.Config;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxPayActivity;
import com.easymi.component.entity.BaseOrder;
import com.easymi.component.entity.EmLoc;
import com.easymi.component.entity.MySmoothMarker;
import com.easymi.component.loc.LocObserver;
import com.easymi.component.loc.LocReceiver;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.network.NoErrSubscriberListener;
import com.easymi.component.result.EmResult;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.utils.DensityUtil;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.Log;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.overlay.DrivingRouteOverlay;
import com.easymin.carpooling.R;
import com.easymin.carpooling.StaticVal;
import com.easymin.carpooling.adapter.LeftWindowAdapter;
import com.easymin.carpooling.entity.AllStation;
import com.easymin.carpooling.entity.MyStation;
import com.easymin.carpooling.entity.PincheOrder;
import com.easymin.carpooling.flowmvp.fragment.AcceptSendFragment;
import com.easymin.carpooling.flowmvp.fragment.ChangeSeqFragment;
import com.easymin.carpooling.flowmvp.fragment.CusListFragment;
import com.easymin.carpooling.flowmvp.fragment.FinishFragment;
import com.easymin.carpooling.flowmvp.fragment.NotStartFragment;
import com.easymin.carpooling.flowmvp.fragment.PasTicketsFragment;
import com.easymin.carpooling.receiver.CancelOrderReceiver;
import com.easymin.carpooling.receiver.OrderFinishReceiver;
import com.easymin.carpooling.receiver.ScheduleTurnReceiver;
import com.easymin.carpooling.widget.StationListDialog;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: FlowActivity
 *
 * @Author: hufeng
 * Date: 2018/12/24 下午1:10
 * Description: 订单执行流程
 * History:
 */
@Route(path = "/carpooling/FlowActivity")
public class FlowActivity extends RxPayActivity implements
        FlowContract.View,
        LocObserver,
        CancelOrderReceiver.OnCancelListener,
        ScheduleTurnReceiver.OnTurnListener,
        AMap.OnMapTouchListener,
        OrderFinishReceiver.OnFinishListener,
        AMap.OnMarkerClickListener,
        AMap.OnMapClickListener,
        OrderRefreshReceiver.OnRefreshOrderListener {

    CusToolbar cusToolbar;
    MapView mapView;
    FrameLayout fragmentFrame;

    AMap aMap;

    /**
     * 司机位置
     */
    private LatLng lastLatlng;
    /**
     * 首次进入司机marker
     */
    private Marker myFirstMarker;


    FlowPresenter presenter;

    /**
     * 当前加载fragment
     */
    Fragment currentFragment;
    /**
     * activity和fragment的通信接口
     */
    private ActFraCommBridge bridge;

    /**
     * 是否是手动操作地图
     */
    public static boolean isMapTouched = false;

    /**
     * 订单查询是否完成
     */
    private boolean isOrderLoadOk = false;

    PincheOrder pincheOrder;

    /**
     * 取消订单
     */
    private CancelOrderReceiver cancelOrderReceiver;

    /**
     * 转单
     */
    private ScheduleTurnReceiver scheduleTurnReceiver;

    /**
     * 刷新订单
     */
    private OrderRefreshReceiver orderRefreshReceiver;

    private boolean needJump;
    private MySmoothMarker smoothMoveMarker;

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_cp_flow;
    }


    @Override
    public void initViews(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        BaseOrder baseOrder = (BaseOrder) getIntent().getSerializableExtra("baseOrder");

        baseToCarPool(baseOrder);
        presenter = new FlowPresenter(this, this);

        mapView = findViewById(R.id.map_view);
        fragmentFrame = findViewById(R.id.fragment_frame);

        needJump = getIntent().getBooleanExtra("needJump", false);
        mapView.onCreate(savedInstanceState);
        initMap();
        initBridget();
        initFragment();

        presenter.queryOrderInTime(pincheOrder.scheduleId);
    }

    /**
     * 基本数据转拼车班次
     *
     * @param baseOrder
     */
    public void baseToCarPool(BaseOrder baseOrder) {
        pincheOrder = new PincheOrder();

        pincheOrder.orderId = baseOrder.scheduleId;
        pincheOrder.orderType = baseOrder.serviceType;
        pincheOrder.startAddress = baseOrder.bookAddress;
        pincheOrder.endAddress = baseOrder.destination;
        pincheOrder.bookTime = baseOrder.bookTime * 1000;//开始出发时间
        pincheOrder.minute = baseOrder.minute;//xx分钟前开始接人
        pincheOrder.startJierenTime = baseOrder.bookTime * 1000 - baseOrder.minute * 60 * 1000;//开始接人时间
        pincheOrder.startLatitude = baseOrder.startLatitude;
        pincheOrder.startLongitude = baseOrder.startLongitude;
        pincheOrder.endLatitude = baseOrder.endLatitude;
        pincheOrder.endLongitude = baseOrder.endLongitude;
        pincheOrder.status = baseOrder.scheduleStatus;
        pincheOrder.lineId = baseOrder.lineId;//线路Id
        pincheOrder.lineName = baseOrder.lineName;//线路名称
        pincheOrder.seats = baseOrder.seats;//剩余票数
        pincheOrder.scheduleId = baseOrder.scheduleId;
    }

    @Override
    public void changeToolbar(int flag, int index) {
        if (flag == StaticVal.TOOLBAR_NOT_START) {
            cusToolbar.setLeftBack(view -> finish());
            cusToolbar.setTitle("行程未开始");
            cusToolbar.setRightText("购票乘客", v -> bridge.toPasTickets());
        } else if (flag == StaticVal.TOOLBAR_CHANGE_ACCEPT) {
            cusToolbar.setLeftBack(view -> {
                presenter.qureyScheduleInfo(pincheOrder.scheduleId);
                if (myAllStation.scheduleStatus == PincheOrder.SCHEDULE_STATUS_NEW) {
                    bridge.toNotStart();
                } else if (myAllStation.scheduleStatus == PincheOrder.SCHEDULE_STATUS_RUNNING) {
                    bridge.toAcSend();
                }
            });
            if (!TextUtils.isEmpty(myAllStation.scheduleStationVoList.get(index).stationName)) {
                cusToolbar.setTitle(myAllStation.scheduleStationVoList.get(index).stationName);
            }
            cusToolbar.setRightGone();
        } else if (flag == StaticVal.TOOLBAR_FLOW) {
            cusToolbar.setLeftBack(view -> finish());
            cusToolbar.setRightText("查看规划", view -> {
                StationListDialog dialog = null;
                if (dialog == null) {
                    dialog = new StationListDialog(this, myAllStation);
                }
                dialog.setOnSelectListener(this::switchCompany).show();
            });
            CarpoolOrder current = acceptSendFragment.getCurrent();
            if (current == null) {
                ToastUtil.showMessage(this, "未获取到当前订单");
                finish();
            } else {
                if (current.status < CarpoolOrder.CARPOOL_STATUS_START) {
                    cusToolbar.setTitle("出发接人");
                } else if (current.status == CarpoolOrder.CARPOOL_STATUS_START) {
                    cusToolbar.setTitle("前往预约地");
                } else if (current.status == CarpoolOrder.CARPOOL_STATUS_ARRIVED) {
                    cusToolbar.setTitle("到达预约地");
                } else if (current.status == CarpoolOrder.CARPOOL_STATUS_RUNNING) {
                    cusToolbar.setTitle("前往目的地");
                }
            }
        } else if (flag == StaticVal.TOOLBAR_FINISH) {
            cusToolbar.setLeftBack(view -> finish());
            cusToolbar.setTitle("行程结束");
            cusToolbar.setRightGone();
        } else if (flag == StaticVal.TOOLBAR_PAS_TICKET) {
            cusToolbar.setLeftBack(view -> bridge.toNotStart());
            cusToolbar.setTitle("乘客列表");
            cusToolbar.setRightGone();
        }

    }

    @Override
    public void startOutSuc() {

        presenter.queryOrderInTime(pincheOrder.scheduleId);
    }

    @Override
    public void startSendSuc() {

    }

    @Override
    public void finishTaskSuc() {
        bridge.toFinished();
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
    PasTicketsFragment pasTicketsFragment;

    /**
     * 初始化fragment
     */
    @Override
    public void initFragment() {
        cusListFragment = new CusListFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("orderId", pincheOrder.orderId);
        bundle.putString("serviceType", pincheOrder.orderType);
        cusListFragment.setArguments(bundle);

        pasTicketsFragment = new PasTicketsFragment();
        pasTicketsFragment.setArguments(bundle);

        changeSeqFragment = new ChangeSeqFragment();
        changeSeqFragment.setArguments(bundle);

        notStartFragment = new NotStartFragment();
        Bundle orderBundle = new Bundle();
        orderBundle.putSerializable("pincheOrder", pincheOrder);
        notStartFragment.setArguments(orderBundle);
        notStartFragment.setBridge(bridge);

        acceptSendFragment = new AcceptSendFragment();
        acceptSendFragment.setArguments(bundle);
        acceptSendFragment.setBridge(bridge);

        finishFragment = new FinishFragment();
        finishFragment.setBridge(bridge);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
//        if (isOrderLoadOk) {
//            showFragmentByStatus();
//        }
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
            //手动调用上次位置 减少从北京跳过来的时间
            receiveLoc(emLoc);
            //移动镜头，首次镜头快速跳到指定位置
            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLatlng, 19));
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
            public void addMarker(LatLng latLng, int flag, int num, int ticketNumber, String phone) {
                FlowActivity.this.addMarker(latLng, flag, num, ticketNumber, phone);
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
                acceptSendFragment.setOrders(myAllStation);
            }

            @Override
            public void toChangeSeq(int flag) {
                changeSeqFragment.setAllStation(myAllStation);
                changeSeqFragment.setParam(bridge, flag);
                switchFragment(changeSeqFragment).commit();
            }

            @Override
            public void toFinished() {
                switchFragment(finishFragment).commit();
            }

            @Override
            public void toOrderList() {
                FlowActivity.this.finish();
            }

            @Override
            public void changeToolbar(int flag, int index) {
                FlowActivity.this.changeToolbar(flag, index);
            }

            @Override
            public void clearMap() {
                aMap.clear();
                if (smoothMoveMarker != null) {
                    smoothMoveMarker.destory();
                }
                smoothMoveMarker = null;
                initMap();
                //第一时间加上自身位置
                receiveLoc(EmUtil.getLastLoc());
            }

            @Override
            public void routePath(LatLng toLatlng) {
                Log.e("hufeng/routePath", "routePath22222");
                presenter.routePlanByRouteSearch(lastLatlng, null, toLatlng);
            }

            @Override
            public void routePath(LatLng startLatlng, List<LatLng> passLatlngs, LatLng endLatlng) {
                presenter.routePlanByRouteSearch(startLatlng, passLatlngs, endLatlng);
            }

            @Override
            public void startOutSet() {
                //时间到了 开始出发接人
//                ToastUtil.showMessage(FlowActivity.this,"行程开始");
                presenter.startOutSet(pincheOrder.orderId);
            }

            @Override
            public void gotoStart(CarpoolOrder carpoolOrder) {
                presenter.gotoStart(carpoolOrder);
            }

            @Override
            public void arriveStart(CarpoolOrder carpoolOrder) {

                presenter.arriveStart(carpoolOrder);
            }

            @Override
            public void acceptCustomer(CarpoolOrder carpoolOrder) {

                presenter.acceptCustomer(carpoolOrder);
            }

            @Override
            public void jumpAccept(CarpoolOrder carpoolOrder) {

                presenter.jumpAccept(carpoolOrder);
            }

            @Override
            public void arriveEnd(CarpoolOrder carpoolOrder) {
                presenter.arriveEnd(carpoolOrder);
            }

            @Override
            public void jumpSend(CarpoolOrder carpoolOrder) {

                presenter.jumpSend(carpoolOrder);
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

            @Override
            public void toPasTickets() {
                pasTicketsFragment.setParam(bridge);
                pasTicketsFragment.setAllStation(myAllStation);
                switchFragment(pasTicketsFragment).commit();
            }

            @Override
            public void onDialogClick(int type, long orderId, double money) {
                createDialog(type, orderId, money);
            }

            @Override
            public void changeOrderSequence(String orderIdSequence) {
                presenter.changeOrderSequence(orderIdSequence);
            }

            @Override
            public void finishTask(long scheduleId) {
                presenter.finishTask(scheduleId);
            }
        };
    }
    Dialog dialog = null;
    private void createDialog(int type, long orderId, double money) {
        if (type == 3) {
            showDialog(orderId, money);
        } else {
            if (dialog == null){
                dialog = new Dialog(this);
            }
            View view = LayoutInflater.from(this).inflate(type == 2 ? R.layout.cus_list_dialog_order : R.layout.cus_list_dialog_pay, null);
            dialog.setContentView(view);
            TextView dialogTvCancel = view.findViewById(R.id.dialog_tv_cancel);
            dialogTvCancel.setText(type == 2 ? "取消" : "取消订单");
            dialogTvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (type == 1) {
                        createDialog(2, orderId, money);
                    }
                    dialog.dismiss();
                }
            });
            TextView dialogTvAction = view.findViewById(R.id.dialog_tv_action);
            dialogTvAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (type == 2) {
                        cancelOrder(orderId);
                    } else {
                        showDialog(orderId, money);
                    }
                    dialog.dismiss();
                }
            });
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }

    private void cancelOrder(long orderId) {
        ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .cancelOrder(orderId, "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MySubscriber<EmResult>(this, true, false, new NoErrSubscriberListener<EmResult>() {
                    @Override
                    public void onNext(EmResult emResult) {
                        ToastUtil.showMessage(FlowActivity.this, "取消订单成功");
                        finish();
                    }
                }));
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
        //设置Marker可拖动
        markerOption.draggable(false);
        if (flag == StaticVal.MARKER_FLAG_START) {
            markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(getResources(), R.mipmap.ic_start)));
        } else if (flag == StaticVal.MARKER_FLAG_END) {
            markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(getResources(), R.mipmap.ic_end)));
        }
        // 将Marker设置为贴地显示，可以双指下拉地图查看效果
        //设置marker平贴地图效果
        markerOption.setFlat(true);
        aMap.addMarker(markerOption);

    }

    /**
     * 添加数字标号种类的marker
     *
     * @param latLng
     * @param flag
     */
    @Override
    public void addMarker(LatLng latLng, int flag, int num, int ticketNumber, String phone) {
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(latLng);
        //设置Marker可拖动
        markerOption.draggable(false);

        View view = LayoutInflater.from(this).inflate(R.layout.pc_sequence_marker, null);

        TextView tv_seq_num = view.findViewById(R.id.tv_seq_num);
        TextView tv_name = view.findViewById(R.id.tv_name);
        TextView tv_ticket_num = view.findViewById(R.id.tv_ticket_num);

        tv_seq_num.setText(num + "");
        tv_name.setText("尾号" + phone.substring(phone.length() - 4, phone.length()));
        tv_ticket_num.setText("人数:" + ticketNumber);

        if (flag == StaticVal.MARKER_FLAG_PASS_ENABLE) {
            tv_seq_num.setBackgroundResource(R.drawable.circle_dark_22);
        } else if (flag == StaticVal.MARKER_FLAG_PASS_DISABLE) {
            tv_seq_num.setBackgroundResource(R.drawable.circle_accent);
        }

        markerOption.icon(BitmapDescriptorFactory.fromView(view));

        // 将Marker设置为贴地显示，可以双指下拉地图查看效果
        markerOption.setFlat(true);
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
            int left = DensityUtil.dp2px(this, 100);
            int bottom = DensityUtil.dp2px(this, 280);
            int top = DensityUtil.dp2px(this, 100);
            aMap.animateCamera(CameraUpdateFactory.newLatLngBoundsRect(bounds, left, left, top, bottom));
        } else {

            aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
                    bounds,
                    (int) (DensityUtil.getDisplayWidth(this) / 1.5),
                    (int) (DensityUtil.getDisplayWidth(this) / 2),
                    0));
        }

        //后续可能会使用这个latLngs 所以移除加入的上次位置
        latLngs.remove(lastLatlng);
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


    /**
     * 导航路径展示
     *
     * @param ints
     * @param path
     */
    @Override
    public void showPath(int[] ints, AMapNaviPath path) {
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
//        overlay.setRouteWidth(5);
        drivingRouteOverlay.setIsColorfulline(false);
        drivingRouteOverlay.setNodeIconVisibility(false);//隐藏转弯的节点
        drivingRouteOverlay.addToMap();

        float dis = 0;
        float time = 0;

        for (DriveStep step : result.getPaths().get(0).getSteps()) {
            dis += step.getDistance();
            time += step.getDuration();
        }
        showLeft((int) dis, (int) time);
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
    public void gotoStartSuc(CarpoolOrder carpoolOrder) {
        presenter.queryOrderInTime(pincheOrder.scheduleId);
    }

    @Override
    public void arriveStartSuc(CarpoolOrder carpoolOrder) {
        /**
         * 存下预约订单的时间戳
         */
        XApp.getEditor().putLong(Config.PC_BOOKTIME, System.currentTimeMillis() + carpoolOrder.waitMinute * 60 * 1000).apply();

        presenter.queryOrderInTime(pincheOrder.scheduleId);
    }

    @Override
    public void acceptCustomerSuc(CarpoolOrder carpoolOrder) {
        presenter.queryOrderInTime(pincheOrder.scheduleId);
    }


    @Override
    public void jumpAcceptSuc(CarpoolOrder carpoolOrder) {
        presenter.queryOrderInTime(pincheOrder.scheduleId);
    }

    @Override
    public void arriveEndSuc(CarpoolOrder carpoolOrder) {
        presenter.queryOrderInTime(pincheOrder.scheduleId);
    }

    @Override
    public void jumpSendSuc(CarpoolOrder carpoolOrder) {
        presenter.queryOrderInTime(pincheOrder.scheduleId);
    }

    @Override
    public void showFragmentByStatus() {
        if (myAllStation.scheduleStatus == PincheOrder.SCHEDULE_STATUS_NEW) {
            //未开始
            if (needJump) {
                needJump = false;
                bridge.toPasTickets();
            } else {
                if (currentFragment instanceof ChangeSeqFragment) {
                    bridge.toChangeSeq(((ChangeSeqFragment) currentFragment).getFlag());
                } else {
                    bridge.toNotStart();
                }
            }
        } else if (myAllStation.scheduleStatus == PincheOrder.SCHEDULE_STATUS_RUNNING) {
//            if (currentFragment instanceof ChangeSeqFragment) {
//                bridge.toChangeSeq(((ChangeSeqFragment) currentFragment).getFlag());
//            } else {
                bridge.toAcSend();
//            }

        } else if (myAllStation.scheduleStatus == PincheOrder.SCHEDULE_STATUS_FINISH) {
            bridge.toFinished();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //添加位置订阅
        LocReceiver.getInstance().addObserver(this);

        cancelOrderReceiver = new CancelOrderReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Config.BROAD_CANCEL_ORDER);
        filter.addAction(Config.BROAD_BACK_ORDER);
        registerReceiver(cancelOrderReceiver, filter, EmUtil.getBroadCastPermission(), null);

        scheduleTurnReceiver = new ScheduleTurnReceiver(this);
        IntentFilter filter1 = new IntentFilter();
        filter1.addAction(Config.SCHEDULE_FINISH);
        registerReceiver(scheduleTurnReceiver, filter1, EmUtil.getBroadCastPermission(), null);

        orderRefreshReceiver = new OrderRefreshReceiver(this);
        registerReceiver(orderRefreshReceiver, new IntentFilter(Config.ORDER_REFRESH), EmUtil.getBroadCastPermission(), null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //取消位置订阅
        LocReceiver.getInstance().deleteObserver(this);

        unregisterReceiver(cancelOrderReceiver);
        unregisterReceiver(scheduleTurnReceiver);
        unregisterReceiver(orderRefreshReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        presenter.cancelQueryInTime();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
        if (smoothMoveMarker != null) {
            smoothMoveMarker.destory();
        }
    }

    @Override
    public void onPaySuc() {
        ToastUtil.showMessage(this, "代付成功");
        presenter.qureyScheduleInfo(pincheOrder.scheduleId);
    }

    @Override
    public void onPayFail() {

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


    boolean delayAnimate = true;

    @Override
    public void receiveLoc(EmLoc location) {
        if (null == location) {
            return;
        }
        Log.e("locPos", "bearing 2 >>>>" + location.bearing);
        LatLng latLng = new LatLng(location.latitude, location.longitude);

        if (null == smoothMoveMarker) { //首次进入
            MarkerOptions markerOption = new MarkerOptions();
            markerOption.position(latLng);
            markerOption.draggable(false);
            markerOption.infoWindowEnable(true);
            markerOption.anchor(0.5f, 0.5f);
            markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(getResources(), R.mipmap.ic_flow_my_pos)));
            markerOption.rotateAngle(360.0F - location.bearing + aMap.getCameraPosition().bearing);
            smoothMoveMarker = new MySmoothMarker(aMap, markerOption);
            Marker marker = smoothMoveMarker.getMarker();
            if (null != marker) {
                marker.setClickable(false);
            }
        } else {
            Marker marker = smoothMoveMarker.getMarker();
            if (null != marker) {
                marker.setRotateAngle(360.0F - location.bearing + aMap.getCameraPosition().bearing);
            }
            smoothMoveMarker.startMove(latLng, 3000, true);
        }

        if (pincheOrder.status == PincheOrder.SCHEDULE_STATUS_RUNNING) {
            if (!isMapTouched && currentFragment instanceof AcceptSendFragment) {
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19), delayAnimate ? Config.NORMAL_LOC_TIME : 0, null);
                delayAnimate = true;
            }
        }
        lastLatlng = latLng;
    }

    @Override
    public void onFinishOrder(long orderId, String orderType) {
        presenter.queryOrderInTime(pincheOrder.scheduleId);
    }

    /**
     * 取消订单
     */
    @Override
    public void onCancelOrder(long orderId, String orderType, String msg) {
        if (orderType.equals(Config.CARPOOL)) {
            presenter.queryOrderInTime(pincheOrder.scheduleId);
        }
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
        if (null != smoothMoveMarker) {
            smoothMoveMarker.getMarker().hideInfoWindow();
        }
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

    @Override
    public void onTurnOrder(long scheduleId, String orderType, String msg) {
        if (scheduleId == pincheOrder.scheduleId) {
            presenter.queryOrderInTime(pincheOrder.scheduleId);
        }
    }


////////////////

    AllStation myAllStation;

    @Override
    public void scheduleInfo(AllStation allStation) {
        if (allStation == null) {
            ToastUtil.showMessage(this, "未查询到时段信息");
            return;
        }
        if (allStation.scheduleStationVoList == null || allStation.scheduleStationVoList.size() == 0) {
            ToastUtil.showMessage(this, "此时段没有任何上下车点");
            return;
        }
        myAllStation = allStation;
        showFragmentByStatus();
    }

    @Override
    public void changeSequenceSuc() {

    }

    /**
     * 选择站点后的处理
     *
     * @param index
     */
    public void switchCompany(int index) {
        bridge.toChangeSeq(index);
    }

    @Override
    public void onRefreshOrder() {
        //新指派拼车订单后的推送，收到后刷新数据
        presenter.qureyScheduleInfo(pincheOrder.scheduleId);
    }
}
