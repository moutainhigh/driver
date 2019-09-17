package com.easymin.carpooling.entity;

import com.easymi.component.result.EmResult;

import java.util.List;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: AllStation
 * @Author: hufeng
 * @Date: 2019/9/17 下午9:40
 * @Description:
 * @History:
 */
public class AllStation{

    public long scheduleId;
    public int scheduleStatus;
    public long currentStationId;
    public List<MyStation> scheduleStationVoList;

}
