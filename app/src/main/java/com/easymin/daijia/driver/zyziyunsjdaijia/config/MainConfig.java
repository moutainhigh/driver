package com.easymin.daijia.driver.zyziyunsjdaijia.config;

import android.content.Context;

import com.easymi.component.Config;
import com.easymi.component.utils.StringUtils;
import com.easymin.daijia.consumer.zyziyunclient.BuildConfig;

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

                Config.HOST = BuildConfig.HOST;
                Config.H5_HOST = BuildConfig.H5_HOST;
                Config.MQTT_HOST = BuildConfig.MQTT_HOST;
                Config.MQTT_USER_NAME = BuildConfig.MQTT_USER_NAME;
                Config.MQTT_PSW = BuildConfig.MQTT_PSW;
                Config.VERSION_NAME = BuildConfig.VERSION_NAME;

                Config.APP_KEY = jb.optString("APP_KEY");
                Config.QQ_APP_ID = jb.optString("QQ_APP_ID");
                Config.WX_APP_ID = jb.optString("WX_APP_ID");
                Config.IMG_SERVER = jb.optString("IMG_SERVER");
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
