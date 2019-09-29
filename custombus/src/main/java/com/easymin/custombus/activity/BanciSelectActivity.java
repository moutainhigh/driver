package com.easymin.custombus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.easymi.component.Config;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.HttpResultFunc2;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.widget.CusErrLayout;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.SwipeRecyclerView;
import com.easymin.custombus.DZBusApiService;
import com.easymin.custombus.R;
import com.easymin.custombus.adapter.BanciAdapter;
import com.easymin.custombus.entity.DZBusLine;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class BanciSelectActivity extends RxBaseActivity {


    SwipeRecyclerView recyclerView;

    BanciAdapter adapter;

    CusToolbar toolbar;

    CusErrLayout errLayout;

    @Override
    public boolean isEnableSwipe() {
        return true;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_banci_select;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        recyclerView = findViewById(R.id.recyclerView);
        errLayout = findViewById(R.id.cus_err_layout);

        initRecycler();
        recyclerView.setRefreshing(true);
    }


    /**
     * 初始化列表
     */
    private void initRecycler() {
        adapter = new BanciAdapter(this);
        adapter.setOnItemClickListener(busLine -> {
            Intent intent = new Intent();
            intent.putExtra("data", busLine);
            setResult(RESULT_OK, intent);
            finish();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setLoadMoreEnable(false);
        recyclerView.setAdapter(adapter);
        recyclerView.setOnLoadListener(new SwipeRecyclerView.OnLoadListener() {
            @Override
            public void onRefresh() {
                queryOrders();
            }

            @Override
            public void onLoadMore() {
            }
        });
    }

    @Override
    public void initToolBar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("班次选择");
        toolbar.setLeftIcon(R.drawable.ic_arrow_back, v -> finish());
    }

    /**
     * 查询专线订单
     */
    private void queryOrders() {
        Observable<List<DZBusLine>> observable = ApiManager.getInstance().createApi(Config.HOST, DZBusApiService.class)
                .queryDriverSchedule(EmUtil.getEmployId())
                .map(new HttpResultFunc2<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        mRxManager.add(observable.subscribe(new MySubscriber<List<DZBusLine>>(this, false, false, new HaveErrSubscriberListener<List<DZBusLine>>() {
            @Override
            public void onNext(List<DZBusLine> cbBusOrders) {
                if (cbBusOrders == null || cbBusOrders.size() == 0) {
                    List<DZBusLine>  list =  new ArrayList<>();
                    adapter.setBaseOrders(list);
                    showErr(0);
                } else {
                    hideErr();
                    adapter.setBaseOrders(cbBusOrders);
                }
                recyclerView.complete();
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

    /**
     * 显示错误布局
     */
    private void hideErr() {
        errLayout.setVisibility(View.GONE);
    }
}
