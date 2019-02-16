package com.easymin.custombus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.CustomSlideToUnlockView;
import com.easymi.component.widget.LoadingButton;
import com.easymin.custombus.R;
import com.easymin.custombus.adapter.PassengerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: PassengerActivity
 * @Author: hufeng
 * @Date: 2019/2/15 下午1:22
 * @Description:
 * @History:
 */
public class PassengerActivity extends RxBaseActivity{
    /**
     * 界面控件
     */
    CusToolbar cus_toolbar;
    TextView tv_station_name;
    TextView tv_ticket_status;
    TextView tv_countdown;
    TextView tv_countdown_hint;
    RecyclerView recyclerView;
    LinearLayout lin_bottom;
    TextView tv_no_check;
    LoadingButton btn_go_next;

    /**
     * 适配器
     */
    public PassengerAdapter adapter;

    /**
     *乘客数据源
     */
    public List<String> passList = new ArrayList<>();

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_passenger;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        findById();
        initAdapter();
        initListener();
    }

    @Override
    public void initToolBar() {
        super.initToolBar();
        cus_toolbar.setLeftIcon(R.drawable.ic_arrow_back, v -> finish());
        cus_toolbar.setTitle(R.string.cb_passenger_info);
    }

    /**
     * 加载控件
     */
    public void findById(){
        cus_toolbar = findViewById(R.id.cus_toolbar);
        tv_station_name = findViewById(R.id.tv_station_name);
        tv_ticket_status = findViewById(R.id.tv_ticket_status);
        tv_countdown = findViewById(R.id.tv_countdown);
        tv_countdown_hint = findViewById(R.id.tv_countdown_hint);
        recyclerView = findViewById(R.id.recyclerView);
        lin_bottom = findViewById(R.id.lin_bottom);
        tv_no_check = findViewById(R.id.tv_no_check);
        btn_go_next = findViewById(R.id.btn_go_next);
    }

    public void initAdapter(){
        adapter = new PassengerAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        for (int i = 0;i<10;i++){
            passList.add("");
        }
        adapter.setDatas(passList);
    }

    public void initListener(){
        tv_no_check.setOnClickListener(v -> {
            Intent intent = new Intent(this,CheckTicketActivity.class);
            startActivity(intent);
        });
    }
}
