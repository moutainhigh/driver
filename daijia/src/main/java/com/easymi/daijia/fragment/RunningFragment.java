package com.easymi.daijia.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easymi.component.base.RxBaseFragment;
import com.easymi.component.utils.PhoneUtil;
import com.easymi.component.widget.LoadingButton;
import com.easymi.daijia.R;
import com.easymi.daijia.entity.DJOrder;
import com.easymi.daijia.flowMvp.ActFraCommBridge;

/**
 * Created by developerLzh on 2017/11/13 0013.
 */

public class RunningFragment extends RxBaseFragment {
    private DJOrder djOrder;

    private ActFraCommBridge bridge;

    public void setBridge(ActFraCommBridge bridge) {
        this.bridge = bridge;
    }


    TextView serviceMoneyText;
    TextView distanceText;

    TextView driveTimeText;
    TextView waitTimeText;

    LoadingButton startWaitBtn;
    LinearLayout settleBtn;

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        djOrder = (DJOrder) args.getSerializable("djOrder");
    }

    @Override
    public int getLayoutResId() {
        return R.layout.running_fragment;
    }

    @Override
    public void finishCreateView(Bundle state) {
        initView();
    }

    private void initView() {
        serviceMoneyText = getActivity().findViewById(R.id.service_money);
        distanceText = getActivity().findViewById(R.id.distance);
        driveTimeText = getActivity().findViewById(R.id.drive_time);
        waitTimeText = getActivity().findViewById(R.id.wait_time);
        startWaitBtn = getActivity().findViewById(R.id.start_wait);
        settleBtn = getActivity().findViewById(R.id.settle);

        serviceMoneyText.setText(djOrder.orderMoney + "");
        distanceText.setText(djOrder.orderDistance + "");
        driveTimeText.setText(djOrder.driveTime + "");
        waitTimeText.setText(djOrder.waitTime + "");

        startWaitBtn.setOnClickListener(view -> bridge.doStartWait(startWaitBtn));
        settleBtn.setOnClickListener(view -> bridge.showSettleDialog());
    }
}
