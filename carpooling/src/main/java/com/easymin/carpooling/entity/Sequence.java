package com.easymin.carpooling.entity;

import java.io.Serializable;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 * @Author: hufeng
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class Sequence implements Serializable{
    /**
     * 1 数字 2 文字 3 图片
     */
    public int type;

    /**
     * 数字编号
     */
    public int num;
    /**
     * 文字内容
     */
    public String text;

    /**
     * 头像
     */
    public String photo;

    /**
     * 票数
     */
    public int ticketNumber;

    /**
     * 状态   0 未接 1 已接 2 跳过接 3 未送 4 已送 5 跳过送
     */
    public int status;

    /**
     * 订单顺序序号
     */
    public int sort;

    /**
     * 站点状态 1上车站点  2 下车站点 3途径点
     */
    public int stationStatus;

    /**
     * 订单id
     */
    public long orderId;

    /**
     * 上车下标
     */
    public int startIndex;


    /**
     * 下车下标
     */
    public int endIndex;

    /**
     * 是否是终点或者起点
     */
    public boolean isStartOrEnd;

}
