package com.easymi.daijia.flowMvp;

import com.easymi.component.Config;
import com.easymi.component.app.XApp;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.daijia.DJApiService;
import com.easymi.daijia.entity.DJOrder;
import com.easymi.daijia.result.DJOrderResult;

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
                .takeOrder(orderId, XApp.getMyPreferences().getLong(Config.SP_DRIVERID, -1), Config.APP_KEY)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<DJOrderResult> findOne(Long orderId) {
        return ApiManager.getInstance().createApi(Config.HOST, DJApiService.class)
                .indexOrders(orderId, Config.APP_KEY)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<DJOrderResult> refuseOrder(Long orderId, String remark) {
        return ApiManager.getInstance().createApi(Config.HOST, DJApiService.class)
                .refuseOrder(orderId, XApp.getMyPreferences().getLong(Config.SP_DRIVERID, -1), Config.APP_KEY, remark)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<DJOrderResult> toStart(Long orderId) {
        return ApiManager.getInstance().createApi(Config.HOST, DJApiService.class)
                .goToBookAddress(orderId, Config.APP_KEY)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<DJOrderResult> arriveStart(Long orderId) {
        return ApiManager.getInstance().createApi(Config.HOST, DJApiService.class)
                .arrivalBookAddress(orderId, Config.APP_KEY)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<DJOrderResult> startWait(Long orderId) {
        return ApiManager.getInstance().createApi(Config.HOST, DJApiService.class)
                .waitOrder(orderId, Config.APP_KEY)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<DJOrderResult> startDrive(Long orderId) {
        return ApiManager.getInstance().createApi(Config.HOST, DJApiService.class)
                .goToDistination(orderId, Config.APP_KEY)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<DJOrderResult> arriveDes(DJOrder djOrder) {
        return ApiManager.getInstance().createApi(Config.HOST, DJApiService.class)
                .arrivalDistination(djOrder.orderId, Config.APP_KEY, 0.0, 0.0,
                        "", 0.0, 0.0, 0, 0.0, 0,
                        0.0, 0.0, 0.0, 0.0,
                        0.0, 0.0, 0.0)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<DJOrderResult> changeEnd(Long orderId, Double lat, Double lng, String address) {
        return ApiManager.getInstance().createApi(Config.HOST, DJApiService.class)
                .changeEnd(orderId, lat, lng, address, Config.APP_KEY)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<DJOrderResult> cancelOrder(Long orderId, String remark) {
        return ApiManager.getInstance().createApi(Config.HOST, DJApiService.class)
                .cancelOrder(orderId, XApp.getMyPreferences().getLong(Config.SP_DRIVERID, -1), Config.APP_KEY, remark)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
