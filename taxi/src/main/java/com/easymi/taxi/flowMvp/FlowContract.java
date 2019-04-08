package com.easymi.taxi.flowMvp;

import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.services.route.DriveRouteResult;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.result.EmResult;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.widget.LoadingButton;
import com.easymi.taxi.entity.ConsumerInfo;
import com.easymi.taxi.entity.TaxiOrder;
import com.easymi.taxi.result.ConsumerResult;
import com.easymi.taxi.result.TaxiOrderResult;

import rx.Observable;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */
public interface FlowContract {

    interface View {
        /**
         * 初始化标题
         */
        void initToolbar();

        /**
         * 初始化弹框
         */
        void initPop();

        /**
         * 显示地图顶部布局
         */
        void showTopView();

        /**
         * 显示前往目的地布局 未使用
         * @param toPlace
         */
        void showToPlace(String toPlace);

        /**
         * 显示剩余时间 未使用
         * @param leftTime
         */
        void showLeftTime(String leftTime);

        /**
         * 初始化bridge
         */
        void initBridge();

        /**
         * 显示底部fragment
         * @param taxiOrder
         */
        void showBottomFragment(TaxiOrder taxiOrder);

        /**
         * 显示订单信息
         * @param taxiOrder
         */
        void showOrder(TaxiOrder taxiOrder);

        /**
         * 初始化地图
         */
        void initMap();

        /**
         * 显示地图路径
         */
        void showMapBounds();

        /**
         * 取消规划
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
         * 显示导航剩余公里时间
         * @param dis
         * @param time
         */
        void showLeft(int dis, int time);

        /**
         * 重新规划路径
         */
        void showReCal();//重新规划路径开始

        /**
         * 显示前往目的地布局
         */
        void showToEndFragment();

        /**
         * 显示乘客信息
         * @param consumerInfo
         */
        void showConsumer(ConsumerInfo consumerInfo);

        /**
         * 隐藏顶部信息
         */
        void hideTops();

        /**
         * 结算成功
         */
        void settleSuc();

        /**
         * 获取RxManager
         * @return
         */
        RxManager getManager();
    }

    interface Presenter {
        /**
         * 接单
         * @param orderId
         * @param btn
         */
        void acceptOrder(Long orderId, LoadingButton btn);

        /**
         * 拒单
         * @param orderId
         * @param orderType
         * @param remark
         */
        void refuseOrder(Long orderId,String orderType, String remark);

        /**
         * 前往目的地
         * @param orderId
         * @param btn
         */
        void toStart(Long orderId, LoadingButton btn);

        /**
         * 到达目的地
         * @param orderId
         */
        void arriveStart(Long orderId);

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
         * @param btn
         */
        void startDrive(Long orderId, LoadingButton btn);

        /**
         * 到达目的地
         * @param taxiOrder
         * @param btn
         * @param dymOrder
         */
        void arriveDes(TaxiOrder taxiOrder, LoadingButton btn, DymOrder dymOrder);

        /**
         * 开始导航
         * @param latLng
         * @param poi
         * @param orderId
         */
        void navi(LatLng latLng, String poi, Long orderId);

        /**
         * 查找订单
         * @param orderId
         */
        void findOne(Long orderId);

        /**
         * 查找订单 带进度条
         * @param orderId
         * @param needShowProgress
         */
        void findOne(Long orderId, boolean needShowProgress);

        /**
         * 修改目的地  未使用
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
         * 通过导航规划线路
         * @param endLat
         * @param endLng
         */
        void routePlanByNavi(Double endLat, Double endLng);

        /**
         * 通过线路规划查询路线 未使用
         * @param endLat
         * @param endLng
         */
        void routePlanByRouteSearch(Double endLat, Double endLng);

        /**
         * 更新本地数据库
         * @param taxiOrder
         */
        void updateDymOrder(TaxiOrder taxiOrder);

        /**
         * 支付订单
         * @param orderId
         * @param payType
         */
        void payOrder(Long orderId, String payType);

        /**
         * 停止导航
         */
        void stopNavi();

        /**
         * 获取乘客信息
         * @param orderId
         */
        void getConsumerInfo(Long orderId);

        /**
         * 订单数据更新同步
         * @param result
         * @return
         */
        TaxiOrderResult orderResult2ZCOrder(TaxiOrderResult result);

        /**
         * 改变订单状态
         * @param companyId
         * @param detailAddress
         * @param driverId
         * @param latitude
         * @param longitude
         * @param orderId
         * @param status
         * @param btn
         */
        void changeOrderStatus(Long companyId,String detailAddress, Long driverId, Double latitude,
                               Double longitude,Long orderId,int status, LoadingButton btn);

        /**
         * 订单结算
         * @param orderId
         * @param orderNo
         * @param fee
         */
        void taxiSettlement(Long orderId, String orderNo,double fee);

    }

    interface Model {
        /**
         * 接单
         * @param orderId
         * @return
         */
        Observable<TaxiOrderResult> doAccept(Long orderId);

        /**
         * 查询订单
         * @param orderId
         * @return
         */
        Observable<TaxiOrderResult> findOne(Long orderId);

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
         * @return
         */
        Observable<TaxiOrderResult> toStart(Long orderId);

        /**
         * 到达预约地
         * @param orderId
         * @return
         */
        Observable<TaxiOrderResult> arriveStart(Long orderId);

        /**
         * 开始等待
         * @param orderId
         * @return
         */
        Observable<TaxiOrderResult> startWait(Long orderId);

        /**
         * 开始出发
         * @param orderId
         * @return
         */
        Observable<TaxiOrderResult> startDrive(Long orderId);

        /**
         * 到达目的地
         * @param taxiOrder
         * @param dymOrder
         * @return
         */
        Observable<TaxiOrderResult> arriveDes(TaxiOrder taxiOrder, DymOrder dymOrder);

        /**
         * 修改终点
         * @param orderId
         * @param lat
         * @param lng
         * @param address
         * @return
         */
        Observable<TaxiOrderResult> changeEnd(Long orderId, Double lat, Double lng, String address);

        /**
         * 取消订单
         * @param orderId
         * @param remark
         * @return
         */
        Observable<EmResult> cancelOrder(Long orderId, String remark);

        /**
         * 获取乘客信息
         * @param orderId
         * @return
         */
        Observable<ConsumerResult> consumerInfo(Long orderId);

        /**
         * 支付订单哪
         * @param orderId
         * @param payType
         * @return
         */
        Observable<EmResult> payOrder(Long orderId, String payType);

        /**
         * 修改订单状态
         * @param companyId
         * @param detailAddress
         * @param driverId
         * @param latitude
         * @param longitude
         * @param orderId
         * @param status
         * @return
         */
        Observable<EmResult> changeOrderStatus(Long companyId,String detailAddress, Long driverId, Double latitude,
                                               Double longitude,Long orderId,int status);

        /**
         * 结算订单
         * @param orderId
         * @param orderNo
         * @param fee  输入费用
         * @return
         */
        Observable<EmResult> taxiSettlement(Long orderId, String orderNo,double fee);
    }
}
