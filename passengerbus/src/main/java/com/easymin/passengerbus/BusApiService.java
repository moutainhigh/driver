package com.easymin.passengerbus;

import com.easymi.component.result.EmResult2;
import com.easymin.passengerbus.entity.BusStationResult;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
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

    /**
     * 班次站点查询
     * @param scheduleId
     * @return
     */
    @GET("api/v1/bus/country/driver/schedule/stations")
    Observable<Object> findStations(@Query("scheduleId") long scheduleId);


    /**
     * 开始班次
     * @param scheduleId
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/bus/country/driver/schedule/start")
    Observable<EmResult2<Object>> start(@Field("scheduleId") long scheduleId);


    /**
     * 到达某个站点
     * @param scheduleId
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/bus/country/driver/schedule/arrive")
    Observable<EmResult2<Object>> arriveStation(@Field("scheduleId") long scheduleId,
                                        @Field("stationId") long stationId);


    /**
     * 结束班次
     * @param scheduleId
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/bus/country/driver/schedule/finish")
    Observable<EmResult2<Object>> finish(@Field("scheduleId") long scheduleId);

}
