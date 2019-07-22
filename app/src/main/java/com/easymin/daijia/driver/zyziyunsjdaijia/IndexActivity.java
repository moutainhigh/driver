package com.easymin.daijia.driver.zyziyunsjdaijia;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.easymi.common.activity.SplashActivity;
import com.easymi.component.base.RxBaseActivity2;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: IndexActivity
 *
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:  防止白屏
 * History:
 */
public class IndexActivity extends RxBaseActivity2 {

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_index;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        Log.e("IndexActivity", "initViews");

        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        Intent intent = new Intent(this, SplashActivity.class);
        startActivity(intent);
        finish();
    }
}
