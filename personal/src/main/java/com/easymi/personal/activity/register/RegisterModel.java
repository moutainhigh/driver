package com.easymi.personal.activity.register;

import android.content.Context;
import android.text.TextUtils;

import com.easymi.common.CommApiService;
import com.easymi.common.entity.BusinessList;
import com.easymi.common.entity.CompanyList;
import com.easymi.common.entity.Pic;
import com.easymi.common.entity.QiNiuToken;
import com.easymi.common.entity.RegisterRes;
import com.easymi.common.register.RegisterRequest;
import com.easymi.component.Config;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.GsonUtil;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.result.EmResult;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.Log;
import com.easymi.component.utils.RsaUtils;
import com.easymi.personal.McService;
import com.easymi.personal.result.LoginResult;
import com.easymi.personal.result.RegisterResult;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Field;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: RegisterModel
 * @Author: shine
 * Date: 2018/12/19 下午3:25
 * Description:
 * History:
 */
public class RegisterModel {

    /**
     * 发送短信验证码
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
     * 上传图片到七牛云
     * @param file
     * @param token
     * @return
     */
    public static Observable<Pic> putPic(File file, String token) {
        RequestBody photoRequestBody = RequestBody.create(MediaType.parse("image/jpg"), file);
        RequestBody tokenBody = RequestBody.create(MediaType.parse("multipart/form-data"), token);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), photoRequestBody);
        return ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .uploadPic(Config.HOST_UP_PIC, tokenBody, body);
    }


    /**
     * 司机注册提交审核资料
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

//        Log.e("hufeng",GsonUtil.toJson(request));
//        Log.e("hufeng/portraitPath",portraitPath);
//        Log.e("hufeng/idCardPath",idCardPath);
//        Log.e("hufeng/idCardBackPath",idCardBackPath);
//        Log.e("hufeng/driveLicensePath",driveLicensePath);

        return ApiManager.getInstance().createApi(Config.HOST, McService.class)
                .applyDriver(
                        RsaUtils.encryptAndEncode(context, request.driverId + ""),
                        RsaUtils.encryptAndEncode(context, request.driverName + ""),
                        RsaUtils.encryptAndEncode(context, request.driverPhone + ""),
                        RsaUtils.encryptAndEncode(context, request.idCard + ""),
                        RsaUtils.encryptAndEncode(context, request.emergency + ""),
                        RsaUtils.encryptAndEncode(context, request.emergencyPhone + ""),
                        RsaUtils.encryptAndEncode(context, request.companyId + ""),
                        RsaUtils.encryptAndEncode(context, request.serviceType + ""),
                        RsaUtils.encryptAndEncode(context, request.startTime + ""),
                        RsaUtils.encryptAndEncode(context, request.endTime + ""),
                        RsaUtils.encryptAndEncode(context, request.introducer + ""),
                        RsaUtils.encryptAndEncode(context, portraitPath + ""),
                        RsaUtils.encryptAndEncode(context, idCardPath + ""),
                        RsaUtils.encryptAndEncode(context, idCardBackPath + ""),
                        RsaUtils.encryptAndEncode(context, driveLicensePath + ""))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 批量4张上传图片
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

//                    List<Observable<Pic>> lsit = new ArrayList<>();
                    //必传图片
//                    if (!TextUtils.isEmpty(request.portraitPath)) {
                        portraitPic = putPic(new File(request.portraitPath), token);
//                        lsit.add(portraitPic);
//                    }
//                    if (!TextUtils.isEmpty(request.idCardPath)) {
                        idCardPic = putPic(new File(request.idCardPath), token);
//                        lsit.add(idCardPic);
//                    }
//                    if (!TextUtils.isEmpty(request.idCardBackPath)) {
                        idCardBackPic = putPic(new File(request.idCardBackPath), token);
//                        lsit.add(idCardBackPic);
//                    }
//                    if (!TextUtils.isEmpty(request.driveLicensePath)) {
                        driveLicensePic = putPic(new File(request.driveLicensePath), token);
//                        lsit.add(driveLicensePic);
//                    }
//
                    Observable<Pic> pics;
////                    //todo 为空能加入不并不知道
////                    for (int i = 0;i<lsit.size();i++){
////
////                    }
//                    if (portraitPic == null ){
//
//                    }
                    pics = Observable.concat(portraitPic, idCardPic, idCardBackPic, driveLicensePic);

                    return pics.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread());
                });
    }

    /**
     * 提交电话号码和验证码密码去注册基本司机信息信息
     * @param password
     * @param phone
     * @param smsCode
     * @return
     */
    public static Observable<LoginResult> register(String password, String phone, String smsCode) {
        return ApiManager.getInstance().createApi(Config.HOST, McService.class)
                .register(password, phone, smsCode)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取司机之前的注册信息
     * @param driverId
     * @return
     */
    public static Observable<RegisterResult> getDriverInfo(String driverId) {
        return ApiManager.getInstance().createApi(Config.HOST, McService.class)
                .getDriverInfo(driverId)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取七牛云token
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
     * @param context
     * @param request
     * @return
     */
    public static Observable<RegisterRes> applyUpdate(Context context, RegisterRequest request) {

        String portraitPath = request.portraitPath;
        String idCardPath = request.idCardPath;
        String idCardBackPath = request.idCardBackPath;
        String driveLicensePath = request.driveLicensePath;

        return ApiManager.getInstance().createApi(Config.HOST, McService.class)
                .applyUpdate(
                        RsaUtils.encryptAndEncode(context, request.driverId + ""),
                        RsaUtils.encryptAndEncode(context, request.driverName + ""),
                        RsaUtils.encryptAndEncode(context, request.driverPhone + ""),
                        RsaUtils.encryptAndEncode(context, request.idCard + ""),
                        RsaUtils.encryptAndEncode(context, request.emergency + ""),
                        RsaUtils.encryptAndEncode(context, request.emergencyPhone + ""),
                        RsaUtils.encryptAndEncode(context, request.companyId + ""),
                        RsaUtils.encryptAndEncode(context, request.serviceType + ""),
                        RsaUtils.encryptAndEncode(context, request.startTime + ""),
                        RsaUtils.encryptAndEncode(context, request.endTime + ""),
                        RsaUtils.encryptAndEncode(context, request.introducer + ""),
                        RsaUtils.encryptAndEncode(context, portraitPath + ""),
                        RsaUtils.encryptAndEncode(context, idCardPath + ""),
                        RsaUtils.encryptAndEncode(context, idCardBackPath + ""),
                        RsaUtils.encryptAndEncode(context, driveLicensePath + ""),
                        RsaUtils.encryptAndEncode(context, request.version + ""))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
