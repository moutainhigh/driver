package com.easymi.component.network;

import com.easymi.component.Config;
import com.easymi.component.utils.Base64;
import com.easymi.component.utils.CsSharedPreferences;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * token 拦截器:用来统一添加token参数
 */
public class TokenInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        HttpUrl httpUrl = chain.request().url();
        Request request = chain.request();
        if (httpUrl.toString().contains("/api/v3/connections/")) {
//            request.url().queryParameter()
            return chain.proceed(request.newBuilder()
                    .url(httpUrl.newBuilder()
                            .host(Config.MQTT_HOST)
                            .port(Config.PORT_HTTP)
                            .build())
                    .addHeader("Authorization", "Basic " + Base64.encode((Config.MQTT_USER_NAME + ":" + Config.MQTT_PSW).getBytes()))
                    .addHeader("Accept", "applicaton/json")
                    .build());
        } else {
            String sToken = new CsSharedPreferences().getString(Config.SP_TOKEN, "");
            return chain.proceed(request.newBuilder()
                    .addHeader("token", sToken)
                    .addHeader("appKey", Config.APP_KEY)
                    .build());
        }
    }
}
