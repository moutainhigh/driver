package com.easymi.component.utils;

import com.easymi.component.Config;
import com.easymi.component.app.XApp;
import com.easymi.component.entity.EmLoc;
import com.easymi.component.entity.Employ;
import com.google.gson.Gson;

/**
 * Created by developerLzh on 2017/11/30 0030.
 * 封装的一些快捷获取对象方法
 */

public class EmUtil {

    public static Long getEmployId() {
        return EmUtil.getEmployId();
    }

    public static Employ getEmployInfo() {
        return Employ.findByID(getEmployId());
    }

    public static EmLoc getLastLoc() {
        return new Gson().fromJson(XApp.getMyPreferences().getString(Config.SP_LAST_LOC, ""), EmLoc.class);
    }

}
