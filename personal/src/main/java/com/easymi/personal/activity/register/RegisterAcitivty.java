package com.easymi.personal.activity.register;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.bumptech.glide.Glide;
import com.easymi.component.Config;
import com.easymi.component.activity.WebActivity;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.network.ErrCode;
import com.easymi.component.network.ErrCodeTran;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.result.EmResult;
import com.easymi.component.utils.AlexStatusBarUtils;
import com.easymi.component.utils.CommonUtil;
import com.easymi.component.utils.PhoneUtil;
import com.easymi.component.utils.RsaUtils;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.utils.UIStatusBarHelper;
import com.easymi.component.widget.LoadingButton;
import com.easymi.component.widget.keyboard.SafeKeyboard;
import com.easymi.personal.R;
import com.easymi.personal.activity.LoginActivity;
import com.easymi.personal.entity.Register;

import java.util.Locale;

import rx.Observable;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: RegisterAcitivty
 *
 * @Author: shine
 * Date: 2018/12/18 下午3:16
 * Description:
 * History:
 */
@Route(path = "/personal/RegisterAcitivty")
public class RegisterAcitivty extends RxBaseActivity {

    LoadingButton register_button;
    TextView tv_get_code;
    EditText et_phone;
    EditText et_code;
    EditText et_password;
    ImageView eye;
    CheckBox checkbox_agreement;
    TextView text_agreement;
    ImageView iv_image_code;
    EditText et_img_code;

    /**
     * 唯一标示符 用于获取图形验证码
     */
    private String randomNum;

    SafeKeyboard safeKeyboard;

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        UIStatusBarHelper.setStatusBarLightMode(this);
        AlexStatusBarUtils.setStatusColor(this, Color.WHITE);
        return R.layout.activity_register;
    }

    private void initKeyBoard() {
        LinearLayout keyboardContainer = findViewById(R.id.keyboardViewPlace);


        safeKeyboard = new SafeKeyboard(
                RegisterAcitivty.this,
                keyboardContainer, et_password);
        safeKeyboard.setDelDrawable(RegisterAcitivty.this.getResources().getDrawable(R.drawable.icon_del));
        safeKeyboard.setLowDrawable(RegisterAcitivty.this.getResources().getDrawable(R.drawable.icon_capital_default));
        safeKeyboard.setUpDrawable(RegisterAcitivty.this.getResources().getDrawable(R.drawable.icon_capital_selected));

    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);//禁止截屏
        findById();

        initEdit();
        initEye();
        initBox();

        getImgCode();

        initKeyBoard();

        setLoginBtnEnable(false);

        register_button.setOnClickListener(v -> {
            if (!StringUtils.isNotBlank(et_phone.getText().toString())) {
                ToastUtil.showMessage(RegisterAcitivty.this, getString(R.string.login_pl_phone));
                return;
            }
            if (!CommonUtil.isMobileNO(et_phone.getText().toString())) {
                ToastUtil.showMessage(RegisterAcitivty.this, getString(R.string.register_cheack_phone));
                return;
            }
            if (!StringUtils.isNotBlank(et_img_code.getText().toString())) {
                ToastUtil.showMessage(RegisterAcitivty.this, getString(R.string.rg_iamge_code));
                return;
            }
            if (!StringUtils.isNotBlank(et_code.getText().toString())) {
                ToastUtil.showMessage(RegisterAcitivty.this, getString(R.string.register_code_hint));
                return;
            }
            if (!StringUtils.isNotBlank(et_password.getText().toString())) {
                ToastUtil.showMessage(RegisterAcitivty.this, getString(R.string.register_input_ps));
                return;
            }
            if (!checkbox_agreement.isChecked()) {
                ToastUtil.showMessage(RegisterAcitivty.this, getString(R.string.please_agree_agreement));
                return;
            }
            PhoneUtil.hideKeyboard(this);

            register();
        });

        tv_get_code.setOnClickListener(v -> {
            if (!StringUtils.isNotBlank(et_phone.getText().toString())) {
                ToastUtil.showMessage(RegisterAcitivty.this, getString(R.string.login_pl_phone));
                return;
            }
            if (!CommonUtil.isMobileNO(et_phone.getText().toString())) {
                ToastUtil.showMessage(RegisterAcitivty.this, getString(R.string.register_cheack_phone));
                return;
            }
            if (!StringUtils.isNotBlank(et_img_code.getText().toString())) {
                ToastUtil.showMessage(RegisterAcitivty.this, getString(R.string.rg_iamge_code));
                return;
            }
            sendSms();
        });

        iv_image_code.setOnClickListener(view -> getImgCode());
    }

    /**
     * 初始化控件
     */
    public void findById() {
        register_button = findViewById(R.id.register_button);
        tv_get_code = findViewById(R.id.tv_get_code);
        et_phone = findViewById(R.id.et_phone);
        et_code = findViewById(R.id.et_code);
        et_password = findViewById(R.id.et_password);
        eye = findViewById(R.id.eye);
        checkbox_agreement = findViewById(R.id.checkbox_agreement);
        text_agreement = findViewById(R.id.text_agreement);
        iv_image_code = findViewById(R.id.iv_image_code);
        et_img_code = findViewById(R.id.et_img_code);
    }

    /**
     * 设置监听
     */
    private void initEdit() {
        et_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (null != editable && StringUtils.isNotBlank(editable.toString())) {
                    if (StringUtils.isNotBlank(et_code.getText().toString())) {
                        if (StringUtils.isNotBlank(et_password.getText().toString()) && StringUtils.isNotBlank(et_img_code.getText().toString())) {
                            setLoginBtnEnable(true);
                        } else {
                            setLoginBtnEnable(false);
                        }
                    } else {
                        setLoginBtnEnable(false);
                    }
                } else {
                    setLoginBtnEnable(false);
                }
            }
        });

        et_img_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (null != editable && StringUtils.isNotBlank(editable.toString())) {
                    if (StringUtils.isNotBlank(et_code.getText().toString())) {
                        if (StringUtils.isNotBlank(et_password.getText().toString()) && StringUtils.isNotBlank(et_phone.getText().toString())) {
                            setLoginBtnEnable(true);
                        } else {
                            setLoginBtnEnable(false);
                        }
                    } else {
                        setLoginBtnEnable(false);
                    }
                } else {
                    setLoginBtnEnable(false);
                }
            }
        });

        et_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (null != editable && StringUtils.isNotBlank(editable.toString())) {
                    if (StringUtils.isNotBlank(et_phone.getText().toString())) {
                        if (StringUtils.isNotBlank(et_password.getText().toString()) && StringUtils.isNotBlank(et_img_code.getText().toString())) {
                            setLoginBtnEnable(true);
                        } else {
                            setLoginBtnEnable(false);
                        }
                    } else {
                        setLoginBtnEnable(false);
                    }
                } else {
                    setLoginBtnEnable(false);
                }
            }
        });

        et_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (null != editable && StringUtils.isNotBlank(editable.toString())) {
                    if (StringUtils.isNotBlank(et_phone.getText().toString()) && StringUtils.isNotBlank(et_code.getText().toString()) && StringUtils.isNotBlank(et_img_code.getText().toString())) {
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

    /**
     * 设置按钮是否可点击
     *
     * @param enable
     */
    private void setLoginBtnEnable(boolean enable) {
        register_button.setEnabled(enable);
        if (enable) {
            register_button.setBackgroundDrawable(getResources().getDrawable(R.drawable.p_corners_button_bg));
        } else {
            register_button.setBackgroundDrawable(getResources().getDrawable(R.drawable.corners_button_press_bg));
        }
    }

    /**
     * 设置服务人员协议跳转
     */
    private void initBox() {
        text_agreement.setOnClickListener(view -> {
            Intent intent = new Intent(this, WebActivity.class);
            intent.putExtra("url", Config.H5_HOST+"#/protocol?articleName=driverLogin&appKey=" + Config.APP_KEY);
            intent.putExtra("title", getString(R.string.login_agreement));
            startActivity(intent);
        });
    }

    /**
     * 能否看密码
     */
    private boolean eyeOn = false;

    /**
     * 可视密码点击事件
     */
    private void initEye() {
        eye.setOnClickListener(view -> {
            if (eyeOn) {
                eye.setImageResource(R.mipmap.ic_close_eye);
                eyeOn = false;
                et_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            } else {
                eye.setImageResource(R.mipmap.ic_open_eye);
                eyeOn = true;
                et_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            }
            String input = et_password.getText().toString();
            if (StringUtils.isNotBlank(input)) {
                et_password.setSelection(input.length());
            }
        });
    }

    /**
     * 验证码倒计时
     */
    public void countDown() {
        et_code.requestFocus();  //申请获取焦点
        //开始倒计时
        new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tv_get_code.setClickable(false);
                tv_get_code.setText("" + millisUntilFinished / 1000 + "s");
                tv_get_code.setTextColor(getResources().getColor(R.color.color_999999));
            }

            @Override
            public void onFinish() {
                tv_get_code.setClickable(true);
                tv_get_code.setText(getString(R.string.register_send_code));
                tv_get_code.setTextColor(getResources().getColor(R.color.colorAccent));
            }
        }.start();
    }

    /**
     * 跳转注册
     *
     * @param id 注册返回id
     */
    public void startBase(Long id) {
        Intent intent = new Intent(this, RegisterBaseActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("phone", et_phone.getText().toString());
        startActivity(intent);
    }

    /**
     * 获取图形验证码
     */
    public void getImgCode() {
        randomNum = "" + System.currentTimeMillis() + (int) ((Math.random() * 9 + 1) * 100000);
        Glide.with(this).load(Config.HOST + "api/v1/public/app/captcha/code/" + randomNum).into(iv_image_code);
    }

    /**
     * 发送验证码
     */
    private void sendSms() {
        String code_rsa = null;
        String phone_rsa = null;
        String randomNum_rsa = null;
        String type_rsa = null;
        String userType_rsa = null;
        try {
            code_rsa = RsaUtils.rsaEncode( et_img_code.getText().toString());
            phone_rsa = RsaUtils.rsaEncode( et_phone.getText().toString());
            randomNum_rsa = RsaUtils.rsaEncode( randomNum);
            type_rsa = RsaUtils.rsaEncode( "PASSENGER_LOGIN_CODE");
            userType_rsa = RsaUtils.rsaEncode( "2");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Observable<EmResult> observable = RegisterModel.getSms(code_rsa, phone_rsa, randomNum_rsa, type_rsa, userType_rsa);
        mRxManager.add(observable.subscribe(new MySubscriber<>(this, false, false, emResult -> {
            if (emResult.getCode() == 1) {
                ToastUtil.showMessage(this, getResources().getString(R.string.register_send_succed));
                countDown();
            }
        })));
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

    /**
     * 调用注册接口
     */
    private void register() {
        String password_rsa = null;
        String phone_rsa = null;
        String smsCode_rsa = null;
        String random_rsa = null;
        try {
            password_rsa = RsaUtils.rsaEncode( et_password.getText().toString());
            phone_rsa = RsaUtils.rsaEncode( et_phone.getText().toString());
            smsCode_rsa = RsaUtils.rsaEncode( et_code.getText().toString());
            random_rsa = RsaUtils.rsaEncode( randomNum);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Observable<Register> observable = RegisterModel.register(password_rsa, phone_rsa, smsCode_rsa,random_rsa);
        mRxManager.add(observable.subscribe(new MySubscriber<>(this, register_button, register -> {
            if (register.getCode() == 1) {
                startBase(register.data);
            } else if (register.getCode() == APPLYING) {
                Intent intent = new Intent(this, RegisterNoticeActivity.class);
                intent.putExtra("type", 2);
                startActivity(intent);
            } else if (register.getCode() == APPLY_PASS) {
                ToastUtil.showMessage(this, "申请已经通过");
            } else if (register.getCode() == APPLY_REJECT) {
                Intent intent = new Intent(this, RegisterNoticeActivity.class);
                intent.putExtra("type", 3);
                intent.putExtra("phone", et_phone.getText().toString());
                startActivity(intent);
            }else {
                String msg = register.getMessage();
                //获取默认配置
                Configuration config = XApp.getInstance().getResources().getConfiguration();
                if (config.locale == Locale.TAIWAN || config.locale == Locale.TRADITIONAL_CHINESE) {
                    for (ErrCodeTran errCode : ErrCodeTran.values()) {
                        if (register.getCode() == errCode.getCode()) {
                            msg = errCode.getShowMsg();
                            break;
                        }
                    }
                } else {
                    for (ErrCode errCode : ErrCode.values()) {
                        if (register.getCode() == errCode.getCode()) {
                            msg = errCode.getShowMsg();
                            break;
                        }
                    }
                }
                ToastUtil.showMessage(this, msg);
            }
        })));
    }
}
