package com.easymi.personal.activity;

import android.os.Bundle;
import android.widget.GridView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.widget.CusToolbar;
import com.easymi.personal.R;
import com.easymi.personal.adapter.GridAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: FinishActivity
 * Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */
@Route(path = "/personal/MoreActivity")
public class MoreActivity extends RxBaseActivity {

    CusToolbar cusToolbar;

    GridView gridView;

    @Override
    public int getLayoutId() {
        return R.layout.activity_more;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        gridView = findViewById(R.id.grid_view);
        List<String> stringList = new ArrayList<>();
        stringList.add(getString(R.string.near_wc));
//        stringList.add(getString(R.string.reli_pic));
        stringList.add(getString(R.string.help_center));
        stringList.add(getString(R.string.weather_forecast));
//        stringList.add(getString(R.string.contract_service));
        stringList.add(getString(R.string.sys_check));
//        stringList.add("");
//        stringList.add("");

        GridAdapter gridAdapter = new GridAdapter(this);
        gridAdapter.setStringList(stringList);
        gridView.setAdapter(gridAdapter);
    }

    @Override
    public void initToolBar() {
        cusToolbar = findViewById(R.id.cus_toolbar);
        cusToolbar.setTitle(R.string.more);
        cusToolbar.setLeftBack(v -> finish());
    }

    @Override
    public boolean isEnableSwipe() {
        return true;
    }
}
