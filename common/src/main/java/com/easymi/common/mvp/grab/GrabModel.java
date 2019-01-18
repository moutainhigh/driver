package com.easymi.common.mvp.grab;

import android.text.TextUtils;

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
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 * @Author: shine
 * Date: 2018/12/24 下午5:00
 * Description:
 * History:
 */

public class GrabModel implements GrabContract.Model {
    @Override
    public Observable<MultipleOrderResult> queryDJOrder(Long orderId) {
        return ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .queryDJOrder(orderId, EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<MultipleOrderResult> grabDJOrder(Long orderId) {
        return ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .grabDJOrder(orderId, EmUtil.getEmployId(), EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<MultipleOrderResult> takeDJOrder(Long orderId) {
        return ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .takeDJOrder(orderId, EmUtil.getEmployId(), EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<MultipleOrderResult> queryZCOrder(Long orderId) {
        return ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .queryZCOrder(orderId, EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<MultipleOrderResult> grabZCOrder(Long orderId, Long version) {
        return ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .grabZCOrder(EmUtil.getEmployId(), EmUtil.getEmployInfo().realName, EmUtil.getEmployInfo().phone, orderId, version)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<MultipleOrderResult> takeZCOrder(Long orderId, Long version) {
        String realName = "";
        if (EmUtil.getEmployId() != null && !TextUtils.isEmpty(EmUtil.getEmployInfo().realName)) {
            realName = EmUtil.getEmployInfo().realName;
        }
        return ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .takeZCOrder(EmUtil.getEmployId(), realName, EmUtil.getEmployInfo().phone, orderId, version)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<MultipleOrderResult> queryTaxiOrder(Long orderId) {
        return ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .queryTaxiOrder(orderId, EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<MultipleOrderResult> grabTaxiOrder(Long orderId, Long version) {
        return ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .grabTaxiOrder(Config.APP_KEY, EmUtil.getEmployInfo().companyId, EmUtil.getEmployId(), orderId)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<MultipleOrderResult> takeTaxiOrder(Long orderId, Long version) {
        return ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .takeTaxiOrder(Config.APP_KEY, EmUtil.getEmployInfo().companyId, EmUtil.getEmployId(), orderId)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
