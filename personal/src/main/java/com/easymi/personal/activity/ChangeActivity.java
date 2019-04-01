package com.easymi.personal.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.easymi.component.Config;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.network.NoErrSubscriberListener;
import com.easymi.component.result.EmResult;
import com.easymi.component.utils.AesUtil;
import com.easymi.component.utils.CsEditor;
import com.easymi.component.utils.CsSharedPreferences;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.PhoneUtil;
import com.easymi.component.utils.SHA256Util;
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
 * FileName: ChangeActivity
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description: 修改密码界面
 * History:
 */

public class ChangeActivity extends RxBaseActivity {

    /**
     * 旧密码
     */
    EditText editOld;
    /**
     * 新密码
     */
    EditText editNew;
    /**
     * 确认新密码
     */
    EditText editConfirm;

    /**
     * 修改按钮
     */
    LoadingButton btn;

    @Override
    public int getLayoutId() {
        return R.layout.activity_change_psw;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        editOld = findViewById(R.id.edit_old);
        editNew = findViewById(R.id.edit_new);
        editConfirm = findViewById(R.id.confirm_new);
        btn = findViewById(R.id.apply);

        editOld.addTextChangedListener(new MyTextWatcher());
        editNew.addTextChangedListener(new MyTextWatcher());
        editConfirm.addTextChangedListener(new MyTextWatcher());

        btn.setOnClickListener(view -> {
            String oldPsw = editOld.getText().toString();
            String newPsw = editNew.getText().toString();
            String confirmPsw = editConfirm.getText().toString();

            String oldPswSave = new CsSharedPreferences().getString(Config.SP_LOGIN_PSW, "");
            if (!oldPsw.equals(oldPswSave)) {
                ToastUtil.showMessage(ChangeActivity.this, getString(R.string.not_same_old));
                return;
            } else if (oldPsw.equals(newPsw)) {
                ToastUtil.showMessage(ChangeActivity.this, getString(R.string.same_old_new));
                return;
            } else if (!newPsw.equals(confirmPsw)) {
                ToastUtil.showMessage(ChangeActivity.this, getString(R.string.different_new_confirm));
                return;
            }
            PhoneUtil.hideKeyboard(this);
            changePsw();
        });
    }

    /**
     * 修改密码接口
     */
    private void changePsw() {
        McService mcService = ApiManager.getInstance().createApi(Config.HOST, McService.class);

        Observable<EmResult> observable = mcService
                .updatePsw(SHA256Util.getSHA256StrJava(editNew.getText().toString()),
                        SHA256Util.getSHA256StrJava(editOld.getText().toString()),
                        EmUtil.getEmployInfo().realName)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, btn, emResult -> {
            ToastUtil.showMessage(ChangeActivity.this, getString(R.string.change_psw_suc));
            EmUtil.employLogout(ChangeActivity.this);
        })));
    }

    CusToolbar cusToolbar;

    @Override
    public void initToolBar() {
        cusToolbar = findViewById(R.id.cus_toolbar);
        cusToolbar.setLeftIcon(R.drawable.ic_arrow_back, view -> onBackPressed());
        cusToolbar.setTitle(R.string.set_change_psw);
    }

    /**
     * edittext监听
     */
    class MyTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (StringUtils.isNotBlank(editOld.getText().toString())
                    && StringUtils.isNotBlank(editNew.getText().toString())
                    && StringUtils.isNotBlank(editConfirm.getText().toString())) {
                btn.setEnabled(true);
            } else {
                btn.setEnabled(false);
            }
        }
    }

    @Override
    public boolean isEnableSwipe() {
        return true;
    }
}
