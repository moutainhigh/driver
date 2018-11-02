package com.rvakva.travel.publicdriver;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.easymi.common.activity.SplashActivity;
import com.easymi.component.base.RxBaseActivity;

/**
 * Copyright (C) , 2012-2018 , 四川小咖科技有限公司
 *
 * @author Lzh
 * @version 5.0.0.000
 * @date 2018/5/19
 * @since 5.0.0.000
 */
public class IndexActivity extends RxBaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ARouter.getInstance().
        Intent intent = new Intent(this, SplashActivity.class);
        startActivity(intent);
        finish();
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
