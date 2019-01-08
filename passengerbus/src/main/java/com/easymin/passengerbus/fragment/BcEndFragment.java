package com.easymin.passengerbus.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.easymi.component.base.RxBaseFragment;
import com.easymin.passengerbus.R;
import com.easymin.passengerbus.entity.BusStationResult;
import com.easymin.passengerbus.entity.BusStationsBean;
import com.easymin.passengerbus.flowMvp.ActFraCommBridge;
import com.easymin.passengerbus.flowMvp.BcFlowActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 行程结束
 */
public class BcEndFragment extends RxBaseFragment{

    private TextView tvLineAddress;
    private TextView tvEnd;

    private ActFraCommBridge bridge;

    private long scheduleId;

    private List<BusStationsBean> listLine = new ArrayList<>();

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

    private void initView() {
        listLine = BusStationsBean.findByScheduleId(scheduleId);

        tvLineAddress = $(R.id.tv_line_address);
        tvLineAddress.setText("终点站："+listLine.get(listLine.size()-1).address);
        tvEnd = $(R.id.tv_end);

        ((BcFlowActivity)getActivity()).initPop(listLine.size()-1);

        tvEnd.setOnClickListener(v ->{
            bridge.arriveEnd();
        });
    }
}
