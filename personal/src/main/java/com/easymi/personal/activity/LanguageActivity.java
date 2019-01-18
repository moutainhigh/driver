package com.easymi.personal.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.alibaba.android.arouter.launcher.ARouter;
import com.easymi.component.Config;
import com.easymi.component.app.ActManager;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.widget.CusToolbar;
import com.easymi.personal.R;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: LanguageActivity
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class LanguageActivity extends RxBaseActivity implements View.OnClickListener {

    RelativeLayout flowSystem;
    RelativeLayout simpleChinese;
    RelativeLayout zhTw;
    RelativeLayout english;

    ImageView img1;
    ImageView img2;
    ImageView img3;
    ImageView img4;

    /**
     * 当前语言
     */
    private int currentLanguage = Config.SP_LANGUAGE_AUTO;

    @Override
    public int getLayoutId() {
        return R.layout.activity_language;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        flowSystem = findViewById(R.id.flow_system);
        simpleChinese = findViewById(R.id.simple_chinese);
        zhTw = findViewById(R.id.zh_tw);
        english = findViewById(R.id.english);

        img1 = findViewById(R.id.img_1);
        img2 = findViewById(R.id.img_2);
        img3 = findViewById(R.id.img_3);
        img4 = findViewById(R.id.img_4);

        currentLanguage = XApp.getMyPreferences().getInt(Config.SP_USER_LANGUAGE, Config.SP_LANGUAGE_AUTO);
        if (currentLanguage == Config.SP_LANGUAGE_AUTO) {
            img1.setVisibility(View.VISIBLE);
        } else if (currentLanguage == Config.SP_SIMPLIFIED_CHINESE) {
            img2.setVisibility(View.VISIBLE);
        } else if (currentLanguage == Config.SP_TRADITIONAL_CHINESE) {
            img3.setVisibility(View.VISIBLE);
        } else if (currentLanguage == Config.SP_ENGLISH) {
            img4.setVisibility(View.VISIBLE);
        }

        flowSystem.setOnClickListener(this);
        simpleChinese.setOnClickListener(this);
        zhTw.setOnClickListener(this);
        english.setOnClickListener(this);
    }

    @Override
    public void initToolBar() {
        CusToolbar cusToolbar = findViewById(R.id.cus_toolbar);
        cusToolbar.setLeftIcon(R.drawable.ic_arrow_back, view -> onBackPressed());
        cusToolbar.setTitle(R.string.set_language);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        //获取偏好设置编辑器
        SharedPreferences.Editor editor = XApp.getMyPreferences().edit();
        currentLanguage = Config.SP_LANGUAGE_AUTO;
        if (id == R.id.flow_system) {
            currentLanguage = Config.SP_LANGUAGE_AUTO;

        } else if (id == R.id.simple_chinese) {
            currentLanguage = Config.SP_SIMPLIFIED_CHINESE;

        } else if (id == R.id.zh_tw) {
            currentLanguage = Config.SP_TRADITIONAL_CHINESE;

        } else if (id == R.id.english) {
            currentLanguage = Config.SP_ENGLISH;

        }

        //配置本地化
        editor.putInt(Config.SP_USER_LANGUAGE, currentLanguage);
        editor.apply();

        //需要重启Activity才会刷新
        ActManager.getInstance().finishAllActivity();

        //重启应用
        ARouter.getInstance().build("/common/SplashActivity")
                .withFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK).navigation();
    }

    @Override
    public boolean isEnableSwipe() {
        return true;
    }
}
