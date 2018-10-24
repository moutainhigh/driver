package com.easymi.common.push;

import android.content.Context;

import com.easymi.common.CommApiService;
import com.easymi.common.entity.PullFeeCon;
import com.easymi.common.result.OrderFeeResult;
import com.easymi.component.Config;
import com.easymi.component.DJOrderStatus;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.entity.EmLoc;
import com.easymi.component.loc.TrackHelper;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.network.NoErrSubscriberListener;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.NetUtil;
import com.google.gson.Gson;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Copyright (C) , 2012-2018 , 四川小咖科技有限公司
 *
 * @author Lzh
 * @version 5.0.0.000
 * @date 2018/10/24
 * @since 5.0.0.000
 */
public class OrderPushDisTimer {

    private Timer timer;
    private TimerTask timerTask;

    private RxManager rxManager;

    private Context context;

    private long orderId;
    private String orderType;

    public OrderPushDisTimer(Context context, long orderId, String orderType) {
        this.context = context;
        this.orderId = orderId;
        this.orderType = orderType;
    }

    public void startTimer() {
        cancelTimer();
        rxManager = new RxManager();
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (NetUtil.getNetWorkState(context) != NetUtil.NETWORK_NONE) {
                    DymOrder dymOrder = DymOrder.findByIDType(orderId, orderType);
                    if (null != dymOrder) {
                        TrackHelper.getInstance().queryDis(dymOrder.toEndTrackId, meters -> startPushDis(meters / 1000));
                    }
                }
            }
        };
        timer.schedule(timerTask, 5 * 1000, 30 * 1000);
    }

    public void cancelTimer() {
        if (timer != null) {
            timer.cancel();
        }
        if (timerTask != null) {
            timerTask.cancel();
        }
        if (null != rxManager) {
            rxManager.clear();
            rxManager = null;
        }
    }

    private void startPushDis(double distance) {
        DymOrder dymOrder = DymOrder.findByIDType(orderId, orderType);
        if (dymOrder == null) {
            return;
        }
        int state = 0;
        if (dymOrder.orderStatus == DJOrderStatus.START_WAIT_ORDER) {
            state = 2;
        } else if (dymOrder.orderStatus == DJOrderStatus.GOTO_DESTINATION_ORDER) {
            state = 1;
        }
        if (state == 0) {
            return;
        }
        EmLoc emLoc = EmUtil.getLastLoc();

        Observable<OrderFeeResult> observable = ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .pushDistance(dymOrder.orderId, distance, Config.APP_KEY, state, (double) dymOrder.addedKm, dymOrder.addedFee, emLoc.latitude, emLoc.longitude)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        rxManager.add(observable.subscribe(new MySubscriber<>(context, false, false, new NoErrSubscriberListener<OrderFeeResult>() {
            @Override
            public void onNext(OrderFeeResult result) {
                if (null != result.budgetFee) {
                    if (dymOrder != null) {
                        if (dymOrder.distance <= result.budgetFee.mileges) {
                            dymOrder.startFee = result.budgetFee.start_price;
                            dymOrder.waitTime = result.budgetFee.wait_time / 60;
                            dymOrder.waitTimeFee = result.budgetFee.wait_time_fee;
                            dymOrder.travelTime = result.budgetFee.driver_time / 60;
                            dymOrder.travelFee = result.budgetFee.drive_time_cost;
                            dymOrder.totalFee = result.budgetFee.total_amount;

                            dymOrder.minestMoney = result.budgetFee.min_cost;

                            dymOrder.disFee = result.budgetFee.mileage_cost;
                            dymOrder.distance = result.budgetFee.mileges;

                            DecimalFormat decimalFormat = new DecimalFormat("#0.0");
                            decimalFormat.setRoundingMode(RoundingMode.DOWN);
                            dymOrder.distance = Double.parseDouble(decimalFormat.format(dymOrder.distance));
                            //公里数保留一位小数。。

                            dymOrder.updateFee();

                            PullFeeCon pullFeeCon = new PullFeeCon();
                            pullFeeCon.msg = "pull_fee";
                            pullFeeCon.orderId = dymOrder.orderId;
                            pullFeeCon.orderType = dymOrder.orderType;
                            HandlePush.getInstance().handPush(new Gson().toJson(pullFeeCon));
                        }
                    }
                }
            }
        })));
    }
}
