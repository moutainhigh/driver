package com.easymi.taxi.flowMvp;

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
import com.easymi.common.entity.BuildPushData;
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
import com.easymi.taxi.entity.TaxiOrder;
import com.easymi.taxi.result.TaxiOrderResult;

import rx.Observable;

//import com.easymi.common.push.MQTTService;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: FlowPresenter
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

    public FlowPresenter(Context context, FlowContract.View view) {
        this.context = context;
        this.view = view;
        model = new FlowModel();
    }

    @Override
    public void acceptOrder(Long orderId, LoadingButton btn) {
        Observable<TaxiOrderResult> observable = model.doAccept(orderId);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, btn, zcOrderResult -> {
            findOne(orderId);
        })));
    }

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

    @Override
    public void toStart(Long orderId, LoadingButton btn) {
        Observable<TaxiOrderResult> observable = model.toStart(orderId);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, btn, zcOrderResult -> {
            zcOrderResult = orderResult2ZCOrder(zcOrderResult);
            updateDymOrder(zcOrderResult.data);
            view.showOrder(zcOrderResult.data);
        })));
    }

    @Override
    public void arriveStart(Long orderId) {
        Observable<TaxiOrderResult> observable = model.arriveStart(orderId);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, true, false, zcOrderResult -> {
            zcOrderResult = orderResult2ZCOrder(zcOrderResult);
            updateDymOrder(zcOrderResult.data);
            view.showOrder(zcOrderResult.data);

        })));
    }

    @Override
    public void startWait(Long orderId, LoadingButton btn) {
        Observable<TaxiOrderResult> observable = model.startWait(orderId);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, btn, zcOrderResult -> {
            zcOrderResult = orderResult2ZCOrder(zcOrderResult);
            updateDymOrder(zcOrderResult.data);
            view.showOrder(zcOrderResult.data);

        })));
    }

    @Override
    public void startWait(Long orderId) {
        Observable<TaxiOrderResult> observable = model.startWait(orderId);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, true, true, zcOrderResult -> {
            zcOrderResult = orderResult2ZCOrder(zcOrderResult);
            updateDymOrder(zcOrderResult.data);
            view.showOrder(zcOrderResult.data);

        })));
    }

    @Override
    public void startDrive(Long orderId, LoadingButton btn) {
        if (!PhoneUtil.checkGps(context)) {
            return;
        }
        Observable<TaxiOrderResult> observable = model.startDrive(orderId);
        view.getManager().add(observable.subscribe(new MySubscriber<>(context, btn, zcOrderResult -> {
            zcOrderResult = orderResult2ZCOrder(zcOrderResult);
            updateDymOrder(zcOrderResult.data);
            view.showOrder(zcOrderResult.data);

        })));
    }

    @Override
    public void arriveDes(TaxiOrder taxiOrder, LoadingButton btn, DymOrder dymOrder) {

        Observable<TaxiOrderResult> observable = model.arriveDes(taxiOrder, dymOrder);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, btn, zcOrderResult -> {
            zcOrderResult = orderResult2ZCOrder(zcOrderResult);
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
//        TaxiOrder zcOrder = view.getOrder();
//        if (zcOrder != null && (zcOrder.orderStatus < DJOrderStatus.ARRIVAL_BOOKPLACE_ORDER)) {
//            intent.putExtra(Config.NAVI_MODE, Config.WALK_TYPE);
//        } else {
        intent.putExtra(Config.NAVI_MODE, Config.DRIVE_TYPE);
//        }
        stopNavi();//停止当前页面的导航，在到导航页时重新初始化导航
        context.startActivity(intent);
    }

    @Override
    public void findOne(Long orderId) {
        Observable<TaxiOrderResult> observable = model.findOne(orderId);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, true, false, new HaveErrSubscriberListener<TaxiOrderResult>() {
            @Override
            public void onNext(TaxiOrderResult taxiOrderResult) {
                updateDymOrder(taxiOrderResult.data);
                view.showOrder(taxiOrderResult.data);
            }

            @Override
            public void onError(int code) {
                view.showOrder(null);
            }
        })));
    }

    @Override
    public void findOne(Long orderId, boolean needShowProgress) {
        Observable<TaxiOrderResult> observable = model.findOne(orderId);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, needShowProgress, false, new HaveErrSubscriberListener<TaxiOrderResult>() {
            @Override
            public void onNext(TaxiOrderResult taxiOrderResult) {
                taxiOrderResult = orderResult2ZCOrder(taxiOrderResult);
                updateDymOrder(taxiOrderResult.data);
                view.showOrder(taxiOrderResult.data);
            }

            @Override
            public void onError(int code) {
                view.showOrder(null);
            }
        })));
    }

    @Override
    public void changeEnd(Long orderId, Double lat, Double lng, String address) {
        Observable<TaxiOrderResult> observable = model.changeEnd(orderId, lat, lng, address);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, true, false, zcOrderResult -> {
            zcOrderResult = orderResult2ZCOrder(zcOrderResult);
            updateDymOrder(zcOrderResult.data);
            view.showOrder(zcOrderResult.data);

        })));
    }

    @Override
    public void cancelOrder(Long orderId, String remark) {
        Observable<EmResult> observable = model.cancelOrder(orderId, remark);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, true, true, zcOrderResult -> {
            DymOrder dymOrder = DymOrder.findByIDType(orderId, Config.DAIJIA);
            if (null != dymOrder) {
                dymOrder.delete();
            }
            view.cancelSuc();
        })));
    }



    @Override
    public void routePlanByNavi(Double endLat, Double endLng) {

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
                RouteSearch.DRIVING_SINGLE_SHORTEST, null, null, "");
        routeSearch.calculateDriveRouteAsyn(query);
    }

    @Override
    public void updateDymOrder(TaxiOrder taxiOrder) {
        DymOrder dymOrder = DymOrder.findByIDType(taxiOrder.id, taxiOrder.serviceType);
        if (null == dymOrder) {
//            if (null != taxiOrder.orderFee) {
//                dymOrder = taxiOrder.orderFee;
//                dymOrder.orderId = taxiOrder.id;
//                dymOrder.orderType = taxiOrder.serviceType;
//                dymOrder.passengerId = taxiOrder.passengerId;
//                dymOrder.orderStatus = taxiOrder.status;
//            } else {
            dymOrder = new DymOrder(taxiOrder.id, taxiOrder.serviceType,
                    taxiOrder.passengerId, taxiOrder.status);
//            }
            dymOrder.save();
        } else {
            dymOrder.orderStatus = taxiOrder.status;
            dymOrder.updateStatus();
        }
        MqttManager.getInstance().pushLoc(new BuildPushData(EmUtil.getLastLoc()));
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
    }

    @Override
    public void getConsumerInfo(Long orderId) {
        view.getManager().add(model.consumerInfo(orderId).subscribe(new MySubscriber<>(context, true,
                false, consumerResult -> view.showConsumer(consumerResult.consumerInfo))));
    }

    @Override
    public TaxiOrderResult orderResult2ZCOrder(TaxiOrderResult taxiOrderResult) {
//        taxiOrderResult.order.orderAddressVos = taxiOrderResult.address;
//        taxiOrderResult.order.orderFee = taxiOrderResult.orderFee;
//        taxiOrderResult.order.coupon = taxiOrderResult.coupon;
        return taxiOrderResult;
    }

    @Override
    public void changeOrderStatus(Long companyId, String detailAddress, Long driverId, Double latitude, Double longitude, Long orderId,
                                  int status, LoadingButton btn) {
        Observable<EmResult> observable = model.changeOrderStatus(companyId, detailAddress, driverId, latitude, longitude, orderId, status);
        if (btn != null) {
            view.getManager().add(observable.subscribe(new MySubscriber<>(context, btn, emResult -> {
                findOne(orderId);
            })));
        } else {
            view.getManager().add(observable.subscribe(new MySubscriber<>(context, true, true, emResult -> {
                findOne(orderId);
            })));
        }
    }

    @Override
    public void taxiSettlement(Long orderId, String orderNo, double fee) {
        Observable<EmResult> observable = model.taxiSettlement(orderId, orderNo, fee);
        view.getManager().add(observable.subscribe(new MySubscriber<>(context, true, true, zcOrderResult -> {
            view.settleSuc();
        })));
    }

}
