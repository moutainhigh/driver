//package com.easymin.passengerbus.flowMvp;
//
//import android.content.Context;
//
//import com.easymi.common.entity.OrderCustomer;
//import com.easymi.component.Config;
//import com.easymi.component.network.ApiManager;
//import com.easymi.component.network.HttpResultFunc2;
//import com.easymi.component.network.HttpResultFunc3;
//import com.easymi.component.result.EmResult2;
//import com.easymin.passengerbus.BusApiService;
//
//import java.util.List;
//
//import rx.Observable;
//import rx.android.schedulers.AndroidSchedulers;
//import rx.schedulers.Schedulers;
//
///**
// * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
// * FileName: FlowModel
// * Author: shine
// * Date: 2018/12/18 下午1:59
// * Description:
// * History:
// */
//public class FlowModel implements FlowContract.Model {
//
//    private Context context;
//
//    public FlowModel(Context context) {
//        this.context = context;
//    }
//
//    @Override
//    public Observable<EmResult2<List<ZXOrder>>> getZxOrderList() {
//        return ApiManager.getInstance().createApi(Config.HOST, BusApiService.class)
//                .queryDriverSchedule()
//                .filter(new HttpResultFunc3<>())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//    }
//
//    @Override
//    public Observable<Object> startSend(long orderId) {
//        return ApiManager.getInstance().createApi(Config.HOST, BusApiService.class)
//                .runSchedule(orderId)
//                .map(new HttpResultFunc2<>())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//    }
//
//    @Override
//    public Observable<Object> startSchedule(long orderId) {
//        return ApiManager.getInstance().createApi(Config.HOST, BusApiService.class)
//                .startSchedule(orderId)
//                .map(new HttpResultFunc2<>())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//    }
//
//    @Override
//    public Observable<Object> finishSchedule(long orderId) {
//        return ApiManager.getInstance().createApi(Config.HOST, BusApiService.class)
//                .finishTask(orderId)
//                .map(new HttpResultFunc2<>())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//    }
//
//    @Override
//    public Observable<Object> arriveStart(long id) {
//        return ApiManager.getInstance().createApi(Config.HOST, BusApiService.class)
//                .arriveStart(id)
//                .map(new HttpResultFunc2<>())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//    }
//
//    @Override
//    public Observable<Object> acceptCustomer(long id) {
//        return ApiManager.getInstance().createApi(Config.HOST, BusApiService.class)
//                .acceptCustomer(id)
//                .map(new HttpResultFunc2<>())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//    }
//
//    @Override
//    public Observable<Object> jumpCustomer(long id) {
//        return ApiManager.getInstance().createApi(Config.HOST, BusApiService.class)
//                .jumpCustomer(id)
//                .map(new HttpResultFunc2<>())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//    }
//
//    @Override
//    public Observable<Object> sendCustomer(long id) {
//        return ApiManager.getInstance().createApi(Config.HOST, BusApiService.class)
//                .sendCustomer(id)
//                .map(new HttpResultFunc2<>())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//    }
//
//    @Override
//    public Observable<EmResult2<List<OrderCustomer>>> geOrderCustomers(long orderId) {
//        return ApiManager.getInstance().createApi(Config.HOST, BusApiService.class)
//                .getOrderCustomers(orderId, "5,10,15,20")
//                .filter(new HttpResultFunc3<>())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//    }
//}