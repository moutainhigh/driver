package com.easymi.zhuanche.util;

import android.os.Build;
import android.view.View;
import android.view.Window;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: PhoneUtil
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class PhoneUtil {
    /**
     * 隐藏系统布局
     * @param window
     */
    public static void setHideVirtualKey(Window window) {
        //保持布局状态
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                //布局位于状态栏下方
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                //全屏
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                //隐藏导航栏
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        if (Build.VERSION.SDK_INT >= 19) {
            uiOptions |= 0x00001000;
        } else {
            uiOptions |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
        }
        window.getDecorView().setSystemUiVisibility(uiOptions);
    }
}
