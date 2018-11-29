package cn.projcet.hf.securitycenter.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;

import cn.projcet.hf.securitycenter.R;
import cn.projcet.hf.securitycenter.widget.CusToolbar;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: AddEmergeActivity
 * Author: shine
 * Date: 2018/11/28 下午1:36
 * Description:
 * History:
 */
public class AddEmergeActivity extends AppCompatActivity{

    private CusToolbar toolbar;
    private EditText et_phone;
    private TextView tv_tongxunlu;
    private EditText et_name;
    private TextView tv_zengjia;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_emerge);
        initToolbar();
        initView();
    }

    public void initToolbar(){
        toolbar = findViewById(R.id.cus_toolbar);
        toolbar.setLeftIcon(R.mipmap.ic_back, view -> {
            finish();
        });
        toolbar.setTitle("紧急联系人");
    }

    public void initView(){
          et_phone = findViewById(R.id.et_phone);
          tv_tongxunlu = findViewById(R.id.tv_tongxunlu);
          et_name = findViewById(R.id.et_name);
          tv_zengjia = findViewById(R.id.tv_zengjia);
    }
}
