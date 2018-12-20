package com.easymi.personal.activity.register;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.widget.CusToolbar;
import com.easymi.personal.R;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: RegisterNoticeActivity
 * Author: shine
 * Date: 2018/12/19 上午9:57
 * Description:
 * History:
 */
public class RegisterNoticeActivity extends RxBaseActivity {

    CusToolbar toolbar;
    ImageView iv_iamge;
    TextView tv_title;
    TextView tv_notice;
    TextView tv_amend;

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_register_notice;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        findById();
    }

    @Override
    public void initToolBar() {
        super.initToolBar();
        toolbar.setLeftIcon(R.drawable.ic_arrow_back, v -> finish());
        toolbar.setTitle(R.string.register_become);
    }

    public void findById() {
        toolbar = findViewById(R.id.toolbar);
        iv_iamge = findViewById(R.id.iv_iamge);
        tv_title = findViewById(R.id.tv_title);
        tv_notice = findViewById(R.id.tv_notice);
        tv_amend = findViewById(R.id.tv_amend);
    }


}
