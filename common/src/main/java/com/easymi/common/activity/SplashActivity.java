package com.easymi.common.activity;

import android.Manifest;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.easymi.common.BuildConfig;
import com.easymi.common.R;
import com.easymi.common.mvp.work.WorkActivity;
import com.easymi.component.Config;
import com.easymi.component.activity.WebActivity;
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
import com.easymi.component.widget.NoUnderLineSpan;

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
        checkYinSi();
    }


    AlertDialog yinSiDialog;

    /**
     * 检查隐私设置
     */
    private void checkYinSi() {
        boolean agreed = XApp.getMyPreferences().getBoolean(Config.SP_YINSI_AGREED, false);

        if (!agreed) {
            View view = LayoutInflater.from(SplashActivity.this).inflate(R.layout.yinsi_dialog, null, false);

            TextView textView = view.findViewById(R.id.textView);

            String s0 = "亲，感谢您的使用！\n\n" +
                    "我们非常重视您的个人信息和隐私保护。" +
                    "为了可以更好地保障您的个人权益，在您使用我们的产品前，请您认真仔细的阅读";

            String s1 = "《服务人员合作协议》";
            NoUnderLineSpan noUnderLineSpan = new NoUnderLineSpan(this, WebActivity.IWebVariable.DRIVER_LOGIN, R.string.driver_login);

            String s2 = "和";

            String s3 = "《隐私权政策》";
            NoUnderLineSpan noUnderLineSpan3 = new NoUnderLineSpan(this, WebActivity.IWebVariable.DRIVER_PRIVACY_POLICY, R.string.driver_policy);

            String s4 = "的全部内容，同意并接受全部条款后开始使用我们的产品，以及享受我们提供的服务。";

            SpannableString text5 = new SpannableString(s0 + s1 + s2 + s3 + s4);
            text5.setSpan(noUnderLineSpan, s0.length(), s0.length() + s1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            text5.setSpan(noUnderLineSpan3, s0.length() + s1.length() + 1, s0.length() + s1.length() + 1 + s3.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            textView.setText(text5);

            textView.setMovementMethod(LinkMovementMethod.getInstance());


            Button agree = view.findViewById(R.id.agree_yinsi);
            Button disagree = view.findViewById(R.id.dis_agree_yinsi);

            agree.setOnClickListener(v -> {
                XApp.getEditor().putBoolean(Config.SP_YINSI_AGREED, true).apply();
                yinSiDialog.dismiss();
                initData();
            });
            disagree.setOnClickListener(v -> {
                yinSiDialog.dismiss();
                showOnceHint();
            });

            yinSiDialog = new AlertDialog.Builder(this).setView(view).create();
            yinSiDialog.setCanceledOnTouchOutside(false);
            yinSiDialog.setCancelable(false);
            yinSiDialog.show();
        } else {
            initData();
        }
    }

    AlertDialog onceHintDialog;

    /**
     * 第一次提醒同意隐私协议
     */
    private void showOnceHint() {
        View view = LayoutInflater.from(this).inflate(R.layout.once_hint_dialog, null, false);

        Button once_dis_agree = view.findViewById(R.id.once_dis_agree);
        Button once_show_yinsi = view.findViewById(R.id.once_show_yinsi);

        once_dis_agree.setOnClickListener(v -> {
            onceHintDialog.dismiss();
            showTwiceHint();
        });

        once_show_yinsi.setOnClickListener(v -> {
            onceHintDialog.dismiss();
            yinSiDialog.show();
        });

        onceHintDialog = new AlertDialog.Builder(this).setView(view).create();
        onceHintDialog.setCanceledOnTouchOutside(false);
        onceHintDialog.setCancelable(false);
        onceHintDialog.show();
    }

    AlertDialog twiceHintDialog;

    /**
     * 第二次提醒同意隐私协议
     */
    private void showTwiceHint() {
        View view = LayoutInflater.from(this).inflate(R.layout.twice_hint_dialog, null, false);

        Button twice_dis_agree = view.findViewById(R.id.twice_dis_agree);
        Button twice_show_yinsi = view.findViewById(R.id.twice_show_yinsi);

        twice_dis_agree.setOnClickListener(v -> {
            twiceHintDialog.dismiss();
            finish();//退出应用
        });

        twice_show_yinsi.setOnClickListener(v -> {
            twiceHintDialog.dismiss();
            yinSiDialog.show();
        });

        twiceHintDialog = new AlertDialog.Builder(this).setView(view).create();
        twiceHintDialog.setCanceledOnTouchOutside(false);
        twiceHintDialog.setCancelable(false);
        twiceHintDialog.show();
    }

    private void initData(){
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
                        + "获取位置权限-->获取实时位置，为您精准派单\n"
                        + "读取手机状态权限-->保障账户安全\n"
                        + "读写外部存储权限-->存储一些文件到磁盘")
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
