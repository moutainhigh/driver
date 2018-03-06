package com.easymi.common;

import com.easymi.common.result.AnnouncementResult;
import com.easymi.common.result.LoginResult;
import com.easymi.common.result.MultipleOrderResult;
import com.easymi.common.result.NearDriverResult;
import com.easymi.common.result.NotitfyResult;
import com.easymi.common.result.QueryOrdersResult;
import com.easymi.common.result.SettingResult;
import com.easymi.common.result.WorkStatisticsResult;
import com.easymi.component.result.EmResult;

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

public interface CommApiService {

    /**
     * 查询所有订单接口
     *
     * @param driverId
     * @param appKey
     * @return
     */
    @GET("driver/api/v1/runningOrders")
    Observable<QueryOrdersResult> queryRunningOrders(@Query("driver_id") Long driverId,
                                                     @Query("app_key") String appKey,
                                                     @Query("page") int page,
                                                     @Query("limit") int limit);

    /**
     * 上线接口
     *
     * @param driverId
     * @param appKey
     * @return
     */
    @FormUrlEncoded
    @PUT("driver/api/v1/online")
    Observable<EmResult> online(@Field("driver_id") Long driverId,
                                @Field("app_key") String appKey);


    @FormUrlEncoded
    @PUT("driver/api/v1/offline")
    Observable<EmResult> offline(@Field("driver_id") Long driverId,
                                 @Field("app_key") String appKey);


    /**
     * 查询附近司机
     *
     * @param lat
     * @param lng
     * @param distance
     * @param appKey
     * @return
     */
    @GET("driver/api/v1/getNearDrivers")
    Observable<NearDriverResult> getNearDrivers(
            @Query("driver_id") Long driverId,
            @Query("lat") Double lat,
            @Query("lng") Double lng,
            @Query("distance") Double distance,
            @Query("app_key") String appKey
    );

    /**
     * 查询未支付和已完成订单
     *
     * @param driverId
     * @param business
     * @param startTime
     * @param endTime
     * @param appKey
     * @param page
     * @param limit
     * @return
     */
    @GET("driver/api/v1/orders")
    Observable<QueryOrdersResult> queryOverOrdersByBunsiness(@Query("driver_id") Long driverId,
                                                             @Query("business") String business,
                                                             @Query("start_time") Long startTime,
                                                             @Query("end_time") Long endTime,
                                                             @Query("app_key") String appKey,
                                                             @Query("page") int page,
                                                             @Query("limit") int limit);

    /**
     * 查询通知
     *
     * @param noticeId
     * @return
     */
    @GET("driver/api/v1/notice")
    Observable<NotitfyResult> loadNotice(@Query("id") Long noticeId,
                                         @Query("app_key") String appKey);

    /**
     * 查询通知
     *
     * @param noticeId
     * @return
     */
    @GET("driver/api/v1/employAfficheById")
    Observable<AnnouncementResult> employAfficheById(@Query("id") Long noticeId,
                                                     @Query("app_key") String appKey);

    /**
     * 首页统计
     *
     * @param driverId
     * @param nowDate
     * @param appKey
     * @return
     */
    @GET("driver/api/v1/driverInfo")
    Observable<WorkStatisticsResult> workStatistics(@Query("driver_id") Long driverId,
                                                    @Query("now_date") String nowDate,
                                                    @Query("app_key") String appKey,
                                                    @Query("is_online")Integer isOnline);

    /**
     * 代驾 --> 查询单个订单
     *
     * @param orderId
     * @param appKey
     * @return
     */
    @GET("driver/api/v1/orderFindOne")
    Observable<MultipleOrderResult> queryDJOrder(@Query("order_id") Long orderId,
                                                 @Query("app_key") String appKey);

    /**
     * 代驾 --> 抢单
     *
     * @param orderId
     * @param driverId
     * @param appKey
     * @return
     */
    @FormUrlEncoded
    @PUT("driver/api/v1/grabOrder")
    Observable<MultipleOrderResult> grabDJOrder(@Field("order_id") Long orderId,
                                                @Field("driver_id") Long driverId,
                                                @Field("app_key") String appKey);

    /**
     * 代驾 -->接单
     *
     * @param orderId
     * @param driverId
     * @param appKey
     * @return
     */
    @FormUrlEncoded
    @POST("driver/api/v1/takeOrder")
    Observable<MultipleOrderResult> takeDJOrder(@Field("order_id") Long orderId,
                                                @Field("driver_id") Long driverId,
                                                @Field("app_key") String appKey);

    /**
     * @param orderId
     * @param baoxiaoMoney  报销金额
     * @param baoxiaoReason 报销理由
     * @param appKey
     * @return
     */
    @FormUrlEncoded
    @POST("/driver/api/v1/wipeOutApply")
    Observable<EmResult> baoxiao(@Field("order_id") Long orderId,
                                 @Field("money") Double baoxiaoMoney,
                                 @Field("reason") String baoxiaoReason,
                                 @Field("app_key") String appKey);

    /**
     * 获取司机信息
     *
     * @param driverId
     * @param appKey
     * @return
     */
    @GET("driver/api/v1/getEmployById")
    Observable<LoginResult> getDriverInfo(@Query("id") Long driverId,
                                          @Query("app_key") String appKey);

    /**
     * 获取APP配置
     * @param appKey
     * @return
     */
    @GET("api/v1/daijiaApp")
    Observable<SettingResult> getAppSetting(@Query("app_key") String appKey);
}
