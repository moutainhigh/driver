package com.rvakva.trip.driver;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.easymi.common.activity.SplashActivity;
import com.easymi.component.base.RxBaseActivity;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: IndexActivity
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:  防止白屏
 * History:
 */
public class IndexActivity extends RxBaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, SplashActivity.class);
        startActivity(intent);
    }

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

    }
}
