package com.easymin.passengerbus.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easymi.component.base.RxBaseFragment;
import com.easymi.component.widget.CustomSlideToUnlockView;
import com.easymin.passengerbus.R;
import com.easymin.passengerbus.entity.BusStationResult;
import com.easymin.passengerbus.flowMvp.ActFraCommBridge;
import com.easymin.passengerbus.flowMvp.BcFlowActivity;

/**
 * 行程中
 */
public class BcRuningFragment extends RxBaseFragment{

    private TextView tvLineAddress;
    private TextView tvTip;
    private LinearLayout lineLayout;
    private TextView tvWaiteTime;
    private CustomSlideToUnlockView slider;
    private LinearLayout controlCon;
    private ActFraCommBridge bridge;
    private BusStationResult result;

    /**
     * 当前状态
     */
    private String curStr;
    int index;

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        result = (BusStationResult) args.getSerializable("busLineResult");

    }

    public void setBridge(ActFraCommBridge bridge) {
        this.bridge = bridge;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.flow_status_two_step_layout;
    }

    @Override
    public void finishCreateView(Bundle state) {

        initView();

        if (curStr.equals("滑动前往下一站")) {

            bridge.changeToolbar(BcFlowActivity.RUNNING);

        } else if (curStr.equals("滑动到达站点")) {

            bridge.changeToolbar(BcFlowActivity.ENDRUNING);

        }

    }

    private void initView() {
        tvLineAddress =  $(R.id.tv_line_address);
        tvTip =  $(R.id.tv_tip);
        lineLayout =  $(R.id.line_layout);
        tvWaiteTime =  $(R.id.tv_waite_time);
        slider =  $(R.id.slider);
        controlCon =  $(R.id.control_con);

        if (result == null) {
            return;
        }

        slider.setOnClickListener(v -> {

//            for (int i = 1; i <= result.stationVos.size()-1; i++) {
//
//                if (curStr.equals("滑动前往下一站")) {
//                    bridge.slideToNext(result.stationVos.get(i).id);
//
//                } else if (curStr.equals("滑动到达站点")) {
//                    bridge.sideToArrived(result.stationVos.get(i).id);
//                }
//            }

        });



        curStr = slider.setHint2("滑动前往下一站");
        slider.setmCallBack(new CustomSlideToUnlockView.CallBack() {
            @Override
            public void onSlide(int distance) {

            }

            @Override
            public void onUnlocked() {

                resetView();
                index = result.stationVos.get(0).id;
                int total = result.stationVos.get(result.stationVos.size()-1).id;

                if (index < total){
                    index = index + 1;
                }
                if (curStr.equals("滑动前往下一站")) {
                    bridge.slideToNext(index);

                } else if (curStr.equals("滑动到达站点")) {
                    bridge.sideToArrived(index);
                }
            }
        });

    }

    Handler handler = new Handler();

    private void resetView() {
        if (curStr.equals("滑动前往下一站")) {
            slider.setHint("滑动到达站点");
            curStr = slider.setHint2("滑动到达站点");


        } else if (curStr.equals("滑动到达站点")) {
            slider.setHint("滑动前往下一站");
            curStr = slider.setHint2("滑动前往下一站");

        }

        handler.postDelayed(() -> getActivity().runOnUiThread(() -> {
            slider.resetView();
            slider.setVisibility(View.VISIBLE);
        }), 1000);
        //防止卡顿
    }
}
