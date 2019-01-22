package com.easymi.cityline.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.easymi.cityline.CLService;
import com.easymi.cityline.R;
import com.easymi.cityline.adapter.BanciAdapter;
import com.easymi.cityline.entity.ZXOrder;
import com.easymi.component.Config;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.HttpResultFunc3;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.result.EmResult2;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.CusErrLayout;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: FinishActivity
 * @Author: hufeng
 * Date: 2018/12/24 下午1:10
 * Description: 选择班次
 * History:
 */

public class BanciSelectActivity extends RxBaseActivity {

    SwipeRecyclerView recyclerView;

    BanciAdapter adapter;

    CusToolbar toolbar;

    CusErrLayout errLayout;

    private List<ZXOrder> orders;

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

        orders = new ArrayList<>();

        initToolBar();
        initRecycler();
        recyclerView.setRefreshing(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 初始化列表
     */
    private void initRecycler() {
        adapter = new BanciAdapter(this);
        adapter.setOnItemClickListener(zxOrder -> {
            if(zxOrder.status == ZXOrder.SCHEDULE_STATUS_RUN){
                ToastUtil.showMessage(BanciSelectActivity.this,"该次已出发，无法选择");
                return;
            }
            if(zxOrder.status == ZXOrder.SCHEDULE_STATUS_FINISH){
                ToastUtil.showMessage(BanciSelectActivity.this,"该次已完成，无法选择");
                return;
            }
            Intent intent= new Intent();
            intent.putExtra("zxOrder",zxOrder);
            setResult(RESULT_OK,intent);
            finish();
        });

        recyclerView.getRecyclerView().setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setLoadMoreEnable(false);
        recyclerView.setAdapter(adapter);
        recyclerView.setOnLoadListener(new SwipeRecyclerView.OnLoadListener() {
            @Override
            public void onRefresh() {
                queryOrders();
            }

            @Override
            public void onLoadMore() {
                queryOrders();
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
        Observable<EmResult2<List<ZXOrder>>> observable = ApiManager.getInstance().createApi(Config.HOST, CLService.class)
                .queryDriverSchedule()
                .filter(new HttpResultFunc3<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(BanciSelectActivity.this,
                false,
                false,
                new HaveErrSubscriberListener<EmResult2<List<ZXOrder>>>() {
                    @Override
                    public void onNext(EmResult2<List<ZXOrder>> result2) {
                        recyclerView.complete();
                        orders.clear();
                        orders.addAll(result2.getData() == null ? new ArrayList<>() : result2.getData());
                        adapter.setBaseOrders(result2.getData());

                        recyclerView.setLoadMoreEnable(false);

                        if (orders.size() == 0) {
                            showErr(0);
                        } else {
                            hideErr();
                        }
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
