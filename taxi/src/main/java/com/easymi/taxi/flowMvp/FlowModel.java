package com.easymi.taxi.flowMvp;

import com.easymi.common.CommApiService;
import com.easymi.common.entity.PullFeeResult;
import com.easymi.common.entity.PushBean;
import com.easymi.common.entity.PushData;
import com.easymi.common.entity.PushDataLoc;
import com.easymi.common.entity.PushDataOrder;
import com.easymi.common.push.HandlePush;
import com.easymi.component.Config;
import com.easymi.component.ZCOrderStatus;
import com.easymi.component.app.XApp;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.entity.EmLoc;
import com.easymi.component.entity.Employ;
import com.easymi.component.entity.PushEmploy;
import com.easymi.component.entity.Vehicle;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.GsonUtil;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.result.EmResult;
import com.easymi.component.utils.EmUtil;
import com.easymi.taxi.TaxiApiService;
import com.easymi.taxi.entity.TaxiOrder;
import com.easymi.taxi.result.ConsumerResult;
import com.easymi.taxi.result.TaxiOrderResult;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:FlowModel
 *
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */
public class FlowModel implements FlowContract.Model {

    @Override
    public Observable<TaxiOrderResult> doAccept(Long orderId) {
        return ApiManager.getInstance().createApi(Config.HOST, TaxiApiService.class)
                .takeOrder(EmUtil.getAppKey(), EmUtil.getEmployInfo().companyId, EmUtil.getEmployId(), orderId)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<TaxiOrderResult> findOne(Long orderId) {
        return ApiManager.getInstance().createApi(Config.HOST, TaxiApiService.class)
                .queryTaxiOrder(orderId, EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<EmResult> refuseOrder(Long orderId, String orderType, String remark) {
        return ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .refuseOrder(orderId, orderType, remark)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<TaxiOrderResult> toStart(Long orderId) {
        return ApiManager.getInstance().createApi(Config.HOST, TaxiApiService.class)
                .goToBookAddress(orderId, EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<TaxiOrderResult> arriveStart(Long orderId) {
        EmLoc loc = EmUtil.getLastLoc();
        return ApiManager.getInstance().createApi(Config.HOST, TaxiApiService.class)
                .arrivalBookAddress(orderId, EmUtil.getAppKey(),
                        loc.street + "  " + loc.poiName, loc.latitude, loc.longitude)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<TaxiOrderResult> startWait(Long orderId) {
        return ApiManager.getInstance().createApi(Config.HOST, TaxiApiService.class)
                .waitOrder(orderId, EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<TaxiOrderResult> startDrive(Long orderId) {
        return ApiManager.getInstance().createApi(Config.HOST, TaxiApiService.class)
                .goToDistination(orderId, EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<TaxiOrderResult> arriveDes(TaxiOrder taxiOrder, DymOrder order) {
        PushData pushData = new PushData();

        //司机的信息
        Employ employ = Employ.findByID(XApp.getMyPreferences().getLong(Config.SP_DRIVERID, 0));
        PushEmploy pe = null;
        if (employ != null) {
            pe = new PushEmploy();
            pe.id = employ.id;
            pe.name = employ.realName;
            pe.status = employ.status;
            pe.companyId = employ.companyId;
            pe.phone = employ.phone;
            pe.business = employ.serviceType;
            pe.sex = employ.sex;
            if (Vehicle.exists(employ.id)) {
                Vehicle vehicle = Vehicle.findByEmployId(employ.id);
                pe.vehicleNo = vehicle.vehicleNo;
                pe.modelId = vehicle.vehicleModel;
            } else {
                pe.vehicleNo = "";
            }
            pushData.serviceType = employ.serviceType;
        }
        pushData.driver = pe;

        EmLoc emLoc = EmUtil.getLastLoc();

        pushData.location = new PushDataLoc();
        pushData.location.latitude = emLoc.latitude;
        pushData.location.longitude = emLoc.longitude;
        pushData.location.speed = emLoc.speed;
        pushData.location.locationType = emLoc.locationType;
        pushData.location.appKey = EmUtil.getAppKey();
//        pushData.calc.darkCost = buildPushData.darkCost;
//        pushData.calc.darkMileage = buildPushData.darkMileage;
        pushData.location.positionTime = System.currentTimeMillis() / 1000;
        pushData.location.accuracy = (float) emLoc.accuracy;

        pushData.location.adCode = emLoc.adCode;
        pushData.location.cityCode = emLoc.cityCode;
        pushData.location.bearing = emLoc.bearing;
        pushData.location.provider = emLoc.provider;
        pushData.location.altitude = emLoc.altitude;
        pushData.location.time = System.currentTimeMillis() / 1000;
        pushData.location.isOffline = emLoc.isOffline;


        //订单信息
        List<PushDataOrder> orderList = new ArrayList<>();
        for (DymOrder dymOrder : DymOrder.findAll()) {
            PushDataOrder dataOrder = new PushDataOrder();
            if (dymOrder.orderId == order.orderId && dymOrder.orderType.equals(Config.TAXI)) {
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
        pushData.location.orderInfo = orderList;
        String json = GsonUtil.toJson(new PushBean("gps", pushData));

        return ApiManager.getInstance().createApi(Config.HOST, TaxiApiService.class)
                .pullFee(json, EmUtil.getAppKey())
                .flatMap(new Func1<PullFeeResult, Observable<TaxiOrderResult>>() {
                    @Override
                    public Observable<TaxiOrderResult> call(PullFeeResult pullFeeResult) {
                        //-------------------------------------
                        //order为原始数据,finalOrder为最终数据
                        //--------------------------------------

                        DymOrder finalOrder = null;
                        if (pullFeeResult != null) {
                            try {
                                HandlePush.getInstance().handPush(pullFeeResult.fee);
                                finalOrder = DymOrder.findByIDType(order.orderId, order.orderType);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                        if (finalOrder == null) {
                            finalOrder = order;
                        }


                        //-----------------重新计算费用---------------------

                        DecimalFormat df = new DecimalFormat("#0.0");

                        //拷贝本地数据
                        finalOrder.prepay = order.prepay;
                        finalOrder.extraFee = order.extraFee;
                        finalOrder.remark = order.remark;
                        finalOrder.paymentFee = order.paymentFee;
                        finalOrder.prepay = order.prepay;

                        finalOrder.orderTotalFee = Double.parseDouble(df.format(finalOrder.totalFee + finalOrder.extraFee + finalOrder.paymentFee));

                        double canCouponMoney = finalOrder.totalFee + finalOrder.extraFee;//可以参与优惠券抵扣的钱
                        if (canCouponMoney < finalOrder.minestMoney) {
                            canCouponMoney = finalOrder.minestMoney;
                        }

//                        if (taxiOrder != null && taxiOrder.coupon != null) {
//                            if (taxiOrder.coupon.couponType == 2) {
//                                finalOrder.couponFee = taxiOrder.coupon.deductible;
//                            } else if (taxiOrder.coupon.couponType == 1) {
//                                finalOrder.couponFee = Double.parseDouble(df.format(canCouponMoney * (100 - taxiOrder.coupon.discount) / 100));
//                            }
//                        }
                        double exls = Double.parseDouble(df.format(canCouponMoney - finalOrder.couponFee));//打折抵扣后应付的钱
                        if (exls < 0) {
                            exls = 0;//优惠券不退钱
                        }

                        finalOrder.orderShouldPay = Double.parseDouble(df.format(exls + finalOrder.paymentFee - finalOrder.prepay));

                        //--------------------------------------

                        double couponFee = finalOrder.couponFee;
                        double orderTotalFee = finalOrder.orderTotalFee;
                        double orderShouldPay = finalOrder.orderShouldPay;

                        DymOrder finalOrder1 = finalOrder;

                        return ApiManager.getInstance().createApi(Config.HOST, TaxiApiService.class)
                                .arrivalDistination(finalOrder.orderId, EmUtil.getAppKey(), finalOrder.paymentFee, finalOrder.extraFee,
                                        finalOrder.remark, finalOrder.distance, finalOrder.disFee, finalOrder.travelTime,
                                        finalOrder.travelFee, finalOrder.waitTime,
                                        finalOrder.waitTimeFee, 0.0, 0.0, couponFee,
                                        orderTotalFee, orderShouldPay, finalOrder.startFee,
                                        emLoc.street + "  " + emLoc.poiName, emLoc.latitude, emLoc.longitude,
                                        finalOrder.minestMoney, finalOrder.peakCost, finalOrder.nightPrice, finalOrder.lowSpeedCost, finalOrder.lowSpeedTime,
                                        finalOrder.peakMile, finalOrder.nightTime, finalOrder.nightMile, finalOrder.nightTimePrice)
                                .filter(new HttpResultFunc<>())
                                .map(new Func1<TaxiOrderResult, TaxiOrderResult>() {
                                    @Override
                                    public TaxiOrderResult call(TaxiOrderResult taxiOrderResult) {
                                        finalOrder1.updateConfirm();
                                        return taxiOrderResult;
                                    }
                                });
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());


//        return ApiManager.getInstance().createApi(Config.HOST, TaxiApiService.class)
//                .arrivalDistination(order.orderId, EmUtil.getAppKey(), order.paymentFee, order.extraFee,
//                        order.remark, order.distance, order.disFee, order.travelTime,
//                        order.travelFee, order.waitTime,
//                        order.waitTimeFee, 0.0, 0.0, order.couponFee,
//                        order.orderTotalFee, order.orderShouldPay, order.startFee,
//                        loc.street + "  " + loc.poiName, loc.latitude, loc.longitude,
//                        order.minestMoney, order.peakCost, order.nightPrice, order.lowSpeedCost, order.lowSpeedTime,
//                        order.peakMile,order.nightTime,order.nightMile,order.nightTimePrice)
//                .filter(new HttpResultFunc<>())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<TaxiOrderResult> changeEnd(Long orderId, Double lat, Double lng, String address) {
        return ApiManager.getInstance().createApi(Config.HOST, TaxiApiService.class)
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
        return ApiManager.getInstance().createApi(Config.HOST, TaxiApiService.class)
                .getConsumer(orderId, EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<EmResult> payOrder(Long orderId, String payType) {
        return ApiManager.getInstance().createApi(Config.HOST, TaxiApiService.class)
                .payOrder(orderId, EmUtil.getAppKey(), payType)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<EmResult> changeOrderStatus(Long companyId, String detailAddress, Long driverId, Double latitude, Double longitude, Long orderId, int status) {
        return ApiManager.getInstance().createApi(Config.HOST, TaxiApiService.class)
                .changeOrderStatus(companyId, detailAddress, driverId, latitude, longitude, orderId, status)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<EmResult> taxiSettlement(Long orderId, String orderNo, double fee) {
        EmLoc emLoc = EmUtil.getLastLoc();
        return ApiManager.getInstance().createApi(Config.HOST, TaxiApiService.class)
                .taxiSettlement(orderId, orderNo,
//                        EmUtil.getEmployId(),EmUtil.getEmployInfo().companyId,
                        emLoc.longitude, emLoc.latitude, emLoc.address, fee)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


}
