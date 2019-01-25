package com.easymin.daijia.driver.rvakvaV6;

import android.content.Intent;
import android.os.Bundle;

import com.alibaba.sdk.android.push.AndroidPopupActivity;
import com.easymi.component.utils.Log;

import java.util.Map;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: PopupPushActivity
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:  辅助弹窗Activity
 * History:
 */
public class PopupPushActivity extends AndroidPopupActivity {

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    protected void onSysNoticeOpened(String s, String s1, Map<String, String> map) {
        Log.d("OnMiPushSysNoticeOpened", "title: " + s + ", content: " + s1 + ", extMap: " + map);
    }

    @Override
    public void onMessage(Intent intent) {
        super.onMessage(intent);
    }
}
