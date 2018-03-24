package com.easymi.personal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.widget.CusToolbar;
import com.easymi.personal.R;

/**
 * Created by liuzihao on 2017/11/16.
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

    public void toDaijiaHelp(View view) {
        Intent intent = new Intent(HelpCenterActivity.this, HelpCenterSubActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean isEnableSwipe() {
        return true;
    }
}
