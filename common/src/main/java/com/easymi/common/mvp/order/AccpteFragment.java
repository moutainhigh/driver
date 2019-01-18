package com.easymi.common.mvp.order;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.easymi.common.CommApiService;
import com.easymi.common.R;
import com.easymi.common.adapter.MyOrderAdapter;
import com.easymi.common.entity.MultipleOrder;
import com.easymi.common.result.QueryOrdersResult;
import com.easymi.component.Config;
import com.easymi.component.base.RxBaseFragment;
import com.easymi.component.entity.BaseOrder;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.network.NoErrSubscriberListener;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.widget.CusErrLayout;
import com.easymi.component.widget.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: AccpteFragment
 *@Author: shine
 * Date: 2018/11/15 下午4:05
 * Description:
 * History:
 * @author hufeng
 */
public class AccpteFragment extends RxBaseFragment implements MyOrderContract.View {

    SwipeRecyclerView recyclerView;
    CusErrLayout cus_err_layout;

    private ArrayList<MultipleOrder> list = new ArrayList<>();
    private MyOrderAdapter adapter;
    private int page = 1;
    private int size = 10;
    private MyOrderPresenter presenter;

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_base_order;
    }

    @Override
    public void finishCreateView(Bundle state) {
        recyclerView = $(R.id.recyclerView);
        cus_err_layout = $(R.id.cus_err_layout);

        initAdapter();
        initPresenter();
    }

    /**
     * 初始化presenter 请求数据
     */
    public void initPresenter() {
        presenter = new MyOrderPresenter(getContext(), this);
        setRefresh();
    }

    /**
     * 请求数据
     */
    public void setRefresh() {
        presenter.indexOrders(page, size, "10,15,20,25,28,30,35,40");
    }

    /**
     * 加载adapter
     */
    public void initAdapter() {
        adapter = new MyOrderAdapter(getContext(), 1);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        recyclerView.setOnLoadListener(new SwipeRecyclerView.OnLoadListener() {
            @Override
            public void onRefresh() {
                page = 1;
                setRefresh();
            }

            @Override
            public void onLoadMore() {
                page++;
                setRefresh();
            }
        });

        adapter.setItemClickListener((view, baseOrder) -> {
            if (view.getId() == R.id.root && StringUtils.isNotBlank(baseOrder.serviceType)) {
                if (baseOrder.status < 35) {
                    if (baseOrder.serviceType.equals(Config.ZHUANCHE)) {
                        ARouter.getInstance()
                                .build("/zhuanche/FlowActivity")
                                .withLong("orderId", baseOrder.id).navigation();
                    } else if (baseOrder.serviceType.equals(Config.TAXI)) {
                        ARouter.getInstance()
                                .build("/taxi/FlowActivity")
                                .withLong("orderId", baseOrder.id).navigation();
                    }
                }
            }
        });
    }


    @Override
    public void showOrders(List<MultipleOrder> MultipleOrders, int total) {
        recyclerView.complete();
        if (page == 1) {
            list.clear();
        }
        if (MultipleOrders != null) {
            list.addAll(MultipleOrders);
        }
        if (list.size() == 0) {
            cus_err_layout.setText(R.string.empty_hint);
            cus_err_layout.setVisibility(View.VISIBLE);
        } else {
            cus_err_layout.setVisibility(View.GONE);
        }
        if (total > page * 10) {
            recyclerView.setLoadMoreEnable(true);
        } else {
            recyclerView.setLoadMoreEnable(false);
            recyclerView.onNoMore(getResources().getString(R.string.lib_no_more_data));
        }
        adapter.setBaseOrders(list);
    }

    @Override
    public void doSuccesd() {
        setRefresh();
    }

    @Override
    public RxManager getRxManager() {
        return new RxManager();
    }
}
