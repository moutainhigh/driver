package com.easymi.zhuanche.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easymi.component.base.RxBaseFragment;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.entity.Setting;
import com.easymi.component.widget.LoadingButton;
import com.easymi.zhuanche.R;
import com.easymi.zhuanche.flowMvp.ActFraCommBridge;
import com.easymi.zhuanche.flowMvp.FlowActivity;

/**
 * Created by developerLzh on 2017/11/13 0013.
 */

public class RunningFragment extends RxBaseFragment {
    private DymOrder zcOrder;

    private ActFraCommBridge bridge;

    public void setBridge(ActFraCommBridge bridge) {
        this.bridge = bridge;
    }

    LinearLayout feeCon;

    TextView serviceMoneyText;
    TextView distanceText;

    TextView driveTimeText;
    TextView waitTimeText;

    LoadingButton startWaitBtn;
    LinearLayout settleBtn;

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
        return R.layout.running_fragment;
    }

    @Override
    public void finishCreateView(Bundle state) {
        initView();
    }

    private void initView() {
        if (zcOrder == null) {
            zcOrder = new DymOrder();
        }
        serviceMoneyText = getActivity().findViewById(R.id.service_money);
        distanceText = getActivity().findViewById(R.id.distance);
        driveTimeText = getActivity().findViewById(R.id.drive_time);
        waitTimeText = getActivity().findViewById(R.id.wait_time);
        startWaitBtn = getActivity().findViewById(R.id.start_wait);
        settleBtn = getActivity().findViewById(R.id.settle);

        quanlanCon = getActivity().findViewById(R.id.quanlan_con);
        quanlanImg = getActivity().findViewById(R.id.quanlan_img);
        quanlanText = getActivity().findViewById(R.id.quanlan_text);

        refreshImg = getActivity().findViewById(R.id.ic_refresh);

        feeCon = getActivity().findViewById(R.id.fee_con);

        serviceMoneyText.setText(zcOrder.totalFee + "");
        distanceText.setText(zcOrder.distance + "");
        driveTimeText.setText(zcOrder.travelTime + "");
        waitTimeText.setText(zcOrder.waitTime + "");

        startWaitBtn.setOnClickListener(view -> bridge.doStartWait(startWaitBtn));
        settleBtn.setOnClickListener(view -> bridge.showSettleDialog());

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
            boolean canAddPrice = Setting.findOne().isAddPrice == 1;
            if (canAddPrice) {
                bridge.showCheating();
            }
            return true;
        });
    }

    public void showFee(DymOrder dymOrder) {
        this.zcOrder = dymOrder;
        serviceMoneyText.setText(zcOrder.totalFee + "");
        distanceText.setText(zcOrder.distance + "");
        driveTimeText.setText(zcOrder.travelTime + "");
        waitTimeText.setText(zcOrder.waitTime + "");
    }

    public void mapStatusChanged() {
        refreshImg.setVisibility(View.VISIBLE);
    }
}
