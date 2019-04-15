package com.easymin.custombus.mvp;

import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.model.AMapNaviPath;
import com.easymi.component.result.EmResult2;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.widget.LoadingButton;
import com.easymin.custombus.entity.CbBusOrder;
import com.easymin.custombus.entity.Customer;
import com.easymin.custombus.entity.OrdersResult;
import com.easymin.custombus.entity.StationResult;
import com.easymin.custombus.entity.TimeResult;

import java.util.List;

import rx.Observable;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: FlowContract
 * @Author: hufeng
 * @Date: 2019/2/18 下午5:11
 * @Description:
 * @History:
 */
public interface FlowContract {

    interface View {

        /**
         * 显示剩余距离和时间
         * @param dis
         * @param time
         */
        void showLeft(int dis, int time);

        /**
         * 展示班车信息
         * @param cbBusOrder
         */
        void showBusLineInfo(CbBusOrder cbBusOrder);

        /**
         * 开始验票时间点
         * @param time
         */
        void showcheckTime(long time);

        /**
         * 获取站点订单信息
         * @param customers
         */
        void showOrders(List<Customer> customers);

        /**
         * 站点操作成功
         */
        void dealSuccese();

        /**
         * 站点操作成功
         */
        void succeseOrder(Customer customer);

        /**
         * 结束行程关闭界面
         */
        void finishActivity();

        /**
         * 验票码订单状态错误
         */
        void errorCode();

        /**
         * 获取 RxManager
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
         * 停止导航
         */
        void stopNavi();

        /**
         * 开始行程
         * @param scheduleId
         * @param button
         */
        void startStation(long scheduleId,LoadingButton button);

        /**
         * 到达站点
         * @param scheduleId
         * @param stationId
         */
        void arriveStation(long scheduleId, long stationId);

        /**
         * 结束行程
         * @param scheduleId
         * @param button
         */
        void endStation(long scheduleId,LoadingButton button);

        /**
         * 查询班车信息
         * @param id
         */
        void findBusOrderById(long id);

        /**
         * 前往下一个站点
         * @param scheduleId
         * @param stationId
         */
        void toNextStation(long scheduleId, long stationId);

        /**
         * 开始验票
         * @param id
         */
        void chechTickets(long id);

        /**
         * 获取站点订单
         * @param scheduleId
         * @param stationId
         */
        void queryOrders(long scheduleId, long stationId);


        /**
         * 根据乘车码查询订单详情
         * @param rideCode
         */
        void queryByRideCode(String rideCode);

        /**
         * 检查乘车码
         * @param rideCode
         */
        void checkRideCode(String rideCode, LoadingButton button);



    }

    interface Model {
        /**
         * 开始行程
         * @param scheduleId
         * @return
         */
        Observable<EmResult2<Object>> startStation(long scheduleId);

        /**
         * 到达站点
         * @param scheduleId
         * @param stationId
         * @return
         */
        Observable<EmResult2<Object>> arriveStation(long scheduleId, long stationId);

        /**
         * 结束行程
         * @param scheduleId
         * @return
         */
        Observable<EmResult2<Object>> endStation(long scheduleId);

        /**
         * 查询班车信息
         * @param id
         * @return
         */
        Observable<StationResult> findBusOrderById(long id);

        /**
         * 前往下一个站点
         * @param scheduleId
         * @param stationId
         * @return
         */
        Observable<EmResult2<Object>> toNextStation(long scheduleId, long stationId);

        /**
         * 开始验票
         * @param id
         * @return
         */
        Observable<TimeResult> chechTickets(long id);

        /**
         * 获取站点订单
         * @param scheduleId
         * @param stationId
         * @return
         */
        Observable<OrdersResult> queryOrders(long scheduleId, long stationId);

        /**
         * 根据乘车码查询订单详情
         * @param rideCode
         * @return
         */
        Observable<EmResult2<Customer>> queryByRideCode(String rideCode);


        /**
         * 检查乘车码
         * @param rideCode
         * @return
         */
        Observable<EmResult2<Object>> checkRideCode(String rideCode);

    }
}
