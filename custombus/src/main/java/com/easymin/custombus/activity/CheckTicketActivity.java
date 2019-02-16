package com.easymin.custombus.activity;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.CustomSlideToUnlockView;
import com.easymi.component.widget.LoadingButton;
import com.easymin.custombus.R;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: CheckTicketActivity
 * @Author: hufeng
 * @Date: 2019/2/15 下午1:25
 * @Description:
 * @History:
 */
public class CheckTicketActivity extends RxBaseActivity{
    /**
     * 界面控件
     */
    EditText et_ticket;
    TextView tv_cancel;
    TextView tv_name;
    TextView tv_start;
    TextView tv_end;
    TextView tv_number;
    TextView tv_remark;
    LoadingButton btn_get_on;

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_check_ticket;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        findById();
        tv_cancel.setOnClickListener(v -> {
            finish();
        });

    }

    /**
     * 加载控件
     */
    public void findById(){
        et_ticket = findViewById(R.id.et_ticket);
        tv_cancel = findViewById(R.id.tv_cancel);
        tv_name = findViewById(R.id.tv_name);
        tv_start = findViewById(R.id.tv_start);
        tv_end = findViewById(R.id.tv_end);
        tv_number = findViewById(R.id.tv_number);
        tv_remark = findViewById(R.id.tv_remark);
        btn_get_on = findViewById(R.id.btn_get_on);
    }
}
