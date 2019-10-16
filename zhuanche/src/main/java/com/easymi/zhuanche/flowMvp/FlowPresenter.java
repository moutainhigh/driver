package com.easymi.zhuanche.flowMvp;

import android.content.Context;
import android.content.Intent;

import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.easymi.common.push.MqttManager;
import com.easymi.component.Config;
import com.easymi.component.activity.NaviActivity;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.result.EmResult;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.PhoneUtil;
import com.easymi.component.widget.LoadingButton;
import com.easymi.zhuanche.entity.ZCOrder;
import com.easymi.zhuanche.result.ZCOrderResult;
import com.google.gson.JsonElement;

import java.math.BigDecimal;

import rx.Observable;

//import com.easymi.common.push.MQTTService;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 *
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */
public class FlowPresenter implements FlowContract.Presenter {

    private Context context;

    private FlowContract.View view;
    private FlowContract.Model model;
    private RouteSearch routeSearch;

    public FlowPresenter(Context context, FlowContract.View view) {
        this.context = context;
        this.view = view;
        model = new FlowModel();
    }

    //接单
    @Override
    public void acceptOrder(Long orderId, Long version, LoadingButton btn) {
        Observable<ZCOrderResult> observable = model.doAccept(orderId, version);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, btn, zcOrderResult -> {
            findOne(orderId);
        })));
    }

    //拒单
    @Override
    public void refuseOrder(Long orderId, String orderType, String remark) {
        Observable<EmResult> observable = model.refuseOrder(orderId, orderType, remark);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, true, true, zcOrderResult -> {
            DymOrder dymOrder = DymOrder.findByIDType(orderId, Config.DAIJIA);
            if (null != dymOrder) {
                dymOrder.delete();
            }
            view.refuseSuc();
        })));
    }

    //前往预约地
    @Override
    public void toStart(Long orderId, Long version, LoadingButton btn) {
        Observable<ZCOrderResult> observable = model.toStart(orderId, version);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, btn, zcOrderResult -> {
            findOne(orderId);
        })));
    }

    //到达预约地
    @Override
    public void arriveStart(Long orderId, Long version) {
        Observable<ZCOrderResult> observable = model.arriveStart(orderId, version);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, false, false, zcOrderResult -> {
            findOne(orderId);
        })));
    }

    @Override
    public void startWait(Long orderId, LoadingButton btn) {
        Observable<ZCOrderResult> observable = model.startWait(orderId);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, btn, zcOrderResult -> {
            findOne(orderId);
        })));
    }

    //开始中途等待 达到预约地的
    @Override
    public void startWait(Long orderId) {
        Observable<ZCOrderResult> observable = model.startWait(orderId);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, false, false, zcOrderResult -> {
            findOne(orderId);
        })));
    }

    //前往目的地
    @Override
    public void startDrive(Long orderId, Long version) {
        if (!PhoneUtil.checkGps(context)) {
            return;
        }
        Observable<ZCOrderResult> observable = model.startDrive(orderId, version);
        view.getManager().add(observable.subscribe(new MySubscriber<ZCOrderResult>(context, false, false, zcOrderResult -> {
            updateDymOrder(zcOrderResult.data);
            view.showOrder(zcOrderResult.data);
        })));
    }


    //到达目的地
    @Override
    public void arriveDes(ZCOrder zcOrder, Long version, DymOrder dymOrder) {
        Observable<ZCOrderResult> observable = model.arriveDes(zcOrder, dymOrder, version);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, true, false, zcOrderResult -> {
            updateDymOrder(zcOrderResult.data);
            view.showOrder(zcOrderResult.data);
            //todo 一键报警
//            AudioUtil audioUtil = new AudioUtil();
//            audioUtil.onRecord(context, false);
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
        context.startActivity(intent);
    }

    @Override
    public void findOne(Long orderId) {
        Observable<ZCOrderResult> observable = model.findOne(orderId);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, false, false, new HaveErrSubscriberListener<ZCOrderResult>() {
            @Override
            public void onNext(ZCOrderResult zcOrderResult) {
                updateDymOrder(zcOrderResult.data);
                view.showOrder(zcOrderResult.data);
            }

            @Override
            public void onError(int code) {
                view.showOrder(null);
            }
        })));
    }

    @Override
    public void findOne(Long orderId, boolean needShowProgress) {
        Observable<ZCOrderResult> observable = model.findOne(orderId);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, needShowProgress, false, new HaveErrSubscriberListener<ZCOrderResult>() {
            @Override
            public void onNext(ZCOrderResult zcOrderResult) {
                updateDymOrder(zcOrderResult.data);
                view.showOrder(zcOrderResult.data);
            }

            @Override
            public void onError(int code) {
                view.showOrder(null);
            }
        })));
    }

    @Override
    public void changeEnd(Long orderId, Double lat, Double lng, String address) {
        Observable<ZCOrderResult> observable = model.changeEnd(orderId, lat, lng, address);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, false, false, zcOrderResult -> {
            findOne(orderId);
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
        LatLonPoint startPoint = new LatLonPoint(EmUtil.getLastLoc().latitude, EmUtil.getLastLoc().longitude);
        LatLonPoint endPoint = new LatLonPoint(endLat, endLng);

        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(startPoint, endPoint);
        RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo,
                RouteSearch.DRIVING_SINGLE_SHORTEST, null, null, "");
        routeSearch.calculateDriveRouteAsyn(query);
    }

    @Override
    public void updateDymOrder(ZCOrder zcOrder) {
        DymOrder dymOrder = DymOrder.findByIDType(zcOrder.orderId, zcOrder.orderType);
        if (null == dymOrder) {
            if (null != zcOrder.orderFee) {
                dymOrder = zcOrder.orderFee;
                dymOrder.orderId = zcOrder.orderId;
                dymOrder.orderType = zcOrder.orderType;
                dymOrder.passengerId = zcOrder.passengerId;
                dymOrder.orderStatus = zcOrder.orderStatus;

                dymOrder.waitTime = zcOrder.orderFee.waitTime / 60;
                dymOrder.travelTime = zcOrder.orderFee.travelTime / 60;
                dymOrder.distance = new BigDecimal(zcOrder.orderFee.distance / 1000).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                dymOrder.lowSpeedTime = zcOrder.orderFee.lowSpeedTime / 60;
                dymOrder.nightTime = zcOrder.orderFee.nightTime / 60;

                dymOrder.orderNo = zcOrder.orderNumber;
            } else {
                dymOrder = new DymOrder(zcOrder.orderId, zcOrder.orderType,
                        zcOrder.passengerId, zcOrder.orderStatus);
            }
            dymOrder.orderStatus = zcOrder.orderStatus;
            dymOrder.save();
        } else {
            if (null != zcOrder.orderFee) {
                long id = dymOrder.id;
                dymOrder = zcOrder.orderFee;
                dymOrder.id = id;
                dymOrder.orderId = zcOrder.orderId;
                dymOrder.orderType = zcOrder.orderType;
                dymOrder.passengerId = zcOrder.passengerId;
                dymOrder.orderStatus = zcOrder.orderStatus;

                dymOrder.waitTime = zcOrder.orderFee.waitTime / 60;
                dymOrder.travelTime = zcOrder.orderFee.travelTime / 60;
                dymOrder.distance = new BigDecimal(zcOrder.orderFee.distance / 1000).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                dymOrder.lowSpeedTime = zcOrder.orderFee.lowSpeedTime / 60;
                dymOrder.nightTime = zcOrder.orderFee.nightTime / 60;

                dymOrder.orderNo = zcOrder.orderNumber;
            }
            dymOrder.orderStatus = zcOrder.orderStatus;
            dymOrder.updateAll();
        }
        MqttManager.getInstance().savePushMessage(EmUtil.getLastLoc());
    }

    //选择支付类型后的结算接口
    @Override
    public void payOrder(Long orderId, String payType, Long version) {
        Observable<JsonElement> observable = model.payOrder(orderId, payType, version);

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
    }

    @Override
    public void getConsumerInfo(Long orderId) {
        view.getManager().add(model.consumerInfo(orderId).subscribe(new MySubscriber<>(context, true,
                false, consumerResult -> view.showConsumer(consumerResult.consumerInfo))));
    }
}
