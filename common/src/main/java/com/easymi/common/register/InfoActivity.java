package com.easymi.common.register;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.EditText;
import android.widget.TextView;

import com.easymi.common.R;
import com.easymi.component.base.RxBaseActivity;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 * @Author: hufeng
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */
public class InfoActivity extends RxBaseActivity {

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.com_activity_info;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        findViewById(R.id.left_icon).setOnClickListener(v -> onBackPressed());

        if (savedInstanceState == null) {
            Fragment fragment = new InfoFragment();
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ts = fm.beginTransaction();
            ts.add(R.id.registerContainer, fragment, fragment.getClass().getName());
            ts.commitAllowingStateLoss();
        }
    }


    public static void setTVs(TextView... tvs) {
        for (TextView tv : tvs) {
            setXIN(tv);
        }
    }

    /**
     * 将第一个字标红
     * @param tv
     */
    private static void setXIN(TextView tv) {
        String content = tv.getText().toString();
        if (TextUtils.isEmpty(content)) {
            return;
        }
        SpannableString ss = new SpannableString(content);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.RED);
        ss.setSpan(colorSpan, 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tv.setText(ss);
    }


}
