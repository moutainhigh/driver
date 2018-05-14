package com.easymi.personal.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.ListPopupWindow;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.easymi.component.Config;
import com.easymi.component.app.ActManager;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.Employ;
import com.easymi.component.entity.NetWorkUtil;
import com.easymi.component.entity.Setting;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.utils.AesUtil;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.Log;
import com.easymi.component.utils.PhoneUtil;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.LoadingButton;
import com.easymi.personal.McService;
import com.easymi.personal.R;
import com.easymi.personal.adapter.PopListAdapter;
import com.easymi.personal.result.LoginResult;
import com.easymi.personal.result.SettingResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by developerLzh on 2017/11/3 0003.
 */
@Route(path = "/personal/LoginActivity")
public class LoginActivity extends RxBaseActivity {

    LoadingButton loginBtn;

    TextView registerText;

    TextView resetPsw;

    EditText editAccount;
    EditText editPsw;
    EditText editQiye;

    ImageView xiala;

    ImageView eye;

    CheckBox checkboxAgreement;
    CheckBox checkboxRemember;

    TextView textAgreement;

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
//            login("13518181189", "1234");

            if (!checkboxAgreement.isChecked()) {
                ToastUtil.showMessage(LoginActivity.this, getString(R.string.please_agree_agreement));
                return;
            }
            PhoneUtil.hideKeyboard(this);


            login(editAccount.getText().toString(), editPsw.getText().toString(), editQiye.getText().toString());


        });
        editAccount = findViewById(R.id.login_et_account);
        editPsw = findViewById(R.id.login_et_password);

        loginBtn.setEnabled(false);

        registerText = findViewById(R.id.login_register);
        registerText.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterLocalActivity.class);
            intent.putExtra("url", Config.REGISTER_URL);
            intent.putExtra("title", getString(R.string.register_title));
            startActivity(intent);
        });

        resetPsw = findViewById(R.id.login_forget);
        resetPsw.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, ResetPswActivity.class)));

        eye = findViewById(R.id.eye);

        checkboxAgreement = findViewById(R.id.checkbox_agreement);
        checkboxRemember = findViewById(R.id.checkbox_remember);
        textAgreement = findViewById(R.id.text_agreement);

        editQiye = findViewById(R.id.edit_qiye);
        xiala = findViewById(R.id.xiala);

        initEdit();

        initEye();

        initBox();

        initQiye();

        ActManager.getInstance().removeActivity(this);//该activity不加入Activity栈

        ActManager.getInstance().finishAllActivity();
    }

    private void initQiye() {

        if (!Config.COMM_USE) {
            findViewById(R.id.qiye_con).setVisibility(View.GONE);
            findViewById(R.id.qiye_line).setVisibility(View.GONE);
        }

        String saveStr = XApp.getMyPreferences().getString(Config.SP_QIYE_CODE, "");
        if (StringUtils.isBlank(saveStr)) {
            xiala.setVisibility(View.GONE);
        } else {
            xiala.setVisibility(View.VISIBLE);
        }
        xiala.setOnClickListener(view -> selectedQiye());

        strList.clear();

        listPopupWindow = new ListPopupWindow(this);
        adapter = new PopListAdapter(this);

        if (saveStr.contains(",")) {
            strList = Arrays.asList(saveStr.split(","));
        } else {
            strList.add(saveStr);
        }
        adapter.setStrList(strList);

        // ListView适配器
        listPopupWindow.setAdapter(
                new ArrayAdapter<>(getApplicationContext(), R.layout.simple_list_item_1, strList));

        listPopupWindow.setOnItemClickListener((parent, view, position, id) -> {
            editQiye.setText(strList.get(position));
            listPopupWindow.dismiss();
        });

        // 对话框的宽高
        listPopupWindow.setWidth(500);
        listPopupWindow.setAnchorView(xiala);

        listPopupWindow.setHorizontalOffset(0);
        listPopupWindow.setVerticalOffset(0);

        listPopupWindow.setModal(false);
    }

    private void initBox() {
        textAgreement.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, ArticleActivity.class);
            intent.putExtra("tag", "DriverServiceAgreement");
            intent.putExtra("title", getString(R.string.login_agreement));
            startActivity(intent);
        });
    }

    private boolean eyeOn = false;

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
                        if (Config.COMM_USE) {
                            if (StringUtils.isNotBlank(editQiye.getText().toString())) {
                                setLoginBtnEnable(true);
                            } else {
                                setLoginBtnEnable(false);
                            }
                        } else {
                            setLoginBtnEnable(true);
                        }
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
                        if (Config.COMM_USE) {
                            if (StringUtils.isNotBlank(editQiye.getText().toString())) {
                                setLoginBtnEnable(true);
                            } else {
                                setLoginBtnEnable(false);
                            }
                        } else {
                            setLoginBtnEnable(true);
                        }
                    } else {
                        setLoginBtnEnable(false);
                    }
                } else {
                    setLoginBtnEnable(false);
                }
            }
        });

        editQiye.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (null != editable && StringUtils.isNotBlank(editable.toString())) {
                    if (StringUtils.isNotBlank(editAccount.getText().toString())
                            && StringUtils.isNotBlank(editPsw.getText().toString())) {
                        setLoginBtnEnable(true);
                    } else {
                        setLoginBtnEnable(false);
                    }
                } else {
                    setLoginBtnEnable(false);
                }
            }
        });

        String enAcc = XApp.getMyPreferences().getString(Config.SP_LOGIN_ACCOUNT, "");
        String enPsw = XApp.getMyPreferences().getString(Config.SP_LOGIN_PSW, "");
        if (StringUtils.isNotBlank(enAcc) && StringUtils.isNotBlank(enPsw)) {
            String acc = AesUtil.aesDecrypt(enAcc, AesUtil.AAAAA);
            String psw = AesUtil.aesDecrypt(enPsw, AesUtil.AAAAA);
            editAccount.setText(acc);
            editPsw.setText(psw);
        }
        editQiye.setText(XApp.getMyPreferences().getString(Config.SP_LAT_QIYE_CODE, ""));
    }

    private void setLoginBtnEnable(boolean enable) {
        loginBtn.setEnabled(enable);
        if (enable) {
            loginBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.corners_button_bg));
        } else {
            loginBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.corners_button_press_bg));
        }
    }

    private void login(String name, String psw, String qiyeCode) {
        McService api = ApiManager.getInstance().createApi(Config.HOST, McService.class);

        String udid = PhoneUtil.getUDID(this);

        Log.e("LoginActivity", "udid-->" + udid);
        Log.e("LoginActivity", "deviceId-->" + PushServiceFactory.getCloudPushService().getDeviceId());
        String version = "";
        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        /**
         * 系统版本
         */
        String systemVersion = android.os.Build.VERSION.RELEASE;

        String model = Build.MODEL;

        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        String operatorName = "未知";
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
        String netType = NetWorkUtil.getNetWorkTypeName(this);

        if (Config.COMM_USE) {
            Observable<LoginResult> observable = api
                    .loginByQiye(AesUtil.aesEncrypt(name, AesUtil.AAAAA),
                            AesUtil.aesEncrypt(psw, AesUtil.AAAAA),
                            udid,
                            "android",
                            Build.MODEL,
                            version,
                            PushServiceFactory.getCloudPushService().getDeviceId(),
                            systemVersion, //系统版本号
                            operatorName, //运营商
                            netType, //网络类型 3G 4G等
                            model,    //手机品牌
                            qiyeCode//企业码
                    )
                    .filter(new HttpResultFunc<>())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());

            mRxManager.add(observable.subscribe(new MySubscriber<>(this, loginBtn, loginResult -> {
                Employ employ = loginResult.getEmployInfo();
                Log.e("okhttp", employ.toString());
                employ.saveOrUpdate();

                getSetting(employ);
            })));
        } else {
            Observable<LoginResult> observable = api
                    .login(AesUtil.aesEncrypt(name, AesUtil.AAAAA),
                            AesUtil.aesEncrypt(psw, AesUtil.AAAAA),
                            udid,
                            Config.APP_KEY,
                            "android",
                            Build.MODEL,
                            version,
                            PushServiceFactory.getCloudPushService().getDeviceId(),
                            systemVersion, //系统版本号
                            operatorName, //运营商
                            netType, //网络类型 3G 4G等
                            model    //手机品牌
                    )
                    .filter(new HttpResultFunc<>())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());

            mRxManager.add(observable.subscribe(new MySubscriber<>(this, loginBtn, loginResult -> {
                Employ employ = loginResult.getEmployInfo();
                Log.e("okhttp", employ.toString());
                employ.saveOrUpdate();

                getSetting(employ);
            })));
        }
    }

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    private void getSetting(Employ employ) {
        Observable<SettingResult> observable = ApiManager.getInstance().createApi(Config.HOST, McService.class)
                .getAppSetting(employ.app_key)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        observable.subscribe(new MySubscriber<>(this, false, false, settingResult -> {
            Setting setting = settingResult.setting;
            if (null != setting) {

                SharedPreferences.Editor editor = XApp.getPreferencesEditor();
                editor.putBoolean(Config.SP_ISLOGIN, true);
                editor.putLong(Config.SP_DRIVERID, employ.id);
                editor.putString(Config.SP_LOGIN_ACCOUNT, AesUtil.aesEncrypt(employ.phone, AesUtil.AAAAA));
                editor.putBoolean(Config.SP_REMEMBER_PSW, checkboxRemember.isChecked());
                editor.putString(Config.SP_APP_KEY, employ.app_key);
                editor.putString(Config.SP_LOGIN_PSW, employ.password);
                editor.putString(Config.SP_LAT_QIYE_CODE, editQiye.getText().toString());
                editor.apply();

                String saveStr = XApp.getMyPreferences().getString(Config.SP_QIYE_CODE, "");
                if (StringUtils.isNotBlank(saveStr)) {
                    List<String> stringList = new ArrayList<>();
                    if (saveStr.contains(",")) {
                        stringList = Arrays.asList(saveStr.split(","));
                    } else {
                        stringList.add(saveStr);
                    }
                    boolean haveStr = false;
                    for (String s : stringList) {
                        if (s.equals(editQiye.getText().toString())) {
                            haveStr = true;
                            break;
                        }
                    }
                    if (!haveStr) {
                        saveStr += "," + editQiye.getText().toString();
                        XApp.getMyPreferences().edit().putString(Config.SP_QIYE_CODE, saveStr).apply();
                    }
                } else {
                    XApp.getMyPreferences().edit().putString(Config.SP_QIYE_CODE, editQiye.getText().toString()).apply();
                }

                Setting.deleteAll();
                setting.save();

                if (setting.doubleCheck == 1) {//开启
                    Intent intent = new Intent(LoginActivity.this, ResetPswActivity.class);
                    intent.putExtra("flag", "doubleCheck");
                    intent.putExtra("phone", editAccount.getText().toString());
                    intent.putExtra("psw", editPsw.getText().toString());
                    startActivity(intent);
                } else {
                    ARouter.getInstance()
                            .build("/common/WorkActivity")
                            .navigation();
                    finish();
                }
            }
        }));
    }

    List<String> strList = new ArrayList<>();

    private ListPopupWindow listPopupWindow;
    PopListAdapter adapter;

    private void selectedQiye() {
        listPopupWindow.show();
    }
}
