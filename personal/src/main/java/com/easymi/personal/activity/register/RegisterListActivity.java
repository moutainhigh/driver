package com.easymi.personal.activity.register;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.easymi.common.entity.BusinessList;
import com.easymi.common.entity.CompanyList;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.LoadingButton;
import com.easymi.personal.R;
import com.easymi.personal.adapter.RegisterTypeAdapter;
import com.easymi.personal.entity.BusinessType;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: RegisterListActivity
 * Author: shine
 * Date: 2018/12/19 上午9:58
 * Description:
 * History:
 */
public class RegisterListActivity extends RxBaseActivity{

    CusToolbar toolbar;
    RecyclerView recyclerView;
    LoadingButton button_sure;

    RegisterTypeAdapter adapter;

    private int type;  //1业务类型 2 机构类型

    private List<BusinessType> listType = new ArrayList<>();

    private List<CompanyList.Company> companies = new ArrayList<>();

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_register_list;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        findById();
        type = getIntent().getIntExtra("type",0);
        if (type == 1){

        }else if (type == 2){
            getCompany();
        }
        initAdapter();
    }

    @Override
    public void initToolBar() {
        super.initToolBar();
        toolbar.setLeftIcon(R.drawable.ic_arrow_back, v -> finish());
        if (getIntent().getIntExtra("type",0) == 1){
            toolbar.setTitle(R.string.register_type);
        }else if (getIntent().getIntExtra("type",0) == 2){
            toolbar.setTitle(R.string.register_compney);
        }
    }

    public void getData(){

    }

    public void findById() {
        toolbar = findViewById(R.id.toolbar);
        button_sure = findViewById(R.id.button_sure);
        recyclerView = findViewById(R.id.recyclerView);
    }

    public void initAdapter(){
        adapter = new RegisterTypeAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);
    }

    private void getCompany() {
        Observable<CompanyList> observable = RegisterModel.getCompanys();
        mRxManager.add(observable.subscribe(new MySubscriber<>(this, false, false, companyList -> {

        })));
    }

}
