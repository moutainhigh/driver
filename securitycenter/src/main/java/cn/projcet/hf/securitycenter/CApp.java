package cn.projcet.hf.securitycenter;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.StringRes;

import java.util.LinkedList;
import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: CApp
 * Author: shine
 * Date: 2018/11/28 上午10:18
 * Description:
 * History:
 */
public class CApp extends Application {

    private static final String SHARED_PREFERENCES_NAME = "em"; //SharedPreferences 文件名

    private static CApp instance ;    //实例化对象


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    /**
     * 获取ApplicationContext.
     *
     * @return context
     */
    public static CApp getInstance() {
        return instance;
    }

    /**
     * 通过字符串资源id,获取字符串.
     *
     * @param resId 需要获取字符串的资源id
     * @return 返回资源id对应的字符串, 如果获取则返回null
     */
    public static String getMyString(@StringRes int resId) {
        String str = null;
        if (instance != null) {
            str = instance.getString(resId);
        }
        return str;
    }

    /**
     * 获取app的SharedPreferences.
     *
     * @return SharedPreferences对象
     */
    public static SharedPreferences getMyPreferences() {
        return instance.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    /**
     * 获取SharedPreferences.Editor.
     *
     * @return editor对象
     */
    public static SharedPreferences.Editor getPreferencesEditor() {
        return instance.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit();
    }


}
