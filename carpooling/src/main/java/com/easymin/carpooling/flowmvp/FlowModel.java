package com.easymin.carpooling.flowmvp;

import android.content.Context;

import com.easymi.component.Config;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc2;
import com.easymin.carpooling.CarPoolApiService;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 *
 * @Author: hufeng
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class FlowModel implements FlowContract.Model {

    private Context context;

    public FlowModel(Context context) {
        this.context = context;
    }

    @Override
    public Observable<Object> startSend(long orderId) {
        return ApiManager.getInstance().createApi(Config.HOST, CarPoolApiService.class)
                .runSchedule(orderId)
                .map(new HttpResultFunc2<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Object> startSchedule(long orderId) {
        return ApiManager.getInstance().createApi(Config.HOST, CarPoolApiService.class)
                .startSchedule(orderId)
                .map(new HttpResultFunc2<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Object> finishSchedule(long orderId) {
        return ApiManager.getInstance().createApi(Config.HOST, CarPoolApiService.class)
                .finishTask(orderId)
                .map(new HttpResultFunc2<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Object> gotoStart(long orderId) {
        return ApiManager.getInstance().createApi(Config.HOST, CarPoolApiService.class)
                .gotoStart(orderId)
                .map(new HttpResultFunc2<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Object> arriveStart(long id) {
        return ApiManager.getInstance().createApi(Config.HOST, CarPoolApiService.class)
                .arriveStart(id)
                .map(new HttpResultFunc2<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Object> acceptCustomer(long id) {
        return ApiManager.getInstance().createApi(Config.HOST, CarPoolApiService.class)
                .acceptCustomer(id)
                .map(new HttpResultFunc2<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Object> jumpCustomer(long id) {
        return ApiManager.getInstance().createApi(Config.HOST, CarPoolApiService.class)
                .jumpCustomer(id)
                .map(new HttpResultFunc2<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Object> sendCustomer(long id) {
        return ApiManager.getInstance().createApi(Config.HOST, CarPoolApiService.class)
                .sendCustomer(id)
                .map(new HttpResultFunc2<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
