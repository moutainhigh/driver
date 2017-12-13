package com.easymi.component.loc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.easymi.component.Config;
import com.easymi.component.app.XApp;
import com.easymi.component.entity.EmLoc;
import com.easymi.component.utils.StringUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by developerLzh on 2017/11/20 0020.
 * <p>
 * 接收来自其他线程的位置变化广播
 */

public class LocReceiver extends BroadcastReceiver implements LocSubject {

    private List<LocObserver> observers = new ArrayList<>();

    private static LocReceiver locReceiver;

    public LocReceiver() {
        locReceiver = this;
    }

    public static LocReceiver getInstance() {
        return locReceiver;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (null != intent && StringUtils.isNotBlank(intent.getAction())
                && intent.getAction().equals(LocService.LOC_CHANGED)) {
            String loc = intent.getStringExtra("locPos");
            SharedPreferences.Editor editor = XApp.getPreferencesEditor().putString(Config.SP_LAST_LOC, loc);
            editor.apply();//保存上次的位置信息 json格式字符创

            EmLoc emLoc = new Gson().fromJson(loc, EmLoc.class);

            Log.e("locReceiver", "bearing>>>>"+emLoc.bearing);
            notifyObserver(emLoc);
        }

    }

    /**
     * 添加观察者
     *
     * @param obj
     */
    @Override
    public void addObserver(LocObserver obj) {
        observers.add(obj);
    }

    /**
     * @param obj
     */
    @Override
    public void deleteObserver(LocObserver obj) {
        observers.remove(obj);
    }

    @Override
    public void notifyObserver(EmLoc loc) {
        for (LocObserver observer : observers) {
            observer.receiveLoc(loc);
        }
    }
}
