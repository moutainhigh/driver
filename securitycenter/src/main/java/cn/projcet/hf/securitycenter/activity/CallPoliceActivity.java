package cn.projcet.hf.securitycenter.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.projcet.hf.securitycenter.R;
import cn.projcet.hf.securitycenter.widget.CusToolbar;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: CallPoliceActivity
 * Author: shine
 * Date: 2018/11/28 下午1:52
 * Description:
 * History:
 */
public class CallPoliceActivity extends AppCompatActivity {

    private CusToolbar toolbar;
    private TextView tv_now_site;
    private LinearLayout lin_order_info;
    private ImageView iv_head;
    private TextView tv_name;
    private TextView tv_carInfo;
    private TextView tv_carNumber;
    private TextView tv_time;
    private TextView tv_book_site;
    private TextView tv_end_site;
    private TextView tv_call_police;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_police);
        initToolbar();
        initView();
    }

    public void initToolbar() {
        toolbar = findViewById(R.id.cus_toolbar);
        toolbar.setLeftIcon(R.mipmap.ic_back, view -> {
            finish();
        });
        toolbar.setTitle("一键报警");
        toolbar.setRightText("功能说明",v -> {

        });
    }

    public void initView() {
        tv_now_site = findViewById(R.id.tv_now_site);
        lin_order_info = findViewById(R.id.lin_order_info);
        iv_head = findViewById(R.id.iv_head);
        tv_name = findViewById(R.id.tv_name);
        tv_carInfo = findViewById(R.id.tv_carInfo);
        tv_carNumber = findViewById(R.id.tv_carNumber);
        tv_time = findViewById(R.id.tv_time);
        tv_book_site = findViewById(R.id.tv_book_site);
        tv_end_site = findViewById(R.id.tv_end_site);
        tv_call_police = findViewById(R.id.tv_call_police);
    }
}
