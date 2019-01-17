package com.easymi.personal.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.widget.CusToolbar;
import com.easymi.personal.R;
import com.easymi.personal.adapter.RecommendAdapter;
import com.easymi.personal.entity.Recommend;

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


public class RecommendActivity extends RxBaseActivity {

    RecyclerView recyclerView;

    RecommendAdapter adapter;

    CusToolbar cusToolbar;

    @Override
    public void initToolBar() {
        super.initToolBar();
        cusToolbar = findViewById(R.id.cus_toolbar);
        cusToolbar.setLeftBack(view -> finish());
        cusToolbar.setTitle(R.string.recommend_person);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_recommend;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new RecommendAdapter(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        List<Recommend> recommends = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Recommend recommend = new Recommend();
            recommend.name = "name";
            recommend.phone = "15102875535";
            recommend.time = "2017-10-25 10:25";
            recommends.add(recommend);
        }
        adapter.setList(recommends);
    }

    @Override
    public boolean isEnableSwipe() {
        return true;
    }
}
