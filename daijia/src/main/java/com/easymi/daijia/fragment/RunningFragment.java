package com.easymi.daijia.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easymi.component.base.RxBaseFragment;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.utils.PhoneUtil;
import com.easymi.component.widget.LoadingButton;
import com.easymi.daijia.R;
import com.easymi.daijia.entity.DJOrder;
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
                quanlanText.setTextColor(getResources().getColor(R.color.colorAccent));
                bridge.doQuanlan();
            }
        });

        getSupportActivity().findViewById(R.id.change_end_con).setOnClickListener(view -> bridge.changeEnd());

        feeCon.setOnLongClickListener(view -> {
            bridge.showCheating();
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
