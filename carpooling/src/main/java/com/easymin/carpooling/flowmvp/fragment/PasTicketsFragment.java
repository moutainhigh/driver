package com.easymin.carpooling.flowmvp.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.easymi.common.entity.CarpoolOrder;
import com.easymi.component.base.RxBaseFragment;
import com.easymi.component.utils.PhoneUtil;
import com.easymin.carpooling.R;
import com.easymin.carpooling.StaticVal;
import com.easymin.carpooling.adapter.CusListAdapter;
import com.easymin.carpooling.flowmvp.ActFraCommBridge;

import java.util.List;

public class PasTicketsFragment extends RxBaseFragment {

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
     * activity和fragment的通信接口
     */
    private ActFraCommBridge bridge;

    /**
     * 设置必要参数
     *
     * @param bridge
     */
    public void setParam(ActFraCommBridge bridge) {
        this.bridge = bridge;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        if (null == args) {
            return;
        }
        orderId = args.getLong("orderId", 0);
        orderType = args.getString("orderType", "");
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_pas_tickets;
    }

    @Override
    public void finishCreateView(Bundle state) {
        recyclerView = $(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        cusListAdapter = new CusListAdapter(getActivity(), 0);
        cusListAdapter.setOnCallClickListener(new CusListAdapter.OnCallClickListener() {
            @Override
            public void onCallClick(CarpoolOrder order, int position) {
                order.isContract = 1;
                order.updateIsContract();
                showList();
                PhoneUtil.call(getActivity(), order.passengerPhone);
            }
        });


        cusListAdapter.setOnShowDialogListener(new CusListAdapter.OnDialogClickListener() {
            @Override
            public void onDialogClick(boolean isPay, long orderId) {
                bridge.onDialogClick(isPay, orderId);
            }
        });

        recyclerView.setAdapter(cusListAdapter);

        showList();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            showList();
        }
    }

    /**
     * 添加数据到适配器
     */
    private void showList() {
        carpoolOrderList = CarpoolOrder.findByIDTypeOrderByAcceptSeq(orderId, orderType);
        cusListAdapter.setOrderCustomers(carpoolOrderList);
        bridge.changeToolbar(StaticVal.TOOLBAR_PAS_TICKET);
    }
}
