package com.easymi.daijia.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easymi.component.base.RxBaseFragment;
import com.easymi.component.base.RxLazyFragment;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.widget.LoadingButton;
import com.easymi.daijia.R;
import com.easymi.daijia.entity.DJOrder;
import com.easymi.daijia.flowMvp.ActFraCommBridge;

/**
 * Created by developerLzh on 2017/11/13 0013.
 */

public class AcceptFragment extends RxBaseFragment {

    LoadingButton acceptBtn;
    LinearLayout refuseCon;
    TextView startPlaceText;
    TextView endPlaceText;

    private DJOrder djOrder;

    private ActFraCommBridge bridge;

    private Fragment createFragment;

    public void setBridge(ActFraCommBridge bridge){
        this.bridge = bridge;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        djOrder = (DJOrder) args.getSerializable("djOrder");
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

        startPlaceText.setText(djOrder.startPlace);
        endPlaceText.setText(StringUtils.isNotBlank(djOrder.endPlace) ? djOrder.endPlace:getString(R.string.des_place));
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
