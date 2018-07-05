package com.easymi.zhuanche.fragment.create;

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
 * Created by developerLzh on 2017/11/27 0027.
 */

public class CreateZCModel implements CreateZCContract.Model {

    @Override
    public Observable<ZCTypeResult> queryZCType(Long companyId, Integer serviceType) {
        return ApiManager.getInstance().createApi(Config.HOST, ZCApiService.class)
                .getZCBusiness(EmUtil.getAppKey(), 1, 100, companyId, serviceType)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<PassengerResult> queryPassenger(Long companyId, String companyName, String phone) {
        return ApiManager.getInstance().createApi(Config.HOST, ZCApiService.class)
                .queryPassenger(companyId, companyName, phone, EmUtil.getAppKey(), "supplement")
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<BudgetResult> getBudgetPrice(Long passengerId, Long companyId, Double distance, Integer time, Long orderTime, Long typeId, Long modelId) {
        return ApiManager.getInstance().createApi(Config.HOST, ZCApiService.class)
                .getBudgetPrice(passengerId, companyId, distance, time, orderTime, "batch", typeId, modelId, EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<ZCOrderResult> createOrder(Long passengerId,
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
        return ApiManager.getInstance().createApi(Config.HOST, ZCApiService.class)
                .createOrder(passengerId, passengerName, passengerPhone, orderTime,
                        bookAddress, bookAddressLat, bookAddressLng, destination,
                        destinationLat, destinationLng, companyId, companyName,
                        budgetFee, EmUtil.getAppKey(), cid, orderPerson, orderPersonId,
                        (long) EmUtil.getEmployInfo().vehicle.serviceType)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
