package com.easymi.personal.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.easymi.common.CommApiService;
import com.easymi.common.result.LoginResult;
import com.easymi.component.Config;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.Employ;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.result.EmResult;
import com.easymi.component.utils.CsEditor;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.PhoneUtil;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.CusToolbar;
import com.easymi.personal.McService;
import com.easymi.personal.R;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

@Route(path = "/personal/PopularizeActivity")
public class PopularizeActivity extends RxBaseActivity implements View.OnClickListener {

    private CusToolbar popularizeCtb;
    private TextView popularizeTvContent;
    private TextView popularizeTvPhone;
    private TextView popularizeTvActionGreen;
    private TextView popularizeTvActionGreenLine;
    private Employ employ;

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_popularize;
    }

    @Override
    public void initToolBar() {
        super.initToolBar();
        popularizeCtb.setLeftIcon(R.drawable.ic_arrow_back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        popularizeCtb = findViewById(R.id.popularizeCtb);
        popularizeTvContent = findViewById(R.id.popularizeTvContent);
        popularizeTvPhone = findViewById(R.id.popularizeTvPhone);
        popularizeTvActionGreen = findViewById(R.id.popularizeTvActionGreen);
        popularizeTvActionGreen.setOnClickListener(this);
        popularizeTvActionGreenLine = findViewById(R.id.popularizeTvActionGreenLine);
        popularizeTvActionGreenLine.setOnClickListener(this);
        initData();
    }

    private void initData() {
        Employ employ = EmUtil.getEmployInfo();
        if (employ.id != 0) {
            this.employ = employ;
            bindInfoResult();
        } else {
            ToastUtil.showMessage(this, "数据出现错误,请重试...");
            finish();
        }
    }

    private void bindInfoResult() {
        if (employ.isOpenPromote != 1) {
            ToastUtil.showMessage(this, "数据出现错误,请重试...");
            finish();
        } else {
            if (employ.promoteApplyStatus == 0) {
                popularizeTvContent.setText("您可以申请成为平台推广员，申请后经审核您可成为平台的推广员，我们将与您精密合作，互帮互助共同发展。");
                popularizeCtb.setTitle("成为推广者");
                popularizeTvPhone.setVisibility(View.GONE);
                popularizeTvActionGreen.setVisibility(View.VISIBLE);
                popularizeTvActionGreen.setText("申请成为推广者");
                popularizeTvActionGreenLine.setVisibility(View.GONE);
            } else if (employ.promoteApplyStatus == 1) {
                popularizeTvContent.setText("您可以申请成为平台推广员，申请后经审核您可成为平台的推广员，我们将与您精密合作，互帮互助共同发展。");
                popularizeCtb.setTitle("审核中");
                popularizeTvPhone.setVisibility(View.GONE);
                popularizeTvActionGreen.setVisibility(View.GONE);
                popularizeTvActionGreenLine.setVisibility(View.GONE);
            } else if (employ.promoteApplyStatus == 2) {
                popularizeTvContent.setText("您的申请已被管理员驳回，您可以联系平台了解详细信息，也可以重新提交申请。");
                popularizeCtb.setTitle("成为推广者");
                setSpan();
                popularizeTvActionGreen.setVisibility(View.VISIBLE);
                popularizeTvActionGreen.setText("重新提交");
                popularizeTvActionGreenLine.setVisibility(View.GONE);

            } else if (employ.promoteApplyStatus == 3) {
                popularizeTvContent.setText("您可以申请成为平台推广员，申请后经审核您可成为平台的推广员，我们将与您精密合作，互帮互助共同发展。。");
                popularizeCtb.setTitle("我的推广");
                popularizeTvPhone.setVisibility(View.GONE);
                popularizeTvActionGreen.setVisibility(View.VISIBLE);
                popularizeTvActionGreen.setText("我的推广码");
                popularizeTvActionGreenLine.setVisibility(View.VISIBLE);
                popularizeTvActionGreenLine.setText("推广详情");
            } else if (employ.promoteApplyStatus == 4) {
                popularizeTvContent.setText("您已被管理员停权，您可以联系平台了解详细信息，也可以重新提交申请。");
                popularizeCtb.setTitle("成为推广者");
                setSpan();
                popularizeTvActionGreen.setVisibility(View.GONE);
                popularizeTvActionGreenLine.setVisibility(View.GONE);
            }
        }
    }

    private void setSpan() {
        popularizeTvPhone.setVisibility(View.VISIBLE);
        popularizeTvPhone.setHighlightColor(Color.TRANSPARENT);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder("平台联系电话:");
        spannableStringBuilder.append(EmUtil.getEmployInfo().companyPhone);
        spannableStringBuilder.setSpan(new ClickableSpan() {
            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(ContextCompat.getColor(PopularizeActivity.this, R.color.colorYellow));
            }

            @Override
            public void onClick(@NonNull View widget) {
                if (!TextUtils.isEmpty(EmUtil.getEmployInfo().companyPhone)) {
                    PhoneUtil.call(PopularizeActivity.this, EmUtil.getEmployInfo().companyPhone);
                }
            }
        }, 7, spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        popularizeTvPhone.setText(spannableStringBuilder);
        popularizeTvPhone.setMovementMethod(LinkMovementMethod.getInstance());
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.popularizeTvActionGreen) {
            if (employ.promoteApplyStatus == 3) {
                Intent intent = new Intent(this, MyPopularizeCodeActivity.class);
                startActivity(intent);
            } else {
                bePopularizer();
            }

        } else if (v.getId() == R.id.popularizeTvActionGreenLine) {
            Intent intent = new Intent(this, MyPopularizeActivity.class);
            startActivity(intent);
        }
    }

    private void bePopularizer() {
        mRxManager.add(ApiManager.getInstance().createApi(Config.HOST, McService.class)
                .promoteApply(EmUtil.getEmployInfo().nickName, EmUtil.getEmployInfo().phone, 2)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(new Func1<EmResult, Observable<?>>() {
                    @Override
                    public Observable<?> call(EmResult emResult) {
                        return Observable.timer(1, TimeUnit.SECONDS);
                    }
                }).flatMap(new Func1<Object, Observable<LoginResult>>() {
                    @Override
                    public Observable<LoginResult> call(Object o) {
                        return ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                                .getDriverInfo(EmUtil.getEmployId(), EmUtil.getAppKey())
                                .filter(new HttpResultFunc<>());
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MySubscriber<LoginResult>(this, true, false, new HaveErrSubscriberListener<LoginResult>() {
                    @Override
                    public void onNext(LoginResult passengerInfoResult) {
                        Employ employ = passengerInfoResult.data;
                        employ.saveOrUpdate();
                        CsEditor editor = XApp.getEditor();
                        editor.putLong(Config.SP_DRIVERID, employ.id);
                        editor.apply();
                        PopularizeActivity.this.employ = passengerInfoResult.data;
                        bindInfoResult();
                    }

                    @Override
                    public void onError(int code) {
                        finish();
                    }
                })));
    }

}
