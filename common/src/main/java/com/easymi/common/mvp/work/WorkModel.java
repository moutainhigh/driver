package com.easymi.common.mvp.work;

import com.easymi.common.CommApiService;
import com.easymi.common.result.QueryOrdersResult;
import com.easymi.component.Config;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.result.EmResult;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by developerLzh on 2017/11/17 0017.
 */

public class WorkModel implements WorkContract.Model {

    @Override
    public Observable<QueryOrdersResult> indexOrders(Long driverId, String appKey) {
        return ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .queryAllOrders(driverId, appKey,1,100)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<EmResult> online(Long driverId, String appKey) {
        return ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .online(driverId, appKey)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
