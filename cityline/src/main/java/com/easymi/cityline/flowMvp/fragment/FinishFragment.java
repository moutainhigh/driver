package com.easymi.cityline.flowMvp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.easymi.cityline.R;
import com.easymi.cityline.StaticVal;
import com.easymi.cityline.flowMvp.ActFraCommBridge;
import com.easymi.component.base.RxBaseFragment;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by liuzihao on 2018/11/16.
 */

public class FinishFragment extends RxBaseFragment {

    private ActFraCommBridge bridge;

    TextView countDown;
    TextView back;

    Timer timer;
    TimerTask timerTask;

    int time = 5;

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
