package com.easymi.personal.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easymi.component.Config;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.Employ;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.PhoneUtil;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.CusToolbar;
import com.easymi.personal.McService;
import com.easymi.personal.R;
import com.easymi.personal.result.LoginResult;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by developerLzh on 2017/11/9 0009.
 */

public class RechargeActivity extends RxBaseActivity {

    CheckBox pay50;
    CheckBox pay100;
    CheckBox pay200;
    EditText payCus;

    TextView balanceText;

    CusToolbar cusToolbar;

    RelativeLayout payWx;
    RelativeLayout payZfb;
    RelativeLayout payUnion;

    @Override
    public int getLayoutId() {
        return R.layout.activity_recharge;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        findById();

        initCheck();

        initEdit();

        getDriverInfo(EmUtil.getEmployId());

        payWx.setOnClickListener(view -> {
            double money = getMoney();
            if (money == 0.0) {
                ToastUtil.showMessage(RechargeActivity.this, getString(R.string.recharge_0_money));
            } else {
                ToastUtil.showMessage(RechargeActivity.this, "充值：" + money + "元");
                payWx(money);
            }
        });
        payZfb.setOnClickListener(view -> {
            double money = getMoney();
            if (money == 0.0) {
                ToastUtil.showMessage(RechargeActivity.this, getString(R.string.recharge_0_money));
            } else {
                ToastUtil.showMessage(RechargeActivity.this, "充值：" + money + "元");
                payZfb(money);
            }
        });
        payUnion.setOnClickListener(view -> {
            double money = getMoney();
            if (money == 0.0) {
                ToastUtil.showMessage(RechargeActivity.this, getString(R.string.recharge_0_money));
            } else {
                ToastUtil.showMessage(RechargeActivity.this, "充值：" + money + "元");
                payUnion(money);
            }
        });
    }

    @Override
    public void initToolBar() {
        cusToolbar.setLeftBack(view -> finish());
        cusToolbar.setTitle(R.string.recharge_title);
    }

    private void payWx(double money) {

    }

    private void payZfb(double money) {

    }

    private void payUnion(double money) {

    }

    private void initEdit() {
        payCus.setOnFocusChangeListener((view, b) -> {
            if (b) {
                pay50.setChecked(false);
                pay100.setChecked(false);
                pay200.setChecked(false);
            }
        });
    }

    private void initCheck() {
        pay50.setOnCheckedChangeListener(new MyCheckChangeLis(0));
        pay100.setOnCheckedChangeListener(new MyCheckChangeLis(1));
        pay200.setOnCheckedChangeListener(new MyCheckChangeLis(2));
    }

    private void findById() {
        pay50 = findViewById(R.id.pay_50yuan);
        pay100 = findViewById(R.id.pay_100yuan);
        pay200 = findViewById(R.id.pay_200yuan);

        payCus = findViewById(R.id.pay_custom);

        payWx = findViewById(R.id.pay_wenXin);
        payZfb = findViewById(R.id.pay_zfb);
        payUnion = findViewById(R.id.pay_union);

        cusToolbar = findViewById(R.id.cus_toolbar);

        balanceText = findViewById(R.id.balance_text);
    }

    class MyCheckChangeLis implements CompoundButton.OnCheckedChangeListener {

        private int tag = 0;

        public MyCheckChangeLis(int tag) {
            this.tag = tag;
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (b) {
                if (tag == 0) {
                    resetOtherCheck(0);
                } else if (tag == 1) {
                    resetOtherCheck(1);
                } else if (tag == 2) {
                    resetOtherCheck(2);
                }
            }
        }
    }

    private void resetOtherCheck(int tag) {
        if (tag == 0) {
            pay100.setChecked(false);
            pay200.setChecked(false);
        } else if (tag == 1) {
            pay50.setChecked(false);
            pay200.setChecked(false);
        } else if (tag == 2) {
            pay50.setChecked(false);
            pay100.setChecked(false);
        }
        payCus.clearFocus();

        PhoneUtil.hideKeyboard(this);
    }

    private double getMoney() {
        double money = 0.0;
        if (pay50.isChecked()) {
            money = 50;
        }
        if (pay100.isChecked()) {
            money = 100;
        }
        if (pay200.isChecked()) {
            money = 200;
        }
        if (!pay50.isChecked() && !pay100.isChecked() && !pay200.isChecked()) {
            if (StringUtils.isNotBlank(payCus.getText().toString())) {
                try {
                    money = Double.parseDouble(payCus.getText().toString());
                } catch (Exception e) {
                    return 0.0;
                }
            }
        }
        return money;
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
            editor.putLong(Config.SP_DRIVERID, employ.id);
            editor.apply();

            balanceText.setText(String.valueOf(employ.balance));
        })));
    }
}
