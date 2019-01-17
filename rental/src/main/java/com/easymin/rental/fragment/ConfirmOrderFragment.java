package com.easymin.rental.fragment;

import android.os.Bundle;
import android.widget.TextView;

import com.easymi.component.base.RxBaseFragment;
import com.easymi.component.utils.TimeUtil;
import com.easymi.component.widget.LoadingButton;

import com.easymin.rental.R;
import com.easymin.rental.entity.RentalOrder;
import com.easymin.rental.flowMvp.ActFraCommBridge;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: ConfirmOrderFragment
 * Author: shine
 * Date: 2018/12/24 下午5:29
 * Description:
 * History:
 */
public class ConfirmOrderFragment extends RxBaseFragment {

    TextView tv_name;
    TextView tv_phone;
    TextView tv_time;
    TextView tv_start_place;
    TextView tv_end_place;
    TextView remark;
    LoadingButton complet_order;

    private RentalOrder baseOrder;

    private ActFraCommBridge bridge;

    /**
     * 设置bridge
     * @param bridge
     */
    public void setBridge(ActFraCommBridge bridge) {
        this.bridge = bridge;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        baseOrder = (RentalOrder) args.getSerializable("baseOrder");
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_confirm_order;
    }

    @Override
    public void finishCreateView(Bundle state) {
        findById();

        initData();
        complet_order.setOnClickListener(v -> {
            bridge.toFinish(complet_order);
        });
    }

    public void findById(){
        tv_name = getActivity().findViewById(R.id.tv_name);
        tv_phone = getActivity().findViewById(R.id.tv_phone);
        tv_time = getActivity().findViewById(R.id.tv_time);
        tv_start_place = getActivity().findViewById(R.id.tv_start_place);
        tv_end_place = getActivity().findViewById(R.id.tv_end_place);
        remark = getActivity().findViewById(R.id.remark);
        complet_order = getActivity().findViewById(R.id.complet_order);
    }

    public void initData(){
        tv_name.setText(baseOrder.passengerName);
        tv_phone.setText(baseOrder.passengerPhone);
        tv_time.setText(TimeUtil.getTime("yyyy年MM月dd日", baseOrder.bookTime*1000));
        tv_start_place.setText(baseOrder.getStartSite().address);
        tv_end_place.setText(baseOrder.getEndSite().address);

        remark.setText(baseOrder.orderRemark);
    }
}
