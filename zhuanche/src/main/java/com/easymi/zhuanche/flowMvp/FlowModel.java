package com.easymi.zhuanche.flowMvp;

import com.easymi.common.entity.PullFeeResult;
import com.easymi.common.entity.PushData;
import com.easymi.common.entity.PushDataLoc;
import com.easymi.common.entity.PushDataOrder;
import com.easymi.common.entity.PushFee;
import com.easymi.common.entity.PushFeeEmploy;
import com.easymi.common.entity.PushFeeLoc;
import com.easymi.common.entity.PushFeeOrder;
import com.easymi.common.push.HandlePush;
import com.easymi.common.result.GetFeeResult;
import com.easymi.component.Config;
import com.easymi.component.DJOrderStatus;
import com.easymi.component.ZCOrderStatus;
import com.easymi.component.entity.BaseEmploy;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.entity.EmLoc;
import com.easymi.component.entity.Employ;
import com.easymi.component.entity.PushEmploy;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.GsonUtil;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.result.EmResult;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.Log;
import com.easymi.zhuanche.ZCApiService;
import com.easymi.zhuanche.entity.ZCOrder;
import com.easymi.zhuanche.result.ConsumerResult;
import com.easymi.zhuanche.result.ZCOrderResult;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by liuzihao on 2017/11/15.
 */

public class FlowModel implements FlowContract.Model {

    @Override
    public Observable<ZCOrderResult> doAccept(Long orderId,Long version) {
        return ApiManager.getInstance().createApi(Config.HOST, ZCApiService.class)
//                .takeOrder(orderId, EmUtil.getEmployId(), EmUtil.getAppKey())
                .takeOrder(EmUtil.getEmployId(),EmUtil.getEmployInfo().realName,EmUtil.getEmployInfo().phone,orderId,version)
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
    public Observable<EmResult> refuseOrder(Long orderId, String remark) {
        return ApiManager.getInstance().createApi(Config.HOST, ZCApiService.class)
                .refuseOrder(orderId, EmUtil.getEmployId(), EmUtil.getAppKey(), remark)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<ZCOrderResult> toStart(Long orderId) {
        return ApiManager.getInstance().createApi(Config.HOST, ZCApiService.class)
                .goToBookAddress(orderId, EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<ZCOrderResult> arriveStart(Long orderId) {
        return ApiManager.getInstance().createApi(Config.HOST, ZCApiService.class)
                .arrivalBookAddress(orderId, EmUtil.getAppKey())
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
    public Observable<ZCOrderResult> startDrive(Long orderId) {
        return ApiManager.getInstance().createApi(Config.HOST, ZCApiService.class)
                .goToDistination(orderId, EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<ZCOrderResult> arriveDes(ZCOrder zcOrder, DymOrder order) {
        PushFee pushData = new PushFee();

        //司机的信息
        BaseEmploy employ1 = new BaseEmploy().employ2This();
        PushFeeEmploy pe = null;
        if (employ1 != null && employ1 instanceof Employ) {
            Employ employ = (Employ) employ1;
            pe = new PushFeeEmploy();
            pe.childType = employ.child_type;
            pe.id = employ.id;
            pe.status = employ.status;
            pe.realName = employ.real_name;
            pe.companyId = employ.company_id;
            pe.phone = employ.phone;
            pe.childType = employ.child_type;
            pe.business = employ.serviceType;
//            if (employ.vehicle != null) {
//                pe.modelId = employ.vehicle.serviceType;
//            }
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

        return ApiManager.getInstance().createApi(Config.HOST, ZCApiService.class)
//                .pullFee(json, EmUtil.getAppKey())
                .gpsPush(EmUtil.getAppKey(),json)
//                .flatMap(new Func1<PullFeeResult, Observable<ZCOrderResult>>() {
//                    @Override
//                    public Observable<ZCOrderResult> call(PullFeeResult pullFeeResult) {
//                        //-------------------------------------
//                        //order为原始数据,finalOrder为最终数据
//                        //--------------------------------------
//
//                        DymOrder finalOrder = null;
//                        if (pullFeeResult != null) {
//                            try {
//                                HandlePush.getInstance().handPush(pullFeeResult.fee);
//                                finalOrder = DymOrder.findByIDType(order.orderId, order.orderType);
//                            } catch (Exception ex) {
//                                ex.printStackTrace();
//                            }
//                        }
//                        if (finalOrder == null) {
//                            finalOrder = order;
//                        }
//
//
//                        //-----------------重新计算费用---------------------
//
//                        DecimalFormat df = new DecimalFormat("#0.0");
//
//                        //拷贝本地数据
//                        finalOrder.prepay = order.prepay;
//                        finalOrder.extraFee = order.extraFee;
//                        finalOrder.remark = order.remark;
//                        finalOrder.paymentFee = order.paymentFee;
//                        finalOrder.prepay = order.prepay;
//
//                        finalOrder.orderTotalFee = Double.parseDouble(df.format(finalOrder.totalFee + finalOrder.extraFee + finalOrder.paymentFee));
//
//                        double canCouponMoney = finalOrder.totalFee + finalOrder.extraFee;//可以参与优惠券抵扣的钱
//                        if (canCouponMoney < finalOrder.minestMoney) {
//                            canCouponMoney = finalOrder.minestMoney;
//                        }
//
//                        if (zcOrder != null && zcOrder.coupon != null) {
//                            if (zcOrder.coupon.couponType == 2) {
//                                finalOrder.couponFee = zcOrder.coupon.deductible;
//                            } else if (zcOrder.coupon.couponType == 1) {
//                                finalOrder.couponFee = Double.parseDouble(df.format(canCouponMoney * (100 - zcOrder.coupon.discount) / 100));
//                            }
//                        }
//                        double exls = Double.parseDouble(df.format(canCouponMoney - finalOrder.couponFee));//打折抵扣后应付的钱
//                        if (exls < 0) {
//                            exls = 0;//优惠券不退钱
//                        }
//
//                        finalOrder.orderShouldPay = Double.parseDouble(df.format(exls + finalOrder.paymentFee - finalOrder.prepay));
//
//                        //--------------------------------------
//
//                        double couponFee = finalOrder.couponFee;
//                        double orderTotalFee = finalOrder.orderTotalFee;
//                        double orderShouldPay = finalOrder.orderShouldPay;
//
//                        DymOrder finalOrder1 = finalOrder;
                .flatMap(new Func1<GetFeeResult, Observable<ZCOrderResult>>() {
                    @Override
                    public Observable<ZCOrderResult> call(GetFeeResult getFeeResult) {

                        return ApiManager.getInstance().createApi(Config.HOST, ZCApiService.class)
                                .arrivalDistination(
//                                        finalOrder.orderId,
                                        zcOrder.orderId,
                                        EmUtil.getAppKey()
//                                        , finalOrder.paymentFee, finalOrder.extraFee,
//                                        finalOrder.remark, finalOrder.distance, finalOrder.disFee, finalOrder.travelTime,
//                                        finalOrder.travelFee, finalOrder.waitTime,
//                                        finalOrder.waitTimeFee, 0.0, 0.0, couponFee,
//                                        orderTotalFee, orderShouldPay, finalOrder.startFee,
//                                        loc.street + "  " + loc.poiName, loc.latitude, loc.longitude,
//                                        finalOrder.minestMoney, finalOrder.peakCost, finalOrder.nightPrice, finalOrder.lowSpeedCost, finalOrder.lowSpeedTime,
//                                        finalOrder.peakMile, finalOrder.nightTime, finalOrder.nightMile, finalOrder.nightTimePrice
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


//        return ApiManager.getInstance().createApi(Config.HOST, ZCApiService.class)
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
    public Observable<ZCOrderResult> changeEnd(Long orderId, Double lat, Double lng, String address) {
        return ApiManager.getInstance().createApi(Config.HOST, ZCApiService.class)
                .changeEnd(orderId, lat, lng, address, EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<EmResult> cancelOrder(Long orderId, String remark) {
        return ApiManager.getInstance().createApi(Config.HOST, ZCApiService.class)
                .cancelOrder(orderId, EmUtil.getEmployId(), EmUtil.getAppKey(), remark)
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
    public Observable<EmResult> payOrder(Long orderId, String payType) {
        return ApiManager.getInstance().createApi(Config.HOST, ZCApiService.class)
                .payOrder(orderId, EmUtil.getAppKey(), payType)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


}
