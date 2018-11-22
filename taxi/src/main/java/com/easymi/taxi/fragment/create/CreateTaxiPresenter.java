package com.easymi.taxi.fragment.create;

import android.content.Context;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.DriveStep;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.easymi.component.entity.Employ;
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.utils.EmUtil;
import com.easymi.taxi.result.BudgetResult;
import com.easymi.taxi.result.ZCTypeResult;
import com.easymi.taxi.result.PassengerResult;

import java.util.List;

/**
 * Created by developerLzh on 2017/11/27 0027.
 */

public class CreateTaxiPresenter implements CreateTaxiContract.Presenter {

    private CreateTaxiContract.View view;
    private CreateTaxiContract.Model model;

    private Context context;

    public CreateTaxiPresenter(CreateTaxiContract.View view, Context context) {
        this.view = view;
        this.context = context;
        this.model = new CreateTaxiModel();
    }

    @Override
    public void queryZCType() {
        Employ employ = EmUtil.getEmployInfo();
        view.getManager().add(model.queryZCType(employ.company_id
//                , employ.vehicle.serviceType
                , 1
        ).subscribe(new MySubscriber<>(context, true, false, new HaveErrSubscriberListener<ZCTypeResult>() {
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
        view.getManager().add(model.queryPassenger(employ.company_id,
//                employ.company_name,
                "11",
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
    public void queryBudget(Long passengerId, Double distance, Integer time, Long orderTime, Long typeId,Long modelId) {
        Employ employ = EmUtil.getEmployInfo();
        view.getManager().add(model.getBudgetPrice(passengerId, employ.company_id,
                distance == null ? 0.0 : distance, time == null ? 0 : time, orderTime, typeId,modelId)
                .subscribe(new MySubscriber<BudgetResult>(context, false,
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
        RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo,
                RouteSearch.DRIVING_MULTI_STRATEGY_FASTEST_SHORTEST, null, null, "");
        routeSearch.calculateDriveRouteAsyn(query);
    }

    @Override
    public void createOrder(Long passengerId, String passengerName, String passengerPhone, long orderTime, String bookAddress, Double bookAddressLat, Double bookAddressLng, String destination, Double destinationLat, Double destinationLng, Double budgetFee, Long cid) {
        Employ employ = EmUtil.getEmployInfo();
        view.getManager().add(model.createOrder(passengerId, passengerName, passengerPhone, orderTime,
                bookAddress, bookAddressLat, bookAddressLng, destination, destinationLat, destinationLng,
                employ.company_id,
//                employ.company_name,
                "11",
                budgetFee, cid, employ.nickName, employ.id).subscribe(
                new MySubscriber<>(context, true, false, zcOrderResult -> view.createSuc(zcOrderResult))
        ));
    }
}