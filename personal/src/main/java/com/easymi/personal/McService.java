package com.easymi.personal;

import com.easymi.component.result.EmResult;
import com.easymi.personal.result.AnnouncementResult;
import com.easymi.personal.result.ArticleResult;
import com.easymi.personal.result.BusinessResult;
import com.easymi.personal.result.CompanyResult;
import com.easymi.personal.result.LiushuiResult;
import com.easymi.personal.result.LoginResult;
import com.easymi.personal.result.NotifityResult;

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
     *
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
                                         @Query("limit") Integer limit,
                                         @Query("start_time") Long startTime,
                                         @Query("end_time") Long endTime,
                                         @Query("app_key") String appKey,
                                         @Query("company_id") Long companyId,
                                         @Query("driver_id") Long driverId);

    /**
     * 文章
     * <p>
     * 联系我们 alias = ContactUs
     * 关于我们 alias = AboutUs
     * 司机推广 alias = DriverPromotion
     *
     * @param appKey
     * @param alias
     * @return
     */
    @GET("driver/api/v1/articlebyalias")
    Observable<ArticleResult> getArticle(@Query("app_key") String appKey,
                                         @Query("alias") String alias);

    /**
     * 反馈
     *
     * @param userName
     * @param companyId
     * @param appKey
     * @param content
     * @return
     */
    @FormUrlEncoded
    @POST("/api/v1/feedback")
    Observable<EmResult> feedback(@Field("user_id") Long userId,
                                  @Field("user_name") String userName,
                                  @Field("company_id") Long companyId,
                                  @Field("app_key") String appKey,
                                  @Field("content") String content
    );

    /**
     * 查询所有通知
     *
     * @param driverId
     * @param appKey
     * @param page
     * @param limit
     * @return
     */
    @GET("/driver/api/v1/notices")
    Observable<NotifityResult> notices(@Query("driver_id") Long driverId,
                                       @Query("app_key") String appKey,
                                       @Query("page") Integer page,
                                       @Query("limit") Integer limit
    );

    /**
     * 读通知
     *
     * @param id
     * @param appKey
     * @return
     */
    @FormUrlEncoded
    @PUT("/driver/api/v1/readNotice")
    Observable<EmResult> readNotice(@Field("id") Long id,
                                    @Field("app_key") String appKey);

    /**
     * 全部已读
     *
     * @param id
     * @param appKey
     * @return
     */
    @FormUrlEncoded
    @PUT("/driver/api/v1/readNotices")
    Observable<EmResult> readAll(@Field("driver_id") Long id,
                                 @Field("app_key") String appKey);

    /**
     * 查询所有公告
     *
     * @param companyId
     * @param appKey
     * @param page
     * @param limit
     * @return
     */
    @GET("/driver/api/v1/employAffiches")
    Observable<AnnouncementResult> employAffiches(@Query("company_id") Long companyId,
                                                  @Query("app_key") String appKey,
                                                  @Query("page") Integer page,
                                                  @Query("limit") Integer limit
    );


    /**
     * 注销
     *
     * @param driverId
     * @param appKey
     * @return
     */
    @FormUrlEncoded
    @PUT("driver/api/v1/employLoginOut")
    Observable<EmResult> employLoginOut(@Field("id") Long driverId,
                                        @Field("app_key") String appKey);

    /**
     * 根据区域编码获取公司
     *
     * @param cityCode
     * @param adCode
     * @param appKey
     * @return
     */
    @GET("/driver/api/v1/company")
    Observable<CompanyResult> getCompany(@Query("city_code") String cityCode,
                                         @Query("ad_code") String adCode,
                                         @Query("app_key") String appKey
    );

    /**
     * 根据公司id业务类型获取子类型
     *
     * @param companyId
     * @param business
     * @param app_key
     * @return
     */
    @GET("/driver/api/v1/getBusiness")
    Observable<BusinessResult> getBusiness(@Query("company_id") Long companyId,
                                           @Query("business") String business,
                                           @Query("app_key") String app_key
    );
}
