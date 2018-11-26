package com.easymi.common.mvp.order;

import com.easymi.common.entity.MultipleOrder;
import com.easymi.common.result.MultipleOrderResult;
import com.easymi.common.result.QueryOrdersResult;
import com.easymi.component.result.EmResult;
import com.easymi.component.rxmvp.RxManager;

import rx.Observable;
import java.util.List;
/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: MyOrderContract
 * Author: shine
 * Date: 2018/11/23 上午11:35
 * Description:
 * History:
 */
public interface MyOrderContract {


    interface View {
        void showOrders(List<MultipleOrder> MultipleOrders,int total);

        void doSuccesd();

        RxManager getRxManager();
    }

    interface Presenter {
        void indexOrders(int page,int size,String status);

        void refuseOrder(MultipleOrder order);

        void grabOrder(MultipleOrder order);

        void takeOrder(MultipleOrder order);
    }

    interface Model {
        Observable<QueryOrdersResult> indexOrders(int page,int size,String status);

        Observable<MultipleOrderResult> grabZCOrder(Long orderId, Long version);

        Observable<MultipleOrderResult> takeZCOrder(Long orderId,Long version);

        Observable<MultipleOrderResult> takeTaxiOrder(Long orderId,Long version);

        Observable<EmResult> refuseOrder(Long orderId, String serviceType);
    }

}
