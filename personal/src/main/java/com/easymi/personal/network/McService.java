package com.easymi.personal.network;

import com.easymi.personal.result.LoginResult;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by liuzihao on 2017/11/16.
 * 个人模块 api
 */

public interface McService {

    @FormUrlEncoded
    @POST("api/v1/employLogin")
    Observable<LoginResult> login(@Field("username") String userName,
                                  @Field("password") String psw,
                                  @Field("app_key")String appKey);

}
