package com.easymi.zhuanche.flowMvp;

import com.easymi.common.CommApiService;
import com.easymi.common.entity.PushFee;
import com.easymi.common.entity.PushFeeEmploy;
import com.easymi.common.entity.PushFeeLoc;
import com.easymi.common.entity.PushFeeOrder;
import com.easymi.common.result.GetFeeResult;
import com.easymi.component.Config;
import com.easymi.component.ZCOrderStatus;
import com.easymi.component.app.XApp;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.entity.EmLoc;
import com.easymi.component.entity.Employ;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.GsonUtil;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.HttpResultFunc2;
import com.easymi.component.result.EmResult;
import com.easymi.component.utils.EmUtil;
import com.easymi.zhuanche.ZCApiService;
import com.easymi.zhuanche.entity.ZCOrder;
import com.easymi.zhuanche.result.ConsumerResult;
import com.easymi.zhuanche.result.ZCOrderResult;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: FlowModel
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class FlowModel implements FlowContract.Model {

    @Override
    public Observable<ZCOrderResult> doAccept(Long orderId, Long version) {
        return ApiManager.getInstance().createApi(Config.HOST, ZCApiService.class)
                .takeOrder(EmUtil.getEmployId(), EmUtil.getEmployInfo().realName, EmUtil.getEmployInfo().phone, orderId, version
                        ,EmUtil.getLastLoc().longitude+""
                        ,EmUtil.getLastLoc().longitude+"")
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<ZCOrderResult> findOne(Long orderId) {
        return ApiManager.getInstance().createApi(Config.HOST, ZCApiService.class)
                .indexOrders(orderId, EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<EmResult> refuseOrder(Long orderId,String orderType, String remark) {
        return ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .refuseOrder(orderId,orderType , remark)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<ZCOrderResult> toStart(Long orderId, Long version) {
        return ApiManager.getInstance().createApi(Config.HOST, ZCApiService.class)
                .goToBookAddress(orderId, EmUtil.getAppKey(), version)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<ZCOrderResult> arriveStart(Long orderId, Long version) {
        return ApiManager.getInstance().createApi(Config.HOST, ZCApiService.class)
                .arrivalBookAddress(orderId, EmUtil.getAppKey(), version)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<ZCOrderResult> startWait(Long orderId) {
        return ApiManager.getInstance().createApi(Config.HOST, ZCApiService.class)
                .waitOrder(orderId, EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<ZCOrderResult> startDrive(Long orderId, Long version) {
        EmLoc emLoc = EmUtil.getLastLoc();
        return ApiManager.getInstance().createApi(Config.HOST, ZCApiService.class)
                .goToDistination(orderId, EmUtil.getAppKey(), version, emLoc.longitude, emLoc.latitude, emLoc.address)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<ZCOrderResult> arriveDes(ZCOrder zcOrder, DymOrder order, Long version) {
        PushFee pushData = new PushFee();

        //司机的信息
        Employ employ1 = Employ.findByID(XApp.getMyPreferences().getLong(Config.SP_DRIVERID,0));
        PushFeeEmploy pe = null;
        if (employ1 != null && employ1 instanceof Employ) {
            Employ employ = (Employ) employ1;
            pe = new PushFeeEmploy();
            pe.id = employ.id;
            pe.status = employ.status;
            pe.realName = employ.realName;
            pe.companyId = employ.companyId;
            pe.phone = employ.phone;
            pe.business = employ.serviceType;
        }
        pushData.employ = pe;

        EmLoc loc = EmUtil.getLastLoc();

        //位置信息
        pushData.calc = new PushFeeLoc();
        pushData.calc.lat = loc.latitude;
        pushData.calc.lng = loc.longitude;
        pushData.calc.speed = loc.speed;
        pushData.calc.locationType = loc.locationType;
        pushData.calc.appKey = EmUtil.getAppKey();
        pushData.calc.positionTime = System.currentTimeMillis() / 1000;
        pushData.calc.accuracy = (float) loc.accuracy;

        //订单信息
        List<PushFeeOrder> orderList = new ArrayList<>();
        for (DymOrder dymOrder : DymOrder.findAll()) {
            PushFeeOrder dataOrder = new PushFeeOrder();
            if (dymOrder.orderId == order.orderId && dymOrder.orderType.equals(Config.ZHUANCHE)) {
                dataOrder.orderId = dymOrder.orderId;
                dataOrder.orderType = dymOrder.orderType;
                dataOrder.status = 0;
                dataOrder.addedKm = dymOrder.addedKm;
                dataOrder.addedFee = dymOrder.addedFee;
                if (dymOrder.orderStatus < ZCOrderStatus.GOTO_DESTINATION_ORDER) {//出发前
                    dataOrder.status = 1;
                } else if (dymOrder.orderStatus == ZCOrderStatus.GOTO_DESTINATION_ORDER) {//行驶中
                    dataOrder.status = 2;
                } else if (dymOrder.orderStatus == ZCOrderStatus.START_WAIT_ORDER) {//中途等待
                    dataOrder.status = 3;
                }
                dataOrder.peakMile = dymOrder.peakMile;
                dataOrder.nightTime = dymOrder.nightTime;
                dataOrder.nightMile = dymOrder.nightMile;
                dataOrder.nightTimePrice = dymOrder.nightTimePrice;
                if (dataOrder.status != 0) {
                    orderList.add(dataOrder);
                }
                break;
            }
        }
        pushData.calc.orderInfo = orderList;
        String json = GsonUtil.toJson(pushData);
        EmLoc emLoc = EmUtil.getLastLoc();
        return ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .gpsPush(EmUtil.getAppKey(), json)
                .flatMap(new Func1<GetFeeResult, Observable<ZCOrderResult>>() {
                    @Override
                    public Observable<ZCOrderResult> call(GetFeeResult getFeeResult) {

                        return ApiManager.getInstance().createApi(Config.HOST, ZCApiService.class)
                                .arrivalDistination(
                                        zcOrder.orderId,
                                        EmUtil.getAppKey(),
                                        version,
                                        emLoc.longitude,
                                        emLoc.latitude,
                                        emLoc.address
                               )
                                .filter(new HttpResultFunc<>())
                                .map(new Func1<ZCOrderResult, ZCOrderResult>() {
                                    @Override
                                    public ZCOrderResult call(ZCOrderResult zcOrderResult) {
//                                        finalOrder1.updateConfirm();
                                        return zcOrderResult;
                                    }
                                });
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<ZCOrderResult> changeEnd(Long orderId, Double lat, Double lng, String address) {
        return ApiManager.getInstance().createApi(Config.HOST, ZCApiService.class)
                .changeEnd(orderId, lat, lng, address, EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<EmResult> cancelOrder(Long orderId, String remark) {
        return ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .cancelOrder(orderId, remark)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<ConsumerResult> consumerInfo(Long orderId) {
        return ApiManager.getInstance().createApi(Config.HOST, ZCApiService.class)
                .getConsumer(orderId, EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<JsonElement> payOrder(Long orderId, String payType, Long version) {
        return ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .payOrder(orderId, payType)
                .map(new HttpResultFunc2<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


}
