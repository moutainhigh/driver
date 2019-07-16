package com.easymin.official;

import com.easymi.common.entity.QiNiuToken;
import com.easymi.component.result.EmResult;
import com.easymin.official.result.GovOrderResult;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: OfficialService
 * @Author: hufeng
 * @Date: 2019/3/25 下午1:40
 * @Description:
 * @History:
 */
public interface OfficialService {

    /**
     * 公务用车 --> 查询单个订单
     *
     * @return
     */
    @GET("api/v1/gov/driver/order/get/{id}")
    Observable<GovOrderResult> findOne (@Path("id") Long id);

    /**
     * 公务用车 --> 前往预约地
     *
     * @param id
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/gov/driver/order/goto_book_place")
    Observable<GovOrderResult> goToBookAddress(@Field("id") Long id,
                                               @Field("version") Long version);

    /**
     * 公务用车 --> 到达预约地
     *
     * @param id
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/gov/driver/order/arrive_book_place")
    Observable<GovOrderResult> arrivalBookAddress(@Field("id") Long id,
                                                  @Field("version") Long version);

    /**
     * 公务用车 --> 前往目的地
     *
     * @param id
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/gov/driver/order/goto_destination")
    Observable<GovOrderResult> goToDistination(@Field("id") Long id,
                                               @Field("version") Long version,
                                               @Field("longitude") Double longitude,
                                               @Field("latitude") Double latitude,
                                               @Field("detailAddress") String detailAddress);


    /**
     * 公务用车 --> 到达目的地
     *
     * @param orderId
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/gov/driver/order/arrive_destination")
    Observable<GovOrderResult> arrivalDistination(@Field("id") Long orderId,
                                                  @Field("version") Long version,
                                                  @Field("longitude") Double longitude,
                                                  @Field("latitude") Double latitude,
                                                  @Field("detailAddress") String detailAddress);


    /**
     * 获取七牛云token
     * @return
     */
    @GET("api/v1/public/app/qny_token")
    Observable<QiNiuToken> getToken();



    /**
     * 确认订单
     *
     * @param orderId
     * @param voucher
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/gov/driver/order/finish")
    Observable<EmResult> confirmOrder(@Field("id") Long orderId,
                                     @Field("version") Long version,
                                     @Field("voucher") String voucher);

}
