package com.easymi.common.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.easymi.common.R;
import com.easymi.component.base.RxBaseActivity;

/**
 * Created by developerLzh on 2017/11/3 0003.
 */
@Route(path = "/common/SplashActivity")
public class SplashActivity extends RxBaseActivity {
    @Override
    public int getLayoutId() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.activity_splash;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        Handler handler = new Handler();
        handler.postDelayed(() -> runOnUiThread(() -> {
            ARouter.getInstance()
                    .build("/personal/LoginActivity")
                    .navigation();
            finish();
        }), 2000);
    }
}
