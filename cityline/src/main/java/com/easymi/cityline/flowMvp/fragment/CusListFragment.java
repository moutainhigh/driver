package com.easymi.cityline.flowMvp.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.easymi.cityline.R;
import com.easymi.cityline.StaticVal;
import com.easymi.cityline.adapter.CusListAdapter;
import com.easymi.cityline.entity.OrderCustomer;
import com.easymi.cityline.flowMvp.ActFraCommBridge;
import com.easymi.component.base.RxBaseFragment;
import com.easymi.component.entity.DymOrder;

import java.util.List;

/**
 * Created by liuzihao on 2018/11/15.
 */

public class CusListFragment extends RxBaseFragment {

    Button button;
    RecyclerView recyclerView;

    private long orderId;
    private String orderType;

    private CusListAdapter cusListAdapter;

    private List<OrderCustomer> orderCustomerList;

    private DymOrder dymOrder;

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

    private void showList() {
        if (flag == StaticVal.PLAN_ACCEPT) {
            orderCustomerList = OrderCustomer.findByIDTypeOrderByAcceptSeq(orderId, orderType);
        } else {
            orderCustomerList = OrderCustomer.findByIDTypeOrderBySendSeq(orderId, orderType);
        }
        cusListAdapter.setOrderCustomers(orderCustomerList);
    }
}
