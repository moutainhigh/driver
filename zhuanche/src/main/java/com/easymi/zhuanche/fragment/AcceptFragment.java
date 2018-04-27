package com.easymi.zhuanche.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easymi.component.base.RxBaseFragment;
import com.easymi.component.widget.LoadingButton;
import com.easymi.zhuanche.R;
import com.easymi.zhuanche.entity.ZCOrder;
import com.easymi.zhuanche.flowMvp.ActFraCommBridge;

/**
 * Created by developerLzh on 2017/11/13 0013.
 */

public class AcceptFragment extends RxBaseFragment {

    LoadingButton acceptBtn;
    LinearLayout refuseCon;
    TextView startPlaceText;
    TextView endPlaceText;

    private ZCOrder zcOrder;

    private ActFraCommBridge bridge;

    private Fragment createFragment;

    public void setBridge(ActFraCommBridge bridge){
        this.bridge = bridge;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        zcOrder = (ZCOrder) args.getSerializable("zcOrder");
    }

    @Override
    public int getLayoutResId() {
        return R.layout.accept_fragment;
    }

    @Override
    public void finishCreateView(Bundle state) {
        initView();
    }

    private void initView() {
        acceptBtn = getActivity().findViewById(R.id.accept_btn);
        refuseCon = getActivity().findViewById(R.id.refuse_con);
        startPlaceText = getActivity().findViewById(R.id.start_place);
        endPlaceText = getActivity().findViewById(R.id.end_place);

        startPlaceText.setText(zcOrder.startPlace);
        endPlaceText.setText(zcOrder.endPlace);
        acceptBtn.setOnClickListener(view -> {
            if(null != bridge){
                acceptBtn.setClickable(false);
                acceptBtn.setStatus(LoadingButton.STATUS_LOADING);
                bridge.doAccept(acceptBtn);
            }
        });

        refuseCon.setOnClickListener(view -> {
            bridge.doRefuse();
        });
    }
}
