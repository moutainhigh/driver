package com.easymi.component;

import com.easymi.component.entity.PassengerLcResult;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface ComponentService {

    /**
     * 乘客位置请求
     *
     * @param
     * @return
     */
    @GET("api/v1/message/location/passenger")
    Observable<PassengerLcResult> passengerLoc(@Query("orderId") Long orderId);
}
