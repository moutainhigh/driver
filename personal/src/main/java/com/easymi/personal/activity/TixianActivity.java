package com.easymi.personal.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.easymi.component.Config;
import com.easymi.component.activity.WebActivity;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.Employ;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.network.NoErrSubscriberListener;
import com.easymi.component.result.EmResult;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.Log;
import com.easymi.component.utils.MathUtil;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.CusToolbar;
import com.easymi.personal.McService;
import com.easymi.personal.R;
import com.easymi.personal.entity.BankInfo;
import com.easymi.personal.entity.TixianRule;
import com.easymi.personal.result.BankResult;
import com.easymi.personal.result.LoginResult;
import com.easymi.personal.result.TixianResult;
import com.easymi.personal.result.TixianRuleResult;
import com.easymi.personal.widget.AddSpaceTextWatcher;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: TixianActivity
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description: 余额提现
 * History:
 */

public class TixianActivity extends RxBaseActivity {

    CusToolbar cusToolbar;

    TextView balanceText;
    EditText editMoney;
    TextView maxTixian;
    EditText bankName;
    EditText bankNo;
    EditText bankOwner;
    Button apply;
    TextView tixianRuleText;
    TextView tixianRecord;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_tixian;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        balanceText = findViewById(R.id.balance_text);
        editMoney = findViewById(R.id.edit_money);
        maxTixian = findViewById(R.id.max_tixian);
        bankName = findViewById(R.id.edit_bank_name);
        bankNo = findViewById(R.id.edit_bank_no);
        bankOwner = findViewById(R.id.edit_bank_owner);
        apply = findViewById(R.id.apply);
        tixianRuleText = findViewById(R.id.tixian_rule);
        tixianRecord = findViewById(R.id.tixian_record);

        tixianRuleText.setOnClickListener(view -> {
                    //                startActivity(new Intent(TixianActivity.this, TixianRuleActivity.class))
                    Intent intent = new Intent(this, WebActivity.class);
                    intent.putExtra("url", Config.H5_HOST+"#/protocol?articleName=driverPutForward&appKey=" + Config.APP_KEY);
                    intent.putExtra("title", getString(R.string.tixian_rule));
                    startActivity(intent);
                }
        );
        tixianRecord.setOnClickListener(view -> startActivity(new Intent(TixianActivity.this, TixianRecordActivity.class)));
        apply.setOnClickListener(view -> apply());

        AddSpaceTextWatcher watcher = new AddSpaceTextWatcher(this, bankNo, 48);
        watcher.setBinChangedListener(bName -> bankName.setText(bName));

        DecimalFormat decimalFormat = new DecimalFormat("#0.0");
        decimalFormat.setRoundingMode(RoundingMode.DOWN);

        editMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String str = editable.toString();
                if (StringUtils.isNotBlank(str)) {
                    if (str.startsWith("0")) {
                        if (str.length() >= 2) {
                            if (!String.valueOf(str.charAt(1)).equals(".")) {
                                str = str.substring(1, str.length());
                                editMoney.setText(str);
                                editMoney.setSelection(str.length());
                            }
                        }
                    }
                    String s = editMoney.getText().toString();
                    double money = 0;
                    try {
                        money = Double.parseDouble(s);
                    } catch (Exception e) {
                        money = 0;
                    }
                    if (!MathUtil.isDoubleLegal(money, 1)) {
                        editMoney.setText(decimalFormat.format(money));
                        editMoney.setSelection(decimalFormat.format(money).length());
                    }
                    if (!moneyLegal(money)) {
                        maxTixian.setText(getString(R.string.tixian_1) + tixianRule.withdrawals_min + getString(R.string.tixian_2) + tixianRule.withdrawals_max + getString(R.string.tixian_3)
                                + getString(R.string.tixian_4) + tixianRule.withdrawals_base + getString(R.string.tixian_5));
                        maxTixian.setTextColor(getResources().getColor(R.color.red));
                    } else {
                        if (null != tixianRule) {
                            maxTixian.setText(tixianRule.withdrawals_memo);
                            maxTixian.setTextColor(getResources().getColor(R.color.text_default));
                        }
                    }
                } else {
                    if (null != tixianRule) {
                        maxTixian.setText(tixianRule.withdrawals_memo);
                        maxTixian.setTextColor(getResources().getColor(R.color.text_default));
                    }
                }

            }
        });

        getTixianConfig();//获取提现的规则
        getBankInfo();

        balanceText.setText(String.valueOf(EmUtil.getEmployInfo().balance));
    }

    /**
     * 设置提现等级
     * @param money
     * @return
     */
    private boolean moneyLegal(double money) {
        if (null != tixianRule) {
            if (money < tixianRule.withdrawals_min) {
                return false;
            }
            if (money > tixianRule.withdrawals_max) {
                return false;
            }
            if (tixianRule.withdrawals_base != 0 && money % tixianRule.withdrawals_base != 0) {
                return false;
            }
            return true;
        } else {
            return true;
        }
    }

    /**
     * 提现申请
     */
    private void apply() {
        double money = 0.0;
        try {
            money = Double.parseDouble(editMoney.getText().toString());
        } catch (Exception e) {
            money = 0.0;
        }
        String name = bankName.getText().toString();
        String no = bankNo.getText().toString();
        String owner = bankOwner.getText().toString();

        if (money == 0.0) {
            ToastUtil.showMessage(TixianActivity.this, getString(R.string.please_money));
            return;
        }
        if (null != tixianRule) {
            if (money < tixianRule.withdrawals_min) {
                ToastUtil.showMessage(TixianActivity.this, getString(R.string.min_money) + tixianRule.withdrawals_min + getString(R.string.yuan));
                return;
            }
            if (money > tixianRule.withdrawals_max) {
                ToastUtil.showMessage(TixianActivity.this, getString(R.string.max_money) + tixianRule.withdrawals_max + getString(R.string.yuan));
                return;
            }
            if (tixianRule.withdrawals_base != 0 && money % tixianRule.withdrawals_base != 0) {
                ToastUtil.showMessage(TixianActivity.this, getString(R.string.base_money) + tixianRule.withdrawals_base + getString(R.string.de_beishu));
                return;
            }
        }
        if (StringUtils.isBlank(no)) {
            ToastUtil.showMessage(TixianActivity.this, getString(R.string.please_bank_number));
            return;
        }
        if (StringUtils.isBlank(name)) {
            ToastUtil.showMessage(TixianActivity.this, getString(R.string.please_bank_name));
            return;
        }

        if (StringUtils.isBlank(owner)) {
            ToastUtil.showMessage(TixianActivity.this, getString(R.string.please_bank_owner));
            return;
        }

        Observable<EmResult> observable = ApiManager.getInstance().createApi(Config.HOST, McService.class)
                .enchashment(no, money, name, owner)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(TixianActivity.this, true,
                true, emResult -> {
            ToastUtil.showMessage(TixianActivity.this, getString(R.string.tixian_apply_suc));
            Intent intent = new Intent(TixianActivity.this, TixianRecordActivity.class);
            startActivity(intent);
            finish();
        })));
    }

    @Override
    public void initToolBar() {
        cusToolbar = findViewById(R.id.cus_toolbar);
        cusToolbar.setLeftBack(view -> finish());
        cusToolbar.setTitle(R.string.tixian_title);
    }

    /**
     * 提现规则
     */
    private TixianRule tixianRule;

    /**
     * 获取提现规则
     */
    private void getTixianConfig() {
        Observable<TixianRuleResult> observable = ApiManager.getInstance().createApi(Config.HOST, McService.class)
                .tixianRule()
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, true, false, new NoErrSubscriberListener<TixianRuleResult>() {
            @Override
            public void onNext(TixianRuleResult result) {
                if (null != result.tixianRule) {
                    tixianRule = result.tixianRule;
                    maxTixian.setText(tixianRule.withdrawals_memo);
                }
            }
        })));
    }

    @Override
    public boolean isEnableSwipe() {
        return true;
    }

    /**
     * 获取银行信息
     */
    private void getBankInfo() {
        Observable<BankResult> observable = ApiManager.getInstance().createApi(Config.HOST, McService.class)
                .bankInfo()
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, true,
                false, bankResult -> {
            if (bankResult.getCode() == 1 && bankResult.data != null) {
                BankInfo bankInfo = bankResult.data;

                if (StringUtils.isNotBlank(bankInfo.bankName)) {
                    bankName.setText(bankInfo.bankName);
                }
                if (StringUtils.isNotBlank(bankInfo.bankCardNo)) {
                    bankNo.setText(bankInfo.bankCardNo);
                }
                if (StringUtils.isNotBlank(bankInfo.payee)) {
                    bankOwner.setText(bankInfo.payee);
                }
            }
        })));
    }
}
