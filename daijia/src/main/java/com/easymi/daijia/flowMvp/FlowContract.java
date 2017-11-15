package com.easymi.daijia.flowMvp;

import com.easymi.daijia.entity.DJOrder;

import rx.Observable;

/**
 * Created by liuzihao on 2017/11/15.
 */

public interface FlowContract {

    interface View{
        void showTopView();

        void showToPlace(String toPlace);

        void showLeftTime(String leftTime);

        void initBridge();

        void showBottomFragment(DJOrder djOrder);
    }

    interface Presenter{
        void acceptOrder();
        void refuseOrder();
        void navi(DJOrder djOrder);
        //...
    }

    interface Model{
        Observable<Object> accept();
    }
}
