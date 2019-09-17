package com.easymin.carpooling.entity;

import com.easymi.common.entity.CarpoolOrder;

import java.util.List;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: MyStation
 * @Author: hufeng
 * @Date: 2019/9/17 下午9:55
 * @Description:
 * @History:
 */
public class MyStation {

    /**
     * 站点id
     */
    public long stationId;

    /**
     * 站点名字
     */
    public String name;

    /**
     * 站点的顺序
     */
    public int stationSequence;

    /**
     * 执行的订单下标
     */
    public int orderSequence;

    /**
     * 站点订单列表
     */
    public List<CarpoolOrder> stationOrderVoList;
}
