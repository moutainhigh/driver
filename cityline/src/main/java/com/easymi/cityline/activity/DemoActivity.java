package com.easymi.cityline.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.easymi.cityline.R;
import com.easymi.cityline.adapter.SequenceAdapter;
import com.easymi.cityline.entity.Sequence;
import com.easymi.cityline.widget.ItemDragCallback;
import com.easymi.component.base.RxBaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuzihao on 2018/11/14.
 */
@Route(path = "/cityline/DemoActivity")
public class DemoActivity extends RxBaseActivity {

    RecyclerView recyclerView;

    SequenceAdapter adapter;

    private ItemTouchHelper itemTouchHelper;

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_demo;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        recyclerView = findViewById(R.id.recycler_view);

        adapter = new SequenceAdapter(this);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(manager);

        recyclerView.setAdapter(adapter);

        ItemDragCallback touchHelper = new ItemDragCallback(adapter);
//        touchHelper.setSort(true);//默认关闭拖动排序
        itemTouchHelper = new ItemTouchHelper(touchHelper);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        adapter.setItemTouchHelper(itemTouchHelper);

        adapter.setSequences(buildData());

        adapter.setMinAndMax(2, 6);
    }

    private List<Sequence> buildData() {
        List<Sequence> datas = new ArrayList<>();

        Sequence data0 = new Sequence();
        data0.type = 3;
        datas.add(data0);

        for (int i = 0; i < 6; i++) {
            Sequence data1 = new Sequence();
            data1.type = 1;
            data1.num = i;
            datas.add(data1);
        }

        Sequence data2 = new Sequence();
        data2.type = 2;
        data2.text = "出城";
        datas.add(data2);

        return datas;
    }
}
