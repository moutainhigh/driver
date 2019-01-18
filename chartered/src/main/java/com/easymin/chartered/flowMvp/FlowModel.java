package com.easymin.chartered.flowMvp;
import android.content.Context;

import com.easymi.common.entity.OrderCustomer;
import com.easymi.component.Config;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.HttpResultFunc2;
import com.easymi.component.network.HttpResultFunc3;
import com.easymi.component.result.EmResult;
import com.easymi.component.result.EmResult2;
import com.easymi.component.utils.EmUtil;
import com.easymin.chartered.CharteredService;
import com.easymin.chartered.result.OrderListResult;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: FlowModel
 *@Author: shine
 * Date: 2018/12/18 下午1:59
 * Description:
 * History:
 */
public class FlowModel implements FlowContract.Model {

    private Context context;

    public FlowModel(Context context) {
        this.context = context;
    }


    @Override
    public Observable<OrderListResult> findOne(long orderId) {
        return ApiManager.getInstance().createApi(Config.HOST, CharteredService.class)
                .findOne(orderId)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<EmResult> changeStauts( Long orderId, int status) {
        return ApiManager.getInstance().createApi(Config.HOST, CharteredService.class)
                .changeStatus(EmUtil.getEmployInfo().companyId,EmUtil.getLastLoc().address,EmUtil.getEmployId(),EmUtil.getLastLoc().latitude,EmUtil.getLastLoc().longitude,
                        orderId,status)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<EmResult> orderConfirm(long orderId, long version) {
        return ApiManager.getInstance().createApi(Config.HOST, CharteredService.class)
                .orderConfirm(orderId,version)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}