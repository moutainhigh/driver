package com.easymi.taxi.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easymi.component.base.RxBaseFragment;
import com.easymi.component.widget.LoadingButton;
import com.easymi.taxi.R;
import com.easymi.taxi.entity.TaxiOrder;
import com.easymi.taxi.flowMvp.ActFraCommBridge;

/**
 * Created by developerLzh on 2017/11/13 0013.
 */

public class AcceptFragment extends RxBaseFragment {

    LoadingButton acceptBtn;
    LinearLayout refuseCon;
    TextView startPlaceText;
    TextView endPlaceText;

    private TaxiOrder taxiOrder;

    private ActFraCommBridge bridge;

    private Fragment createFragment;

    public void setBridge(ActFraCommBridge bridge){
        this.bridge = bridge;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        taxiOrder = (TaxiOrder) args.getSerializable("taxiOrder");
    }

    @Override
    public int getLayoutResId() {
        return R.layout.zc_accept_fragment;
    }

    @Override
    public void finishCreateView(Bundle state) {
        initView();
    }

    private void initView() {
        acceptBtn = $(R.id.accept_btn);
        refuseCon = $(R.id.refuse_con);
        startPlaceText = $(R.id.start_place);
        endPlaceText = $(R.id.end_place);

        startPlaceText.setText(taxiOrder.getStartSite().address);
        endPlaceText.setText(taxiOrder.getEndSite().address);
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
