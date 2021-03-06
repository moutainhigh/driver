package com.easymi.personal.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.easymi.component.Config;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.widget.CusErrLayout;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.SwipeRecyclerView;
import com.easymi.personal.McService;
import com.easymi.personal.R;
import com.easymi.personal.adapter.TixianRecordAdapter;
import com.easymi.personal.entity.TixianRecord;
import com.easymi.personal.result.TixianResult;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: TixianRecordActivity
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description: 提现记录
 * History:
 */

public class TixianRecordActivity extends RxBaseActivity {

    SwipeRecyclerView recyclerView;
    CusErrLayout errLayout;
    CusToolbar toolbar;

    /**
     * 适配器
     */
    TixianRecordAdapter adapter;

    private int page = 1;
    private int limit = 10;

    /**
     * 提现记录数据集
     */
    private List<TixianRecord> recordList = new ArrayList<>();

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

        recyclerView.setOnLoadListener(new SwipeRecyclerView.OnLoadListener() {
            @Override
            public void onRefresh() {
                page = 1;
                queryData();
            }

            @Override
            public void onLoadMore() {
                page++;
                queryData();
            }
        });

        recyclerView.setRefreshing(true);
    }

    /**
     * 获取提现记录
     */
    private void queryData() {
        Observable<TixianResult> observable = ApiManager.getInstance().createApi(Config.HOST, McService.class)
                .enchashments(page,limit)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, false, true, new HaveErrSubscriberListener<TixianResult>() {
            @Override
            public void onNext(TixianResult tixianResult) {
                recyclerView.complete();
                if (page == 1) {
                    recordList.clear();
                }

                if (tixianResult.tixianRecords == null) {
                    showErr(0);
                } else {
                    hideErr();
                    recordList.addAll(tixianResult.tixianRecords);
                    if (tixianResult.total > page * 10) {
                        recyclerView.setLoadMoreEnable(true);
                    } else {
                        recyclerView.setLoadMoreEnable(false);
                    }
                }
                adapter.setList(recordList);

            }

            @Override
            public void onError(int code) {
                recyclerView.complete();
                showErr(code);
            }
        })));
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

    @Override
    public boolean isEnableSwipe() {
        return true;
    }
}
