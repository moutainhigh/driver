package com.easymi.personal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.widget.CusToolbar;
import com.easymi.personal.R;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: HelpCenterActivity
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class HelpCenterActivity extends RxBaseActivity {

    @Override
    public int getLayoutId() {
        return R.layout.activity_help_center;
    }

    @Override
    public void initToolBar() {
        CusToolbar cusToolbar = findViewById(R.id.cus_toolbar);
        cusToolbar.setLeftBack(view -> finish());
        cusToolbar.setTitle(R.string.set_help);
    }

    @Override
    public void initViews(Bundle savedInstanceState) {

    }

    /**
     * 前往代驾帮助中心
     * @param view
     */
    public void toDaijiaHelp(View view) {
        Intent intent = new Intent(HelpCenterActivity.this, HelpCenterSubActivity.class);
        intent.putExtra("cateId", 2L);
        startActivity(intent);
    }
    /**
     * 前往专车帮助中心
     * @param view
     */
    public void toZhuanCheHelp(View view) {
        Intent intent = new Intent(HelpCenterActivity.this, HelpCenterSubActivity.class);
        intent.putExtra("cateId", 4L);
        startActivity(intent);
    }

    @Override
    public boolean isEnableSwipe() {
        return true;
    }
}
