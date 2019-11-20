package com.easymi.common.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;

import com.easymi.common.CommApiService;
import com.easymi.common.R;
import com.easymi.common.entity.AuthResult;
import com.easymi.common.entity.IdCardResult;
import com.easymi.common.entity.Pic;
import com.easymi.common.entity.QiNiuToken;
import com.easymi.common.faceCheck.RegisterAndRecognizeActivity;
import com.easymi.component.Config;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.result.EmResult;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.LoadingButton;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: AuthenticationActivity
 * @Author: hufeng
 * @Date: 2019/11/18 下午2:18
 * @Description:
 * @History:
 */
public class AuthenticationActivity extends RxBaseActivity {

    CusToolbar cus_toolbar;
    EditText et_name;
    EditText et_idcard;
    ImageView iv_scan;
    LoadingButton loading_btn;

    @Override
    public void initToolBar() {
        cus_toolbar = findViewById(R.id.cus_toolbar);
        cus_toolbar.setTitle("实名认证");
        cus_toolbar.setLeftBack(v -> {
            finish();
        });
    }

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_authentication;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        et_name = findViewById(R.id.et_name);
        et_idcard = findViewById(R.id.et_idcard);
        iv_scan = findViewById(R.id.iv_scan);
        loading_btn = findViewById(R.id.loading_btn);

        et_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setBtnClick();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        setEditTextInputSpace(et_idcard);
        et_idcard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setBtnClick();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        loading_btn.setOnClickListener(v -> {
            authentication();
        });

        iv_scan.setOnClickListener(v -> {

            File file = new File(getFilesDir(), "pic.jpg");
            if (file.isFile() && file.exists()){
                file.delete();
            }

            Intent intent = new Intent(AuthenticationActivity.this, CameraActivity.class);
            intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH, new File(getFilesDir(), "pic.jpg").getAbsolutePath());
            intent.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_ID_CARD_FRONT);
            startActivityForResult(intent, REQUEST_CODE_CAMERA);
        });

        getQiniuToken();
    }

    private static final int REQUEST_CODE_CAMERA = 102;

    /**
     * 设置按钮是否可以点击
     */
    public void setBtnClick() {
        if (!TextUtils.isEmpty(et_name.getText().toString()) && (et_idcard.getText().toString().length() == 18)) {
            loading_btn.setClickable(true);
            loading_btn.setBackgroundResource(R.drawable.corners_button_bg);
        } else {
            loading_btn.setClickable(false);
            loading_btn.setBackgroundResource(R.drawable.corners_button_press_bg);
        }
    }

    /**
     * 实名认证
     */
    public void authentication() {
        rx.Observable<AuthResult> observable = ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .authentication(et_name.getText().toString(), et_idcard.getText().toString())
                .filter(new HttpResultFunc<>())
                .subscribeOn(rx.schedulers.Schedulers.io())
                .observeOn(rx.android.schedulers.AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, false, false, emResult -> {

            Intent intent = new Intent(this, RegisterAndRecognizeActivity.class);
            intent.putExtra("flag", 0);
            intent.putExtra("name", emResult.data);

            startActivityForResult(intent, 0x01);
        })));
    }


    /**
     * 禁止EditText输入空格和换行符 限制长度18位
     *
     * @param editText EditText输入框
     */
    public void setEditTextInputSpace(EditText editText) {
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.equals(" ") || source.toString().contentEquals("\n")) {
                    return "";
                } else {
                    return null;
                }
            }
        };
        InputFilter filter_speChat = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
                String speChat = "[`~!@#_$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）— +|{}【】‘；：”“’。，、？]";
                Pattern pattern = Pattern.compile(speChat);
                Matcher matcher = pattern.matcher(charSequence.toString());
                if (matcher.find()) return "";
                else return null;
            }
        };
        editText.setFilters(new InputFilter[]{filter, filter_speChat, new InputFilter.LengthFilter(18)});
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0x01) {
            if (resultCode == RESULT_OK) {
                finish();
            }
        } else if (requestCode == REQUEST_CODE_CAMERA) {
            if (resultCode == RESULT_OK) {
                updateImage();
            }
        }
    }

    /**
     * 图片上传七牛云token
     */
    String qiniuToken;

    /**
     * 获取七牛云token
     */
    public void getQiniuToken() {
        rx.Observable<QiNiuToken> observable = ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .getToken()
                .subscribeOn(rx.schedulers.Schedulers.io())
                .filter(new HttpResultFunc<>())
                .subscribeOn(rx.schedulers.Schedulers.io())
                .observeOn(rx.android.schedulers.AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, false, false, qiNiuToken -> {
            if (qiNiuToken.getCode() == 1) {
                qiniuToken = qiNiuToken.qiNiu;
                if (qiniuToken == null) {
                    throw new IllegalArgumentException("token无效");
                }
            }
        })));
    }


    /**
     * 上传图片
     */
    public void updateImage() {
        if (TextUtils.isEmpty(qiniuToken)) {
            ToastUtil.showMessage(this, "配置获取错误，请稍后重试");
            return;
        }

        File file = new File(getFilesDir(), "pic.jpg");

        RequestBody photoRequestBody = RequestBody.create(MediaType.parse("image/jpg"), file);
        RequestBody tokenBody = RequestBody.create(MediaType.parse("multipart/form-data"), qiniuToken);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), photoRequestBody);

        rx.Observable<Pic> observable = ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .uploadPic(Config.HOST_UP_PIC, tokenBody, body)
                .subscribeOn(rx.schedulers.Schedulers.io())
                .observeOn(rx.android.schedulers.AndroidSchedulers.mainThread());
        mRxManager.add(observable.subscribe(new MySubscriber<>(this, true, false, pic -> {
            identifyIdCard(pic.hashCode);
        })));
    }

    /**
     * 识别身份证照片
     *
     * @param idCardPic
     */
    public void identifyIdCard(String idCardPic) {
        rx.Observable<IdCardResult> observable = ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .identifyIdCard(idCardPic)
                .filter(new HttpResultFunc<>())
                .subscribeOn(rx.schedulers.Schedulers.io())
                .observeOn(rx.android.schedulers.AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, true, false, emResult -> {
            if (emResult.data != null) {
                et_name.setText(emResult.data.name);
                et_idcard.setText(emResult.data.number);
            }
        })));
    }

}
