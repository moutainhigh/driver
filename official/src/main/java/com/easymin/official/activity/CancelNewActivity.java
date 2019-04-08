package com.easymin.official.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.CusToolbar;
import com.easymin.official.R;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: CancelNewActivity
 * @Author: hufeng
 * @Date: 2019/3/26 下午3:04
 * @Description:
 * @History:
 */
public class CancelNewActivity extends RxBaseActivity implements View.OnClickListener {

    CusToolbar cusToolbar;
    Button btn_1;
    Button btn_2;
    Button btn_3;
    Button btn_4;
    Button btn_5;
    Button btn_6;
    EditText edit_reason;
    Button cancel_cancel;
    Button cancel_apply;
    TextView tv_two;

    @Override
    public void initToolBar() {
        cusToolbar.setLeftBack(view -> finish());
        cusToolbar.setTitle(R.string.cancel_order);
    }

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_cancel_new;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        cusToolbar = findViewById(R.id.cus_toolbar);
        btn_1 = findViewById(R.id.btn_1);
        btn_2 = findViewById(R.id.btn_2);
        btn_3 = findViewById(R.id.btn_3);
        btn_4 = findViewById(R.id.btn_4);
        btn_5 = findViewById(R.id.btn_5);
        btn_6 = findViewById(R.id.btn_6);
        edit_reason = findViewById(R.id.edit_reason);
        cancel_cancel = findViewById(R.id.cancel_cancel);
        cancel_apply = findViewById(R.id.cancel_apply);
        tv_two = findViewById(R.id.tv_two);

        initListener();
    }

    public void initListener() {
        btn_1.setOnClickListener(this);
        btn_2.setOnClickListener(this);
        btn_3.setOnClickListener(this);
        btn_4.setOnClickListener(this);
        btn_5.setOnClickListener(this);
        btn_6.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        edit_reason.setVisibility(View.GONE);
        int i = v.getId();
        if (i == R.id.btn_1) {
            checkId = 1;
            btn_1.setBackgroundResource(R.color.color_3c98e3);
            btn_2.setBackgroundResource(R.color.color_f2f2f2);
            btn_3.setBackgroundResource(R.color.color_f2f2f2);
            btn_4.setBackgroundResource(R.color.color_f2f2f2);
            btn_5.setBackgroundResource(R.color.color_f2f2f2);
            btn_6.setBackgroundResource(R.color.color_f2f2f2);

            btn_1.setTextColor(getResources().getColor(R.color.white));
            btn_2.setTextColor(getResources().getColor(R.color.color_333333));
            btn_3.setTextColor(getResources().getColor(R.color.color_333333));
            btn_4.setTextColor(getResources().getColor(R.color.color_333333));
            btn_5.setTextColor(getResources().getColor(R.color.color_333333));
            btn_6.setTextColor(getResources().getColor(R.color.color_333333));

        } else if (i == R.id.btn_2) {
            checkId = 2;
            btn_1.setBackgroundResource(R.color.color_f2f2f2);
            btn_2.setBackgroundResource(R.color.color_3c98e3);
            btn_3.setBackgroundResource(R.color.color_f2f2f2);
            btn_4.setBackgroundResource(R.color.color_f2f2f2);
            btn_5.setBackgroundResource(R.color.color_f2f2f2);
            btn_6.setBackgroundResource(R.color.color_f2f2f2);

            btn_1.setTextColor(getResources().getColor(R.color.color_333333));
            btn_2.setTextColor(getResources().getColor(R.color.white));
            btn_3.setTextColor(getResources().getColor(R.color.color_333333));
            btn_4.setTextColor(getResources().getColor(R.color.color_333333));
            btn_5.setTextColor(getResources().getColor(R.color.color_333333));
            btn_6.setTextColor(getResources().getColor(R.color.color_333333));
        } else if (i == R.id.btn_3) {
            checkId = 3;
            btn_1.setBackgroundResource(R.color.color_f2f2f2);
            btn_2.setBackgroundResource(R.color.color_f2f2f2);
            btn_3.setBackgroundResource(R.color.color_3c98e3);
            btn_4.setBackgroundResource(R.color.color_f2f2f2);
            btn_5.setBackgroundResource(R.color.color_f2f2f2);
            btn_6.setBackgroundResource(R.color.color_f2f2f2);

            btn_1.setTextColor(getResources().getColor(R.color.color_333333));
            btn_2.setTextColor(getResources().getColor(R.color.color_333333));
            btn_3.setTextColor(getResources().getColor(R.color.white));
            btn_4.setTextColor(getResources().getColor(R.color.color_333333));
            btn_5.setTextColor(getResources().getColor(R.color.color_333333));
            btn_6.setTextColor(getResources().getColor(R.color.color_333333));
        } else if (i == R.id.btn_4) {
            checkId = 4;
            btn_1.setBackgroundResource(R.color.color_f2f2f2);
            btn_2.setBackgroundResource(R.color.color_f2f2f2);
            btn_3.setBackgroundResource(R.color.color_f2f2f2);
            btn_4.setBackgroundResource(R.color.color_3c98e3);
            btn_5.setBackgroundResource(R.color.color_f2f2f2);
            btn_6.setBackgroundResource(R.color.color_f2f2f2);

            btn_1.setTextColor(getResources().getColor(R.color.color_333333));
            btn_2.setTextColor(getResources().getColor(R.color.color_333333));
            btn_3.setTextColor(getResources().getColor(R.color.color_333333));
            btn_4.setTextColor(getResources().getColor(R.color.white));
            btn_5.setTextColor(getResources().getColor(R.color.color_333333));
            btn_6.setTextColor(getResources().getColor(R.color.color_333333));
        } else if (i == R.id.btn_5) {
            checkId = 5;
            btn_1.setBackgroundResource(R.color.color_f2f2f2);
            btn_2.setBackgroundResource(R.color.color_f2f2f2);
            btn_3.setBackgroundResource(R.color.color_f2f2f2);
            btn_4.setBackgroundResource(R.color.color_f2f2f2);
            btn_5.setBackgroundResource(R.color.color_3c98e3);
            btn_6.setBackgroundResource(R.color.color_f2f2f2);

            btn_1.setTextColor(getResources().getColor(R.color.color_333333));
            btn_2.setTextColor(getResources().getColor(R.color.color_333333));
            btn_3.setTextColor(getResources().getColor(R.color.color_333333));
            btn_4.setTextColor(getResources().getColor(R.color.color_333333));
            btn_5.setTextColor(getResources().getColor(R.color.white));
            btn_6.setTextColor(getResources().getColor(R.color.color_333333));
        } else if (i == R.id.btn_6) {
            checkId = 6;
            btn_1.setBackgroundResource(R.color.color_f2f2f2);
            btn_2.setBackgroundResource(R.color.color_f2f2f2);
            btn_3.setBackgroundResource(R.color.color_f2f2f2);
            btn_4.setBackgroundResource(R.color.color_f2f2f2);
            btn_5.setBackgroundResource(R.color.color_f2f2f2);
            btn_6.setBackgroundResource(R.color.color_3c98e3);

            btn_1.setTextColor(getResources().getColor(R.color.color_333333));
            btn_2.setTextColor(getResources().getColor(R.color.color_333333));
            btn_3.setTextColor(getResources().getColor(R.color.color_333333));
            btn_4.setTextColor(getResources().getColor(R.color.color_333333));
            btn_5.setTextColor(getResources().getColor(R.color.color_333333));
            btn_6.setTextColor(getResources().getColor(R.color.white));

            edit_reason.setVisibility(View.VISIBLE);
        }
    }

    public void cancel_cancel(View view) {
        finish();
    }

    private int checkId = 1;

    /**
     * 回传拒绝理由
     *
     * @param view
     */
    public void cancel_order(View view) {
        Intent intent = new Intent();
        String reason = "";
        if (checkId == 1) {
            reason = "车辆故障";
        } else if (checkId == 2) {
            reason = "联系不上客户";
        } else if (checkId == 3) {
            reason = "等待时间过长";
        } else if (checkId == 4) {
            reason = "交通故障";
        } else if (checkId == 5) {
            reason = "距离太远";
        } else if (checkId == 6) {
            reason = edit_reason.getText().toString();
        }

        if (StringUtils.isBlank(reason)) {
            ToastUtil.showMessage(this, getString(R.string.please_cancel_reason));
            return;
        }
        intent.putExtra("reason", reason);
        setResult(RESULT_OK, intent);
        finish();
    }
}