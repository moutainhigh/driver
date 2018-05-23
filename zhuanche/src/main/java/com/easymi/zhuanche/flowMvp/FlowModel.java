package com.easymi.zhuanche.flowMvp;

import com.easymi.component.Config;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.entity.EmLoc;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.result.EmResult;
import com.easymi.component.utils.EmUtil;
import com.easymi.zhuanche.ZCApiService;
import com.easymi.zhuanche.result.ConsumerResult;
import com.easymi.zhuanche.result.ZCOrderResult;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by liuzihao on 2017/11/15.
 */

public class FlowModel implements FlowContract.Model {

    @Override
    public Observable<ZCOrderResult> doAccept(Long orderId) {
        return ApiManager.getInstance().createApi(Config.HOST, ZCApiService.class)
                .takeOrder(orderId, EmUtil.getEmployId(), EmUtil.getAppKey())
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
    public Observable<EmResult> refuseOrder(Long orderId, String remark) {
        return ApiManager.getInstance().createApi(Config.HOST, ZCApiService.class)
                .refuseOrder(orderId, EmUtil.getEmployId(), EmUtil.getAppKey(), remark)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<ZCOrderResult> toStart(Long orderId) {
        return ApiManager.getInstance().createApi(Config.HOST, ZCApiService.class)
                .goToBookAddress(orderId, EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<ZCOrderResult> arriveStart(Long orderId) {
        EmLoc loc = EmUtil.getLastLoc();
        return ApiManager.getInstance().createApi(Config.HOST, ZCApiService.class)
                .arrivalBookAddress(orderId, EmUtil.getAppKey(),
                        loc.street + "  " + loc.poiName, loc.latitude, loc.longitude)
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
    public Observable<ZCOrderResult> startDrive(Long orderId) {
        return ApiManager.getInstance().createApi(Config.HOST, ZCApiService.class)
                .goToDistination(orderId, EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<ZCOrderResult> arriveDes(DymOrder dymOrder) {
        EmLoc loc = EmUtil.getLastLoc();
        return ApiManager.getInstance().createApi(Config.HOST, ZCApiService.class)
                .arrivalDistination(dymOrder.orderId, EmUtil.getAppKey(), dymOrder.paymentFee, dymOrder.extraFee,
                        dymOrder.remark, dymOrder.distance, dymOrder.disFee, dymOrder.travelTime,
                        dymOrder.travelFee, dymOrder.waitTime,
                        dymOrder.waitTimeFee, 0.0, 0.0, dymOrder.couponFee,
                        dymOrder.orderTotalFee, dymOrder.orderShouldPay, dymOrder.startFee,
                        loc.street + "  " + loc.poiName, loc.latitude, loc.longitude,
                        dymOrder.minestMoney, dymOrder.peakCost, dymOrder.nightPrice, dymOrder.lowSpeedCost, dymOrder.lowSpeedTime)
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
        return ApiManager.getInstance().createApi(Config.HOST, ZCApiService.class)
                .cancelOrder(orderId, EmUtil.getEmployId(), EmUtil.getAppKey(), remark)
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
    public Observable<EmResult> payOrder(Long orderId, String payType) {
        return ApiManager.getInstance().createApi(Config.HOST, ZCApiService.class)
                .payOrder(orderId, EmUtil.getAppKey(), payType)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
