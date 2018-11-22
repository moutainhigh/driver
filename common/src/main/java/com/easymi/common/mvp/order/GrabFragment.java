package com.easymi.common.mvp.order;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.easymi.common.R;
import com.easymi.common.adapter.MyOrderAdapter;
import com.easymi.component.base.RxBaseFragment;
import com.easymi.component.widget.CusErrLayout;
import com.easymi.component.widget.SwipeRecyclerView;

import java.util.ArrayList;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: GrabFragment
 * Author: shine
 * Date: 2018/11/15 下午4:06
 * Description:
 * History:
 */
public class GrabFragment extends RxBaseFragment{

    SwipeRecyclerView recyclerView;
    CusErrLayout cus_err_layout;

    private ArrayList<String> list = new ArrayList<>();
    private MyOrderAdapter adapter;
    @Override
    public int getLayoutResId() {
        return R.layout.fragment_base_order;
    }

    @Override
    public void finishCreateView(Bundle state) {
        recyclerView = $(R.id.recyclerView);
        cus_err_layout = $(R.id.cus_err_layout);

        list.clear();
        list.add("");
        list.add("");
        list.add("");
        adapter = new MyOrderAdapter(getContext());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setBaseOrders(list);
    }
}
