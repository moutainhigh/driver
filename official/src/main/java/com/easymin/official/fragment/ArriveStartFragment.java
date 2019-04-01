package com.easymin.official.fragment;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.easymi.component.base.RxBaseFragment;
import com.easymi.component.utils.PhoneUtil;
import com.easymi.component.widget.CustomSlideToUnlockView;
import com.easymin.official.R;
import com.easymin.official.entity.GovOrder;
import com.easymin.official.flowMvp.ActFraCommBridge;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: ArriveStartFragment
 * @Author: hufeng
 * @Date: 2019/3/26 上午10:21
 * @Description:
 * @History:
 */
public class ArriveStartFragment extends RxBaseFragment{

    CustomSlideToUnlockView slider;
    TextView tv_phone;
    TextView tv_custom_name;
    TextView start_place;
    TextView end_place;
    ImageView call_phone_con;

    /**
     * 订单信息
     */
    private GovOrder govOrder;

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

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        govOrder = (GovOrder) args.getSerializable("govOrder");
    }


    @Override
    public int getLayoutResId() {
        return R.layout.gw_fragment_arrive;
    }

    @Override
    public void finishCreateView(Bundle state) {
        findId();


        String weihao;
        if (govOrder.passengerPhone != null && govOrder.passengerPhone.length() > 4) {
            weihao = govOrder.passengerPhone.substring(govOrder.passengerPhone.length() - 4, govOrder.passengerPhone.length());
        } else {
            weihao = govOrder.passengerPhone;
        }
        tv_phone.setText(weihao+"");

        start_place.setSelected(true);
        end_place.setSelected(true);

        slider.setHint("滑动确认上车");

        start_place.setText(govOrder.getStartSite().addr);
        end_place.setText(govOrder.getEndSite().addr);

        call_phone_con.setOnClickListener(view -> PhoneUtil.call(getActivity(), govOrder.passengerPhone));

        slider.setHint(getContext().getResources().getString(R.string.huadongdaoda));

        slider.setmCallBack(new CustomSlideToUnlockView.CallBack() {
            @Override
            public void onSlide(int distance) {

            }

            @Override
            public void onUnlocked() {
                bridge.doArriveStart();
            }
        });
    }

    public void findId(){
        slider = $(R.id.slider);
        tv_phone = $(R.id.tv_phone);
        tv_custom_name = $(R.id.tv_custom_name);
        start_place = $(R.id.start_place);
        end_place = $(R.id.end_place);
        call_phone_con = $(R.id.call_phone_con);
    }
}
