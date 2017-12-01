package com.easymi.personal;

import com.easymi.component.result.EmResult;
import com.easymi.personal.result.LiushuiResult;
import com.easymi.personal.result.LoginResult;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by liuzihao on 2017/11/16.
 * 个人模块 api
 */

public interface McService {

    @FormUrlEncoded
    @POST("driver/api/v1/employLogin")
    Observable<LoginResult> login(@Field("username") String userName,
                                  @Field("password") String psw,
                                  @Field("app_key") String appKey);

    @FormUrlEncoded
    @PUT("driver/api/v1/modifyPassword")
    Observable<EmResult> changePsw(@Field("phone") String phone,
                                   @Field("password") String password,
                                   @Field("app_key") String appKey);

    @GET("driver/api/v1/employInfo")
    Observable<LoginResult> getDriverInfo(@Query("id") Long driverId,
                                          @Query("app_key") String appKey);

    /**
     * 分页查询业务流水
     * @param page
     * @param limit
     * @param startTime
     * @param endTime
     * @param appKey
     * @param companyId
     * @param driverId
     * @return
     */
    @GET("driver/api/v1/driverPreSaveSerials")
    Observable<LiushuiResult> getLiushui(@Query("page") Integer page,
                                         @Query("limit")Integer limit,
                                         @Query("start_time")Long startTime,
                                         @Query("end_time")Long endTime,
                                         @Query("app_key")String appKey,
                                         @Query("company_id")Long companyId,
                                         @Query("driver_id")Long driverId);
}
