package com.easymi.personal.activity.register;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.Employ;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.result.EmResult;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.RsaUtils;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.CusToolbar;
import com.easymi.personal.R;
import com.easymi.personal.result.RegisterResult;

import rx.Observable;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: RegisterNoticeActivity
 * Author: shine
 * Date: 2018/12/19 上午9:57
 * Description:
 * History:
 */
public class RegisterNoticeActivity extends RxBaseActivity {

    CusToolbar toolbar;
    ImageView iv_iamge;
    TextView tv_title;
    TextView tv_notice;
    TextView tv_amend;

    private int type; //注册状态：1.未注册；2.审核中；3驳回；4通过
//    private long driverId;
    private Employ employ;

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_register_notice;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        findById();
        type = getIntent().getIntExtra("type", 0);
//        driverId = getIntent().getLongExtra("driverId",0);
        if (type == 2) {
            iv_iamge.setImageResource(R.mipmap.ic_rg_passing);
            tv_title.setText(getResources().getString(R.string.register_checking));
            tv_notice.setText(getResources().getString(R.string.register_wait));

            tv_amend.setVisibility(View.GONE);
        } else if (type == 3) {
            iv_iamge.setImageResource(R.mipmap.ic_rg_no_pass);
            tv_title.setText(getResources().getString(R.string.register_no_pass));

            tv_amend.setVisibility(View.VISIBLE);

            employ = EmUtil.getEmployInfo();
            getDriverInfo();
        }

        tv_amend.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterBaseActivity.class);
            intent.putExtra("employ",employ);
            startActivity(intent);
        });
    }

    @Override
    public void initToolBar() {
        super.initToolBar();
        toolbar.setLeftIcon(R.drawable.ic_arrow_back, v -> finish());
        toolbar.setTitle(R.string.register_become);
    }

    public void findById() {
        toolbar = findViewById(R.id.toolbar);
        iv_iamge = findViewById(R.id.iv_iamge);
        tv_title = findViewById(R.id.tv_title);
        tv_notice = findViewById(R.id.tv_notice);
        tv_amend = findViewById(R.id.tv_amend);
    }


    public void getDriverInfo() {
        String id_rsa = RsaUtils.encryptAndEncode(this, employ.id+"");
        Observable<RegisterResult> observable = RegisterModel.getDriverInfo(id_rsa);
        mRxManager.add(observable.subscribe(new MySubscriber<>(this, false, false, emResult -> {
            if (emResult.getCode() == 1) {
                tv_notice.setText(emResult.data.remark);
            }
        })));
    }

}