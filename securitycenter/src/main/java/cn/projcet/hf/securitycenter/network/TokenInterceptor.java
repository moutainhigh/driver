package cn.projcet.hf.securitycenter.network;

import android.text.TextUtils;

import java.io.IOException;

import cn.projcet.hf.securitycenter.CenterConfig;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * token 拦截器:用来统一添加token参数
 */
public class TokenInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        String sToken = CenterConfig.TOKEN;
        Request.Builder builder = chain.request().newBuilder();
        builder.addHeader("token", sToken);
        return chain.proceed(builder.build());
    }
}
