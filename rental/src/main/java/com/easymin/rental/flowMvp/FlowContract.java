package com.easymin.rental.flowMvp;
import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.services.route.DriveRouteResult;
import com.easymi.component.entity.BaseOrder;
import com.easymi.component.result.EmResult;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.widget.LoadingButton;
import com.easymin.rental.entity.RentalOrder;
import com.easymin.rental.result.OrderResult;

import java.util.List;

import rx.Observable;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: FlowContract
 *@Author: shine
 * Date: 2018/12/18 下午1:55
 * Description:
 * History:
 */
public interface FlowContract {

    interface View {

        void initMap();

        void initBridget();

        void boundsZoom(List<LatLng> latLngs);

        void locZoom(int level);

        void showPath(int[] ints, AMapNaviPath path);

        void showPath(DriveRouteResult result);

        void showLeft(int dis, int time);

        void showOrder(RentalOrder order);

        void showBottomFragment(BaseOrder baseOrder);

        void showMapBounds();

        void toFinish();

        RxManager getManager();
    }

    interface Presenter {
        void routePlanByNavi(Double endLat, Double endLng);

        void routePlanByRouteSearch(LatLng start, List<LatLng> latLngs, LatLng end);

        void navi(LatLng latLng, Long orderId);

        void stopNavi();

        void findOne(long orderId);

        void changeStauts(Long orderId, int status);

        void orderConfirm(long orderId, long version, LoadingButton button);
    }

    interface Model {
        Observable<OrderResult> findOne(long orderId);

        Observable<EmResult> changeStauts(Long orderId, int status);

        Observable<EmResult> orderConfirm(long orderId, long version);
    }
}
