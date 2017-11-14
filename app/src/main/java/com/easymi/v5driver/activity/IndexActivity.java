package com.easymi.v5driver.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.alibaba.android.arouter.launcher.ARouter;

/**
 * Created by developerLzh on 2017/11/3 0003.
 */

public class IndexActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //跳转到启动页面
        ARouter.getInstance()
                .build("/common/SplashActivity")
                .navigation();

        finish();
    }
}
