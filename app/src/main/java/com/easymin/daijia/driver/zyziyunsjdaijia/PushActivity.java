package com.easymin.daijia.driver.zyziyunsjdaijia;

import android.os.Bundle;

import com.easymi.component.base.RxBaseActivity;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: PushActivity
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */
public class PushActivity extends RxBaseActivity {
    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_push;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {

    }
}
