package com.easymi.daijia.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easymi.component.base.RxBaseFragment;
import com.easymi.component.utils.Log;
import com.easymi.component.utils.PhoneUtil;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.widget.LoadingButton;
import com.easymi.daijia.R;
import com.easymi.daijia.entity.DJOrder;
import com.easymi.daijia.flowMvp.ActFraCommBridge;
import com.easymi.daijia.widget.CallPhoneDialog;

/**
 * Created by developerLzh on 2017/11/13 0013.
 */

public class ArriveStartFragment extends RxBaseFragment {
    private DJOrder djOrder;

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
        djOrder = (DJOrder) args.getSerializable("djOrder");
    }

    @Override
    public int getLayoutResId() {
        return R.layout.arrive_fragment;
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

        startPlaceText.setText(djOrder.startPlace);
        endPlaceText.setText(StringUtils.isNotBlank(djOrder.endPlace) ? djOrder.endPlace:getString(R.string.des_place));
        startDrive.setOnClickListener(view -> bridge.doStartDrive(startDrive));
        startWait.setOnClickListener(view -> bridge.doStartWait());
        callPhoneCon.setOnClickListener(view -> CallPhoneDialog.callDialog(getActivity(),djOrder));
        $(R.id.change_end_con).setOnClickListener(view -> bridge.changeEnd());
    }
}
