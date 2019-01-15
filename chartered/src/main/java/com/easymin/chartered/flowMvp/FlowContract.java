package com.easymin.chartered.flowMvp;
import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.services.route.DriveRouteResult;
import com.easymi.component.entity.BaseOrder;
import com.easymi.component.result.EmResult;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.widget.LoadingButton;
import com.easymin.chartered.entity.CharteredOrder;
import com.easymin.chartered.result.OrderListResult;

import java.util.List;

import retrofit2.http.Field;
import rx.Observable;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: FlowContract
 * Author: shine
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
         * 初始化连接桥
         */
        void initBridget();

        /**
         * 显示路径区域缩放
         * @param latLngs
         */
        void boundsZoom(List<LatLng> latLngs);

        /**
         * 定位缩放
         * @param level
         */
        void locZoom(int level);

        /**
         * 显示导航路径
         * @param ints
         * @param path
         */
        void showPath(int[] ints, AMapNaviPath path);

        /**
         * 显示规划路径
         * @param result
         */
        void showPath(DriveRouteResult result);

        /**
         * 显示时间距离
         * @param dis
         * @param time
         */
        void showLeft(int dis, int time);

        /**
         * 显示订单信息
         * @param listdata
         */
        void showOrder(CharteredOrder listdata);

        /**
         * 显示底部订单fragment
         * @param baseOrder
         */
        void showBottomFragment(BaseOrder baseOrder);

        /**
         * 显示地图路径
         */
        void showMapBounds();

        /**
         * 结束订单
         */
        void toFinish();

        /**
         * 初始化RxManager
         * @return
         */
        RxManager getManager();
    }

    interface Presenter {
        /**
         * 通过导航规划路径
         * @param endLat
         * @param endLng
         */
        void routePlanByNavi(Double endLat, Double endLng);

        /**
         * 路径规划
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
         * 查询单个订单
         * @param orderId
         */
        void findOne(long orderId);

        /**
         * 订单状态改变
         * @param orderId
         * @param status
         */
        void changeStauts(Long orderId, int status);

        /**
         * 订单确认
         * @param orderId
         * @param version
         * @param button
         */
        void orderConfirm(long orderId,long version,LoadingButton button);
    }

    interface Model {
        /**
         * 查询单个订单
         * @param orderId
         * @return
         */
        Observable<OrderListResult> findOne(long orderId);

        /**
         * 改变订单的状态
         * @param orderId
         * @param status
         * @return
         */
        Observable<EmResult> changeStauts(Long orderId, int status);

        /**
         * 确认订单
         * @param orderId
         * @param version
         * @return
         */
        Observable<EmResult> orderConfirm(long orderId,long version);
    }
}
