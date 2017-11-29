package com.easymi.daijia.flowMvp;

import com.amap.api.maps.model.LatLng;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.daijia.entity.DJOrder;
import com.easymi.daijia.result.DJOrderResult;

import rx.Observable;

/**
 * Created by liuzihao on 2017/11/15.
 */

public interface FlowContract {

    interface View {
        void initToolbar();

        void initPop();

        void showTopView();

        void showToPlace(String toPlace);

        void showLeftTime(String leftTime);

        void initBridge();

        void showBottomFragment(DJOrder djOrder);

        void showOrder(DJOrder djOrder);

        void initMap();

        void showMapBounds();

        void cancelSuc();

        void refuseSuc();

        RxManager getManager();
    }

    interface Presenter {
        void acceptOrder(Long orderId);

        void refuseOrder(Long orderId, String remark);

        void toStart(Long orderId);

        void arriveStart(Long orderId);

        void startWait(Long orderId);

        void startDrive(Long orderId);

        void arriveDes(DJOrder djOrder);

        void navi(LatLng latLng, String poi);

        void findOne(Long orderId);

        void changeEnd(Long orderId, Double lat, Double lng, String address);

        void cancelOrder(Long orderId, String remark);
        //...
    }

    interface Model {
        Observable<DJOrderResult> doAccept(Long orderId);

        Observable<DJOrderResult> findOne(Long orderId);

        Observable<DJOrderResult> refuseOrder(Long orderId, String remark);

        Observable<DJOrderResult> toStart(Long orderId);

        Observable<DJOrderResult> arriveStart(Long orderId);

        Observable<DJOrderResult> startWait(Long orderId);

        Observable<DJOrderResult> startDrive(Long orderId);

        Observable<DJOrderResult> arriveDes(DJOrder djOrder);

        Observable<DJOrderResult> changeEnd(Long orderId, Double lat, Double lng, String address);

        Observable<DJOrderResult> cancelOrder(Long orderId, String remark);
    }
}
