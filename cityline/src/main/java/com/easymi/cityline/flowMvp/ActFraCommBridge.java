package com.easymi.cityline.flowMvp;


import com.amap.api.maps.model.LatLng;
import com.easymi.common.entity.OrderCustomer;

import java.util.List;

/**
 * Created by liuzihao on 2017/11/15.
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
