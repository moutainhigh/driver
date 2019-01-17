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
 * FileName: FinishActivity
 * Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class SlideArriveStartFragment extends RxBaseFragment {
    private ZCOrder zcOrder;

    private ActFraCommBridge bridge;

    /**
     * 设置bridge
     * @param bridge
     */
    public void setBridge(ActFraCommBridge bridge){
        this.bridge = bridge;
    }

    LoadingButton slideView;
    TextView startPlaceText;
    TextView endPlaceText;
    LinearLayout changeEndCon;
    FrameLayout callPhoneCon;

    ImageView customHead;
    TextView customName;

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

    private void initView() {
        startPlaceText = $(R.id.start_place);
        endPlaceText = $(R.id.end_place);
        slideView = $(R.id.slider);
        changeEndCon = $(R.id.change_end_con);
        callPhoneCon = $(R.id.call_phone_con);

        customHead = $(R.id.iv_head);
        customName = $(R.id.tv_custom_name);

        startPlaceText.setSelected(true);
        endPlaceText.setSelected(true);


        customName.setText(zcOrder.passengerName);
        if (StringUtils.isNotBlank(zcOrder.avatar)) {
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .transform(new GlideCircleTransform())
                    .placeholder(R.mipmap.ic_customer_head)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(this)
                    .load(Config.IMG_SERVER + zcOrder.avatar + Config.IMG_PATH)
                    .apply(options)
                    .into(customHead);
        }

        startPlaceText.setText(zcOrder.getStartSite().addr);
        endPlaceText.setText(zcOrder.getEndSite().addr);
//        slideView.setmCallBack(new CustomSlideToUnlockView.CallBack() {
//            @Override
//            public void onSlide(int distance) {
//
//            }
//
//            @Override
//            public void onUnlocked() {
//                bridge.doArriveStart();
//            }
//        });
        slideView.setOnClickListener(v -> {
            bridge.doArriveStart();
        });
        callPhoneCon.setOnClickListener(view -> CallPhoneDialog.callDialog(getActivity(),zcOrder));
        changeEndCon.setOnClickListener(view -> bridge.changeEnd());
    }
}
