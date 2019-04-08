package com.easymi.zhuanche.fragment;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.easymi.component.Config;
import com.easymi.component.base.RxBaseFragment;
import com.easymi.component.utils.GlideCircleTransform;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.widget.CustomSlideToUnlockView;
import com.easymi.component.widget.LoadingButton;
import com.easymi.zhuanche.R;
import com.easymi.zhuanche.entity.ZCOrder;
import com.easymi.zhuanche.flowMvp.ActFraCommBridge;
import com.easymi.zhuanche.widget.CallPhoneDialog;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: SlideArriveStartFragment
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description: 滑动到达预约地
 * History:
 */

public class SlideArriveStartFragment extends RxBaseFragment {
    /**
     * 专车订单
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

    CustomSlideToUnlockView slideView;
    TextView startPlaceText;
    TextView endPlaceText;
    LinearLayout changeEndCon;
    ImageView callPhoneCon;
    TextView tv_phone;

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        zcOrder = (ZCOrder) args.getSerializable("zcOrder");
    }

    @Override
    public int getLayoutResId() {
        return R.layout.zc_slide_arrive_start_fragment;
    }

    @Override
    public void finishCreateView(Bundle state) {
        initView();
    }

    /**
     * 初始化布局
     */
    private void initView() {
        startPlaceText = $(R.id.start_place);
        endPlaceText = $(R.id.end_place);
        slideView = $(R.id.slider);
        changeEndCon = $(R.id.change_end_con);
        callPhoneCon = $(R.id.call_phone_con);

        tv_phone = $(R.id.tv_phone);

        String weihao;
        if (zcOrder.passengerPhone != null && zcOrder.passengerPhone.length() > 4) {
            weihao = zcOrder.passengerPhone.substring(zcOrder.passengerPhone.length() - 4, zcOrder.passengerPhone.length());
        } else {
            weihao = zcOrder.passengerPhone;
        }
        tv_phone.setText(weihao+"");

        startPlaceText.setSelected(true);
        endPlaceText.setSelected(true);

        startPlaceText.setText(zcOrder.getStartSite().addr);
        endPlaceText.setText(zcOrder.getEndSite().addr);

        slideView.setHint("滑动到达预约地");
        slideView.setmCallBack(new CustomSlideToUnlockView.CallBack() {
            @Override
            public void onSlide(int distance) {

            }

            @Override
            public void onUnlocked() {
                bridge.doArriveStart();
            }
        });

        callPhoneCon.setOnClickListener(view -> CallPhoneDialog.callDialog(getActivity(),zcOrder));
        changeEndCon.setOnClickListener(view -> bridge.changeEnd());
    }
}
