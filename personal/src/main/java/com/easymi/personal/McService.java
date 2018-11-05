package com.easymi.personal;

import com.easymi.component.result.EmResult;
import com.easymi.personal.entity.CarInfo;
import com.easymi.personal.result.AnnResult;
import com.easymi.personal.result.AnnouncementResult;
import com.easymi.personal.result.ArticleResult;
import com.easymi.personal.result.BusinessResult;
import com.easymi.personal.result.CompanyResult;
import com.easymi.personal.result.EvaResult;
import com.easymi.personal.result.HelpMenuResult;
import com.easymi.personal.result.LiushuiResult;
import com.easymi.personal.result.LoginResult;
import com.easymi.personal.result.NotifityResult;
import com.easymi.personal.result.PicCodeResult;
import com.easymi.personal.result.RechargeResult;
import com.easymi.personal.result.ShareResult;
import com.easymi.personal.result.StatisResult;
import com.easymi.personal.result.TixianResult;
import com.easymi.personal.result.TixianRuleResult;

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
                                  @Field("udId") String udid,
                                  @Field("app_key") String appKey,
                                  @Field("deviceType") String deviceType,
                                  @Field("deviceInfo") String deviceInfo,
                                  @Field("appversion") String appversion,
                                  @Field("userId") String userId,
                                  @Field("device_version") String deviceVersion,
                                  @Field("device_net") String deviceNet,
                                  @Field("device_net_type") String deviceNetType,
                                  @Field("mobile_brand") String mobileBrand
    );

    @FormUrlEncoded
    @POST("driver/api/v1/allEmployLogin")
    Observable<LoginResult> loginByQiye(@Field("username") String userName,
                                        @Field("password") String psw,
                                        @Field("udId") String udid,
                                        @Field("deviceType") String deviceType,
                                        @Field("deviceInfo") String deviceInfo,
                                        @Field("appversion") String appversion,
                                        @Field("userId") String userId,
                                        @Field("device_version") String deviceVersion,
                                        @Field("device_net") String deviceNet,
                                        @Field("device_net_type") String deviceNetType,
                                        @Field("mobile_brand") String mobileBrand,
                                        @Field("system_code") String sysCode
    );


    @FormUrlEncoded
    @PUT("driver/api/v1/modifyPassword")
    Observable<EmResult> changePsw(@Field("phone") String phone,
                                   @Field("password") String password,
                                   @Field("app_key") String appKey);

    @GET("driver/api/v1/getEmployById")
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
                                         @Query("alias") String alias,
                                         @Query("companyId") Long companyId);

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
                                  @Field("content") String content,
                                  @Field("user_type") Integer type,
                                  @Field("phone") String phone
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

    /**
     * 充值  /api/v1/recharge  PUT  user_id  int  是  用户id
     * app_key  string  是  系统key
     * pay_type  string  是  支付类型
     * money  float  是  充值金额
     * type  string  是  用户类型（1客户，2司机）
     *
     * @param userId
     * @param appKey
     * @param payType
     * @param money
     * @param type
     * @return
     */
    @PUT("/api/v1/recharge")
    @FormUrlEncoded
    Observable<RechargeResult> recharge(@Field("user_id") Long userId,
                                        @Field("app_key") String appKey,
                                        @Field("pay_type") String payType,
                                        @Field("money") Double money,
                                        @Field("type") Integer type);


    /**
     * 登录后修改密码
     *
     * @param driver_id
     * @param old_password
     * @param new_password
     * @param app_key
     * @return
     */
    @PUT("/driver/api/v1/updatePassword")
    @FormUrlEncoded
    Observable<EmResult> updatePsw(@Field("driver_id") Long driver_id,
                                   @Field("old_password") String old_password,
                                   @Field("new_password") String new_password,
                                   @Field("app_key") String app_key);

    /**
     * 分页查询提现申请
     *
     * @param driverId
     * @param page
     * @param appkey
     * @param limit
     * @return
     */
    @GET("/driver/api/v1/enchashments")
    Observable<TixianResult> enchashments(@Query("driver_id") Long driverId,
                                          @Query("page") Integer page,
                                          @Query("app_key") String appkey,
                                          @Query("company_id") Long company_id,
                                          @Query("limit") int limit);

    /**
     * 申请提现
     *
     * @param driverName
     * @param jobNo
     * @param phone
     * @param cost
     * @param companyId
     * @param appKey
     * @param bank
     * @param account
     * @param name
     * @return
     */
    @POST("/driver/api/v1/enchashment")
    @FormUrlEncoded
    Observable<EmResult> enchashment(@Field("driver_id") Long driverId,
                                     @Field("driver_name") String driverName,
                                     @Field("job_no") String jobNo,
                                     @Field("driver_tel") String phone,
                                     @Field("cost") Double cost,
                                     @Field("company_id") Long companyId,
                                     @Field("app_key") String appKey,
                                     @Field("bank") String bank,
                                     @Field("account") String account,
                                     @Field("payee") String name);

    /**
     * 分享地址
     *
     * @param driverId
     * @param companyId
     * @param appKey
     * @param type
     * @return
     */
    @GET("/api/v1/shareLink")
    Observable<ShareResult> shareLink(@Query("id") Long driverId,
                                      @Query("company_id") Long companyId,
                                      @Query("app_key") String appKey,
                                      @Query("type") Integer type);


    /**
     * 客户的评价接口
     *
     * @param driverId
     * @param appKey
     * @return
     */
    @GET("/driver/api/v1/driverRate")
    Observable<EvaResult> driverRate(@Query("driver_id") Long driverId,
                                     @Query("app_key") String appKey);

    /**
     * 统计中心
     *
     * @param driverId
     * @param appKey
     * @param startTime
     * @param endTime
     * @return
     */
    @GET("/driver/api/v1/driverSend")
    Observable<StatisResult> driverSend(@Query("driver_id") Long driverId,
                                        @Query("app_key") String appKey,
                                        @Query("start_time") String startTime,
                                        @Query("end_time") String endTime);


    @GET("/driver/api/v1/systemConfig")
    Observable<TixianRuleResult> tixianRule(@Query("app_key") String appKey);

    /**
     * 根据业务类型id查询子菜单
     *
     * @param appKey
     * @param companyId
     * @param page
     * @param limit
     * @param cateId    代驾：2
     * @return
     */
    @GET("/driver/api/v1/articles")
    Observable<HelpMenuResult> getHelpeSubMenu(@Query("app_key") String appKey,
                                               @Query("company_id") Long companyId,
                                               @Query("page") Integer page,
                                               @Query("limit") Integer limit,
                                               @Query("category_id") Long cateId);

    /**
     * 根据文章id查询文章
     *
     * @param appKey
     * @param id
     * @return
     */
    @GET("/driver/api/v1/article")
    Observable<ArticleResult> getArticleById(@Query("app_key") String appKey,
                                             @Query("id") Long id);

    /**
     * 查询单个公告
     *
     * @param noticeId
     * @return
     */
    @GET("driver/api/v1/employAfficheById")
    Observable<AnnResult> employAfficheById(@Query("id") Long noticeId,
                                            @Query("app_key") String appKey);

//    /**
//     * 获取APP配置
//     *
//     * @param appKey
//     * @return
//     */
//    @GET("api/v1/appSetting")
//    Observable<com.easymi.common.result.SettingResult> getAppSetting(@Query("app_key") String appKey);

    /**
     * 获取图形验证码
     *
     * @return
     */
    @GET("driver/api/v1/picCode")
    Observable<PicCodeResult> picCode(@Query("phone") String phone,
                                      @Query("app_key") String appKey);

    /**
     * 获取短信验证码
     *
     * @param phone
     * @param appKey
     * @param country
     * @param companyId
     * @return
     */
    @GET("/driver/api/v1/smsCode")
    Observable<EmResult> smsCode(@Query("phone") String phone,
                                 @Query("app_key") String appKey,
                                 @Query("country") String country,
                                 @Query("company_id") Long companyId);

    @FormUrlEncoded
    @POST("driver/api/v1/checkCode")
    Observable<EmResult> checkCode(@Field("phone") String phone,
                                   @Field("code") String code,
                                   @Field("code_type") String codeType,
                                   @Field("app_key") String appKey);


    @GET("driver/api/v1/getVehicleInfoByEmployId")
    Observable<CarInfo> getCarInfo(@Query("employ_id") long employId,
                                   @Query("app_key") String appKey);


    //add hufeng

    /**
     * 账号密码登录
     *
     * @param phone
     * @param password
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/public/driver/login")
    Observable<LoginResult> loginByPW(@Field("phone") String phone,
                                   @Field("password") String password);




    /**
     * 获取APP配置
     * @return
     */
    @GET("api/v1/taxi_online/config/app/get")
    Observable<com.easymi.common.result.SettingResult> getAppSetting();
}
