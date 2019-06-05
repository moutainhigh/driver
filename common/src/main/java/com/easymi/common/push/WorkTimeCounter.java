package com.easymi.common.push;

import android.content.Context;
import android.os.SystemClock;

import com.easymi.common.CommApiService;
import com.easymi.common.result.OnLineTimeResult;
import com.easymi.common.result.WorkStatisticsResult;
import com.easymi.component.Config;
import com.easymi.component.EmployStatus;
import com.easymi.component.app.XApp;
import com.easymi.component.entity.Employ;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.result.EmResult;
import com.easymi.component.result.EmResult2;
import com.easymi.component.utils.CsSharedPreferences;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.Log;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.utils.TimeUtil;
import com.easymi.component.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:WorkTimeCounter
 *
 * @Author: hufeng
 * Date: 2018/12/24 下午1:10
 * Description: 工作时间统计器，需要尽可能长久的工作。目前策略将其生命周期关联到推送服务生命上。比较没有推送，统计工作时间也没有什么意义。
 * History:
 */
public class WorkTimeCounter {

    //向服务器上传的时间间隔
    private static final long TIME_OFFSET = 60 * 1000;

    private Context context;

    /**
     * 总的分钟数（改为总的在线秒数，必须提一下，设计缺陷导致只有应用程序不被杀死的时候才能统计时间，杀死后不会增加工作时长）
     */
    private int totalMinute;

    /**
     * 定时器
     */
    private Timer timer;
    private TimerTask timerTask;

    private Subscription mSubscription;

    /**
     * 强制上传数据  -1刷新  1下线  2上线
     *
     * @param statues
     */
    public void forceUpload(int statues) {
        if (statues == 1) {
            uploadTime(statues, totalMinute);
        } else if (statues == 2) {
            getOnlinTime();
        } else {
            if (timer == null || timerTask == null) {
                getOnlinTime();
            }else {
                startCount();
            }
        }
    }

    /**
     * 获取司机在线时长
     */
    public void getOnlinTime() {
        Observable<OnLineTimeResult> observable = ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .getOnlineTime()
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        observable.subscribe(new MySubscriber<>(context, false,
                true, result -> {
            if (result != null) {

                totalMinute = (int) result.object;

                CountEvent event = new CountEvent();
                event.finishCount = -1;
                event.income = -1;
                event.minute = totalMinute / 60;
                EventBus.getDefault().post(event);

                startCount();
            }
        }));
    }

    /**
     * 初始化定时器
     *
     * @param context
     */
    public WorkTimeCounter(Context context) {
        Log.d("WorkTimeCounter", "WorkTimeCounter create");
        this.context = context;

    }

    /**
     * 初始化定时器，开始记时
     */
    public void startCount() {
        destroy();
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                count();
            }
        };
        //一分钟计时一次，延迟60s执行
        timer.schedule(timerTask, 1000, 1000);
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
            int bofore = totalMinute / 60;
            totalMinute++;
            int after = totalMinute / 60;
            if (after > bofore) {
                CountEvent event = new CountEvent();
                event.finishCount = -1;
                event.income = -1;
                event.minute = totalMinute / 60;
                EventBus.getDefault().post(event);

                uploadTime(-1, totalMinute);
            }
        }
    }

    /**
     * 向后台上传本地数据。
     *
     * @param minute 当前在线秒数
     */
    private void uploadTime(int statues, int minute) {

        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }

        Observable<EmResult> observable = ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .upLoadOnlineTime(minute)
                .filter(new HttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mSubscription = observable.subscribe(new MySubscriber<>(context, false,
                true, result -> {
            if (result.getCode() == 1) {
                if (statues == 1) {
                    //下线
                    destroy();
                    CountEvent event = new CountEvent();
                    event.finishCount = -1;
                    event.income = -1;
                    event.minute = 0;
                    EventBus.getDefault().post(event);
                } else {
                    //判断是否隔天
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());
                    calendar.set(Calendar.HOUR_OF_DAY, 24);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    Date start = calendar.getTime();

                    if ((start.getTime()-System.currentTimeMillis())/1000 < 60){
                        totalMinute = 0;
                    }

                }
            } else {
                ToastUtil.showMessage(context, result.getMessage());
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
