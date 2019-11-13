package com.easymi.personal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.CusToolbar;
import com.easymi.personal.R;

public class AccountAndSafeActivity extends RxBaseActivity {

    TextView tv_status;

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
        tv_status = findViewById(R.id.tv_status);
        findViewById(R.id.delete_account).setOnClickListener(v -> startActivity(new Intent(this, UnregisterActivity.class)));
        findViewById(R.id.ll_face_check).setOnClickListener(v -> ToastUtil.showMessage(this,"点击了刷脸"));
    }

    @Override
    public void initToolBar() {
        super.initToolBar();
        CusToolbar cusToolbar = findViewById(R.id.cus_toolbar);
        cusToolbar.setLeftBack(v -> finish());
        cusToolbar.setTitle("账号与安全");
    }
}
