package com.easymi.personal.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.easymi.component.Config;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.Employ;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.utils.AesUtil;
import com.easymi.component.utils.EmUtil;
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

public class PocketActivity extends RxBaseActivity {

    TextView balanceText;
    RelativeLayout rechargeCon;
    RelativeLayout detailCon;

    CusToolbar toolbar;

    @Override
    public int getLayoutId() {
        return R.layout.activity_pocket;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        balanceText = findViewById(R.id.balance_text);
        rechargeCon = findViewById(R.id.recharge_con);
        detailCon = findViewById(R.id.detail_con);

        rechargeCon.setOnClickListener(v -> startActivity(new Intent(PocketActivity.this, RechargeActivity.class)));

        detailCon.setOnClickListener(v -> startActivity(new Intent(PocketActivity.this, DetailActivity.class)));
    }

    @Override
    public void initToolBar() {
        toolbar = findViewById(R.id.cus_toolbar);
        toolbar.setLeftBack(view -> finish());
        toolbar.setTitle(R.string.pocket_title);
        toolbar.setRightText(R.string.ti_xian,
                view -> startActivity(new Intent(PocketActivity.this, TixianActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDriverInfo(EmUtil.getEmployId());
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
            editor.putBoolean(Config.SP_ISLOGIN, true);
            editor.putLong(Config.SP_DRIVERID, employ.id);
            editor.apply();

            balanceText.setText(String.valueOf(employ.balance));
        })));
    }
}
