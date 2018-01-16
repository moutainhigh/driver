package com.easymi.common.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.easymi.common.R;
import com.easymi.common.mvp.work.WorkActivity;
import com.easymi.component.Config;
import com.easymi.component.app.ActManager;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.permission.RxPermissions;

import java.util.Locale;

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

    RxPermissions rxPermissions;

    @Override
    public void initViews(Bundle savedInstanceState) {

        rxPermissions = new RxPermissions(this);

        loadLanguage();

        if (!rxPermissions.isGranted(Manifest.permission.ACCESS_COARSE_LOCATION)
                || !rxPermissions.isGranted(Manifest.permission.READ_PHONE_STATE)
                || !rxPermissions.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            showDialog();
        } else {
            delayIn();
        }
    }

    private void delayIn() {
        Handler handler = new Handler();
        handler.postDelayed(() -> runOnUiThread(() -> {
            boolean isLogin = XApp.getMyPreferences().getBoolean(Config.SP_ISLOGIN, false);
            if (isLogin) {
                startActivity(new Intent(SplashActivity.this, WorkActivity.class));
            } else {
                ARouter.getInstance()
                        .build("/personal/LoginActivity")
                        .navigation();
            }
            finish();
//            startActivity(new Intent(SplashActivity.this, WorkActivity.class));
        }), 2000);
    }

    private void delayExit() {
        Handler handler = new Handler();
        handler.postDelayed(() -> runOnUiThread(() -> {
            ActManager.getInstance().finishAllActivity();
        }), 1000);
    }


    private void showDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.hint))
                .setMessage(getString(R.string.message))
                .setPositiveButton(getString(R.string.sure), (dialog1, which) -> requestPer())
                .setCancelable(false)
                .create();
        dialog.show();
    }

    private void requestPer() {
        rxPermissions.request(Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) {
                        delayIn();
                    } else {
                        Toast.makeText(SplashActivity.this, getString(R.string.exit), Toast.LENGTH_SHORT).show();
                        delayExit();
                    }
                });
    }

    /**
     * 加载本地设置的语言
     */
    private void loadLanguage() {
        SharedPreferences preferences = XApp.getMyPreferences();
        Configuration config = getResources().getConfiguration();   //获取默认配置
        int language = preferences.getInt(Config.SP_USER_LANGUAGE, Config.SP_LANGUAGE_AUTO);
        switch (language) {
            case Config.SP_SIMPLIFIED_CHINESE:
                config.locale = Locale.SIMPLIFIED_CHINESE;  //加载简体中文
                break;

            case Config.SP_TRADITIONAL_CHINESE:
                config.locale = Locale.TAIWAN;  //加载台湾繁体
                break;

            case Config.SP_LANGUAGE_AUTO:
                config.locale = Locale.getDefault();    //获取默认区域
                break;

            case Config.SP_ENGLISH:
                config.locale = Locale.ENGLISH;    //获取默认区域
                break;
        }
        getBaseContext().getResources().updateConfiguration(config, null);   //更新配置文件
    }
}
