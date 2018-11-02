package com.easymi.daijia.flowMvp;

import com.easymi.common.entity.PushFee;
import com.easymi.common.entity.PushFeeEmploy;
import com.easymi.common.entity.PushFeeLoc;
import com.easymi.common.entity.PushFeeOrder;
import com.easymi.common.push.HandlePush;
import com.easymi.component.Config;
import com.easymi.component.DJOrderStatus;
import com.easymi.component.entity.BaseEmploy;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.entity.EmLoc;
import com.easymi.component.entity.Employ;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.GsonUtil;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.result.EmResult;
import com.easymi.component.utils.EmUtil;
import com.easymi.daijia.DJApiService;
import com.easymi.common.entity.PullFeeResult;
import com.easymi.daijia.entity.DJOrder;
import com.easymi.daijia.result.ConsumerResult;
import com.easymi.daijia.result.DJOrderResult;
import com.easymi.daijia.result.OrderFeeResult;

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
    public Observable<DJOrderResult> doAccept(Long orderId) {
        return ApiManager.getInstance().createApi(Config.HOST, DJApiService.class)
                .takeOrder(orderId, EmUtil.getEmployId(), EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<DJOrderResult> findOne(Long orderId) {
        return ApiManager.getInstance().createApi(Config.HOST, DJApiService.class)
                .indexOrders(orderId, EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<EmResult> refuseOrder(Long orderId, String remark) {
        return ApiManager.getInstance().createApi(Config.HOST, DJApiService.class)
                .refuseOrder(orderId, EmUtil.getEmployId(), EmUtil.getAppKey(), remark)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<DJOrderResult> toStart(Long orderId) {
        return ApiManager.getInstance().createApi(Config.HOST, DJApiService.class)
                .goToBookAddress(orderId, EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<DJOrderResult> arriveStart(Long orderId) {
        EmLoc loc = EmUtil.getLastLoc();
        return ApiManager.getInstance().createApi(Config.HOST, DJApiService.class)
                .arrivalBookAddress(orderId, EmUtil.getAppKey(),
                        loc.street + "  " + loc.poiName, loc.latitude, loc.longitude)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<DJOrderResult> startWait(Long orderId) {
        return ApiManager.getInstance().createApi(Config.HOST, DJApiService.class)
                .waitOrder(orderId, EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<DJOrderResult> startDrive(Long orderId) {
        return ApiManager.getInstance().createApi(Config.HOST, DJApiService.class)
                .goToDistination(orderId, EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<DJOrderResult> arriveDes(DJOrder djOrder,DymOrder order) {

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
            pe.business = employ.service_type;
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
            if (dymOrder.orderId == order.orderId && dymOrder.orderType.equals("daijia")) {
                dataOrder.orderId = dymOrder.orderId;
                dataOrder.orderType = dymOrder.orderType;
                dataOrder.status = 0;
                dataOrder.addedKm = dymOrder.addedKm;
                dataOrder.addedFee = dymOrder.addedFee;
                if (dymOrder.orderStatus < DJOrderStatus.GOTO_DESTINATION_ORDER) {//出发前
                    dataOrder.status = 1;
                } else if (dymOrder.orderStatus == DJOrderStatus.GOTO_DESTINATION_ORDER) {//行驶中
                    dataOrder.status = 2;
                } else if (dymOrder.orderStatus == DJOrderStatus.START_WAIT_ORDER) {//中途等待
                    dataOrder.status = 3;
                }
                if (dataOrder.status != 0) {
                    orderList.add(dataOrder);
                }
                break;
            }
        }
        pushData.calc.orderInfo = orderList;
        String json = GsonUtil.toJson(pushData);





        return ApiManager.getInstance().createApi(Config.HOST, DJApiService.class)
                .pullFee(json,EmUtil.getAppKey())
                .flatMap(pullFeeResult -> {
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
//                    if (canCouponMoney < finalOrder.minestMoney) {
//                        canCouponMoney = finalOrder.minestMoney;
//                    }
                    if (djOrder != null && djOrder.coupon != null) {
                        if (djOrder.coupon.couponType == 2) {
                            finalOrder.couponFee = djOrder.coupon.deductible;
                        } else if (djOrder.coupon.couponType == 1) {
                            finalOrder.couponFee = Double.parseDouble(df.format(canCouponMoney * (100 - djOrder.coupon.discount) / 100));
                        }
                    }
                    double exls = Double.parseDouble(df.format(canCouponMoney - finalOrder.couponFee));//打折抵扣后应付的钱
                    if (exls < 0) {
                        exls = 0;//优惠券不退钱
                    }
                    if (exls < finalOrder.minestMoney) {
                        exls = finalOrder.minestMoney;
                    }
                    finalOrder.orderShouldPay = Double.parseDouble(df.format(exls + finalOrder.paymentFee - finalOrder.prepay));

                    //----------------------------------------

                    DymOrder finalOrder1 = finalOrder;
                    return ApiManager.getInstance().createApi(Config.HOST, DJApiService.class)
                            .arrivalDistination(finalOrder.orderId, EmUtil.getAppKey(), finalOrder.paymentFee, finalOrder.extraFee,
                                    finalOrder.remark, finalOrder.distance, finalOrder.disFee, finalOrder.travelTime,
                                    finalOrder.travelFee, finalOrder.waitTime,
                                    finalOrder.waitTimeFee, 0.0, 0.0, finalOrder.couponFee,
                                    finalOrder.orderTotalFee, finalOrder.orderShouldPay, finalOrder.startFee,
                                    loc.street + "  " + loc.poiName, loc.latitude, loc.longitude, finalOrder.minestMoney)
                            .filter(new HttpResultFunc<>())
                            .map(new Func1<DJOrderResult, DJOrderResult>() {
                                @Override
                                public DJOrderResult call(DJOrderResult djOrderResult) {
                                    finalOrder1.updateConfirm();
                                    return djOrderResult;
                                }
                            });
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

//        return ApiManager.getInstance().createApi(Config.HOST, DJApiService.class)
//                .arrivalDistination(order.orderId, EmUtil.getAppKey(), order.paymentFee, order.extraFee,
//                        order.remark, order.distance, order.disFee, order.travelTime,
//                        order.travelFee, order.waitTime,
//                        order.waitTimeFee, 0.0, 0.0, order.couponFee,
//                        order.orderTotalFee, order.orderShouldPay, order.startFee,
//                        loc.street + "  " + loc.poiName, loc.latitude, loc.longitude, order.minestMoney)
//                .filter(new HttpResultFunc<>())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<DJOrderResult> changeEnd(Long orderId, Double lat, Double lng, String address) {
        return ApiManager.getInstance().createApi(Config.HOST, DJApiService.class)
                .changeEnd(orderId, lat, lng, address, EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<EmResult> cancelOrder(Long orderId, String remark) {
        return ApiManager.getInstance().createApi(Config.HOST, DJApiService.class)
                .cancelOrder(orderId, EmUtil.getEmployId(), EmUtil.getAppKey(), remark)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<ConsumerResult> consumerInfo(Long orderId) {
        return ApiManager.getInstance().createApi(Config.HOST, DJApiService.class)
                .getConsumer(orderId, EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<EmResult> payOrder(Long orderId, String payType) {
        return ApiManager.getInstance().createApi(Config.HOST, DJApiService.class)
                .payOrder(orderId, EmUtil.getAppKey(), payType)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<OrderFeeResult> getOrderFee(Long orderId, Long driverId, String orderType, Integer isArrive) {

        EmLoc emLoc = EmUtil.getLastLoc();

        return ApiManager.getInstance().createApi(Config.HOST, DJApiService.class)
//                .getOrderFee(orderId, driverId, orderType, EmUtil.getAppKey(), 30.7736989546,104.1627502441, isArrive)
                .getOrderFee(orderId, driverId, orderType, EmUtil.getAppKey(), emLoc.latitude, emLoc.longitude, isArrive)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


}
