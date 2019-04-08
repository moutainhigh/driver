package com.easymin.passengerbus.flowmvp;

import android.content.Context;

import com.easymi.component.Config;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc3;
import com.easymi.component.result.EmResult2;
import com.easymin.passengerbus.BusApiService;
import com.easymin.passengerbus.entity.BusStationResult;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: FlowModel
 * @Author: shine
 * Date: 2018/12/18 下午1:59
 * Description:
 * History:
 */
public class FlowModel implements FlowContract.Model {


    private Context context;

    public FlowModel(Context context) {
        this.context = context;
    }

    @Override
    public Observable<EmResult2<Object>> startStation(long scheduleId) {
        return ApiManager.getInstance().createApi(Config.HOST, BusApiService.class)
                .start(scheduleId)
                .filter(new HttpResultFunc3<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<EmResult2<Object>> arriveStation(long scheduleId, long stationId) {
        return ApiManager.getInstance().createApi(Config.HOST, BusApiService.class)
                .arriveStation(scheduleId, stationId)
                .filter(new HttpResultFunc3<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<EmResult2<Object>> endStation(long scheduleId) {
        return ApiManager.getInstance().createApi(Config.HOST, BusApiService.class)
                .finish(scheduleId)
                .filter(new HttpResultFunc3<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<EmResult2<BusStationResult>> findBusOrderById(long id) {
        return ApiManager.getInstance().createApi(Config.HOST, BusApiService.class)
                .findBusInfoById(id)
                .filter(new HttpResultFunc3<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<EmResult2<Object>> toNextStation(long scheduleId, long stationId) {
        return ApiManager.getInstance().createApi(Config.HOST, BusApiService.class)
                .toNextStation(scheduleId, stationId)
                .filter(new HttpResultFunc3<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


}