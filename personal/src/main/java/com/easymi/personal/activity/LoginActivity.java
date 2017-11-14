package com.easymi.personal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.personal.R;

/**
 * Created by developerLzh on 2017/11/3 0003.
 */
@Route(path = "/personal/LoginActivity")
public class LoginActivity extends RxBaseActivity {

    Button loginBtn;

    TextView registerText;

    TextView resetPsw;

    @Override
    public int getLayoutId() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.activity_login;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        loginBtn = findViewById(R.id.login_button);
        loginBtn.setOnClickListener(v -> {
            ARouter.getInstance().build("/common/WorkActivity")
                    .navigation();
            finish();
        });

        registerText = findViewById(R.id.login_register);
        registerText.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this,RegisterActivity.class)));

        resetPsw = findViewById(R.id.login_forget);
        resetPsw.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this,ResetPswActivity.class)));
    }
}
