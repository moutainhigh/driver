package com.easymin.passengerbus.flowmvp;

import com.amap.api.maps.model.LatLng;
import com.easymi.common.entity.OrderCustomer;
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
     * 改标题
     * @param flag
     */
    void changeToolbar(int flag);
    /**
     * 出发
     */
    void arriveStart(LoadingButton button);
    /**
     * 到达终点
     */
    void arriveEnd(LoadingButton button);
    /**
     * 滑动到达下一站
     */
    void slideToNext(int index);
    /**
     * 滑动到达站点
     */
    void sideToArrived(int index);
    /**
     * 展示结束界面
     */
    void showEndFragment();
}
