package com.easymi.zhuanche.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.easymi.component.base.RxBaseFragment;
import com.easymi.component.entity.ZCSetting;
import com.easymi.component.widget.CustomSlideToUnlockView;
import com.easymi.component.widget.LoadingButton;
import com.easymi.zhuanche.R;
import com.easymi.zhuanche.activity.CancelNewActivity;
import com.easymi.zhuanche.entity.ZCOrder;
import com.easymi.zhuanche.flowMvp.ActFraCommBridge;
import com.easymi.zhuanche.flowMvp.FlowActivity;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: NoBeginFragment
 * @Author: hufeng
 * @Date: 2019-11-26 09:14
 * @Description:
 * @History:
 */
public class NoBeginFragment extends RxBaseFragment {

    CustomSlideToUnlockView slider;
    TextView time_count_down;

    /**
     * 专车订单哪
     */
    private ZCOrder zcOrder;
    /**
     * activity和fragment的通信接口
     */
    private ActFraCommBridge bridge;

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
        zcOrder = (ZCOrder) args.getSerializable("zcOrder");
    }

    @Override
    public int getLayoutResId() {
        return R.layout.zc_no_begin_fragment;
    }

    @Override
    public void finishCreateView(Bundle state) {
        slider = $(R.id.slider);
        time_count_down = $(R.id.time_count_down);

        initCountDown();

        slider.setHint("开始行程");

        slider.setmCallBack(new CustomSlideToUnlockView.CallBack() {
            @Override
            public void onSlide(int distance) {

            }

            @Override
            public void onUnlocked() {
                //前往预约地就是开始行程
                bridge.doToStart(null);
            }
        });

    }


    long bookLeftSec;

    /**
     * 行程开始倒计时
     */
    private Timer timer;
    private TimerTask timerTask;

    /**
     * 初始化计时器
     */
    private void initCountDown() {
        cancelTimer();
        //剩余的秒钟数
        bookLeftSec = (zcOrder.bookTime * 1000 - System.currentTimeMillis()) / 1000;
        if (bookLeftSec < 0) {
            bookLeftSec = 0;
        }

        setLeftText(bookLeftSec);

        if (bookLeftSec > 0) {
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    if (!isAdded()) {
                        return;
                    }
                    if (bridge == null || getActivity() == null) {
                        return;
                    }
                    bookLeftSec--;
                    long sec = bookLeftSec % (60 * 60 * 24);
                    if (sec != 0) {
                        return;//整分才往下走
                    }
                    getActivity().runOnUiThread(() -> {
                        setLeftText(bookLeftSec);
                        if (bookLeftSec <= 0) {
                            cancelTimer();
                        }
                    });
                }
            };
            timer.schedule(timerTask, 0, 1000);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cancelTimer();
    }


    /**
     * 取消计时器
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
     * 设置显示距离开始时间的计时
     *
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
        time_count_down.setText(sb.toString());
    }

}
