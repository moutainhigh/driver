package com.easymi.taxi;

import com.easymi.common.entity.PullFeeResult;
import com.easymi.component.result.EmResult;
import com.easymi.taxi.entity.TransferList;
import com.easymi.taxi.result.BudgetResult;
import com.easymi.taxi.result.ConsumerResult;
import com.easymi.taxi.result.TaxiOrderResult;
import com.easymi.taxi.result.ZCTypeResult;
import com.easymi.taxi.result.PassengerResult;
import com.easymi.taxi.result.SameOrderResult;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by developerLzh on 2017/11/17 0017.
 */

public interface TaxiApiService {

    /**
     * 查询单个订单
     *
     * @param orderId
     * @param appKey
     * @return
     */
    @GET("driver/api/v1/orderFindOne")
    Observable<TaxiOrderResult> indexOrders(@Query("order_id") Long orderId,
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
    Observable<TaxiOrderResult> grabOrder(@Field("order_id") Long orderId,
                                          @Field("driver_id") Long driverId,
                                          @Field("app_key") String appKey);

//    /**
//     * 接单
//     *
//     * @param orderId
//     * @param driverId
//     * @param appKey
//     * @return
//     */
//    @FormUrlEncoded
//    @POST("/driver/api/v1/takeSpecialOrder")
//    Observable<TaxiOrderResult> takeOrder(@Field("order_id") Long orderId,
//                                          @Field("driver_id") Long driverId,
//                                          @Field("app_key") String appKey);

//    /**
//     * 拒单
//     *
//     * @param orderId
//     * @param driverId
//     * @param appKey
//     * @return
//     */
//    @FormUrlEncoded
//    @PUT("driver/api/v1/refuseSpecialOrder")
//    Observable<EmResult> refuseOrder(@Field("order_id") Long orderId,
//                                     @Field("driver_id") Long driverId,
//                                     @Field("app_key") String appKey,
//                                     @Field("remark") String remark);

    /**
     * 司机修改终点
     */
    @FormUrlEncoded
    @PUT("driver/api/v1/modifyDestination")
    Observable<TaxiOrderResult> changeEnd(@Field("id") Long orderId,
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
    Observable<TaxiOrderResult> goToBookAddress(@Field("order_id") Long orderId,
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
    Observable<TaxiOrderResult> arrivalBookAddress(@Field("order_id") Long orderId,
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
    Observable<TaxiOrderResult> goToDistination(@Field("order_id") Long orderId,
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
    Observable<TaxiOrderResult> waitOrder(@Field("order_id") Long orderId,
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
    Observable<TaxiOrderResult> arrivalDistination(@Field("order_id") Long orderId,
                                                   @Field("app_key") String appKey,

                                                   //用原始数据
                                                   @Field("advance_price") Double advance_price,//垫付
                                                   @Field("other_price") Double other_price,//附加费用
                                                   @Field("remark") String remark,//备注

                                                   //最新的数据
                                                   @Field("distance") Double distance,
                                                   @Field("distance_fee") Double distance_fee,
                                                   @Field("time") Integer time,
                                                   @Field("time_fee") Double time_fee,
                                                   @Field("wait_time") Integer wait_time,
                                                   @Field("wait_fee") Double wait_fee,

                                                   //用原始数据
                                                   @Field("add_distance") Double add_distance,
                                                   @Field("add_fee") Double add_fee,

                                                   //重新计算
                                                   @Field("coupon_fee") Double coupon_fee,//优惠券
                                                   @Field("total_fee") Double total_fee,//跑出来的钱 + 垫付 + 附加费用 (不算优惠券的钱)
                                                   @Field("real_pay") Double real_pay,//total_fee - 优惠金额 - 预付费

                                                   //最新的数据
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
    Observable<TaxiOrderResult> createOrder(@Field("passenger_id") Long passengerId,
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
     * @param memo
     * @return
     */
    @FormUrlEncoded
    @PUT("api/v1/public/order/cancel")
    Observable<EmResult> cancelOrder(@Field("orderId") long orderId,
                                     @Field("memo") String memo);

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
     * 出租车 --> 查询单个订单
     *
     * @param appKey
     * @return
     */
    @GET("api/v1/taxi/normal/order/{id}")
    Observable<TaxiOrderResult> queryTaxiOrder(@Path("id") Long id,
                                               @Query("appKey") String appKey);

    /**
     * 接单
     *
     * @param appKey
     * @param companyId
     * @param driverId
     * @param orderId
     * @return
     */
    @FormUrlEncoded
    @PUT("api/v1/taxi/normal/order/receipt")
    Observable<TaxiOrderResult> takeOrder(@Field("appKey") String appKey,
                                          @Field("companyId") Long companyId,
                                          @Field("driverId") Long driverId,
                                          @Field("orderId") Long orderId);

    /**
     * 修改订单状态
     *
     * @param companyId
     * @param detailAddress
     * @param driverId
     * @param latitude
     * @param longitude
     * @param orderId
     * @param status
     * @return
     */
    @FormUrlEncoded
    @PUT("api/v1/taxi/normal/order/status")
    Observable<EmResult> changeOrderStatus(@Field("companyId") Long companyId,
                                           @Field("detailAddress") String detailAddress,
                                           @Field("driverId") Long driverId,
                                           @Field("latitude") Double latitude,
                                           @Field("longitude") Double longitude,
                                           @Field("orderId") Long orderId,
                                           @Field("status") int status);

    /**
     * 结算接口
     *
     * @param orderId
     * @param orderNo
     * @param fee
     * @return
     */
    @FormUrlEncoded
    @PUT("api/v1/taxi/normal/order/settlement")
    Observable<EmResult> taxiSettlement(@Field("orderId") Long orderId,
                                        @Field("orderNo") String orderNo,
//                                        @Field("driverId") Long driverId,
//                                        @Field("companyId") Long companyId,
                                        @Field("longitude") Double longitude,
                                        @Field("latitude") Double latitude,
                                        @Field("detailAddress") String detailAddress,
                                        @Field("fee") double fee);

    /**
     * 通用拒单 专车出租车用
     *
     * @param orderId
     * @param serviceType
     * @param remark
     * @return
     */
    @FormUrlEncoded
    @PUT("api/v1/public/order/refusal")
    Observable<EmResult> refuseOrder(@Field("orderId") long orderId,
                                     @Field("serviceType") String serviceType,
                                     @Field("remark") String remark);
}
