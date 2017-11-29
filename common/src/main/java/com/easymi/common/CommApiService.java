package com.easymi.common;

import com.easymi.common.result.NearDriverResult;
import com.easymi.common.result.QueryOrdersResult;
import com.easymi.component.result.EmResult;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
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

    @GET("driver/api/v1/orders")
    Observable<QueryOrdersResult> queryOverOrdersByBunsiness(@Query("driver_id") Long driverId,
                                                             @Query("business") String business,
                                                             @Query("start_time") Long startTime,
                                                             @Query("end_time") Long endTime,
                                                             @Query("app_key") String appKey,
                                                             @Query("page") int page,
                                                             @Query("limit") int limit);
}
