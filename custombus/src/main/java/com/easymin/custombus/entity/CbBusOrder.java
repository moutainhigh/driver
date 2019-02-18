package com.easymin.custombus.entity;

import java.util.List;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: CbBusOrder
 * @Author: hufeng
 * @Date: 2019/2/18 下午2:25
 * @Description: 定制班车班次实体
 * @History:
 */
public class CbBusOrder {

    /**
     * 班次状态 1 未开始行程 2开始行程执行中 3 到达终点站
     */
    public int status;
    public String startAddr;
    public String endAddr;
    public List<Station> siteList;
    public long booktime;
}
