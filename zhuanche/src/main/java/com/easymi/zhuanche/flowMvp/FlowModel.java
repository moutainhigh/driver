package com.easymi.zhuanche.flowMvp;

import com.easymi.common.CommApiService;
import com.easymi.component.ComponentService;
import com.easymi.component.Config;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.entity.EmLoc;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.HttpResultFunc2;
import com.easymi.component.result.EmResult;
import com.easymi.component.utils.EmUtil;
import com.easymi.zhuanche.ZCApiService;
import com.easymi.zhuanche.entity.ZCOrder;
import com.easymi.zhuanche.result.ConsumerResult;
import com.easymi.zhuanche.result.ZCOrderResult;
import com.google.gson.JsonElement;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: FlowModel
 *
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class FlowModel implements FlowContract.Model {

    @Override
    public Observable<ZCOrderResult> doAccept(Long orderId, Long version) {
        return ApiManager.getInstance().createApi(Config.HOST, ZCApiService.class)
                .takeOrder(EmUtil.getEmployId(), EmUtil.getEmployInfo().realName, EmUtil.getEmployInfo().phone, orderId, version
                        , EmUtil.getLastLoc().longitude + ""
                        , EmUtil.getLastLoc().longitude + "")
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<ZCOrderResult> findOne(Long orderId) {
        return ApiManager.getInstance().createApi(Config.HOST, ZCApiService.class)
                .indexOrders(orderId, EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<EmResult> refuseOrder(Long orderId, String orderType, String remark) {
        return ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .refuseOrder(orderId, orderType, remark)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<ZCOrderResult> toStart(Long orderId, Long version) {
        return ApiManager.getInstance().createApi(Config.HOST, ZCApiService.class)
                .goToBookAddress(orderId, EmUtil.getAppKey(), version)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<ZCOrderResult> arriveStart(Long orderId, Long version) {
        return ApiManager.getInstance().createApi(Config.HOST, ZCApiService.class)
                .arrivalBookAddress(orderId, EmUtil.getAppKey(), version)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<ZCOrderResult> startWait(Long orderId) {
        return ApiManager.getInstance().createApi(Config.HOST, ZCApiService.class)
                .waitOrder(orderId, EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<ZCOrderResult> startDrive(Long orderId, Long version) {
        EmLoc emLoc = EmUtil.getLastLoc();
        return ApiManager.getInstance().createApi(Config.HOST, ZCApiService.class)
                .goToDistination(orderId, EmUtil.getAppKey(), version, emLoc.longitude, emLoc.latitude, emLoc.address)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<ZCOrderResult> arriveDes(ZCOrder zcOrder, DymOrder order, Long version) {
        EmLoc emLoc = EmUtil.getLastLoc();
        return ApiManager.getInstance().createApi(Config.HOST, ZCApiService.class)
                .arrivalDistination(
                        zcOrder.orderId,
                        EmUtil.getAppKey(),
                        version,
                        emLoc.longitude,
                        emLoc.latitude,
                        emLoc.address
                )
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<ZCOrderResult> changeEnd(Long orderId, Double lat, Double lng, String address) {
        return ApiManager.getInstance().createApi(Config.HOST, ZCApiService.class)
                .changeEnd(orderId, lat, lng, address, EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<EmResult> cancelOrder(Long orderId, String remark) {
        return ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .cancelOrder(orderId, remark)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<ConsumerResult> consumerInfo(Long orderId) {
        return ApiManager.getInstance().createApi(Config.HOST, ZCApiService.class)
                .getConsumer(orderId, EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<JsonElement> payOrder(Long orderId, String payType, Long version) {
        return ApiManager.getInstance().createApi(Config.HOST, ComponentService.class)
                .payOrder(true, orderId, payType)
                .map(new HttpResultFunc2<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<EmResult> taxiSettlement(Long orderId, String orderNo, double fee) {
        EmLoc emLoc = EmUtil.getLastLoc();
        return ApiManager.getInstance().createApi(Config.HOST, ZCApiService.class)
                .taxiSettlement(orderId, orderNo,
                        emLoc.longitude, emLoc.latitude, emLoc.address, fee)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


}
