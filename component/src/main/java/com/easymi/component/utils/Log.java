package com.easymi.component.utils;

import com.easymi.component.BuildConfig;

/**
 * Created by liuzihao on 2018/1/12.
 */

public class Log {

    public static void d(String tag, String msg) {
        if (true) {
            android.util.Log.d(tag, msg);
        }

    }

    public static void e(String tag, String msg) {
        if (true) {
            android.util.Log.e(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (true) {
            android.util.Log.i(tag, msg);
        }
    }

    public static void e(String tag, String msg,Throwable e) {
        if (true) {
            android.util.Log.e(tag, msg,e);
        }
    }
}
