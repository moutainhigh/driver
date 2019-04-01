package com.easymin.official.flowMvp;

import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.services.route.DriveRouteResult;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.result.EmResult;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.widget.LoadingButton;
import com.easymin.official.entity.GovOrder;
import com.easymin.official.result.GovOrderResult;

import rx.Observable;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: FlowContract
 * @Author: hufeng
 * @Date: 2019/3/25 下午5:12
 * @Description:
 * @History:
 */
public interface FlowContract {

    interface View {
        /**
         * 初始化
         */
        void initToolbar();
        /**
         * 初始化地图
         */
        void initMap();
        /**
         * 显示顶部布局
         */
        void showTopView();

        /**
         * 初始化bridge
         */
        void initBridge();

        /**
         * 根据订单显示底部布局
         * @param govOrder
         */
        void showBottomFragment(GovOrder govOrder);

        /**
         * 显示订单
         * @param govOrder
         */
        void showOrder(GovOrder govOrder);

        /**
         * 显示地图线路
         */
        void showMapBounds();

        /**
         * 取消订单成功
         */
        void cancelSuc();

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
         * 显示剩余距离和时间
         * @param dis
         * @param time
         */
        void showLeft(int dis, int time);

        /**
         * 重新规划路径开始
         */
        void showReCal();
//
//        /**
//         * 显示到达终点布局
//         */
//        void showToEndFragment();

        /**
         * 获取 RxManager
         * @return
         */
        RxManager getManager();

        /**
         * 重新导航规划
         */
        void reRout();

        /**
         * 确认订单后结束界面
         */
        void finishActivity();
    }

    interface Presenter {

        /**
         * 前往预约地
         * @param orderId
         */
        void toStart(Long orderId,Long version);

        /**
         * 到达预约地
         * @param orderId
         */
        void arriveStart(Long orderId,Long version);

        /**
         * 开始出发 前往目的地
         * @param orderId
         */
        void startDrive(Long orderId,Long version);

        /**
         * 到达目的地
         * @param orderId
         */
        void arriveDes(Long orderId,Long version, DymOrder dymOrder);

        /**
         * 确认订单
         * @param orderId
         */
        void confirmOrder(Long orderId,Long version,String voucher);

        /**
         * 开始导航
         * @param latLng
         * @param poi
         * @param orderId
         */
        void navi(LatLng latLng, String poi, Long orderId);

        /**
         * 查询单个订单
         * @param orderId
         */
        void findOne(Long orderId);


        /**
         * 取消订单
         * @param orderId
         * @param remark
         */
        void cancelOrder(Long orderId, String remark);

        /**
         * 通过导航规划路线
         * @param endLat
         * @param endLng
         */
        void routePlanByNavi(Double endLat, Double endLng);


        /**
         * 更新本地订单信息
         * @param govOrder
         */
        void updateDymOrder(GovOrder govOrder);

        /**
         * 停止导航
         */
        void stopNavi();
    }

    interface Model {


        /**
         * 查询订单
         * @param orderId
         * @return
         */
        Observable<GovOrderResult> findOne(Long orderId);

        /**
         * 前往预约地
         * @param orderId
         * @return
         */
        Observable<GovOrderResult> toStart(Long orderId,Long version);

        /**
         * 到达预约地
         * @param orderId
         * @return
         */
        Observable<GovOrderResult> arriveStart(Long orderId,Long version);


        /**
         * 开始出发 前往预约地
         * @param orderId
         * @return
         */
        Observable<GovOrderResult> startDrive(Long orderId,Long version);

        /**
         * 到达目的地
         * @param orderId
         * @return
         */
        Observable<GovOrderResult> arriveDes(Long orderId,DymOrder dymOrder,Long version);

        /**
         * 取消订单
         * @param orderId
         * @param remark
         * @return
         */
        Observable<EmResult> cancelOrder(Long orderId, String remark);

        /**
         * 确认订单
         * @param orderId
         * @return
         */
        Observable<EmResult> confirmOrder(Long orderId,Long version,String voucher);

    }
}
