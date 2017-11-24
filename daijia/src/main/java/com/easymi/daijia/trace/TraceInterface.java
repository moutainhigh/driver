package com.easymi.daijia.trace;

import com.amap.api.maps.model.LatLng;

import java.util.List;

/**
 * Created by developerLzh on 2017/11/24 0024.
 */

public interface TraceInterface {

    void showTraceAfter(List<LatLng> beforeLatlngs,List<LatLng> afterLatlngs);
}
