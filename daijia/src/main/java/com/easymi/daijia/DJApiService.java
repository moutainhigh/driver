package com.easymi.daijia;

import com.easymi.daijia.result.DJOrderResult;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by developerLzh on 2017/11/17 0017.
 */

public interface DJApiService {

    /**
     * 查询单个订单
     * @param orderId
     * @param appKey
     * @return
     */
    @GET("driver/api/v1/orderFindOne")
    Observable<DJOrderResult> indexOrders(@Query("order_id") Long orderId,
                                          @Query("app_key") String appKey);

    /**
     * 接单
     * @param orderId
     * @param driverId
     * @param appKey
     * @return
     */
    @FormUrlEncoded
    @POST("driver/api/v1/takeOrder")
    Observable<DJOrderResult> takeOrder(@Field("order_id") Long orderId,
                                        @Field("driver_id") Long driverId,
                                        @Field("app_key") String appKey);

    /**
     * 拒单
     * @param orderId
     * @param driverId
     * @param appKey
     * @return
     */
    @FormUrlEncoded
    @POST("driver/api/v1/refuseOrder")
    Observable<DJOrderResult> refuseOrder(@Field("order_id") Long orderId,
                                        @Field("driver_id") Long driverId,
                                        @Field("app_key") String appKey,
                                          @Field("remark")String remark);

    /**
     * 前往预约地
     * @param orderId
     * @param appKey
     * @return
     */
    @FormUrlEncoded
    @POST("driver/api/v1/goToBookAddress")
    Observable<DJOrderResult> goToBookAddress(@Field("order_id") Long orderId,
                                        @Field("app_key") String appKey);
    /**
     * 到达预约地
     * @param orderId
     * @param appKey
     * @return
     */
    @FormUrlEncoded
    @POST("driver/api/v1/arrivalBookAddress")
    Observable<DJOrderResult> arrivalBookAddress(@Field("order_id") Long orderId,
                                        @Field("app_key") String appKey);
    /**
     * 前往目的地
     * @param orderId
     * @param appKey
     * @return
     */
    @FormUrlEncoded
    @POST("driver/api/v1/goToDistination")
    Observable<DJOrderResult> goToDistination(@Field("order_id") Long orderId,
                                        @Field("app_key") String appKey);
    /**
     * 开始中途等待
     * @param orderId
     * @param appKey
     * @return
     */
    @FormUrlEncoded
    @POST("driver/api/v1/waitOrder")
    Observable<DJOrderResult> waitOrder(@Field("order_id") Long orderId,
                                        @Field("app_key") String appKey);
    /**
     * 到达目的地
     * @param orderId
     * @param appKey
     * @return
     */
    @FormUrlEncoded
    @POST("driver/api/v1/arrivalDistination")
    Observable<DJOrderResult> arrivalDistination(@Field("order_id") Long orderId,
                                        @Field("app_key") String appKey);
}
