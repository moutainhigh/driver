package com.easymin.daijia.driver.zyziyunsjdaijia.config;

import android.content.Context;
import android.text.TextUtils;

import com.easymi.component.Config;
import com.easymi.component.app.XApp;
import com.easymi.component.entity.EnvironmentPojo;
import com.easymi.component.utils.StringUtils;
import com.easymin.daijia.driver.zyziyunsjdaijia.BuildConfig;
import com.easymin.driver.securitycenter.CenterConfig;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by liuzihao on 2019/2/21.
 */

public class MainConfig {
    public MainConfig(Context context) {
        String config = getFromAssets(context, "config.json");
        if (StringUtils.isNotBlank(config)) {
            try {
                JSONObject jb = new JSONObject(config);

                String data = XApp.getMyPreferences().getString("environment_setting", "");
                if (!TextUtils.isEmpty(data) && BuildConfig.DEBUG) {
                    EnvironmentPojo environmentPojo = new Gson().fromJson(data, EnvironmentPojo.class);
                    Config.HOST = environmentPojo.host;
                    Config.IS_ENCRYPT = environmentPojo.encryption;
                    Config.H5_HOST = environmentPojo.h5Host;
                    Config.APP_KEY = environmentPojo.appKey;
                } else {
                    Config.HOST = BuildConfig.HOST;
                    Config.IS_ENCRYPT = BuildConfig.IS_ENCRYPT;
                    Config.H5_HOST = BuildConfig.H5_HOST;
                    Config.APP_KEY = jb.optString("APP_KEY");
                }
                
                CenterConfig.HOST = Config.HOST;
                CenterConfig.H5_HOST = Config.H5_HOST;
                CenterConfig.IMG_SERVER = Config.IMG_SERVER;
                Config.VERSION_DATA = BuildConfig.VERSION_DATA;
                Config.VERSION_NAME = BuildConfig.VERSION_NAME;
                Config.IMG_SERVER = BuildConfig.IMG_SERVER;
                Config.IMG_PATH = "";
                Config.QQ_APP_ID = jb.optString("QQ_APP_ID");
                Config.WX_APP_ID = jb.optString("WX_APP_ID");
                Config.TTS_APP_ID = jb.optString("TTS_APP_ID");
                Config.TTS_APP_KEY = jb.optString("TTS_APP_KEY");
                Config.TTS_APP_SECRET = jb.optString("TTS_APP_SECRET");
            } catch (JSONException e) {
                throw new RuntimeException("配置信息加载错误");
            }
        }
    }

    public String getFromAssets(Context context, String fileName) {
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String Result = "";
            while ((line = bufReader.readLine()) != null)
                Result += line;
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
