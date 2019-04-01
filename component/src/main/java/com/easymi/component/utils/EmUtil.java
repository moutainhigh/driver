package com.easymi.component.utils;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.easymi.component.Config;
import com.easymi.component.app.XApp;
import com.easymi.component.entity.EmLoc;
import com.easymi.component.entity.Employ;
import com.easymi.component.loc.LocService;
import com.google.gson.Gson;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: FinishActivity
 *@Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:  封装的一些快捷获取对象方法
 * History:
 */

public class EmUtil {

    public static Long getEmployId() {
        return new CsSharedPreferences().getLong(Config.SP_DRIVERID, -1);
    }

    public static String getAppKey() {
        if (!Config.COMM_USE) {
            return Config.APP_KEY;
        } else {
            return new CsSharedPreferences().getString(Config.SP_APP_KEY, "");
        }
    }


    public static Employ getEmployInfo() {
        return Employ.findByID(getEmployId());
    }

    public static EmLoc getLastLoc() {
        EmLoc emLoc = new Gson().fromJson(new CsSharedPreferences().getString(Config.SP_LAST_LOC, ""), EmLoc.class);
        if (null == emLoc) {
            emLoc = new EmLoc();
            emLoc.poiName = "未知";
        }
        return emLoc;
    }

    public static void employLogout(Context context) {
        CsEditor editor = new CsEditor();
        editor.putBoolean(Config.SP_ISLOGIN, false);
        editor.putString(Config.APP_KEY, "");//重置APPKEY
        editor.putString(Config.SP_TOKEN, "");
        editor.putLong(Config.SP_DRIVERID, -1);
        editor.apply();

        if (null != XApp.getInstance().player) {
            XApp.getInstance().player.stop();
        }

        try {
            Class<?> clazz = Class.forName("com.easymi.common.push.MqttManager");
            Method method = clazz.getMethod("release");
            method.invoke(null);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        stopAllService(context);

        Intent i = context.getPackageManager()
                .getLaunchIntentForPackage(context.getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(i);

        ActivityManager activityMgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityMgr != null) {
            activityMgr.killBackgroundProcesses(context.getPackageName());
        }

//        System.exit(0);
    }

    private static void stopAllService(Context context) {

        Intent locIntent = new Intent(context, LocService.class);
        locIntent.setAction(LocService.STOP_LOC);
        context.startService(locIntent);

//        Intent locHelpIntent = new Intent(context, LocationHelperService.class);
//        context.stopService(locHelpIntent);
//        try {
//            Service mQTTService = (Service) (Class.forName("com.easymi.common.push.MQTTService").newInstance());
//            Service jobKeepLiveService = (Service) Class.forName("com.easymi.common.daemon.JobKeepLiveService").newInstance();
//            Service puppetService = (Service) Class.forName("com.easymi.common.daemon.PuppetService").newInstance();
//            Service daemonService = (Service) Class.forName("com.easymi.common.daemon.DaemonService").newInstance();

//            Intent mqttIntent = new Intent(context, mQTTService.getClass());
//            mqttIntent.setPackage(context.getPackageName());
//            context.stopService(mqttIntent);

//            Intent daemonIntent = new Intent(context, daemonService.getClass());
//            daemonIntent.setPackage(context.getPackageName());
//            context.stopService(daemonIntent);
//
//            Intent jobkeepIntent = new Intent(context, jobKeepLiveService.getClass());
//            jobkeepIntent.setPackage(context.getPackageName());
//            context.stopService(jobkeepIntent);
//
//            Intent puppetIntent = new Intent(context, puppetService.getClass());
//            puppetIntent.setPackage(context.getPackageName());
//            context.stopService(puppetIntent);
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        }
    }
}
