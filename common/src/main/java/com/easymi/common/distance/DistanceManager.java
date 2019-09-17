package com.easymi.common.distance;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.easymi.common.CommApiService;
import com.easymi.common.entity.PullFeeEntity;
import com.easymi.common.push.HandlePush;
import com.easymi.common.result.GetFeeResult;
import com.easymi.component.Config;
import com.easymi.component.DJOrderStatus;
import com.easymi.component.app.XApp;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.rxmvp.RxManager;
import com.google.gson.Gson;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:  DistanceManager
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description: 公里数管理类,此类只针对于在运营中出现的那种后台收到了mqtt的推送，没收到后台响应的情况.
 * History:
 */

public class DistanceManager {

    private static DistanceManager instance;

    private List<LatLng> latLngs;

    private RxManager rxManager;

    private long orderId;
    private String orderType;

    public static DistanceManager getInstance() {
        if (instance == null) {
            instance = new DistanceManager();
        }
        return instance;
    }

    private DistanceManager() {
        rxManager = new RxManager();
        latLngs = new ArrayList<>();
    }

    /**
     * @param lat
     * @param lng
     * @return 是否还需要mqtt推
     */
    public boolean addLatlng(double lat, double lng, long orderId, String orderType) {
        this.orderId = orderId;
        this.orderType = orderType;
        LatLng latLng = new LatLng(lat, lng);
        latLngs.add(latLng);
        if (latLngs.size() > 12) {//5秒一个点，12个点大概一分钟
            routeLine(); //规划路径、调接口
            return false;
        } else {
            return true;
        }
    }

    /**
     * 移除所有积压的经纬度
     */
    public void removeAllLatlng() {
        if (null != latLngs
                && latLngs.size() != 0) {
            latLngs.clear();
        }
    }

    RouteSearch routeSearch;

    /**
     * 根据未响应的latlng规划路径
     */
    public void routeLine() {
        if (latLngs != null && latLngs.size() > 1) {
            if (null == routeSearch) {
                routeSearch = new RouteSearch(XApp.getInstance());
                routeSearch.setRouteSearchListener(new RouteSearch.OnRouteSearchListener() {
                    @Override
                    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

                    }

                    @Override
                    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int code) {

//                        latLngs.clear();
//                        new CsEditor().putLong(Config.SP_MQTT_LAST_RESPONSE_TIME, 0).apply();

                        double distance = 0;
                        if (code == 1000) {
                            if (driveRouteResult.getPaths() != null
                                    && driveRouteResult.getPaths().size() != 0) {
                                DrivePath drivePath = driveRouteResult.getPaths().get(0);
                                distance = drivePath.getTollDistance();//路径规划得到的距离
                            }
                        }
                        DymOrder dymOrder = DymOrder.findByIDType(orderId, orderType);
                        if (null != dymOrder) {
                            pullFee(distance, dymOrder);
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

            LatLonPoint start = new LatLonPoint(latLngs.get(0).latitude, latLngs.get(0).longitude);
            LatLonPoint end = new LatLonPoint(latLngs.get(latLngs.size() - 1).latitude,
                    latLngs.get(latLngs.size() - 1).longitude);

            List<LatLonPoint> latLonPoints = new ArrayList<>();
            if (latLngs.size() > 2) {
                for (int i = 0; i < latLngs.size(); i++) {
                    if (i != 0 && i != latLngs.size() - 1) {
                        LatLonPoint point = new LatLonPoint(latLngs.get(i).latitude,
                                latLngs.get(i).longitude);
                        latLonPoints.add(point);
                    }
                }
            }

            RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(start, end);
            RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo,
                    RouteSearch.DRIVING_SINGLE_SHORTEST,
                    latLonPoints,//途经点
                    null,//避让点
                    "");
            routeSearch.calculateDriveRouteAsyn(query);
        }
    }

    /**
     * 拉取费用信息
     */
    private void pullFee(double distance, DymOrder dymOrder) {
//        rxManager.add();
        LatLng lastLatlng = latLngs.get(latLngs.size() - 1); //上次位置信息
        latLngs.clear();
        if (!dymOrder.orderType.equals(Config.DAIJIA)) {
            return;
        }
        int state = 0;
        if (dymOrder.orderStatus == DJOrderStatus.GOTO_DESTINATION_ORDER) {
            state = 1;
        } else if (dymOrder.orderStatus == DJOrderStatus.START_WAIT_ORDER) {
            state = 2;
        }
        if (state == 0) {
            return;
        }
        Observable<GetFeeResult> observable = ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .pullFee(dymOrder.orderId,
                        distance,
                        Config.APP_KEY,
                        state,
                        dymOrder.addedKm,
                        dymOrder.addedFee,
                        lastLatlng.latitude,
                        lastLatlng.longitude)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        rxManager.add(observable.subscribe(new MySubscriber<>(XApp.getInstance(),
                false,
                false,
                new HaveErrSubscriberListener<GetFeeResult>() {
                    @Override
                    public void onNext(GetFeeResult getFeeResult) {
                        GetFeeResult.Cost cost = getFeeResult.budgetFee;
                        if (null == cost) {
                            return;
                        }
                        dymOrder.startFee = cost.start_price;
                        dymOrder.waitTime = cost.wait_time / 60;
                        dymOrder.waitTimeFee = cost.wait_time_fee;
                        dymOrder.travelTime = cost.driver_time / 60;
                        dymOrder.travelFee = cost.drive_time_cost;
                        dymOrder.totalFee = cost.total_amount;

                        dymOrder.minestMoney = cost.min_cost;

                        dymOrder.disFee = cost.mileage_cost;
                        dymOrder.distance = cost.mileges;

                        DecimalFormat decimalFormat = new DecimalFormat("#0.0");
                        decimalFormat.setRoundingMode(RoundingMode.DOWN);
                        dymOrder.distance = Double.parseDouble(decimalFormat.format(dymOrder.distance));
                        //公里数保留一位小数。。

                        dymOrder.updateFee();

                        PullFeeEntity entity = new PullFeeEntity();
                        entity.msg = "pull_fee";
//                        entity.orderId = dymOrder.orderId;
//                        entity.orderType = dymOrder.orderType;
                        HandlePush.getInstance().handPush(new Gson().toJson(entity));
                    }

                    @Override
                    public void onError(int code) {

                    }
                })));
    }

    /**
     * 销毁
     */
    public void destory() {
        if (latLngs != null) {
            latLngs.clear();
        }
        if (null != routeSearch) {
            routeSearch = null;
        }
        instance = null;
    }

}
