package com.easymi.common.mvp.order;

import com.easymi.common.CommApiService;
import com.easymi.common.result.MultipleOrderResult;
import com.easymi.common.result.QueryOrdersResult;
import com.easymi.component.Config;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.result.EmResult;
import com.easymi.component.utils.EmUtil;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: MyOrderModel
 *@Author: shine
 * Date: 2018/11/23 上午11:37
 * Description:
 * History:
 * @author hufeng
 */
public class MyOrderModel implements MyOrderContract.Model {

    @Override
    public Observable<QueryOrdersResult> indexOrders(int page,int size,String status) {
        return ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .queryMyOrders( page, size,status,EmUtil.getEmployInfo().serviceType)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<MultipleOrderResult> grabZCOrder(Long orderId, Long version) {
        return ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .grabZCOrder(EmUtil.getEmployId(),EmUtil.getEmployInfo().realName,EmUtil.getEmployInfo().phone,orderId,version
                        ,EmUtil.getLastLoc().longitude+""
                        ,EmUtil.getLastLoc().longitude+"")
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<MultipleOrderResult> takeZCOrder(Long orderId, Long version) {
        return ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .takeZCOrder(EmUtil.getEmployId(),EmUtil.getEmployInfo().realName,EmUtil.getEmployInfo().phone,orderId,version
                        ,EmUtil.getLastLoc().longitude+""
                        ,EmUtil.getLastLoc().longitude+"")
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<MultipleOrderResult> takeTaxiOrder(Long orderId, Long version) {
        return ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .takeTaxiOrder(Config.APP_KEY,EmUtil.getEmployInfo().companyId,EmUtil.getEmployId(),orderId)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<EmResult> refuseOrder(Long orderId, String serviceType) {
        return ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .refuseOrder(orderId,serviceType,"临时有事")
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
