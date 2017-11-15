package com.easymi.daijia.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easymi.component.base.RxBaseFragment;
import com.easymi.component.utils.PhoneUtil;
import com.easymi.daijia.R;
import com.easymi.daijia.entity.DJOrder;
import com.easymi.daijia.flowMvp.ActFraCommBridge;

/**
 * Created by developerLzh on 2017/11/13 0013.
 */

public class WaitFragment extends RxBaseFragment {
    private DJOrder djOrder;

    private ActFraCommBridge bridge;

    public void setBridge(ActFraCommBridge bridge){
        this.bridge = bridge;
    }

    TextView waitTimeText;
    TextView waitFeeText;

    Button startDrive;

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        djOrder = (DJOrder) args.getSerializable("djOrder");
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
        startDrive = getActivity().findViewById(R.id.start_drive);
        waitTimeText = getActivity().findViewById(R.id.wait_time);
        waitFeeText = getActivity().findViewById(R.id.wait_fee);

        waitFeeText.setText(djOrder.waitFee+"");
        waitTimeText.setText(djOrder.waitTime+"");

        startDrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bridge.doStartDrive();
            }
        });
    }
}
