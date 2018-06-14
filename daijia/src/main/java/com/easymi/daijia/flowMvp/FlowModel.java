package com.easymi.daijia.flowMvp;

import com.easymi.component.Config;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.entity.EmLoc;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.result.EmResult;
import com.easymi.component.utils.EmUtil;
import com.easymi.daijia.DJApiService;
import com.easymi.daijia.result.ConsumerResult;
import com.easymi.daijia.result.DJOrderResult;
import com.easymi.daijia.result.OrderFeeResult;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by liuzihao on 2017/11/15.
 */

public class FlowModel implements FlowContract.Model {

    @Override
    public Observable<DJOrderResult> doAccept(Long orderId) {
        return ApiManager.getInstance().createApi(Config.HOST, DJApiService.class)
                .takeOrder(orderId, EmUtil.getEmployId(), EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<DJOrderResult> findOne(Long orderId) {
        return ApiManager.getInstance().createApi(Config.HOST, DJApiService.class)
                .indexOrders(orderId, EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<EmResult> refuseOrder(Long orderId, String remark) {
        return ApiManager.getInstance().createApi(Config.HOST, DJApiService.class)
                .refuseOrder(orderId, EmUtil.getEmployId(), EmUtil.getAppKey(), remark)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<DJOrderResult> toStart(Long orderId) {
        return ApiManager.getInstance().createApi(Config.HOST, DJApiService.class)
                .goToBookAddress(orderId, EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<DJOrderResult> arriveStart(Long orderId) {
        EmLoc loc = EmUtil.getLastLoc();
        return ApiManager.getInstance().createApi(Config.HOST, DJApiService.class)
                .arrivalBookAddress(orderId, EmUtil.getAppKey(),
                        loc.street + "  " + loc.poiName, loc.latitude, loc.longitude)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<DJOrderResult> startWait(Long orderId) {
        return ApiManager.getInstance().createApi(Config.HOST, DJApiService.class)
                .waitOrder(orderId, EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<DJOrderResult> startDrive(Long orderId) {
        return ApiManager.getInstance().createApi(Config.HOST, DJApiService.class)
                .goToDistination(orderId, EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<DJOrderResult> arriveDes(DymOrder dymOrder) {
        EmLoc loc = EmUtil.getLastLoc();
        return ApiManager.getInstance().createApi(Config.HOST, DJApiService.class)
                .arrivalDistination(dymOrder.orderId, EmUtil.getAppKey(), dymOrder.paymentFee, dymOrder.extraFee,
                        dymOrder.remark, dymOrder.distance, dymOrder.disFee, dymOrder.travelTime,
                        dymOrder.travelFee, dymOrder.waitTime,
                        dymOrder.waitTimeFee, 0.0, 0.0, dymOrder.couponFee,
                        dymOrder.orderTotalFee, dymOrder.orderShouldPay, dymOrder.startFee,
                        loc.street + "  " + loc.poiName, loc.latitude, loc.longitude, dymOrder.minestMoney)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<DJOrderResult> changeEnd(Long orderId, Double lat, Double lng, String address) {
        return ApiManager.getInstance().createApi(Config.HOST, DJApiService.class)
                .changeEnd(orderId, lat, lng, address, EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<EmResult> cancelOrder(Long orderId, String remark) {
        return ApiManager.getInstance().createApi(Config.HOST, DJApiService.class)
                .cancelOrder(orderId, EmUtil.getEmployId(), EmUtil.getAppKey(), remark)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<ConsumerResult> consumerInfo(Long orderId) {
        return ApiManager.getInstance().createApi(Config.HOST, DJApiService.class)
                .getConsumer(orderId, EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<EmResult> payOrder(Long orderId, String payType) {
        return ApiManager.getInstance().createApi(Config.HOST, DJApiService.class)
                .payOrder(orderId, EmUtil.getAppKey(), payType)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<OrderFeeResult> getOrderFee(Long orderId, Long driverId, String orderType, Integer isArrive) {

        EmLoc emLoc = EmUtil.getLastLoc();

        return ApiManager.getInstance().createApi(Config.HOST, DJApiService.class)
//                .getOrderFee(orderId, driverId, orderType, EmUtil.getAppKey(), 30.7736989546,104.1627502441, isArrive)
                .getOrderFee(orderId, driverId, orderType, EmUtil.getAppKey(), emLoc.latitude, emLoc.longitude, isArrive)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
