package com.easymi.personal.activity.register;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.easymi.common.entity.BusinessList;
import com.easymi.common.entity.CompanyList;
import com.easymi.component.Config;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.result.EmResult;
import com.easymi.component.utils.AlexStatusBarUtils;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.utils.UIStatusBarHelper;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.LoadingButton;
import com.easymi.personal.R;
import com.easymi.personal.adapter.RegisterTypeAdapter;
import com.easymi.personal.entity.BusinessType;
import com.easymi.personal.result.BusinessResult;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: RegisterListActivity
 * @Author: hufeng
 * Date: 2018/12/19 上午9:58
 * Description:
 * History:
 */
public class RegisterListActivity extends RxBaseActivity{

    CusToolbar toolbar;
    RecyclerView recyclerView;
    LoadingButton button_sure;

    RegisterTypeAdapter adapter;

    /**
     * 1业务类型 2 机构类型
     */
    private int type;

    /**
     * 公司id
     */
    private long id;

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
//        UIStatusBarHelper.setStatusBarLightMode(this);
//        AlexStatusBarUtils.setStatusColor(this, Color.WHITE);
        return R.layout.activity_register_list;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        findById();
        type = getIntent().getIntExtra("type",0);
        initAdapter();

        if (type == 1){
            id = getIntent().getLongExtra("id",0);
            getBusinessType(id);
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

    /**
     * 获取业务类型
     */
    public void getBusinessType(long id){
        Observable<BusinessResult> observable = RegisterModel.getBusinessType(id);
        mRxManager.add(observable.subscribe(new MySubscriber<>(this, false, false, emResult -> {
            if (emResult.getCode() == 1){
                listType = emResult.data;
                adapter.setList(listType);
            }
        })));
    }

    /**
     * 初始化控件
     */
    public void findById() {
        toolbar = findViewById(R.id.toolbar);
        button_sure = findViewById(R.id.button_sure);
        recyclerView = findViewById(R.id.recyclerView);
    }

    /**
     * 初始化适配器
     */
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

    /**
     * 获取服务机构数据
     */
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

    /**
     * 返回上个界面数据
     */
    public void setResult(){
        Intent intent = new Intent();
        intent.putExtra("selectType",selectType);
        intent.putExtra("selectComPany",selectComPany);
        setResult(RESULT_OK,intent);
        finish();
    }

}
