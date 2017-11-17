package com.easymi.daijia.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.easymi.component.base.RxBaseFragment;
import com.easymi.component.utils.PhoneUtil;
import com.easymi.daijia.R;
import com.easymi.daijia.entity.DJOrder;
import com.easymi.daijia.flowMvp.ActFraCommBridge;

/**
 * Created by developerLzh on 2017/11/13 0013.
 */

public class ToStartFragment extends RxBaseFragment {
    private DJOrder djOrder;

    private ActFraCommBridge bridge;

    public void setBridge(ActFraCommBridge bridge) {
        this.bridge = bridge;
    }

    Button controlCon;
    FrameLayout callPhoneCon;
    TextView startPlaceText;
    TextView endPlaceText;

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        djOrder = (DJOrder) args.getSerializable("djOrder");
    }

    @Override
    public int getLayoutResId() {
        return R.layout.to_start_fragment;
    }

    @Override
    public void finishCreateView(Bundle state) {
        initView();
    }

    private void initView() {
        startPlaceText = getActivity().findViewById(R.id.start_place);
        endPlaceText = getActivity().findViewById(R.id.end_place);
        controlCon = getActivity().findViewById(R.id.to_start_btn);
        callPhoneCon = getActivity().findViewById(R.id.call_phone_con);

        startPlaceText.setText(djOrder.startPlace);
        endPlaceText.setText(djOrder.endPlace);
        controlCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("tag", "onClick");
                bridge.doToStart();
            }
        });
        callPhoneCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("tag", "onClick");
                PhoneUtil.call(getActivity(), djOrder.passengerPhone);
            }
        });
    }
}
