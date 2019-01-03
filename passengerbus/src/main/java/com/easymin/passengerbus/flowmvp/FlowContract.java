package com.easymin.passengerbus.flowMvp;

import com.easymi.component.result.EmResult2;
import com.easymi.component.rxmvp.RxManager;
import com.easymin.passengerbus.entity.BusStationResult;

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

        void initMap();

        void initBridget();

        void showFragmentByStatus();

        void showBusLineInfo(BusStationResult busStationResult);

        void showNext();


        RxManager getManager();
    }

    interface Presenter {

        void startStation(long scheduleId);

        void arriveStation(long scheduleId, long stationId);

        void endStation(long scheduleId);

        void findBusOrderById(long id);

        void toNextStation(long scheduleId, long stationId);

    }

    interface Model {

        Observable<EmResult2<Object>> startStation(long scheduleId);
        Observable<EmResult2<Object>> arriveStation(long scheduleId, long stationId);
        Observable<EmResult2<Object>> endStation(long scheduleId);

        Observable<EmResult2<BusStationResult>> findBusOrderById(long id);

        Observable<EmResult2<Object>> toNextStation(long scheduleId, long stationId);
    }
}
