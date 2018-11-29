package cn.projcet.hf.securitycenter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.swipe.util.Attributes;

import java.util.ArrayList;
import java.util.Arrays;

import cn.projcet.hf.securitycenter.R;
import cn.projcet.hf.securitycenter.adapter.RecyclerViewAdapter;
import cn.projcet.hf.securitycenter.dialog.TimeDialog;
import cn.projcet.hf.securitycenter.widget.CusToolbar;
import cn.projcet.hf.securitycenter.widget.SwitchButton;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: EmergeActivity
 * Author: shine
 * Date: 2018/11/26 下午6:47
 * Description:
 * History:
 */
public class EmergeActivity extends AppCompatActivity{

    private RecyclerView.Adapter mAdapter;
    private ArrayList<String> mDataSet;

    private RecyclerView recyclerView;
    private TextView tv_hint;
    private TextView tv_notice;
    private LinearLayout lin_time;
    private SwitchButton share_able_btn;
    private TextView tv_time;
    private TextView tv_lianxiren_hint;
    private EditText et_phone;
    private TextView tv_tongxunlu;
    private EditText et_name;
    private TextView tv_zengjia;
    private LinearLayout lin_list;
    private TextView tv_add;

    private CusToolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emerge);

        initView();
        initToolbar();
        initAdapter();
        initListener();
    }

    public void initToolbar(){
        toolbar = findViewById(R.id.cus_toolbar);
        toolbar.setLeftIcon(R.mipmap.ic_back, view -> {
            finish();
        });
        toolbar.setTitle("紧急联系人");
    }

    public void initView(){
        recyclerView = findViewById(R.id.recycler);
        tv_hint = findViewById(R.id.tv_hint);
        tv_notice = findViewById(R.id.tv_notice);
        lin_time = findViewById(R.id.lin_time);
        share_able_btn = findViewById(R.id.share_able_btn);
        tv_time = findViewById(R.id.tv_time);
        tv_lianxiren_hint = findViewById(R.id.tv_lianxiren_hint);
        et_phone = findViewById(R.id.et_phone);
        tv_tongxunlu = findViewById(R.id.tv_tongxunlu);
        et_name = findViewById(R.id.et_name);
        tv_zengjia = findViewById(R.id.tv_zengjia);
        lin_list = findViewById(R.id.lin_list);
        tv_add = findViewById(R.id.tv_add);

    }

    public void initAdapter(){
        // Layout Managers:
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        // Item Decorator:
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        // Adapter:
        String[] adapterData = new String[]{"Alabama", "Alaska", "Arizona", "Arkansas", "California", "Colorado", "Connecticut", "Delaware", "Florida", "Georgia", "Hawaii", "Idaho", "Illinois", "Indiana", "Iowa", "Kansas", "Kentucky", "Louisiana", "Maine", "Maryland", "Massachusetts", "Michigan", "Minnesota", "Mississippi", "Missouri", "Montana", "Nebraska", "Nevada", "New Hampshire", "New Jersey", "New Mexico", "New York", "North Carolina", "North Dakota", "Ohio", "Oklahoma", "Oregon", "Pennsylvania", "Rhode Island", "South Carolina", "South Dakota", "Tennessee", "Texas", "Utah", "Vermont", "Virginia", "Washington", "West Virginia", "Wisconsin", "Wyoming"};
        mDataSet = new ArrayList<>(Arrays.asList(adapterData));
        mAdapter = new RecyclerViewAdapter(this, mDataSet);
        ((RecyclerViewAdapter) mAdapter).setMode(Attributes.Mode.Single);
        recyclerView.setAdapter(mAdapter);
    }

    public void initListener(){
        tv_add.setOnClickListener(v -> {
            Intent intent = new Intent(this, EmergeActivity.class);
            startActivity(intent);
        });
        tv_time.setOnClickListener(v -> {
            TimeDialog timeDialog = new TimeDialog(this);
            timeDialog.show();
        });
    }
}
