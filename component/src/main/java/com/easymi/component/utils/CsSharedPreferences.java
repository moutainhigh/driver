package com.easymi.component.utils;

import android.text.TextUtils;
import android.util.Log;

import com.easymi.component.Config;
import com.easymi.component.app.XApp;

/**
 * Created by liuzihao on 2019/3/1.
 */

public class CsSharedPreferences {


    /**
     * 解密sp中的String
     * @param key
     * @param defaultValue
     * @return
     */
    public String getString(String key,String defaultValue){
        String s = XApp.getMyPreferences().getString(key, defaultValue);
        if(!TextUtils.isEmpty(s)){
            return EncApi.getInstance().dec(new Loader().getRsaPs().substring(0,16),s);
        }
        return defaultValue;
    }

    /**
     * 得到double
     * @param key
     * @param defaultValue
     * @return
     */
    public float getFloat(String key,float defaultValue){
        String s = XApp.getMyPreferences().getString(key, "");
        if(!TextUtils.isEmpty(s)){
            String floatStr = EncApi.getInstance().dec(new Loader().getRsaPs().substring(0,16),s);
            try{
                return Float.parseFloat(floatStr);
            }catch (Exception e){
                return defaultValue;
            }
        }
        return defaultValue;
    }

    /**
     * 得到long
     * @param key
     * @param defaultValue
     * @return
     */
    public long getLong(String key,long defaultValue){
        String s = XApp.getMyPreferences().getString(key, "");
        if(!TextUtils.isEmpty(s)){
            String longStr = EncApi.getInstance().dec(new Loader().getRsaPs().substring(0,16),s);
            try{
                return Long.parseLong(longStr);
            }catch (Exception e){
                return defaultValue;
            }
        }
        return defaultValue;
    }

    /**
     * 得到int
     * @param key
     * @param defaultValue
     * @return
     */
    public int getInt(String key,int defaultValue){
        String s = XApp.getMyPreferences().getString(key, "");

        if(!TextUtils.isEmpty(s)){
            String intStr = EncApi.getInstance().dec(new Loader().getRsaPs().substring(0,16),s);
            try{
                return Integer.parseInt(intStr);
            }catch (Exception e){
                return defaultValue;
            }
        }
        return defaultValue;
    }

    /**
     * 获取boolean
     * @param key
     * @param defaultValue
     * @return
     */
    public boolean getBoolean(String key,boolean defaultValue){
        return XApp.getMyPreferences().getBoolean(key,defaultValue);
    }

}
