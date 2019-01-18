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
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: FlowContract
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */
public interface FlowContract {

    interface View {
        /**
         * 初始化
         */
        void initToolbar();

        /**
         * 初始化右上角弹窗
         */
        void initPop();

        /**
         * 显示顶部布局
         */
        void showTopView();

        /**
         * 显示顶部地址
         * @param toPlace
         */
        void showToPlace(String toPlace);

        /**
         * 显示剩余时间
         * @param leftTime
         */
        void showLeftTime(String leftTime);

        /**
         * 初始化bridge
         */
        void initBridge();

        /**
         * 根据订单显示底部布局
         * @param zcOrder
         */
        void showBottomFragment(ZCOrder zcOrder);

        /**
         * 显示订单
         * @param zcOrder
         */
        void showOrder(ZCOrder zcOrder);

        /**
         * 初始化地图
         */
        void initMap();

        /**
         * 显示赌徒线路
         */
        void showMapBounds();

        /**
         * 取消订单成功
         */
        void cancelSuc();

        /**
         * 拒单成功
         */
        void refuseSuc();

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
         * 显示支付类型
         * @param money
         * @param consumerInfo
         */
        void showPayType(double money, ConsumerInfo consumerInfo);

        /**
         * 支付成功
         */
        void paySuc();

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

        /**
         * 显示到达终点布局
         */
        void showToEndFragment();

        /**
         * 显示乘客星系
         * @param consumerInfo
         */
        void showConsumer(ConsumerInfo consumerInfo);

        /**
         * 隐藏顶部布局
         */
        void hideTops();

        /**
         * 获取 RxManager
         * @return
         */
        RxManager getManager();

        /**
         * 重新导航规划
         */
        void reRout();
    }

    interface Presenter {
        /**
         * 接单
         * @param orderId
         * @param version
         * @param btn
         */
        void acceptOrder(Long orderId, Long version,LoadingButton btn);

        /**
         * 拒单
         * @param orderId
         * @param orderType
         * @param remark
         */
        void refuseOrder(Long orderId,String orderType, String remark);

        /**
         * 前往预约地
         * @param orderId
         * @param version
         * @param btn
         */
        void toStart(Long orderId,Long version, LoadingButton btn);

        /**
         * 到达预约地
         * @param orderId
         * @param version
         */
        void arriveStart(Long orderId,Long version);

        /**
         * 开始等待 带点击效果
         * @param orderId
         * @param btn
         */
        void startWait(Long orderId, LoadingButton btn);

        /**
         * 开始等待
         * @param orderId
         */
        void startWait(Long orderId);

        /**
         * 开始出发
         * @param orderId
         * @param version
         * @param btn
         */
        void startDrive(Long orderId,Long version, LoadingButton btn);

        /**
         * 到达目的地
         * @param zcOrder
         * @param version
         * @param btn
         * @param dymOrder
         */
        void arriveDes(ZCOrder zcOrder,Long version, LoadingButton btn, DymOrder dymOrder);

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
         * 查询单个订单 是否带进度条
         * @param orderId
         * @param needShowProgress
         */
        void findOne(Long orderId, boolean needShowProgress);

        /**
         * 修改终点 未使用
         * @param orderId
         * @param lat
         * @param lng
         * @param address
         */
        void changeEnd(Long orderId, Double lat, Double lng, String address);

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
         * 通过查询规划路线
         * @param endLat
         * @param endLng
         */
        void routePlanByRouteSearch(Double endLat, Double endLng);

        /**
         * 更新本地订单信息
         * @param zcOrder
         */
        void updateDymOrder(ZCOrder zcOrder);

        /**
         * 支付订单
         * @param orderId
         * @param payType
         * @param version
         */
        void payOrder(Long orderId, String payType, Long version);

        /**
         * 停止导航
         */
        void stopNavi();

        /**
         * 获取乘客信息
         * @param orderId
         */
        void getConsumerInfo(Long orderId);

    }

    interface Model {
        /**
         * 接单
         * @param orderId
         * @param version
         * @return
         */
        Observable<ZCOrderResult> doAccept(Long orderId,Long version);

        /**
         * 查询订单
         * @param orderId
         * @return
         */
        Observable<ZCOrderResult> findOne(Long orderId);

        /**
         * 拒绝订单
         * @param orderId
         * @param orderType
         * @param remark
         * @return
         */
        Observable<EmResult> refuseOrder(Long orderId,String orderType, String remark);

        /**
         * 前往预约地
         * @param orderId
         * @param version
         * @return
         */
        Observable<ZCOrderResult> toStart(Long orderId,Long version);

        /**
         * 到达预约地
         * @param orderId
         * @param version
         * @return
         */
        Observable<ZCOrderResult> arriveStart(Long orderId,Long version);

        /**
         * 开始等待
         * @param orderId
         * @return
         */
        Observable<ZCOrderResult> startWait(Long orderId);

        /**
         * 开始出发
         * @param orderId
         * @param version
         * @return
         */
        Observable<ZCOrderResult> startDrive(Long orderId,Long version);

        /**
         * 到达目的地
         * @param zcOrder
         * @param dymOrder
         * @param version
         * @return
         */
        Observable<ZCOrderResult> arriveDes(ZCOrder zcOrder,DymOrder dymOrder,Long version);

        /**
         * 修改终点
         * @param orderId
         * @param lat
         * @param lng
         * @param address
         * @return
         */
        Observable<ZCOrderResult> changeEnd(Long orderId, Double lat, Double lng, String address);

        /**
         * 取消订单
         * @param orderId
         * @param remark
         * @return
         */
        Observable<EmResult> cancelOrder(Long orderId, String remark);

        /**
         * 客户信息
         * @param orderId
         * @return
         */
        Observable<ConsumerResult> consumerInfo(Long orderId);

        /**
         * 支付订单
         * @param orderId
         * @param payType
         * @param version
         * @return
         */
        Observable<EmResult> payOrder(Long orderId, String payType, Long version);
    }
}
