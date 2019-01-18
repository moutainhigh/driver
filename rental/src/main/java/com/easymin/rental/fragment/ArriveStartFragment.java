package com.easymin.rental.fragment;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.easymi.component.Config;
import com.easymi.component.base.RxBaseFragment;
import com.easymi.component.utils.GlideCircleTransform;
import com.easymi.component.utils.PhoneUtil;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.CustomSlideToUnlockView;
import com.easymin.rental.R;
import com.easymin.rental.entity.RentalOrder;
import com.easymin.rental.flowMvp.ActFraCommBridge;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: ArriveStartFragment
 *@Author: shine
 * Date: 2018/12/24 下午5:00
 * Description:
 * History:
 */
public class ArriveStartFragment extends RxBaseFragment {

    private RentalOrder baseOrder;

    private ActFraCommBridge bridge;

    /**
     * 设置bridge
     * @param bridge
     */
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
        baseOrder = (RentalOrder) args.getSerializable("baseOrder");
    }

    @Override
    public int getLayoutResId() {
        return R.layout.layout_arrive_start;
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

        customer_name.setText(baseOrder.passengerName);

        String weihao;
        if (baseOrder.passengerPhone != null && baseOrder.passengerPhone.length() > 4) {
            weihao = baseOrder.passengerPhone.substring(baseOrder.passengerPhone.length() - 4, baseOrder.passengerPhone.length());
        } else {
            weihao = baseOrder.passengerPhone;
        }
        customer_phone.setText(getContext().getString(R.string.custom_phone_four) + weihao);

        call_phone.setOnClickListener(v -> {
            PhoneUtil.call(getActivity(), baseOrder.passengerPhone);
        });

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


        to_place.setText(baseOrder.getEndSite().address);

        slider.setHint("滑动开始出发");
        slider.setmCallBack(new CustomSlideToUnlockView.CallBack() {
            @Override
            public void onSlide(int distance) {

            }

            @Override
            public void onUnlocked() {
                bridge.doStartDrive();
            }
        });

        navi_view.setOnClickListener(v -> {
            ToastUtil.showMessage(getContext(),"乘客上车出发后方可导航");
        });

    }
}
