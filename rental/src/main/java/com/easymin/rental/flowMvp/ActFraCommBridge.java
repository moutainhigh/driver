package com.easymin.rental.flowMvp;

import com.amap.api.maps.model.LatLng;
import com.easymi.component.widget.LoadingButton;

import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: ActFraCommBridge
 * @Author: shine
 * Date: 2018/12/18 下午2:00
 * Description:
 * History:
 */
public interface ActFraCommBridge {

    /**
     * 显示线路点
     * @param latLngs
     */
    void showBounds(List<LatLng> latLngs);

    /**
     * 清理marker
     */
    void clearMap();

    /**
     * 规划到某点的线路
     * @param toLatlng
     */
    void routePath(LatLng toLatlng);

    /**
     * 规划线路
     * @param startLatlng
     * @param passLatlngs
     * @param endLatlng
     */
    void routePath(LatLng startLatlng, List<LatLng> passLatlngs, LatLng endLatlng);

    /**
     * 刷新地图，定位当前点
     */
    void doRefresh();

    /**
     * 倒计时结束
     */
    void countStartOver();

    /**
     * 开始导航
     * @param latLng
     * @param orderId
     */
    void navi(LatLng latLng, Long orderId);

    /**
     * 开始行程
     */
    void toNotStart();

    /**
     * 前往预约地
     */
    void toStart();

    /**
     * 到达预约地
     */
    void arriveStart();

    /**
     * 开始出发
     */
    void doStartDrive();

    /**
     * 到达目的地
     */
    void arriveDestance();

    /**
     * 完成订单
     * @param button
     */
    void toFinish(LoadingButton button);
}
