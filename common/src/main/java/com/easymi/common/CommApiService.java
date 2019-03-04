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
import com.easymi.common.result.CityLineResult;
import com.easymi.common.result.LoginResult;
import com.easymi.common.result.MultipleOrderResult;
import com.easymi.common.result.NearDriverResult;
import com.easymi.common.result.NotitfyResult;
import com.easymi.common.result.GetFeeResult;
import com.easymi.common.result.QueryOrdersResult;
import com.easymi.common.result.SettingResult;
import com.easymi.common.result.SystemResult;
import com.easymi.common.result.VehicleResult;
import com.easymi.common.result.WorkStatisticsResult;
import com.easymi.component.result.EmResult;
import com.easymi.component.result.EmResult2;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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
 * FileName:
 * @Author: shine
 * Date: 2018/12/24 下午5:00
 * Description:
 * History:
 */

public interface CommApiService {

    /**
     * 查询附近司机
     *
     * @param lat
     * @param lng
     * @param range
     * @param serviceType
     * @return
     */
    @GET("api/v1/public/driver/ranges")
    Observable<NearDriverResult> getNearDrivers(@Query("lat") Double lat,
                                                @Query("lng") Double lng,
                                                @Query("range") Double range,
                                                @Query("serviceType") String serviceType);

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
     * 获取APP配置
     *
     * @param appKey
     * @return
     */
    @GET("api/v1/appSetting")
    Observable<SettingResult> getAppSetting(@Query("app_key") String appKey);

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

    @GET("/driver/api/v1/pullFee")
    Observable<GetFeeResult> pullFee(@Query("order_id") long orderId,
                                     @Query("distance") double distance,
                                     @Query("app_key") String appKey,
                                     @Query("state") int state,
                                     @Query("dark_distance") double dark_distance,
                                     @Query("dark_price") double dark_price,
                                     @Query("lat") double lat,
                                     @Query("lng") double lng);

    //add hufeng

    /**
     * 上线接口
     *
     * @param id
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/public/driver/online")
    Observable<EmResult> online(@Field("id") Long id, @Field("companyId") Long companyId);

    /**
     * 下线接口
     *
     * @param id
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/public/driver/offline")
    Observable<EmResult> offline(@Field("id") Long id, @Field("companyId") Long companyId);


    /**
     * 推送绑定接口
     *
     * @param userId
     * @param aliBaBaKey
     * @param mqttKey
     * @param mqttClientId
     * @param driverType
     * @param type
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/public/message/push/binding")
    Observable<EmResult> pushBinding(@Field("userId") long userId,
                                     @Field("aliBaBaKey") String aliBaBaKey,
                                     @Field("mqttKey") String mqttKey,
                                     @Field("mqttClientId") String mqttClientId,
                                     @Field("driverType") String driverType,
                                     @Field("type") int type);

    /**
     * 专车 --> 查询单个订单
     *
     * @param appKey
     * @return
     */
    @GET("api/v1/taxi_online/order/get/{id}")
    Observable<MultipleOrderResult> queryZCOrder(@Path("id") Long id,
                                                 @Query("appKey") String appKey);

    /**
     * 专车 --> 抢单
     *
     * @param driverId
     * @param driverName
     * @param driverPhone
     * @param id          订单id
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/taxi_online/order/grab")
    Observable<MultipleOrderResult> grabZCOrder(@Field("driverId") Long driverId,
                                                @Field("driverName") String driverName,
                                                @Field("driverPhone") String driverPhone,
                                                @Field("id") Long id,
                                                @Field("version") Long version,
                                                @Field("receiptLongitude") String receiptLongitude,
                                                @Field("receiptLatitude") String receiptLatitude);

    /**
     * 专车 -->接单
     *
     * @param driverId
     * @param driverName
     * @param driverPhone
     * @param id          订单id
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/taxi_online/order/take")
    Observable<MultipleOrderResult> takeZCOrder(@Field("driverId") Long driverId,
                                                @Field("driverName") String driverName,
                                                @Field("driverPhone") String driverPhone,
                                                @Field("id") Long id,
                                                @Field("version") Long version,
                                                @Field("receiptLongitude") String receiptLongitude,
                                                @Field("receiptLatitude") String receiptLatitude);

    /**
     * 出租车 --> 查询单个订单
     *
     * @param appKey
     * @return
     */
    @GET("api/v1/taxi/normal/order/{id}")
    Observable<MultipleOrderResult> queryTaxiOrder(@Path("id") Long id,
                                                   @Query("appKey") String appKey);

    /**
     * 出租车 -->接单
     *
     * @param appKey
     * @param companyId
     * @param driverId
     * @param orderId
     * @return
     */
    @FormUrlEncoded
    @PUT("api/v1/taxi/normal/order/receipt")
    Observable<MultipleOrderResult> takeTaxiOrder(@Field("appKey") String appKey,
                                                  @Field("companyId") Long companyId,
                                                  @Field("driverId") Long driverId,
                                                  @Field("orderId") Long orderId);

    /**
     * 出租车 --> 抢单
     *
     * @param appKey
     * @param companyId
     * @param driverId
     * @param orderId
     * @return
     */
    @FormUrlEncoded
    @PUT("api/v1/taxi/normal/order/grab")
    Observable<MultipleOrderResult> grabTaxiOrder(@Field("appKey") String appKey,
                                                  @Field("companyId") Long companyId,
                                                  @Field("driverId") Long driverId,
                                                  @Field("orderId") Long orderId);

    /**
     * 获取司机信息
     *
     * @param driverId
     * @param appKey
     * @return
     */
    @GET("api/v1/public/driver/get/{id}")
    Observable<LoginResult> getDriverInfo(@Path("id") Long driverId,
                                          @Query("appKey") String appKey);

    /**
     * 通过http上传位置信息，30秒一次
     *
     * @param appKey
     * @param json
     * @return
     */
    @POST("api/v1/public/message/location")
    @FormUrlEncoded
    Observable<GetFeeResult> gpsPush(@Field("app_key") String appKey,
                                     @Field("json") String json);

    /**
     * 获取工作台 出租车 列表
     */
    @GET("api/v1/taxi/normal/orders")
    Observable<QueryOrdersResult> getTaxiOrders(@Query("driverPhone") String driverPhone,
                                                @Query("page") int page,
                                                @Query("size") int size,
                                                @Query("status") String status);

    /**
     * 获取工作台 城际专线 列表
     */
    @GET("api/v1/bus/city/schedule/queryDriverSchedule")
    Observable<CityLineResult> getCityLineOrders(@Query("driverId") Long driverId,
                                                 @Query("appKey") String appKey);

    /**
     * 获取工作台 专车 列表
     */
    @GET("api/v1/taxi_online/order/running_list")
    Observable<QueryOrdersResult> getZCOrders(@Query("driverId") Long driverId,
                                              @Query("appKey") String appKey);

    /**
     * 业务订单
     *
     * @param startTime
     * @param endTime
     * @param page
     * @param size
     * @return
     */
    @GET("api/v1/public/order/cold/list")
    Observable<QueryOrdersResult> queryOverOrdersByBunsiness(@Query("startTime") Long startTime,
                                                             @Query("endTime") Long endTime,
                                                             @Query("page") int page,
                                                             @Query("size") int size);

    /**
     * 首页统计
     *
     * @return
     */
    @GET("api/v1/public/statistics/driver_profit/get")
    Observable<WorkStatisticsResult> workStatistics();

    /**
     * 获取工作台 所有业务列表
     */
    @GET("api/v1/public/order/hot/list")
    Observable<QueryOrdersResult> queryRunningOrders(@Query("page") int page,
                                                     @Query("size") int size,
                                                     @Query("status") String status);


    /**
     * 通用拒单 专车出租车用
     */
    @FormUrlEncoded
    @PUT("api/v1/public/order/refusal")
    Observable<EmResult> refuseOrder(@Field("orderId") long orderId,
                                     @Field("serviceType") String serviceType,
                                     @Field("remark") String remark);

    /**
     * @param orderId
     * @param reimburseFee   报销金额
     * @param reimburseCause 报销理由
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/public/finance/order_reimburse/save")
    Observable<EmResult> baoxiao(@Field("orderId") Long orderId,
                                 @Field("reimburseFee") Double reimburseFee,
                                 @Field("reimburseCause") String reimburseCause,
                                 @Field("reimburseType") String reimburseType
    );

    /**
     * 查询单个通知
     *
     * @param noticeId
     * @return
     */
    @GET("api/v1/public/notice/employ/record/{id}")
    Observable<NotitfyResult> loadNotice(@Path("id") Long noticeId,
                                         @Query("app_key") String appKey);

    /**
     * 查询单个公告
     *
     * @param noticeId
     * @return
     */
    @GET("api/v1/public/message/affiche/{id}")
    Observable<PushAnnouncement> employAfficheById(@Path("id") Long noticeId,
                                                   @Query("app_key") String appKey);

    /**
     * 我的订单接口
     */
    @GET("api/v1/public/orders")
    Observable<QueryOrdersResult> queryMyOrders(@Query("page") int page,
                                                @Query("size") int size,
                                                @Query("status") String status,
                                                @Query("serviceType") String serviceType);

    /**
     * 获取专车出租车的车型
     *
     * @return
     */
    @GET("api/v1/public/driver/vehicle")
    Observable<VehicleResult> driverehicle();

    /**
     * 获取APP配置
     *
     * @return
     */
    @GET("api/v1/public/driver/app")
    Observable<SettingResult> getAppSetting();


    /**
     * 班次结束
     *
     * @param scheduleId
     * @return
     */
    @FormUrlEncoded
    @POST("/api/v1/carpool/driver/schedule/finishSchedule")
    Observable<EmResult2<Object>> finishTask(
            @Field("scheduleId") long scheduleId);
}
