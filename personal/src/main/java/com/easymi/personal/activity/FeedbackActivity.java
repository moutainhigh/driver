package com.easymi.personal.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.easymi.component.Config;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.result.EmResult;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.LoadingButton;
import com.easymi.personal.McService;
import com.easymi.personal.R;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: FinishActivity
 * Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class FeedbackActivity extends RxBaseActivity {

    EditText editText;

    LoadingButton btn;

    @Override
    public int getLayoutId() {
        return R.layout.activity_feedback;
    }

    @Override
    public void initToolBar() {
        CusToolbar cusToolbar = findViewById(R.id.cus_toolbar);
        cusToolbar.setLeftIcon(R.drawable.ic_arrow_back, view -> onBackPressed());
        cusToolbar.setTitle(R.string.set_feedback);
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        editText = findViewById(R.id.edit_text);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String s = editable.toString();
                if (StringUtils.isBlank(s)) {
                    btn.setEnabled(false);
                } else {
                    btn.setEnabled(true);
                }
            }
        });

        btn = findViewById(R.id.apply);
        btn.setOnClickListener(view -> {
            apply();
        });
    }

    public void apply() {
        McService api = ApiManager.getInstance().createApi(Config.HOST, McService.class);

        Observable<EmResult> observable = api
                .feedback(EmUtil.getEmployId(),
                        EmUtil.getEmployInfo().userName,
                        EmUtil.getEmployInfo().company_id,
                        EmUtil.getAppKey(),
                        editText.getText().toString(),
                        2,
                        EmUtil.getEmployInfo().phone)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, btn, emResult -> {
            ToastUtil.showMessage(FeedbackActivity.this, getString(R.string.feedback_suc));
            finish();
        })));
    }

    @Override
    public boolean isEnableSwipe() {
        return true;
    }
}
