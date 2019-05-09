package com.easymi.zhuanche.fragment.create;

import android.content.Context;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.DriveStep;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.easymi.common.result.CreateOrderResult;
import com.easymi.component.entity.Employ;
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.utils.EmUtil;
import com.easymi.zhuanche.result.BudgetResult;
import com.easymi.zhuanche.result.ZCTypeResult;
import com.easymi.zhuanche.result.PassengerResult;

import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: CreateZCPresenter
 *
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class CreateZCPresenter implements CreateZCContract.Presenter {

    private CreateZCContract.View view;
    private CreateZCContract.Model model;

    private Context context;

    public CreateZCPresenter(CreateZCContract.View view, Context context) {
        this.view = view;
        this.context = context;
        this.model = new CreateZCModel();
    }

    @Override
    public void queryZCType(String adcode, String citycode, int carModel, double lat, double lng) {
        view.getManager().add(model.queryZCType(adcode, citycode, carModel, lat, lng)
                .subscribe(new MySubscriber<>(context, true, false, new HaveErrSubscriberListener<ZCTypeResult>() {
                    @Override
                    public void onNext(ZCTypeResult zcTypeResult) {
                        view.showTypeTab(zcTypeResult);
                    }

                    @Override
                    public void onError(int code) {
                        view.showQueryTypeErr(code);
                    }
                })));
    }

    @Override
    public void queryPassenger(String phone) {
        Employ employ = EmUtil.getEmployInfo();
        view.getManager().add(model.queryPassenger(employ.companyId,
                "company_name",
                phone).subscribe(new MySubscriber<>(context, true, true, new HaveErrSubscriberListener<PassengerResult>() {
            @Override
            public void onNext(PassengerResult result) {
                view.showPassenger(result);
            }

            @Override
            public void onError(int code) {
                view.showQueryPasErr(code);
            }
        })));
    }

    @Override
    public void queryBudget(Long businessId, Long companyId, Double distance, Integer time, Long modelId) {
        view.getManager().add(model.getBudgetPrice(businessId, companyId,
                distance == null ? 0.0 : distance, time == null ? 0 : time, modelId)
                .subscribe(new MySubscriber<>(context, false,
                        false, new HaveErrSubscriberListener<BudgetResult>() {
                    @Override
                    public void onNext(BudgetResult budgetResult) {
                        view.showBudget(budgetResult);
                    }

                    @Override
                    public void onError(int code) {
                        view.showQueryBudgetErr(code);
                    }
                })));
    }

    @Override
    public void routePlan(LatLonPoint start, LatLonPoint end) {
        RouteSearch routeSearch = new RouteSearch(context);
        routeSearch.setRouteSearchListener(new RouteSearch.OnRouteSearchListener() {
            @Override
            public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

            }

            @Override
            public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int code) {
                if (code == 1000) {
                    List<DrivePath> paths = driveRouteResult.getPaths();
                    if (paths != null && paths.size() != 0) {
                        DrivePath path = paths.get(0);//选取第一条路线
                        List<DriveStep> steps = path.getSteps();
                        float dis = 0;//米
                        float dur = 0;//秒
                        for (DriveStep step : steps) {
                            dis += step.getDistance();
                            dur += step.getDuration();
                        }
                        view.showDisAndTime(dis, dur);
                    }
                } else {
                    view.showDisAndTimeErr();
                }
            }

            @Override
            public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

            }

            @Override
            public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

            }
        });
        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(start, end);
        //只考虑距离最短 不考虑是否拥堵等
        RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo,
                RouteSearch.DRIVING_SINGLE_SHORTEST, null, null, "");
        routeSearch.calculateDriveRouteAsyn(query);
    }

    @Override
    public void createOrder(Long bookTime,
                            Double budgetFee,
                            Long businessId,
                            String channelAlias,
                            Long companyId,
                            Long driverId,
                            String driverName,
                            String driverPhone,
                            Long modelId,
                            String orderAddress,
                            Long passengerId,
                            String passengerName,
                            String passengerPhone,
                            String serviceType,
                            boolean onePrice,
                            Integer time,
                            Double distance) {
        view.getManager().add(model.createOrder(bookTime,
                budgetFee,
                businessId,
                channelAlias,
                companyId,
                driverId,
                driverName,
                driverPhone,
                modelId,
                orderAddress,
                passengerId,
                passengerName,
                passengerPhone,
                serviceType,
                onePrice,
                time,
                distance).subscribe(
                new MySubscriber<>(context, true, false, createOrderResult -> view.createSuc(createOrderResult))
        ));
    }
}
