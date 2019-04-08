package com.easymi.taxi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.CusToolbar;
import com.easymi.taxi.R;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: CancelActivity
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class CancelActivity extends RxBaseActivity {

    CheckBox checkBox1;
    CheckBox checkBox2;
    CheckBox checkBox3;
    CheckBox checkBox4;
    CheckBox checkBox5;

    EditText editReason;

    CusToolbar cusToolbar;

    private int checkId = 1;

    @Override
    public void initToolBar() {
        cusToolbar.setLeftBack(view -> finish());
        cusToolbar.setTitle(R.string.cancel_order);
    }

    @Override
    public boolean isEnableSwipe() {
        return true;
    }

    @Override
    public int getLayoutId() {
        return R.layout.taxi_activity_cancel_order;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        checkBox1 = findViewById(R.id.check_1);
        checkBox2 = findViewById(R.id.check_2);
        checkBox3 = findViewById(R.id.check_3);
        checkBox4 = findViewById(R.id.check_4);
        checkBox5 = findViewById(R.id.check_5);

        cusToolbar = findViewById(R.id.cus_toolbar);

        editReason = findViewById(R.id.edit_reason);

        checkBox1.setOnCheckedChangeListener(new MyCheckChange(1));
        checkBox2.setOnCheckedChangeListener(new MyCheckChange(2));
        checkBox3.setOnCheckedChangeListener(new MyCheckChange(3));
        checkBox4.setOnCheckedChangeListener(new MyCheckChange(4));
        checkBox5.setOnCheckedChangeListener(new MyCheckChange(5));
    }

    /**
     * 选择框监听
     */
    class MyCheckChange implements CheckBox.OnCheckedChangeListener {

        int id;

        public MyCheckChange(int id) {
            this.id = id;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                checkId = id;
                checkBox1.setChecked(false);
                checkBox2.setChecked(false);
                checkBox3.setChecked(false);
                checkBox4.setChecked(false);
                checkBox5.setChecked(false);

                buttonView.setChecked(true);

                if (checkId == 5) {
                    editReason.setVisibility(View.VISIBLE);
                } else {
                    editReason.setVisibility(View.GONE);
                }
            }
        }
    }

    public void cancel_cancel(View view) {
        finish();
    }

    /**
     * 取消订单
     * @param view
     */
    public void cancel_order(View view) {
        Intent intent = new Intent();
        String reason = "";
        if (checkId == 1) {
            reason = getString(R.string.cancel_reason_1);
        } else if (checkId == 2) {
            reason = getString(R.string.cancel_reason_2);
        } else if (checkId == 3) {
            reason = getString(R.string.cancel_reason_3);
        } else if (checkId == 4) {
            reason = getString(R.string.cancel_reason_4);
        } else if (checkId == 5) {
            reason = editReason.getText().toString();
        }
        if (StringUtils.isBlank(reason)) {
            ToastUtil.showMessage(CancelActivity.this, getString(R.string.please_cancel_reason));
            return;
        }
        intent.putExtra("reason", reason);
        setResult(RESULT_OK, intent);
        finish();
    }
}
