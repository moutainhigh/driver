package com.easymin.passengerbus.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.easymi.component.base.RxBaseFragment;
import com.easymin.passengerbus.R;
import com.easymin.passengerbus.entity.BusStationResult;
import com.easymin.passengerbus.entity.ChangeTitleEvent;
import com.easymin.passengerbus.flowMvp.ActFraCommBridge;

import org.greenrobot.eventbus.EventBus;

/**
 * 开始行程
 */
public class BcStartFragment extends RxBaseFragment {

    private TextView tvLineAddress;
    private TextView tvTip;
    private TextView tvStart;

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
        return R.layout.flow_status_one_step_layout;
    }

    @Override
    public void finishCreateView(Bundle state) {
        initView();
    }

    private void initView() {
        tvLineAddress = $(R.id.tv_line_address);
        tvTip = $(R.id.tv_tip);
        tvStart = $(R.id.tv_start);


        if (result == null) {
            return;
        }
        tvLineAddress.setText("起点站：" + result.startStation);
        tvStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeTitleEvent event = new ChangeTitleEvent();
                event.setChange(true);
                EventBus.getDefault().post(event);

            }
        });
    }
}
