package com.easymin.carpooling.flowmvp.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

import com.easymi.common.entity.CarpoolOrder;
import com.easymi.common.entity.OrderCustomer;
import com.easymi.component.base.RxBaseFragment;
import com.easymi.component.entity.DymOrder;
import com.easymin.carpooling.R;
import com.easymin.carpooling.StaticVal;
import com.easymin.carpooling.adapter.CusListAdapter;
import com.easymin.carpooling.flowmvp.ActFraCommBridge;

import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: CusListFragment
 * @Author: hufeng
 * Date: 2018/12/24 下午1:10
 * Description: 客户列表界面
 * History:
 */
public class CusListFragment extends RxBaseFragment {

    Button button;
    RecyclerView recyclerView;
    /**
     * 订单id，类型
     */
    private long orderId;
    private String orderType;

    /**
     * 客户适配器
     */
    private CusListAdapter cusListAdapter;

    /**
     * 客户数据集
     */
    private List<CarpoolOrder> carpoolOrderList;

    /**
     * 本地数据库订单数据
     */
    private DymOrder dymOrder;
    /**
     * activity和fragment的通信接口
     */
    private ActFraCommBridge bridge;

    private int flag;

    /**
     * 设置必要参数
     *
     * @param bridge
     * @param flag   接/送
     */
    public void setParam(ActFraCommBridge bridge, int flag) {
        this.bridge = bridge;
        this.flag = flag;
        changeUi();
    }

    /**
     * 显示ui
     */
    private void changeUi() {
        if (button == null) {
            return;
        }
        if (flag == StaticVal.PLAN_ACCEPT) {
            button.setText("开始接人");
            button.setOnClickListener(view -> bridge.toAcSend());
            bridge.changeToolbar(StaticVal.TOOLBAR_ACCEPT_ING);
        } else if (flag == StaticVal.PLAN_SEND) {
            button.setText("行程开始");
            button.setOnClickListener(view -> bridge.toAcSend());
            bridge.changeToolbar(StaticVal.TOOLBAR_SEND_ING);
        }
        showList();
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        if (null == args) {
            return;
        }
        orderId = args.getLong("orderId", 0);
        orderType = args.getString("orderType", "");
        dymOrder = DymOrder.findByIDType(orderId, orderType);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_cus_list;
    }

    @Override
    public void finishCreateView(Bundle state) {
        button = $(R.id.bottom_btn);
        recyclerView = $(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        cusListAdapter = new CusListAdapter(getActivity());
        recyclerView.setAdapter(cusListAdapter);

        changeUi();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            changeUi();
        }
    }

    /**
     * 添加数据到适配器
     */
    private void showList() {
        if (flag == StaticVal.PLAN_ACCEPT) {
            carpoolOrderList = CarpoolOrder.findByIDTypeOrderByAcceptSeq(orderId, orderType);
        } else {
            carpoolOrderList = CarpoolOrder.findByIDTypeOrderBySendSeq(orderId, orderType);
        }
        cusListAdapter.setOrderCustomers(carpoolOrderList);
    }
}
