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
    @GET("driver/api/v1/findOne")
    Observable<DJOrderResult> indexOrders(@Query("order_id") Long orderId,
                                          @Query("app_key") String appKey);

    @FormUrlEncoded
    @POST("driver/api/v1/takeOrder")
    Observable<DJOrderResult> takeOrder(@Field("order_id") Long orderId,
                                        @Field("driver_id") Long driverId,
                                        @Field("app_key") String appKey);
}
