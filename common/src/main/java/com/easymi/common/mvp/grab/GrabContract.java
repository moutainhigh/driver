package com.easymi.common.mvp.grab;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.DriveRouteResult;
import com.easymi.common.entity.MultipleOrder;
import com.easymi.common.result.MultipleOrderResult;
import com.easymi.component.rxmvp.RxManager;

import java.util.List;

import rx.Observable;

/**
 * Created by developerLzh on 2017/11/22 0022.
 */

public interface GrabContract {

    interface View {
        void showBase(MultipleOrder multipleOrder);

        void showShade();

        void showGrabCountDown();

        void initViewPager();

        void finishActivity();

        void showPath(DriveRouteResult result);

        void showStartMarker(LatLonPoint start);

        void showPassMarker(List<LatLonPoint> pass);

        void showEndMarker(LatLonPoint end);

        void removeAllOrderMarker();

        void removerOrderById(long orderId);

        RxManager getManager();
    }

    interface Presenter {
        void queryOrder(MultipleOrder order);

        void grabOrder(MultipleOrder order);

        void takeOrder(MultipleOrder order);

        void routePlanByRouteSearch(LatLonPoint endPoint, List<LatLonPoint> pass);
    }

    interface Model {
        Observable<MultipleOrderResult> queryDJOrder(Long orderId);

        Observable<MultipleOrderResult> grabDJOrder(Long orderId);

        Observable<MultipleOrderResult> takeDJOrder(Long orderId);

        Observable<MultipleOrderResult> queryZCOrder(Long orderId);

        Observable<MultipleOrderResult> grabZCOrder(Long orderId,Long version);

        Observable<MultipleOrderResult> takeZCOrder(Long orderId,Long version);

        Observable<MultipleOrderResult> queryTaxiOrder(Long orderId);

        Observable<MultipleOrderResult> grabTaxiOrder(Long orderId,Long version);

        Observable<MultipleOrderResult> takeTaxiOrder(Long orderId,Long version);


    }

}
