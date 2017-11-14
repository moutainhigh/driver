package com.easymi.personal.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.easymi.component.base.RxBaseActivity;
import com.easymi.personal.R;
import com.easymi.personal.adapter.TixianRecordAdapter;
import com.easymi.personal.entity.TixianRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by developerLzh on 2017/11/11 0011.
 */

public class TixinaRecordActivity extends RxBaseActivity {

    RecyclerView recyclerView;

    TixianRecordAdapter adapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_tixian_record;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        recyclerView = findViewById(R.id.recyclerView);
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
}
