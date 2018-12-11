package com.easymi.common.mvp.order;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.easymi.common.entity.MultipleOrder;
import com.easymi.common.result.MultipleOrderResult;
import com.easymi.common.result.QueryOrdersResult;
import com.easymi.component.Config;
import com.easymi.component.network.ErrCode;
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.result.EmResult;

import rx.Observable;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: MyOrderPresenter
 * Author: shine
 * Date: 2018/11/23 上午11:34
 * Description:
 * History:
 */
public class MyOrderPresenter implements MyOrderContract.Presenter {
    private Context context;

    private MyOrderContract.View view;
    private MyOrderContract.Model model;


    public MyOrderPresenter(Context context, MyOrderContract.View view) {
        this.context = context;
        this.view = view;
        model = new MyOrderModel();
    }

    @Override
    public void indexOrders(int page,int size,String status) {
        Observable<QueryOrdersResult> observable = model.indexOrders(page,size,status);
        view.getRxManager().add(observable.subscribe(new MySubscriber<>(context, false, false, new HaveErrSubscriberListener<QueryOrdersResult>() {
            @Override
            public void onNext(QueryOrdersResult emResult) {
                if (emResult.getCode() == 1){
                    if (emResult.data != null) {
                        view.showOrders(emResult.data,emResult.total);
                    } else {
                        view.showOrders(null,0);
                    }
                }
            }

            @Override
            public void onError(int code) {
                view.showOrders(null,0);
            }
        })));
    }

    @Override
    public void refuseOrder(MultipleOrder order) {
        Observable<EmResult> observable = model.refuseOrder(order.orderId, order.serviceType);

        view.getRxManager().add(observable.subscribe(new MySubscriber<>(context, true, false, new HaveErrSubscriberListener<EmResult>() {
            @Override
            public void onNext(EmResult result) {
                if (result.getCode() == 0){
                    view.doSuccesd();
                }
            }

            @Override
            public void onError(int code) {
                if (code == ErrCode.NOT_MATCH.getCode()
                        || code == ErrCode.GRAB_ORDER_ERROR.getCode()
                        || code == ErrCode.DRIVER_GOTO_PRE_ORDER_CODE.getCode()) {
                    view.doSuccesd();
                }
            }
        })));
    }

    @Override
    public void grabOrder(MultipleOrder order) {
        Observable<MultipleOrderResult> observable = null;
        if (order.serviceType.equals(Config.ZHUANCHE)) {
            observable = model.grabZCOrder(order.orderId, order.version);
        } else if (order.serviceType.equals(Config.TAXI)) {
            observable = model.takeTaxiOrder(order.orderId, order.version);
        }

        if (observable == null) {
            return;
        }

        view.getRxManager().add(observable.subscribe(new MySubscriber<>(context, true, false, new HaveErrSubscriberListener<MultipleOrderResult>() {
            @Override
            public void onNext(MultipleOrderResult multipleOrderResult) {
                view.doSuccesd();
            }

            @Override
            public void onError(int code) {
                if (code == ErrCode.NOT_MATCH.getCode()
                        || code == ErrCode.GRAB_ORDER_ERROR.getCode()
                        || code == ErrCode.DRIVER_GOTO_PRE_ORDER_CODE.getCode()) {
                        view.doSuccesd();
                }
            }
        })));
    }

    @Override
    public void takeOrder(MultipleOrder order) {
        Observable<MultipleOrderResult> observable = null;
        if (order.serviceType.equals(Config.ZHUANCHE)) {
            observable = model.takeZCOrder(order.orderId, order.version);
        } else if (order.serviceType.equals(Config.TAXI)) {
            observable = model.takeTaxiOrder(order.orderId, order.version);
        }
        if (observable == null) {
            return;
        }
        view.getRxManager().add(observable.subscribe(new MySubscriber<>(context, true, false, new HaveErrSubscriberListener<MultipleOrderResult>() {
            @Override
            public void onNext(MultipleOrderResult multipleOrderResult) {
                view.doSuccesd();
            }

            @Override
            public void onError(int code) {
                if (code == ErrCode.NOT_MATCH.getCode()
                        || code == ErrCode.GRAB_ORDER_ERROR.getCode()
                        || code == ErrCode.DRIVER_GOTO_PRE_ORDER_CODE.getCode()) {
                    view.doSuccesd();
                }
            }
        })));
    }


}
