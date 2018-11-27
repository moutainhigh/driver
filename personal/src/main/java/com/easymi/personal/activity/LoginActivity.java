package com.easymi.personal.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.easymi.common.CommApiService;
import com.easymi.component.Config;
import com.easymi.component.app.ActManager;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.Employ;
import com.easymi.component.entity.NetWorkUtil;
import com.easymi.component.entity.Setting;
import com.easymi.component.entity.SubSetting;
import com.easymi.component.entity.ZCSetting;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.GsonUtil;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.result.EmResult;
import com.easymi.component.utils.AesUtil;
import com.easymi.component.utils.Base64;
import com.easymi.component.utils.Base64Utils;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.Log;
import com.easymi.component.utils.PhoneUtil;
import com.easymi.component.utils.RsaUtils;
import com.easymi.component.utils.SHA256Util;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.CustomPopWindow;
import com.easymi.component.widget.LoadingButton;
import com.easymi.personal.McService;
import com.easymi.personal.R;
import com.easymi.personal.adapter.PopAdapter;
import com.easymi.personal.result.LoginResult;
import com.easymi.personal.result.SettingResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
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

        Intent intent222 = new Intent(Intent.ACTION_VIEW, Uri.parse("customscheme://com.rvakva.travel.publicdriver/local_push?title=华为测试"));
        intent222.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String intentUri = intent222.toUri(Intent.URI_INTENT_SCHEME);
        Log.e("intentUri", intentUri);

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

        if (saveStr.contains(",")) {
            strList = new ArrayList<>(Arrays.asList(saveStr.split(",")));
        } else {
            strList.add(saveStr);
        }

    }

    CustomPopWindow mListPopWindow;

    private void initPop() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.pop_list, null);
        //处理popWindow 显示内容
        handleListView(contentView);
        //创建并显示popWindow
        mListPopWindow = new CustomPopWindow.PopupWindowBuilder(this)
                .setView(contentView)
                .size(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)//显示大小
                .create()
                .showAsDropDown(editQiye, 0, 0);

        findViewById(R.id.hide_able_con).setVisibility(View.INVISIBLE);

        mListPopWindow.getPopupWindow().setOnDismissListener(() -> findViewById(R.id.hide_able_con).setVisibility(View.VISIBLE));
    }

    private void handleListView(View contentView) {
        RecyclerView recyclerView = contentView.findViewById(R.id.recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        PopAdapter adapter = new PopAdapter();
        adapter.setData(strList, editQiye.getText().toString());
        adapter.setOnItemClick(new PopAdapter.OnItemClick() {
            @Override
            public void onItemClick(String qiye) {
                editQiye.setText(qiye);
                editQiye.setSelection(qiye.length());
                mListPopWindow.dissmiss();
            }

            @Override
            public void onDataDelete(String qiye, int position) {
                strList.remove(position);
                adapter.setData(strList, editQiye.getText().toString());
                if (strList.size() > 0) {
                    StringBuilder sp = new StringBuilder();
                    for (int i = 0; i < strList.size(); i++) {
                        sp.append(strList.get(i));
                        if (i != strList.size() - 1) {
                            sp.append(",");
                        }
                    }
                    XApp.getPreferencesEditor().putString(Config.SP_QIYE_CODE, sp.toString()).apply();
                } else {
                    XApp.getPreferencesEditor().putString(Config.SP_QIYE_CODE, "").apply();
                    xiala.setVisibility(View.GONE);
                }
            }
        });
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
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

        editQiye.setText(XApp.getMyPreferences().getString(Config.SP_LAT_QIYE_CODE, ""));

        String enAcc = XApp.getMyPreferences().getString(Config.SP_LOGIN_ACCOUNT, "");
        String enPsw = XApp.getMyPreferences().getString(Config.SP_LOGIN_PSW, "");
        if (StringUtils.isNotBlank(enAcc) && StringUtils.isNotBlank(enPsw)) {
            String acc = AesUtil.aesDecrypt(enAcc, AesUtil.AAAAA);
            String psw = AesUtil.aesDecrypt(enPsw, AesUtil.AAAAA);
            editAccount.setText(acc);
            editPsw.setText(psw);
        }
    }

    private void setLoginBtnEnable(boolean enable) {
        loginBtn.setEnabled(enable);
        if (enable) {
            loginBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.p_corners_button_bg));
        } else {
            loginBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.corners_button_press_bg));
        }
    }

    private void login(String name, String psw, String qiyeCode) {
        McService api = ApiManager.getInstance().createLoginApi(Config.HOST, McService.class);

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
        String systemVersion = Build.VERSION.RELEASE;

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

        Log.e("LoginAc", "deviceId-->" + PushServiceFactory.getCloudPushService().getDeviceId());

//        if (Config.COMM_USE) {
//            Observable<LoginResult> observable = api
//                    .loginByQiye(AesUtil.aesEncrypt(name, AesUtil.AAAAA),
//                            AesUtil.aesEncrypt(psw, AesUtil.AAAAA),
//                            udid,
//                            "android",
//                            Build.MODEL,
//                            version,
//                            PushServiceFactory.getCloudPushService().getDeviceId(),
//                            systemVersion, //系统版本号
//                            operatorName, //运营商
//                            netType, //网络类型 3G 4G等
//                            model,    //手机品牌
//                            qiyeCode//企业码
//                    )
//                    .filter(new HttpResultFunc<>())
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread());
//
//            mRxManager.add(observable.subscribe(new MySubscriber<>(this, loginBtn, loginResult -> {
//                Employ employ = loginResult.getEmployInfo();
//                Log.e("okhttp", employ.toString());
//                employ.saveOrUpdate();
//
//                getSetting(employ);
//            })));
//        } else {
//            Observable<LoginResult> observable = api
//                    .login(AesUtil.aesEncrypt(name, AesUtil.AAAAA),
//                            AesUtil.aesEncrypt(psw, AesUtil.AAAAA),
//                            udid,
//                            Config.APP_KEY,
//                            "android",
//                            Build.MODEL,
//                            version,
//                            PushServiceFactory.getCloudPushService().getDeviceId(),
//                            systemVersion, //系统版本号
//                            operatorName, //运营商
//                            netType, //网络类型 3G 4G等
//                            model    //手机品牌
//                    )
//                    .filter(new HttpResultFunc<>())
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread());
//
//            mRxManager.add(observable.subscribe(new MySubscriber<>(this, loginBtn, loginResult -> {
//                Employ employ = loginResult.getEmployInfo();
//                Log.e("okhttp", employ.toString());
//                employ.saveOrUpdate();
//
//                getSetting(employ);
//            })));
//        }
        String randomStr = RsaUtils.getRandomString(16);
        Log.e("hufeng/randomStr",randomStr);
        XApp.getPreferencesEditor().putString(Config.AES_PASSWORD,randomStr).apply();

        String name_rsa = null;
        String pws_rsa = null;
        String randomStr_rsa = null;
        try {
            name_rsa = Base64Utils.encode(RsaUtils.encryptByPublicKey(name.getBytes("UTF-8"), getResources().getString(R.string.rsa_public_key)));
            pws_rsa = Base64Utils.encode(RsaUtils.encryptByPublicKey(SHA256Util.getSHA256StrJava(psw).getBytes("UTF-8"), getResources().getString(R.string.rsa_public_key)));
            randomStr_rsa = Base64Utils.encode(RsaUtils.encryptByPublicKey(randomStr.getBytes("UTF-8"), getResources().getString(R.string.rsa_public_key)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Observable<LoginResult> observable = api
                .loginByPW(name_rsa, pws_rsa, randomStr_rsa)
//                .loginByPW(name, SHA256Util.getSHA256StrJava(psw))
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, loginBtn, loginResult -> {
            Employ employ = loginResult.getEmployInfo();
            employ.saveOrUpdate();

            SharedPreferences.Editor editor = XApp.getPreferencesEditor();
            editor.putString(Config.SP_TOKEN, employ.token);
            editor.apply();

            getSetting(employ, name, psw);
        })));
    }

    @Override
    public boolean isEnableSwipe() {
        return false;
    }


    private void getSetting(Employ employ, String name, String psw) {
        Observable<com.easymi.common.result.SettingResult> observable = ApiManager.getInstance().createApi(Config.HOST, McService.class)
                .getAppSetting()
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        observable.subscribe(new MySubscriber<>(this, false, false, settingResult -> {
            SharedPreferences.Editor editor = XApp.getPreferencesEditor();
            editor.putBoolean(Config.SP_ISLOGIN, true);
            editor.putLong(Config.SP_DRIVERID, employ.id);
            editor.putString(Config.SP_LOGIN_ACCOUNT, AesUtil.aesEncrypt(name, AesUtil.AAAAA));
            editor.putBoolean(Config.SP_REMEMBER_PSW, checkboxRemember.isChecked());
            editor.putString(Config.SP_LOGIN_PSW, AesUtil.aesEncrypt(psw, AesUtil.AAAAA));
            editor.apply();

//            List<SubSetting> settingList = GsonUtil.parseToList(settingResult.appSetting, SubSetting[].class);
//            if (settingList != null) {
//                for (SubSetting sub : settingList) {
//                    if (Config.ZHUANCHE.equals(sub.businessType)) {
//                        ZCSetting zcSetting = GsonUtil.parseJson(sub.subJson, ZCSetting.class);
            if (settingResult.data != null) {
                ZCSetting.deleteAll();
                settingResult.data.save();
            }
//                    } else if ("daijia".equals(sub.businessType)) {
//                        Setting djSetting = GsonUtil.parseJson(sub.subJson, Setting.class);
//                        if (djSetting != null) {
//                            Setting.deleteAll();
//                            djSetting.save();
//                        }
//                    }
//                }
//            }

            pushBinding(employ.id);

            ARouter.getInstance()
                    .build("/common/WorkActivity")
                    .navigation();
            finish();
        }));
    }


    List<String> strList = new ArrayList<>();

    private void selectedQiye() {
        initPop();
    }

//add hf

    //手机唯一识别码
    String androidID = Settings.Secure.getString(XApp.getInstance().getContentResolver(), Settings.Secure.ANDROID_ID);
    String id = androidID + Build.SERIAL;

    //绑定推送
    private void pushBinding(long userId) {
        Observable<EmResult> observable = ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .pushBinding(userId,
                        "12323",
                        "/driver" + "/" + EmUtil.getEmployId(),
                        "driver-" + EmUtil.getEmployId(),
                        "ANDROID",
                        2)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, true, false, emResult -> {
            if (emResult.getCode() == 1) {
                Log.e("hufeng/binding", "bindingMerchants is Ok");
            } else {
                ToastUtil.showMessage(this, emResult.getMessage());
            }
        })));
    }
}
