package com.easymin.passengerbus.flowmvp;

import com.amap.api.maps.model.LatLng;
import com.easymi.common.entity.OrderCustomer;

import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: ActFraCommBridge
 * Author: shine
 * Date: 2018/12/18 下午2:00
 * Description:
 * History:
 */
public interface ActFraCommBridge {





    void changeToolbar(int flag);

    /**
     * 出发
     */
    void arriveStart();

    /**
     * 到达终点
     */
    void arriveEnd();

    /**
     * 滑动到达下一站
     */
    void slideToNext(long stationId);

    /**
     * 滑动到达站点
     */
    void sideToArrived(long stationId);


    void showEndFragment();


}
