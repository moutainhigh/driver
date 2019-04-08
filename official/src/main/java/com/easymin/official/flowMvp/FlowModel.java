package com.easymin.official.flowMvp;

import com.easymi.component.Config;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.entity.EmLoc;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.result.EmResult;
import com.easymi.component.utils.EmUtil;
import com.easymin.official.OfficialService;
import com.easymin.official.result.GovOrderResult;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: FlowModel
 * @Author: hufeng
 * @Date: 2019/3/26 上午9:03
 * @Description:
 * @History:
 */
public class FlowModel implements FlowContract.Model{

    @Override
    public Observable<GovOrderResult> findOne(Long orderId) {
        return ApiManager.getInstance().createApi(Config.HOST, OfficialService.class)
                .findOne(orderId)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<GovOrderResult> toStart(Long orderId, Long version) {
        return ApiManager.getInstance().createApi(Config.HOST, OfficialService.class)
                .goToBookAddress(orderId,version)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<GovOrderResult> arriveStart(Long orderId, Long version) {
        return ApiManager.getInstance().createApi(Config.HOST, OfficialService.class)
                .arrivalBookAddress(orderId,version)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<GovOrderResult> startDrive(Long orderId, Long version) {
        EmLoc emLoc = EmUtil.getLastLoc();
        return ApiManager.getInstance().createApi(Config.HOST, OfficialService.class)
                .goToDistination(orderId,version, emLoc.longitude, emLoc.latitude, emLoc.address)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<GovOrderResult> arriveDes(Long orderId, DymOrder order, Long version) {
        EmLoc emLoc = EmUtil.getLastLoc();
        return ApiManager.getInstance().createApi(Config.HOST, OfficialService.class)
                .arrivalDistination(orderId,version,
                        emLoc.longitude,
                        emLoc.latitude,
                        emLoc.address)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<EmResult> cancelOrder(Long orderId, String remark) {
        return ApiManager.getInstance().createApi(Config.HOST, OfficialService.class)
                .cancelOrder(orderId, remark)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<EmResult> confirmOrder(Long orderId, Long version, String voucher) {
        return ApiManager.getInstance().createApi(Config.HOST, OfficialService.class)
                .confirmOrder(orderId, version,voucher)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
