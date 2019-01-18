package com.easymin.driver.securitycenter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easymin.driver.securitycenter.CenterConfig;
import com.easymin.driver.securitycenter.ComService;
import com.easymin.driver.securitycenter.R;
import com.easymin.driver.securitycenter.network.ApiManager;
import com.easymin.driver.securitycenter.network.HttpResultFunc;
import com.easymin.driver.securitycenter.network.MySubscriber;
import com.easymin.driver.securitycenter.result.EmResult;
import com.easymin.driver.securitycenter.rxmvp.RxManager;
import com.easymin.driver.securitycenter.widget.CusToolbar;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: LuyinActivity
 *@Author: shine
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
    private LinearLayout lin_bg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_luyin);
        initToolbar();
        initView();
        initData();
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
        lin_bg = findViewById(R.id.lin_bg);
    }

    public void initData(){
        if (getIntent().getIntExtra("soundRecordCheck",0) == 0){
            lin_no_agree.setVisibility(View.VISIBLE);
            tv_ready_agree.setVisibility(View.GONE);
            lin_bg.setBackgroundResource(R.color.color_f2f2f2);
            tv_agree.setVisibility(View.VISIBLE);
        }else {
            lin_no_agree.setVisibility(View.GONE);
            tv_ready_agree.setVisibility(View.VISIBLE);
            lin_bg.setBackgroundResource(R.mipmap.voice_bg);
            tv_agree.setVisibility(View.GONE);
        }

        tv_agree.setOnClickListener(v -> {
            luyinAuthorize();
        });
        tv_xieyi.setOnClickListener(v -> {
            Intent intent = new Intent(this, WebActivity.class);
            intent.putExtra("url", "http://h5.xiaokakj.com/#/protocol?articleName=passengerSafetyRecord&appKey="+CenterConfig.APPKEY);
            intent.putExtra("title", "录音保护协议");
            startActivity(intent);
        });
    }

    public void luyinAuthorize(){
        Observable<EmResult> observable = ApiManager.getInstance().createApi(CenterConfig.HOST, ComService.class)
                .authorize(CenterConfig.PASSENGERID,CenterConfig.APPKEY)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        new RxManager().add(observable.subscribe(new MySubscriber<>(this, true,
                true, emResult -> {
                if (emResult.getCode() == 1){
                    lin_no_agree.setVisibility(View.GONE);
                    tv_ready_agree.setVisibility(View.VISIBLE);
                    lin_bg.setBackgroundResource(R.mipmap.voice_bg);
                    tv_agree.setVisibility(View.GONE);
                }else {
                    Toast.makeText(this,emResult.getMessage(),Toast.LENGTH_LONG);
                }
        })));
    }
}
