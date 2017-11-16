package com.easymi.component.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.StringRes;
import android.support.multidex.MultiDexApplication;

import com.alibaba.android.arouter.launcher.ARouter;
import com.easymi.component.BuildConfig;
import com.easymi.component.db.SqliteHelper;

/**
 * Created by xyin on 2016/9/30.
 * application 注:每启动一个新的进程就会调用application的onCreate方法(需要注意某些方法是否允许多次初始化).
 */

public class XApp extends MultiDexApplication {

    private static final String SHARED_PREFERENCES_NAME = "em"; //SharedPreferences 文件名
    private static XApp instance;    //实例化对象

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        //初始化路由
        if (BuildConfig.DEBUG) {
            ARouter.openDebug();   //非打包情况下,必须调用调用
            ARouter.openLog();
        }
        ARouter.init(this);
        SqliteHelper.init(this);
    }

    /**
     * 获取ApplicationContext.
     *
     * @return context
     */
    public static Context context() {
        return instance;
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

}
