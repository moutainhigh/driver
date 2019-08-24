package com.easymi.cityline.flowMvp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.easymi.common.entity.OrderCustomer;
import com.easymi.component.Config;
import com.easymi.component.activity.NaviActivity;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.network.ErrCode;
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.network.NoErrSubscriberListener;
import com.easymi.component.utils.EmUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 *
 * @Author: hufeng
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class FlowPresenter implements FlowContract.Presenter {

    private Context context;
    private FlowContract.View view;
    private FlowContract.Model model;

    RouteSearch routeSearch;

    public FlowPresenter(Context context, FlowContract.View view) {
        this.context = context;
        this.view = view;
        model = new FlowModel(context);
    }

    @Override
    public void navi(LatLng latLng, Long orderId) {
        NaviLatLng start = new NaviLatLng(EmUtil.getLastLoc().latitude, EmUtil.getLastLoc().longitude);
        NaviLatLng end = new NaviLatLng(latLng.latitude, latLng.longitude);
        Intent intent = new Intent(context, NaviActivity.class);
        intent.putExtra("startLatlng", start);
        intent.putExtra("endLatlng", end);
        intent.putExtra("orderId", orderId);
        intent.putExtra("orderType", Config.CITY_LINE);

        intent.putExtra(Config.NAVI_MODE, Config.DRIVE_TYPE);
        context.startActivity(intent);
    }

    @Override
    public void startSend(long orderId) {
        view.getManager().add(model.startSend(orderId).subscribe(new MySubscriber<>(context,
                true,
                true,
                o -> view.startSendSuc())));
    }

    @Override
    public void finishTask(long orderId) {
        view.getManager().add(model.finishSchedule(orderId).subscribe(new MySubscriber<>(context,
                true,
                true,
                o -> view.finishTaskSuc())));
    }

    @Override
    public void routeLineByNavi(LatLng start, List<LatLng> latLngs, LatLng end) {
    }


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
    public void stopNavi() {
    }

    @Override
    public void startOutSet(long orderId) {
        view.getManager().add(model.startSchedule(orderId).subscribe(new MySubscriber<Object>(context, true, true, new NoErrSubscriberListener<Object>() {
            @Override
            public void onNext(Object o) {
                view.startOutSuc();
            }
        })));
    }

    @Override
    public void arriveStart(OrderCustomer orderCustomer) {
        view.getManager().add(model.arriveStart(orderCustomer.id).subscribe(new MySubscriber<>(context,
                true,
                true,
                new HaveErrSubscriberListener<Object>() {
                    @Override
                    public void onNext(Object o) {
                        view.arriveStartSuc(orderCustomer);
                    }

                    @Override
                    public void onError(int code) {
                        if (code == ErrCode.ORDER_STATUS_ERR.getCode()) {
                            AlertDialog dialog = new AlertDialog.Builder(context)
                                    .setMessage("请确认客户是否已完成支付，否则无法进行下一步操作")
                                    .setPositiveButton("了解", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .create();
                            dialog.show();
                        }
                    }
                })));
    }

    @Override
    public void acceptCustomer(OrderCustomer orderCustomer) {
        view.getManager().add(model.acceptCustomer(orderCustomer.id).subscribe(new MySubscriber<>(context,
                true,
                true,
                o -> view.acceptCustomerSuc(orderCustomer))));
    }

    @Override
    public void jumpAccept(OrderCustomer orderCustomer) {
        view.getManager().add(model.jumpCustomer(orderCustomer.id).subscribe(new MySubscriber<>(context,
                true,
                true,
                o -> view.jumpAcceptSuc(orderCustomer))));
    }

    @Override
    public void arriveEnd(OrderCustomer orderCustomer) {
        view.getManager().add(model.sendCustomer(orderCustomer.id).subscribe(new MySubscriber<>(context,
                true,
                true,
                o -> view.arriveEndSuc(orderCustomer))));
    }

    @Override
    public void jumpSend(OrderCustomer orderCustomer) {
        view.jumpSendSuc(orderCustomer);
    }

    @Override
    public void deleteDb(long orderId, String orderType) {
        DymOrder dymOrder = DymOrder.findByIDType(orderId, orderType);
        if (null != dymOrder) {
            dymOrder.delete();
        }
        OrderCustomer.delete(orderId, orderType);
    }
}
