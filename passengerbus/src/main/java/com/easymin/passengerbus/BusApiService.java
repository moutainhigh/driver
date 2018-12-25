package com.easymin.passengerbus;

import com.easymi.component.result.EmResult2;
import com.easymin.passengerbus.entity.BusStationResult;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: BusApiService
 * Author: shine
 * Date: 2018/12/18 下午1:30
 * Description:
 * History:
 */
public interface BusApiService {

//    /**
//     * 查询单个订单
//     * @param id
//     * @return
//     */
//    @GET("api/v1/bus/country/passenger/line/{id}")
//    Observable<SettingResult> getAppSetting(@Path("id") long id);


    @GET("api/v1/bus/country/driver/line/{id}")
    Observable<EmResult2<BusStationResult>> findBusInfoById(@Path("id") long id);
}
