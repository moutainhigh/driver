package com.easymin.official.fragment;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.easymi.component.base.RxBaseFragment;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.widget.CustomSlideToUnlockView;
import com.easymin.official.R;
import com.easymin.official.entity.GovOrder;
import com.easymin.official.flowMvp.ActFraCommBridge;

import java.text.DecimalFormat;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: RunningFragment
 * @Author: hufeng
 * @Date: 2019/3/26 上午10:37
 * @Description:
 * @History:
 */
public class RunningFragment  extends RxBaseFragment {

    CustomSlideToUnlockView slider;
    TextView service_money;
    TextView distance;

    /**
     * 订单信息
     */
    private GovOrder govOrder;

    /**
     * 动态数据
     */
    private DymOrder dymOrder;

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

    DecimalFormat df = new DecimalFormat("#0.00");

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        govOrder = (GovOrder) args.getSerializable("govOrder");
        dymOrder = DymOrder.findByIDType(govOrder.orderId, govOrder.orderType);
        if (null == dymOrder) {
            if (govOrder.orderFee != null) {
                dymOrder = govOrder.orderFee;
            }
        }
    }

    @Override
    public int getLayoutResId() {
        return R.layout.gw_fragment_running;
    }

    @Override
    public void finishCreateView(Bundle state) {
        findId();

        slider.setHint(getContext().getResources().getString(R.string.slider_destance));
        slider.setmCallBack(new CustomSlideToUnlockView.CallBack() {
            @Override
            public void onSlide(int distance) {

            }

            @Override
            public void onUnlocked() {
                bridge.arriveDes(dymOrder);
            }
        });

        service_money.setText(df.format(dymOrder.totalFee));
        distance.setText(dymOrder.distance + "");
    }

    public void findId(){
        slider = $(R.id.slider);
        service_money = $(R.id.service_money);
        distance = $(R.id.distance);
    }

    public void showFee(DymOrder dymOrder) {
        getActivity().runOnUiThread(() -> {
            RunningFragment.this.dymOrder = dymOrder;

            service_money.setText(df.format(dymOrder.totalFee));
            distance.setText(dymOrder.distance + "");
        });
    }
    
}