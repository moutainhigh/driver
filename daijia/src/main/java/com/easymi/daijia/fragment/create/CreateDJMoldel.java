package com.easymi.daijia.fragment.create;

import com.easymi.component.Config;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.daijia.DJApiService;
import com.easymi.daijia.result.BudgetResult;
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
                .getBusiness(companyId, "daijia", Config.APP_KEY)
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
}
