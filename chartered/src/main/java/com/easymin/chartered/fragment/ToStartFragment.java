package com.easymin.chartered.fragment;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.easymi.component.Config;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.base.RxBaseFragment;
import com.easymi.component.entity.BaseOrder;
import com.easymi.component.utils.GlideCircleTransform;
import com.easymi.component.utils.Log;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.widget.CustomSlideToUnlockView;
import com.easymi.component.widget.LoadingButton;
import com.easymin.chartered.R;
import com.easymin.chartered.flowMvp.ActFraCommBridge;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: ToStartFragment
 * Author: shine
 * Date: 2018/12/22 下午3:43
 * Description:
 * History:
 */
public class ToStartFragment extends RxBaseFragment {

    private BaseOrder baseOrder;

    private ActFraCommBridge bridge;

    public void setBridge(ActFraCommBridge bridge) {
        this.bridge = bridge;
    }

    ImageView customer_photo;
    TextView customer_name;
    TextView customer_phone;
    ImageView call_phone;
    TextView to_place;
    ImageView navi_view;
    CustomSlideToUnlockView slider;

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        baseOrder = (BaseOrder) args.getSerializable("baseOrder");
    }

    @Override
    public int getLayoutResId() {
        return R.layout.layout_to_start_fragment;
    }

    @Override
    public void finishCreateView(Bundle state) {
        initView();
    }

    private void initView() {
        customer_photo = $(R.id.customer_photo);
        customer_name = $(R.id.customer_name);
        customer_phone = $(R.id.customer_phone);
        call_phone = $(R.id.call_phone);
        to_place = $(R.id.to_place);
        navi_view = $(R.id.navi_view);
        slider = $(R.id.slider);

        //todo 差客户头像
        customer_name.setText(baseOrder.passengerName);

        if (StringUtils.isNotBlank(baseOrder.avatar)) {
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .transform(new GlideCircleTransform())
                    .placeholder(R.mipmap.photo_default)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(this)
                    .load(Config.IMG_SERVER + baseOrder.avatar + Config.IMG_PATH)
                    .apply(options)
                    .into(customer_photo);
        }

//        startPlaceText.setText(baseOrder.);
//        endPlaceText.setText(zcOrder.getEndSite().addr);
//        controlCon.setOnClickListener(view -> {
//            Log.e("tag", "onClick");
//            bridge.doToStart(controlCon);
//        });
//        callPhoneCon.setOnClickListener(view -> {
//            Log.e("tag", "onClick");
//            CallPhoneDialog.callDialog(getActivity(),zcOrder);
//        });
//        changEndCon.setOnClickListener(v -> bridge.changeEnd());
    }
}
