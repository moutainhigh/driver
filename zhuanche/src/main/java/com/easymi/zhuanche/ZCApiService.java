package com.easymi.zhuanche;

import com.easymi.common.entity.PullFeeResult;
import com.easymi.component.result.EmResult;
import com.easymi.zhuanche.result.BudgetResult;
import com.easymi.zhuanche.result.ConsumerResult;
import com.easymi.zhuanche.result.ZCOrderResult;
import com.easymi.zhuanche.result.ZCTypeResult;
import com.easymi.zhuanche.result.PassengerResult;
import com.easymi.zhuanche.result.SameOrderResult;

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

public interface ZCApiService {

    /**
     * 查询单个订单
     *
     * @param orderId
     * @param appKey
     * @return
     */
    @GET("driver/api/v1/orderFindOne")
    Observable<ZCOrderResult> indexOrders(@Query("order_id") Long orderId,
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
    @PUT("driver/api/v1/grabSpecialOrder")
    Observable<ZCOrderResult> grabOrder(@Field("order_id") Long orderId,
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
    @POST("/driver/api/v1/takeSpecialOrder")
    Observable<ZCOrderResult> takeOrder(@Field("order_id") Long orderId,
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
    @PUT("driver/api/v1/refuseSpecialOrder")
    Observable<EmResult> refuseOrder(@Field("order_id") Long orderId,
                                     @Field("driver_id") Long driverId,
                                     @Field("app_key") String appKey,
                                     @Field("remark") String remark);

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
     * 前往预约地
     *
     * @param orderId
     * @param appKey
     * @return
     */
    @FormUrlEncoded
    @POST("/driver/api/v1/goToSpecialBookAddress")
    Observable<ZCOrderResult> goToBookAddress(@Field("order_id") Long orderId,
                                              @Field("app_key") String appKey);

    /**
     * 到达预约地
     *
     * @param orderId
     * @param appKey
     * @return
     */
    @FormUrlEncoded
    @POST("driver/api/v1/arrivalSpecialBookAddress")
    Observable<ZCOrderResult> arrivalBookAddress(@Field("order_id") Long orderId,
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
    @POST("driver/api/v1/goToSpecialDistination")
    Observable<ZCOrderResult> goToDistination(@Field("order_id") Long orderId,
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
     * 到达目的地
     *
     * @param orderId
     * @param appKey
     * @return
     */
    @FormUrlEncoded
    @POST("driver/api/v1/arrivalSpecialDistination")
    Observable<ZCOrderResult> arrivalDistination(@Field("order_id") Long orderId,
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
                                                 @Field("min_cost") Double minCost,
                                                 @Field("peak_cost") double peakCost,
                                                 @Field("night_price") double nightPrice,
                                                 @Field("low_speed_cost") double lowSpeedCost,
                                                 @Field("low_speed_time") int lowSpeedTime,
                                                 @Field("peak_mile") double peakMile,
                                                 @Field("night_time") int nightTime,
                                                 @Field("night_mile") double nightMile,
                                                 @Field("night_time_price") double nightTimePrice);

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
    @PUT("/api/v1/finishSpecialOrder")
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
    @POST("/driver/api/v1/createSpecialOrder")
    Observable<ZCOrderResult> createOrder(@Field("passenger_id") Long passengerId,
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
                                          @Field("order_person_id") Long orderPersonId,
                                          @Field("car_type") Long carType);

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
    @GET("api/v1/getSpecialBudgetPrice")
    Observable<BudgetResult> getBudgetPrice(@Query("passenger_id") Long passengerId,
                                            @Query("company_id") Long companyId,
                                            @Query("distance") Double distance,
                                            @Query("time") Integer time,
                                            @Query("order_time") Long orderTime,
                                            @Query("channel") String channel,
                                            @Query("typeId") Long typeId,
                                            @Query("model_id") Long modelId,
                                            @Query("app_key") String appKey);

    /**
     * 获取专车子类型
     *
     * @param appKey
     * @return
     */
    @GET("/api/v1/specialcar/bustree")
    Observable<ZCTypeResult> getZCBusiness(@Query("app_key") String appKey,
                                           @Query("page") Integer page,
                                           @Query("limit") Integer limit,
                                           @Query("company_id") Long companyId,
                                           @Query("service_type") Integer serviceType);

    @GET("api/v1/passengerMustBe")
    Observable<PassengerResult> queryPassenger(@Query("company_id") Long companyId,
                                               @Query("company_name") String companyName,
                                               @Query("phone") String phone,
                                               @Query("app_key") String appKey,
                                               @Query("channel") String channel);

    /**
     * 销单
     *
     * @param orderId
     * @param driverId
     * @param appKey
     * @return
     */
    @FormUrlEncoded
    @PUT("driver/api/v1/cancelSpecialOrder")
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
     * 通过接口方式推送最后一个点到后台.
     */
    @FormUrlEncoded
    @POST("api/v1/pullfee")
    Observable<PullFeeResult> pullFee(@Field("gps") String json,
                                      @Field("app_key") String appKey);

}
