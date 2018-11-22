package com.easymi.component.network;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.easymi.component.Config;
import com.easymi.component.utils.Log;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/11/15 0015.
 * 拦截请求添加sign.
 * <p>
 * 为避免线程同步造成数据紊乱的问题，这里都只使用局部变量，不使用全局变量
 */

public class SignInterceptor implements Interceptor {

    private static final String LOG_TAG = SignInterceptor.class.getSimpleName();

    private final static String secret = "123456";  // TODO: 2017/11/16 设置一个秘钥

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {

        return chain.proceed(chain.request().newBuilder()
                .addHeader("token", "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE1NDI3ODgwOTYsInN1YiI6IntcImlkXCI6NTIsXCJuYW1lXCI6XCI4ODc3ODY2XCIsXCJ1c2VyVHlwZVwiOjIsXCJjb21wYW55SWRcIjoxLFwidGltZXN0YW1wXCI6MTU0Mjc4ODA5NixcImFwcEtleVwiOlwiMUhBY2llbnQxa0xxZmVYN0RWVFYwZGtsVWtwR0VuVUNcIn0iLCJleHAiOjE1NDI4NzQ0OTZ9.TTHjs4F16dmGakM0HTF_UuRkBwIhZg-P4LBkAfmcWjw")
                .build());
    }


    /**
     * 获取排序后的url.
     */
    private String sortUrl(Map<String, String> paramsMap, String mPath) {
        List<String> sortList = new LinkedList<>();
        sortList.addAll(paramsMap.keySet());
        int size = sortList.size();

        Collections.sort(sortList); // 排序
        StringBuilder sb = new StringBuilder();
        sb.append("/").append(mPath).append("?");
        for (int i = 0; i < size; i++) {
            String key = sortList.get(i);
            sb.append(key).append("=").append(paramsMap.get(key));
            if (i < size - 1) {
                sb.append("&");
            }
        }
        return sb.toString();
    }

    private String sign(Map<String, String> paramsMap, String mPath) {
        return hamcsha1(sortUrl(paramsMap, mPath).getBytes(), secret.getBytes());
    }

    /**
     * sha1值计算.
     *
     * @param data data
     * @param key  key
     * @return 签名后的字符串.
     */
    private String hamcsha1(byte[] data, byte[] key) {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key, "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
            return byte2hex(mac.doFinal(data));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String byte2hex(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; b != null && n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1)
                hs.append('0');
            hs.append(stmp);
        }
        return hs.toString().toUpperCase();
    }


}
