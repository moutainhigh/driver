package com.easymin.custombus.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.utils.ToastUtil;
import com.easymin.custombus.R;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: TestActivity
 * @Author: hufeng
 * @Date: 2019/3/13 下午8:15
 * @Description:
 * @History:
 */
public class TestActivity extends RxBaseActivity{

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_custom_test;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {

    }
}
