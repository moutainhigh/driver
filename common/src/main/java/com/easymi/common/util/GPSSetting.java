package com.easymi.common.util;

import android.content.SharedPreferences;

import com.easymi.component.Config;
import com.easymi.component.app.XApp;

public class GPSSetting {

    //是否上传网络类型的定位点
    private boolean netLocEnable;

    private static class SingletonHolder {
        private static final GPSSetting INSTANCE = new GPSSetting();
    }


    public static GPSSetting getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private GPSSetting() {
        //没开启过滤功能,netLocEnable为true
        netLocEnable = !XApp.getMyPreferences().getBoolean(Config.SP_GPS_FILTER, false);
    }

    /**
     * 设置为true表示网络定位类型有效,此时就表示不开启过滤功能.
     *
     * @param enable 网络定位是否有效
     */
    public void setNetEnable(boolean enable) {
        this.netLocEnable = enable;
        SharedPreferences.Editor editor = XApp.getPreferencesEditor();
        editor.putBoolean(Config.SP_GPS_FILTER, !enable);
        editor.apply();
    }

    public boolean getNetEnable() {
        return netLocEnable;
    }

}
