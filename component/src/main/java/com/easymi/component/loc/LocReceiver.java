package com.easymi.component.loc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.easymi.component.Config;
import com.easymi.component.app.XApp;
import com.easymi.component.entity.EmLoc;
import com.google.gson.Gson;

/**
 * Created by developerLzh on 2017/11/20 0020.
 */

public class LocReceiver extends BroadcastReceiver {

    private ReceiveLocInterface receiveLocInterface;

    public LocReceiver(ReceiveLocInterface receiveLocInterface) {
        this.receiveLocInterface = receiveLocInterface;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (null != receiveLocInterface) {
            String loc = intent.getStringExtra("locPos");
            SharedPreferences.Editor editor = XApp.getPreferencesEditor().putString(Config.SP_LAST_LOC, loc);
            editor.apply();//保存上次的位置信息 json格式字符创

            EmLoc emLoc = new Gson().fromJson(loc, EmLoc.class);
            receiveLocInterface.receiveLoc(emLoc);
        }
    }
}
