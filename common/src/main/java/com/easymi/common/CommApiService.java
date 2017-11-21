package com.easymi.common;

import com.easymi.common.result.QueryOrdersResult;
import com.easymi.component.result.EmResult;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by developerLzh on 2017/11/17 0017.
 */

public interface CommApiService {

    /**
     * 查询所有订单接口
     * @param driverId
     * @param appKey
     * @return
     */
    @GET("driver/api/v1/orders")
    Observable<QueryOrdersResult> indexOrders(@Query("driver_id") Long driverId,
                                              @Query("app_key") String appKey);

    /**
     * 上线接口
     * @param driverId
     * @param appKey
     * @return
     */
    @GET("driver/api/v1/online")
    Observable<EmResult> online(@Query("driver_id") Long driverId,
                                @Query("app_key") String appKey);
}
