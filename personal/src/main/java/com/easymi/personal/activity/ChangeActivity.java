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
 * Created by developerLzh on 2017/11/11 0011.
 */

public class ChangeActivity extends RxBaseActivity {

    EditText editOld;
    EditText editNew;
    EditText editConfirm;

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

            String oldPswSave = AesUtil.aesDecrypt(XApp.getMyPreferences().getString(Config.SP_LOGIN_PSW, ""), AesUtil.AAAAA);

//            if (DymOrder.findAll().size() != 0) {
//                ToastUtil.showMessage(ChangeActivity.this, getString(R.string.be_busy));
//                return;
//            }
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
//            boolean canRemember = XApp.getMyPreferences().getBoolean(Config.SP_REMEMBER_PSW, true);
//            if (canRemember) {
//                XApp.getPreferencesEditor().putString(Config.SP_LOGIN_PSW,
//                        AesUtil.aesEncrypt(editNew.getText().toString(), AesUtil.AAAAA)).apply();
//            }
//            finish();
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
