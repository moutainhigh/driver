package com.easymi.component.network;

import android.support.annotation.NonNull;


import com.easymi.component.Config;
import com.easymi.component.app.XApp;
import com.easymi.component.utils.AesUtil;
import com.easymi.component.utils.Log;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/11/15 0015.
 * 拦截请求添加sign.
 */

public class EncryptInterceptor implements Interceptor {

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {

        Request originRequest = chain.request();

        EncryptSet encryptSet = hookRequest(originRequest);

        if (encryptSet == null || encryptSet.request == null) {
            return chain.proceed(originRequest);
        }
        //排除登录接口，其余接口使用拦截器进行aes加密。
        String url = chain.request().url().toString();
        if (url.contains("api/v1/public/driver/login")
                || url.contains("api/v1/public/driver/register/save")
                || url.contains("api/v1/public/app/captcha/send_sms")
                || url.contains("api/v1/public/driver/register/apply/save")
                || url.contains("api/v1/public/driver/register/get")
                ) {
            return chain.proceed(originRequest);
        }

        return chain.proceed(encryptSet.request.newBuilder()
                .build());
    }

    /**
     * 拦截原始获取请求字段添加新的字段.
     *
     * @param originRequest 原始请求.
     * @return 处理后的新请求
     */
    private EncryptSet hookRequest(@NonNull Request originRequest) {
        RequestBody requestBody = originRequest.body();
        if (requestBody == null) {
            return handleGet(originRequest);
        } else if (requestBody instanceof FormBody) {
            return handlePost(originRequest, (FormBody) requestBody);
        }
        return null;
    }

    private EncryptSet handlePost(@NonNull Request originRequest, FormBody originBody) {

        EncryptSet encryptSet = new EncryptSet();

        FormBody.Builder newFormBody = new FormBody.Builder();
        for (int i = 0; i < originBody.size(); i++) {
            String name = originBody.encodedName(i);
            String value = originBody.encodedValue(i);
            value = encrypt(value);
            newFormBody.addEncoded(name, value);
        }

        encryptSet.request = originRequest.newBuilder()
                .method(originRequest.method(), newFormBody.build())
                .build();

        return encryptSet;

    }

    private EncryptSet handleGet(@NonNull Request originRequest) {
        String originUrl = "" + originRequest.url();

        //截取路由和参数列表计算sign
        String[] result = originUrl.split("[?]");
        if (result.length <= 1) {
            return null;
        }

        EncryptSet encryptSet = new EncryptSet();
        StringBuilder newUrl = new StringBuilder().append(result[0]).append("?");
        String[] params = result[1].split("&"); //参数
        for (String p : params) {
            String[] ps = p.split("=");
            String name = ps[0];
            String value = "";
            if (ps.length == 2) {
                value = ps[1];
                value = encrypt(value);
            }
            newUrl.append(name).append("=").append(value).append("&");
        }
        newUrl.deleteCharAt(newUrl.length() - 1);
        encryptSet.request = originRequest.newBuilder().url(newUrl.toString()).build();
        return encryptSet;
    }

    /**
     * AES 解密
     *
     * @param content 密文
     * @return
     */

    public static String aesDecrypt(String content) {
        return AesUtil.aesDecrypt(content, "");
    }

    /**
     * 加密.
     */
    private String encrypt(String content) {
        String value = content;
//        Log.e("hufeng/value",value);
        try {
            //将默认的url编码还原后加密在url编码
            String decoderStr = URLDecoder.decode(content, "utf-8");
            value = AesUtil.aesEncrypt(decoderStr, XApp.getMyPreferences().getString(Config.AES_PASSWORD, AesUtil.AAAAA));
//            Log.e("hufeng/aesvalue",value);
            value = URLEncoder.encode(value, "utf-8");
//            Log.e("hufeng/encode",value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }


}
