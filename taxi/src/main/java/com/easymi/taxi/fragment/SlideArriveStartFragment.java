package com.easymi.taxi.fragment;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easymi.component.base.RxBaseFragment;
import com.easymi.component.widget.CustomSlideToUnlockView;
import com.easymi.taxi.R;
import com.easymi.taxi.entity.TaxiOrder;
import com.easymi.taxi.flowMvp.ActFraCommBridge;
import com.easymi.taxi.widget.CallPhoneDialog;

/**
 * Created by developerLzh on 2017/11/13 0013.
 */

public class SlideArriveStartFragment extends RxBaseFragment {
    private TaxiOrder taxiOrder;

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
        taxiOrder = (TaxiOrder) args.getSerializable("taxiOrder");
    }

    @Override
    public int getLayoutResId() {
        return R.layout.taxi_slide_arrive_start_fragment;
    }

    @Override
    public void finishCreateView(Bundle state) {
        initView();
    }

    private void initView() {
        startPlaceText = $(R.id.start_place);
        endPlaceText = $(R.id.end_place);
        slideView = $(R.id.slider);
        changeEndCon = $(R.id.change_end_con);
        callPhoneCon = $(R.id.call_phone_con);

        startPlaceText.setText(taxiOrder.bookAddress);
        endPlaceText.setText(taxiOrder.destination);
        slideView.setmCallBack(new CustomSlideToUnlockView.CallBack() {
            @Override
            public void onSlide(int distance) {

            }

            @Override
            public void onUnlocked() {
                bridge.doArriveStart();
            }
        });
        callPhoneCon.setOnClickListener(view -> CallPhoneDialog.callDialog(getActivity(), taxiOrder));
        changeEndCon.setOnClickListener(view -> bridge.changeEnd());
    }
}