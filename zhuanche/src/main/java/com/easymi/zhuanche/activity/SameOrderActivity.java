package com.easymi.zhuanche.activity;

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
import com.easymi.zhuanche.R;
import com.easymi.zhuanche.adapter.SameOrderAdapter;
import com.easymi.zhuanche.entity.SameOrder;
import com.easymi.zhuanche.result.SameOrderResult;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by liuzihao on 2018/2/12.
 */

public class SameOrderActivity extends RxBaseActivity {

    private List<SameOrder> orderList = new ArrayList<>();

    SameOrderAdapter sameOrderAdapter;

    SwipeRecyclerView recyclerView;

    CusErrLayout errLayout;

    private String groupId;

    @Override
    public int getLayoutId() {
        return R.layout.zc_activity_same_order;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        recyclerView = findViewById(R.id.recyclerView);
        errLayout = findViewById(R.id.cus_err_layout);

        groupId = getIntent().getStringExtra("groupId");

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        sameOrderAdapter = new SameOrderAdapter(this);
        recyclerView.setAdapter(sameOrderAdapter);
        recyclerView.setLoadMoreEnable(false);

        recyclerView.setOnLoadListener(new SwipeRecyclerView.OnLoadListener() {
            @Override
            public void onRefresh() {
                getData(groupId);
            }

            @Override
            public void onLoadMore() {

            }
        });
        getData(groupId);
    }

    private void getData(String groupId) {

        Observable<SameOrderResult> observable = ApiManager.getInstance().createApi(Config.HOST, ZCApiService.class)
                .getSameOrderDriver(groupId, EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, false, true, new HaveErrSubscriberListener<SameOrderResult>() {
            @Override
            public void onNext(SameOrderResult sameOrderResult) {
                recyclerView.complete();
                orderList.clear();
                if (sameOrderResult.sameOrders != null) {
                    orderList = sameOrderResult.sameOrders;
                }
                sameOrderAdapter.setSameOrders(orderList);
                if (orderList.size() == 0) {
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

    @Override
    public void initToolBar() {
        CusToolbar cusToolbar = findViewById(R.id.cus_toolbar);
        cusToolbar.setLeftBack(view -> onBackPressed());
        cusToolbar.setTitle(R.string.same_order_driver);
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
