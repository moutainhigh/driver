package com.easymi.personal.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.LoadingButton;
import com.easymi.personal.R;

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

            if (oldPsw.equals(newPsw)) {
                ToastUtil.showMessage(ChangeActivity.this, getString(R.string.same_old_new));
            } else if (!newPsw.equals(confirmPsw)) {
                ToastUtil.showMessage(ChangeActivity.this, getString(R.string.different_new_confirm));
            } else {
                btn.setClickable(false);
                btn.setStatus(LoadingButton.STATUS_LOADING);
            }
        });
    }

    @Override
    public void initToolBar() {

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
}
