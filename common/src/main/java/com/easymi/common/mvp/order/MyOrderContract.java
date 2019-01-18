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
 *@Author: shine
 * Date: 2018/11/23 上午11:35
 * Description:
 * History:
 * @author hufeng
 */
public interface MyOrderContract {


    interface View {
        /**
         * 订单数据集合
         * @param MultipleOrders
         * @param total
         */
        void showOrders(List<MultipleOrder> MultipleOrders,int total);

        /**
         * 拒单等操作成功
         */
        void doSuccesd();

        /**
         * 获取RxManager
         * @return
         */
        RxManager getRxManager();
    }

    interface Presenter {
        /**
         * 查询订单列表
         * @param page
         * @param size
         * @param status
         */
        void indexOrders(int page,int size,String status);

        /**
         * 拒绝的订单
         * @param order
         */
        void refuseOrder(MultipleOrder order);

        /**
         * 抢单
         * @param order
         */
        void grabOrder(MultipleOrder order);

        /**
         * 接单
         * @param order
         */
        void takeOrder(MultipleOrder order);
    }

    interface Model {
        /**
         * 查询订单列表
         * @param page
         * @param size
         * @param status
         * @return
         */
        Observable<QueryOrdersResult> indexOrders(int page,int size,String status);

        /**
         * 抢专车订单
         * @param orderId
         * @param version
         * @return
         */
        Observable<MultipleOrderResult> grabZCOrder(Long orderId, Long version);

        /**
         * 接专车订单
         * @param orderId
         * @param version
         * @return
         */
        Observable<MultipleOrderResult> takeZCOrder(Long orderId,Long version);

        /**
         * 接出租车订单
         * @param orderId
         * @param version
         * @return
         */
        Observable<MultipleOrderResult> takeTaxiOrder(Long orderId,Long version);

        /**
         * 拒绝订单
         * @param orderId
         * @param serviceType
         * @return
         */
        Observable<EmResult> refuseOrder(Long orderId, String serviceType);
    }

}
