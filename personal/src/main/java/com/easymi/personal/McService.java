package com.easymi.personal;

import com.easymi.common.entity.CompanyList;
import com.easymi.common.entity.PushAnnouncement;
import com.easymi.common.entity.QiNiuToken;
import com.easymi.common.entity.RegisterRes;
import com.easymi.component.result.EmResult;
import com.easymi.personal.entity.CarInfo;
import com.easymi.personal.entity.Register;
import com.easymi.personal.result.AnnouncementResult;
import com.easymi.personal.result.ArticleResult;
import com.easymi.personal.result.BankResult;
import com.easymi.personal.result.BusinessResult;
import com.easymi.personal.result.CompanyResult;
import com.easymi.personal.result.ConfigResult;
import com.easymi.personal.result.EvaResult;
import com.easymi.personal.result.HelpMenuResult;
import com.easymi.personal.result.LiushuiResult;
import com.easymi.personal.result.LoginResult;
import com.easymi.personal.result.NotifityResult;
import com.easymi.personal.result.PicCodeResult;
import com.easymi.personal.result.RateResult;
import com.easymi.personal.result.RechargeResult;
import com.easymi.personal.result.RechargeTypeResult;
import com.easymi.personal.result.RegisterResult;
import com.easymi.personal.result.ShareResult;
import com.easymi.personal.result.StatisResult;
import com.easymi.personal.result.TixianResult;
import com.easymi.personal.result.TixianRuleResult;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 *
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
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
    @GET("v1/public/article_configure/get")
    Observable<ArticleResult> getArticle(@Query("appKey") String appKey,
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
    @POST("api/v1/feedback")
    Observable<EmResult> feedback(@Field("user_id") Long userId,
                                  @Field("user_name") String userName,
                                  @Field("company_id") Long companyId,
                                  @Field("app_key") String appKey,
                                  @Field("content") String content,
                                  @Field("user_type") Integer type,
                                  @Field("phone") String phone
    );

    /**
     * 注销
     *
     * @param driverId
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/resources/driver/cancellation")
    Observable<EmResult> employLoginOut(@Field("id") Long driverId);

    /**
     * 根据区域编码获取公司
     *
     * @param cityCode
     * @param adCode
     * @param appKey
     * @return
     */
    @GET("driver/api/v1/company")
    Observable<CompanyResult> getCompany(@Query("city_code") String cityCode,
                                         @Query("ad_code") String adCode,
                                         @Query("app_key") String appKey
    );


    /**
     * 分享地址
     *
     * @param driverId
     * @param companyId
     * @param appKey
     * @param type
     * @return
     */
    @GET("api/v1/shareLink")
    Observable<ShareResult> shareLink(@Query("id") Long driverId,
                                      @Query("company_id") Long companyId,
                                      @Query("app_key") String appKey,
                                      @Query("type") Integer type);

    /**
     * 统计中心
     *
     * @param driverId
     * @param appKey
     * @param startTime
     * @param endTime
     * @return
     */
    @GET("driver/api/v1/driverSend")
    Observable<StatisResult> driverSend(@Query("driver_id") Long driverId,
                                        @Query("app_key") String appKey,
                                        @Query("start_time") String startTime,
                                        @Query("end_time") String endTime);

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
    @GET("driver/api/v1/articles")
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
    @GET("driver/api/v1/article")
    Observable<ArticleResult> getArticleById(@Query("app_key") String appKey,
                                             @Query("id") Long id);

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
    @GET("driver/api/v1/smsCode")
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
    @POST("api/v1/resources/driver/login")
    Observable<LoginResult> loginByPW(@Field("phone") String phone,
                                      @Field("password") String password,
                                      @Field("randomStr") String randomStr,
                                      @Field("mac") String mac,
                                      @Field("imei") String imei,
                                      @Field("imsi") String imsi,
                                      @Field("loginType") String loginType,
                                      @Field("latitude") String latitude,
                                      @Field("longitude") String longitude,
                                      @Field("appVersion") String appVersion,
                                      @Field("mobileOperators") String mobileOperators,
                                      @Field("mapType") String mapType

    );

    /**
     * 查询所有公告
     *
     * @param page
     * @param size
     * @return
     */
    @GET("api/v1/message/affiches")
    Observable<AnnouncementResult> employAffiches(@Query("page") Integer page,
                                                  @Query("size") Integer size
    );

    /**
     * 查询所有通知
     *
     * @param page
     * @param size
     * @return
     */
    @GET("api/v1/message/notice/employ/records")
    Observable<NotifityResult> notices(@Query("page") Integer page,
                                       @Query("size") Integer size
    );

    /**
     * 服务人员读通知
     *
     * @param id
     * @return
     */
    @FormUrlEncoded
    @PUT("api/v1/message/notice/employ/records/read ")
    Observable<EmResult> readNotice(@Field("id") Long id);

    /**
     * 服务人员通知全部已读
     *
     * @param ids
     * @param appKey
     * @return
     */
    @FormUrlEncoded
    @PUT("api/v1/message/notice/employ/records/read/all")
    Observable<EmResult> readAll(@Field("ids") String ids,
                                 @Field("appKey") String appKey);

    /**
     * 分页查询提现记录
     *
     * @param page
     * @param limit
     * @return
     */
    @GET("api/v1/finance/driver/put_forward")
    Observable<TixianResult> enchashments(@Query("page") int page,
                                          @Query("size") int limit);

    /**
     * 获取司机提现规则
     *
     * @return
     */
    @GET("api/v1/finance/driver/put_forward/configure")
    Observable<TixianRuleResult> tixianRule();

    /**
     * 获取司机银行卡信息
     *
     * @return
     */
    @GET("api/v1/resources/driver/encash/my")
    Observable<BankResult> bankInfo();

    /**
     * 申请提现
     *
     * @param account 提现银行卡号
     * @param fee     提现金额
     * @param payType 银行名字
     * @param payee   持卡人名字
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/finance/driver/put_forward")
    Observable<EmResult> enchashment(@Field("account") String account,
                                     @Field("fee") double fee,
                                     @Field("payType") String payType,
                                     @Field("payee") String payee);

    /**
     * 获取司机充值配置
     *
     * @return
     */
    @GET("api/v1/resources/driver/recharge/configure")
    Observable<ConfigResult> rechargeConfigure();


    /**
     * 获取司机充值配置
     *
     * @return
     */
    @GET("api/v1/system/config/payment")
    Observable<RechargeTypeResult> configPayment();


    /**
     * 司机充值
     *
     * @param channel 充值方式
     * @param fee     充值金额
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/resources/driver/recharge\n")
    Observable<RechargeResult> recharge(@Field("channel") String channel,
                                        @Field("fee") Double fee);


    /**
     * 分页查询账户流水
     *
     * @param page
     * @param size
     * @param startTime
     * @param endTime
     * @return
     */
    @GET("api/v1/finance/driver/flowing/list")
    Observable<LiushuiResult> getLiushui(@Query("page") Integer page,
                                         @Query("size") Integer size,
                                         @Query("startTime") Long startTime,
                                         @Query("endTime") Long endTime);

    /**
     * 登录后修改密码  司机端修改司机密码接口
     *
     * @param newPassword
     * @param oldPassword
     * @param operator
     * @return
     */
    @PUT("api/v1/resources/driver/password")
    @FormUrlEncoded
    Observable<EmResult> updatePsw(@Field("newPassword") String newPassword,
                                   @Field("oldPassword") String oldPassword,
                                   @Field("operator") String operator);

    /**
     * 查询司机评价标签 列表查询
     *
     * @param page
     * @param size
     * @return
     */
    @GET("api/v1/statistics/evaluate/content")
    Observable<EvaResult> drivertag(@Query("page") int page,
                                    @Query("size") int size);

    /**
     * 查询司机评价等级
     *
     * @return
     */
    @GET("api/v1/statistics/evaluate/grade/my")
    Observable<RateResult> driverstar();


    /**
     * 文章获取
     *
     * @return
     */
    @GET("api/v1/public/article_configure/get/{alias}")
    Observable<ArticleResult> getArticle(@Path("alias") String alias);


    /**
     * 查询单个公告
     *
     * @param noticeId
     * @return
     */
    @GET("api/v1/public/message/affiche/{id}")
    Observable<PushAnnouncement> employAfficheById(@Path("id") Long noticeId,
                                                   @Query("appKey") String appKey);


    //注册相关

    /**
     * 公司表列表查询
     *
     * @return
     */
    @GET("api/v1/public/system/app/companys")
    Observable<CompanyList> qureyCompanys();

    /**
     * 公司表列表查询
     *
     * @return
     */
    @GET("api/v1/public/system/app/company/{id}")
    Observable<BusinessResult> getBusinessType(@Path("id") long id);

    /**
     * 发送短信登录接口
     *
     * @param code
     * @param phone
     * @param random
     * @param type
     * @param userType
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/public/app/captcha/send_sms")
    Observable<EmResult> sendSms(@Field("code") String code,
                                 @Field("phone") String phone,
                                 @Field("random") String random,
                                 @Field("type") String type,
                                 @Field("userType") String userType);

    /**
     * 司机注册接口
     *
     * @param password
     * @param phone
     * @param smsCode
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/public/driver/register/save")
    Observable<Register> register(@Field("password") String password,
                                  @Field("phone") String phone,
                                  @Field("smsCode") String smsCode,
//                                  @Field("randomStr") String randomStr,
//                                  @Field("code") String code,
                                  @Field("random") String random);

    /**
     * 获取七牛云token
     *
     * @return
     */
    @GET("api/v1/public/app/qny_token")
    Observable<QiNiuToken> getToken();

    /**
     * 注册提交申请资料
     *
     * @param id
     * @param realName
     * @param phone
     * @param idCard
     * @param emergency
     * @param emergencyPhone
     * @param companyId
     * @param serviceType
     * @param startTime
     * @param endTime
     * @param introducer
     * @param portraitPath
     * @param idCardPath
     * @param idCardBackPath
     * @param driveLicensePath
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/public/driver/register/apply/app/save")
    Observable<RegisterRes> applyDriver(@Field("id") String id,
                                        @Field("realName") String realName,
                                        @Field("phone") String phone,
                                        @Field("idCard") String idCard,
                                        @Field("emergency") String emergency,
                                        @Field("emergencyPhone") String emergencyPhone,
                                        @Field("companyId") String companyId,
                                        @Field("serviceType") String serviceType,
                                        @Field("driveLicenceStart") String startTime,
                                        @Field("driveLicenceEnd") String endTime,
                                        @Field("introducer") String introducer,
                                        @Field("portraitPath") String portraitPath,
                                        @Field("idCardPath") String idCardPath,
                                        @Field("idCardBackPath") String idCardBackPath,
                                        @Field("driveLicensePath") String driveLicensePath,

                                        @Field("netCarQualificationsStart") String netCarQualificationsStart,
                                        @Field("netCarQualificationsEnd") String netCarQualificationsEnd,
                                        @Field("practitionersPhoto") String practitionersPhoto
    );


    /**
     * 获取司机注册信息
     *
     * @param phone
     * @return
     */
    @GET("api/v1/public/driver/register/get")
    Observable<RegisterResult> getDriverInfo(@Query("phone") String phone);


    /**
     * 获取当前司机名片地址
     *
     * @param driverId 司机id
     * @return
     */
    @GET("api/v1/resources/driver/card")
    Observable<LoginResult> queryCardHost(@Query("driverId") String driverId);


}
