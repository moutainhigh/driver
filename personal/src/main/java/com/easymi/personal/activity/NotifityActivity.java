package com.easymi.personal.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.easymi.component.base.RxBaseActivity;
import com.easymi.personal.R;
import com.easymi.personal.adapter.NotifityAdapter;
import com.easymi.personal.entity.Notifity;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by developerLzh on 2017/11/11 0011.
 */

public class NotifityActivity extends RxBaseActivity {

    PullLoadMoreRecyclerView recyclerView;

    NotifityAdapter adapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_notifity;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new NotifityAdapter(this);

        recyclerView.getRecyclerView().setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        recyclerView.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                initData();
            }

            @Override
            public void onLoadMore() {

            }
        });
    }

    private void initData() {
        recyclerView.setPullLoadMoreCompleted();
        List<Notifity> notifities = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Notifity notifity = new Notifity();
            notifity.isNew = true;
            notifity.message = "这是个测试消息这是个测试消息这是个测试消息这是个测试消息这是个测试消息";
            notifity.time = "2017-11-11 19:48";
            notifities.add(notifity);
        }
        adapter.setList(notifities);
    }
}
