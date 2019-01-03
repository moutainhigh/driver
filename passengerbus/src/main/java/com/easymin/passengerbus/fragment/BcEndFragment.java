package com.easymin.passengerbus.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.easymi.component.base.RxBaseFragment;
import com.easymin.passengerbus.R;
import com.easymin.passengerbus.entity.BusStationResult;
import com.easymin.passengerbus.flowMvp.ActFraCommBridge;
import com.easymin.passengerbus.flowMvp.BcFlowActivity;

/**
 * 行程结束
 */
public class BcEndFragment extends RxBaseFragment{

    private TextView tvLineAddress;
    private TextView tvEnd;

    private ActFraCommBridge bridge;

    private BusStationResult result;


    public void setBridge(ActFraCommBridge bridge) {
        this.bridge = bridge;
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        result = (BusStationResult) args.getSerializable("busLineResult");

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
        tvLineAddress = $(R.id.tv_line_address);
        tvLineAddress.setText(result.stationVos.get(result.stationVos.size()-1).address);
        tvEnd = $(R.id.tv_end);

        tvEnd.setOnClickListener(v -> bridge.arriveEnd());
    }
}
