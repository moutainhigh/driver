package com.easymi.zhuanche.fragment;

import android.os.Bundle;
import android.widget.TextView;

import com.easymi.component.base.RxBaseFragment;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.widget.LoadingButton;
import com.easymi.zhuanche.R;
import com.easymi.zhuanche.flowMvp.ActFraCommBridge;

/**
 * Created by developerLzh on 2017/11/13 0013.
 */

public class WaitFragment extends RxBaseFragment {
    private DymOrder zcOrder;

    private ActFraCommBridge bridge;

    public void setBridge(ActFraCommBridge bridge) {
        this.bridge = bridge;
    }

    TextView waitTimeText;
    TextView waitFeeText;

    LoadingButton startDrive;

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        zcOrder = (DymOrder) args.getSerializable("zcOrder");
    }

    @Override
    public int getLayoutResId() {
        return R.layout.zc_waiting_fragment;
    }

    @Override
    public void finishCreateView(Bundle state) {
        initView();
    }

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

    public void showFee(DymOrder dymOrder) {
        this.zcOrder = dymOrder;
        waitFeeText.setText(zcOrder.waitTimeFee + "");
        waitTimeText.setText(zcOrder.waitTime + "");
    }
}
