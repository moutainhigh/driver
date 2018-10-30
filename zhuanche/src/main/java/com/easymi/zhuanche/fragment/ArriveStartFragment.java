package com.easymi.zhuanche.fragment;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easymi.component.base.RxBaseFragment;
import com.easymi.component.widget.LoadingButton;
import com.easymi.zhuanche.R;
import com.easymi.zhuanche.entity.ZCOrder;
import com.easymi.zhuanche.flowMvp.ActFraCommBridge;
import com.easymi.zhuanche.widget.CallPhoneDialog;

/**
 * Created by developerLzh on 2017/11/13 0013.
 */

public class ArriveStartFragment extends RxBaseFragment {
    private ZCOrder zcOrder;

    private ActFraCommBridge bridge;

    public void setBridge(ActFraCommBridge bridge) {
        this.bridge = bridge;
    }

    LoadingButton startDrive;
    LinearLayout startWait;
    TextView startPlaceText;
    TextView endPlaceText;
    FrameLayout callPhoneCon;

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        zcOrder = (ZCOrder) args.getSerializable("zcOrder");
    }

    @Override
    public int getLayoutResId() {
        return R.layout.zc_arrive_fragment;
    }

    @Override
    public void finishCreateView(Bundle state) {
        initView();
    }

    private void initView() {
        startPlaceText = $(R.id.start_place);
        endPlaceText = $(R.id.end_place);
        startDrive = $(R.id.start_drive);
        startWait = $(R.id.start_wait);
        callPhoneCon = $(R.id.call_phone_con);

        startPlaceText.setText(zcOrder.startPlace);
        endPlaceText.setText(zcOrder.endPlace);
        startDrive.setOnClickListener(view -> bridge.doStartDrive(startDrive));
        startWait.setOnClickListener(view -> bridge.doStartWait());
        callPhoneCon.setOnClickListener(view -> CallPhoneDialog.callDialog(getActivity(),zcOrder));
        $(R.id.change_end_con).setOnClickListener(view -> bridge.changeEnd());
    }
}
