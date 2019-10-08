package com.easymin.custombus.mvp;

import android.content.Context;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.DriveStep;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.result.EmResult2;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.LoadingButton;
import com.easymin.custombus.entity.Customer;
import com.easymin.custombus.entity.OrdersResult;
import com.easymin.custombus.entity.StationResult;
import com.easymin.custombus.entity.TimeResult;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: FlowPresenter
 * @Author: hufeng
 * @Date: 2019/2/18 下午5:14
 * @Description:
 * @History:
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

    @Override
    public void routePlanByNavi(Double endLat, Double endLng) {
        if (null == routeSearch) {
            routeSearch = new RouteSearch(context);
            routeSearch.setRouteSearchListener(new RouteSearch.OnRouteSearchListener() {
                @Override
                public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

                }

                @Override
                public void onDriveRouteSearched(DriveRouteResult result, int code) {
                    if (code == 1000) {
                        float dis = 0;
                        float time = 0;
                        for (DriveStep step : result.getPaths().get(0).getSteps()) {
                            dis += step.getDistance();
                            time += step.getDuration();
                        }
                        view.showLeft((int) dis, (int) time);
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
    public void stopNavi() {

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
                true,
                true,
                new HaveErrSubscriberListener<StationResult>() {

                    @Override
                    public void onNext(StationResult result) {
//                        if (result.getCode() == 1) {
//                            for (StationResult busStation : result.getData().stationVos) {
//                                if (!StationResult.existsById(busStation.id, result.getData().id)) {
//                                    busStation.scheduleId = result.getData().id;
//                                    busStation.serviceType = Config.COUNTRY;
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

    /**
     * @param id 班次id
     */
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
                true,
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
                        view.succeseOrder(null);
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
                        view.errorCode();
                    }
                })));
    }
}
