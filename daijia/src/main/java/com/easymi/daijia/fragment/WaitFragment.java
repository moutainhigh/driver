package com.easymi.daijia.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easymi.component.base.RxBaseFragment;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.utils.PhoneUtil;
import com.easymi.component.widget.LoadingButton;
import com.easymi.daijia.R;
import com.easymi.daijia.entity.DJOrder;
import com.easymi.daijia.flowMvp.ActFraCommBridge;

/**
 * Created by developerLzh on 2017/11/13 0013.
 */

public class WaitFragment extends RxBaseFragment {
    private DymOrder djOrder;

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
        djOrder = (DymOrder) args.getSerializable("djOrder");
    }

    @Override
    public int getLayoutResId() {
        return R.layout.waiting_fragment;
    }

    @Override
    public void finishCreateView(Bundle state) {
        initView();
    }

    private void initView() {
        if (djOrder == null) {
            djOrder = new DymOrder();
        }
        startDrive = $(R.id.start_drive);
        waitTimeText = $(R.id.wait_time);
        waitFeeText = $(R.id.wait_fee);

        waitFeeText.setText(djOrder.waitTimeFee + "");
        waitTimeText.setText(djOrder.waitTime + "");

        startDrive.setOnClickListener(view -> bridge.doStartDrive(startDrive));

        $(R.id.change_end_con).setOnClickListener(view -> bridge.changeEnd());
    }

    public void showFee(DymOrder dymOrder) {
        this.djOrder = dymOrder;
        waitFeeText.setText(djOrder.waitTimeFee + "");
        waitTimeText.setText(djOrder.waitTime + "");
    }
}
