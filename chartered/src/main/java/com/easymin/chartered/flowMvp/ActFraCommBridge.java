package com.easymin.chartered.flowMvp;

import com.amap.api.maps.model.LatLng;
import com.easymi.common.entity.OrderCustomer;
import com.easymi.component.entity.BaseOrder;
import com.easymi.component.widget.LoadingButton;

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
    void toStart();

    /**
     * 到达预约地
     */
    void arriveStart();

    /**
     * 前往预约地
     */
    void doStartDrive();

    /**
     * 到达目的地
     */
    void arriveDestance();

    /**
     * 订单完成
     * @param button
     */
    void toFinish(LoadingButton button);
}
