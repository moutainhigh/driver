package com.easymi.daijia.activity.grab;

import com.easymi.component.rxmvp.RxManager;
import com.easymi.daijia.entity.DJOrder;
import com.easymi.daijia.result.DJOrderResult;

import rx.Observable;

/**
 * Created by developerLzh on 2017/11/22 0022.
 */

public interface GrabContract {

    interface View {
        void showBase(DJOrder djOrder);
        void showShade();
        void showGrabCountDown();
        void finishActivity();
        RxManager getManager();
    }

    interface Presenter {
        void queryOrder(Long orderId);

        void grabOrder(Long orderId);
    }

    interface Model {
        Observable<DJOrderResult> queryOrder(Long orderId);

        Observable<DJOrderResult> grabOrder(Long orderId);
    }

}
