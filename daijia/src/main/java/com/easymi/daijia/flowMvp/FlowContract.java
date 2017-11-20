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

    interface View{
        void showTopView();

        void showToPlace(String toPlace);

        void showLeftTime(String leftTime);

        void initBridge();

        void showBottomFragment(DJOrder djOrder);

        void showOrder(DJOrder djOrder);

        void initMap();

        void showMapBounds();

        RxManager getManager();
    }

    interface Presenter{
        void acceptOrder(Long orderId);
        void refuseOrder(Long orderId);
        void toStart(Long orderId);
        void arriveStart(Long orderId);
        void startWait(Long orderId);
        void startDrive(Long orderId);
        void arriveDes(Long orderId);

        void navi(LatLng latLng,String poi);
        void findOne(Long orderId);
        //...
    }

    interface Model{
        Observable<DJOrderResult> doAccept(Long orderId);
        Observable<DJOrderResult> findOne(Long orderId);

        Observable<DJOrderResult> refuseOrder(Long orderId);
        Observable<DJOrderResult> toStart(Long orderId);
        Observable<DJOrderResult> arriveStart(Long orderId);
        Observable<DJOrderResult> startWait(Long orderId);
        Observable<DJOrderResult> startDrive(Long orderId);
        Observable<DJOrderResult> arriveDes(Long orderId);
    }
}
