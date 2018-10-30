package com.easymi.common;

import com.easymi.common.entity.Brands;
import com.easymi.common.entity.BusinessList;
import com.easymi.common.entity.CompanyList;
import com.easymi.common.entity.Pic;
import com.easymi.common.entity.PushAnnouncement;
import com.easymi.common.entity.QiNiuToken;
import com.easymi.common.entity.RegisterRes;
import com.easymi.common.entity.Vehicles;
import com.easymi.common.result.AnnouncementResult;
import com.easymi.common.result.LoginResult;
import com.easymi.common.result.MultipleOrderResult;
import com.easymi.common.result.NearDriverResult;
import com.easymi.common.result.NotitfyResult;
import com.easymi.common.result.OrderFeeResult;
import com.easymi.common.result.QueryOrdersResult;
import com.easymi.common.result.SettingResult;
import com.easymi.common.result.SystemResult;
import com.easymi.common.result.WorkStatisticsResult;
import com.easymi.component.result.EmResult;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by developerLzh on 2017/11/17 0017.
 */

public interface CommApiService {

    /**
     * 查询所有订单接口
     *
     * @param driverId
     * @param appKey
     * @return
     */
    @GET("driver/api/v1/runningOrders")
    Observable<QueryOrdersResult> queryRunningOrders(@Query("driver_id") Long driverId,
                                                     @Query("app_key") String appKey,
                                                     @Query("page") int page,
                                                     @Query("limit") int limit);

    /**
     * 上线接口
     *
     * @param driverId
     * @param appKey
     * @return
     */
    @FormUrlEncoded
    @PUT("driver/api/v1/online")
    Observable<EmResult> online(@Field("driver_id") Long driverId,
                                @Field("app_key") String appKey);


    @FormUrlEncoded
    @PUT("driver/api/v1/offline")
    Observable<EmResult> offline(@Field("driver_id") Long driverId,
                                 @Field("app_key") String appKey);


    /**
     * 查询附近司机
     *
     * @param lat
     * @param lng
     * @param distance
     * @param appKey
     * @return
     */
    @GET("driver/api/v1/getNearDrivers")
    Observable<NearDriverResult> getNearDrivers(@Query("driver_id") Long driverId,
                                                @Query("lat") Double lat,
                                                @Query("lng") Double lng,
                                                @Query("distance") Double distance,
                                                @Query("business") String business,
                                                @Query("app_key") String appKey);

    /**
     * 查询未支付和已完成订单
     *
     * @param driverId
     * @param business
     * @param startTime
     * @param endTime
     * @param appKey
     * @param page
     * @param limit
     * @return
     */
    @GET("driver/api/v1/orders")
    Observable<QueryOrdersResult> queryOverOrdersByBunsiness(@Query("driver_id") Long driverId,
                                                             @Query("business") String business,
                                                             @Query("start_time") Long startTime,
                                                             @Query("end_time") Long endTime,
                                                             @Query("app_key") String appKey,
                                                             @Query("driver_name") String driverName,
                                                             @Query("page") int page,
                                                             @Query("limit") int limit);

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
    Observable<NotitfyResult> loadNotice(@Query("driver_id") Long driverId,
                                         @Query("app_key") String appKey,
                                         @Query("page") Integer page,
                                         @Query("limit") Integer limit);


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
    Observable<AnnouncementResult> loadAnn(@Query("company_id") Long companyId,
                                           @Query("app_key") String appKey,
                                           @Query("page") Integer page,
                                           @Query("limit") Integer limit
    );

    /**
     * 首页统计
     *
     * @param driverId
     * @param nowDate
     * @param appKey
     * @return
     */
    @GET("driver/api/v1/driverInfo")
    Observable<WorkStatisticsResult> workStatistics(@Query("driver_id") Long driverId,
                                                    @Query("now_date") String nowDate,
                                                    @Query("app_key") String appKey,
                                                    @Query("is_online") Integer isOnline,
                                                    @Query("minute") int minute,
                                                    @Query("driver_no") String driverNo,
                                                    @Query("company_id") long company_id);

    /**
     * 代驾 --> 查询单个订单
     *
     * @param orderId
     * @param appKey
     * @return
     */
    @GET("driver/api/v1/orderFindOne")
    Observable<MultipleOrderResult> queryDJOrder(@Query("order_id") Long orderId,
                                                 @Query("app_key") String appKey);

    /**
     * 代驾 --> 抢单
     *
     * @param orderId
     * @param driverId
     * @param appKey
     * @return
     */
    @FormUrlEncoded
    @PUT("driver/api/v1/grabOrder")
    Observable<MultipleOrderResult> grabDJOrder(@Field("order_id") Long orderId,
                                                @Field("driver_id") Long driverId,
                                                @Field("app_key") String appKey);

    /**
     * 代驾 -->接单
     *
     * @param orderId
     * @param driverId
     * @param appKey
     * @return
     */
    @FormUrlEncoded
    @POST("driver/api/v1/takeOrder")
    Observable<MultipleOrderResult> takeDJOrder(@Field("order_id") Long orderId,
                                                @Field("driver_id") Long driverId,
                                                @Field("app_key") String appKey);

    /**
     * @param orderId
     * @param baoxiaoMoney  报销金额
     * @param baoxiaoReason 报销理由
     * @param appKey
     * @return
     */
    @FormUrlEncoded
    @POST("/driver/api/v1/wipeOutApply")
    Observable<EmResult> baoxiao(@Field("order_id") Long orderId,
                                 @Field("money") Double baoxiaoMoney,
                                 @Field("reason") String baoxiaoReason,
                                 @Field("app_key") String appKey);

    /**
     * 获取司机信息
     *
     * @param driverId
     * @param appKey
     * @return
     */
    @GET("driver/api/v1/getEmployById")
    Observable<LoginResult> getDriverInfo(@Query("id") Long driverId,
                                          @Query("app_key") String appKey);

    /**
     * 获取APP配置
     *
     * @param appKey
     * @return
     */
    @GET("api/v1/appSetting")
    Observable<SettingResult> getAppSetting(@Query("app_key") String appKey);

    /**
     * 查询通知
     *
     * @param noticeId
     * @return
     */
    @GET("driver/api/v1/notice")
    Observable<NotitfyResult> loadNotice(@Query("id") Long noticeId,
                                         @Query("app_key") String appKey);

    /**
     * 查询通知
     *
     * @param noticeId
     * @return
     */
    @GET("driver/api/v1/employAfficheById")
    Observable<PushAnnouncement> employAfficheById(@Query("id") Long noticeId,
                                                   @Query("app_key") String appKey);

    /**
     * 专车 --> 查询单个订单
     *
     * @param orderId
     * @param appKey
     * @return
     */
    @GET("driver/api/v1/orderFindOne")
    Observable<MultipleOrderResult> queryZCOrder(@Query("order_id") Long orderId,
                                                 @Query("app_key") String appKey);

    /**
     * 专车 --> 抢单
     *
     * @param orderId
     * @param driverId
     * @param appKey
     * @return
     */
    @FormUrlEncoded
    @PUT("driver/api/v1/grabSpecialOrder")
    Observable<MultipleOrderResult> grabZCOrder(@Field("order_id") Long orderId,
                                                @Field("driver_id") Long driverId,
                                                @Field("app_key") String appKey);

    /**
     * 专车 -->接单
     *
     * @param orderId
     * @param driverId
     * @param appKey
     * @return
     */
    @FormUrlEncoded
    @POST("driver/api/v1/takeSpecialOrder")
    Observable<MultipleOrderResult> takeZCOrder(@Field("order_id") Long orderId,
                                                @Field("driver_id") Long driverId,
                                                @Field("app_key") String appKey);


    /**
     * 获取系统配置
     *
     * @param appKey
     * @return
     */
    @GET("driver/api/v1/systemConfig")
    Observable<SystemResult> getSysCofig(@Query("app_key") String appKey);


    /**
     * 读一个通知
     *
     * @param id
     * @param appKey
     * @return
     */
    @FormUrlEncoded
    @PUT("driver/api/v1/readNotice")
    Observable<EmResult> readNotice(@Field("id") Long id,
                                    @Field("app_key") String appKey);


    @GET("manager/api/v1/companyByAppKey")
    Observable<CompanyList> getCompanyList(@Query("app_key") String appKey);


    @GET("driver/api/v1/getCompanyBusinesses")
    Observable<BusinessList> getBusinessList(@Query("app_key") String appKey);


    @GET("driver/api/v1/getToken")
    Observable<QiNiuToken> getToken(@Query("app_key") String appKey,
                                    @Query("id") long id);

    @Multipart
    @POST
    Observable<Pic> uploadPic(@Url String url,
                              @Part("token") RequestBody token,
                              @Part MultipartBody.Part photo);


    /**
     * @param appKey
     * @param driverId
     * @param idCard              身份证号码
     * @param emergency           紧急联系人（姓名+电话+关系
     * @param emergencyPhone      紧急电话
     * @param introducer          推荐人
     * @param companyId
     * @param serviceType         服务类型
     * @param portraitPath        头像照片地址
     * @param idCardPath          身份证照片地址
     * @param idCardBackPath      身份证背面地址
     * @param driveLicensePath    驾驶证照片地址
     * @param fullBodyPath        全身照地址
     * @param carPhoto            车辆照片地址
     * @param brand               车辆厂牌
     * @param model
     * @param plateColor          车牌颜色
     * @param vehicleNo           车牌号码
     * @param vehicleType         车辆使用类型,
     * @param seats               核定载客位
     * @param mileage             已行驶里程
     * @param useProperty         使用性质
     * @param vin                 车辆识别VIN
     * @param fuelType            燃料类型
     * @param buyDate             购车日期
     * @param certifyDate         车辆注册日期
     * @param drivingLicensePhoto 行驶证照片
     * @param nextFixDate         车辆下次年检日期
     * @param transPhoto          车辆运输证照
     * @param vehicleColor        车辆颜色
     * @return
     */
    @FormUrlEncoded
    @POST("driver/api/v1/completeEmploy")
    Observable<RegisterRes> register(@Field("app_key") String appKey,
                                     @Field("driver_id") long driverId,
                                     @Field("id_card") String idCard,
                                     @Field("emergency") String emergency,
                                     @Field("emergency_phone") String emergencyPhone,
                                     @Field("introducer") String introducer,
                                     @Field("companyId") long companyId,
                                     @Field("service_type") String serviceType,
                                     @Field("portrait_path") String portraitPath,
                                     @Field("idcard_path") String idCardPath,
                                     @Field("idcard_back_path") String idCardBackPath,
                                     @Field("drive_license_path") String driveLicensePath,
                                     @Field("full_body_path") String fullBodyPath,
                                     //如果选择有车注册下面的参数
                                     @Field("photo") String carPhoto,
                                     @Field("brand") String brand,
                                     @Field("model") String model,
                                     @Field("plate_color") String plateColor,
                                     @Field("vehicle_no") String vehicleNo,
                                     @Field("vehicle_type") String vehicleType,
                                     @Field("seats") Integer seats,
                                     @Field("mileage") Float mileage,
                                     @Field("use_property") String useProperty,
                                     @Field("vin") String vin,
                                     @Field("fuel_type") String fuelType,
                                     @Field("buy_date") Long buyDate,
                                     @Field("certify_date_a") Long certifyDate,
                                     @Field("driving_license_photo") String drivingLicensePhoto,
                                     @Field("next_fix_date") Long nextFixDate,
                                     @Field("trans_photo") String transPhoto,
                                     @Field("vehicle_color") String vehicleColor);


    @GET("api/v1/brands")
    Observable<Brands> getBrands();

    @GET("api/v1/vehicles")
    Observable<Vehicles> getVehicles(@Query("brand_id") long brandId,
                                     @Query("page") int page,
                                     @Query("limit") int limit);

    /**
     * 代驾订单上传距离
     *
     * @param orderId
     * @param distance
     * @param appKey
     * @param state
     * @param dark_distance
     * @param dark_price
     * @param lat
     * @param lng
     * @return
     */
    @GET("/driver/api/v1/pullFee")
    Observable<OrderFeeResult> pushDistance(@Query("order_id") Long orderId,
                                            @Query("distance") Double distance,
                                            @Query("app_key") String appKey,
                                            @Query("state") Integer state,
                                            @Query("dark_distance") Double dark_distance,
                                            @Query("dark_price") Double dark_price,
                                            @Query("lat") Double lat,
                                            @Query("lng") Double lng);

    /**
     * http方式推送gps信息
     * @param gpsContent
     * @param appKey
     * @return
     */
    @FormUrlEncoded
    @POST("driver/api/v1/gpsPush")
    Observable<EmResult> gpsPush(@Field("gps_content") String gpsContent,
                               @Field("app_key") String appKey);
}
