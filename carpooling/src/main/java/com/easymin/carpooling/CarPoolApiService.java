package com.easymin.carpooling;

import com.easymi.common.entity.CarpoolOrder;
import com.easymi.component.result.EmResult;
import com.easymi.component.result.EmResult2;
import com.easymin.carpooling.entity.AllStation;
import com.easymin.carpooling.entity.LineBean;
import com.easymin.carpooling.entity.PincheOrder;
import com.easymin.carpooling.entity.PriceResult;
import com.easymin.carpooling.entity.StationResult;

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
    @GET("api/v1/carpool/driver/order")
    Observable<EmResult2<List<CarpoolOrder>>> getOrderCustomers(@Query("scheduleId") long scheduleId, @Query("appKey") String appKey);

    /**
     * 开始送人
     *
     * @param scheduleId
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/carpool/driver/schedule/runSchedule")
    Observable<EmResult2<Object>> runSchedule(
            @Field("scheduleId") long scheduleId);

    /**
     * 开始班次(开始接人)
     *
     * @param scheduleId
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/carpool/driver/schedule/startSchedule")
    Observable<EmResult2<Object>> startSchedule(
            @Field("scheduleId") long scheduleId);

    /**
     * 班次结束
     *
     * @param scheduleId
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/carpool/driver/schedule/finishSchedule")
    Observable<EmResult2<Object>> finishTask(
            @Field("scheduleId") long scheduleId);


    /**
     * 前往预约地
     *
     * @param orderId
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/carpool/driver/order/gotoStartAddress")
    Observable<EmResult2<Object>> gotoStart(
            @Field("orderId") long orderId);

    /**
     * 到达预约地(到达订单预约地)
     *
     * @param orderId
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/carpool/driver/order/arriveStartAddress")
    Observable<EmResult2<Object>> arriveStart(
            @Field("orderId") long orderId);

    /**
     * 接到乘客(出发订单)
     *
     * @param orderId
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/carpool/driver/order/runningOrder")
    Observable<EmResult2<Object>> acceptCustomer(
            @Field("orderId") long orderId);

    /**
     * 跳过接乘客(跳过订单)
     *
     * @param orderId
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/carpool/driver/order/skipOrder")
    Observable<EmResult2<Object>> jumpCustomer(
            @Field("orderId") long orderId);

    /**
     * 送到(订单结束(到达目的地))
     *
     * @param orderId
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/carpool/driver/order/finishOrder")
    Observable<EmResult2<Object>> sendCustomer(
            @Field("orderId") long orderId);


    /**
     * 查询司机关联班次
     *
     * @return
     */
    @GET("api/v1/carpool/driver/schedule/getDriverSchedule")
    Observable<EmResult2<List<PincheOrder>>> queryDriverSchedule(@Query("id") long driverId);


    /**
     * 根据班次查询电子围栏
     *
     * @param scheduleId
     * @return
     */
    @GET("api/v1/carpool/driver/schedule/getStation")
    Observable<StationResult> getStationResult(@Query("scheduleId") long scheduleId);

    /**
     * 获取单价
     *
     * @param endStationId
     * @param lineId
     * @param startStationId
     * @return
     */
    @GET("api/v1/carpool/passenger/order/price")
    Observable<PriceResult> getPrice(@Query("endStationId") long endStationId,
                                     @Query("lineId") long lineId,
                                     @Query("startStationId") long startStationId,
                                     @Query("startCoordinateId") long startCoordinateId,
                                     @Query("endCoordinateId") long endCoordinateId);

    /**
     * 拼车下单接口
     *
     * @param companyId      公司ID
     * @param startStationId 起点站ID
     * @param endStationId   终点站ID
     * @param scheduleId     班次ID
     * @param ticketNumber   票数(人数)
     * @param passengerPhone 乘客电话
     * @param channelAlias   订单渠道
     * @param timeSlotId     服务时间段ID
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/carpool/driver/order/create")
    Observable<EmResult2<Long>> createOrder(@Field("companyId") long companyId,
                                            @Field("orderAddress") String orderAddress,
                                            @Field("startStationId") long startStationId,
                                            @Field("endStationId") long endStationId,
                                            @Field("scheduleId") Long scheduleId,
                                            @Field("ticketNumber") int ticketNumber,
                                            @Field("passengerPhone") String passengerPhone,
                                            @Field("channelAlias") String channelAlias,
                                            @Field("timeSlotId") long timeSlotId,
                                            @Field("passengerInfos") String passengerInfos,
                                            @Field("sorts") String sorts,
                                            @Field("startCoordinateId")long startCoordinateId,
                                            @Field("endCoordinateId")long endCoordinateId);

    /**
     * 指派订单
     *
     * @param orderIds   订单ID
     * @param timeSlotId 服务时间段ID
     * @param driverId   司机ID
     * @param seats      座位数
     * @param totalMoney 订单总金额
     * @param saleSeat   已售票数
     * @param source     已有运力1 空闲运力2   补单2
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/carpool/driver/order/assign")
    Observable<EmResult> assginOrder(@Field("orderIds") long orderIds,
                                     @Field("timeSlotId") long timeSlotId,
                                     @Field("driverId") long driverId,
                                     @Field("seats") long seats,
                                     @Field("totalMoney") double totalMoney,
                                     @Field("saleSeat") long saleSeat,
                                     @Field("source") long source,
                                     @Field("appKey") String appKey);


////////////////流程更改迭代新增接口

    /**
     * 查询当前站点的全部信息
     *
     * @param scheduleId
     * @return
     */
    @GET("api/v1/carpool/driver/schedule/allStationData")
    Observable<EmResult2<AllStation>> qureyScheduleInfo(@Query("scheduleId") long scheduleId);


    /**
     * 司机端修改订单执行顺序
     *
     * @param orderIdSequence
     * @return
     */
    @GET("api/v1/carpool/driver/schedule/changeOrderSequence")
    Observable<EmResult2<Object>> changeOrderSequence(@Query("orderIdSequence") String orderIdSequence);

    /**
     * 查询司机可补单的线路及其班次
     *
     * @param id
     * @param companyId
     * @return
     */
    @GET("api/v1/carpool/driver/schedule/getLineDriverSchedule")
    Observable<EmResult2<List<LineBean>>> getLineDriverSchedule(@Query("id") long id, @Query("companyId") long companyId);


    /**
     * 根据时间段id查询电子围栏
     *
     * @param timeSlotId
     * @return
     */
    @GET("api/v1/carpool/driver/schedule/getStationByTimeSlotId")
    Observable<StationResult> qureyStationResult(@Query("timeSlotId") long timeSlotId);

    /**
     * 根据班次id或者不传班次id查询当前班次或者司机可补单的最大票数
     *
     * @param scheduleId
     * @return
     */
    @GET("api/v1/carpool/driver/schedule/getSeats")
    Observable<EmResult2<Integer>> getSeats(@Query("scheduleId") Long scheduleId);
}
