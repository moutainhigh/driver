package com.easymi.common.activity;

import android.Manifest;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.easymi.common.BuildConfig;
import com.easymi.common.R;
import com.easymi.common.mvp.work.WorkActivity;
import com.easymi.component.Config;
import com.easymi.component.app.ActManager;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.cat.Cat;
import com.easymi.component.permission.RxPermissions;
import com.easymi.component.update.UpdateHelper;
import com.easymi.component.utils.CsSharedPreferences;
import com.easymi.component.utils.Log;
import com.easymi.component.utils.NetUtil;
import com.easymi.component.utils.RootUtil;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.utils.emulator.EmulatorCheckUtil;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:SplashActivity
 *
 * @Author: shine
 * Date: 2018/12/24 下午5:00
 * Description:
 * History:
 */
@Route(path = "/common/SplashActivity")
public class SplashActivity extends RxBaseActivity {
    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
    }

    /**
     * 权限管理
     */
    RxPermissions rxPermissions;

    private static final String TAG = "SplashActivity";

    @Override
    public void initViews(Bundle savedInstanceState) {

        rxPermissions = new RxPermissions(this);

        loadLanguage();

        if (!rxPermissions.isGranted(Manifest.permission.READ_PHONE_STATE)
                || !rxPermissions.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                || !rxPermissions.isGranted(Manifest.permission.ACCESS_FINE_LOCATION)
                || !rxPermissions.isGranted(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            Log.e(TAG, "showDialog");
            showDialog();
        } else {
            Log.e(TAG, "checkForUpdate");
            checkForUpdate();
        }
    }

    /**
     * 检查更新
     */
    private void checkForUpdate() {
        //判定用户是否单独关闭了该应用的网络
        if (NetUtil.getNetWorkState(this) != NetUtil.NETWORK_NONE) {
            if (!NetUtil.ping()) {
                //通过ping baidu的方式来判断网络是否可用
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.hint))
                        .setMessage(getString(R.string.reject_net))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.ok), (dialog1, which) -> {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        })
                        .create();
                dialog.show();
                return;
            }
        }
        //二次打包应用
        if (!new Cat(this).check()) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.common_tips)
                    .setMessage("非法应用")
                    .setPositiveButton(R.string.ok, (dialog1, which) -> {
                        finish();
                    })
                    .create();
            dialog.setCancelable(false);
            dialog.show();
            return;
        }

        //检测模拟器
        boolean isEmulator = EmulatorCheckUtil.getSingleInstance().isEmulator(this);
        if (isEmulator) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.common_tips)
                    .setMessage("检测到当前运行环境为模拟器，不能正常运行")
                    .setPositiveButton(R.string.ok, (dialog1, which) -> {
                        finish();
                    })
                    .create();
            dialog.setCancelable(false);
            dialog.show();
            return;
        }

        //检测是否root
        boolean isXposedExists = RootUtil.isXposedExists();
        boolean isRoot = RootUtil.isRoot();
        if (isRoot || isXposedExists) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.common_tips)
                    .setMessage("检测到您的手机已取得root权限或安装了xposed\n可能会存在账户安全问题,是否继续？")
                    .setPositiveButton("我已清楚问题，继续运行", (dialog1, which) -> {
                        update();
                    })
                    .setNegativeButton("退出应用", (dialog1, which) -> {
                        ActManager.getInstance().finishAllActivity();
                    })
                    .create();
            dialog.setCancelable(false);
            dialog.show();
            return;
        }
//        if (WifiProxyUtil.isWifiProxy(this)) {
//            AlertDialog dialog = new AlertDialog.Builder(this)
//                    .setTitle("提示")
//                    .setMessage("请关闭代理后再使用该程序")
//                    .setPositiveButton(R.string.ok, (dialog1, which) -> {
//                        finish();
//                    })
//                    .create();
//            dialog.setCancelable(false);
//            dialog.show();
//            return;
//        }
        XApp.getInstance().startLocService();
        update();
    }

    private void update() {
        if (BuildConfig.DEBUG) {
            jump();
        } else {
            new UpdateHelper(this, new UpdateHelper.OnNextListener() {
                @Override
                public void onNext() {
                    Log.e(TAG, "onNext");
                    runOnUiThread(() -> jump());
                }

                @Override
                public void onNoVersion() {
                    Log.e(TAG, "onNoVersion");
                    runOnUiThread(() -> jump());
                }
            });
        }
    }

    /**
     * 跳转方法
     */
    private void jump() {
        boolean isLogin = XApp.getMyPreferences().getBoolean(Config.SP_ISLOGIN, false);
        if (isLogin) {
            startActivity(new Intent(SplashActivity.this, WorkActivity.class));
        } else {
            ARouter.getInstance()
                    .build("/personal/LoginActivity")
                    .navigation();
        }
        finish();
    }

    /**
     * 延时退出
     */
    private void delayExit() {
        Observable.timer(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onCompleted() {
                        ActManager.getInstance().finishAllActivity();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Long aLong) {

                    }
                });
    }

    /**
     * 显示加载框
     */
    private void showDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("温馨提示")
                .setMessage("亲爱的司机师傅，为了您能正常使用软件，我们需要下列权限:\n"
                        + "获取位置权限-->方便管理人员根据位置为您派单\n"
                        + "读取手机状态权限-->司机与手机完成绑定防止他人登录\n"
                        + "读写外部存储权限-->存放一些资源在外部存储")
                .setPositiveButton("好", (dialog1, which) -> {
                    rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .subscribe(granted -> {
                                if (granted) {
                                    checkForUpdate();
                                } else {
                                    ToastUtil.showMessage(this, "未能获得必要权限，即将退出..");
                                    delayExit();
                                }
                            });
                    dialog1.dismiss();
                })
                .setCancelable(false)
                .create();
        dialog.show();
    }

    /**
     * 加载本地设置的语言
     */
    private void loadLanguage() {
        Log.e(TAG, "loadLanguage");

        CsSharedPreferences preferences = XApp.getMyPreferences();
        //获取默认配置
        Configuration config = getResources().getConfiguration();
        int language = preferences.getInt(Config.SP_USER_LANGUAGE, Config.SP_LANGUAGE_AUTO);
        switch (language) {
            case Config.SP_SIMPLIFIED_CHINESE:
                //加载简体中文
                config.locale = Locale.SIMPLIFIED_CHINESE;
                break;

            case Config.SP_TRADITIONAL_CHINESE:
                //加载台湾繁体
                config.locale = Locale.TRADITIONAL_CHINESE;
                break;

            case Config.SP_LANGUAGE_AUTO:
                String sysLan = preferences.getString(Config.SP_SYS_LANGUAGE, "");
                if (StringUtils.isBlank(sysLan)) {
                    XApp.getEditor().putString(Config.SP_SYS_LANGUAGE,
                            Locale.getDefault().toString()).apply();
                } else {
                    if (sysLan.contains(Locale.TAIWAN.toString())
                            || sysLan.contains(Locale.TRADITIONAL_CHINESE.toString())) {
                        config.locale = Locale.TRADITIONAL_CHINESE;
                    } else if (sysLan.contains("en")) {
                        config.locale = Locale.ENGLISH;
                    } else {
                        config.locale = Locale.SIMPLIFIED_CHINESE;
                    }
                }
                break;
            case Config.SP_ENGLISH:
                //获取默认区域
                config.locale = Locale.ENGLISH;
                break;
            default:
                break;
        }
        //更新配置文件
        getBaseContext().getResources().updateConfiguration(config, null);
    }
}
