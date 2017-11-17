package com.easymi.common.mvp.work;

import android.content.Context;

import com.easymi.common.result.QueryOrdersResult;
import com.easymi.common.entity.BaseOrder;
import com.easymi.component.Config;
import com.easymi.component.app.XApp;
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.MySubscriber;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * Created by developerLzh on 2017/11/17 0017.
 */

public class WorkPresenter implements WorkContract.Presenter {

    private Context context;

    private WorkContract.View view;
    private WorkContract.Model model;

    public WorkPresenter(Context context, WorkContract.View view) {
        this.context = context;
        this.view = view;
        model = new WorkModel();
    }

    @Override
    public void queryStats() {

    }

    @Override
    public void indexOrders() {
        view.showOrders(null);

        long driverId = XApp.getMyPreferences().getLong(Config.SP_DRIVERID, -1L);

        Observable<QueryOrdersResult> observable = model.indexOrders(driverId, Config.APP_KEY);
        view.getRxManager().add(observable.subscribe(new MySubscriber<>(context, false, false, new HaveErrSubscriberListener<QueryOrdersResult>() {
            @Override
            public void onNext(QueryOrdersResult emResult) {
                List<BaseOrder> orders = emResult.orders;
                List<BaseOrder> nowOrders = new ArrayList<>();
                List<BaseOrder> yuyueOrders = new ArrayList<>();
                for (BaseOrder order : orders) {
                    order.viewType = BaseOrder.ITEM_POSTER;
                    if (order.isBookOrder == 2) {
                        nowOrders.add(order);
                    } else {
                        yuyueOrders.add(order);
                    }
                }
                orders.clear();
                //预约header
                BaseOrder header1 = new BaseOrder(BaseOrder.ITEM_HEADER);
                header1.isBookOrder = 1;
                orders.add(header1);
                //预约单
                orders.addAll(yuyueOrders);

                //即时header
                BaseOrder header2 = new BaseOrder(BaseOrder.ITEM_HEADER);
                header1.isBookOrder = 2;
                orders.add(header2);
                //即时单
                orders.addAll(nowOrders);

                view.showOrders(orders);
            }

            @Override
            public void onError(int code) {
                view.showOrders(null);
            }
        })));
//        view.showOrders(initRecyclerData());
    }

//    private List<BaseOrder> initRecyclerData() {
//
//        List<BaseOrder> baseOrders = new ArrayList<>();
//        BaseOrder header1 = new BaseOrder(BaseOrder.ITEM_HEADER);
//        header1.ifNow = false;
//        baseOrders.add(header1);
//        for (int i = 0; i < 10; i++) {
//            BaseOrder item = new BaseOrder(BaseOrder.ITEM_POSTER);
//            item.orderId = 1L;
//            item.orderEndPlace = "锦绣大道南段99号";
//            item.orderStartPlace = "花样年花样城5期";
//            item.orderStatus = 1;
//            item.orderTime = System.currentTimeMillis();
//            item.orderType = "日常代驾";
//            baseOrders.add(item);
//        }
//
//        BaseOrder header2 = new BaseOrder(BaseOrder.ITEM_HEADER);
//        header1.ifNow = true;
//        baseOrders.add(header2);
//        for (int i = 0; i < 10; i++) {
//            BaseOrder item = new BaseOrder(BaseOrder.ITEM_POSTER);
//            item.orderId = 1L;
//            item.orderEndPlace = "锦绣大道南段99号";
//            item.orderStartPlace = "花样年花样城5期";
//            item.orderStatus = 1;
//            item.orderTime = System.currentTimeMillis();
//            item.orderType = "日常代驾";
//            baseOrders.add(item);
//        }
//
//        return baseOrders;
//    }
}
