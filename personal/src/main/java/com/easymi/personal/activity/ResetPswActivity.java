package com.easymi.personal.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.utils.PhoneUtil;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.OnCodeListener;
import com.easymi.component.widget.VerifyCodeView;
import com.easymi.personal.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by developerLzh on 2017/11/7 0007.
 */

public class ResetPswActivity extends RxBaseActivity {
    @Override
    public int getLayoutId() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.activity_set_psw;
    }

    LinearLayout pswCon;
    EditText editPsw;
    Button confirmPsw;

    LinearLayout authCodeCon;
    ImageView authImg;
    TextView clickRefresh;
    VerifyCodeView authInput;

    LinearLayout secCodeCon;
    TextView phoneNumber;
    TextView timerText;
    VerifyCodeView secInput;

    @Override
    public void initViews(Bundle savedInstanceState) {
        pswCon = findViewById(R.id.set_psw_con);
        editPsw = findViewById(R.id.edit_psw);
        confirmPsw = findViewById(R.id.confirm_psw);

        authCodeCon = findViewById(R.id.edit_auth_code_code);
        authImg = findViewById(R.id.auth_img);
        clickRefresh = findViewById(R.id.click_refresh);
        authInput = findViewById(R.id.auth_input);

        secCodeCon = findViewById(R.id.edit_security_code_con);
        timerText = findViewById(R.id.timer_text);
        secInput = findViewById(R.id.sec_code_input);
        phoneNumber = findViewById(R.id.phone_number);

        confirmPsw.setEnabled(false);
        editPsw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (StringUtils.isBlank(s.toString()) || s.toString().length() < 6 || s.toString().length() > 16) {
                    confirmPsw.setEnabled(false);
                    confirmPsw.setBackgroundDrawable(getResources().getDrawable(R.drawable.corners_button_press_bg));
                } else {
                    confirmPsw.setEnabled(true);
                    confirmPsw.setBackgroundDrawable(getResources().getDrawable(R.drawable.corners_button_bg));
                }
            }
        });
        editPsw.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);

        confirmPsw.setOnClickListener(v -> {
            PhoneUtil.hideKeyboard(ResetPswActivity.this);
            pswCon.setVisibility(View.GONE);
            authCodeCon.setVisibility(View.VISIBLE);
        });

        clickRefresh.setOnClickListener(v -> ToastUtil.showMessage(ResetPswActivity.this, "refresh"));
        authInput.setOnCodeListener(code -> {
            ToastUtil.showMessage(ResetPswActivity.this, "CodeComplete");
            secCodeCon.setVisibility(View.VISIBLE);
            authCodeCon.setVisibility(View.GONE);

            initSecView();
        });

        timerText.setOnClickListener(v -> {
            ToastUtil.showMessage(ResetPswActivity.this, "开始重新获取验证码");
            initSecView();
        });
        secInput.setOnCodeListener(code -> {
            ToastUtil.showMessage(ResetPswActivity.this, "完成重置密码");
            finish();
        });

    }

    private Timer timer;
    private TimerTask timerTask;
    private int time = 60;

    private void initSecView() {
        phoneNumber.setText("18148140090");
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
                        timerText.setText(time + getString(R.string.reset_sec_resend));
                        timerText.setClickable(false);
                    });
                } else {
                    timer.cancel();
                    timerTask.cancel();
                    runOnUiThread(() -> {
                        timerText.setText(R.string.reset_resend_code);
                        timerText.setClickable(true);
                    });
                }
            }
        };
        timer.schedule(timerTask, 0, 1000);
    }
}
