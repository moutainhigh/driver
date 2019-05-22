package com.easymin.carpooling;

import com.easymi.common.entity.CarpoolOrder;
import com.easymi.common.entity.OrderCustomer;
import com.easymi.component.result.EmResult2;
import com.easymin.carpooling.entity.PincheOrder;

import java.util.List;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: CarPoolApiService
 * @Author: hufeng
 * @Date: 2019/2/19 上午11:58
 * @Description:
 * @History:
 */
public interface CarPoolApiService {

    /**
     * 查询指定班次所有订单
     *
     * @param scheduleId
     * @return
     */
    @GET("/api/v1/carpool/driver/order")
    Observable<EmResult2<List<CarpoolOrder>>> getOrderCustomers(@Query("scheduleId") long scheduleId, @Query("appKey") String appKey);

    /**
     * 开始送人
     *
     * @param scheduleId
     * @return
     */
    @FormUrlEncoded
    @POST("/api/v1/carpool/driver/schedule/runSchedule")
    Observable<EmResult2<Object>> runSchedule(
            @Field("scheduleId") long scheduleId);

    /**
     * 开始班次(开始接人)
     *
     * @param scheduleId
     * @return
     */
    @FormUrlEncoded
    @POST("/api/v1/carpool/driver/schedule/startSchedule")
    Observable<EmResult2<Object>> startSchedule(
            @Field("scheduleId") long scheduleId);

    /**
     * 班次结束
     *
     * @param scheduleId
     * @return
     */
    @FormUrlEncoded
    @POST("/api/v1/carpool/driver/schedule/finishSchedule")
    Observable<EmResult2<Object>> finishTask(
            @Field("scheduleId") long scheduleId);


    /**
     * 前往预约地
     *
     * @param orderId
     * @return
     */
    @FormUrlEncoded
    @POST("/api/v1/carpool/driver/order/gotoStartAddress")
    Observable<EmResult2<Object>> gotoStart(
            @Field("orderId") long orderId);

    /**
     * 到达预约地(到达订单预约地)
     *
     * @param orderId
     * @return
     */
    @FormUrlEncoded
    @POST("/api/v1/carpool/driver/order/arriveStartAddress")
    Observable<EmResult2<Object>> arriveStart(
            @Field("orderId") long orderId);

    /**
     * 接到乘客(出发订单)
     *
     * @param orderId
     * @return
     */
    @FormUrlEncoded
    @POST("/api/v1/carpool/driver/order/runningOrder")
    Observable<EmResult2<Object>> acceptCustomer(
            @Field("orderId") long orderId);

    /**
     * 跳过接乘客(跳过订单)
     *
     * @param orderId
     * @return
     */
    @FormUrlEncoded
    @POST("/api/v1/carpool/driver/order/skipOrder")
    Observable<EmResult2<Object>> jumpCustomer(
            @Field("orderId") long orderId);

    /**
     * 送到(订单结束(到达目的地))
     *
     * @param orderId
     * @return
     */
    @FormUrlEncoded
    @POST("/api/v1/carpool/driver/order/finishOrder")
    Observable<EmResult2<Object>> sendCustomer(
            @Field("orderId") long orderId);


    /**
     * 查询司机关联班次
     *
     * @return
     */
    @GET("/api/v1/carpool/driver/schedule/getDriverSchedule")
    Observable<EmResult2<List<PincheOrder>>> queryDriverSchedule(@Query("id") long driverId);
}
