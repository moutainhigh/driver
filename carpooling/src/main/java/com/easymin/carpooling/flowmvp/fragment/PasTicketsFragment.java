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
import com.easymin.carpooling.entity.AllStation;
import com.easymin.carpooling.entity.MyStation;
import com.easymin.carpooling.flowmvp.ActFraCommBridge;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
    private List<CarpoolOrder> carpoolOrderList = new ArrayList<>();

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
        orderType = args.getString("serviceType", "");
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
        cusListAdapter.setOnCallClickListener((order, position) -> {
            order.isContract = 1;
            order.updateIsContract();
            showList();
            PhoneUtil.call(getActivity(), order.passengerPhone);
        });


        cusListAdapter.setOnShowDialogListener((isPay, orderId, money) -> bridge.onDialogClick(isPay ? 3 : 2, orderId, money));

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

    AllStation allStation;

    /**
     * 传递数据
     *
     * @param allStation
     */
    public void setAllStation(AllStation allStation) {
        this.allStation = allStation;
    }

    /**
     * 添加数据到适配器
     */
    private void showList() {
        carpoolOrderList.clear();
        for (MyStation myStation : allStation.scheduleStationVoList) {
            if (myStation.stationOrderVoList != null && myStation.stationOrderVoList.size() != 0) {
                for (CarpoolOrder carpoolOrder : myStation.stationOrderVoList) {
                    carpoolOrderList.add(carpoolOrder);
                }
            }
        }

        cusListAdapter.setOrderCustomers(getList(carpoolOrderList));
        bridge.changeToolbar(StaticVal.TOOLBAR_PAS_TICKET, -1);
    }


    /**
     * 过滤重复数据
     * @param arr
     * @return
     */
    private ArrayList getList(List arr) {
        List list = new ArrayList();
        Iterator it = arr.iterator();
        while (it.hasNext()) {
            CarpoolOrder carpoolOrder = (CarpoolOrder) it.next();
            if (!list.contains(carpoolOrder)) {
                list.add(carpoolOrder);
            }
        }
        return (ArrayList) list;
    }

}
