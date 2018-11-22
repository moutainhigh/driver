package com.easymi.cityline.flowMvp;

import android.content.Context;

import com.easymi.cityline.CLService;
import com.easymi.cityline.entity.OrderCustomer;
import com.easymi.cityline.entity.ZXOrder;
import com.easymi.cityline.result.OrderCustomerResult;
import com.easymi.cityline.result.ZxOrderResult;
import com.easymi.component.Config;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.HttpResultFunc2;
import com.easymi.component.network.HttpResultFunc3;
import com.easymi.component.result.EmResult;
import com.easymi.component.result.EmResult2;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by liuzihao on 2018/11/15.
 */

public class FlowModel implements FlowContract.Model {

    private Context context;

    public FlowModel(Context context) {
        this.context = context;
    }

    @Override
    public Observable<EmResult2<List<ZXOrder>>> getZxOrderList() {
        return ApiManager.getInstance().createApi(Config.HOST, CLService.class)
                .queryDriverSchedule()
                .filter(new HttpResultFunc3<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Object> startSend(long orderId) {
        return ApiManager.getInstance().createApi(Config.HOST, CLService.class)
                .runSchedule(orderId)
                .map(new HttpResultFunc2<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Object> startSchedule(long orderId) {
        return ApiManager.getInstance().createApi(Config.HOST, CLService.class)
                .startSchedule(orderId)
                .map(new HttpResultFunc2<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Object> finishSchedule(long orderId) {
        return ApiManager.getInstance().createApi(Config.HOST, CLService.class)
                .finishTask(orderId)
                .map(new HttpResultFunc2<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Object> arriveStart(long id) {
        return ApiManager.getInstance().createApi(Config.HOST, CLService.class)
                .arriveStart(id)
                .map(new HttpResultFunc2<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Object> acceptCustomer(long id) {
        return ApiManager.getInstance().createApi(Config.HOST, CLService.class)
                .acceptCustomer(id)
                .map(new HttpResultFunc2<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Object> jumpCustomer(long id) {
        return ApiManager.getInstance().createApi(Config.HOST, CLService.class)
                .jumpCustomer(id)
                .map(new HttpResultFunc2<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Object> sendCustomer(long id) {
        return ApiManager.getInstance().createApi(Config.HOST, CLService.class)
                .sendCustomer(id)
                .map(new HttpResultFunc2<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<EmResult2<List<OrderCustomer>>> geOrderCustomers(long orderId) {
        return ApiManager.getInstance().createApi(Config.HOST, CLService.class)
                .getOrderCustomers(orderId)
                .filter(new HttpResultFunc3<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
