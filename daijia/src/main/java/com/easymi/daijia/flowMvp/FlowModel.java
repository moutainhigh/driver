package com.easymi.daijia.flowMvp;

import com.easymi.component.Config;
import com.easymi.component.app.XApp;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.daijia.DJApiService;
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
}
