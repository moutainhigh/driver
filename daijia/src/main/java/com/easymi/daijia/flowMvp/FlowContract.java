package com.easymi.daijia.flowMvp;

import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.NaviPath;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.result.EmResult;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.widget.LoadingButton;
import com.easymi.daijia.entity.DJOrder;
import com.easymi.daijia.result.DJOrderResult;

import java.util.List;

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

        void showPath(int[] ints, AMapNaviPath path);

        void showPath(DriveRouteResult result);

        void showPayType();

        void paySuc();

        void showLeft(int dis,int time);

        void showReCal();//重新规划路径开始

        RxManager getManager();
    }

    interface Presenter {
        void acceptOrder(Long orderId, LoadingButton btn);

        void refuseOrder(Long orderId, String remark);

        void toStart(Long orderId, LoadingButton btn);

        void arriveStart(Long orderId);

        void startWait(Long orderId, LoadingButton btn);

        void startDrive(Long orderId, LoadingButton btn);

        void arriveDes(LoadingButton btn, DymOrder dymOrder);

        void navi(LatLng latLng, String poi,Long orderId);

        void findOne(Long orderId);

        void changeEnd(Long orderId, Double lat, Double lng, String address);

        void cancelOrder(Long orderId, String remark);

        void routePlanByNavi(Double endLat, Double endLng);

        void routePlanByRouteSearch(Double endLat, Double endLng);

        void updateDymOrder(DJOrder djOrder);

        void payOrder(Long orderId, String payType);

        void onDestory();
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

        Observable<DJOrderResult> arriveDes(DymOrder dymOrder);

        Observable<DJOrderResult> changeEnd(Long orderId, Double lat, Double lng, String address);

        Observable<DJOrderResult> cancelOrder(Long orderId, String remark);

        Observable<EmResult> payOrder(Long orderId, String payType);
    }
}
