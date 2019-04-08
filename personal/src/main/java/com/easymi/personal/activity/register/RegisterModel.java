package com.easymi.personal.activity.register;

import android.content.Context;

import com.easymi.common.CommApiService;
import com.easymi.common.entity.CompanyList;
import com.easymi.common.entity.Pic;
import com.easymi.common.entity.QiNiuToken;
import com.easymi.common.entity.RegisterRes;
import com.easymi.common.register.RegisterRequest;
import com.easymi.component.Config;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.result.EmResult;
import com.easymi.component.utils.RsaUtils;
import com.easymi.personal.McService;
import com.easymi.personal.entity.Register;
import com.easymi.personal.result.BusinessResult;
import com.easymi.personal.result.RegisterResult;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: RegisterModel
 *
 * @Author: shine
 * Date: 2018/12/19 下午3:25
 * Description:
 * History:
 */
public class RegisterModel {

    /**
     * 发送短信验证码
     *
     * @param code
     * @param phone
     * @param random
     * @param type
     * @param userType
     * @return
     */
    public static Observable<EmResult> getSms(String code, String phone, String random, String type, String userType) {
        return ApiManager.getInstance().createApi(Config.HOST, McService.class)
                .sendSms(code, phone, random, type, userType)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取注册选择的所属公司（服务机构）
     *
     * @return
     */
    public static Observable<CompanyList> getCompanys() {
        return ApiManager.getInstance().createApi(Config.HOST, McService.class)
                .qureyCompanys()
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 根据服务机构获取对应业务
     *
     * @return
     */
    public static Observable<BusinessResult> getBusinessType(long id) {
        return ApiManager.getInstance().createApi(Config.HOST, McService.class)
                .getBusinessType(id)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 上传图片到七牛云
     *
     * @param file
     * @param token
     * @return
     */
    public static Observable<Pic> putPic(File file, String token) {
        RequestBody photoRequestBody = RequestBody.create(MediaType.parse("image/jpg"), file);
        RequestBody tokenBody = RequestBody.create(MediaType.parse("multipart/form-data"), token);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), photoRequestBody);
        return ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .uploadPic(Config.HOST_UP_PIC, tokenBody, body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 司机注册提交审核资料
     *
     * @param context
     * @param request
     * @param pics
     * @return
     */
    public static Observable<RegisterRes> applyDriver(Context context, RegisterRequest request, List<String> pics) {

        String portraitPath = pics.get(0);
        String idCardPath = pics.get(1);
        String idCardBackPath = pics.get(2);
        String driveLicensePath = pics.get(3);

        String practitionersPhoto = pics.get(4);

        return ApiManager.getInstance().createApi(Config.HOST, McService.class)
                .applyDriver(
                        RsaUtils.rsaEncode(request.id + ""),
                        RsaUtils.rsaEncode(request.driverName + ""),
                        RsaUtils.rsaEncode(request.driverPhone + ""),
                        RsaUtils.rsaEncode(request.idCard + ""),
                        RsaUtils.rsaEncode(request.emergency + ""),
                        RsaUtils.rsaEncode(request.emergencyPhone + ""),
                        RsaUtils.rsaEncode(request.companyId + ""),
                        RsaUtils.rsaEncode(request.serviceType + ""),
                        RsaUtils.rsaEncode(request.startTime + ""),
                        RsaUtils.rsaEncode(request.endTime + ""),
                        RsaUtils.rsaEncode(request.introducer + ""),
                        RsaUtils.rsaEncode(portraitPath + ""),
                        RsaUtils.rsaEncode(idCardPath + ""),
                        RsaUtils.rsaEncode(idCardBackPath + ""),
                        RsaUtils.rsaEncode(driveLicensePath + ""),

                        RsaUtils.rsaEncode(request.netCarQualificationsStart + ""),
                        RsaUtils.rsaEncode(request.netCarQualificationsEnd + ""),
                        RsaUtils.rsaEncode(practitionersPhoto + "")
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 批量4张上传图片
     *
     * @param request
     * @return
     */
    public static Observable<Pic> uploadPics(RegisterRequest request) {
        return ApiManager.getInstance().createApi(Config.HOST, McService.class)
                .getToken()
                .subscribeOn(Schedulers.io())
                .flatMap((Func1<QiNiuToken, Observable<Pic>>) qiNiuToken -> {
                    String token = qiNiuToken.qiNiu;
                    if (token == null) {
                        throw new IllegalArgumentException("token无效");
                    }
                    Observable<Pic> portraitPic = null;
                    Observable<Pic> idCardPic = null;
                    Observable<Pic> idCardBackPic = null;
                    Observable<Pic> driveLicensePic = null;
                    Observable<Pic> zigeLicensePic = null;

                    portraitPic = putPic(new File(request.portraitPath), token);
                    idCardPic = putPic(new File(request.idCardPath), token);
                    idCardBackPic = putPic(new File(request.idCardBackPath), token);
                    driveLicensePic = putPic(new File(request.driveLicensePath), token);
                    zigeLicensePic = putPic(new File(request.practitionersPhoto), token);

                    Observable<Pic> pics;

                    pics = Observable.concat(portraitPic, idCardPic, idCardBackPic, driveLicensePic, zigeLicensePic);

                    return pics.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread());
                });
    }

    /**
     * 提交电话号码和验证码密码去注册基本司机信息信息
     *
     * @param password
     * @param phone
     * @param smsCode
     * @return
     */
    public static Observable<Register> register(String password, String phone, String smsCode, String random) {
        return ApiManager.getInstance().createApi(Config.HOST, McService.class)
                .register(password, phone, smsCode, random)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取司机之前的注册信息
     *
     * @param phone
     * @return
     */
    public static Observable<RegisterResult> getDriverInfo(String phone) {
        return ApiManager.getInstance().createApi(Config.HOST, McService.class)
                .getDriverInfo(phone)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取七牛云token
     *
     * @return
     */
    public static Observable<QiNiuToken> getQiniuToken() {
        return ApiManager.getInstance().createApi(Config.HOST, McService.class)
                .getToken()
                .subscribeOn(Schedulers.io())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 更新注册提交资料
     *
     * @param context
     * @param request
     * @return
     */
    public static Observable<RegisterRes> applyUpdate(Context context, RegisterRequest request) {

        String portraitPath = request.portraitPath;
        String idCardPath = request.idCardPath;
        String idCardBackPath = request.idCardBackPath;
        String driveLicensePath = request.driveLicensePath;
        String practitionersPhoto = request.practitionersPhoto;

        return ApiManager.getInstance().createApi(Config.HOST, McService.class)
                .applyDriver(
                        RsaUtils.rsaEncode(request.id + ""),
                        RsaUtils.rsaEncode(request.driverName + ""),
                        RsaUtils.rsaEncode(request.driverPhone + ""),
                        RsaUtils.rsaEncode(request.idCard + ""),
                        RsaUtils.rsaEncode(request.emergency + ""),
                        RsaUtils.rsaEncode(request.emergencyPhone + ""),
                        RsaUtils.rsaEncode(request.companyId + ""),
                        RsaUtils.rsaEncode(request.serviceType + ""),
                        RsaUtils.rsaEncode(request.startTime + ""),
                        RsaUtils.rsaEncode(request.endTime + ""),
                        RsaUtils.rsaEncode(request.introducer + ""),
                        RsaUtils.rsaEncode(portraitPath + ""),
                        RsaUtils.rsaEncode(idCardPath + ""),
                        RsaUtils.rsaEncode(idCardBackPath + ""),
                        RsaUtils.rsaEncode(driveLicensePath + ""),

                        RsaUtils.rsaEncode(request.netCarQualificationsStart + ""),
                        RsaUtils.rsaEncode(request.netCarQualificationsEnd + ""),
                        RsaUtils.rsaEncode(practitionersPhoto + ""))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
