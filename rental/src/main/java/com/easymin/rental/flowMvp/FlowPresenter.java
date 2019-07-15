package com.easymin.rental.flowMvp;

import android.content.Context;
import android.content.Intent;

import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapCalcRouteResult;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapModelCross;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.AMapNaviRouteNotifyData;
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
import com.easymi.component.Config;
import com.easymi.component.activity.NaviActivity;
import com.easymi.component.app.XApp;
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.result.EmResult;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.Log;
import com.easymi.component.widget.LoadingButton;
import com.easymin.rental.result.OrderResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: FlowPresenter
 *
 * @Author: shine
 * Date: 2018/12/18 下午1:59
 * Description:
 * History:
 */
public class FlowPresenter implements FlowContract.Presenter, AMapNaviListener {

    private Context context;
    private FlowContract.View view;
    private FlowContract.Model model;

    AMapNavi mAMapNavi;

    public FlowPresenter(Context context, FlowContract.View view) {
        this.context = context;
        this.view = view;
        model = new FlowModel(context);
    }


    private double endLat;
    private double endLng;
    private boolean isInit;
    private boolean isCalculate;

    @Override
    public void routePlanByNavi(Double endLat, Double endLng) {
        if (isInit || isCalculate) {
            return;
        }
        this.endLat = endLat;
        this.endLng = endLng;

        if (null == mAMapNavi) {
            mAMapNavi = AMapNavi.getInstance(context);
            mAMapNavi.addAMapNaviListener(this);
            isInit = true;
        } else {
            onInitNaviSuccess();
        }

    }

    private void calculateRoute() {

        if (isCalculate) {
            return;
        }
        if (mAMapNavi != null) {
            int strateFlag = mAMapNavi.strategyConvert(
                    XApp.getMyPreferences().getBoolean(Config.SP_CONGESTION, false),
                    XApp.getMyPreferences().getBoolean(Config.SP_AVOID_HIGH_SPEED, false),
                    XApp.getMyPreferences().getBoolean(Config.SP_COST, false),
                    XApp.getMyPreferences().getBoolean(Config.SP_HIGHT_SPEED, false),
                    false);

            NaviLatLng start = new NaviLatLng(EmUtil.getLastLoc().latitude, EmUtil.getLastLoc().longitude);
            NaviLatLng end = new NaviLatLng(endLat, endLng);

            List<NaviLatLng> startLs = new ArrayList<>();
            List<NaviLatLng> endLs = new ArrayList<>();

            startLs.add(start);
            endLs.add(end);
            isCalculate = true;
            mAMapNavi.calculateDriveRoute(startLs, endLs, null, strateFlag);
        }
    }

    RouteSearch routeSearch;

    @Override
    public void routePlanByRouteSearch(LatLng start, List<LatLng> latLngs, LatLng end) {
        if (start == null || end == null) {
            return;
        }
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
        LatLonPoint startPoint = new LatLonPoint(start.latitude, start.longitude);
        LatLonPoint endPoint = new LatLonPoint(end.latitude, end.longitude);

        List<LatLonPoint> latLonPoints = new ArrayList<>();
        if (latLngs != null && latLngs.size() != 0) {
            for (LatLng latLng : latLngs) {
                LatLonPoint point = new LatLonPoint(latLng.latitude, latLng.longitude);
                latLonPoints.add(point);
            }
        }

        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(startPoint, endPoint);
        RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo,
                RouteSearch.DRIVING_MULTI_STRATEGY_FASTEST_SHORTEST, latLonPoints, null, "");
        routeSearch.calculateDriveRouteAsyn(query);
    }

    @Override
    public void navi(LatLng latLng, Long orderId) {
        NaviLatLng start = new NaviLatLng(EmUtil.getLastLoc().latitude, EmUtil.getLastLoc().longitude);
        NaviLatLng end = new NaviLatLng(latLng.latitude, latLng.longitude);
        Intent intent = new Intent(context, NaviActivity.class);
        intent.putExtra("startLatlng", start);
        intent.putExtra("endLatlng", end);
        intent.putExtra("orderId", orderId);
        intent.putExtra("orderType", Config.RENTAL);

        intent.putExtra(Config.NAVI_MODE, Config.DRIVE_TYPE);

        stopNavi();//停止当前页面的导航，在到导航页时重新初始化导航
        context.startActivity(intent);
    }

    @Override
    public void stopNavi() {
        //since 1.6.0 不再在naviview destroy的时候自动执行AMapNavi.stopNavi();请自行执行
        if (null != mAMapNavi) {
            mAMapNavi.stopNavi();
            mAMapNavi.destroy();
            mAMapNavi = null;
        }
    }


    @Override
    public void findOne(long orderId) {
        Observable<OrderResult> observable = model.findOne(orderId);
        view.getManager().add(observable.subscribe(new MySubscriber<>(context, true, true,
                new HaveErrSubscriberListener<OrderResult>() {
                    @Override
                    public void onNext(OrderResult orderResult) {
                        view.showOrder(orderResult.data);
                    }

                    @Override
                    public void onError(int code) {
                        view.showOrder(null);
                    }
                })));
    }

    @Override
    public void changeStauts(Long orderId, int status) {
        Observable<EmResult> observable = model.changeStauts(orderId, status);
        view.getManager().add(observable.subscribe(new MySubscriber<>(context, false, false,
                new HaveErrSubscriberListener<EmResult>() {
                    @Override
                    public void onNext(EmResult emResult) {
                        if (emResult.getCode() == 1) {
                            findOne(orderId);
                        }
                    }

                    @Override
                    public void onError(int code) {
                        Log.e("hufeng/code", "errorCode:" + code);
                    }
                })));
    }

    @Override
    public void orderConfirm(long orderId, long version, LoadingButton button) {
        Observable<EmResult> observable = model.orderConfirm(orderId, version);
        view.getManager().add(observable.subscribe(new MySubscriber<>(context, button,
                new HaveErrSubscriberListener<EmResult>() {
                    @Override
                    public void onNext(EmResult emResult) {
                        if (emResult.getCode() == 1) {
                            view.toFinish();
                        }
                    }

                    @Override
                    public void onError(int code) {
                        Log.e("hufeng/code", "errorCode:" + code);
                    }
                })));
    }


    @Override
    public void onInitNaviFailure() {
        isInit = false;
        stopNavi();

    }

    @Override
    public void onInitNaviSuccess() {
        isInit = false;
        calculateRoute();
    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

    }

    @Override
    public void onGetNavigationText(int i, String s) {

    }

    @Override
    public void onStartNavi(int i) {

    }

    @Override
    public void onTrafficStatusUpdate() {

    }

    @Override
    public void onCalculateRouteSuccess(int[] ints) {
        isCalculate = false;
        if (mAMapNavi != null) {
            AMapNaviPath path;
            HashMap<Integer, AMapNaviPath> paths = mAMapNavi.getNaviPaths();
            if (null != paths && paths.size() != 0) {
                path = paths.get(ints[0]);
            } else {
                path = mAMapNavi.getNaviPath();
            }
            if (path != null) {
                view.showPath(ints, path);
                view.showLeft(path.getAllLength(), path.getAllTime());
                if (XApp.getMyPreferences().getBoolean(Config.SP_DEFAULT_NAVI, true)) {
//                    mAMapNavi.startNavi(NaviType.GPS);
                }
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
    public void onCalculateRouteSuccess(AMapCalcRouteResult aMapCalcRouteResult) {

    }

    @Override
    public void onCalculateRouteFailure(AMapCalcRouteResult aMapCalcRouteResult) {

    }

    @Override
    public void onNaviRouteNotify(AMapNaviRouteNotifyData aMapNaviRouteNotifyData) {

    }

    @Override
    public void onCalculateRouteFailure(int i) {
        isCalculate = false;
    }

    /**
     * 重新算路前的回调
     */
    @Override
    public void onReCalculateRouteForYaw() {

    }

    /**
     * 重新算路前的回调
     */
    @Override
    public void onReCalculateRouteForTrafficJam() {

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
    public void updateIntervalCameraInfo(AMapNaviCameraInfo aMapNaviCameraInfo, AMapNaviCameraInfo aMapNaviCameraInfo1, int i) {

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
    public void showLaneInfo(AMapLaneInfo aMapLaneInfo) {

    }

    @Override
    public void hideLaneInfo() {

    }

    @Override
    public void onGetNavigationText(String s) {

    }

    @Override
    public void onEndEmulatorNavi() {

    }

    @Override
    public void onArriveDestination() {

    }

}