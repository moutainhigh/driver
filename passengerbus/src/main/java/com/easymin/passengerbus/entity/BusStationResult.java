package com.easymin.passengerbus.entity;

import com.easymi.component.result.EmResult;

import java.io.Serializable;
import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: BusStationResult
 *@Author: shine
 * Date: 2018/12/24 下午1:10
 * Description: 单个班车站点信息
 * History:
 */
public class BusStationResult extends EmResult implements Serializable{

    /**
     * "id":69,
     "lineId":11,
     "lineName":"金沙到温江客运",
     "driverId":98,
     "driverName":"Wade",
     "driverPhone":"15682387777",
     "carId":50,
     "vehicleNo":"京B11111",
     "seats":3,
     "saleSeat":0,
     "day":"2018-12-25",
     "hour":"13:00",
     "time":1545714000,
     "minute":2,
     "appKey":"1HAcient1kLqfeX7DVTV0dklUkpGEnUC",
     "status":1,
     "advanceMinute":0,
     "totalMoney":0,
     "scheduleStatusVos":null,
     */
    public long id;

    public long lineId;

    public List<BusStationsBean> stationVos;

}
