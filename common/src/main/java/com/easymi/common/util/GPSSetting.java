package com.easymi.common.util;

import com.easymi.component.Config;
import com.easymi.component.app.XApp;

public class GPSSetting {

    /**
     * 是否上传网络类型的定位点
     */
    private boolean netLocEnable;

    /**
     * 实例化
     */
    private static class SingletonHolder {
        private static final GPSSetting INSTANCE = new GPSSetting();
    }


    public static GPSSetting getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * gps定位设置
     */
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
        XApp.getEditor()
                .putBoolean(Config.SP_GPS_FILTER, !enable)
                .apply();
    }

    public boolean getNetEnable() {
        return netLocEnable;
    }


}
