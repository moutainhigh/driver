package com.easymi.component.utils;

import android.content.SharedPreferences;
import android.text.TextUtils;
import android.widget.TextView;

import com.easymi.component.app.XApp;

/**
 * Created by liuzihao on 2019/3/1.
 */

public class CsEditor {

    private static SharedPreferences.Editor editor;

    public CsEditor() {
        editor = XApp.getPreferencesEditor();
    }

    public  SharedPreferences.Editor putString(String key, String value) {
        if (TextUtils.isEmpty(value)){
            editor.remove(key);
        }else {
            editor.putString(key, EncApi.getInstance().en(new Loader().getRsaPs().substring(0, 16), value));
        }
        return editor;
    }

    public SharedPreferences.Editor putLong(String key, long value) {
        editor.putString(key, EncApi.getInstance().en(new Loader().getRsaPs().substring(0, 16), String.valueOf(value)));
        return editor;
    }

    public SharedPreferences.Editor putFloat(String key, float value) {
        editor.putString(key, EncApi.getInstance().en(new Loader().getRsaPs().substring(0, 16), String.valueOf(value)));
        return editor;
    }

    public SharedPreferences.Editor putInt(String key, int value) {
        editor.putString(key, EncApi.getInstance().en(new Loader().getRsaPs().substring(0, 16), String.valueOf(value)));
        return editor;
    }

    public SharedPreferences.Editor putBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        return editor;
    }

    public void apply() {
        editor.apply();
    }

    public void remove(String key) {
        editor.remove(key);
    }

    public void clear() {
        editor.clear();
    }
}
