package com.easymi.personal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.easymi.common.CommApiService;
import com.easymi.common.result.LoginResult;
import com.easymi.component.Config;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.Employ;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.network.NoErrSubscriberListener;
import com.easymi.component.result.EmResult;
import com.easymi.component.utils.CommonUtil;
import com.easymi.component.utils.CsEditor;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.ToastUtil;
import com.easymi.personal.McService;
import com.easymi.personal.R;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MyPopularizeCountOnActivity extends RxBaseActivity implements View.OnClickListener {

    private com.easymi.component.widget.CusToolbar myPopularizeCountOnCtb;
    private android.widget.EditText myPopularizeCountOnEt;
    private android.widget.TextView myPopularizeCountOnTvCurrent;
    private android.widget.TextView myPopularizeCountOnTvAll;
    private android.widget.TextView myPopularizeCountOnTvCountApply;
    private android.widget.TextView myPopularizeCountOnTvRecord;
    private Employ employ;

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_my_popularize_count_on;
    }

    @Override
    public void initToolBar() {
        super.initToolBar();
        myPopularizeCountOnCtb = findViewById(R.id.myPopularizeCountOnCtb);
        myPopularizeCountOnCtb.setTitle("结算").setLeftIcon(R.drawable.ic_arrow_back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        myPopularizeCountOnEt = findViewById(R.id.myPopularizeCountOnEt);
        myPopularizeCountOnTvCurrent = findViewById(R.id.myPopularizeCountOnTvCurrent);
        myPopularizeCountOnTvAll = findViewById(R.id.myPopularizeCountOnTvAll);
        myPopularizeCountOnTvAll.setOnClickListener(this);
        myPopularizeCountOnTvCountApply = findViewById(R.id.myPopularizeCountOnTvCountApply);
        myPopularizeCountOnTvCountApply.setOnClickListener(this);
        myPopularizeCountOnTvRecord = findViewById(R.id.myPopularizeCountOnTvRecord);
        myPopularizeCountOnTvRecord.setOnClickListener(this);
        myPopularizeCountOnEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setText(s);
            }
        });
    }

    private void setText(Editable s) {
        if (employ != null) {
            double current = 0;
            if (!TextUtils.isEmpty(s)) {
                current = Double.parseDouble(s.toString());
            }
            if (current > employ.balance) {
                myPopularizeCountOnTvAll.setVisibility(View.GONE);
                myPopularizeCountOnTvCurrent.setTextColor(ContextCompat.getColor(MyPopularizeCountOnActivity.this, R.color.colorRed));
                myPopularizeCountOnTvCurrent.setText("输入金额超过剩余金额");
            } else {
                myPopularizeCountOnTvAll.setVisibility(View.VISIBLE);
                myPopularizeCountOnTvCurrent.setTextColor(ContextCompat.getColor(MyPopularizeCountOnActivity.this, R.color.colorBlack));
                myPopularizeCountOnTvCurrent.setText("当前剩余 ¥" + CommonUtil.d2s(employ.balance, "0.00"));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDriverInfo();
    }

    private void getDriverInfo() {
        Observable<LoginResult> observable = ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .getDriverInfo(EmUtil.getEmployId(), EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, true, false, loginResult -> {
            employ = loginResult.data;
            employ.saveOrUpdate();
            CsEditor editor = XApp.getEditor();
            editor.putLong(Config.SP_DRIVERID, employ.id);
            editor.apply();

            setText(myPopularizeCountOnEt.getText());
        })));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.myPopularizeCountOnTvAll) {
            if (employ != null) {
                String content = CommonUtil.d2s(employ.balance, "0.00");
                myPopularizeCountOnEt.setText(content);
                if (content.length() <= 6) {
                    myPopularizeCountOnEt.setSelection(content.length());
                }
            }
        } else if (v.getId() == R.id.myPopularizeCountOnTvCountApply) {
            promoteApply();
        } else if (v.getId() == R.id.myPopularizeCountOnTvRecord) {
            Intent intent = new Intent(this, MyPopularizeApplyRecordActivity.class);
            startActivity(intent);
        }
    }

    private void promoteApply() {
        if (!TextUtils.isEmpty(myPopularizeCountOnEt.getText())) {
            String content = myPopularizeCountOnEt.getText().toString();
            double contentDouble = 0;
            try {
                contentDouble = Double.parseDouble(content);
            } catch (Exception e) {
                e.fillInStackTrace();
            }
            if (contentDouble > 0) {
                ApiManager.getInstance().createApi(Config.HOST, McService.class)
                        .promoteSettlementApply(myPopularizeCountOnEt.getText().toString(), EmUtil.getEmployId())
                        .filter(new HttpResultFunc<>())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new MySubscriber<EmResult>(this, true, false, new NoErrSubscriberListener<EmResult>() {
                            @Override
                            public void onNext(EmResult emResult) {
                                ToastUtil.showMessage(MyPopularizeCountOnActivity.this, "结算成功");
                                finish();
                            }
                        }));
            } else {
                ToastUtil.showMessage(MyPopularizeCountOnActivity.this, "请输入正确的金额");
            }
        } else {
            ToastUtil.showMessage(MyPopularizeCountOnActivity.this, "请输入正确的金额");
        }
    }

}
