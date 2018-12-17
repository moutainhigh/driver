package com.easymin.driver.securitycenter.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.easymin.driver.securitycenter.CenterConfig;
import com.easymin.driver.securitycenter.ComService;
import com.easymin.driver.securitycenter.network.ApiManager;
import com.easymin.driver.securitycenter.network.HttpResultFunc;
import com.easymin.driver.securitycenter.network.MySubscriber;
import com.easymin.driver.securitycenter.result.EmResult;
import com.easymin.driver.securitycenter.rxmvp.RxManager;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: CenterUtil
 * Author: shine
 * Date: 2018/12/14 下午1:43
 * Description:
 * History:
 */
public class CenterUtil {

    private Context mContext;

    public CenterUtil(Context context) {
        this.mContext = context;
    }

    public CenterUtil(Context context,String appKey,String aeskey,String token) {
        this.mContext = context;
        CenterConfig.APPKEY = appKey;
        CenterConfig.AES_KEY = aeskey;
        CenterConfig.TOKEN = token;
    }

    //检测自动分享
    public void smsShareAuto(long orderId, long companyId, long passengerId, String passengerPhone, String serviceType) {
        Observable<EmResult> observable = ApiManager.getInstance().createApi(CenterConfig.HOST, ComService.class)
                .smsShareAuto(orderId, companyId, passengerId, passengerPhone, serviceType)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        new RxManager().add(observable.subscribe(new MySubscriber<>(mContext, false,
                true, emResult -> {

        })));
    }

    //检测乘客录音授权
    public void checkingAuth(long passengerId) {
        Observable<EmResult> observable = ApiManager.getInstance().createApi(CenterConfig.HOST, ComService.class)
                .checkingAuth(passengerId)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        new RxManager().add(observable.subscribe(new MySubscriber<>(mContext, false,
                true, emResult -> {
            AudioUtil audioUtil = new AudioUtil();
//                audioUtil.onRecord(mContext,true);
            anyToken();
        })));
    }

    //获取七牛云token
    public void anyToken() {
        Observable<EmResult> observable = ApiManager.getInstance().createApi(CenterConfig.HOST, ComService.class)
                .anyToken()
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        new RxManager().add(observable.subscribe(new MySubscriber<>(mContext, false,
                true, emResult -> {

        })));
    }

    //七牛云的语音文件key上传后台
    public void upSoundRecord(long orderId, long passengerId, String recordFile) {
        Observable<EmResult> observable = ApiManager.getInstance().createApi(CenterConfig.HOST, ComService.class)
                .upSoundRecord(orderId, passengerId, recordFile)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        new RxManager().add(observable.subscribe(new MySubscriber<>(mContext, false,
                true, emResult -> {

        })));
    }

    //上线
    public void driverUp(long driverId, long companyId, String driverNo, String driverName,
                         String driverPhone, long onTime, String serviceType) {
        Observable<EmResult> observable = ApiManager.getInstance().createApi(CenterConfig.HOST, ComService.class)
                .driverUp(driverId, companyId, driverNo, driverName, driverPhone, onTime, serviceType)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        new RxManager().add(observable.subscribe(new MySubscriber<>(mContext, false,
                true, emResult -> {
            ToastUtil.showMessage(mContext,"driverUp");
        })));
    }

    //下线
    public void driverDown(long driverId, long companyId, String driverNo, String driverName,
                           String driverPhone, long offTime, String serviceType) {
        Observable<EmResult> observable = ApiManager.getInstance().createApi(CenterConfig.HOST, ComService.class)
                .driverDown(driverId, companyId, driverNo, driverName, driverPhone, offTime, serviceType)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        new RxManager().add(observable.subscribe(new MySubscriber<>(mContext, false,
                true, emResult -> {

        })));
    }

    //上传音频文件到七牛云
    public void putAudio(File file, String token) {
        RequestBody audioRequestBody = RequestBody.create(MediaType.parse("image/jpg"), file);
        RequestBody tokenBody = RequestBody.create(MediaType.parse("multipart/form-data"), token);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), audioRequestBody);

        Observable<EmResult> observable = ApiManager.getInstance().createApi(CenterConfig.HOST, ComService.class)
                .uploadPic(CenterConfig.QINIU_HOST, tokenBody, body)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        new RxManager().add(observable.subscribe(new MySubscriber<>(mContext, false,
                true, emResult -> {

        })));
    }
}
