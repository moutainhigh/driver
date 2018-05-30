package com.easymi.common.mvp.work;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.easymi.common.R;
import com.easymi.common.daemon.DaemonService;
import com.easymi.common.daemon.JobKeepLiveService;
import com.easymi.common.entity.MultipleOrder;
import com.easymi.common.entity.NearDriver;
import com.easymi.common.entity.WorkStatistics;
import com.easymi.common.result.AnnouncementResult;
import com.easymi.common.result.LoginResult;
import com.easymi.common.result.NearDriverResult;
import com.easymi.common.result.NotitfyResult;
import com.easymi.common.result.QueryOrdersResult;
import com.easymi.common.result.SettingResult;
import com.easymi.common.result.WorkStatisticsResult;
import com.easymi.component.Config;
import com.easymi.component.EmployStatus;
import com.easymi.component.app.XApp;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.entity.Employ;
import com.easymi.component.entity.Setting;
import com.easymi.component.network.ErrCode;
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.network.NoErrSubscriberListener;
import com.easymi.component.result.EmResult;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.Log;
import com.easymi.component.utils.PhoneUtil;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.utils.TimeUtil;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.LoadingButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import rx.Observable;

/**
 * Created by developerLzh on 2017/11/17 0017.
 */

public class WorkPresenter implements WorkContract.Presenter {

    private Context context;

    private WorkContract.View view;
    private WorkContract.Model model;

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    Bundle bundle = message.getData();
                    WorkStatistics statistics = (WorkStatistics) bundle.getSerializable("statis");
                    view.showStatis(statistics);
                    break;
            }
            return true;
        }
    });

    public WorkPresenter(Context context, WorkContract.View view) {
        this.context = context;
        this.view = view;
        model = new WorkModel();
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

        Observable<QueryOrdersResult> observable = model.indexOrders(driverId, EmUtil.getAppKey());
        view.getRxManager().add(observable.subscribe(new MySubscriber<>(context, false, false, new HaveErrSubscriberListener<QueryOrdersResult>() {
            @Override
            public void onNext(QueryOrdersResult emResult) {
                view.stopRefresh();
                List<MultipleOrder> orders = emResult.orders;
                List<MultipleOrder> nowOrders = new ArrayList<>();
                List<MultipleOrder> yuyueOrders = new ArrayList<>();
                if (orders != null) {
                    for (MultipleOrder order : orders) {
                        DymOrder dymOrder;
                        if (DymOrder.exists(order.orderId, order.orderType)) {//校验本地订单与服务器订单
                            dymOrder = DymOrder.findByIDType(order.orderId, order.orderType);
                            dymOrder.orderStatus = order.orderStatus;
                            dymOrder.updateStatus();
                        } else {//服务器有本地没得 创建数据
                            dymOrder = new DymOrder(order.orderId, order.orderType,
                                    order.passengerId, order.orderStatus);
                            dymOrder.save();
                        }

                        order.viewType = MultipleOrder.ITEM_POSTER;
                        if (order.isBookOrder == 2) {
                            nowOrders.add(order);
                        } else {
                            yuyueOrders.add(order);
                        }
                    }

                    List<DymOrder> allDym = DymOrder.findAll();
                    for (DymOrder dymOrder : allDym) {
                        boolean isExist = false;
                        for (MultipleOrder order : orders) {
                            if (dymOrder.orderId == order.orderId
                                    && dymOrder.orderType.equals(order.orderType)) {
                                isExist = true;
                                break;
                            }
                        }
                        if (!isExist) {
                            dymOrder.delete();
                        }
                    }
                    orders.clear();
                    //预约header
                    if (yuyueOrders.size() != 0) {
                        MultipleOrder header1 = new MultipleOrder(MultipleOrder.ITEM_HEADER);
                        header1.isBookOrder = 1;
                        orders.add(header1);
                    }

                    //预约单
                    orders.addAll(yuyueOrders);

                    //即时header
                    if (nowOrders.size() != 0) {
                        MultipleOrder header2 = new MultipleOrder(MultipleOrder.ITEM_HEADER);
                        header2.isBookOrder = 2;
                        orders.add(header2);
                    }
                    //即时单
                    orders.addAll(nowOrders);
                } else {
                    DymOrder.deleteAll();
                    orders = new ArrayList<>();
                }


                startLocService();//重启定位更改定位周期

                view.showOrders(orders);
            }

            @Override
            public void onError(int code) {
                view.stopRefresh();
                startLocService();//重启定位更改定位周期
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

        Observable<EmResult> observable = model.online(driverId, EmUtil.getAppKey());
        view.getRxManager().add(observable.subscribe(new MySubscriber<>(context, btn, emResult -> view.onlineSuc())));
    }

    @Override
    public void offline() {
        long driverId = EmUtil.getEmployId();

        Observable<EmResult> observable = model.offline(driverId, EmUtil.getAppKey());
        view.getRxManager().add(observable.subscribe(new MySubscriber<>(context, true,
                true, emResult -> {
            onPause();
            view.offlineSuc();
        })));
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
                false, notitfyResult -> view.showAnn(notitfyResult.employAfficheRequest))));
    }

    @Override
    public void queryNearDriver(Double lat, Double lng) {
        long driverId = EmUtil.getEmployId();
        double dis = 20;

        Observable<NearDriverResult> observable = model.queryNearDriver(driverId, lat, lng, dis);
        view.getRxManager().add(observable.subscribe(new MySubscriber<>(context, false,
                true, nearDriverResult -> {
            if (null != nearDriverResult.emploies && nearDriverResult.emploies.size() != 0) {
                List<NearDriver> nearDrivers = new ArrayList<>();
                for (NearDriver employ : nearDriverResult.emploies) {
                    if (!employ.status.equals(EmployStatus.ONLINE)
                            && !employ.status.equals(EmployStatus.OFFLINE)
                            && !employ.status.equals(EmployStatus.FROZEN)) {
                        nearDrivers.add(employ);
                    }
                }
                view.showDrivers(nearDrivers);
            } else {
                view.showDrivers(nearDriverResult.emploies);
            }
        })));
    }

    @Override
    public void queryStatis() {
        long driverId = EmUtil.getEmployId();
        int driverStatus = 2;
        if (EmUtil.getEmployInfo() != null && EmUtil.getEmployInfo().status.equals(EmployStatus.ONLINE)) {
            driverStatus = 1;
        }
        String nowDate = TimeUtil.getTime("yyyy-MM-dd", System.currentTimeMillis());
        Observable<WorkStatisticsResult> observable = model.getDriverStatistics(driverId, nowDate, driverStatus);
        view.getRxManager().add(observable.subscribe(new MySubscriber<>(context, false,
                true, result -> {
            view.showStatis(result.workStatistics);
            startLineTimer(result.workStatistics);
        })));
    }

    @Override
    public void loadDataOnResume() {
        indexOrders();//查询订单
        loadEmploy(EmUtil.getEmployId());
        getAppSetting();//获取配置信息
        queryStatis();

        PhoneUtil.checkGps(context);
    }

    private Timer timer;
    private TimerTask timerTask;

    @Override
    public void startLineTimer(WorkStatistics workStatistics) {
        if (timer != null) {
            timer.cancel();
        }
        if (timerTask != null) {
            timerTask.cancel();
        }
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Employ employ = EmUtil.getEmployInfo();
                if (employ != null && StringUtils.isNotBlank(employ.status)) {
                    if (employ.status.equals(EmployStatus.ONLINE)) {
//                        showOffline();//非听单状态
                    } else {
                        workStatistics.minute++;
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("statis", workStatistics);
                        Message message = new Message();
                        message.what = 0;
                        message.setData(bundle);
                        handler.sendMessage(message);//听单状态
                    }
                }

            }
        };
        timer.schedule(timerTask, 60 * 1000, 60 * 1000);

    }

    @Override
    public void onPause() {
        if (null != timer) {
            timer.cancel();
        }
        if (null != timerTask) {
            timerTask.cancel();
        }
    }

    @Override
    public void loadEmploy(long id) {

        Observable<LoginResult> observable = model.getEmploy(id, EmUtil.getAppKey());
        view.getRxManager().add(observable.subscribe(new MySubscriber<>(context, false,
                true, new HaveErrSubscriberListener<LoginResult>() {
            @Override
            public void onNext(LoginResult result) {
                Employ employ = result.getEmployInfo();
                String udid = XApp.getMyPreferences().getString(Config.SP_UDID, "");
                if (StringUtils.isNotBlank(employ.device_no)
                        && StringUtils.isNotBlank(udid)) {
                    if (!employ.device_no.equals(udid)) {
                        ToastUtil.showMessage(context, context.getString(R.string.unbunding));
                        EmUtil.employLogout(context);
                        return;
                    }
                }
                employ.saveOrUpdate();
                SharedPreferences.Editor editor = XApp.getPreferencesEditor();
                editor.putLong(Config.SP_DRIVERID, employ.id);
                editor.apply();
                view.showDriverStatus();
            }

            @Override
            public void onError(int code) {
                if (code == ErrCode.QUERY_ERROR.getCode()) {
                    EmUtil.employLogout(context);
                }
            }
        })));
    }

    @Override
    public void getAppSetting() {
        Observable<SettingResult> observable = model.getAppSetting();
        view.getRxManager().add(observable.subscribe(new MySubscriber<>(context, false,
                true, result -> {
            Setting.deleteAll();
            result.setting.save();
        })));
    }
}
