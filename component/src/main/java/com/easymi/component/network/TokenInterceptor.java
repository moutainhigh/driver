package com.easymi.component.network;

import com.easymi.component.Config;
import com.easymi.component.app.XApp;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * token 拦截器:用来统一添加token参数
 */
public class TokenInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String sToken = XApp.getMyPreferences().getString(Config.SP_TOKEN, "");
        return chain.proceed(request.newBuilder()
                .addHeader("token", sToken)
                .addHeader("appKey", Config.APP_KEY)
                .build());
    }
}
