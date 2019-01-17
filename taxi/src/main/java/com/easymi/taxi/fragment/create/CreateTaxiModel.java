package com.easymi.taxi.fragment.create;

import com.easymi.component.Config;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.utils.EmUtil;
import com.easymi.taxi.TaxiApiService;
import com.easymi.taxi.result.BudgetResult;
import com.easymi.taxi.result.TaxiOrderResult;
import com.easymi.taxi.result.ZCTypeResult;
import com.easymi.taxi.result.PassengerResult;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: FinishActivity
 * Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class CreateTaxiModel implements CreateTaxiContract.Model {

    @Override
    public Observable<ZCTypeResult> queryZCType(Long companyId, Integer serviceType) {
        return ApiManager.getInstance().createApi(Config.HOST, TaxiApiService.class)
                .getZCBusiness(EmUtil.getAppKey(), 1, 100, companyId, serviceType)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<PassengerResult> queryPassenger(Long companyId, String companyName, String phone) {
        return ApiManager.getInstance().createApi(Config.HOST, TaxiApiService.class)
                .queryPassenger(companyId, companyName, phone, EmUtil.getAppKey(), "supplement")
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<BudgetResult> getBudgetPrice(Long passengerId, Long companyId, Double distance, Integer time, Long orderTime, Long typeId, Long modelId) {
        return ApiManager.getInstance().createApi(Config.HOST, TaxiApiService.class)
                .getBudgetPrice(passengerId, companyId, distance, time, orderTime, "supplement", typeId, modelId, EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<TaxiOrderResult> createOrder(Long passengerId,
                                                   String passengerName,
                                                   String passengerPhone,
                                                   long orderTime,
                                                   String bookAddress,
                                                   Double bookAddressLat,
                                                   Double bookAddressLng,
                                                   String destination,
                                                   Double destinationLat,
                                                   Double destinationLng,
                                                   Long companyId,
                                                   String companyName,
                                                   Double budgetFee,
                                                   Long cid,
                                                   String orderPerson,
                                                   Long orderPersonId) {
        return ApiManager.getInstance().createApi(Config.HOST, TaxiApiService.class)
                .createOrder(passengerId, passengerName, passengerPhone, orderTime,
                        bookAddress, bookAddressLat, bookAddressLng, destination,
                        destinationLat, destinationLng, companyId, companyName,
                        budgetFee, EmUtil.getAppKey(), cid, orderPerson, orderPersonId
//                        , (long) EmUtil.getEmployInfo().vehicle.serviceType
                        , 1L
                )
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
