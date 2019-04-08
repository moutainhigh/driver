package com.easymin.passengerbus.flowmvp;

import com.easymi.component.result.EmResult2;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.widget.LoadingButton;
import com.easymin.passengerbus.entity.BusStationResult;

import rx.Observable;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: FlowContract
 * @Author: shine
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
         * 初始化Bridget
         */
        void initBridget();

        /**
         * 展示班车信息
         * @param busStationResult
         */
        void showBusLineInfo(BusStationResult busStationResult);

        /**
         * 显示下一步布局
         */
        void showNext();

        /**
         * 结束
         */
        void finishFragment();

        /**
         * 获取RxManager
         * @return
         */
        RxManager getManager();
    }

    interface Presenter {

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
        Observable<EmResult2<BusStationResult>> findBusOrderById(long id);

        /**
         * 前往下一个站点
         * @param scheduleId
         * @param stationId
         * @return
         */
        Observable<EmResult2<Object>> toNextStation(long scheduleId, long stationId);
    }
}
