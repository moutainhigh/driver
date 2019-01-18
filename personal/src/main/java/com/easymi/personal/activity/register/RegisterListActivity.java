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

    /**
     * 初始化注册业务类型
     */
    public void getData(){
        listType.clear();
        if (Config.KT_ZHUANCHE){
            BusinessType businessType = new BusinessType();
            businessType.name = getResources().getString(com.easymi.common.R.string.create_zhuanche);
            businessType.type = Config.ZHUANCHE;
            listType.add(businessType);
        }
        if (Config.KT_ZHUANXIAN){
            BusinessType businessType1 = new BusinessType();
            businessType1.name = getResources().getString(com.easymi.common.R.string.create_zhuanxian);
            businessType1.type = Config.CITY_LINE;
            listType.add(businessType1);
        }
        if (Config.KT_CHUZUCHE){
            BusinessType businessType2 = new BusinessType();
            businessType2.name = getResources().getString(com.easymi.common.R.string.create_taxi);
            businessType2.type =  Config.TAXI;
            listType.add(businessType2);
        }
        if (Config.KT_BAOCHE){
            BusinessType businessType3 = new BusinessType();
            businessType3.name = getResources().getString(com.easymi.common.R.string.create_chartered);
            businessType3.type = Config.CHARTERED;
            listType.add(businessType3);
        }
        if (Config.KT_ZUCHE){
            BusinessType businessType4 = new BusinessType();
            businessType4.name = getResources().getString(com.easymi.common.R.string.create_rental);
            businessType4.type = Config.RENTAL;
            listType.add(businessType4);
        }
        if (Config.KT_BANCHE){
            BusinessType businessType5 = new BusinessType();
            businessType5.name = getResources().getString(com.easymi.common.R.string.create_bus_country);
            businessType5.type =  Config.COUNTRY;
            listType.add(businessType5);
        }
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
