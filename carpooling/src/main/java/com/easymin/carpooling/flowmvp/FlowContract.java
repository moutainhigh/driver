package com.easymin.carpooling.flowmvp;

import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.services.route.DriveRouteResult;
import com.easymi.common.entity.CarpoolOrder;
import com.easymi.component.result.EmResult2;
import com.easymi.component.rxmvp.RxManager;
import com.easymin.carpooling.entity.AllStation;
import com.easymin.carpooling.entity.Station;
import com.easymin.carpooling.entity.StationResult;

import java.util.List;

import rx.Observable;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 *
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
         *
         * @param latLng
         * @param flag
         */
        void addMarker(LatLng latLng, int flag);

        /**
         * 添加顺序marker
         *
         * @param latLng
         * @param flag
         * @param num
         */
        void addMarker(LatLng latLng, int flag, int num, int ticketNumber, String photo);

        /**
         * 线路缩放
         *
         * @param latLngs
         */
        void boundsZoom(List<LatLng> latLngs);

        /**
         * 定位缩放
         *
         * @param level
         */
        void locZoom(int level);

        /**
         * 展示导航线路
         *
         * @param ints
         * @param path
         */
        void showPath(int[] ints, AMapNaviPath path);

        /**
         * 展示规划线路
         *
         * @param result
         */
        void showPath(DriveRouteResult result);

        /**
         * 展示线路信息
         *
         * @param dis
         * @param time
         */
        void showLeft(int dis, int time);

        /**
         * 前往预约地
         */
        void gotoStartSuc(CarpoolOrder carpoolOrder);

        /**
         * 到达起点
         *
         * @param carpoolOrder
         */
        void arriveStartSuc(CarpoolOrder carpoolOrder);

        /**
         * 接到乘客
         *
         * @param carpoolOrder
         */
        void acceptCustomerSuc(CarpoolOrder carpoolOrder);

        /**
         * 跳过接乘客成功
         *
         * @param carpoolOrder
         */
        void jumpAcceptSuc(CarpoolOrder carpoolOrder);

        /**
         * 到达终点成功
         *
         * @param carpoolOrder
         */
        void arriveEndSuc(CarpoolOrder carpoolOrder);

        /**
         * 跳过送成功
         *
         * @param carpoolOrder
         */
        void jumpSendSuc(CarpoolOrder carpoolOrder);

        /**
         * 根据状加载对应fragment
         */
        void showFragmentByStatus();

        /**
         * 切换toolbar
         *
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

///////////迭代新增

        void scheduleInfo(AllStation allStation);


        RxManager getManager();
    }

    interface Presenter {


        /**
         * 通过线路规划获取路径
         *
         * @param start
         * @param latLngs
         * @param end
         */
        void routePlanByRouteSearch(LatLng start, List<LatLng> latLngs, LatLng end);

        /**
         * 开始接人
         *
         * @param orderId
         */
        void startOutSet(long orderId);

        /**
         * 前往预约地
         *
         * @param carpoolOrder
         */
        void gotoStart(CarpoolOrder carpoolOrder);

        /**
         * 到达起点
         *
         * @param carpoolOrder
         */
        void arriveStart(CarpoolOrder carpoolOrder);

        /**
         * 接到乘客
         *
         * @param carpoolOrder
         */
        void acceptCustomer(CarpoolOrder carpoolOrder);

        /**
         * 跳过接乘客
         *
         * @param carpoolOrder
         */
        void jumpAccept(CarpoolOrder carpoolOrder);

        /**
         * 到达终点
         *
         * @param carpoolOrder
         */
        void arriveEnd(CarpoolOrder carpoolOrder);

        /**
         * 跳过送乘客
         *
         * @param carpoolOrder
         */
        void jumpSend(CarpoolOrder carpoolOrder);

        /**
         * 完成班次删除数据库
         *
         * @param orderId
         * @param orderType
         */
        void deleteDb(long orderId, String orderType);

        /**
         * 开始导航
         *
         * @param latLng
         * @param orderId
         */
        void navi(LatLng latLng, Long orderId);

        /**
         * 开始送人
         *
         * @param orderId
         */
        void startSend(long orderId);

        /**
         * 完成订单
         *
         * @param orderId
         */
        void finishTask(long orderId);

///////////迭代新增

        /**
         * 查询班次详情
         * @param scheduleId
         */
        void qureyScheduleInfo(long scheduleId);

    }

    interface Model {
        /**
         * 开始送人
         *
         * @param orderId
         * @return
         */
        Observable<Object> startSend(long orderId);

        /**
         * 开始接人
         *
         * @param orderId
         * @return
         */
        Observable<Object> startSchedule(long orderId);

        /**
         * 班次结束
         *
         * @param orderId
         * @return
         */
        Observable<Object> finishSchedule(long orderId);

        /**
         * 前往预约地
         *
         * @param id
         * @return
         */
        Observable<Object> gotoStart(long id);

        /**
         * 到达起点
         *
         * @param id
         * @return
         */
        Observable<Object> arriveStart(long id);

        /**
         * 接到乘客
         *
         * @param id
         * @return
         */
        Observable<Object> acceptCustomer(long id);

        /**
         * 跳过乘客
         *
         * @param id
         * @return
         */
        Observable<Object> jumpCustomer(long id);

        /**
         * 送到乘客
         *
         * @param id
         * @return
         */
        Observable<Object> sendCustomer(long id);

///////////迭代新增

        /**
         * 查询班次详情
         * @param scheduleId
         */
        Observable<AllStation> qureyScheduleInfo(long scheduleId);
    }
}
