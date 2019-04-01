package com.easymi.component.network;

import android.util.Log;

import com.easymi.component.Config;
import com.easymi.component.app.XApp;
import com.easymi.component.utils.AesUtil;
import com.easymi.component.utils.CsSharedPreferences;
import com.easymi.component.utils.EncApi;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import java.io.IOException;
import java.net.URLDecoder;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: KeyGsonResponseBodyConverter
 *@Author: shine
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
            if (!str.contains("{")){
                String jsonStr = EncApi.getInstance().dec(new CsSharedPreferences().getString(Config.AES_PASSWORD,AesUtil.AAAAA),str);
                String urlString = URLDecoder.decode(jsonStr);
                Log.e("responseJson", urlString);
                return adapter.fromJson(urlString);
            }else {
                String urlString = URLDecoder.decode(str);
                Log.e("responseJson", urlString);
                return adapter.fromJson(urlString);
            }
        } finally {
            value.close();
        }
    }
}
