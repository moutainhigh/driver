package com.easymi.personal.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.easymi.component.Config;
import com.easymi.component.entity.Setting;
import com.easymi.component.utils.Log;

import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
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
import com.easymi.personal.result.PayResult;
import com.easymi.personal.result.RechargeResult;
import com.ffcs.inapppaylib.bean.Constants;
import com.ffcs.inapppaylib.bean.response.BaseResponse;
import com.google.gson.JsonElement;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.unionpay.UPPayAssistEx;

import org.json.JSONException;
import org.json.JSONObject;

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

        payWx.setOnClickListener(view -> {
            double money = getMoney();
            if (money == 0.0) {
                ToastUtil.showMessage(RechargeActivity.this, getString(R.string.recharge_0_money));
            } else {
                payWx(money);
            }
        });
        payZfb.setOnClickListener(view -> {
            double money = getMoney();
            if (money == 0.0) {
                ToastUtil.showMessage(RechargeActivity.this, getString(R.string.recharge_0_money));
            } else {
                payZfb(money);
            }
        });
        payUnion.setOnClickListener(view -> {
            double money = getMoney();
            if (money == 0.0) {
                ToastUtil.showMessage(RechargeActivity.this, getString(R.string.recharge_0_money));
            } else {
                payUnion(money);
            }
        });
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
//        new Payer().wxPay();
        recharge(EmUtil.getEmployId(), "weixin", money);
    }

    private void payZfb(double money) {
        recharge(EmUtil.getEmployId(), "alipay", money);
    }

    private void payUnion(double money) {
        recharge(EmUtil.getEmployId(), "unionpay", money);
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

        Setting setting = Setting.findOne();
        pay50.setText(getString(R.string.money_sign) + setting.payMoney1);
        pay100.setText(getString(R.string.money_sign) + setting.payMoney2);
        pay200.setText(getString(R.string.money_sign) + setting.payMoney3);
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
        Setting setting = Setting.findOne();
        if (pay50.isChecked()) {
            money = setting.payMoney1;
        }
        if (pay100.isChecked()) {
            money = setting.payMoney2;
        }
        if (pay200.isChecked()) {
            money = setting.payMoney3;
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
                .getDriverInfo(driverId, EmUtil.getAppKey())
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

    private void recharge(Long driverId, String payType, Double money) {
        Observable<RechargeResult> observable = ApiManager.getInstance().createApi(Config.HOST, McService.class)
                .recharge(driverId, EmUtil.getAppKey(), payType, money, 2)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, true, true, rechargeResult -> {
            if (payType.equals("weixin")) {
                launchWeixin(rechargeResult.weixinResult);
            } else if (payType.equals("alipay")) {
                launchZfb(rechargeResult.aliPayResult.payUrl);
            } else if (payType.equals("unionpay")) {
                launchYiPay(rechargeResult.unionResult.data);
            }
        })));
    }

    private void launchWeixin(JsonElement data) {
        JSONObject json;
        try {
            json = new JSONObject(data.toString());
            PayReq req = new PayReq();
            req.appId = json.getString("wx_app_id");
            req.partnerId = json.getString("wx_mch_id");
            req.prepayId = json.getString("wx_pre_id");
            req.nonceStr = json.getString("wx_app_nonce");
            req.timeStamp = json.getString("wx_app_ts");
            req.packageValue = json.getString("wx_app_pkg");
            req.sign = json.getString("wx_app_sign");
            req.extData = "app data"; // optional
            Log.e("wxPay", "正常调起支付");

            IWXAPI api = WXAPIFactory.createWXAPI(RechargeActivity.this, req.appId);
            // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信

            api.sendReq(req);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void launchZfb(String data) {
        new Thread() {
            public void run() {

                PayTask alipay = new PayTask(RechargeActivity.this);
                String result = alipay
                        .pay(data, true);

                Message msg = new Message();
                msg.what = 0;
                msg.obj = result;
                handler.sendMessage(msg);
            }
        }.start();
    }

    private void launchYiPay(String data) {
        String serverMode = "01";//00测试环境 01真实环境
        UPPayAssistEx.startPay(RechargeActivity.this, null, null, data, serverMode);
    }

    Handler handler = new Handler(msg -> {
        switch (msg.what) {
            case 0:
                Context context = RechargeActivity.this;
                PayResult result = new PayResult((String) msg.obj);
                if (result.resultStatus.equals("9000")) {
                    Toast.makeText(context, getString(R.string.alipay_success),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, getString(R.string.alipay_failed),
                            Toast.LENGTH_SHORT).show();
                }
                break;
            /**翼支付回调**/
            case Constants.RESULT_VALIDATE_FAILURE:
                // 合法性验证失败
                BaseResponse resp = (BaseResponse) msg.obj;
                Toast.makeText(RechargeActivity.this,
                        resp.getRes_code() + ":" + resp.getRes_message(),
                        Toast.LENGTH_SHORT).show();
                break;

            case Constants.RESULT_PAY_SUCCESS:
                // 支付成功
                resp = (BaseResponse) msg.obj;
                Toast.makeText(RechargeActivity.this,
                        resp.getRes_code() + ":" + resp.getRes_message(),
                        Toast.LENGTH_SHORT).show();
                break;

            case Constants.RESULT_PAY_FAILURE:
                // 支付失败
                resp = (BaseResponse) msg.obj;
                Toast.makeText(RechargeActivity.this,
                        resp.getRes_code() + ":" + resp.getRes_message(),
                        Toast.LENGTH_SHORT).show();
                break;
            /**翼支付回调**/
        }
        return true;
    });

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        String str = data.getExtras().getString("pay_result");
        if (str.equalsIgnoreCase("success")) {

            ToastUtil.showMessage(RechargeActivity.this, "支付成功");

// 结果result_data为成功时，去商户后台查询一下再展示成功
        } else if (str.equalsIgnoreCase("fail")) {
            ToastUtil.showMessage(RechargeActivity.this, "支付失败！");
        } else if (str.equalsIgnoreCase("cancel")) {
            ToastUtil.showMessage(RechargeActivity.this, "你已取消了本次订单的支付！");
        }
    }

    @Override
    public boolean isEnableSwipe() {
        return true;
    }
}
