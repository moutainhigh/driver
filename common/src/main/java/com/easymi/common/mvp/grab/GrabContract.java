package com.easymi.common.mvp.grab;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.DriveRouteResult;
import com.easymi.common.entity.MultipleOrder;
import com.easymi.common.result.MultipleOrderResult;
import com.easymi.component.rxmvp.RxManager;

import java.util.List;

import rx.Observable;

/**
 *
 * @author developerLzh
 * @date 2017/11/22 0022
 */

public interface GrabContract {

    interface View {
        /**
         * 显示订单
         * @param multipleOrder
         */
        void showBase(MultipleOrder multipleOrder);

        /**
         * 显示阴影
         */
        void showShade();

        /**
         * 展示抢单接单倒计时
         */
        void showGrabCountDown();

        /**
         * 初始化viewpager
         */
        void initViewPager();

        /**
         * 结束当前页面
         */
        void finishActivity();

        /**
         * 展示规划路径
         * @param result
         */
        void showPath(DriveRouteResult result);

        /**
         * 设置起点marker
         * @param start
         */
        void showStartMarker(LatLonPoint start);

        /**
         * 设置途径点marker
         * @param pass
         */
        void showPassMarker(List<LatLonPoint> pass);

        /**
         * 设置终点marker
         * @param end
         */
        void showEndMarker(LatLonPoint end);

        /**
         * 移除所有marker
         */
        void removeAllOrderMarker();

        /**
         * 通过订单id移除订单
         * @param orderId
         */
        void removerOrderById(long orderId);

        /**
         * 获取RxManager
         * @return
         */
        RxManager getManager();
    }

    interface Presenter {
        /**
         * 查询对应订单
         * @param order
         */
        void queryOrder(MultipleOrder order);

        /**
         * 抢单
         * @param order
         */
        void grabOrder(MultipleOrder order);

        /**
         * 接单
         * @param order
         */
        void takeOrder(MultipleOrder order);

        /**
         * 规划路径
         * @param endPoint
         * @param pass
         */
        void routePlanByRouteSearch(LatLonPoint endPoint, List<LatLonPoint> pass);
    }

    interface Model {
        /**
         * 查询代驾订单
         * @param orderId
         * @return
         */
        Observable<MultipleOrderResult> queryDJOrder(Long orderId);

        /**
         * 抢代驾订单
         * @param orderId
         * @return
         */
        Observable<MultipleOrderResult> grabDJOrder(Long orderId);

        /**
         * 接代驾订单
         * @param orderId
         * @return
         */
        Observable<MultipleOrderResult> takeDJOrder(Long orderId);

        /**
         * 查询专车订单
         * @param orderId
         * @return
         */
        Observable<MultipleOrderResult> queryZCOrder(Long orderId);

        /**
         * 抢专车订单
         * @param orderId
         * @param version
         * @return
         */
        Observable<MultipleOrderResult> grabZCOrder(Long orderId,Long version);

        /**
         * 接专车订单
         * @param orderId
         * @param version
         * @return
         */
        Observable<MultipleOrderResult> takeZCOrder(Long orderId,Long version);

        /**
         * 查询出租车订单
         * @param orderId
         * @return
         */
        Observable<MultipleOrderResult> queryTaxiOrder(Long orderId);

        /**
         * 抢出租车订单
         * @param orderId
         * @param version
         * @return
         */
        Observable<MultipleOrderResult> grabTaxiOrder(Long orderId,Long version);

        /**
         * 接出租车订单
         * @param orderId
         * @param version
         * @return
         */
        Observable<MultipleOrderResult> takeTaxiOrder(Long orderId,Long version);


    }

}
