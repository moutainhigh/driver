package com.easymi.common.mvp.order;

import android.os.Bundle;

import com.easymi.component.base.RxBaseActivity;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: OrderActivity
 * Author: shine
 * Date: 2018/11/14 下午7:26
 * Description:
 * History:
 */
public class OrderActivity extends RxBaseActivity {

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return 0;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {

    }
}
