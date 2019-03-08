package com.easymin.custombus;

import com.easymi.component.result.EmResult2;
import com.easymin.custombus.entity.Customer;
import com.easymin.custombus.entity.OrdersResult;
import com.easymin.custombus.entity.StationResult;
import com.easymin.custombus.entity.TimeResult;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: DZBusApiService
 * @Author: hufeng
 * @Date: 2019/2/14 下午1:52
 * @Description: 定制班车接口地址
 * @History:
 */
public interface DZBusApiService {

    /**
     * 查询班次完整信息(包含站点)
     */
    @GET("/api/v1/bus/country/driver/order/querySchedule")
    Observable<StationResult> findBusInfoById(@Query("scheduleId") long scheduleId);

    /**
     * 开始班次
     *
     * @param scheduleId
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/bus/country/driver/order/startSchedule")
    Observable<EmResult2<Object>> start(@Field("scheduleId") long scheduleId);

    /**
     * 开始验票
     */
    @FormUrlEncoded
    @POST("/api/v1/bus/country/driver/order/startCheck")
    Observable<TimeResult> startCheck(@Field("scheduleId") long scheduleId);

    /**
     * 根据班次id和站点id查询对应站点的订单详情
     */
    @GET("/api/v1/bus/country/driver/order/queryScheduleDetail")
    Observable<OrdersResult> queryOrders(@Query("scheduleId") long scheduleId,
                                         @Query("stationId") long stationId);

    /**
     * 根据乘车码查询订单详情
     */
    @GET("/api/v1/bus/country/driver/order/queryByRideCode")
    Observable<EmResult2<Customer>> queryByRideCode(@Query("rideCode") String rideCode);

    /**
     * 检查乘车码
     */
    @FormUrlEncoded
    @POST("/api/v1/bus/country/driver/order/checkRideCode")
    Observable<EmResult2<Object>> checkRideCode(@Field("rideCode") String rideCode);

    /**
     * 前往下一个站点
     *
     * @param scheduleId
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/bus/country/driver/order/gotoNextStation")
    Observable<EmResult2<Object>> toNextStation(@Field("scheduleId") long scheduleId,
                                                @Field("nextStationId") long nextStationId);

    /**
     * 到达某个站点
     *
     * @param scheduleId
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/bus/country/driver/order/arrivedStation")
    Observable<EmResult2<Object>> arriveStation(@Field("scheduleId") long scheduleId);

    /**
     * 结束班次
     *
     * @param scheduleId
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/bus/country/driver/order/finishSchedule")
    Observable<EmResult2<Object>> finish(@Field("scheduleId") long scheduleId);




}
