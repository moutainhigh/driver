package com.easymi.personal.activity;

import android.os.Bundle;

import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.widget.CusToolbar;
import com.easymi.personal.R;

/**
 * Created by developerLzh on 2017/11/11 0011.
 */

public class TixianRuleActivity extends RxBaseActivity {

    CusToolbar cusToolbar;

    @Override
    public void initToolBar() {
        cusToolbar = findViewById(R.id.cus_toolbar);
        cusToolbar.setLeftBack(view -> finish());
        cusToolbar.setTitle(R.string.tixian_rule);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_tixian_rules;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {

    }
}
