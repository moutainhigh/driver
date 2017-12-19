package com.easymi.daijia.flowMvp;

import android.content.Context;
import android.content.Intent;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.INaviInfoCallback;
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
import com.easymi.component.Config;
import com.easymi.component.activity.NaviActivity;
import com.easymi.component.app.XApp;
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.widget.LoadingButton;
import com.easymi.daijia.entity.DJOrder;
import com.easymi.daijia.result.DJOrderResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;

/**
 * Created by liuzihao on 2017/11/15.
 */

public class FlowPresenter implements FlowContract.Presenter, INaviInfoCallback, AMapNaviListener {

    private Context context;

    private FlowContract.View view;
    private FlowContract.Model model;

    public FlowPresenter(Context context, FlowContract.View view) {
        this.context = context;
        this.view = view;
        model = new FlowModel();
    }

    @Override
    public void acceptOrder(Long orderId, LoadingButton btn) {
        Observable<DJOrderResult> observable = model.doAccept(orderId);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, btn, new HaveErrSubscriberListener<DJOrderResult>() {
            @Override
            public void onNext(DJOrderResult djOrderResult) {
                djOrderResult.order.addresses = djOrderResult.address;
                view.showOrder(djOrderResult.order);
            }

            @Override
            public void onError(int code) {
                view.showOrder(null);
            }
        })));
    }

    @Override
    public void refuseOrder(Long orderId, String remark) {
        Observable<DJOrderResult> observable = model.refuseOrder(orderId, remark);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, true, true, new HaveErrSubscriberListener<DJOrderResult>() {
            @Override
            public void onNext(DJOrderResult djOrderResult) {
                view.refuseSuc();
            }

            @Override
            public void onError(int code) {
                view.showOrder(null);
            }
        })));
    }

    @Override
    public void toStart(Long orderId, LoadingButton btn) {
        Observable<DJOrderResult> observable = model.toStart(orderId);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, btn, new HaveErrSubscriberListener<DJOrderResult>() {
            @Override
            public void onNext(DJOrderResult djOrderResult) {
                djOrderResult.order.addresses = djOrderResult.address;
                view.showOrder(djOrderResult.order);
            }

            @Override
            public void onError(int code) {
                view.showOrder(null);
            }
        })));
    }

    @Override
    public void arriveStart(Long orderId) {
        Observable<DJOrderResult> observable = model.arriveStart(orderId);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, true, false, new HaveErrSubscriberListener<DJOrderResult>() {
            @Override
            public void onNext(DJOrderResult djOrderResult) {
                djOrderResult.order.addresses = djOrderResult.address;
                view.showOrder(djOrderResult.order);
            }

            @Override
            public void onError(int code) {
                view.showOrder(null);
            }
        })));
    }

    @Override
    public void startWait(Long orderId, LoadingButton btn) {
        Observable<DJOrderResult> observable = model.startWait(orderId);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, btn, new HaveErrSubscriberListener<DJOrderResult>() {
            @Override
            public void onNext(DJOrderResult djOrderResult) {
                djOrderResult.order.addresses = djOrderResult.address;
                view.showOrder(djOrderResult.order);
            }

            @Override
            public void onError(int code) {
                view.showOrder(null);
            }
        })));
    }

    @Override
    public void startDrive(Long orderId, LoadingButton btn) {
        Observable<DJOrderResult> observable = model.startDrive(orderId);
        view.getManager().add(observable.subscribe(new MySubscriber<>(context, btn, new HaveErrSubscriberListener<DJOrderResult>() {
            @Override
            public void onNext(DJOrderResult djOrderResult) {
                djOrderResult.order.addresses = djOrderResult.address;
                view.showOrder(djOrderResult.order);
            }

            @Override
            public void onError(int code) {
                view.showOrder(null);
            }
        })));
    }

    @Override
    public void arriveDes(DJOrder djOrder, LoadingButton btn) {
        Observable<DJOrderResult> observable = model.arriveDes(djOrder);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, btn, new HaveErrSubscriberListener<DJOrderResult>() {
            @Override
            public void onNext(DJOrderResult djOrderResult) {
                djOrderResult.order.addresses = djOrderResult.address;
                view.showOrder(djOrderResult.order);
            }

            @Override
            public void onError(int code) {
                view.showOrder(null);
            }
        })));
    }

    @Override
    public void navi(LatLng latLng, String poi) {
//        String startPoi = EmUtil.getLastLoc().poiName;
//        LatLng startLatlng = new LatLng(EmUtil.getLastLoc().latitude, EmUtil.getLastLoc().longitude);
//        AmapNaviPage.getInstance()
//                .showRouteActivity(
//                        context,
//                        new AmapNaviParams(new Poi(startPoi, startLatlng, ""),
//                                null,
//                                new Poi(poi, latLng, ""),
//                                AmapNaviType.DRIVER),
//                        this);

        NaviLatLng start = new NaviLatLng(EmUtil.getLastLoc().latitude, EmUtil.getLastLoc().longitude);
        NaviLatLng end = new NaviLatLng(latLng.latitude, latLng.longitude);
        Intent intent = new Intent(context, NaviActivity.class);
        intent.putExtra("startLatlng", start);
        intent.putExtra("endLatlng", end);
        context.startActivity(intent);
    }

    @Override
    public void findOne(Long orderId) {
        Observable<DJOrderResult> observable = model.findOne(orderId);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, true, false, new HaveErrSubscriberListener<DJOrderResult>() {
            @Override
            public void onNext(DJOrderResult djOrderResult) {
                djOrderResult.order.addresses = djOrderResult.address;
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

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, true, false, new HaveErrSubscriberListener<DJOrderResult>() {
            @Override
            public void onNext(DJOrderResult djOrderResult) {
                djOrderResult.order.addresses = djOrderResult.address;
                view.showOrder(djOrderResult.order);
            }

            @Override
            public void onError(int code) {
                view.showOrder(null);
            }
        })));
    }

    @Override
    public void cancelOrder(Long orderId, String remark) {
        Observable<DJOrderResult> observable = model.cancelOrder(orderId, remark);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, true, true, new HaveErrSubscriberListener<DJOrderResult>() {
            @Override
            public void onNext(DJOrderResult djOrderResult) {
                view.cancelSuc();
            }

            @Override
            public void onError(int code) {
                view.showOrder(null);
            }
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

        NaviLatLng start = new NaviLatLng(EmUtil.getLastLoc().latitude, EmUtil.getLastLoc().longitude);
        NaviLatLng end = new NaviLatLng(endLat, endLng);

        List<NaviLatLng> startLs = new ArrayList<>();
        List<NaviLatLng> endLs = new ArrayList<>();

        startLs.add(start);
        endLs.add(end);
        mAMapNavi.calculateDriveRoute(startLs, endLs, null, strateFlag);
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
        HashMap<Integer, AMapNaviPath> paths = mAMapNavi.getNaviPaths();
        if (null != paths && paths.size() != 0) {
            AMapNaviPath path = paths.get(ints[0]);
            if (path != null) {
                view.showPath(ints, path);
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

    @Override
    public void onReCalculateRouteForYaw() {

    }

    @Override
    public void onReCalculateRouteForTrafficJam() {

    }

    @Override
    public void onArrivedWayPoint(int i) {

    }

    @Override
    public void onGpsOpenStatus(boolean b) {

    }

    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {

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
        XApp.getInstance().syntheticVoice(s, true);
    }

    @Override
    public void onEndEmulatorNavi() {

    }

    @Override
    public void onArriveDestination() {

    }


}
