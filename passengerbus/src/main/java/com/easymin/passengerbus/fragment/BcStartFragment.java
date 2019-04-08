package com.easymin.passengerbus.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.easymi.component.base.RxBaseFragment;
import com.easymi.component.widget.LoadingButton;
import com.easymin.passengerbus.R;
import com.easymin.passengerbus.entity.BusStationResult;
import com.easymin.passengerbus.entity.BusStationsBean;
import com.easymin.passengerbus.flowmvp.ActFraCommBridge;
import com.easymin.passengerbus.flowmvp.BcFlowActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: FlowModel
 * @Author: shine
 * Date: 2018/12/18 下午1:59
 * Description: 开始行程
 * History:
 */

public class BcStartFragment extends RxBaseFragment {

    private TextView tvLineAddress;
    private LoadingButton tvStart;
    /**
     * activity和fragment的通信接口
     */
    private ActFraCommBridge bridge;

    private long scheduleId;

    private List<BusStationsBean> listLine = new ArrayList<>();

    /**
     * 设置bridge
     * @param bridge
     */
    public void setBridge(ActFraCommBridge bridge) {
        this.bridge = bridge;
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        scheduleId = args.getLong("scheduleId");
    }

    @Override
    public int getLayoutResId() {
        return R.layout.flow_status_one_step_layout;
    }

    @Override
    public void finishCreateView(Bundle state) {
        bridge.changeToolbar(BcFlowActivity.STARTRUNNING);

        initView();
    }

    /**
     * 加载布局
     */
    private void initView() {
        tvLineAddress = $(R.id.tv_line_address);
        tvStart = $(R.id.tv_start);

        listLine = BusStationsBean.findByScheduleId(scheduleId);

        if (listLine == null || listLine.size() == 0) {
            return;
        }
        tvLineAddress.setText("起点站：" + listLine.get(0).address);
        tvStart.setOnClickListener(v ->{
            bridge.arriveStart(tvStart);
        });
    }
}
