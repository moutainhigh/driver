package com.easymi.personal.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.easymi.common.CommApiService;
import com.easymi.component.Config;
import com.easymi.component.activity.WebActivity;
import com.easymi.component.app.ActManager;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.Employ;
import com.easymi.component.entity.ZCSetting;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.ErrCode;
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.utils.AlexStatusBarUtils;
import com.easymi.component.utils.CsEditor;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.MacUtils;
import com.easymi.component.utils.MobileInfoUtil;
import com.easymi.component.utils.PhoneUtil;
import com.easymi.component.utils.RsaUtils;
import com.easymi.component.utils.SHA256Util;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.utils.SysUtil;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.utils.UIStatusBarHelper;
import com.easymi.component.widget.LoadingButton;
import com.easymi.component.widget.keyboard.SafeKeyboard;
import com.easymi.personal.McService;
import com.easymi.personal.R;
import com.easymi.personal.activity.register.RegisterAcitivty;
import com.easymi.personal.activity.register.RegisterNoticeActivity;
import com.easymi.personal.result.LoginResult;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: LoginActivity
 *
 * @Author: hufeng
 * Date: 2018/9/20 下午1:10
 * Description: 登陆页面
 * History:
 */
@Route(path = "/personal/LoginActivity")
public class LoginActivity extends RxBaseActivity {
    /**
     * 布局控件
     */
    LoadingButton loginBtn;
    TextView registerText;
    TextView resetPsw;
    EditText editAccount;
    EditText editPsw;
    ImageView eye;
    CheckBox checkboxAgreement;
    CheckBox checkboxRemember;
    TextView textAgreement;


    SafeKeyboard safeKeyboard;
    private String account;
    private String password;

    @Override
    public int getLayoutId() {
        UIStatusBarHelper.setStatusBarLightMode(this);
        AlexStatusBarUtils.setStatusColor(this, Color.WHITE);
        return R.layout.activity_login;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);//禁止截屏
        loginBtn = findViewById(R.id.login_button);
        loginBtn.setOnClickListener(v -> {
            if (!checkboxAgreement.isChecked()) {
                ToastUtil.showMessage(LoginActivity.this, getString(R.string.please_agree_agreement));
                return;
            }
            PhoneUtil.hideKeyboard(this);

            login();

        });
        editAccount = findViewById(R.id.login_et_account);
        editPsw = findViewById(R.id.login_et_password);

        loginBtn.setEnabled(false);

        registerText = findViewById(R.id.login_register);
        registerText.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterAcitivty.class);
            startActivity(intent);
        });

        resetPsw = findViewById(R.id.login_forget);
        resetPsw.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, ResetPswActivity.class)));

        eye = findViewById(R.id.eye);

        checkboxAgreement = findViewById(R.id.checkbox_agreement);
        checkboxRemember = findViewById(R.id.checkbox_remember);
        textAgreement = findViewById(R.id.text_agreement);

        initEdit();

        initEye();

        initBox();

        ActManager.getInstance().finishAllActivity(getClass().getName());


        initKeyBoard();
    }

    private void initKeyBoard() {
        LinearLayout keyboardContainer = findViewById(R.id.keyboardViewPlace);

        safeKeyboard = new SafeKeyboard(
                LoginActivity.this,
                keyboardContainer, editPsw);
        safeKeyboard.setDelDrawable(LoginActivity.this.getResources().getDrawable(R.drawable.icon_del));
        safeKeyboard.setLowDrawable(LoginActivity.this.getResources().getDrawable(R.drawable.icon_capital_default));
        safeKeyboard.setUpDrawable(LoginActivity.this.getResources().getDrawable(R.drawable.icon_capital_selected));

    }

    /**
     * 服务协议跳转
     */
    private void initBox() {
        textAgreement.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, WebActivity.class);
            intent.putExtra("url", Config.H5_HOST + "#/protocol?articleName=driverLogin&appKey=" + Config.APP_KEY);
            intent.putExtra("title", getString(R.string.login_agreement));
            startActivity(intent);
        });
    }

    private boolean eyeOn = false;

    /**
     * 可视化密码
     */
    private void initEye() {
        eye.setOnClickListener(view -> {
            if (eyeOn) {
                eye.setImageResource(R.mipmap.ic_close_eye);
                eyeOn = false;
                editPsw.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            } else {
                eye.setImageResource(R.mipmap.ic_open_eye);
                eyeOn = true;
                editPsw.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            }
            String input = editPsw.getText().toString();
            if (StringUtils.isNotBlank(input)) {
                editPsw.setSelection(input.length());
            }

        });
    }

    /**
     * 初始化edittext监听
     */
    private void initEdit() {
        editAccount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (null != editable && StringUtils.isNotBlank(editable.toString())) {
                    if (StringUtils.isNotBlank(editPsw.getText().toString())) {
                        setLoginBtnEnable(true);
                    } else {
                        setLoginBtnEnable(false);
                    }
                } else {
                    setLoginBtnEnable(false);
                }
            }
        });

        editPsw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (null != editable && StringUtils.isNotBlank(editable.toString())) {
                    if (StringUtils.isNotBlank(editAccount.getText().toString())) {
                        setLoginBtnEnable(true);
                    } else {
                        setLoginBtnEnable(false);
                    }
                } else {
                    setLoginBtnEnable(false);
                }
            }
        });

        checkboxRemember.setChecked(XApp.getMyPreferences().getBoolean(Config.SP_REMEMBER_PSW, true));
        if (XApp.getMyPreferences().getBoolean(Config.SP_REMEMBER_PSW, false)) {
            String enAcc = XApp.getMyPreferences().getString(Config.SP_LOGIN_ACCOUNT, "");
            String enPsw = XApp.getMyPreferences().getString(Config.SP_LOGIN_PSW, "");
            if (StringUtils.isNotBlank(enAcc) && StringUtils.isNotBlank(enPsw)) {
                editAccount.setText(enAcc);
                editPsw.setText(enPsw);
            }
        }
    }

    /**
     * 设置登陆按钮是否可点击
     *
     * @param enable
     */
    private void setLoginBtnEnable(boolean enable) {
        loginBtn.setEnabled(enable);
        if (enable) {
            loginBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.p_corners_button_bg));
        } else {
            loginBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.corners_button_press_bg));
        }
    }

    private void login() {
        if (EmUtil.getLastLoc() == null) {
            ToastUtil.showMessage(this, "获取当前位置失败,请重试");
            return;
        }

        account = editAccount.getText().toString();
        password = editPsw.getText().toString();

//        Observable<LoginResult> observable = ApiManager.getInstance().createApi(Config.HOST, McService.class)
//                .getIsLogin(2, account)
//                .map(new HttpResultFunc2<>())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .flatMap(new Func1<Boolean, Observable<LoginResult>>() {
//                    @Override
//                    public Observable<LoginResult> call(Boolean aBoolean) {
//                        if (!aBoolean) {
//                            return getLoginObservable(account, password);
//                        } else {
//                            showDialog();
//                            return Observable.error(new RuntimeException());
//                        }
//                    }
//                });
//
//        subscribeObservable(observable);
        subscribeObservable(getLoginObservable(account, password));
    }

    private void showDialog() {
        new AlertDialog.Builder(this)
                .setTitle("系统提示")
                .setMessage("当前账号已经登录,是否继续登录?")
                .setCancelable(false)
                .setPositiveButton("确定", (dialog, which) -> {
                    subscribeObservable(getLoginObservable(account, password));
                    dialog.dismiss();
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void subscribeObservable(Observable<LoginResult> observable) {
        mRxManager.add(observable.subscribe(new MySubscriber<>(this, loginBtn, new HaveErrSubscriberListener<LoginResult>() {
            @Override
            public void onNext(LoginResult loginResult) {
                if (loginResult.getCode() == 1) {
                    Employ employ = loginResult.data;
                    XApp.getEditor()
                            .putLong(Config.SP_DRIVERID, employ.id)
                            .putString(Config.SP_TOKEN, employ.token)
                            .apply();
                    employ.saveOrUpdate();
                    employ.updateDriverCompanyId();
                    getSetting(employ, account, password);
                } else if (loginResult.getCode() == APPLYING) {
                    Intent intent = new Intent(LoginActivity.this, RegisterNoticeActivity.class);
                    intent.putExtra("type", 2);
                    startActivity(intent);
                } else if (loginResult.getCode() == APPLY_PASS) {
                    Employ employ = loginResult.data;
                    XApp.getEditor()
                            .putLong(Config.SP_DRIVERID, employ.id)
                            .putString(Config.SP_TOKEN, employ.token)
                            .apply();
                    employ.saveOrUpdate();
                    employ.updateDriverCompanyId();
                    getSetting(employ, account, password);
                } else if (loginResult.getCode() == APPLY_REJECT) {
                    Intent intent = new Intent(LoginActivity.this, RegisterNoticeActivity.class);
                    intent.putExtra("type", 3);
                    intent.putExtra("phone", account);
                    startActivity(intent);
                } else {
                    String msg = loginResult.getMessage();
                    for (ErrCode errCode : ErrCode.values()) {
                        if (loginResult.getCode() == errCode.getCode()) {
                            msg = errCode.getShowMsg();
                            break;
                        }
                    }

                    ToastUtil.showMessage(LoginActivity.this, msg);
                }
            }

            @Override
            public void onError(int code) {
            }
        })));
    }

    private Observable<LoginResult> getLoginObservable(String name, String psw) {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String operatorName = "其他";
        String operator = tm.getSimOperator();
        if (operator != null) {
            if (operator.equals("46000") || operator.equals("46002")) {
                operatorName = "中国移动";
            } else if (operator.equals("46001")) {
                operatorName = "中国联通";
            } else if (operator.equals("46003")) {
                operatorName = "中国电信";
            }
        }

        XApp.getEditor().putString(Config.SP_TOKEN, "").apply();

        String randomStr = RsaUtils.getRandomString(16);
        XApp.getEditor().putString(Config.AES_PASSWORD, randomStr).apply();

        String mac = "";
        String imei = "";
        String imsi = "";
        String appVersion = "";
        try {
            mac = MacUtils.getMobileMAC(this);
            imei = MobileInfoUtil.getIMEI(this);
            imsi = MobileInfoUtil.getIMSI(this);
            appVersion = SysUtil.getVersionName(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ApiManager.getInstance().createApi(Config.HOST, McService.class)
                .loginByPW(name, SHA256Util.getSHA256StrJava(psw), randomStr,
                        mac, imei, imsi,
                        Build.MODEL, String.valueOf(EmUtil.getLastLoc().latitude), String.valueOf(EmUtil.getLastLoc().longitude), appVersion,
                        operatorName, "2")
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 申请中
     */
    int APPLYING = 50009;

    /**
     * 申请已经通过
     */
    int APPLY_PASS = 50010;

    /**
     * 申请拒绝
     */
    int APPLY_REJECT = 50011;

    @Override
    public boolean isEnableSwipe() {
        return false;
    }


    /**
     * 获取配置信息
     *
     * @param employ
     * @param name
     * @param psw
     */
    private void getSetting(Employ employ, String name, String psw) {
        Observable<com.easymi.common.result.SettingResult> observable = ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .getAppSetting(EmUtil.getEmployInfo().companyId)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        observable.subscribe(new MySubscriber<>(this, false, false, settingResult -> {
            CsEditor editor = XApp.getEditor();
            editor.putBoolean(Config.SP_ISLOGIN, true);
            editor.putString(Config.SP_LOGIN_ACCOUNT, name);
            editor.putBoolean(Config.SP_REMEMBER_PSW, checkboxRemember.isChecked());
            if (checkboxRemember.isChecked()) {
                editor.putString(Config.SP_LOGIN_PSW, psw);
            } else {
                editor.putString(Config.SP_LOGIN_PSW, "");
            }
            editor.apply();

            for (ZCSetting sub : settingResult.data) {
                if (EmUtil.getEmployInfo().serviceType.equals(sub.serviceType)) {
                    ZCSetting.deleteAll();
                    sub.save();
                }
            }

//            if (settingResult.data != null) {
//                for (ZCSetting sub : settingResult.data) {
//                    if (sub.serviceType.equals(Config.ZHUANCHE) ||
//                            sub.serviceType.equals(Config.CARPOOL)) {
//                        ZCSetting.deleteAll();
//                        sub.save();
//                    } else if (sub.serviceType.equals(Config.TAXI)) {
//                        TaxiSetting.deleteAll();
//                        TaxiSetting taxiSetting = new TaxiSetting();
//                        taxiSetting.isPaid = sub.isPaid;
//                        taxiSetting.isExpenses = sub.isExpenses;
//                        taxiSetting.canCancelOrder = sub.canCancelOrder;
//                        taxiSetting.isAddPrice = sub.isAddPrice;
//                        taxiSetting.employChangePrice = sub.employChangePrice;
//                        taxiSetting.employChangeOrder = sub.employChangeOrder;
//                        taxiSetting.driverRepLowBalance = sub.driverRepLowBalance;
//                        taxiSetting.passengerDistance = sub.passengerDistance;
//                        taxiSetting.version = sub.version;
//                        taxiSetting.grabOrder = sub.grabOrder;
//                        taxiSetting.distributeOrder = sub.distributeOrder;
//                        taxiSetting.serviceType = sub.serviceType;
//                        taxiSetting.save();
//                    }
//                }
//            }

            ARouter.getInstance()
                    .build("/common/WorkActivity")
                    .navigation();
            finish();
        }));
    }

    /**
     * 手机唯一识别码
     */
    String androidID = Settings.Secure.getString(XApp.getInstance().getContentResolver(), Settings.Secure.ANDROID_ID);
    String id = androidID + Build.SERIAL;
}
