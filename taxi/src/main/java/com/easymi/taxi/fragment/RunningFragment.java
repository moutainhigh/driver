package com.easymi.taxi.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easymi.component.base.RxBaseFragment;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.entity.ZCSetting;
import com.easymi.component.widget.CustomSlideToUnlockView;
import com.easymi.component.widget.LoadingButton;
import com.easymi.taxi.R;
import com.easymi.taxi.flowMvp.ActFraCommBridge;
import com.easymi.taxi.flowMvp.FlowActivity;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: FinishActivity
 * Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class RunningFragment extends RxBaseFragment {
    private DymOrder zcOrder;

    private ActFraCommBridge bridge;

    /**
     * 设置bridge
     * @param bridge
     */
    public void setBridge(ActFraCommBridge bridge) {
        this.bridge = bridge;
    }

    LinearLayout feeCon;

    TextView serviceMoneyText;
    TextView distanceText;

    TextView driveTimeText;
    TextView waitTimeText;

//    LoadingButton startWaitBtn;
//    LinearLayout settleBtn;
    CustomSlideToUnlockView slideView;

    LinearLayout quanlanCon;
    ImageView quanlanImg;
    TextView quanlanText;

    ImageView refreshImg;



    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        zcOrder = (DymOrder) args.getSerializable("zcOrder");
    }

    @Override
    public int getLayoutResId() {
        return R.layout.taxi_running_fragment;
    }

    @Override
    public void finishCreateView(Bundle state) {
        initView();
    }

    private void initView() {
        if (zcOrder == null) {
            zcOrder = new DymOrder();
        }
        serviceMoneyText = $(R.id.service_money);
        distanceText = $(R.id.distance);
        driveTimeText = $(R.id.drive_time);
        waitTimeText = $(R.id.wait_time);
//        startWaitBtn = $(R.id.start_wait);
//        settleBtn = $(R.id.settle);
        slideView = $(R.id.slider);

        quanlanCon = $(R.id.quanlan_con);
        quanlanImg = $(R.id.quanlan_img);
        quanlanText = $(R.id.quanlan_text);

        refreshImg = $(R.id.ic_refresh);

        feeCon = $(R.id.fee_con);

        serviceMoneyText.setText(zcOrder.totalFee + "");
        distanceText.setText(zcOrder.distance + "");
        driveTimeText.setText(zcOrder.travelTime + "");
        waitTimeText.setText(zcOrder.waitTime + "");

//        startWaitBtn.setOnClickListener(view -> bridge.doStartWait(startWaitBtn));
//        settleBtn.setOnClickListener(view -> bridge.showSettleDialog());
        slideView.setmCallBack(new CustomSlideToUnlockView.CallBack() {
            @Override
            public void onSlide(int distance) {

            }

            @Override
            public void onUnlocked() {
                bridge.showSettleDialog();
                slideView.resetView();
            }
        });

        refreshImg.setOnClickListener(v -> {
            bridge.doRefresh();
            refreshImg.setVisibility(View.GONE);
            quanlanImg.setImageResource(R.drawable.ic_quan_lan_normal);
            quanlanText.setTextColor(getResources().getColor(R.color.text_default));
        });

        quanlanCon.setOnClickListener(v -> {
            if (FlowActivity.isMapTouched) {
                quanlanImg.setImageResource(R.drawable.ic_quan_lan_normal);
                quanlanText.setTextColor(getResources().getColor(R.color.text_default));
                refreshImg.setVisibility(View.GONE);
                bridge.doRefresh();
            } else {
                FlowActivity.isMapTouched = true;
                refreshImg.setVisibility(View.VISIBLE);
                quanlanImg.setImageResource(R.drawable.ic_quan_lan_pressed);
                quanlanText.setTextColor(getResources().getColor(R.color.color_50b8da));
                bridge.doQuanlan();
            }
        });

        getSupportActivity().findViewById(R.id.change_end_con).setOnClickListener(view -> bridge.changeEnd());

        feeCon.setOnLongClickListener(view -> {
            boolean canAddPrice = ZCSetting.findOne().isAddPrice == 1;
            if (canAddPrice) {
                bridge.showCheating();
            }
            return true;
        });
    }

    public void showFee(DymOrder dymOrder) {
        getActivity().runOnUiThread(() -> {
            RunningFragment.this.zcOrder = dymOrder;
            serviceMoneyText.setText(zcOrder.totalFee + "");
            distanceText.setText(zcOrder.distance + "");
            driveTimeText.setText(zcOrder.travelTime + "");
            waitTimeText.setText(zcOrder.waitTime + "");    });

    }

    public void mapStatusChanged() {
        refreshImg.setVisibility(View.VISIBLE);
    }
}
