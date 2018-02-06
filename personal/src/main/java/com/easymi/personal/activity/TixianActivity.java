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
import com.easymi.component.utils.StringUtils;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.CusToolbar;
import com.easymi.personal.McService;
import com.easymi.personal.R;
import com.easymi.personal.result.LoginResult;
import com.easymi.personal.result.TixianResult;
import com.easymi.personal.widget.AddSpaceTextWatcher;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by developerLzh on 2017/11/11 0011.
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
    TextView tixianRule;
    TextView tixianRecord;

    @Override
    protected void onResume() {
        super.onResume();
        getDriverInfo(EmUtil.getEmployId());
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
        tixianRule = findViewById(R.id.tixian_rule);
        tixianRecord = findViewById(R.id.tixian_record);

        tixianRule.setOnClickListener(view -> startActivity(new Intent(TixianActivity.this, TixianRuleActivity.class)));
        tixianRecord.setOnClickListener(view -> startActivity(new Intent(TixianActivity.this, TixianRecordActivity.class)));
        apply.setOnClickListener(view -> apply());

        AddSpaceTextWatcher watcher = new AddSpaceTextWatcher(this, bankNo, 48);
        watcher.setBinChangedListener(bName -> bankName.setText(bName));

        editMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String s = editable.toString();
                if (StringUtils.isBlank(s)) {
                    return;
                }
                double money = 0;
                try {
                    money = Double.parseDouble(s);
                } catch (NumberFormatException ex) {
                    money = 0;
                } finally {
                    Employ employ = EmUtil.getEmployInfo();
                    if (money > employ.balance) {
                        balanceText.setText(String.valueOf(employ.balance));
                    }
                }

            }
        });

    }

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
        } //TODO 提现最小金额 提现金额 整数倍
        if (StringUtils.isBlank(name)) {
            ToastUtil.showMessage(TixianActivity.this, getString(R.string.please_bank_name));
            return;
        }
        if (StringUtils.isBlank(no)) {
            ToastUtil.showMessage(TixianActivity.this, getString(R.string.please_bank_number));
            return;
        }
        if (StringUtils.isBlank(owner)) {
            ToastUtil.showMessage(TixianActivity.this, getString(R.string.please_bank_owner));
            return;
        }

        Employ employ = EmUtil.getEmployInfo();
        Observable<EmResult> observable = ApiManager.getInstance().createApi(Config.HOST, McService.class)
                .enchashment(employ.name, employ.user_name, employ.phone, money,
                        employ.company_id, Config.APP_KEY, name, no, owner)
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

    private void getDriverInfo(Long driverId) {
        Observable<LoginResult> observable = ApiManager.getInstance().createApi(Config.HOST, McService.class)
                .getDriverInfo(driverId, Config.APP_KEY)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, true, true, loginResult -> {
            Employ employ = loginResult.getEmployInfo();
            Log.e("okhttp", employ.toString());
            employ.saveOrUpdate();
            SharedPreferences.Editor editor = XApp.getPreferencesEditor();
            editor.apply();

            balanceText.setText(String.valueOf(employ.balance));
            maxTixian.setText(String.valueOf(employ.balance));

            if (StringUtils.isNotBlank(employ.bank_name)) {
                bankName.setText(employ.bank_name);
            }
            if (StringUtils.isNotBlank(employ.bank_card_no)) {
                bankNo.setText(employ.bank_card_no);
            }
            if (StringUtils.isNotBlank(employ.cash_person_name)) {
                bankOwner.setText(employ.cash_person_name);
            }
        })));
    }
}
