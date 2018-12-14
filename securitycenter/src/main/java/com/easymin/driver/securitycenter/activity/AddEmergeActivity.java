package com.easymin.driver.securitycenter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.easymin.driver.securitycenter.CenterConfig;
import com.easymin.driver.securitycenter.ComService;
import com.easymin.driver.securitycenter.R;
import com.easymin.driver.securitycenter.entity.Contact;
import com.easymin.driver.securitycenter.network.ApiManager;
import com.easymin.driver.securitycenter.network.HttpResultFunc;
import com.easymin.driver.securitycenter.network.MySubscriber;
import com.easymin.driver.securitycenter.result.EmResult;
import com.easymin.driver.securitycenter.rxmvp.RxManager;
import com.easymin.driver.securitycenter.utils.PhoneUtil;
import com.easymin.driver.securitycenter.utils.ToastUtil;
import com.easymin.driver.securitycenter.widget.CusToolbar;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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

    private Contact contact;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_emerge);
        initToolbar();
        initView();
        initData();
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

    public void initData(){

        int type = getIntent().getIntExtra("type",0);
        if (type == 0){
            tv_zengjia.setText("确认新增");
        }else {
            tv_zengjia.setText("确认修改");
            contact = (Contact) getIntent().getSerializableExtra("contact");
            et_name.setText(contact.emerg_name);
            et_phone.setText(contact.emerg_phone);
        }
        tv_zengjia.setOnClickListener(v -> {
            if (type == 0){
                firstAdd();
            }else {
                amentContact();
            }
        });
        tv_tongxunlu.setOnClickListener(v -> {
            PhoneUtil.getContacts(this, 0X00);
        });
    }

    //第一次添加紧急联系人
    public void firstAdd() {
        Observable<EmResult> observable = ApiManager.getInstance().createApi(CenterConfig.HOST, ComService.class)
                .tripEmergeContact(et_name.getText().toString(), et_phone.getText().toString(),
                        CenterConfig.PASSENGERID, CenterConfig.PASSENGERPHONE, CenterConfig.APPKEY)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        new RxManager().add(observable.subscribe(new MySubscriber<>(this, true,
                true, emResult -> {
            if (emResult.getCode() == 1){
                setResult(RESULT_OK);
                finish();
            }else {
                ToastUtil.showMessage(this,emResult.getMessage());
            }
        })));
    }

    //修改联系人
    public void amentContact(){
        Observable<EmResult> observable = ApiManager.getInstance().createApi(CenterConfig.HOST, ComService.class)
                .amentEmerge(et_name.getText().toString(), et_phone.getText().toString(),contact.id,
                        CenterConfig.PASSENGERID, CenterConfig.PASSENGERPHONE, CenterConfig.APPKEY)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        new RxManager().add(observable.subscribe(new MySubscriber<>(this, true,
                true, emResult -> {
                if (emResult.getCode() == 1){
                    setResult(RESULT_OK);
                    finish();
                }else {
                    ToastUtil.showMessage(this,emResult.getMessage());
                }
        })));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == 0X00){
                PhoneUtil.UserPhone userPhone = PhoneUtil.handleResult(this, resultCode, data);
                et_name.setText(userPhone.name);
                et_phone.setText(userPhone.phoneNo);
            }
        }
    }

}
