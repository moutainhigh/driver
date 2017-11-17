package com.easymi.daijia.flowMvp;

import com.easymi.component.rxmvp.RxManager;
import com.easymi.daijia.entity.DJOrder;
import com.easymi.daijia.result.DJOrderResult;

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

        void showOrder(DJOrder djOrder);

        RxManager getManager();
    }

    interface Presenter{
        void acceptOrder(Long orderId);
        void refuseOrder(Long orderId);
        void navi(DJOrder djOrder);
        void findOne(Long orderId);
        //...
    }

    interface Model{
        Observable<DJOrderResult> doAccept(Long orderId);
        Observable<DJOrderResult> findOne(Long orderId);
    }
}
