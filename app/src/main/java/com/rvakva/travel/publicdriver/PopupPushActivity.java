package com.rvakva.travel.publicdriver;

import android.content.Intent;
import android.os.Bundle;

import com.alibaba.sdk.android.push.AndroidPopupActivity;
import com.easymi.component.utils.Log;

import java.util.Map;

/**
 * Copyright (C) , 2012-2018 , 四川小咖科技有限公司
 *
 * 辅助弹窗Activity
 *
 * @author Lzh
 * @version 5.0.0.000
 * @date 2018/5/19
 * @since 5.0.0.000
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
