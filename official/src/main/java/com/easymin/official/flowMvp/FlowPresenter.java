package com.easymin.official.flowMvp;

import android.content.Context;
import android.content.Intent;

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
import com.easymi.common.entity.BuildPushData;
import com.easymi.common.push.MqttManager;
import com.easymi.component.Config;
import com.easymi.component.activity.NaviActivity;
import com.easymi.component.app.XApp;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.result.EmResult;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.Log;
import com.easymi.component.utils.PhoneUtil;
import com.easymi.component.utils.ToastUtil;
import com.easymin.official.entity.GovOrder;
import com.easymin.official.result.GovOrderResult;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: FlowPresenter
 * @Author: hufeng
 * @Date: 2019/3/26 上午9:02
 * @Description:
 * @History:
 */
public class FlowPresenter implements FlowContract.Presenter, INaviInfoCallback, AMapNaviListener {

    /**
     * 导航相关
     */
    AMapNavi mAMapNavi;

    private Context context;

    private FlowContract.View view;
    private FlowContract.Model model;

    public FlowPresenter(Context context, FlowContract.View view) {
        this.context = context;
        this.view = view;
        model = new FlowModel();
    }


    @Override
    public void toStart(Long orderId,Long version) {
        Observable<GovOrderResult> observable = model.toStart(orderId,version);
        view.getManager().add(observable.subscribe(new MySubscriber<>(context, null, govOrderResult -> {
            findOne(orderId);
        })));
    }

    @Override
    public void arriveStart(Long orderId,Long version) {
        Observable<GovOrderResult> observable = model.arriveStart(orderId,version);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, false, false, govOrderResult -> {
            findOne(orderId);
        })));
    }

    @Override
    public void startDrive(Long orderId,Long version) {
        if (!PhoneUtil.checkGps(context)) {
            return;
        }
        Observable<GovOrderResult> observable = model.startDrive(orderId,version);
        view.getManager().add(observable.subscribe(new MySubscriber<>(context,false,false,  govOrderResult -> {
            updateDymOrder(govOrderResult.data);
            view.showOrder(govOrderResult.data);
        })));
    }

    @Override
    public void arriveDes(Long orderId,Long version, DymOrder dymOrder) {
        Observable<GovOrderResult> observable = model.arriveDes(orderId, dymOrder, version);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, null, zcOrderResult -> {
            updateDymOrder(zcOrderResult.data);
            view.showOrder(zcOrderResult.data);
        })));
    }

    @Override
    public void confirmOrder(Long orderId, Long version, String voucher) {
        Observable<EmResult> observable = model.confirmOrder(orderId, version, voucher);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, null, emResult -> {
            if (emResult.getCode() == 1){
                view.finishActivity();
            }

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
        intent.putExtra("orderType", Config.ZHUANCHE);

        intent.putExtra(Config.NAVI_MODE, Config.DRIVE_TYPE);

        stopNavi();//停止当前页面的导航，在到导航页时重新初始化导航
        context.startActivity(intent);
    }

    @Override
    public void findOne(Long orderId) {
        Observable<GovOrderResult> observable = model.findOne(orderId);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, false, false, new HaveErrSubscriberListener<GovOrderResult>() {
            @Override
            public void onNext(GovOrderResult govOrderResult) {
                updateDymOrder(govOrderResult.data);
                view.showOrder(govOrderResult.data);
            }

            @Override
            public void onError(int code) {
                view.showOrder(null);
            }
        })));
    }

    @Override
    public void cancelOrder(Long orderId, String remark) {
        Observable<EmResult> observable = model.cancelOrder(orderId, remark);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, false, true, zcOrderResult -> {
            DymOrder dymOrder = DymOrder.findByIDType(orderId, Config.DAIJIA);
            if (null != dymOrder) {
                dymOrder.delete();
            }
            view.cancelSuc();
        })));
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
    public void updateDymOrder(GovOrder govOrder) {
        DymOrder dymOrder = DymOrder.findByIDType(govOrder.orderId, govOrder.orderType);
        if (null == dymOrder) {
            if (null != govOrder.orderFee) {
                dymOrder = govOrder.orderFee;
                dymOrder.orderId = govOrder.orderId;
                dymOrder.orderType = govOrder.orderType;
                dymOrder.passengerId = govOrder.passengerId;
                dymOrder.orderStatus = govOrder.orderStatus;

                dymOrder.waitTime = govOrder.orderFee.waitTime / 60;
                dymOrder.travelTime = govOrder.orderFee.travelTime / 60;
                dymOrder.distance = new BigDecimal(govOrder.orderFee.distance / 1000).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                dymOrder.lowSpeedTime = govOrder.orderFee.lowSpeedTime / 60;
                dymOrder.nightTime = govOrder.orderFee.nightTime / 60;
            } else {
                dymOrder = new DymOrder(govOrder.orderId, govOrder.orderType,
                        govOrder.passengerId, govOrder.orderStatus);
            }
            dymOrder.orderStatus = govOrder.orderStatus;
            dymOrder.save();
        } else {
            if (null != govOrder.orderFee) {
                long id = dymOrder.id;
                dymOrder = govOrder.orderFee;
                dymOrder.id = id;
                dymOrder.orderId = govOrder.orderId;
                dymOrder.orderType = govOrder.orderType;
                dymOrder.passengerId = govOrder.passengerId;
                dymOrder.orderStatus = govOrder.orderStatus;

                dymOrder.waitTime = govOrder.orderFee.waitTime / 60;
                dymOrder.travelTime = govOrder.orderFee.travelTime / 60;
                dymOrder.distance = new BigDecimal(govOrder.orderFee.distance / 1000).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                dymOrder.lowSpeedTime = govOrder.orderFee.lowSpeedTime / 60;
                dymOrder.nightTime = govOrder.orderFee.nightTime / 60;

                dymOrder.totalFee = govOrder.orderFee.totalFee;
            }
            dymOrder.orderStatus = govOrder.orderStatus;
            dymOrder.updateAll();
        }
        MqttManager.getInstance().pushLoc(new BuildPushData(EmUtil.getLastLoc()));
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
        view.showReCal();
        XApp.getInstance().syntheticVoice("您已偏航，已为您重新规划路径");
    }

    @Override
    public void onReCalculateRouteForTrafficJam() {

    }

    @Override
    public void onArrivedWayPoint(int i) {

    }

    @Override
    public void onGpsOpenStatus(boolean b) {
        if (!b){
            ToastUtil.showMessage(context,"请打开手机gps");
        }
    }

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
                mAMapNavi.startNavi(NaviType.GPS);
            }
        }
    }

    @Override
    public void onCalculateRouteFailure(int i) {

    }

    @Override
    public void onStopSpeaking() {
        XApp.getInstance().stopVoice();
        XApp.getInstance().clearVoiceList();
    }

    @Override
    public void onReCalculateRoute(int i) {

    }

    @Override
    public void onExitPage(int i) {

    }
}
