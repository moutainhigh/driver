package cn.projcet.hf.securitycenter.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.projcet.hf.securitycenter.R;
import cn.projcet.hf.securitycenter.widget.CusToolbar;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: LuyinActivity
 * Author: shine
 * Date: 2018/11/28 下午3:09
 * Description:
 * History:
 */
public class LuyinActivity extends AppCompatActivity{

    private CusToolbar toolbar;

    private LinearLayout lin_no_agree;
    private TextView tv_ready_agree;
    private TextView tv_xieyi;
    private TextView tv_agree;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_luyin);
        initToolbar();
        initView();
    }

    public void initToolbar() {
        toolbar = findViewById(R.id.cus_toolbar);
        toolbar.setLeftIcon(R.mipmap.ic_back, view -> {
            finish();
        });
        toolbar.setTitle("行程录音安全保护");
    }

    public void initView() {
        lin_no_agree = findViewById(R.id.lin_no_agree);
        tv_ready_agree = findViewById(R.id.tv_ready_agree);
        tv_xieyi = findViewById(R.id.tv_xieyi);
        tv_agree = findViewById(R.id.tv_agree);
    }
}
