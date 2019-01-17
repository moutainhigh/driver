package com.easymi.common.register;

import android.text.TextUtils;

import com.easymi.common.CommApiService;
import com.easymi.common.entity.BusinessList;
import com.easymi.common.entity.CompanyList;
import com.easymi.common.entity.Pic;
import com.easymi.common.entity.QiNiuToken;
import com.easymi.common.entity.RegisterRes;
import com.easymi.component.Config;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.utils.EmUtil;

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
 * @author hufeng
 * 已废弃
 */
public class RegisterModel {

    public static Observable<CompanyList> getCompany() {
        return ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .getCompanyList(EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<BusinessList> getBusinessList() {
        return ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .getBusinessList(EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private static Observable<Pic> putPic(File file, String token) {
        RequestBody photoRequestBody = RequestBody.create(MediaType.parse("image/jpg"), file);
        RequestBody tokenBody = RequestBody.create(MediaType.parse("multipart/form-data"), token);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), photoRequestBody);
        return ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .uploadPic(Config.HOST_UP_PIC, tokenBody, body);
    }

    public static Observable<RegisterRes> register(RegisterRequest request, List<String> pics) {

        request.portraitPath = pics.get(0);
        request.idCardPath = pics.get(1);
        request.idCardBackPath = pics.get(2);
        request.driveLicensePath = pics.get(3);
        request.fullBodyPath = pics.get(4);

        if (pics.size() == 8) {
            request.transPhoto = pics.get(7);
            request.carPhoto = pics.get(5);
            request.drivingLicensePhoto = pics.get(6);
        } else if (pics.size() >= 7) {
            request.carPhoto = pics.get(5);
            request.drivingLicensePhoto = pics.get(6);
        }

        return ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .register(EmUtil.getAppKey(), request.driverId, request.idCard, request.emergency, request.emergencyPhone, request.introducer,
                        request.companyId, request.serviceType, request.portraitPath, request.idCardPath, request.idCardBackPath, request.driveLicensePath,
                        request.fullBodyPath, request.carPhoto, request.brand, request.model, request.plateColor, request.vehicleNo, request.vehicleType, request.seats,
                        request.mileage, request.useProperty, request.vin, request.fuelType, request.buyDate, request.certifyDate, request.drivingLicensePhoto, request.nextFixDate,
                        request.transPhoto, request.vehicleColor)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public static Observable<Pic> uploadPics(RegisterRequest request) {
        return ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .getToken(EmUtil.getAppKey(), request.driverId)
                .subscribeOn(Schedulers.io())
                .flatMap((Func1<QiNiuToken, Observable<Pic>>) qiNiuToken -> {
                    String token = qiNiuToken.qiNiu;
                    if (token == null) {
                        throw new IllegalArgumentException("token无效");
                    }

                    //必传图片
                    Observable<Pic> portraitPic = putPic(new File(request.portraitPath), token);
                    Observable<Pic> idCardPic = putPic(new File(request.idCardPath), token);
                    Observable<Pic> idCardBackPic = putPic(new File(request.idCardBackPath), token);
                    Observable<Pic> driveLicensePic = putPic(new File(request.driveLicensePath), token);
                    Observable<Pic> fullBodyPic = putPic(new File(request.fullBodyPath), token);
                    Observable<Pic> pics;

                    //可选图片,有车注册
                    if (!TextUtils.isEmpty(request.vehicleNo)) {
                        //车照片
                        Observable<Pic> carPhotoPic = putPic(new File(request.carPhoto), token);
                        //行驶证照片
                        Observable<Pic> drivingLicensePic = putPic(new File(request.drivingLicensePhoto), token);

                        if (request.transPhoto != null) {
                            Observable<Pic> transPhotoPic = putPic(new File(request.transPhoto), token);
                            pics = Observable.concat(portraitPic, idCardPic, idCardBackPic, driveLicensePic, fullBodyPic, carPhotoPic, drivingLicensePic, transPhotoPic);
                        } else {
                            pics = Observable.concat(portraitPic, idCardPic, idCardBackPic, driveLicensePic, fullBodyPic, carPhotoPic, drivingLicensePic);
                        }
                    } else {
                        pics = Observable.concat(portraitPic, idCardPic, idCardBackPic, driveLicensePic, fullBodyPic);
                    }

                    return pics.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread());
                });
    }


}
