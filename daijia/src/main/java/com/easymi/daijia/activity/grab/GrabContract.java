package com.easymi.daijia.activity.grab;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.DriveRouteResult;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.daijia.entity.DJOrder;
import com.easymi.daijia.result.DJOrderResult;

import java.util.List;

import rx.Observable;

/**
 * Created by developerLzh on 2017/11/22 0022.
 */

public interface GrabContract {

    interface View {
        void showBase(DJOrder djOrder);
        void showShade();
        void showGrabCountDown();
        void initViewPager();
        void finishActivity();
        void showPath(DriveRouteResult result);
        RxManager getManager();
    }

    interface Presenter {
        void queryOrder(Long orderId);

        void grabOrder(Long orderId);

        void routePlanByRouteSearch(LatLonPoint endPoint, List<LatLonPoint> pass);
    }

    interface Model {
        Observable<DJOrderResult> queryOrder(Long orderId);

        Observable<DJOrderResult> grabOrder(Long orderId);
    }

}
