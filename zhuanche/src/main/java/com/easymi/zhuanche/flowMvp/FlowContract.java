package com.easymi.zhuanche.flowMvp;

import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.services.route.DriveRouteResult;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.result.EmResult;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.widget.LoadingButton;
import com.easymi.zhuanche.entity.ConsumerInfo;
import com.easymi.zhuanche.entity.ZCOrder;
import com.easymi.zhuanche.result.ConsumerResult;
import com.easymi.zhuanche.result.ZCOrderResult;

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

        void showBottomFragment(ZCOrder zcOrder);

        void showOrder(ZCOrder zcOrder);

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

//        ZCOrder getOrder();

        RxManager getManager();
    }

    interface Presenter {
        void acceptOrder(Long orderId, Long version,LoadingButton btn);

        void refuseOrder(Long orderId, String remark);

        void toStart(Long orderId, LoadingButton btn);

        void arriveStart(Long orderId);

        void startWait(Long orderId, LoadingButton btn);

        void startWait(Long orderId);

        void startDrive(Long orderId, LoadingButton btn);

        void arriveDes(ZCOrder zcOrder, LoadingButton btn, DymOrder dymOrder);

        void navi(LatLng latLng, String poi, Long orderId);

        void findOne(Long orderId);

        void findOne(Long orderId, boolean needShowProgress);

        void changeEnd(Long orderId, Double lat, Double lng, String address);

        void cancelOrder(Long orderId, String remark);

        void routePlanByNavi(Double endLat, Double endLng);

        void routePlanByRouteSearch(Double endLat, Double endLng);

        void updateDymOrder(ZCOrder zcOrder);

        void payOrder(Long orderId, String payType);

        void stopNavi();

        void getConsumerInfo(Long orderId);

        ZCOrderResult orderResult2ZCOrder(ZCOrderResult result);
        //...
    }

    interface Model {
        Observable<ZCOrderResult> doAccept(Long orderId,Long version);

        Observable<ZCOrderResult> findOne(Long orderId);

        Observable<EmResult> refuseOrder(Long orderId, String remark);

        Observable<ZCOrderResult> toStart(Long orderId);

        Observable<ZCOrderResult> arriveStart(Long orderId);

        Observable<ZCOrderResult> startWait(Long orderId);

        Observable<ZCOrderResult> startDrive(Long orderId);

        Observable<ZCOrderResult> arriveDes(ZCOrder zcOrder,DymOrder dymOrder);

        Observable<ZCOrderResult> changeEnd(Long orderId, Double lat, Double lng, String address);

        Observable<EmResult> cancelOrder(Long orderId, String remark);

        Observable<ConsumerResult> consumerInfo(Long orderId);

        Observable<EmResult> payOrder(Long orderId, String payType);
    }
}
