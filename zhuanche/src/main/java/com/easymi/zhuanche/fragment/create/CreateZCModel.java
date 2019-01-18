package com.easymi.zhuanche.fragment.create;

import com.easymi.common.result.CreateOrderResult;
import com.easymi.component.Config;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.utils.EmUtil;
import com.easymi.zhuanche.ZCApiService;
import com.easymi.zhuanche.result.BudgetResult;
import com.easymi.zhuanche.result.ZCOrderResult;
import com.easymi.zhuanche.result.ZCTypeResult;
import com.easymi.zhuanche.result.PassengerResult;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: FinishActivity
 *@Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class CreateZCModel implements CreateZCContract.Model {

    @Override
    public Observable<ZCTypeResult> queryZCType(String adcode,String citycode,int carModel,double lat,double lng) {
        return ApiManager.getInstance().createApi(Config.HOST, ZCApiService.class)
                .getZCBusiness( adcode, citycode, carModel, lat, lng)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<PassengerResult> queryPassenger(Long companyId, String companyName, String phone) {
        return ApiManager.getInstance().createApi(Config.HOST, ZCApiService.class)
                .queryPassenger(phone)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<BudgetResult> getBudgetPrice(Long businessId, Long companyId, Double distance,Integer time, Long modelId) {
        return ApiManager.getInstance().createApi(Config.HOST, ZCApiService.class)
                .getBudgetPrice(businessId, companyId, distance, time, modelId)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<CreateOrderResult> createOrder(Long bookTime,
                                                     Double budgetFee,
                                                     Long businessId,
                                                     String channelAlias,
                                                     Long companyId,
                                                     Long driverId,
                                                     String driverName,
                                                     String driverPhone,
                                                     Long modelId,
                                                     String orderAddress,
                                                     Long passengerId,
                                                     String passengerName,
                                                     String passengerPhone,
                                                     String serviceType) {
        return ApiManager.getInstance().createApi(Config.HOST, ZCApiService.class)
                .createOrder(bookTime,
                        budgetFee,
                        businessId,
                        channelAlias,
                        companyId,
                        driverId,
                        driverName,
                        driverPhone,
                        modelId,
                        orderAddress,
                        passengerId,
                        passengerName,
                        passengerPhone,
                        serviceType
                )
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
