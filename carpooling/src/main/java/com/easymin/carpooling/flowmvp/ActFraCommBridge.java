package com.easymin.carpooling.flowmvp;


import com.amap.api.maps.model.LatLng;
import com.easymi.common.entity.CarpoolOrder;

import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 *
 * @Author: hufeng
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public interface ActFraCommBridge {

    /**
     * 规划线路点
     *
     * @param latLngs
     */
    void showBounds(List<LatLng> latLngs);

    /**
     * 添加marker到地图
     *
     * @param latLng
     * @param flag
     */
    void addMarker(LatLng latLng, int flag);

    /**
     * 添加排序marker
     *
     * @param latLng
     * @param flag
     * @param num
     */
    void addMarker(LatLng latLng, int flag, int num, int ticketNumber, String photo);

    /**
     * 自定义排序
     *
     * @param flag
     */
    void toCusList(int flag);

    /**
     * 开始行程
     */
    void toNotStart();

    /**
     * 去接人
     */
    void toAcSend();

    /**
     * 排序
     *
     * @param flag
     */
    void toChangeSeq(int flag);

    /**
     * 完成班次
     */
    void toFinished();

    /**
     * 返回订单列表
     */
    void toOrderList();

    /**
     * 改变toolbar展示
     *
     * @param flag
     */
    void changeToolbar(int flag);

    /**
     * 清理地图
     */
    void clearMap();

    /**
     * 规划线路
     *
     * @param toLatlng
     */
    void routePath(LatLng toLatlng);

    /**
     * 根据起点终点途径点规划路径
     *
     * @param startLatlng
     * @param passLatlngs
     * @param endLatlng
     */
    void routePath(LatLng startLatlng, List<LatLng> passLatlngs, LatLng endLatlng);

    /**
     * 开始送人
     */
    void startOutSet();

    /**
     * 前往预约地
     *
     * @param carpoolOrder
     */
    void gotoStart(CarpoolOrder carpoolOrder);

    /**
     * 到达预约地
     *
     * @param carpoolOrder
     */
    void arriveStart(CarpoolOrder carpoolOrder);

    /**
     * 接到客户
     *
     * @param carpoolOrder
     */
    void acceptCustomer(CarpoolOrder carpoolOrder);

    /**
     * 跳过接客户
     *
     * @param carpoolOrder
     */
    void jumpAccept(CarpoolOrder carpoolOrder);

    /**
     * 到达终点
     *
     * @param carpoolOrder
     */
    void arriveEnd(CarpoolOrder carpoolOrder);

    /**
     * 跳过送客户
     *
     * @param carpoolOrder
     */
    void jumpSend(CarpoolOrder carpoolOrder);

    /**
     * 刷新地图，定位当前位置
     */
    void doRefresh();

    /**
     * 倒计时结束
     */
    void countStartOver();

    /**
     * 开始导航
     *
     * @param latLng
     * @param orderId
     */
    void navi(LatLng latLng, Long orderId);

    /**
     * 购票乘客页面(不用CusListFragment是因为他们是两个不同的页面状态，顶部toolbar的事件都不一样)
     */
    void toPasTickets();

// type 1 弹框 支付订单和取消弹窗
// type 2 弹框 取消订单和取消弹窗
// type 3 直接支付
// type 4 弹框 支付订单和取消订单

    void onDialogClick(int type, long orderId, double money);
}
