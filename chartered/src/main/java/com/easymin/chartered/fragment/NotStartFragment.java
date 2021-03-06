package com.easymin.chartered.fragment;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.easymi.component.ZXOrderStatus;
import com.easymi.component.base.RxBaseFragment;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.TimeUtil;
import com.easymin.chartered.R;
import com.easymin.chartered.StaticVal;
import com.easymin.chartered.entity.CharteredOrder;
import com.easymin.chartered.flowMvp.ActFraCommBridge;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: NotStartFragment
 * @Author: shine
 * Date: 2018/12/22 下午1:30
 * Description:
 * History:
 */
public class NotStartFragment extends RxBaseFragment {

    TextView startSite;
    TextView endSite;
    TextView book_time;
    TextView timeCountDown;
    Button bottomBtn;

    CharteredOrder baseOrder;

    ActFraCommBridge bridge;
    /**
     * 设置bridge
     * @param bridge
     */
    public void setBridge(ActFraCommBridge bridge) {
        this.bridge = bridge;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        baseOrder = (CharteredOrder) args.getSerializable("baseOrder");
    }

    @Override
    public int getLayoutResId() {
        return R.layout.layout_no_start;
    }

    @Override
    public void finishCreateView(Bundle state) {
        startSite = $(R.id.start_site);
        endSite = $(R.id.end_site);
        book_time = $(R.id.book_time);
        timeCountDown = $(R.id.time_count_down);
        bottomBtn = $(R.id.bottom_btn);

        startSite.setText(baseOrder.getStartSite().address);
        endSite.setText(baseOrder.getEndSite().address);
        book_time.setText(TimeUtil.getTime("yyyy年MM月dd日 HH:mm", baseOrder.bookTime*1000) + "出发");
        initCountDown();

    }

    /**
     * 出发剩余时间
     */
    long startTimeLeftSec;

    /**
     * 定时器
     */
    private Timer timer;
    private TimerTask timerTask;

    /**
     * 初始化定时器
     */
    private void initCountDown() {
        cancelTimer();
        startTimeLeftSec = (baseOrder.bookTime*1000 - System.currentTimeMillis()) / 1000;//剩余的秒钟数
        if (startTimeLeftSec < 0) {
            startTimeLeftSec = 0;
        }

        setLeftText(startTimeLeftSec);

        if (startTimeLeftSec > 0) {
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    startTimeLeftSec--;
                    long sec = startTimeLeftSec % (60 * 60 * 24);
                    if (sec != 0) {
                        return;//整分才往下走
                    }
                    getActivity().runOnUiThread(() -> {
                        setLeftText(startTimeLeftSec);
                        if (startTimeLeftSec <= 0) {
                            bridge.countStartOver();
                            timer.cancel();
                            timer = null;
                            timerTask.cancel();
                            timerTask = null;
                        }
                    });

                }
            };
            timer.schedule(timerTask, 0, 1000);
        }
    }

    /**
     * 取消定时器
     */
    public void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }

    /**
     * 倒计时显示
     * @param leftSec
     */
    private void setLeftText(long leftSec) {
        long day = leftSec / 60 / 60 / 24;
        long hour = (leftSec / 60 / 60) % 24;
        long minute = leftSec / 60 % 60;
        long sec = leftSec % 60;

        if (minute == 0 && sec > 0) {
            minute++;
        }

        StringBuilder sb = new StringBuilder();
        if (day >= 10) {
            sb.append(String.valueOf(day));
        } else {
            sb.append("0").append(String.valueOf(day));
        }
        sb.append("天");

        if (hour >= 10) {
            sb.append(String.valueOf(hour));
        } else {
            sb.append("0").append(String.valueOf(hour));
        }
        sb.append("时");

        if (minute >= 10) {
            sb.append(String.valueOf(minute));
        } else {
            sb.append("0").append(String.valueOf(minute));
        }
        sb.append("分");
        timeCountDown.setText(sb.toString());

        bottomBtn.setOnClickListener(view -> {
            bridge.toStart();
        });

    }
}
