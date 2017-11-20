package com.easymi.common.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.easymi.component.app.ActivityManager;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.permission.RxPermissions;

import rx.functions.Action1;

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

        if (!rxPermissions.isGranted(Manifest.permission.ACCESS_COARSE_LOCATION)
                || !rxPermissions.isGranted(Manifest.permission.READ_PHONE_STATE)
                || !rxPermissions.isGranted(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            showDialog();
        } else {
            delayIn();
        }
    }

    private void delayIn() {
        Handler handler = new Handler();
        handler.postDelayed(() -> runOnUiThread(() -> {
            ARouter.getInstance()
                    .build("/personal/LoginActivity")
                    .navigation();
//            startActivity(new Intent(SplashActivity.this, WorkActivity.class));
        }), 2000);
    }

    private void delayExit() {
        Handler handler = new Handler();
        handler.postDelayed(() -> runOnUiThread(() -> {
            ActivityManager.getInstance().finishAllActivity();
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
}
