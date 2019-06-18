package com.easymi.personal.activity;

import android.os.Bundle;

import com.easymi.component.Config;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.utils.CsEditor;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.switchButton.SwitchButton;
import com.easymi.personal.R;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: NaviSetActivity
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description: 导航设置界面
 * History:
 */

public class NaviSetActivity extends RxBaseActivity {
    CusToolbar cusToolbar;
    /**
     * 躲避拥堵
     */
    SwitchButton congestion;
    /**
     * 不走高速
     */
    SwitchButton avoidhightspeed;
    /**
     * 避免收费
     */
    SwitchButton cost;
    /**
     * 高速优先
     */
    SwitchButton hightspeed;

    @Override
    public int getLayoutId() {
        return R.layout.activity_navi_set;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        congestion = findViewById(R.id.congestion);
        avoidhightspeed = findViewById(R.id.avoidhightspeed);
        cost = findViewById(R.id.cost);
        hightspeed = findViewById(R.id.hightspeed);

        congestion.setChecked(XApp.getMyPreferences().getBoolean(Config.SP_CONGESTION, false));
        avoidhightspeed.setChecked(XApp.getMyPreferences().getBoolean(Config.SP_AVOID_HIGH_SPEED, false));
        cost.setChecked(XApp.getMyPreferences().getBoolean(Config.SP_COST, false));
        hightspeed.setChecked(XApp.getMyPreferences().getBoolean(Config.SP_HIGHT_SPEED, false));

        congestion.setOnCheckedChangeListener((compoundButton, b) ->
                XApp.getEditor().putBoolean(Config.SP_CONGESTION, b).apply());
        avoidhightspeed.setOnCheckedChangeListener((compoundButton, b) ->
                XApp.getEditor().putBoolean(Config.SP_AVOID_HIGH_SPEED, b).apply());
        cost.setOnCheckedChangeListener((compoundButton, b) ->
                XApp.getEditor().putBoolean(Config.SP_COST, b).apply());
        hightspeed.setOnCheckedChangeListener((compoundButton, b) ->
                XApp.getEditor().putBoolean(Config.SP_HIGHT_SPEED, b).apply());
    }

    @Override
    public void initToolBar() {
        cusToolbar = findViewById(R.id.cus_toolbar);
        cusToolbar.setOnClickListener(view -> finish());
        cusToolbar.setTitle(R.string.set_navi);
    }

    @Override
    public boolean isEnableSwipe() {
        return true;
    }
}
