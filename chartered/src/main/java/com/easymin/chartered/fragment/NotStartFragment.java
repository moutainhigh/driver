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
import com.easymin.chartered.flowMvp.ActFraCommBridge;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: NotStartFragment
 * Author: shine
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

//    ZXOrder zxOrder;

    ActFraCommBridge bridge;

    public void setBridge(ActFraCommBridge bridge) {
        this.bridge = bridge;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
//        zxOrder = (ZXOrder) args.getSerializable("zxOrder");
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

//        startSite.setText(zxOrder.startSite);
//        endSite.setText(zxOrder.endSite);
//        startOutTime.setText(TimeUtil.getTime("yyyy年MM月dd日 HH:mm", zxOrder.startOutTime) + "出发");
//        startJierenTime.setText(TimeUtil.getTime("HH:mm", zxOrder.startJierenTime) + "开始接人");
        initCountDown();

        onHiddenChanged(false);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            bridge.clearMap();
//            LatLng startLatlng = new LatLng(EmUtil.getLastLoc().latitude, EmUtil.getLastLoc().longitude);
//            LatLng middle = new LatLng(zxOrder.startLat, zxOrder.startLng);
//            LatLng endLatlng = new LatLng(zxOrder.endLat, zxOrder.endLng);
//            bridge.addMarker(startLatlng, StaticVal.MARKER_FLAG_START);
//            bridge.addMarker(endLatlng, StaticVal.MARKER_FLAG_END);
//            List<LatLng> latLngs = new ArrayList<>();
//            latLngs.add(middle);
//            bridge.routePath(startLatlng, latLngs, endLatlng);
//            latLngs.add(startLatlng);
//            latLngs.add(endLatlng);
//
//            bridge.showBounds(latLngs);
//            bridge.changeToolbar(StaticVal.TOOLBAR_NOT_START);
        }
    }

    long jieRenTimeLeftSec;

    private Timer timer;
    private TimerTask timerTask;

    private void initCountDown() {
        cancelTimer();
//        jieRenTimeLeftSec = (zxOrder.startJierenTime - System.currentTimeMillis()) / 1000;//剩余的秒钟数
        jieRenTimeLeftSec = 30*60;
        if (jieRenTimeLeftSec < 0) {
            jieRenTimeLeftSec = 0;
        }

        setLeftText(jieRenTimeLeftSec);

        if (jieRenTimeLeftSec > 0) {
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    jieRenTimeLeftSec--;
                    long sec = jieRenTimeLeftSec % (60 * 60 * 24);
                    if (sec != 0) {
                        return;//整分才往下走
                    }
                    getActivity().runOnUiThread(() -> {
                        setLeftText(jieRenTimeLeftSec);
                        if (jieRenTimeLeftSec <= 0) {
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

//        if (leftSec > 0) {
//            bottomBtn.setText("行程规划");
//            bottomBtn.setOnClickListener(view -> bridge.toChangeSeq(StaticVal.PLAN_ACCEPT));
//        } else {
//            bottomBtn.setText("行程规划");
//            bottomBtn.setOnClickListener(view -> {
//                DymOrder dymOrder = DymOrder.findByIDType(zxOrder.orderId, zxOrder.orderType);
//                if (null != dymOrder) {
//                    dymOrder.orderStatus = ZXOrderStatus.ACCEPT_PLAN;
//                    dymOrder.updateStatus();
//                }
//                bridge.toChangeSeq(StaticVal.PLAN_ACCEPT);
//            });
//        }
    }
}
