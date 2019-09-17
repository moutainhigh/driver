package com.easymi.component.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.easymi.component.Config;
import com.easymi.component.R;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.EnvironmentPojo;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.CusToolbar;
import com.google.gson.Gson;

public class SettingActivity extends RxBaseActivity {

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        CusToolbar settingCtb = findViewById(R.id.setting_ctb);
        RadioGroup settingRgKey = findViewById(R.id.setting_rg_key);
        EditText settingEtHost = findViewById(R.id.setting_et_host);
        EditText settingEtH5 = findViewById(R.id.setting_et_h5);
        RadioGroup settingRg = findViewById(R.id.setting_rg);
        TextView settingTv = findViewById(R.id.setting_tv);

        settingCtb
                .setTitle("环境配置")
                .setRightText("重置", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        XApp.getEditor().remove("environment_setting").apply();
                        ToastUtil.showMessage(SettingActivity.this, "已重置为打包配置,请重启APP再试。");
                        finish();
                    }
                })
                .setLeftIcon(R.drawable.ic_arrow_back, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });

        if (Config.APP_KEY.startsWith("1HAc")) {
            settingRgKey.check(R.id.setting_rb_1h);
        } else if (Config.APP_KEY.startsWith("4ji3")) {
            settingRgKey.check(R.id.setting_rb_4j);
        } else if (Config.APP_KEY.startsWith("955B")) {
            settingRgKey.check(R.id.setting_rb_95);
        }
        settingEtHost.setText(Config.HOST);
        settingEtH5.setText(Config.H5_HOST);
        settingRg.check(Config.IS_ENCRYPT ? R.id.setting_rb_en : R.id.setting_rb_de);

        settingTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EnvironmentPojo environmentPojo = new EnvironmentPojo();
                if (settingRgKey.getCheckedRadioButtonId() == R.id.setting_rb_1h) {
                    environmentPojo.appKey = "955BF9BC78A5032107C4B74F4AB65702";
                } else if (settingRgKey.getCheckedRadioButtonId() == R.id.setting_rb_4j) {
                    environmentPojo.appKey = "4ji3EvuwNziPKF8QXqXMTukGqPmlwOFJ";
                } else if (settingRgKey.getCheckedRadioButtonId() == R.id.setting_rb_95) {
                    environmentPojo.appKey = "955BF9BC78A5032107C4B74F4AB65702";
                } else {
                    environmentPojo.appKey = Config.APP_KEY;
                }
                environmentPojo.host = settingEtHost.getText().toString();
                environmentPojo.h5Host = settingEtH5.getText().toString();
                environmentPojo.encryption = settingRg.getCheckedRadioButtonId() == R.id.setting_rb_en;
                XApp.getEditor().putString("environment_setting", new Gson().toJson(environmentPojo)).apply();
                ToastUtil.showMessage(SettingActivity.this, "保存成功,请重启APP再试。");
                finish();
            }
        });

    }
}
