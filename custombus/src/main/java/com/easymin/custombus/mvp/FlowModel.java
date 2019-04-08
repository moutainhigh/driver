package com.easymin.custombus.mvp;

import com.easymi.component.Config;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.HttpResultFunc3;
import com.easymi.component.result.EmResult2;
import com.easymin.custombus.DZBusApiService;
import com.easymin.custombus.entity.Customer;
import com.easymin.custombus.entity.OrdersResult;
import com.easymin.custombus.entity.StationResult;
import com.easymin.custombus.entity.TimeResult;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: FlowModel
 * @Author: hufeng
 * @Date: 2019/2/18 下午5:14
 * @Description:
 * @History:
 */
public class FlowModel implements FlowContract.Model{


    @Override
    public Observable<EmResult2<Object>> startStation(long scheduleId) {
        return ApiManager.getInstance().createApi(Config.HOST, DZBusApiService.class)
                .start(scheduleId)
                .filter(new HttpResultFunc3<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<EmResult2<Object>> arriveStation(long scheduleId, long stationId) {
        return ApiManager.getInstance().createApi(Config.HOST, DZBusApiService.class)
                .arriveStation(scheduleId)
                .filter(new HttpResultFunc3<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<EmResult2<Object>> endStation(long scheduleId) {
        return ApiManager.getInstance().createApi(Config.HOST, DZBusApiService.class)
                .finish(scheduleId)
                .filter(new HttpResultFunc3<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<StationResult> findBusOrderById(long id) {
        return ApiManager.getInstance().createApi(Config.HOST, DZBusApiService.class)
                .findBusInfoById(id)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<EmResult2<Object>> toNextStation(long scheduleId, long stationId) {
        return ApiManager.getInstance().createApi(Config.HOST, DZBusApiService.class)
                .toNextStation(scheduleId, stationId)
                .filter(new HttpResultFunc3<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<TimeResult> chechTickets(long id) {
        return ApiManager.getInstance().createApi(Config.HOST, DZBusApiService.class)
                .startCheck(id)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<OrdersResult> queryOrders(long scheduleId, long stationId) {
        return ApiManager.getInstance().createApi(Config.HOST, DZBusApiService.class)
                .queryOrders(scheduleId,stationId)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<EmResult2<Customer>> queryByRideCode(String rideCode) {
        return ApiManager.getInstance().createApi(Config.HOST, DZBusApiService.class)
                .queryByRideCode(rideCode)
                .filter(new HttpResultFunc3<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<EmResult2<Object>> checkRideCode(String rideCode) {
        return ApiManager.getInstance().createApi(Config.HOST, DZBusApiService.class)
                .checkRideCode(rideCode)
                .filter(new HttpResultFunc3<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
