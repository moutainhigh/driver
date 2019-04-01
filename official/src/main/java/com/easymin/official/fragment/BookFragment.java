package com.easymin.official.fragment;

import android.os.Bundle;
import android.widget.TextView;

import com.easymi.component.base.RxBaseFragment;
import com.easymi.component.utils.TimeUtil;
import com.easymi.component.widget.CustomSlideToUnlockView;
import com.easymin.official.R;
import com.easymin.official.entity.GovOrder;
import com.easymin.official.flowMvp.ActFraCommBridge;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: BookFragment
 * @Author: hufeng
 * @Date: 2019/3/26 上午9:11
 * @Description:
 * @History:
 */
public class BookFragment  extends RxBaseFragment {


    CustomSlideToUnlockView slider;
    TextView tv_booktime;
    TextView start_place;
    TextView end_place;

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
        return R.layout.gw_fragment_book;
    }

    @Override
    public void finishCreateView(Bundle state) {
        findId();

        start_place.setSelected(true);
        end_place.setSelected(true);

        tv_booktime.setText(TimeUtil.getTime("yyyy-MM-dd HH:mm", govOrder.bookTime*1000));
        start_place.setText(govOrder.getStartSite().addr);
        end_place.setText(govOrder.getEndSite().addr);

        slider.setHint(getContext().getResources().getString(R.string.gw_slider_start));

        slider.setmCallBack(new CustomSlideToUnlockView.CallBack() {
            @Override
            public void onSlide(int distance) {

            }

            @Override
            public void onUnlocked() {
                bridge.doToStart();
            }
        });
    }

    public void findId(){
        slider = $(R.id.slider);
        tv_booktime = $(R.id.tv_booktime);
        start_place = $(R.id.start_place);
        end_place = $(R.id.end_place);
    }
}
