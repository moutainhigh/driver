package com.easymi.cityline;

import com.easymi.cityline.entity.OrderCustomer;
import com.easymi.cityline.entity.ZXOrder;
import com.easymi.cityline.result.OrderCustomerResult;
import com.easymi.cityline.result.PriceResult;
import com.easymi.cityline.result.StationResult;
import com.easymi.cityline.result.ZxOrderResult;
import com.easymi.component.result.EmResult;
import com.easymi.component.result.EmResult2;

import java.util.List;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by liuzihao on 2018/11/14.
 */

public interface CLService {
    /**
     * 查询班次
     *
     * @return
     */
    @GET("/api/v1/bus/city/schedule/queryDriverSchedule")
    Observable<EmResult2<List<ZXOrder>>> queryDriverSchedule();

    /**
     * 开始送人
     *
     * @param scheduleId
     * @return
     */
    @FormUrlEncoded
    @POST("/api/v1/bus/city/schedule/runSchedule")
    Observable<EmResult2<Object>> runSchedule(
            @Field("scheduleId") long scheduleId);

    /**
     * 开始接人
     *
     * @param scheduleId
     * @return
     */
    @FormUrlEncoded
    @POST("/api/v1/bus/city/schedule/startSchedule")
    Observable<EmResult2<Object>> startSchedule(
            @Field("scheduleId") long scheduleId);

    /**
     * 查询班次订单
     *
     * @param scheduleId
     * @return
     */
    @GET("/api/v1/bus/city/order/list")
    Observable<EmResult2<List<OrderCustomer>>> getOrderCustomers(
            @Query("scheduleId") long scheduleId);

    /**
     * 班次结束
     *
     * @param scheduleId
     * @return
     */
    @FormUrlEncoded
    @POST("/api/v1/bus/city/schedule/finishSchedule")
    Observable<EmResult2<Object>> finishTask(
            @Field("scheduleId") long scheduleId);

    /**
     * 到达预约地
     *
     * @param orderId
     * @return
     */
    @FormUrlEncoded
    @POST("/api/v1/bus/city/order/take")
    Observable<EmResult2<Object>> arriveStart(
            @Field("orderId") long orderId);


    /**
     * 接到乘客
     *
     * @param orderId
     * @return
     */
    @FormUrlEncoded
    @POST("/api/v1/bus/city/order/run")
    Observable<EmResult2<Object>> acceptCustomer(
            @Field("orderId") long orderId);

    /**
     * 跳过接乘客
     *
     * @param orderId
     * @return
     */
    @FormUrlEncoded
    @POST("/api/v1/bus/city/order/skip")
    Observable<EmResult2<Object>> jumpCustomer(
            @Field("orderId") long orderId);

    /**
     * 送到
     *
     * @param orderId
     * @return
     */
    @FormUrlEncoded
    @POST("/api/v1/bus/city/order/finish")
    Observable<EmResult2<Object>> sendCustomer(
            @Field("orderId") long orderId);

    /**
     * 获取单价
     *
     * @param endStationId
     * @param lineId
     * @param startStationId
     * @return
     */
    @GET("/api/v1/bus/city/order/price")
    Observable<EmResult2<PriceResult>> getPrice(@Query("endStationId") long endStationId,
                                                @Query("lineId") long lineId,
                                                @Query("startStationId") long startStationId);

    /**
     * 补单接口
     * @param bookTime
     * @param channelAlias
     * @param endStationId
     * @param orderAddress
     * @param passengerPhone
     * @param scheduleId
     * @param startStationId
     * @param ticketNumber
     * @return
     */
    @FormUrlEncoded
    @POST("/api/v1/bus/city/order/save")
    Observable<EmResult2<Object>> createOrder(@Field("bookTime") long bookTime,
                                              @Field("channelAlias") String channelAlias,
                                              @Field("endStationId") long endStationId,
                                              @Field("orderAddress") String orderAddress,
                                              @Field("passengerPhone") String passengerPhone,
                                              @Field("scheduleId") long scheduleId,
                                              @Field("startStationId") long startStationId,
                                              @Field("ticketNumber") int ticketNumber);

    /**
     * 根据班次查询起终站点信息
     *
     * @param scheduleId
     * @return
     */
    @GET("/api/v1/bus/city/station/queryByScheduleId")
    Observable<StationResult> getStationResult(@Query("scheduleId") long scheduleId);
}
