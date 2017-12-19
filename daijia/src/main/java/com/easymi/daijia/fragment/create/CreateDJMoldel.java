package com.easymi.daijia.fragment.create;

import com.easymi.component.Config;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.daijia.DJApiService;
import com.easymi.daijia.result.BudgetResult;
import com.easymi.daijia.result.DJOrderResult;
import com.easymi.daijia.result.DJTypeResult;
import com.easymi.daijia.result.PassengerResult;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by developerLzh on 2017/11/27 0027.
 */

public class CreateDJMoldel implements CreateDJContract.Model {

    @Override
    public Observable<DJTypeResult> queryDJType(Long companyId) {
        return ApiManager.getInstance().createApi(Config.HOST, DJApiService.class)
                .getBusiness(companyId, Config.DAIJIA, Config.APP_KEY)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<PassengerResult> queryPassenger(Long companyId, String companyName, String phone) {
        return ApiManager.getInstance().createApi(Config.HOST, DJApiService.class)
                .queryPassenger(companyId, companyName, phone, Config.APP_KEY)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<BudgetResult> getBudgetPrice(Long passengerId, Long companyId, Double distance, Integer time, Long orderTime, Long typeId) {
        return ApiManager.getInstance().createApi(Config.HOST, DJApiService.class)
                .getBudgetPrice(passengerId, companyId, distance, time, orderTime, "batch", typeId, Config.APP_KEY)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<DJOrderResult> createOrder(Long passengerId, String passengerName, String passengerPhone, long orderTime, String bookAddress, Double bookAddressLat, Double bookAddressLng, String destination, Double destinationLat, Double destinationLng, Long companyId, String companyName, Double budgetFee, Long cid, String orderPerson, Long orderPersonId) {
        return ApiManager.getInstance().createApi(Config.HOST, DJApiService.class)
                .createOrder(passengerId,passengerName,passengerPhone,orderTime,
                        bookAddress,bookAddressLat,bookAddressLng,destination,
                        destinationLat,destinationLng,companyId,companyName,
                        budgetFee,Config.APP_KEY,cid,orderPerson,orderPersonId)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
