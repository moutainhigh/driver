package com.easymi.taxi.fragment;

import android.os.Bundle;
import android.widget.TextView;

import com.easymi.component.base.RxBaseFragment;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.widget.LoadingButton;
import com.easymi.taxi.R;
import com.easymi.taxi.flowMvp.ActFraCommBridge;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: WaitFragment
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description: 等待布局 未使用
 * History:
 */
public class WaitFragment extends RxBaseFragment {
    private DymOrder zcOrder;
    /**
     * activity和fragment的通信接口
     */
    private ActFraCommBridge bridge;

    /**
     * 设置bridge
     * @param bridge
     */
    public void setBridge(ActFraCommBridge bridge) {
        this.bridge = bridge;
    }

    TextView waitTimeText;
    TextView waitFeeText;

    LoadingButton startDrive;

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        zcOrder = (DymOrder) args.getSerializable("taxiOrder");
    }

    @Override
    public int getLayoutResId() {
        return R.layout.taxi_waiting_fragment;
    }

    @Override
    public void finishCreateView(Bundle state) {
        initView();
    }

    /**
     * 初始化布局
     */
    private void initView() {
        if (zcOrder == null) {
            zcOrder = new DymOrder();
        }
        startDrive = $(R.id.start_drive);
        waitTimeText = $(R.id.wait_time);
        waitFeeText = $(R.id.wait_fee);

        waitFeeText.setText(zcOrder.waitTimeFee + "");
        waitTimeText.setText(zcOrder.waitTime + "");

        startDrive.setOnClickListener(view -> bridge.doStartDrive(startDrive));

        $(R.id.change_end_con).setOnClickListener(view -> bridge.changeEnd());
    }

    /**
     * 动态显示费用
     * @param dymOrder
     */
    public void showFee(DymOrder dymOrder) {
        this.zcOrder = dymOrder;
        getActivity().runOnUiThread(() -> {
            waitFeeText.setText(zcOrder.waitTimeFee + "");
            waitTimeText.setText(zcOrder.waitTime + "");
        });
    }
}
