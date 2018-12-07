package cn.projcet.hf.securitycenter.network;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import java.io.IOException;
import java.net.URLDecoder;

import cn.projcet.hf.securitycenter.CenterConfig;
        import cn.projcet.hf.securitycenter.utils.AesUtil;
        import okhttp3.ResponseBody;
        import retrofit2.Converter;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: KeyGsonResponseBodyConverter
 * Author: shine
 * Date: 2018/11/25 下午3:55
 * Description:
 * History:
 */
public class KeyGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final TypeAdapter<T> adapter;

    KeyGsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        try {
            String str = value.string();
            String jsonStr = AesUtil.aesDecrypt(str,CenterConfig.AES_KEY);
            String urlString = URLDecoder.decode(jsonStr);
            Log.e("responseJson", urlString);
            return adapter.fromJson(urlString);
        } finally {
            value.close();
        }
    }
}
