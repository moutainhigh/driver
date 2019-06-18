package com.easymi.common.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.easymi.common.R;
import com.easymi.component.Config;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.Employ;
import com.easymi.component.utils.CsEditor;
import com.easymi.component.utils.CsSharedPreferences;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.switchButton.SwitchButton;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:ModelSetActivity
 *
 * @Author: hufeng
 * Date: 2018/12/24 下午1:10
 * Description: 听单模式 未使用
 * History:
 */

public class ModelSetActivity extends RxBaseActivity {

    LinearLayout daijiaCon;
    LinearLayout zhuancheCon;

    SwitchButton daijiaSwitch;
    SwitchButton zhuancheSwitch;

    @Override
    public void initToolBar() {
        super.initToolBar();
        CusToolbar cusToolbar = findViewById(R.id.toolbar);
        cusToolbar.setLeftBack(view -> finish());
        cusToolbar.setTitle(R.string.listen_order_set);
    }

    @Override
    public boolean isEnableSwipe() {
        return true;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_model_set;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        daijiaCon = findViewById(R.id.daijia_con);
        zhuancheCon = findViewById(R.id.zhuanche_con);

        daijiaSwitch = findViewById(R.id.daijia_enable);
        zhuancheSwitch = findViewById(R.id.zhuanche_enable);

        Employ employ = EmUtil.getEmployInfo();
        String business = employ.serviceType;

        if (!business.contains(Config.DAIJIA)) {
            daijiaCon.setVisibility(View.GONE);
        }
        if (!business.contains(Config.ZHUANCHE)) {
            zhuancheCon.setVisibility(View.GONE);
        }

        daijiaSwitch.setChecked(XApp.getMyPreferences().getBoolean(Config.SP_DAIJIA_LISTEN_ORDER, true));
        zhuancheSwitch.setChecked(XApp.getMyPreferences().getBoolean(Config.SP_ZHUANCHE_LISTEN_ORDER, true));

        daijiaSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if (!b) {
                if (!zhuancheSwitch.isChecked()) {
                    ToastUtil.showMessage(ModelSetActivity.this, getString(R.string.listen_order_more_one));
                    daijiaSwitch.setChecked(true);
                }
            }
        });

        zhuancheSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if (!b) {
                if (!daijiaSwitch.isChecked()) {
                    ToastUtil.showMessage(ModelSetActivity.this, getString(R.string.listen_order_more_one));
                    zhuancheSwitch.setChecked(true);
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        XApp.getEditor().putBoolean(Config.SP_DAIJIA_LISTEN_ORDER, daijiaSwitch.isChecked())
                .putBoolean(Config.SP_ZHUANCHE_LISTEN_ORDER, zhuancheSwitch.isChecked())
                .apply();
    }
}
