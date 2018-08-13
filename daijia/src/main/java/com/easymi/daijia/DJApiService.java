package com.easymi.daijia;

import com.easymi.component.result.EmResult;
import com.easymi.common.entity.PullFeeResult;
import com.easymi.daijia.result.BudgetResult;
import com.easymi.daijia.result.ConsumerResult;
import com.easymi.daijia.result.DJOrderResult;
import com.easymi.daijia.result.DJTypeResult;
import com.easymi.daijia.result.OrderFeeResult;
import com.easymi.daijia.result.PassengerResult;
import com.easymi.daijia.result.SameOrderResult;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by developerLzh on 2017/11/17 0017.
 */

public interface DJApiService {

    /**
     * 查询单个订单
     *
     * @param orderId
     * @param appKey
     * @return
     */
    @GET("driver/api/v1/orderFindOne")
    Observable<DJOrderResult> indexOrders(@Query("order_id") Long orderId,
                                          @Query("app_key") String appKey);

    /**
     * 抢单
     *
     * @param orderId
     * @param driverId
     * @param appKey
     * @return
     */
    @FormUrlEncoded
    @PUT("driver/api/v1/grabOrder")
    Observable<DJOrderResult> grabOrder(@Field("order_id") Long orderId,
                                        @Field("driver_id") Long driverId,
                                        @Field("app_key") String appKey);

    /**
     * 接单
     *
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
     *
     * @param orderId
     * @param driverId
     * @param appKey
     * @return
     */
    @FormUrlEncoded
    @PUT("driver/api/v1/refuseOrder")
    Observable<EmResult> refuseOrder(@Field("order_id") Long orderId,
                                     @Field("driver_id") Long driverId,
                                     @Field("app_key") String appKey,
                                     @Field("remark") String remark);

    /**
     * 司机修改终点
     */
    @FormUrlEncoded
    @PUT("driver/api/v1/modifyDestination")
    Observable<DJOrderResult> changeEnd(@Field("id") Long orderId,
                                        @Field("lat") Double lat,
                                        @Field("lng") Double lng,
                                        @Field("destination") String destination,
                                        @Field("app_key") String appKey
    );

    /**
     * 前往预约地
     *
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
     *
     * @param orderId
     * @param appKey
     * @return
     */
    @FormUrlEncoded
    @POST("driver/api/v1/arrivalBookAddress")
    Observable<DJOrderResult> arrivalBookAddress(@Field("order_id") Long orderId,
                                                 @Field("app_key") String appKey,
                                                 @Field("real_book_address") String realBookAddress,
                                                 @Field("real_book_address_lat") Double realBookLat,
                                                 @Field("real_book_address_lng") Double realBookLng);

    /**
     * 前往目的地
     *
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
     *
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
     *
     * @param orderId
     * @param appKey
     * @return
     */
    @FormUrlEncoded
    @POST("driver/api/v1/arrivalDistination")
    Observable<DJOrderResult> arrivalDistination(@Field("order_id") Long orderId,
                                                 @Field("app_key") String appKey,
                                                 @Field("advance_price") Double advance_price,//垫付
                                                 @Field("other_price") Double other_price,//附加费用
                                                 @Field("remark") String remark,//备注
                                                 @Field("distance") Double distance,
                                                 @Field("distance_fee") Double distance_fee,
                                                 @Field("time") Integer time,
                                                 @Field("time_fee") Double time_fee,
                                                 @Field("wait_time") Integer wait_time,
                                                 @Field("wait_fee") Double wait_fee,
                                                 @Field("add_distance") Double add_distance,
                                                 @Field("add_fee") Double add_fee,
                                                 @Field("coupon_fee") Double coupon_fee,//优惠券
                                                 @Field("total_fee") Double total_fee,//跑出来的钱 + 垫付 + 附加费用 (不算优惠券的钱)
                                                 @Field("real_pay") Double real_pay,//total_fee - 优惠金额 - 预付费
                                                 @Field("start_price") Double start_price,
                                                 @Field("real_destination") String realAddress,
                                                 @Field("real_destination_lat") Double realLat,
                                                 @Field("real_destination_lng") Double realLng,
                                                 @Field("min_cost") Double minCost
    );

    /**
     * 结算订单  /api/v1/finishOrder  PUT  id  int  是  订单id
     * app_key  string  是  系统key
     * pay_type  string  是  支付类型(代付helppay，客户余额支付balance)
     *
     * @param orderId
     * @param appKey
     * @param payType
     * @return
     */
    @FormUrlEncoded
    @PUT("/api/v1/finishOrder")
    Observable<EmResult> payOrder(@Field("id") Long orderId,
                                  @Field("app_key") String appKey,
                                  @Field("pay_type") String payType);

    /**
     * 补单
     *
     * @param passengerId
     * @param passengerName
     * @param passengerPhone
     * @param bookTime
     * @param bookAddress
     * @param bookAddressLat
     * @param bookAddressLng
     * @param destination
     * @param destinationLat
     * @param destinationLng
     * @param companyId
     * @param companyName
     * @param budgetFee
     * @param appKey
     * @param cid            订单类型id
     * @param orderPerson
     * @param orderPersonId
     * @return
     */
    @FormUrlEncoded
    @POST("/driver/api/v1/createOrder")
    Observable<DJOrderResult> createOrder(@Field("passenger_id") Long passengerId,
                                          @Field("passenger_name") String passengerName,
                                          @Field("passenger_phone") String passengerPhone,
                                          @Field("book_time") Long bookTime,
                                          @Field("book_address") String bookAddress,
                                          @Field("book_address_lat") Double bookAddressLat,
                                          @Field("book_address_lng") Double bookAddressLng,
                                          @Field("destination") String destination,
                                          @Field("destination_lat") Double destinationLat,
                                          @Field("destination_lng") Double destinationLng,
                                          @Field("company_id") Long companyId,
                                          @Field("company_name") String companyName,
                                          @Field("budget_fee") Double budgetFee,
                                          @Field("app_key") String appKey,
                                          @Field("cid") Long cid,
                                          @Field("order_person") String orderPerson,
                                          @Field("order_person_id") Long orderPersonId
    );

    /**
     * 预估价格
     *
     * @param passengerId
     * @param companyId
     * @param distance
     * @param time
     * @param orderTime
     * @param channel
     * @param typeId
     * @param appKey
     * @return
     */
    @GET("driver/api/v1/getBudgetPrice")
    Observable<BudgetResult> getBudgetPrice(@Query("passenger_id") Long passengerId,
                                            @Query("company_id") Long companyId,
                                            @Query("distance") Double distance,
                                            @Query("time") Integer time,
                                            @Query("order_time") Long orderTime,
                                            @Query("channel") String channel,
                                            @Query("typeId") Long typeId,
                                            @Query("app_key") String appKey
    );

    /**
     * 获取代驾子类型
     *
     * @param companyId
     * @param business
     * @param appKey
     * @return
     */
    @GET("driver/api/v1/getBusiness")
    Observable<DJTypeResult> getBusiness(@Query("company_id") Long companyId,
                                         @Query("business") String business,
                                         @Query("app_key") String appKey
    );

    @GET("api/v1/passengerMustBe")
    Observable<PassengerResult> queryPassenger(@Query("company_id") Long companyId,
                                               @Query("company_name") String companyName,
                                               @Query("phone") String phone,
                                               @Query("app_key") String appKey,
                                               @Query("channel") String channel
    );

    /**
     * 销单
     *
     * @param orderId
     * @param driverId
     * @param appKey
     * @return
     */
    @FormUrlEncoded
    @PUT("driver/api/v1/cancelOrder")
    Observable<EmResult> cancelOrder(@Field("order_id") Long orderId,
                                     @Field("driver_id") Long driverId,
                                     @Field("app_key") String appKey,
                                     @Field("remark") String remark);

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
     * 获取订单计费信息
     *
     * @param orderId
     * @param driverId
     * @param orderType
     * @return
     */
    @GET("driver/api/v1/arrivalCost")
    Observable<OrderFeeResult> getOrderFee(@Query("order_id") Long orderId,
                                           @Query("driver_id") Long driverId,
                                           @Query("order_type") String orderType,
                                           @Query("app_key") String appKey,
                                           @Query("lat") Double lat,
                                           @Query("lng") Double lng,
                                           @Query("is_arrive") Integer isArrive);


    /**
     * 通过接口方式推送最后一个点到后台.
     */
    @FormUrlEncoded
    @POST("api/v1/pullfee")
    Observable<PullFeeResult> pullFee(@Field("gps") String json,
                                      @Field("app_key") String appKey);

}
