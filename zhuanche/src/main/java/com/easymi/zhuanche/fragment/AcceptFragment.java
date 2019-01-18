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
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: AcceptFragment
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */
public class AcceptFragment extends RxBaseFragment {

    LoadingButton acceptBtn;
    LinearLayout refuseCon;
    TextView startPlaceText;
    TextView endPlaceText;

    /**
     * 订单信息
     */
    private ZCOrder zcOrder;

    /**
     * activity和fragment的通信接口
     */
    private ActFraCommBridge bridge;

    /**
     * 设置bridge
     * @param bridge
     */
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
        return R.layout.zc_accept_fragment;
    }

    @Override
    public void finishCreateView(Bundle state) {
        initView();
    }

    /**
     * 初始化布局
     */
    private void initView() {
        acceptBtn = $(R.id.accept_btn);
        refuseCon = $(R.id.refuse_con);
        startPlaceText = $(R.id.start_place);
        endPlaceText = $(R.id.end_place);

        startPlaceText.setSelected(true);
        endPlaceText.setSelected(true);

        startPlaceText.setText(zcOrder.getStartSite().addr);
        endPlaceText.setText(zcOrder.getEndSite().addr);
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
