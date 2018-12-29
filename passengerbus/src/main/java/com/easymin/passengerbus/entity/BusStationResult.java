package com.easymin.passengerbus.entity;

import com.easymi.component.result.EmResult;

import java.io.Serializable;
import java.util.List;

/**
 * 单个班车站点信息
 */
public class BusStationResult extends EmResult implements Serializable{

    /**
     * "id": 9,
     "companyId": 1,
     "name": "站点1到站点24(站点2,站点3,站点4,站点5,站点6,站点7,站点8,站点9,站点10,站点11,站点12,站点13,站点14,站点15,站点16,站点17,站点18,站点19,站点20,站点21,站点22,站点23)",
     "shortName": "站点1到站点24",
     "startStation": "站点1",
     "endStation": "站点24",
     "minute": 66,
     "fullTicket": 1,
     "fullDistance": 62.75,
     "schedule": null,
     "scheduleNumber": 7,
     "impose": 2,
     "created": 1545462920,
     "updated": 1545468151,
     "appKey": "1HAcient1kLqfeX7DVTV0dklUkpGEnUC",
     */

    public String startStation;
    public String endStation;
    //只是班次
    public long id;

    public List<BusStationsBean> stations;

}
