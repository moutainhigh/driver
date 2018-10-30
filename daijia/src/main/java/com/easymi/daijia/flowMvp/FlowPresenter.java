package com.easymi.daijia.flowMvp;

import android.content.Context;
import android.content.Intent;

import com.easymi.common.entity.BuildPushData;
import com.easymi.component.Config;
import com.easymi.component.DJOrderStatus;
import com.easymi.component.loc.OnGetTrackDisListener;
import com.easymi.component.loc.OnGetTrackIdListener;
import com.easymi.component.loc.TrackHelper;
import com.easymi.component.network.NoErrSubscriberListener;
import com.easymi.component.utils.Log;

import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.INaviInfoCallback;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapModelCross;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.easymi.common.push.MQTTService;
import com.easymi.component.activity.NaviActivity;
import com.easymi.component.app.XApp;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.result.EmResult;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.NetUtil;
import com.easymi.component.utils.PhoneUtil;
import com.easymi.component.widget.LoadingButton;
import com.easymi.daijia.entity.DJOrder;
import com.easymi.daijia.result.DJOrderResult;
import com.easymi.daijia.result.OrderFeeResult;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import rx.Observable;

/**
 * Created by liuzihao on 2017/11/15.
 */

public class FlowPresenter implements FlowContract.Presenter, INaviInfoCallback, AMapNaviListener {

    private Context context;

    private FlowContract.View view;
    private FlowContract.Model model;

    private Timer timer;
    private TimerTask timerTask;

    public FlowPresenter(Context context, FlowContract.View view) {
        this.context = context;
        this.view = view;
        model = new FlowModel();
    }

    @Override
    public void acceptOrder(Long orderId, LoadingButton btn) {
        Observable<DJOrderResult> observable = model.doAccept(orderId);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, btn, djOrderResult -> {
            djOrderResult = orderResult2DJOrder(djOrderResult);
            updateDymOrder(djOrderResult.order);
            view.showOrder(djOrderResult.order);


        })));
    }

    @Override
    public void refuseOrder(Long orderId, String remark) {
        Observable<EmResult> observable = model.refuseOrder(orderId, remark);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, true, true, djOrderResult -> {
            DymOrder dymOrder = DymOrder.findByIDType(orderId, Config.DAIJIA);
            if (null != dymOrder) {
                dymOrder.delete();
            }
            view.refuseSuc();
        })));
    }

    @Override
    public void toStart(Long orderId, LoadingButton btn) {
        Observable<DJOrderResult> observable = model.toStart(orderId);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, btn, djOrderResult -> {

            djOrderResult = orderResult2DJOrder(djOrderResult);
            updateDymOrder(djOrderResult.order);
            view.showOrder(djOrderResult.order);

            DymOrder dymOrder = DymOrder.findByIDType(orderId, Config.DAIJIA);
            TrackHelper.getInstance().startTrack(0, dymOrder);

        })));
    }

    @Override
    public void arriveStart(Long orderId) {
        Observable<DJOrderResult> observable = model.arriveStart(orderId);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, true, false, djOrderResult -> {
            djOrderResult = orderResult2DJOrder(djOrderResult);
            updateDymOrder(djOrderResult.order);
            view.showOrder(djOrderResult.order);

            TrackHelper.getInstance().stopTrack();

        })));
    }

    @Override
    public void startWait(Long orderId, LoadingButton btn) {
        Observable<DJOrderResult> observable = model.startWait(orderId);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, btn, djOrderResult -> {
            djOrderResult = orderResult2DJOrder(djOrderResult);
            updateDymOrder(djOrderResult.order);
            view.showOrder(djOrderResult.order);

        })));
    }

    @Override
    public void startWait(Long orderId) {
        Observable<DJOrderResult> observable = model.startWait(orderId);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, true, true, djOrderResult -> {
            djOrderResult = orderResult2DJOrder(djOrderResult);
            updateDymOrder(djOrderResult.order);
            view.showOrder(djOrderResult.order);

            //开始等待时也开启track
            DymOrder dymOrder = DymOrder.findByIDType(orderId, Config.DAIJIA);
            if (dymOrder != null && dymOrder.toEndTrackId == 0) {
                TrackHelper.getInstance().startTrack(0, dymOrder);
                MQTTService.getInstance().startPushDisTimer(context, orderId, Config.DAIJIA);
            }

        })));
    }

    @Override
    public void startDrive(Long orderId, LoadingButton btn) {
        if (!PhoneUtil.checkGps(context)) {
            return;
        }
        Observable<DJOrderResult> observable = model.startDrive(orderId);
        view.getManager().add(observable.subscribe(new MySubscriber<>(context, btn, djOrderResult -> {
            djOrderResult = orderResult2DJOrder(djOrderResult);
            updateDymOrder(djOrderResult.order);
            view.showOrder(djOrderResult.order);

            DymOrder dymOrder = DymOrder.findByIDType(orderId, Config.DAIJIA);
            //开始出发时也开启track
            if (dymOrder != null && dymOrder.toEndTrackId == 0) {
                TrackHelper.getInstance().startTrack(0, dymOrder);
                MQTTService.getInstance().startPushDisTimer(context, orderId, Config.DAIJIA);
            }

        })));
    }

    @Override
    public void arriveDes(LoadingButton btn, DymOrder dymOrder, DJOrder djOrder) {

        Observable<DJOrderResult> observable = model.arriveDes(djOrder, dymOrder);
        view.getManager().add(observable.subscribe(new MySubscriber<>(context, btn, djOrderResult -> {
//            dymOrder.updateConfirm();
            djOrderResult = orderResult2DJOrder(djOrderResult);
            updateDymOrder(djOrderResult.order);
            view.showOrder(djOrderResult.order);

            TrackHelper.getInstance().stopTrack();
            MQTTService.getInstance().stopPushTimer();

        })));

    }

    @Override
    public void navi(LatLng latLng, String poi, Long orderId) {
        NaviLatLng start = new NaviLatLng(EmUtil.getLastLoc().latitude, EmUtil.getLastLoc().longitude);
        NaviLatLng end = new NaviLatLng(latLng.latitude, latLng.longitude);
        Intent intent = new Intent(context, NaviActivity.class);
        intent.putExtra("startLatlng", start);
        intent.putExtra("endLatlng", end);
        intent.putExtra("orderId", orderId);
        intent.putExtra("orderType", Config.DAIJIA);
        intent.putExtra(Config.NAVI_MODE, Config.WALK_TYPE);
        context.startActivity(intent);
    }

    @Override
    public void findOne(Long orderId) {
        Observable<DJOrderResult> observable = model.findOne(orderId);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, true, false, new HaveErrSubscriberListener<DJOrderResult>() {
            @Override
            public void onNext(DJOrderResult djOrderResult) {
                djOrderResult = orderResult2DJOrder(djOrderResult);
                updateDymOrder(djOrderResult.order);
                view.showOrder(djOrderResult.order);
            }

            @Override
            public void onError(int code) {
                view.showOrder(null);
            }
        })));
    }

    @Override
    public void findOne(Long orderId, boolean needShowProgress) {
        Observable<DJOrderResult> observable = model.findOne(orderId);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, needShowProgress, false, new HaveErrSubscriberListener<DJOrderResult>() {
            @Override
            public void onNext(DJOrderResult djOrderResult) {
                djOrderResult = orderResult2DJOrder(djOrderResult);
                updateDymOrder(djOrderResult.order);
                view.showOrder(djOrderResult.order);
            }

            @Override
            public void onError(int code) {
                view.showOrder(null);
            }
        })));
    }

    @Override
    public void changeEnd(Long orderId, Double lat, Double lng, String address) {
        Observable<DJOrderResult> observable = model.changeEnd(orderId, lat, lng, address);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, true, false, djOrderResult -> {
            djOrderResult = orderResult2DJOrder(djOrderResult);
            updateDymOrder(djOrderResult.order);
            view.showOrder(djOrderResult.order);
        })));
    }

    @Override
    public void cancelOrder(Long orderId, String remark) {
        Observable<EmResult> observable = model.cancelOrder(orderId, remark);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, true, true, djOrderResult -> {
            DymOrder dymOrder = DymOrder.findByIDType(orderId, Config.DAIJIA);
            if (null != dymOrder) {
                dymOrder.delete();
            }
            view.cancelSuc();
        })));
    }

    @Override
    public void onStopSpeaking() {
        XApp.getInstance().stopVoice();
        XApp.getInstance().clearVoiceList();
    }

    AMapNavi mAMapNavi;

    @Override
    public void routePlanByNavi(Double endLat, Double endLng) {
        if (null == mAMapNavi) {
            mAMapNavi = AMapNavi.getInstance(context);
            mAMapNavi.addAMapNaviListener(this);
        }

        NaviLatLng start = new NaviLatLng(EmUtil.getLastLoc().latitude, EmUtil.getLastLoc().longitude);
        NaviLatLng end = new NaviLatLng(endLat, endLng);

        DJOrder djOrder = view.getOrder();
        if (djOrder != null && (djOrder.orderStatus < DJOrderStatus.ARRIVAL_BOOKPLACE_ORDER)) {
            //到达预约地之前，调用步行方案
            mAMapNavi.calculateWalkRoute(start, end);
        } else {
            /**
             * congestion - 是否躲避拥堵
             avoidspeed - 不走高速
             cost - 避免收费
             hightspeed - 高速优先
             multipleRoute - 单路径or多路径
             */
            boolean congestion = true;
            boolean avoidspeed = false;
            boolean cost = false;
            boolean hightspeed = true;
            boolean multipleRoute = true;
            int strateFlag = mAMapNavi.strategyConvert(congestion, cost, avoidspeed, hightspeed, multipleRoute);

            List<NaviLatLng> startLs = new ArrayList<>();
            List<NaviLatLng> endLs = new ArrayList<>();

            startLs.add(start);
            endLs.add(end);
            mAMapNavi.calculateDriveRoute(startLs, endLs, null, strateFlag);
        }
    }

    RouteSearch routeSearch;

    @Override
    public void routePlanByRouteSearch(Double endLat, Double endLng) {
        if (null == routeSearch) {
            routeSearch = new RouteSearch(context);
            routeSearch.setRouteSearchListener(new RouteSearch.OnRouteSearchListener() {
                @Override
                public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

                }

                @Override
                public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int code) {
                    if (code == 1000) {
                        view.showPath(driveRouteResult);
                    }
                }

                @Override
                public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {
                }

                @Override
                public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

                }
            });
        }
        LatLonPoint start = new LatLonPoint(EmUtil.getLastLoc().latitude, EmUtil.getLastLoc().longitude);
        LatLonPoint end = new LatLonPoint(endLat, endLng);

        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(start, end);
        RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo,
                RouteSearch.DRIVING_MULTI_STRATEGY_FASTEST_SHORTEST, null, null, "");
        routeSearch.calculateDriveRouteAsyn(query);
    }

    @Override
    public void updateDymOrder(DJOrder djOrder) {
        DymOrder dymOrder = DymOrder.findByIDType(djOrder.orderId, djOrder.orderType);
        if (null == dymOrder) {
            if (null != djOrder.orderFee) {
                dymOrder = djOrder.orderFee;
                dymOrder.orderId = djOrder.orderId;
                dymOrder.orderType = djOrder.orderType;
                dymOrder.passengerId = djOrder.passengerId;
                dymOrder.orderStatus = djOrder.orderStatus;
            } else {
                dymOrder = new DymOrder(djOrder.orderId, djOrder.orderType,
                        djOrder.passengerId, djOrder.orderStatus);
            }
            dymOrder.save();
        } else {
            dymOrder.orderStatus = djOrder.orderStatus;
            dymOrder.updateStatus();
        }
        MQTTService.pushLoc(new BuildPushData(EmUtil.getLastLoc()));
    }

    @Override
    public void payOrder(Long orderId, String payType) {
        Observable<EmResult> observable = model.payOrder(orderId, payType);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, true, false, emResult -> {
            DymOrder dymOrder = DymOrder.findByIDType(orderId, Config.DAIJIA);
            if (null != dymOrder) {
                dymOrder.delete();
            }
            view.paySuc();
        })));
    }

    @Override
    public void stopNavi() {
        //since 1.6.0 不再在naviview destroy的时候自动执行AMapNavi.stopNavi();请自行执行
        if (null != mAMapNavi) {
            mAMapNavi.stopNavi();
            mAMapNavi.destroy();
        }
    }

    @Override
    public void getConsumerInfo(Long orderId) {
        view.getManager().add(model.consumerInfo(orderId).subscribe(new MySubscriber<>(context, true,
                false, consumerResult -> view.showConsumer(consumerResult.consumerInfo))));
    }


    @Override
    public void startTimer(Long orderId) {
        cancelTimer();
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                long lastFeeTime = XApp.getMyPreferences().getLong(Config.SP_LAST_GET_FEE_TIME, 0);
                if (System.currentTimeMillis() - lastFeeTime > 40 * 1000) {
                    if (NetUtil.getNetWorkState(context) != NetUtil.NETWORK_NONE) {
                        getOrderFee(orderId, 1);
                    }
                }
            }
        };
        timer.schedule(timerTask, 40 * 1000, 40 * 1000);
    }


    @Override
    public void cancelTimer() {
        if (timer != null) {
            timer.cancel();
        }
        if (timerTask != null) {
            timerTask.cancel();
        }
    }


    @Override
    public void getOrderFee(Long orderId, Integer isArrive) {
        DymOrder dymOrder = DymOrder.findByIDType(orderId, Config.DAIJIA);
        view.showFeeChanged(dymOrder);
    }


    @Override
    public DJOrderResult orderResult2DJOrder(DJOrderResult djOrderResult) {
        djOrderResult.order.addresses = djOrderResult.address;
        djOrderResult.order.orderFee = djOrderResult.orderFee;
        djOrderResult.order.coupon = djOrderResult.coupon;
        return djOrderResult;
    }

    @Override
    public void onInitNaviFailure() {

    }

    @Override
    public void onInitNaviSuccess() {

    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

    }

    @Override
    public void onGetNavigationText(int i, String s) {

    }

    @Override
    public void onArriveDestination(boolean b) {

    }

    @Override
    public void onStartNavi(int i) {

    }

    @Override
    public void onTrafficStatusUpdate() {

    }

    @Override
    public void onCalculateRouteSuccess(int[] ints) {
        Log.e("FlowerPresenter", "onCalculateRouteSuccess()");
        AMapNaviPath path;
        HashMap<Integer, AMapNaviPath> paths = mAMapNavi.getNaviPaths();
        if (null != paths && paths.size() != 0) {
            path = paths.get(ints[0]);
        } else {
            path = mAMapNavi.getNaviPath();
        }
        if (path != null) {
            view.showPath(ints, path);
            if (XApp.getMyPreferences().getBoolean(Config.SP_DEFAULT_NAVI, true)) {
                mAMapNavi.startNavi(NaviType.GPS);
                view.showLeft(path.getAllLength(), path.getAllTime());
            }
        }
    }

    @Override
    public void notifyParallelRoad(int i) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {

    }

    @Override
    public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {

    }

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {

    }

    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {

    }

    @Override
    public void onPlayRing(int i) {

    }

    @Override
    public void onCalculateRouteFailure(int i) {

    }

    /**
     * 重新算路前的回调
     */
    @Override
    public void onReCalculateRouteForYaw() {
        Log.e("FlowerPresenter", "onReCalculateRouteForYaw()");
        view.showReCal();
    }

    /**
     * 重新算路前的回调
     */
    @Override
    public void onReCalculateRouteForTrafficJam() {
        Log.e("FlowerPresenter", "onReCalculateRouteForTrafficJam()");
        view.showReCal();
    }

    @Override
    public void onArrivedWayPoint(int i) {

    }

    @Override
    public void onGpsOpenStatus(boolean b) {

    }

    /**
     * 导航信息更新
     *
     * @param naviInfo
     */
    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {
        view.showLeft(naviInfo.getPathRetainDistance(), naviInfo.getPathRetainTime());
    }

    @Override
    public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {

    }

    @Override
    public void updateCameraInfo(AMapNaviCameraInfo[] aMapNaviCameraInfos) {

    }

    @Override
    public void onServiceAreaUpdate(AMapServiceAreaInfo[] aMapServiceAreaInfos) {

    }

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {

    }

    @Override
    public void hideCross() {

    }

    @Override
    public void showModeCross(AMapModelCross aMapModelCross) {

    }

    @Override
    public void hideModeCross() {

    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

    }

    @Override
    public void hideLaneInfo() {

    }

    @Override
    public void onGetNavigationText(String s) {
//        XApp.getInstance().syntheticVoice(s, true);
    }

    @Override
    public void onEndEmulatorNavi() {

    }

    @Override
    public void onArriveDestination() {

    }

    public void onExitPage(int i) {

    }

    public void onReCalculateRoute(int i) {

    }

    public void showLaneInfo(AMapLaneInfo info) {

    }

    public void updateIntervalCameraInfo(AMapNaviCameraInfo info1, AMapNaviCameraInfo info2, int i) {

    }

}
