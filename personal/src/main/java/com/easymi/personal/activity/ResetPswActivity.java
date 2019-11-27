package com.easymi.personal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.easymi.component.Config;
import com.easymi.component.activity.WebActivity;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.network.NoErrSubscriberListener;
import com.easymi.component.result.EmResult;
import com.easymi.component.utils.CodeUtil;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.PhoneUtil;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.LoadingButton;
import com.easymi.component.widget.VerifyCodeView;
import com.easymi.component.widget.keyboard.SafeKeyboard;
import com.easymi.personal.McService;
import com.easymi.personal.R;
import com.easymi.personal.activity.register.RegisterAcitivty;
import com.easymi.personal.activity.register.RegisterModel;
import com.easymi.personal.result.PicCodeResult;

import java.util.Timer;
import java.util.TimerTask;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: ResetPswActivity
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description: 重置密码 未使用
 * History:
 */

public class ResetPswActivity extends RxBaseActivity {


    CusToolbar cus_toolbar;
    EditText edt_phone;
    EditText edt_auth_code;
    TextView tv_get_code;
    EditText edt_password;
    ImageView eye;
    LoadingButton btn_complete;

    EditText et_img_code;
    ImageView iv_image_code;

    private String psw;

    private String authCode;

    private String phone;

    /**
     * 唯一标示符 用于获取图形验证码
     */
    private String randomNum;


    @Override
    public void initToolBar() {
        super.initToolBar();
        cus_toolbar.setTitle("找回密码");
        cus_toolbar.setLeftBack(v -> {
           finish();
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_set_psw;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        cus_toolbar = findViewById(R.id.cus_toolbar);
        edt_phone = findViewById(R.id.edt_phone);
        edt_auth_code = findViewById(R.id.edt_auth_code);
        tv_get_code = findViewById(R.id.tv_get_code);
        edt_password = findViewById(R.id.edt_password);
        eye = findViewById(R.id.eye);
        btn_complete = findViewById(R.id.btn_complete);

        et_img_code = findViewById(R.id.et_img_code);
        iv_image_code = findViewById(R.id.iv_image_code);


        initEye();
        initEdit();

        getImgCode();

//        initKeyBoard();

        btn_complete.setEnabled(false);

        tv_get_code.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(edt_phone.getText().toString()) && edt_phone.getText().toString().length() == 11 ){
                if (!TextUtils.isEmpty(et_img_code.getText().toString()) && et_img_code.getText().toString().length() == 4){
                    sendSms();
                } else {
                    ToastUtil.showMessage(this,"请输入4位图形验证码");
                }
            }else {
                ToastUtil.showMessage(this,"请输入正确的电话号码");
            }
        });

        btn_complete.setOnClickListener(v -> {
            if (edt_phone.getText().toString().length() != 11){
                ToastUtil.showMessage(this,"请输入正确的手机号");
                return;
            }
            if (et_img_code.getText().toString().length() != 4){
                ToastUtil.showMessage(this,"请输入正确的图形验证码");
                return;
            }
            if (edt_auth_code.getText().toString().length() != 6){
                ToastUtil.showMessage(this,"请输入正确的短信验证码");
                return;
            }
            if (edt_password.getText().toString().length() < 6){
                ToastUtil.showMessage(this,"请输入6位及其以上的密码");
                return;
            }
            retrieve();
        });
    }

    /**
     * 获取图形验证码
     */
    public void getImgCode() {
        randomNum = "" + System.currentTimeMillis() + (int) ((Math.random() * 9 + 1) * 100000);
        Glide.with(this).load(Config.HOST + "api/v1/system/captcha/code/" + randomNum).into(iv_image_code);
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
                edt_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            } else {
                eye.setImageResource(R.mipmap.ic_open_eye);
                eyeOn = true;
                edt_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            }
            String input = edt_password.getText().toString();
            if (StringUtils.isNotBlank(input)) {
                edt_password.setSelection(input.length());
            }
        });
    }

    /**
     * 设置监听
     */
    private void initEdit() {
        edt_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (null != editable && StringUtils.isNotBlank(editable.toString())) {
                    if (StringUtils.isNotBlank(edt_auth_code.getText().toString())) {
                        if (StringUtils.isNotBlank(edt_auth_code.getText().toString()) && StringUtils.isNotBlank(et_img_code.getText().toString())) {
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
                    if (StringUtils.isNotBlank(edt_auth_code.getText().toString())) {
                        if (StringUtils.isNotBlank(edt_password.getText().toString()) && StringUtils.isNotBlank(edt_phone.getText().toString())) {
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

        edt_auth_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (null != editable && StringUtils.isNotBlank(editable.toString())) {
                    if (StringUtils.isNotBlank(edt_phone.getText().toString())) {
                        if (StringUtils.isNotBlank(edt_password.getText().toString()) && StringUtils.isNotBlank(et_img_code.getText().toString())) {
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

        edt_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (null != editable && StringUtils.isNotBlank(editable.toString())) {
                    if (StringUtils.isNotBlank(edt_phone.getText().toString()) && StringUtils.isNotBlank(edt_auth_code.getText().toString()) && StringUtils.isNotBlank(et_img_code.getText().toString())) {
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
        btn_complete.setEnabled(enable);
        if (enable) {
            btn_complete.setBackgroundDrawable(getResources().getDrawable(R.drawable.p_corners_button_bg));
        } else {
            btn_complete.setBackgroundDrawable(getResources().getDrawable(R.drawable.corners_button_press_bg));
        }
    }

    /**
     * 定时器
     */
    private Timer timer;
    private TimerTask timerTask;

    /**
     * 验证码60秒倒计时
     */
    private int time = 60;

    /**
     * 初始化倒计时
     */
    private void initSecView() {
//        phoneNumber.setText(phone);
        if (null != timer) {
            timer.cancel();
            timer = null;
        }
        if (null != timerTask) {
            timerTask.cancel();
            timerTask = null;
        }
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (time > 0) {
                    time--;
                    runOnUiThread(() -> {
                        tv_get_code.setText(time + getString(R.string.reset_sec_resend));
                        tv_get_code.setClickable(false);
                    });
                } else {
                    timer.cancel();
                    timerTask.cancel();
                    runOnUiThread(() -> {
                        tv_get_code.setText(R.string.reset_resend_code);
                        tv_get_code.setClickable(true);
                    });
                }
            }
        };
        timer.schedule(timerTask, 0, 1000);
    }

    @Override
    public boolean isEnableSwipe() {
        return true;
    }

    /**
     * 发送验证码
     */
    private void sendSms() {
        Observable<EmResult> observable = RegisterModel.getSms(et_img_code.getText().toString(), edt_phone.getText().toString(), randomNum,
                "PASSENGER_LOGIN_CODE", "2");

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, false, false, emResult -> {
            if (emResult.getCode() == 1) {
                ToastUtil.showMessage(this, getResources().getString(R.string.register_send_succed));
                initSecView();
            }
        })));
    }


    /**
     * 重置密码
     */
    private void retrieve(){
        Observable<EmResult> observable = RegisterModel.retrieve(edt_phone.getText().toString(),edt_password.getText().toString(), edt_auth_code.getText().toString(), randomNum);

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, false, false, emResult -> {
            if (emResult.getCode() == 1) {
                ToastUtil.showMessage(this, "密码重置成功");
                finish();
            }
        })));
    }


}
