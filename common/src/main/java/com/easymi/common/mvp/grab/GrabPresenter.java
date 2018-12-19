package com.easymi.common.mvp.grab;

import android.content.Context;
import android.content.Intent;

import com.alibaba.android.arouter.launcher.ARouter;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.easymi.common.entity.MultipleOrder;
import com.easymi.common.result.MultipleOrderResult;
import com.easymi.component.Config;
import com.easymi.component.app.XApp;
import com.easymi.component.network.ErrCode;
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.utils.AesUtil;
import com.easymi.component.utils.EmUtil;
import com.easymin.driver.securitycenter.utils.CenterUtil;

import java.util.List;

import rx.Observable;

import static com.easymi.common.mvp.grab.GrabActivity2.GRAB_VALID_TIME;

/**
 * Created by developerLzh on 2017/11/22 0022.
 */

public class GrabPresenter implements GrabContract.Presenter {

    private GrabContract.Model model;
    private GrabContract.View view;

    private Context context;

    public GrabPresenter(Context context, GrabContract.View view) {
        this.context = context;
        this.view = view;
        this.model = new GrabModel();
    }

    @Override
    public void queryOrder(MultipleOrder order) {
        Observable<MultipleOrderResult> observable = null;
        if (order.serviceType.equals(Config.DAIJIA)) {
            observable = model.queryDJOrder(order.orderId);
        } else if (order.serviceType.equals(Config.ZHUANCHE)) {
            observable = model.queryZCOrder(order.orderId);
        } else if (order.serviceType.equals(Config.TAXI)) {
            observable = model.queryTaxiOrder(order.orderId);
        }

        if (observable == null) {
            return;
        }

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, true, false, new HaveErrSubscriberListener<MultipleOrderResult>() {
            @Override
            public void onNext(MultipleOrderResult multipleOrderResult) {
//                multipleOrderResult.data.orderAddressVos = multipleOrderResult.address;
                view.showBase(multipleOrderResult.data);
            }

            @Override
            public void onError(int code) {
                view.showBase(null);
            }
        })));
    }

    /**
     * 抢单是根据id查询订单详情 属于冷数据。直接取id，id就是订单id
     *
     * @param order
     */
    @Override
    public void grabOrder(MultipleOrder order) {
        if (order.countTime > GRAB_VALID_TIME) {
            return;
        }
        Observable<MultipleOrderResult> observable = null;
        if (order.serviceType.equals(Config.ZHUANCHE)) {
//            if (order.orderId == 0){
            observable = model.grabZCOrder(order.id, order.version);
//            }else {
//                observable = model.grabZCOrder(order.orderId, order.version);
//            }
        } else if (order.serviceType.equals(Config.TAXI)) {
//            if (order.orderId == 0){
            observable = model.grabTaxiOrder(order.id, order.version);
//            }else {
//                observable = model.takeTaxiOrder(order.orderId, order.version);
//            }
        }
        if (observable == null) {
            return;
        }

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, true, false, new HaveErrSubscriberListener<MultipleOrderResult>() {
            @Override
            public void onNext(MultipleOrderResult multipleOrderResult) {
                //todo 一键报警
//                CenterUtil centerUtil = new CenterUtil(context,Config.APP_KEY,
//                        XApp.getMyPreferences().getString(Config.AES_PASSWORD, AesUtil.AAAAA),
//                        XApp.getMyPreferences().getString(Config.SP_TOKEN, ""));
//                centerUtil.smsShareAuto(order.id, EmUtil.getEmployInfo().companyId, order.passengerId, order.passengerPhone, order.serviceType);
//                centerUtil.checkingAuth(order.passengerId);
                if (order.serviceType.equals(Config.ZHUANCHE)) {
//                        if (order.orderId == 0){
                    ARouter.getInstance()
                            .build("/zhuanche/FlowActivity")
                            .withFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .withLong("orderId", order.id).navigation();
//                        }else {
//                            ARouter.getInstance()
//                                    .build("/zhuanche/FlowActivity")
//                                    .withFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                    .withLong("orderId", order.orderId).navigation();
//                        }

                } else if (order.serviceType.equals(Config.TAXI)) {
//                    if (order.orderId == 0){
                    ARouter.getInstance()
                            .build("/taxi/FlowActivity")
                            .withFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .withLong("orderId", order.id).navigation();
//                    }else {
//                        ARouter.getInstance()
//                                .build("/taxi/FlowActivity")
//                                .withFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                .withLong("orderId", order.orderId).navigation();
//                    }
                }
                view.finishActivity();
            }

            @Override
            public void onError(int code) {
                view.showBase(null);
                if (code == ErrCode.NOT_MATCH.getCode()
                        || code == ErrCode.GRAB_ORDER_ERROR.getCode()
                        || code == ErrCode.DRIVER_GOTO_PRE_ORDER_CODE.getCode()) {
                    view.removerOrderById(order.id);
                }
            }
        })));
    }

    /**
     * 抢单界面是根据id查询订单详情 属于冷数据。直接取id，id就是订单id
     *
     * @param order
     */
    @Override
    public void takeOrder(MultipleOrder order) {
        if (order.countTime > GRAB_VALID_TIME) {
            return;
        }
        Observable<MultipleOrderResult> observable = null;
        if (order.serviceType.equals(Config.ZHUANCHE)) {
//            if (order.orderId == 0){
            observable = model.takeZCOrder(order.id, order.version);
//            }else {
//                observable = model.takeZCOrder(order.orderId, order.version);
//            }
        } else if (order.serviceType.equals(Config.TAXI)) {
//            if (order.orderId == 0){
            observable = model.takeTaxiOrder(order.id, order.version);
//            }else {
//                observable = model.takeTaxiOrder(order.orderId, order.version);
//            }
        }

        if (observable == null) {
            return;
        }

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, true, false, new HaveErrSubscriberListener<MultipleOrderResult>() {
            @Override
            public void onNext(MultipleOrderResult multipleOrderResult) {
                //todo 一键报警
//                CenterUtil centerUtil = new CenterUtil(context,Config.APP_KEY,
//                        XApp.getMyPreferences().getString(Config.AES_PASSWORD, AesUtil.AAAAA),
//                        XApp.getMyPreferences().getString(Config.SP_TOKEN, ""));
//                centerUtil.smsShareAuto(order.id, EmUtil.getEmployInfo().companyId, order.passengerId, order.passengerPhone, order.serviceType);
//                centerUtil.checkingAuth(order.passengerId);
                if (order.serviceType.equals(Config.ZHUANCHE)) {

//                            if (order.orderId == 0){
                    ARouter.getInstance()
                            .build("/zhuanche/FlowActivity")
                            .withFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .withLong("orderId", order.id).navigation();
//                            }else {
//                                ARouter.getInstance()
//                                        .build("/zhuanche/FlowActivity")
//                                        .withFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                        .withLong("orderId", order.orderId).navigation();
//                            }

                } else if (order.serviceType.equals(Config.TAXI)) {
//                        if (order.orderId == 0){
                    ARouter.getInstance()
                            .build("/taxi/FlowActivity")
                            .withFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .withLong("orderId", order.id).navigation();
//                        }else {
//                            ARouter.getInstance()
//                                    .build("/taxi/FlowActivity")
//                                    .withFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                    .withLong("orderId", order.orderId).navigation();
//                        }
                }
                view.finishActivity();
            }

            @Override
            public void onError(int code) {
                view.showBase(null);
                if (code == ErrCode.NOT_MATCH.getCode()
                        || code == ErrCode.GRAB_ORDER_ERROR.getCode()
                        || code == ErrCode.DRIVER_GOTO_PRE_ORDER_CODE.getCode()) {
                    view.removerOrderById(order.id);
                }
            }
        })));
    }

    RouteSearch routeSearch;

    @Override
    public void routePlanByRouteSearch(LatLonPoint endPoint, List<LatLonPoint> pass) {
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

        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(start, endPoint);
        RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo,
                RouteSearch.DRIVING_MULTI_STRATEGY_FASTEST_SHORTEST, pass, null, "");
        routeSearch.calculateDriveRouteAsyn(query);
    }
}
