package com.easymi.personal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.widget.CusToolbar;
import com.easymi.personal.R;

public class AccountAndSafeActivity extends RxBaseActivity {
    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_p_account_sec;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        findViewById(R.id.delete_account).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AccountAndSafeActivity.this, UnregisterActivity.class));
            }
        });
    }

    @Override
    public void initToolBar() {
        super.initToolBar();
        CusToolbar cusToolbar = findViewById(R.id.cus_toolbar);
        cusToolbar.setLeftBack(v -> finish());
        cusToolbar.setTitle("账号与安全");
    }
}
