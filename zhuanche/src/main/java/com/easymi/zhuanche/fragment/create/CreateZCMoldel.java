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

public class CreateZCMoldel implements CreateZCContract.Model {

    @Override
    public Observable<ZCTypeResult> queryZCType(Long companyId) {
        return ApiManager.getInstance().createApi(Config.HOST, ZCApiService.class)
                .getBusiness(companyId, Config.DAIJIA, EmUtil.getAppKey())
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
    public Observable<BudgetResult> getBudgetPrice(Long passengerId, Long companyId, Double distance, Integer time, Long orderTime, Long typeId) {
        return ApiManager.getInstance().createApi(Config.HOST, ZCApiService.class)
                .getBudgetPrice(passengerId, companyId, distance, time, orderTime, "batch", typeId, EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<ZCOrderResult> createOrder(Long passengerId, String passengerName, String passengerPhone, long orderTime, String bookAddress, Double bookAddressLat, Double bookAddressLng, String destination, Double destinationLat, Double destinationLng, Long companyId, String companyName, Double budgetFee, Long cid, String orderPerson, Long orderPersonId) {
        return ApiManager.getInstance().createApi(Config.HOST, ZCApiService.class)
                .createOrder(passengerId, passengerName, passengerPhone, orderTime,
                        bookAddress, bookAddressLat, bookAddressLng, destination,
                        destinationLat, destinationLng, companyId, companyName,
                        budgetFee, EmUtil.getAppKey(), cid, orderPerson, orderPersonId)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
