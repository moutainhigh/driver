package com.easymi.component.network;

import android.text.TextUtils;

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
        String sToken = XApp.getMyPreferences().getString(Config.SP_TOKEN, "");
        Request.Builder builder = chain.request().newBuilder();
        builder.addHeader("token", sToken);
        return chain.proceed(builder.build());
    }
}
