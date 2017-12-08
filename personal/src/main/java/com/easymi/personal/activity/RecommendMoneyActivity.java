package com.easymi.personal.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.widget.CusErrLayout;
import com.easymi.component.widget.CusToolbar;
import com.easymi.personal.R;
import com.easymi.personal.adapter.RecommendMoneyAdapter;
import com.easymi.personal.entity.RecommendMoney;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by developerLzh on 2017/11/11 0011.
 */

public class RecommendMoneyActivity extends RxBaseActivity {

    RecyclerView recyclerView;

    RecommendMoneyAdapter adapter;

    CusErrLayout errLayout;

    CusToolbar cusToolbar;

    @Override
    public int getLayoutId() {
        return R.layout.activity_recommend_money;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        recyclerView = findViewById(R.id.recyclerView);
        errLayout = findViewById(R.id.cus_err_layout);
        adapter = new RecommendMoneyAdapter(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        List<RecommendMoney> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            RecommendMoney recommendMoney = new RecommendMoney();
            recommendMoney.money = 20.0;
            recommendMoney.time = "2017-11-11 17:13";
            list.add(recommendMoney);
        }
        adapter.setList(list);
    }

    @Override
    public void initToolBar() {
        cusToolbar = findViewById(R.id.cus_toolbar);
        cusToolbar.setLeftBack(view -> finish());
        cusToolbar.setTitle(R.string.recommend_title);
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
        errLayout.setText(R.string.no_recommend);
    }

    private void hideErr() {
        errLayout.setVisibility(View.GONE);
    }
}
