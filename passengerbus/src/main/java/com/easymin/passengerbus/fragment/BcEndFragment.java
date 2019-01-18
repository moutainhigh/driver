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
 * Description: 行程结束
 * History:
 */
public class BcEndFragment extends RxBaseFragment{

    private TextView tvLineAddress;
    private LoadingButton tvEnd;
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
        return R.layout.flow_status_three_step_layout;
    }

    @Override
    public void finishCreateView(Bundle state) {
        bridge.changeToolbar(BcFlowActivity.ENDRUNING);
        initView();
    }

    /**
     * 初始化界面
     */
    private void initView() {
        listLine = BusStationsBean.findByScheduleId(scheduleId);

        tvLineAddress = $(R.id.tv_line_address);
        tvLineAddress.setText("终点站："+listLine.get(listLine.size()-1).address);
        tvEnd = $(R.id.tv_end);

        ((BcFlowActivity)getActivity()).initPop(listLine.size()-1);

        tvEnd.setOnClickListener(v ->{
            bridge.arriveEnd(tvEnd);
        });
    }
}
