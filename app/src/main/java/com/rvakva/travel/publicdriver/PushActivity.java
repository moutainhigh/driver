package com.rvakva.travel.publicdriver;

import android.os.Bundle;

import com.easymi.component.base.RxBaseActivity;

/**
 * Copyright (C) , 2012-2018 , 四川小咖科技有限公司
 *
 * @author Lzh
 * @version 5.0.0.000
 * @date 2018/5/25
 * @since 5.0.0.000
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
