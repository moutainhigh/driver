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

        /**
         * 初始化地图
         */
        void initMap();

        /**
         * 初始化bridget
         */
        void initBridget();

        /**
         * 缩放线路
         * @param latLngs
         */
        void boundsZoom(List<LatLng> latLngs);

        /**
         * 缩放等级
         * @param level
         */
        void locZoom(int level);

        /**
         * 显示导航线路
         * @param ints
         * @param path
         */
        void showPath(int[] ints, AMapNaviPath path);

        /**
         * 显示规划线路
         * @param result
         */
        void showPath(DriveRouteResult result);

        /**
         * 显示导航剩余里程时间
         * @param dis
         * @param time
         */
        void showLeft(int dis, int time);

        /**
         * 显示订单信息
         * @param order
         */
        void showOrder(RentalOrder order);

        /**
         * 显示底部fragment
         * @param baseOrder
         */
        void showBottomFragment(BaseOrder baseOrder);

        /**
         * 显示地图路径
         */
        void showMapBounds();

        /**
         * 完成订单
         */
        void toFinish();

        /**
         * 获取RxManager
         * @return
         */
        RxManager getManager();
    }

    interface Presenter {
        /**
         * 通过导航规划路线
         * @param endLat
         * @param endLng
         */
        void routePlanByNavi(Double endLat, Double endLng);

        /**
         * 通过路径规划获取路线
         * @param start
         * @param latLngs
         * @param end
         */
        void routePlanByRouteSearch(LatLng start, List<LatLng> latLngs, LatLng end);

        /**
         * 开始导航
         * @param latLng
         * @param orderId
         */
        void navi(LatLng latLng, Long orderId);

        /**
         * 停止导航
         */
        void stopNavi();

        /**
         * 查询订单
         * @param orderId
         */
        void findOne(long orderId);

        /**
         * 改变订单状态
         * @param orderId
         * @param status
         */
        void changeStauts(Long orderId, int status);

        /**
         * 确认订单完成
         * @param orderId
         * @param version
         * @param button
         */
        void orderConfirm(long orderId, long version, LoadingButton button);
    }

    interface Model {
        /**
         * 查询订单
         * @param orderId
         * @return
         */
        Observable<OrderResult> findOne(long orderId);

        /**
         * 改变订单状态
         * @param orderId
         * @param status
         * @return
         */
        Observable<EmResult> changeStauts(Long orderId, int status);

        /**
         * 确认订单完成
         * @param orderId
         * @param version
         * @return
         */
        Observable<EmResult> orderConfirm(long orderId, long version);
    }
}
