package com.easymi.personal.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;

import com.easymi.common.CommApiService;
import com.easymi.common.result.LoginResult;
import com.easymi.component.Config;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxPayActivity;
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
import com.easymi.personal.entity.MoneyConfig;
import com.easymi.personal.result.ConfigResult;
import com.easymi.personal.result.RechargeResult;
import com.easymi.personal.result.RechargeTypeResult;

import org.json.JSONException;
import org.json.JSONObject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: RechargeActivity
 *
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:  充值界面
 * History:
 */
public class RechargeActivity extends RxPayActivity {

    CheckBox pay50;
    CheckBox pay100;
    CheckBox pay200;
    EditText payCus;
    TextView balanceText;
    CusToolbar cusToolbar;
    RelativeLayout payWx;
    RelativeLayout payZfb;
    RelativeLayout payUnion;
    Space id_space1;
    Space id_space2;
    LinearLayout lin_check_box;

    /**
     * 最低充值金额
     */
    private double limitMoney = 0;

    @Override
    public int getLayoutId() {
        return R.layout.activity_recharge;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        findById();

        initCheck();

        initEdit();

        showWhatByConfig();

        getConfigure();

        configPayment();

        payWx.setOnClickListener(view -> {
            double money = getMoney();
            if (money == 0.0) {
                ToastUtil.showMessage(RechargeActivity.this, getString(R.string.recharge_0_money));
            } else if (money < limitMoney) {
                ToastUtil.showMessage(RechargeActivity.this, "最低充值" + limitMoney);
            } else {
                payWx(money);
            }
        });
        payZfb.setOnClickListener(view -> {
            double money = getMoney();
            if (money == 0.0) {
                ToastUtil.showMessage(RechargeActivity.this, getString(R.string.recharge_0_money));
            } else if (money < limitMoney) {
                ToastUtil.showMessage(RechargeActivity.this, "最低充值" + limitMoney);
            } else {
                payZfb(money);
            }
        });
    }

    MoneyConfig moneyConfig;

    /**
     * 获取充值配置
     */
    private void getConfigure() {
        Observable<ConfigResult> observable = ApiManager.getInstance().createApi(Config.HOST, McService.class)
                .rechargeConfigure()
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, true, true, configResult -> {
            if (configResult.getCode() == 1 && configResult.object != null) {
                moneyConfig = configResult.object;
                showWhatByConfig();
            }
        })));
    }

    /**
     * 获取充值方式配置
     */
    private void configPayment() {
        Observable<RechargeTypeResult> observable = ApiManager.getInstance().createApi(Config.HOST, McService.class)
                .configPayment()
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, true, true, emResult -> {
            //todo 未找到后台配置的地方，目前全是false。故不展示
//            if (emResult.getCode() == 1){
//                if (emResult.data.weChatApp){
//                    payWx.setVisibility(View.VISIBLE);
//                }else {
//                    payWx.setVisibility(View.GONE);
//                }
//                if (emResult.data.aliPayApp){
//                    payZfb.setVisibility(View.VISIBLE);
//                }else {
//                    payZfb.setVisibility(View.GONE);
//                }
//            }
        })));
    }

    /**
     * 显示重置配置
     */
    private void showWhatByConfig() {
        if (moneyConfig == null) {
            moneyConfig = new MoneyConfig();
            moneyConfig.one = 50;
            moneyConfig.two = 100;
            moneyConfig.three = 200;
        } else {
            if (moneyConfig.one == 0) {
                pay50.setVisibility(View.GONE);
            } else {
                pay50.setChecked(true);
                pay50.setVisibility(View.VISIBLE);
            }

            if (moneyConfig.two == 0) {
                pay100.setVisibility(View.GONE);
                id_space1.setVisibility(View.GONE);
            } else {
                if (moneyConfig.one == 0) {
                    pay100.setChecked(true);
                }
            }

            if (moneyConfig.three == 0) {
                pay200.setVisibility(View.GONE);
                id_space2.setVisibility(View.GONE);
            } else {
                if (moneyConfig.one == 0 && moneyConfig.two == 0) {
                    pay200.setChecked(true);
                }
            }
            if (moneyConfig.one == 0 && moneyConfig.two == 0 && moneyConfig.three == 0) {
                pay50.setVisibility(View.VISIBLE);
                pay100.setVisibility(View.VISIBLE);
                pay200.setVisibility(View.VISIBLE);
                id_space1.setVisibility(View.VISIBLE);
                id_space2.setVisibility(View.VISIBLE);
                lin_check_box.setVisibility(View.VISIBLE);
                moneyConfig.one = 50;
                moneyConfig.two = 100;
                moneyConfig.three = 200;
            }
        }

        pay50.setText(getString(R.string.renminbi) + moneyConfig.one);
        pay100.setText(getString(R.string.renminbi) + moneyConfig.two);
        pay200.setText(getString(R.string.renminbi) + moneyConfig.three);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDriverInfo(EmUtil.getEmployId());
    }

    @Override
    public void initToolBar() {
        cusToolbar.setLeftBack(view -> finish());
        cusToolbar.setTitle(R.string.recharge_title);
    }

    private void payWx(double money) {
        recharge("CHANNEL_APP_WECHAT", money);
    }

    private void payZfb(double money) {
        recharge("CHANNEL_APP_ALI", money);
    }

    /**
     * 单选框监听
     */
    private void initEdit() {
        payCus.setOnFocusChangeListener((view, b) -> {
            if (b) {
                pay50.setChecked(false);
                pay100.setChecked(false);
                pay200.setChecked(false);
            }
        });
    }

    /**
     * 设置监听
     */
    private void initCheck() {
        pay50.setOnCheckedChangeListener(new MyCheckChangeLis(0));
        pay100.setOnCheckedChangeListener(new MyCheckChangeLis(1));
        pay200.setOnCheckedChangeListener(new MyCheckChangeLis(2));
    }

    /**
     * 初始化控件
     */
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

        lin_check_box = findViewById(R.id.lin_check_box);
        id_space1 = findViewById(R.id.id_space1);
        id_space2 = findViewById(R.id.id_space2);
    }

    @Override
    public void onPaySuc() {

    }

    @Override
    public void onPayFail() {

    }

    /**
     * 选择框监听
     */
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

    /**
     * 重置其他的选择框状态
     *
     * @param tag
     */
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

    /**
     * 获取充值金额
     *
     * @return
     */
    private double getMoney() {
        double money = 0.0;
        if (pay50.isChecked()) {
            if (moneyConfig != null) {
                money = moneyConfig.one;
            } else {
                money = 50;
            }
        }
        if (pay100.isChecked()) {
            if (moneyConfig != null) {
                money = moneyConfig.two;
            } else {
                money = 100;
            }
        }
        if (pay200.isChecked()) {
            if (moneyConfig != null) {
                money = moneyConfig.three;
            } else {
                money = 200;
            }
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

    /**
     * 获取司机信息
     *
     * @param driverId
     */
    private void getDriverInfo(Long driverId) {
        Observable<LoginResult> observable = ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .getDriverInfo(driverId, EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, true, true, loginResult -> {
            Employ employ = loginResult.data;
            employ.saveOrUpdate();
            XApp.getEditor().putLong(Config.SP_DRIVERID, employ.id)
                    .apply();

            balanceText.setText(String.valueOf(employ.balance));
        })));
    }

    /**
     * 充值
     *
     * @param payType
     * @param money
     */
    private void recharge(String payType, Double money) {
        Observable<RechargeResult> observable = ApiManager.getInstance().createApi(Config.HOST, McService.class)
                .recharge(payType, money)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, true, true, rechargeResult -> {
            if (payType.equals("CHANNEL_APP_WECHAT")) {
                launchWeixin(rechargeResult.data);
            } else if (payType.equals("CHANNEL_APP_ALI")) {
                String url = null;
                try {
                    url = new JSONObject(rechargeResult.data.toString()).getString("ali_app_url");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                launchZfb(url);
            }
        })));
    }

    @Override
    public boolean isEnableSwipe() {
        return true;
    }
}
