package com.easymi.daijia.fragment;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easymi.component.base.RxBaseFragment;
import com.easymi.component.utils.PhoneUtil;
import com.easymi.component.widget.CustomSlideToUnlockView;
import com.easymi.daijia.R;
import com.easymi.daijia.entity.DJOrder;
import com.easymi.daijia.flowMvp.ActFraCommBridge;

/**
 * Created by developerLzh on 2017/11/13 0013.
 */

public class SlideArriveStartFragment extends RxBaseFragment {
    private DJOrder djOrder;

    private ActFraCommBridge bridge;

    public void setBridge(ActFraCommBridge bridge){
        this.bridge = bridge;
    }

    CustomSlideToUnlockView slideView;
    TextView startPlaceText;
    TextView endPlaceText;
    LinearLayout changeEndCon;
    FrameLayout callPhoneCon;

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        djOrder = (DJOrder) args.getSerializable("djOrder");
    }

    @Override
    public int getLayoutResId() {
        return R.layout.slide_arrive_start_fragment;
    }

    @Override
    public void finishCreateView(Bundle state) {
        initView();
    }

    private void initView() {
        startPlaceText = getActivity().findViewById(R.id.start_place);
        endPlaceText = getActivity().findViewById(R.id.end_place);
        slideView = getActivity().findViewById(R.id.slider);
        changeEndCon = getActivity().findViewById(R.id.change_end_con);
        callPhoneCon = getActivity().findViewById(R.id.call_phone_con);

        startPlaceText.setText(djOrder.startPlace);
        endPlaceText.setText(djOrder.endPlace);
        slideView.setmCallBack(new CustomSlideToUnlockView.CallBack() {
            @Override
            public void onSlide(int distance) {

            }

            @Override
            public void onUnlocked() {
                bridge.doArriveStart();
            }
        });
        callPhoneCon.setOnClickListener(view -> PhoneUtil.call(getActivity(),djOrder.passengerPhone));
        changeEndCon.setOnClickListener(view -> bridge.changeEnd());
    }
}
