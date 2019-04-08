package com.easymin.chartered;

import com.easymi.component.result.EmResult;
import com.easymin.chartered.result.OrderListResult;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: CharteredService
 * @Author: shine
 * Date: 2018/12/21 下午5:16
 * Description:
 * History:
 */
public interface CharteredService {

    /**
     * 包车租车 --> 查询单个订单
     * <p>
     * id 订单id
     *
     * @return
     */
    @GET("api/v1/chartered/driver/order/{id}")
    Observable<OrderListResult> findOne(@Path("id") Long id);


    /**
     *  订单状态修改
     * @param companyId
     * @param driverId
     * @param latitude
     * @param longitude
     * @param orderId
     * @param status
     * @return
     */
    @FormUrlEncoded
    @PUT("api/v1/chartered/driver/order/status")
    Observable<EmResult> changeStatus(@Field("companyId") Long companyId,
                                      @Field("detailAddress") String detailAddress,
                                      @Field("driverId") Long driverId,
                                      @Field("latitude") Double latitude,
                                      @Field("longitude") Double longitude,
                                      @Field("orderId") Long orderId,
                                      @Field("status") int status);


    /**
     * 订单状态修改
     * @param id
     * @param version
     * @return
     */
    @FormUrlEncoded
    @PUT("api/v1/chartered/driver/order/confirm")
    Observable<EmResult> orderConfirm(@Field("id") Long id,
                                      @Field("version") Long version);

}
