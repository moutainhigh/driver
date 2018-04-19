package com.easymi.personal.activity;

import android.os.Bundle;

import com.easymi.component.base.RxBaseActivity;

/**
 * Created by liuzihao on 2018/4/19.
 */

public class SysCheckActivity extends RxBaseActivity {
    @Override
    public boolean isEnableSwipe() {
        return true;
    }

    @Override
    public int getLayoutId() {
        return 0;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {

    }
}
