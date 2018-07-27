package com.easymi.daijia.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easymi.component.base.RxBaseFragment;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.entity.Setting;
import com.easymi.component.widget.LoadingButton;
import com.easymi.daijia.R;
import com.easymi.daijia.flowMvp.ActFraCommBridge;
import com.easymi.daijia.flowMvp.FlowActivity;

/**
 * Created by developerLzh on 2017/11/13 0013.
 */

public class RunningFragment extends RxBaseFragment {
    private DymOrder djOrder;

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
        djOrder = (DymOrder) args.getSerializable("djOrder");
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
        if (djOrder == null) {
            djOrder = new DymOrder();
        }
        serviceMoneyText = $(R.id.service_money);
        distanceText = $(R.id.distance);
        driveTimeText = $(R.id.drive_time);
        waitTimeText = $(R.id.wait_time);
        startWaitBtn = $(R.id.start_wait);
        settleBtn = $(R.id.settle);

        quanlanCon = $(R.id.quanlan_con);
        quanlanImg = $(R.id.quanlan_img);
        quanlanText = $(R.id.quanlan_text);

        refreshImg = $(R.id.ic_refresh);

        feeCon = $(R.id.fee_con);

        serviceMoneyText.setText(djOrder.totalFee + "");
        distanceText.setText(djOrder.distance + "");
        driveTimeText.setText(djOrder.travelTime + "");
        waitTimeText.setText(djOrder.waitTime + "");

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
        this.djOrder = dymOrder;
        serviceMoneyText.setText(djOrder.totalFee + "");
        distanceText.setText(djOrder.distance + "");
        driveTimeText.setText(djOrder.travelTime + "");
        waitTimeText.setText(djOrder.waitTime + "");
    }

    public void mapStatusChanged() {
        refreshImg.setVisibility(View.VISIBLE);
    }
}
