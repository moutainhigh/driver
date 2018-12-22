package com.easymi.personal.activity.register;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.easymi.common.entity.BusinessList;
import com.easymi.common.entity.CompanyList;
import com.easymi.component.Config;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.utils.ToastUtil;
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

    private BusinessType selectType;
    private CompanyList.Company selectComPany;

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
        initAdapter();

        if (type == 1){
            getData();
            adapter.setList(listType);
        }else if (type == 2){
            getCompany();
        }

        button_sure.setOnClickListener(v -> {
            if (type == 1){
                if (selectType != null){
                    setResult();
                }else {
                    ToastUtil.showMessage(this,"请选择业务类型");
                }
            }else if (type == 2){
                if (selectComPany != null){
                    setResult();
                }else {
                    ToastUtil.showMessage(this,"请选择服务机构");
                }
            }
        });
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
        listType.clear();
        BusinessType businessType = new BusinessType();
        businessType.name = "专车";
        businessType.type = Config.ZHUANCHE;
        BusinessType businessType1 = new BusinessType();
        businessType1.name = "城际专线";
        businessType1.type = Config.CITY_LINE;
        BusinessType businessType2 = new BusinessType();
        businessType2.name = "出租车";
        businessType2.type =  Config.TAXI;
        listType.add(businessType);
        listType.add(businessType2);
        listType.add(businessType1);
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
        adapter.setItemClickListener(position -> {
            if (type == 1){
                adapter.setSelect(position);
                selectType = listType.get(position);
            }else if (type == 2){
                adapter.setSelect(position);
                selectComPany = companies.get(position);
            }
        });
    }

    private void getCompany() {
        Observable<CompanyList> observable = RegisterModel.getCompanys();
        mRxManager.add(observable.subscribe(new MySubscriber<>(this, false, false, companyList -> {
            if (companyList.getCode() == 1){
                companies.clear();
                companies.addAll(companyList.companies);
                adapter.setList(companies);
            }
        })));
    }

    public void setResult(){
        Intent intent = new Intent();
        intent.putExtra("selectType",selectType);
        intent.putExtra("selectComPany",selectComPany);
        setResult(RESULT_OK,intent);
        finish();
    }

}
