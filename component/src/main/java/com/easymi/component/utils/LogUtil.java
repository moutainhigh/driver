package com.easymi.component.utils;

import android.text.TextUtils;
import android.util.Log;

/**
 * Created by xyin on 2016/10/9. 日志输出工具
 */

public class LogUtil {

    /*日志输出开关配置,错误日志我希望在线上也输出所以单独配置*/
    private static final boolean ERROR = false;
    private static final boolean DEBUG = false;

    /**
     * 输出ERROR日志.
     *
     * @param tag tag
     * @param msg msg
     */
    public static void e(String tag, String msg) {
        if (ERROR && !TextUtils.isEmpty(msg)) {
            Log.e(tag, msg);
        }
    }

    /**
     * 输出DEBUG日志.
     *
     * @param tag tag
     * @param msg msg
     */
    public static void d(String tag, String msg) {
        if (DEBUG && !TextUtils.isEmpty(msg)) {
            Log.d(tag, msg);
        }
    }

}
