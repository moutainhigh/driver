package com.easymin.custombus.mvp;

import android.content.Context;

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
import com.autonavi.tbt.TrafficFacilityInfo;
import com.easymi.component.Config;
import com.easymi.component.app.XApp;
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.result.EmResult2;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.Log;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.LoadingButton;
import com.easymin.custombus.entity.Customer;
import com.easymin.custombus.entity.OrdersResult;
import com.easymin.custombus.entity.StationResult;
import com.easymin.custombus.entity.TimeResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: FlowPresenter
 * @Author: hufeng
 * @Date: 2019/2/18 下午5:14
 * @Description:
 * @History:
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

    /**
     * 导航组件
     */
    AMapNavi mAMapNavi;

    @Override
    public void onInitNaviSuccess() {

    }

    @Override
    public void onTrafficStatusUpdate() {

    }

    @Override
    public void onGetNavigationText(int i, String s) {

    }

    @Override
    public void onEndEmulatorNavi() {

    }

    @Override
    public void onArriveDestination() {

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
        Log.e("FlowerPresenter", "onNaviInfoUpdate");
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
    public void onInitNaviFailure() {

    }

    @Override
    public void onGetNavigationText(String s) {

    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

    }

    @Override
    public void onArriveDestination(boolean b) {

    }

    @Override
    public void onStartNavi(int i) {

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
            view.showLeft(path.getAllLength(), path.getAllTime());
            if (XApp.getMyPreferences().getBoolean(Config.SP_DEFAULT_NAVI, true)) {
                mAMapNavi.startNavi(NaviType.GPS);
            }
        }
    }

    @Override
    public void onCalculateRouteFailure(int i) {

    }

    @Override
    public void onStopSpeaking() {

    }

    @Override
    public void onReCalculateRoute(int i) {

    }

    @Override
    public void onExitPage(int i) {

    }

    @Override
    public void routePlanByNavi(Double endLat, Double endLng) {
        if (null == mAMapNavi) {
            mAMapNavi = AMapNavi.getInstance(context);
            mAMapNavi.addAMapNaviListener(this);
        }

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
        mAMapNavi.calculateDriveRoute(startLs, endLs, null, strateFlag);
    }

    @Override
    public void stopNavi() {
        //since 1.6.0 不再在naviview destroy的时候自动执行AMapNavi.stopNavi();请自行执行
        if (null != mAMapNavi) {
            mAMapNavi.stopNavi();
            mAMapNavi.destroy();
        }
    }

    /**
     * 开始行程
     *
     * @param scheduleId
     * @param button
     */
    @Override
    public void startStation(long scheduleId, LoadingButton button) {
        view.getManager().add(model.startStation(scheduleId).subscribe(new MySubscriber<>(context,
                button,
                new HaveErrSubscriberListener<EmResult2<Object>>() {

                    @Override
                    public void onNext(EmResult2<Object> result) {
                        view.dealSuccese();
                    }

                    @Override
                    public void onError(int code) {

                    }
                })));
    }

    @Override
    public void arriveStation(long scheduleId, long stationId) {
        view.getManager().add(model.arriveStation(scheduleId, stationId).subscribe(new MySubscriber<>(context,
                false,
                false,
                new HaveErrSubscriberListener<EmResult2<Object>>() {

                    @Override
                    public void onNext(EmResult2<Object> result) {
                        view.dealSuccese();
                    }

                    @Override
                    public void onError(int code) {
                        ToastUtil.showMessage(context, "获取班车路线失败！");
                    }
                })));
    }

    @Override
    public void endStation(long scheduleId, LoadingButton button) {
        view.getManager().add(model.endStation(scheduleId).subscribe(new MySubscriber<>(context,
                button,
                new HaveErrSubscriberListener<EmResult2<Object>>() {

                    @Override
                    public void onNext(EmResult2<Object> result) {
                        view.finishActivity();
                    }

                    @Override
                    public void onError(int code) {

                    }
                })));
    }

    @Override
    public void findBusOrderById(long id) {
        view.getManager().add(model.findBusOrderById(id).subscribe(new MySubscriber<>(context,
                false,
                false,
                new HaveErrSubscriberListener<StationResult>() {

                    @Override
                    public void onNext(StationResult result) {
//                        if (result.getCode() == 1) {
//                            for (StationResult busStation : result.getData().stationVos) {
//                                if (!StationResult.existsById(busStation.id, result.getData().id)) {
//                                    busStation.scheduleId = result.getData().id;
//                                    busStation.orderType = Config.COUNTRY;
//                                    busStation.save();
//                                }
//                            }
//                            List<BusStationsBean> stations = BusStationsBean.findAll();
//                            for (BusStationsBean station : stations) {
//                                boolean isExist = false;
//                                for (BusStationsBean order : result.getData().stationVos) {
//                                    if (order.id == station.id && result.getData().id == station.scheduleId) {
//                                        isExist = true;
//                                        break;
//                                    }
//                                }
//                                if (!isExist) {
//                                    station.delete(station.id,station.scheduleId);
//                                }
//                            }
//                        }
                        if (result.getCode() == 1) {
                            view.showBusLineInfo(result.data);
                        }
                    }

                    @Override
                    public void onError(int code) {

                    }
                })));
    }

    @Override
    public void toNextStation(long scheduleId, long stationId) {
        view.getManager().add(model.toNextStation(scheduleId, stationId).subscribe(new MySubscriber<>(context,
                false,
                false,
                new HaveErrSubscriberListener<EmResult2<Object>>() {

                    @Override
                    public void onNext(EmResult2<Object> result) {
                         view.dealSuccese();
                    }

                    @Override
                    public void onError(int code) {

                    }
                })));
    }

    @Override
    public void chechTickets(long id) {
        view.getManager().add(model.chechTickets(id).subscribe(new MySubscriber<>(context,
                false,
                false,
                new HaveErrSubscriberListener<TimeResult>() {

                    @Override
                    public void onNext(TimeResult result) {
                        if (result.getCode() == 1) {
                            view.showcheckTime(result.object);
                        }
                    }

                    @Override
                    public void onError(int code) {

                    }
                })));
    }

    @Override
    public void queryOrders(long scheduleId, long stationId) {
        view.getManager().add(model.queryOrders(scheduleId, stationId).subscribe(new MySubscriber<>(context,
                false,
                false,
                new HaveErrSubscriberListener<OrdersResult>() {

                    @Override
                    public void onNext(OrdersResult result) {
                        if (result.getCode() == 1) {
                            view.showOrders(result.data);
                        }
                    }

                    @Override
                    public void onError(int code) {

                    }
                })));
    }

    @Override
    public void queryByRideCode(String rideCode) {
        view.getManager().add(model.queryByRideCode(rideCode).subscribe(new MySubscriber<>(context,
                true,
                true,
                new HaveErrSubscriberListener<EmResult2<Customer>>() {

                    @Override
                    public void onNext(EmResult2<Customer> result) {
                        if (result.getCode() == 1) {
                            view.succeseOrder(result.getData());
                        }
                    }

                    @Override
                    public void onError(int code) {

                    }
                })));
    }

    @Override
    public void checkRideCode(String rideCode, LoadingButton button) {
        view.getManager().add(model.checkRideCode(rideCode).subscribe(new MySubscriber<>(context,
                button,
                new HaveErrSubscriberListener<EmResult2<Object>>() {

                    @Override
                    public void onNext(EmResult2<Object> result) {
                        if (result.getCode() == 1) {
                            view.dealSuccese();
                        }
                    }

                    @Override
                    public void onError(int code) {

                    }
                })));
    }
}
