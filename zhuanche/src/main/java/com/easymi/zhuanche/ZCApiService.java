package com.easymi.zhuanche;

import com.easymi.common.entity.PullFeeResult;
import com.easymi.common.result.CreateOrderResult;
import com.easymi.component.result.EmResult;
import com.easymi.zhuanche.entity.TransferList;
import com.easymi.zhuanche.result.BudgetResult;
import com.easymi.zhuanche.result.ConsumerResult;
import com.easymi.zhuanche.result.PassengerResult;
import com.easymi.zhuanche.result.SameOrderResult;
import com.easymi.zhuanche.result.ZCOrderResult;
import com.easymi.zhuanche.result.ZCTypeResult;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: ZCApiService
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description: 专车接口地址
 * History:
 */

public interface ZCApiService {

    /**
     * 司机修改终点
     */
    @FormUrlEncoded
    @PUT("driver/api/v1/modifyDestination")
    Observable<ZCOrderResult> changeEnd(@Field("id") Long orderId,
                                        @Field("lat") Double lat,
                                        @Field("lng") Double lng,
                                        @Field("destination") String destination,
                                        @Field("app_key") String appKey);

    /**
     * 开始中途等待
     *
     * @param orderId
     * @param appKey
     * @return
     */
    @FormUrlEncoded
    @POST("driver/api/v1/waitSpecialOrder")
    Observable<ZCOrderResult> waitOrder(@Field("order_id") Long orderId,
                                        @Field("app_key") String appKey);

    /**
     * 同单司机
     *
     * @param groupId
     * @param appKey
     * @return
     */
    @GET("driver/api/v1/groupDrivers")
    Observable<SameOrderResult> getSameOrderDriver(@Query("group_id") String groupId,
                                                   @Query("app_key") String appKey);

    /**
     * 客户信息
     *
     * @param orderId
     * @param appKey
     * @return
     */
    @GET("driver/api/v1/passengerInfo")
    Observable<ConsumerResult> getConsumer(@Query("order_id") Long orderId,
                                           @Query("app_key") String appKey);


    /**
     * 通过接口方式推送最后一个点到后台.
     */
    @FormUrlEncoded
    @POST("api/v1/pullfee")
    Observable<PullFeeResult> pullFee(@Field("gps") String json,
                                      @Field("app_key") String appKey);


    /**
     * 查询可转单司机.
     */
    @GET("driver/api/v1/engineChangeSpecialEmploies")
    Observable<TransferList> getTransferList(@Query("lat") double lat,
                                             @Query("lng") double lng,
                                             @Query("distance") float distance,
                                             @Query("app_key") String appKey,
                                             @Query("model_id") long modelId);


    @FormUrlEncoded
    @PUT("api/v1/specialChangeOrder")
    Observable<EmResult> changeOrder(@Field("id") long orderId,
                                     @Field("employ_id") long employId,
                                     @Field("app_key") String appKey);


//hf

    /**
     * 专车 --> 查询单个订单
     *
     * @param appKey
     * @return
     */
    @GET("api/v1/taxi_online/order/get/{id}")
    Observable<ZCOrderResult> indexOrders(@Path("id") Long id,
                                          @Query("appKey") String appKey);

    /**
     * 专车 -->接单
     *
     * @param driverId
     * @param driverName
     * @param driverPhone
     * @param id          订单id
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/taxi_online/order/take")
    Observable<ZCOrderResult> takeOrder(@Field("driverId") Long driverId,
                                        @Field("driverName") String driverName,
                                        @Field("driverPhone") String driverPhone,
                                        @Field("id") Long id,
                                        @Field("version") Long version,
                                        @Field("receiptLongitude") String receiptLongitude,
                                        @Field("receiptLatitude") String receiptLatitude);

    /**
     * 前往预约地
     *
     * @param id
     * @param appKey
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/taxi_online/order/goto_book_place")
    Observable<ZCOrderResult> goToBookAddress(@Field("id") Long id,
                                              @Field("app_key") String appKey,
                                              @Field("version") Long version);

    /**
     * 到达预约地
     *
     * @param id
     * @param appKey
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/taxi_online/order/arrive_book_place")
    Observable<ZCOrderResult> arrivalBookAddress(@Field("id") Long id,
                                                 @Field("app_key") String appKey,
                                                 @Field("version") Long version);

    /**
     * 前往目的地
     *
     * @param id
     * @param appKey
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/taxi_online/order/goto_destination")
    Observable<ZCOrderResult> goToDistination(@Field("id") Long id,
                                              @Field("app_key") String appKey,
                                              @Field("version") Long version,
                                              @Field("longitude") Double longitude,
                                              @Field("latitude") Double latitude,
                                              @Field("detailAddress") String detailAddress);

    /**
     * 到达目的地
     *
     * @param orderId
     * @param appKey
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/taxi_online/order/arrive_destination")
    Observable<ZCOrderResult> arrivalDistination(@Field("id") Long orderId,
                                                 @Field("app_key") String appKey,
                                                 @Field("version") Long version,
                                                 @Field("longitude") Double longitude,
                                                 @Field("latitude") Double latitude,
                                                 @Field("detailAddress") String detailAddress);

    /**
     * 结算订单
     * pay_type  string
     *
     * @param orderId
     * @param channel
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/public/pay/order")
    Observable<EmResult> payOrder(@Field("id") Long orderId,
                                  @Field("channel") String channel);

    /**
     * 根据电话验证客户是新客户还是老客户  根据电话号码查询客户信息，如果客户不存在，新建客户信息
     *
     * @param phone
     * @return
     */
    @GET("api/v1/taxi_online/order/getPassengerByPhone")
    Observable<PassengerResult> queryPassenger(@Query("phone") String phone);


    /**
     * 预算费用
     *
     * @param businessId
     * @param companyId
     * @param distance
     * @param time
     * @param modelId
     * @return
     */
    @GET("api/v1/taxi_online/order/budget")
    Observable<BudgetResult> getBudgetPrice(@Query("businessId") Long businessId,
                                            @Query("companyId") Long companyId,
                                            @Query("distance") Double distance,
                                            @Query("time") Integer time,
                                            @Query("modelId") Long modelId);

    /**
     * 根据公司id获取业务配置信息（获取专车子类型）
     *
     * @param
     * @return
     */
    @GET("api/v1/taxi_online/business_config/driver/business")
    Observable<ZCTypeResult> getZCBusiness(@Query("adcode") String adcode,
                                           @Query("citycode") String citycode,
                                           @Query("carModel") int carModel,
                                           @Query("lat") double lat,
                                           @Query("lng") double lng);

    /**
     *  补单
     * @param bookTime
     * @param budgetFee
     * @param businessId
     * @param channelAlias
     * @param companyId
     * @param driverId
     * @param driverName
     * @param driverPhone
     * @param modelId
     * @param orderAddress
     * @param passengerId
     * @param passengerName
     * @param passengerPhone
     * @param serviceType
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/taxi_online/order/driver/create")
    Observable<CreateOrderResult> createOrder(@Field("bookTime") Long bookTime,
                                              @Field("budgetFee") Double budgetFee,
                                              @Field("businessId") Long businessId,
                                              @Field("channelAlias") String channelAlias,
                                              @Field("companyId") Long companyId,
                                              @Field("driverId") Long driverId,
                                              @Field("driverName") String driverName,
                                              @Field("driverPhone") String driverPhone,
                                              @Field("modelId") Long modelId,
                                              @Field("orderAddress") String orderAddress,
                                              @Field("passengerId") Long passengerId,
                                              @Field("passengerName") String passengerName,
                                              @Field("passengerPhone") String passengerPhone,
                                              @Field("serviceType") String serviceType,
                                              @Field("onePrice") boolean onePrice,
                                              @Field("time") Integer time,
                                              @Field("distance") Double distance);


}
