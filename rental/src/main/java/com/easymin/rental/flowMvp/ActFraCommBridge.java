package com.easymin.rental.flowMvp;

import com.amap.api.maps.model.LatLng;
import com.easymi.component.widget.LoadingButton;

import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: ActFraCommBridge
 *@Author: shine
 * Date: 2018/12/18 下午2:00
 * Description:
 * History:
 */
public interface ActFraCommBridge {


    void showBounds(List<LatLng> latLngs);

    void clearMap();

    void routePath(LatLng toLatlng);

    void routePath(LatLng startLatlng, List<LatLng> passLatlngs, LatLng endLatlng);

    void doRefresh();

    void countStartOver();

    void navi(LatLng latLng, Long orderId);

    void toNotStart();

    void toStart();

    void arriveStart();

    void doStartDrive();

    void arriveDestance();

    void toFinish(LoadingButton button);
}
