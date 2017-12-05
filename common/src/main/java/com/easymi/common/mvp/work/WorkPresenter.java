package com.easymi.common.mvp.work;

import android.content.Context;
import android.content.Intent;

import com.easymi.common.R;
import com.easymi.common.result.AnnouncementResult;
import com.easymi.common.result.NearDriverResult;
import com.easymi.common.result.NotitfyResult;
import com.easymi.common.result.QueryOrdersResult;
import com.easymi.common.entity.BaseOrder;
import com.easymi.component.Config;
import com.easymi.component.loc.LocService;
import com.easymi.component.app.XApp;
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.network.NoErrSubscriberListener;
import com.easymi.component.result.EmResult;
import com.easymi.component.utils.EmUtil;

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

        long driverId = EmUtil.getEmployId();

        Observable<QueryOrdersResult> observable = model.indexOrders(driverId, Config.APP_KEY);
        view.getRxManager().add(observable.subscribe(new MySubscriber<>(context, false, false, new HaveErrSubscriberListener<QueryOrdersResult>() {
            @Override
            public void onNext(QueryOrdersResult emResult) {
                List<BaseOrder> orders = emResult.orders;
                List<BaseOrder> nowOrders = new ArrayList<>();
                List<BaseOrder> yuyueOrders = new ArrayList<>();
                XApp.getPreferencesEditor().putBoolean(Config.SP_NEED_TRACE, false).apply();
                for (BaseOrder order : orders) {
                    order.viewType = BaseOrder.ITEM_POSTER;
                    if (order.isBookOrder == 2) {
                        nowOrders.add(order);
                    } else {
                        yuyueOrders.add(order);
                    }
                    //有任何一个订单的状态是前往目的地就置成需要纠偏
                    if (order.orderType.equals("daijia") && order.orderStatus == 25) {
                        XApp.getPreferencesEditor().putBoolean(Config.SP_NEED_TRACE, true).apply();
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
                XApp.getPreferencesEditor().putBoolean(Config.SP_NEED_TRACE, false).apply();
                view.showOrders(null);
            }
        })));
//        view.showOrders(initRecyclerData());
    }

    @Override
    public void startLocService(Context context) {
        Intent intent = new Intent();
        intent.setAction(LocService.START_LOC);
        intent.setPackage(context.getPackageName());
        context.startService(intent);
    }

    @Override
    public void online() {
        XApp.getInstance().syntheticVoice(context.getString(R.string.start_lis_order),true);

        long driverId = EmUtil.getEmployId();

        Observable<EmResult> observable = model.online(driverId, Config.APP_KEY);
        view.getRxManager().add(observable.subscribe(new MySubscriber<>(context, true,
                true, emResult -> view.onlineSuc())));
    }

    @Override
    public void offline() {
        XApp.getInstance().syntheticVoice(context.getString(R.string.stop_lis_order),true);

        long driverId = EmUtil.getEmployId();

        Observable<EmResult> observable = model.offline(driverId, Config.APP_KEY);
        view.getRxManager().add(observable.subscribe(new MySubscriber<>(context, true,
                true, emResult -> view.offlineSuc())));
    }

    @Override
    public void loadNotice(long id) {
        Observable<NotitfyResult> observable = model.loadNotice(id);
        view.getRxManager().add(observable.subscribe(new MySubscriber<>(context, false,
                false, notitfyResult -> view.showNotify(notitfyResult.employNoticeRecord))));
    }

    @Override
    public void loadAnn(long id) {
        Observable<AnnouncementResult> observable = model.loadAnn(id);
        view.getRxManager().add(observable.subscribe(new MySubscriber<>(context, false,
                false, notitfyResult -> view.showAnn(notitfyResult.EmployAfficheRequest))));
    }

    @Override
    public void queryNearDriver(Double lat, Double lng) {
        long driverId = EmUtil.getEmployId();
        double dis = 20;

        Observable<NearDriverResult> observable = model.queryNearDriver(driverId, lat, lng, dis);
        view.getRxManager().add(observable.subscribe(new MySubscriber<>(context, false,
                true, nearDriverResult -> view.showDrivers(nearDriverResult.emploies))));
    }
}
