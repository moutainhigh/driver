package com.easymin.passengerbus.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.easymi.component.base.RxBaseFragment;
import com.easymin.passengerbus.R;
import com.easymin.passengerbus.flowMvp.ActFraCommBridge;

/**
 * 行程结束
 */
public class BcEndFragment extends RxBaseFragment{

    private TextView tvLineAddress;
    private TextView tvEnd;

    private ActFraCommBridge bridge;

    public void setBridge(ActFraCommBridge bridge) {
        this.bridge = bridge;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.flow_status_three_step_layout;
    }

    @Override
    public void finishCreateView(Bundle state) {
//        bridge.changeToolbar(BcFlowActivity.ENDRUNING);
        initView();

    }

    private void initView() {
        tvLineAddress = $(R.id.tv_line_address);
        tvEnd = $(R.id.tv_end);

        tvEnd.setOnClickListener(v -> bridge.arriveEnd());
    }
}
