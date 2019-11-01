package com.easymin.carpooling.entity;

import java.io.Serializable;
import java.util.List;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: LineBean
 * @Author: hufeng
 * @Date: 2019/10/31 下午2:15
 * @Description:
 * @History:
 */
public class LineBean implements Serializable {

    /**
     * 线路id
     */
    public long lineId;

    /**
     * 线路名称
     */
    public String lineName;

    /**
     * 线路下的班次时间段
     */
    public List<TimeSlotBean> timeSlotVoList;
}
