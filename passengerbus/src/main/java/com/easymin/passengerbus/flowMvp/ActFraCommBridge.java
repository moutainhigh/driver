package com.easymin.passengerbus.flowMvp;

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


    void showBounds(List<LatLng> latLngs);

    void addMarker(LatLng latLng, int flag);

    void addMarker(LatLng latLng, int flag, int num);

    void toCusList(int flag);
    void toNotStart();
    void toAcSend();
    void toChangeSeq(int flag);
    void toFinished();
    void toOrderList();

    void changeToolbar(int flag);

    void clearMap();

    void routePath(LatLng toLatlng);

    void routePath(LatLng startLatlng, List<LatLng> passLatlngs, LatLng endLatlng);

    void startOutSet();

    void arriveStart(OrderCustomer orderCustomer);

    void acceptCustomer(OrderCustomer orderCustomer);

    void jumpAccept(OrderCustomer orderCustomer);

    void arriveEnd(OrderCustomer orderCustomer);

    void jumpSend(OrderCustomer orderCustomer);

    void doRefresh();

    void countStartOver();

    void navi(LatLng latLng, Long orderId);
}
