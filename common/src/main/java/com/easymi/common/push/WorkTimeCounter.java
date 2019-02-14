package com.easymi.common.push;

import android.content.Context;
import android.os.SystemClock;

import com.easymi.common.CommApiService;
import com.easymi.common.result.WorkStatisticsResult;
import com.easymi.component.Config;
import com.easymi.component.EmployStatus;
import com.easymi.component.app.XApp;
import com.easymi.component.entity.Employ;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.Log;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.utils.TimeUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.Timer;
import java.util.TimerTask;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:WorkTimeCounter
 * @Author: hufeng
 * Date: 2018/12/24 下午1:10
 * Description: 工作时间统计器，需要尽可能长久的工作。目前策略将其生命周期关联到推送服务生命上。比较没有推送，统计工作时间也没有什么意义。
 * History:
 */
public class WorkTimeCounter {

    //向服务器上传的时间间隔
    private static final long TIME_OFFSET = 2 * 60 * 1000;

    private Context context;

    /**
     * 最后上传时间
     */
    private long lastUpTime;
    /**
     * 总的分钟数
     */
    private int totalMinute;

    /**
     * 定时器
     */
    private final Timer timer;
    private final TimerTask timerTask;

    private Subscription mSubscription;

    /**
     * 初始化定时器
     * @param context
     */
    public WorkTimeCounter(Context context) {
        Log.d("WorkTimeCounter", "WorkTimeCounter create");
        this.context = context;
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                count();
            }
        };
        //一分钟计时一次，延迟60s执行
        timer.schedule(timerTask, 60 * 1000, 60 * 1000);
        //初始化从服务器拉一次。
        uploadTime(-1, 0);

    }

    /**
     * 每个计时时间间隔计数一次。
     */
    private void count() {
        Employ employ = EmUtil.getEmployInfo();
        if (employ == null || StringUtils.isBlank(String.valueOf(employ.status))) {
            return;
        }
        if (employ.status >= 3) {
            totalMinute++;
            long current = SystemClock.uptimeMillis();
            if (current - lastUpTime >= TIME_OFFSET) {
                lastUpTime = SystemClock.uptimeMillis();
                uploadTime(-1, totalMinute);
            } else {
                CountEvent event = new CountEvent();
                event.finishCount = -1;
                event.income = -1;
                event.minute = totalMinute;
                EventBus.getDefault().post(event);
            }
        }
    }

    public void forceUpload(int statues) {
        uploadTime(statues, totalMinute);
    }

    /**
     * 向后台上传本地数据。
     *
     * @param minute 当前在线分钟数，0表示无效，不影响统计
     */
    private void uploadTime(int statues, int minute) {

        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }


        Employ employ = EmUtil.getEmployInfo();
        if (employ == null) {
            return;
        }

        /**
         * 根据本地缓存的上班时间戳进行计算听单时长 start
         */
        if ( employ.status > 1 && XApp.getMyPreferences().getLong(Config.ONLINE_TIME,0) != 0){
            totalMinute = (int) ((System.currentTimeMillis() - XApp.getMyPreferences().getLong(Config.ONLINE_TIME,0))/(1000 * 60));
        }else {
            totalMinute = 0;
        }

        /**
         * 根据本地缓存的上班时间戳进行计算听单时长 end
         */

//        if (employ.auditType == 2 || employ.auditType == 3 || employ.auditType == 4) {
//            return;
//        }
        long driverId = employ.id;
        String driverNo = employ.userName;
        long companyId = employ.company_id;

        int driverStatus;
        if (statues <= 0) {
            driverStatus = 3;
            if (EmUtil.getEmployInfo() != null && String.valueOf(EmUtil.getEmployInfo().status).equals(EmployStatus.ONLINE)) {
                driverStatus = 2;
            }
        } else {
            driverStatus = statues;
        }

        String nowDate = TimeUtil.getTime("yyyy-MM-dd", System.currentTimeMillis());

        Observable<WorkStatisticsResult> observable = ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
//                .workStatistics(driverId, nowDate, EmUtil.getAppKey(), driverStatus, minute, driverNo, companyId)
                .workStatistics()
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mSubscription = observable.subscribe(new MySubscriber<>(context, false,
                true, result -> {
            if (result != null && result.workStatistics != null) {
                //值已后台返回为准
//                totalMinute = result.workStatistics.minute;
                CountEvent event = new CountEvent();
                event.finishCount = result.workStatistics.finishCount;
                event.income = result.workStatistics.income;
                event.minute = minute;
                EventBus.getDefault().post(event);
            }
        }));
    }

    /**
     * 销毁定时器
     */
    public void destroy() {
        if (timer != null) {
            timer.cancel();
        }
        if (timerTask != null) {
            timerTask.cancel();
        }
    }

}
