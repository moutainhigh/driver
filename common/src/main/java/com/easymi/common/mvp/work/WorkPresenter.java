package com.easymi.common.mvp.work;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.easymi.common.daemon.DaemonService;
import com.easymi.common.daemon.JobKeepLiveService;
import com.easymi.common.entity.BaseOrder;
import com.easymi.common.result.AnnouncementResult;
import com.easymi.common.result.NearDriverResult;
import com.easymi.common.result.NotitfyResult;
import com.easymi.common.result.QueryOrdersResult;
import com.easymi.common.result.WorkStatisticsResult;
import com.easymi.component.Config;
import com.easymi.component.app.XApp;
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.result.EmResult;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.TimeUtil;
import com.easymi.component.widget.LoadingButton;

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

        initDaemon();
        startLocService();
    }

    /**
     * 开启保活服务
     */
    @Override
    public void initDaemon() {
        if (Build.VERSION.SDK_INT > 21) {//21版本以上使用JobScheduler
            try {
                ComponentName mServiceComponent = new ComponentName(context, JobKeepLiveService.class);
                JobInfo.Builder builder = new JobInfo.Builder(0x139888, mServiceComponent);
                builder.setPersisted(true);//持续的
                builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);//任何网络情况下
                builder.setRequiresDeviceIdle(false);//是否需要设备闲置
                builder.setRequiresCharging(false);//是否需要充电状态下

                JobScheduler tm = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
                if (tm != null) {
                    tm.schedule(builder.build());
                }
            } catch (Exception e) {
                Log.e("DriverApp", "初始化失败JobScheduler失败");
            }
        } else {//21版本以下使用native保活
            //开起保活service
            Intent daemonIntent = new Intent(context, DaemonService.class);
            daemonIntent.setPackage(context.getPackageName());
            context.startService(daemonIntent);
        }

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
                XApp.getPreferencesEditor().putBoolean(Config.SP_ORDER_RUNNING, false).apply();
                for (BaseOrder order : orders) {
                    order.viewType = BaseOrder.ITEM_POSTER;
                    if (order.isBookOrder == 2) {
                        nowOrders.add(order);
                    } else {
                        yuyueOrders.add(order);
                    }
                    //有任何一个订单的状态是前往目的地就置成需要纠偏
                    if (order.orderType.equals("daijia") &&
                            (order.orderStatus == 5
                                    || order.orderStatus == 10
                                    || order.orderStatus == 15
                                    || order.orderStatus == 20
                                    || order.orderStatus == 25
                                    || order.orderStatus == 28)) {
                        XApp.getPreferencesEditor().putBoolean(Config.SP_ORDER_RUNNING, true).apply();
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
                XApp.getPreferencesEditor().putBoolean(Config.SP_ORDER_RUNNING, false).apply();
                view.showOrders(null);
            }
        })));
//        view.showOrders(initRecyclerData());
    }

    @Override
    public void startLocService() {
        XApp.getInstance().startLocService();
    }

    @Override
    public void online(LoadingButton btn) {
        long driverId = EmUtil.getEmployId();

        Observable<EmResult> observable = model.online(driverId, Config.APP_KEY);
        view.getRxManager().add(observable.subscribe(new MySubscriber<>(context, btn, emResult -> view.onlineSuc())));
    }

    @Override
    public void offline() {
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

    @Override
    public void queryStatis() {
        long driverId = EmUtil.getEmployId();
        String nowDate = TimeUtil.getTime("yyyy-MM-dd", System.currentTimeMillis());
        Observable<WorkStatisticsResult> observable = model.getDriverStatistics(driverId, nowDate);
        view.getRxManager().add(observable.subscribe(new MySubscriber<>(context, false,
                true, result -> view.showStatis(result.workStatistics))));
    }

    @Override
    public void loadDataOnResume() {
        indexOrders();//查询订单
        queryStatis();
    }
}
