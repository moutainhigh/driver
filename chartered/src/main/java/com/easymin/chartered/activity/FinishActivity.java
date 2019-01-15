package com.easymin.chartered.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.widget.CusToolbar;
import com.easymin.chartered.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: FinishActivity
 * Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */
public class FinishActivity extends RxBaseActivity{

    CusToolbar cusToolbar;
    TextView countDown;
    TextView back;

    /**
     * 5秒倒计时计时器
     */
    Timer timer;
    TimerTask timerTask;
    /**
     * 倒计时
     */
    int time = 5;

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_finish;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        cusToolbar = findViewById(R.id.cus_toolbar);
        countDown = findViewById(R.id.count_down);
        back = findViewById(R.id.btn);

        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                time--;
                if (time == 0) {
                    finish();
                }
                runOnUiThread(() -> countDown.setText(time + "秒后自动返回订单列表"));
            }
        };
        timer.schedule(timerTask, 1000, 1000);

        back.setOnClickListener(view -> finish());
    }

    @Override
    public void initToolBar() {
        super.initToolBar();
        cusToolbar.setTitle(R.string.order_over);
        cusToolbar.setLeftBack(view -> finish());
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

    @Override
    public void onPause() {
        super.onPause();
        cancelTimer();
    }
}
