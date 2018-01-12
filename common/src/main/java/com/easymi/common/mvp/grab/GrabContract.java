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

        RxManager getManager();
    }

    interface Presenter {
        void queryOrder(Long orderId);

        void grabOrder(Long orderId);

        void takeOrder(Long orderId);

        void routePlanByRouteSearch(LatLonPoint endPoint, List<LatLonPoint> pass);
    }

    interface Model {
        Observable<MultipleOrderResult> queryOrder(Long orderId);

        Observable<MultipleOrderResult> grabOrder(Long orderId);

        Observable<MultipleOrderResult> takeOrder(Long orderId);
    }

}
