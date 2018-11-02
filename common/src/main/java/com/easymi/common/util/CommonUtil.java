package com.easymi.common.util;

import android.content.Context;

import com.easymi.common.push.MqttManager;
import com.easymi.component.utils.EmUtil;

/**
 * Created by liuzihao on 2018/10/31.
 */

public class CommonUtil {
    public static void employLogout(Context context){
        MqttManager.release();//释放mqtt
        EmUtil.employLogout(context);
    }
}
