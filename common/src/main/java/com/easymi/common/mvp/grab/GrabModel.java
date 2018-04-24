package com.easymi.common.mvp.grab;

import com.easymi.common.CommApiService;
import com.easymi.common.result.MultipleOrderResult;
import com.easymi.component.Config;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.utils.EmUtil;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by developerLzh on 2017/11/22 0022.
 */

public class GrabModel implements GrabContract.Model {
    @Override
    public Observable<MultipleOrderResult> queryOrder(Long orderId) {
        return ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .queryDJOrder(orderId, EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<MultipleOrderResult> grabOrder(Long orderId) {
        return ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .grabDJOrder(orderId, EmUtil.getEmployId(), EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<MultipleOrderResult> takeOrder(Long orderId) {
        return ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .takeDJOrder(orderId, EmUtil.getEmployId(), EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
