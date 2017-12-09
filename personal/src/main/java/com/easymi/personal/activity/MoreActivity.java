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
 * Created by developerLzh on 2017/12/7 0007.
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
        stringList.add(getString(R.string.reli_pic));
        stringList.add(getString(R.string.weather_forecast));
        stringList.add(getString(R.string.contract_service));
        stringList.add("");
        stringList.add("");

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
}
