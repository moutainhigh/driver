package com.easymi.cityline.flowMvp;

import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.services.route.DriveRouteResult;
import com.easymi.common.entity.OrderCustomer;
import com.easymi.cityline.entity.ZXOrder;
import com.easymi.cityline.result.OrderCustomerResult;
import com.easymi.cityline.result.ZxOrderResult;
import com.easymi.component.result.EmResult2;
import com.easymi.component.rxmvp.RxManager;

import java.util.List;

import rx.Observable;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 * @Author: hufeng
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public interface FlowContract {

    interface View {
        /**
         * 初始化fragment
         */
        void initFragment();

        /**
         * 初始化地图
         */
        void initMap();

        /**
         * 初始化Bridget
         */
        void initBridget();

        /**
         * 添加marker
         * @param latLng
         * @param flag
         */
        void addMarker(LatLng latLng, int flag);

        /**
         * 添加顺序marker
         * @param latLng
         * @param flag
         * @param num
         */
        void addMarker(LatLng latLng, int flag, int num);

        /**
         * 线路缩放
         * @param latLngs
         */
        void boundsZoom(List<LatLng> latLngs);

        /**
         * 定位缩放
         * @param level
         */
        void locZoom(int level);

        /**
         * 展示导航线路
         * @param ints
         * @param path
         */
        void showPath(int[] ints, AMapNaviPath path);

        /**
         * 展示规划线路
         * @param result
         */
        void showPath(DriveRouteResult result);

        /**
         * 展示线路信息
         * @param dis
         * @param time
         */
        void showLeft(int dis, int time);

        /**
         * 到达起点
         * @param orderCustomer
         */
        void arriveStartSuc(OrderCustomer orderCustomer);

        /**
         * 接到乘客
         * @param orderCustomer
         */
        void acceptCustomerSuc(OrderCustomer orderCustomer);

        /**
         * 跳过接乘客成功
         * @param orderCustomer
         */
        void jumpAcceptSuc(OrderCustomer orderCustomer);

        /**
         * 到达终点成功
         * @param orderCustomer
         */
        void arriveEndSuc(OrderCustomer orderCustomer);

        /**
         * 跳过送成功
         * @param orderCustomer
         */
        void jumpSendSuc(OrderCustomer orderCustomer);

        /**
         * 根据状加载对应fragment
         */
        void showFragmentByStatus();

        /**
         * 切换toolbar
         * @param flag
         */
        void changeToolbar(int flag);

        /**
         * 开始接人
         */
        void startOutSuc();

        /**
         * 开始送人
         */
        void startSendSuc();

        /**
         * 完成班次
         */
        void finishTaskSuc();

        RxManager getManager();
    }

    interface Presenter {
        /**
         * 通过导航获取路径
         * @param start
         * @param latLngs
         * @param end
         */
        void routeLineByNavi(LatLng start, List<LatLng> latLngs, LatLng end);

        /**
         * 通过线路规划获取路径
         * @param start
         * @param latLngs
         * @param end
         */
        void routePlanByRouteSearch(LatLng start, List<LatLng> latLngs, LatLng end);

        /**
         * 停止导航
         */
        void stopNavi();

        /**
         * 开始接人
         * @param orderId
         */
        void startOutSet(long orderId);

        /**
         * 到达起点
         * @param orderCustomer
         */
        void arriveStart(OrderCustomer orderCustomer);

        /**
         * 接到乘客
         * @param orderCustomer
         */
        void acceptCustomer(OrderCustomer orderCustomer);

        /**
         * 跳过接乘客
         * @param orderCustomer
         */
        void jumpAccept(OrderCustomer orderCustomer);

        /**
         * 到达终点
         * @param orderCustomer
         */
        void arriveEnd(OrderCustomer orderCustomer);

        /**
         * 跳过送乘客
         * @param orderCustomer
         */
        void jumpSend(OrderCustomer orderCustomer);

        /**
         * 完成班次删除数据库
         * @param orderId
         * @param orderType
         */
        void deleteDb(long orderId, String orderType);

        /**
         * 开始导航
         * @param latLng
         * @param orderId
         */
        void navi(LatLng latLng, Long orderId);

        /**
         * 开始送人
         * @param orderId
         */
        void startSend(long orderId);

        /**
         * 完成订单
         * @param orderId
         */
        void finishTask(long orderId);
    }

    interface Model {
        /**
         * 开始送人
         * @param orderId
         * @return
         */
        Observable<Object> startSend(long orderId);

        /**
         * 开始接人
         * @param orderId
         * @return
         */
        Observable<Object> startSchedule(long orderId);

        /**
         * 班次结束
         * @param orderId
         * @return
         */
        Observable<Object> finishSchedule(long orderId);

        /**
         * 到达起点
         * @param id
         * @return
         */
        Observable<Object> arriveStart(long id);

        /**
         * 接到乘客
         * @param id
         * @return
         */
        Observable<Object> acceptCustomer(long id);

        /**
         * 跳过乘客
         * @param id
         * @return
         */
        Observable<Object> jumpCustomer(long id);

        /**
         * 送到乘客
         * @param id
         * @return
         */
        Observable<Object> sendCustomer(long id);

        /**
         * 获取班次订单
         * @param orderId
         * @return
         */
        Observable<EmResult2<List<OrderCustomer>>> geOrderCustomers(long orderId);
    }
}
