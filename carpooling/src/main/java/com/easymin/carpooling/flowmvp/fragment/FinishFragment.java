package com.easymin.carpooling.flowmvp.fragment;

import android.os.Bundle;
import android.widget.TextView;

import com.easymi.component.base.RxBaseFragment;
import com.easymin.carpooling.R;
import com.easymin.carpooling.StaticVal;
import com.easymin.carpooling.flowmvp.ActFraCommBridge;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: FinishFragment
 *
 * @Author: hufeng
 * Date: 2018/12/24 下午1:10
 * Description: 完成订单界面
 * History:
 */

public class FinishFragment extends RxBaseFragment {
    /**
     * activity和fragment的通信接口
     */
    private ActFraCommBridge bridge;

    TextView countDown;
    TextView back;

    /**
     * 倒计时定时器
     */
    Timer timer;
    TimerTask timerTask;

    /**
     * 倒计时5s
     */
    int time = 5;

    /**
     * 设置bridge
     *
     * @param bridge
     */
    public void setBridge(ActFraCommBridge bridge) {
        this.bridge = bridge;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_finish;
    }

    @Override
    public void finishCreateView(Bundle state) {
        bridge.changeToolbar(StaticVal.TOOLBAR_FINISH);
        countDown = $(R.id.count_down);
        back = $(R.id.btn);

        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                time--;
                if (time == 0) {
                    bridge.toOrderList();
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        countDown.setText(time + "秒后自动返回订单列表");
                    }
                });
            }
        };
        timer.schedule(timerTask, 1000, 1000);
        back.setOnClickListener(view -> bridge.toOrderList());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cancelTimer();
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
}
