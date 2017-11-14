package com.easymi.personal.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.easymi.component.base.RxBaseActivity;
import com.easymi.personal.R;
import com.easymi.personal.adapter.RecommendAdapter;
import com.easymi.personal.entity.Recommend;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by developerLzh on 2017/11/9 0009.
 */

public class RecommendActivity extends RxBaseActivity {

    RecyclerView recyclerView;

    RecommendAdapter adapter;

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
}
