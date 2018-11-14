package com.easymi.taxi.flowMvp;

import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.services.route.DriveRouteResult;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.result.EmResult;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.widget.LoadingButton;
import com.easymi.taxi.entity.ConsumerInfo;
import com.easymi.taxi.entity.TaxiOrder;
import com.easymi.taxi.result.ConsumerResult;
import com.easymi.taxi.result.TaxiOrderResult;

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

        void showBottomFragment(TaxiOrder taxiOrder);

        void showOrder(TaxiOrder taxiOrder);

        void initMap();

        void showMapBounds();

        void cancelSuc();

        void refuseSuc();

        void showPath(int[] ints, AMapNaviPath path);

        void showPath(DriveRouteResult result);

        void showPayType(double money, ConsumerInfo consumerInfo);

        void paySuc();

        void showLeft(int dis, int time);

        void showReCal();//重新规划路径开始

        void showToEndFragment();

        void showConsumer(ConsumerInfo consumerInfo);

        void hideTops();

        RxManager getManager();
    }

    interface Presenter {
        void acceptOrder(Long orderId, LoadingButton btn);

        void refuseOrder(Long orderId, String remark);

        void toStart(Long orderId, LoadingButton btn);

        void arriveStart(Long orderId);

        void startWait(Long orderId, LoadingButton btn);

        void startWait(Long orderId);

        void startDrive(Long orderId, LoadingButton btn);

        void arriveDes(TaxiOrder taxiOrder, LoadingButton btn, DymOrder dymOrder);

        void navi(LatLng latLng, String poi, Long orderId);

        void findOne(Long orderId);

        void findOne(Long orderId, boolean needShowProgress);

        void changeEnd(Long orderId, Double lat, Double lng, String address);

        void cancelOrder(Long orderId, String remark);

        void routePlanByNavi(Double endLat, Double endLng);

        void routePlanByRouteSearch(Double endLat, Double endLng);

        void updateDymOrder(TaxiOrder taxiOrder);

        void payOrder(Long orderId, String payType);

        void stopNavi();

        void getConsumerInfo(Long orderId);

        TaxiOrderResult orderResult2ZCOrder(TaxiOrderResult result);
        //...

        void changeOrderStatus(Long companyId,String detailAddress, Long driverId, Double latitude,
                               Double longitude,Long orderId,int status, LoadingButton btn);

    }

    interface Model {
        Observable<TaxiOrderResult> doAccept(Long orderId);

        Observable<TaxiOrderResult> findOne(Long orderId);

        Observable<EmResult> refuseOrder(Long orderId, String remark);

        Observable<TaxiOrderResult> toStart(Long orderId);

        Observable<TaxiOrderResult> arriveStart(Long orderId);

        Observable<TaxiOrderResult> startWait(Long orderId);

        Observable<TaxiOrderResult> startDrive(Long orderId);

        Observable<TaxiOrderResult> arriveDes(TaxiOrder taxiOrder, DymOrder dymOrder);

        Observable<TaxiOrderResult> changeEnd(Long orderId, Double lat, Double lng, String address);

        Observable<EmResult> cancelOrder(Long orderId, String remark);

        Observable<ConsumerResult> consumerInfo(Long orderId);

        Observable<EmResult> payOrder(Long orderId, String payType);

        Observable<EmResult> changeOrderStatus(Long companyId,String detailAddress, Long driverId, Double latitude,
                                               Double longitude,Long orderId,int status);

    }
}
