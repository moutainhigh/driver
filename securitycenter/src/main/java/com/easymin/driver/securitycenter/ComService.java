package com.easymin.driver.securitycenter;

import com.easymin.driver.securitycenter.result.ContactResult;
import com.easymin.driver.securitycenter.result.EmResult;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: ComService
 *@Author: shine
 * Date: 2018/11/26 下午6:44
 * Description:
 * History:
 */
public interface ComService {

    /**
     * 检测该乘客录音是否授权，是否绑定紧急联系人
     *
     * @param passengerId
     * @return
     */
    @GET("api/v1/safe/checking_auth")
    Observable<EmResult> checkingAuth(@Query("passengerId") long passengerId,
                                      @Query("appKey") String appKey);

    /**
     * 紧急联系人选中，取消选中
     *
     * @param id
     * @param emergCheck
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/safe/trip_emerge_choose")
    Observable<EmResult> tripEmergeChoose(@Field("id") long id,
                                          @Field("emergCheck") int emergCheck,
                                          @Field("appKey") String appKey);

    /**
     * 添加紧急联系人
     *
     * @param appKey
     * @param emergName
     * @param emergPhone
     * @param passengerId
     * @param passengerPhone
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/safe/trip_emerge_contact")
    Observable<EmResult> tripEmergeContact(@Field("emergName") String emergName,
                                           @Field("emergPhone") String emergPhone,
                                           @Field("passengerId") long passengerId,
                                           @Field("passengerPhone") String passengerPhone,
                                           @Field("appKey") String appKey);

    /**
     * 修改紧急联系人
     *
     * @param id
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/safe/trip_emerge_contacts")
    Observable<EmResult> amentEmerge(
//            @Field("emergCheck") int emergCheck,
            @Field("emergName") String emergName,
            @Field("emergPhone") String emergPhone,
            @Field("id") long id,
            @Field("passengerId") long passengerId,
            @Field("passengerPhone") String passengerPhone,
//            @Field("shareAuto") int shareAuto,
//            @Field("shareEnd") long shareEnd,
//            @Field("shareStart") long shareStart,
            @Field("appKey") String appKey);

    /**
     * 查询单个紧急联系人
     *
     * @param id 紧急联系人的id
     * @return
     */
    @GET("api/v1/safe/trip_emerge_contact/{id}")
    Observable<EmResult> contactById(@Path("id") long id,
                                     @Query("appKey") String appKey);

    /**
     * 删除紧急联系人
     *
     * @param id 紧急联系人的id
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/safe/trip_emerge_contacts_del")
    Observable<EmResult> deleteContact(@Field("id") long id,
                                       @Field("appKey") String appKey);

    /**
     * 查询紧急联系人列表，是否绑定紧急联系人
     *
     * @param passengerId 乘客id
     * @return
     */
    @GET("api/v1/safe/trip_emerge_contacts")
    Observable<ContactResult> contactList(@Query("passengerId") long passengerId,
                                          @Query("appKey") String appKey);

    /**
     * 行程自动分享开启，关闭
     *
     * @param appKey
     * @param passengerId
     * @param shareAuto   1:开启自动分享;0:关闭自动分享
     * @param shareEnd
     * @param shareStart
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/safe/trip_share_auto")
    Observable<EmResult> shareAutoAment(@Field("passengerId") long passengerId,
                                        @Field("shareAuto") int shareAuto,
                                        @Field("shareEnd") String shareEnd,
                                        @Field("shareStart") String shareStart,
                                        @Field("appKey") String appKey);

    /**
     * 修改自动分享时间段
     *
     * @param appKey
     * @param passengerId
     * @param shareEnd
     * @param shareStart
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/safe/trip_share_auto_time")
    Observable<EmResult> shareAutoTime(@Field("passengerId") long passengerId,
                                       @Field("shareEnd") String shareEnd,
                                       @Field("shareStart") String shareStart,
                                       @Field("appKey") String appKey);

    /**
     * 乘客行程分享内容
     *
     * @param lat
     * @param lng
     * @param orderId 订单id
     * @return
     */
    @GET("api/v1/safe/share_contents")
    Observable<EmResult> shareContents(@Query("lat") double lat,
                                       @Query("lng") double lng,
                                       @Query("orderId") double orderId,
                                       @Query("appKey") String appKey);

    /**
     * 跑单界面短信分享
     *
     * @param serviceType
     * @param orderId
     * @param passengerId
     * @param passengerPhone
     * @param serviceType
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/safe/sms_share")
    Observable<EmResult> smsShare(@Field("companyId") long companyId,
                                  @Field("orderId") long orderId,
                                  @Field("passengerId") long passengerId,
                                  @Field("passengerPhone") long passengerPhone,
                                  @Field("serviceType") String serviceType,
                                  @Field("appKey") String appKey);



    /**
     * 乘客对录音授权
     *
     * @param passengerId
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/safe/authorize")
    Observable<EmResult> authorize(@Field("passengerId") long passengerId,
                                   @Field("appKey") String appKey);

    /**
     * 查询乘客对订单是否开启了录音授权
     *
     * @param passengerAuthorize 乘客是否开启授权1：开启0：未开启
     * @param passengerId
     * @param appKey
     * @return
     */
    @GET("api/v1/safe/check_auth")
    Observable<EmResult> checkAuth(@Query("passengerAuthorize") int passengerAuthorize,
                                   @Query("passengerId") long passengerId,
                                   @Query("appKey") String appKey);

    /**
     * 微信分享授权数据参数生成
     *
     * @param url    行程录音的id
     * @param appKey
     * @return
     */
    @GET("api/v1/safe/wx/auth")
    Observable<EmResult> shareWx(@Query("url") String url,
                                 @Query("appKey") String appKey);


//司机端

    /**
     * 检测自动分享
     *
     * @param orderId
     * @param companyId
     * @param passengerId
     * @param passengerPhone
     * @param serviceType
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/safe/sms_share_auto")
    Observable<EmResult> smsShareAuto(@Field("orderId") long orderId,
                                      @Field("companyId") long companyId,
                                      @Field("passengerId") long passengerId,
                                      @Field("passengerPhone") String passengerPhone,
                                      @Field("serviceType") String serviceType);

    /**
     * 检测乘客授权
     *
     * @param passengerId
     * @return
     */
    @GET("api/v1/safe/checking_auth")
    Observable<EmResult> checkingAuth(@Query("passengerId") long passengerId);


    /**
     * 开始获取七牛云token
     *
     * @return
     */
    @GET("api/v1/safe/app/qny_token")
    Observable<EmResult> anyToken();


    /**
     * 七牛云的语音文件key上传后台
     *
     * @param orderId
     * @param passengerId
     * @param recordFile  录音文件上传到七牛云的key
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/safe/up_sound_record")
    Observable<EmResult> upSoundRecord(@Field("orderId") long orderId,
                                       @Field("passengerId") long passengerId,
                                       @Field("recordFile") String recordFile);

    /**
     * 上线
     *
     * @param driverId
     * @param companyId
     * @param driverNo
     * @param driverName
     * @param driverPhone
     * @param onTime      10位时间戳
     * @param serviceType
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/safe/trip_tired_driving_record")
    Observable<EmResult> driverUp(@Field("driverId") long driverId,
                                  @Field("companyId") long companyId,
                                  @Field("driverNo") String driverNo,
                                  @Field("driverName") String driverName,
                                  @Field("driverPhone") String driverPhone,
                                  @Field("onTime") long onTime,
                                  @Field("serviceType") String serviceType);

    /**
     * 下线
     *
     * @param driverId
     * @param companyId
     * @param driverNo
     * @param driverName
     * @param driverPhone
     * @param offTime     10位时间戳
     * @param serviceType
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/safe/trip_tired_driving_off")
    Observable<EmResult> driverDown(@Field("driverId") long driverId,
                                    @Field("companyId") long companyId,
                                    @Field("driverNo") String driverNo,
                                    @Field("driverName") String driverName,
                                    @Field("driverPhone") String driverPhone,
                                    @Field("offTime") long offTime,
                                    @Field("serviceType") String serviceType);


    @Multipart
    @POST
    Observable<EmResult> uploadPic(@Url String url,
                                   @Part("token") RequestBody token,
                                   @Part MultipartBody.Part audio);
}
