package com.easymin.chartered.flowMvp;

import com.amap.api.maps.model.LatLng;
import com.easymi.common.entity.OrderCustomer;
import com.easymi.component.entity.BaseOrder;

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

    void toOrderList();

    void clearMap();

    void routePath(LatLng toLatlng);

    void routePath(LatLng startLatlng, List<LatLng> passLatlngs, LatLng endLatlng);

    void doRefresh();

    void countStartOver();

    void navi(LatLng latLng, Long orderId);

    void toNotStart();

}
