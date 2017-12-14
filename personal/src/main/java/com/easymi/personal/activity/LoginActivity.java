package com.easymi.personal.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.amap.api.navi.model.NaviLatLng;
import com.easymi.component.Config;
import com.easymi.component.activity.NaviActivity;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.EmLoc;
import com.easymi.component.loc.LocObserver;
import com.easymi.component.loc.LocReceiver;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.network.NoErrSubscriberListener;
import com.easymi.component.utils.AesUtil;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.widget.LoadingButton;
import com.easymi.personal.R;
import com.easymi.component.entity.Employ;
import com.easymi.personal.McService;
import com.easymi.personal.result.LoginResult;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by developerLzh on 2017/11/3 0003.
 */
@Route(path = "/personal/LoginActivity")
public class LoginActivity extends RxBaseActivity implements LocObserver {

    LoadingButton loginBtn;

    TextView registerText;

    TextView resetPsw;

    EditText editAccount;
    EditText editPsw;

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
            login("13518148468", "1234");

        });
        editAccount = findViewById(R.id.login_et_account);
        editPsw = findViewById(R.id.login_et_password);

        loginBtn.setEnabled(false);

        registerText = findViewById(R.id.login_register);
        registerText.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterWebActivity.class));
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

        XApp.getInstance().startLocService();
    }

    private void initBox() {
        textAgreement.setOnClickListener(view -> ARouter.getInstance()
                .build("/component/WebActivity")
                .withString("url", "www.baidu.com")
                .withString("title", getString(R.string.login_agreement))
                .navigation());
    }

    private boolean eyeOn = false;

    @Override
    protected void onStart() {
        super.onStart();
        LocReceiver.getInstance().addObserver(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocReceiver.getInstance().deleteObserver(this);
    }

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

    }

    private void setLoginBtnEnable(boolean enable) {
        loginBtn.setEnabled(enable);
        if (enable) {
            loginBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.corners_button_bg));
        } else {
            loginBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.corners_button_press_bg));
        }
    }

    private void login(String name, String psw) {
        McService api = ApiManager.getInstance().createApi(Config.HOST, McService.class);

        Observable<LoginResult> observable = api
                .login(AesUtil.aesEncrypt(name, AesUtil.AAAAA), AesUtil.aesEncrypt(psw, AesUtil.AAAAA), Config.APP_KEY)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, loginBtn, loginResult -> {
            Employ employ = loginResult.getEmployInfo();
            Log.e("okhttp", employ.toString());
            employ.saveOrUpdate();
            SharedPreferences.Editor editor = XApp.getPreferencesEditor();
            editor.putBoolean(Config.SP_ISLOGIN, true);
            editor.putLong(Config.SP_DRIVERID, employ.id);
            editor.apply();
            ARouter.getInstance()
                    .build("/common/WorkActivity")
                    .navigation();
        })));
    }

    @Override
    public void receiveLoc(EmLoc loc) {
        XApp.getInstance().stopLocService();
    }
}
