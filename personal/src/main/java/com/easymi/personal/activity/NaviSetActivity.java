package com.easymi.personal.activity;

import android.os.Bundle;

import com.easymi.component.Config;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.switchButton.SwitchButton;
import com.easymi.personal.R;

/**
 * Created by liuzihao on 2017/12/7.
 */

public class NaviSetActivity extends RxBaseActivity {
    CusToolbar cusToolbar;

    SwitchButton congestion;//躲避拥堵
    SwitchButton avoidhightspeed;//不走高速
    SwitchButton cost;//避免收费
    SwitchButton hightspeed;//高速优先

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

        congestion.setChecked(XApp.getMyPreferences().getBoolean(Config.SP_CONGESTION, true));
        avoidhightspeed.setChecked(XApp.getMyPreferences().getBoolean(Config.SP_AVOID_HIGH_SPEED, false));
        cost.setChecked(XApp.getMyPreferences().getBoolean(Config.SP_COST, true));
        hightspeed.setChecked(XApp.getMyPreferences().getBoolean(Config.SP_HIGHT_SPEED, false));

        congestion.setOnCheckedChangeListener((compoundButton, b) ->
                XApp.getMyPreferences().edit().putBoolean(Config.SP_CONGESTION, b).apply());
        avoidhightspeed.setOnCheckedChangeListener((compoundButton, b) ->
                XApp.getMyPreferences().edit().putBoolean(Config.SP_AVOID_HIGH_SPEED, b).apply());
        cost.setOnCheckedChangeListener((compoundButton, b) ->
                XApp.getMyPreferences().edit().putBoolean(Config.SP_COST, b).apply());
        hightspeed.setOnCheckedChangeListener((compoundButton, b) ->
                XApp.getMyPreferences().edit().putBoolean(Config.SP_HIGHT_SPEED, b).apply());
    }

    @Override
    public void initToolBar() {
        cusToolbar = findViewById(R.id.cus_toolbar);
        cusToolbar.setOnClickListener(view -> finish());
        cusToolbar.setTitle(R.string.set_navi);
    }
}
