package com.easymi.personal.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.widget.CusErrLayout;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.SwipeRecyclerView;
import com.easymi.personal.R;
import com.easymi.personal.adapter.TixianRecordAdapter;
import com.easymi.personal.entity.TixianRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by developerLzh on 2017/11/11 0011.
 */

public class TixianRecordActivity extends RxBaseActivity {

    SwipeRecyclerView recyclerView;

    CusErrLayout errLayout;

    TixianRecordAdapter adapter;

    CusToolbar toolbar;

    private int page = 0;
    private int limit = 10;

    @Override
    public void initToolBar() {
        toolbar = findViewById(R.id.cus_toolbar);
        toolbar.setLeftBack(view -> finish());
        toolbar.setTitle(R.string.tixian_record);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_tixian_record;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        recyclerView = findViewById(R.id.recyclerView);
        errLayout = findViewById(R.id.cus_err_layout);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        adapter = new TixianRecordAdapter(this);
        recyclerView.setAdapter(adapter);

        List<TixianRecord> recordList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            TixianRecord record = new TixianRecord();
            record.money = 20.0;
            record.status = "已通过";
            record.time = "2017-11-11 16:06";
            recordList.add(record);
        }

        adapter.setList(recordList);

    }

    private void queryData() {

    }

    /**
     * @param tag 0代表空数据  其他代表网络问题
     */
    private void showErr(int tag) {
        if (tag != 0) {
            errLayout.setErrText(tag);
            errLayout.setErrImg();
        }
        errLayout.setVisibility(View.VISIBLE);
        errLayout.setOnClickListener(v -> {
            hideErr();
            recyclerView.setRefreshing(true);
        });
    }

    private void hideErr() {
        errLayout.setVisibility(View.GONE);
    }
}
