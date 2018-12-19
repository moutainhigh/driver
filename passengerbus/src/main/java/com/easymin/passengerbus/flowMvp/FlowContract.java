//package com.easymin.passengerbus.flowMvp;
//
//import com.amap.api.maps.model.LatLng;
//import com.amap.api.navi.model.AMapNaviPath;
//import com.amap.api.services.route.DriveRouteResult;
//import com.easymi.common.entity.OrderCustomer;
//import com.easymi.component.result.EmResult2;
//import com.easymi.component.rxmvp.RxManager;
//
//import java.util.List;
//
//import rx.Observable;
//
///**
// * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
// * FileName: FlowContract
// * Author: shine
// * Date: 2018/12/18 下午1:55
// * Description:
// * History:
// */
//public interface FlowContract {
//
//    interface View {
//        void initFragment();
//
//        void initMap();
//
//        void initBridget();
//
//        void addMarker(LatLng latLng, int flag);
//
//        void addMarker(LatLng latLng, int flag, int num);
//
//        void boundsZoom(List<LatLng> latLngs);
//
//        void locZoom(int level);
//
//        void showPath(int[] ints, AMapNaviPath path);
//
//        void showPath(DriveRouteResult result);
//
//        void showLeft(int dis, int time);
//
//        void arriveStartSuc(OrderCustomer orderCustomer);
//
//        void acceptCustomerSuc(OrderCustomer orderCustomer);
//
//        void jumpAcceptSuc(OrderCustomer orderCustomer);
//
//        void arriveEndSuc(OrderCustomer orderCustomer);
//
//        void jumpSendSuc(OrderCustomer orderCustomer);
//
//        void showFragmentByStatus();
//
//        void changeToolbar(int flag);
//
//        void startOutSuc();
//        void startSendSuc();
//        void finishTaskSuc();
//
//        RxManager getManager();
//    }
//
//    interface Presenter {
//        void routeLineByNavi(LatLng start, List<LatLng> latLngs, LatLng end);
//
//        void routePlanByRouteSearch(LatLng start, List<LatLng> latLngs, LatLng end);
//
//        void stopNavi();
//
//        void startOutSet(long orderId);
//
//        void arriveStart(OrderCustomer orderCustomer);
//
//        void acceptCustomer(OrderCustomer orderCustomer);
//
//        void jumpAccept(OrderCustomer orderCustomer);
//
//        void arriveEnd(OrderCustomer orderCustomer);
//
//        void jumpSend(OrderCustomer orderCustomer);
//
//        void deleteDb(long orderId, String orderType);
//
//        void navi(LatLng latLng, Long orderId);
//
//        void startSend(long orderId);
//
//        void finishTask(long orderId);
//    }
//
//    interface Model {
//        Observable<EmResult2<List<ZXOrder>>> getZxOrderList();
//
//        Observable<Object> startSend(long orderId);
//
//        Observable<Object> startSchedule(long orderId);
//
//        Observable<Object> finishSchedule(long orderId);
//
//
//        Observable<Object> arriveStart(long id);
//        Observable<Object> acceptCustomer(long id);
//        Observable<Object> jumpCustomer(long id);
//        Observable<Object> sendCustomer(long id);
//
//        Observable<EmResult2<List<OrderCustomer>>> geOrderCustomers(long orderId);
//    }
//}
