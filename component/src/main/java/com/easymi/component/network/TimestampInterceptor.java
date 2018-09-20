package com.easymi.component.network;

import android.support.annotation.NonNull;

import com.easymi.component.utils.Base64_2;

import java.io.IOException;
import java.net.URLDecoder;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by liuzihao on 2018/2/6.
 */

public class TimestampInterceptor implements Interceptor {
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request oldRequest = chain.request();

        RequestBody requestBody = oldRequest.body();
        String method = oldRequest.method();

        Request request;
        if (requestBody == null) {
            //get
            Request.Builder newBuilder;
            HttpUrl oldUrl = oldRequest.url();
            HttpUrl.Builder urlBuilder = oldUrl.newBuilder();


            HttpUrl newUrl = urlBuilder.addQueryParameter("timestamp", String.valueOf(System.currentTimeMillis() / 1000))
                    .build();

            newBuilder = oldRequest.newBuilder()
                    .url(newUrl);
            request = newBuilder.build();

        } else if (requestBody instanceof FormBody) {
            //创建一个新的FromBoby
            FormBody.Builder bobyBuilder = new FormBody.Builder();
            //获取原先的boby
            FormBody body = (FormBody) oldRequest.body();
            //遍历boby
            if (body != null) {
                for (int i = 0; i < body.size(); i++) {
                    //取出原先boby的数据  存入新的boby里
                    String name = body.encodedName(i);
                    String value = body.encodedValue(i);
                    bobyBuilder.add(name, URLDecoder.decode(value, "UTF-8"));
                }
            }
            //添加制定的公共参数到新的boby里  把原先的boby给替换掉
            body = bobyBuilder.add("timestamp", String.valueOf(System.currentTimeMillis() / 1000)).build();
            //获取新的request  取代原先的request
            if (method.equals("PUT")) {
                request = oldRequest.newBuilder().put(body).build();
            } else {
                request = oldRequest.newBuilder().post(body).build();
            }
        } else {
            //不用处理
            return chain.proceed(oldRequest);
        }


        return chain.proceed(request);
    }
}
