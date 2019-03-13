package com.easymi.component.utils.safeutils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.easymi.component.utils.SysUtil;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: Cat
 * @Author: hufeng
 * @Date: 2019/3/12 下午3:06
 * @Description: 安全验证
 * @History:
 */
public class Cat {

    private String md5String;
    public Context mContext;

    static {
        System.loadLibrary("cat");
    }

    public Cat(Context context) {
        this.mContext = context.getApplicationContext();
        try {
            //获取打包时内置的签名MD5
            ApplicationInfo appInfo = mContext.getPackageManager().getApplicationInfo(mContext.getPackageName(), PackageManager.GET_META_DATA);
            if (appInfo != null && appInfo.metaData != null) {
                md5String = appInfo.metaData.getString("xiaoka.keyid");
                if (!TextUtils.isEmpty(md5String)) {
                    md5String = md5String.replace("xiaoka", "");
                    md5String = new StringBuffer(md5String).reverse().toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        watching(context);
    }

    /**
     * 开始监听内存dump.
     *
     * @param context context
     */
    private void watching(Context context) {
        Intent intent = new Intent(context, DumpService.class);
        context.startService(intent);
    }

    /**
     * 检测签名MD5是否一致.
     *
     * @return 如果一致则返回tru，否则false。
     */
    public boolean check() {
        //获取应用签名MD5
        byte[] signByte = getSignByte();
        String md5 = SysUtil.md5(signByte);
        return !TextUtils.isEmpty(md5String) && md5String.equalsIgnoreCase(md5);
    }

    public native byte[] getSignByte();

}
