package com.easymin.carpooling.flowmvp.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.easymi.common.entity.CarpoolOrder;
import com.easymi.component.base.RxBaseFragment;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.utils.PhoneUtil;
import com.easymin.carpooling.R;
import com.easymin.carpooling.StaticVal;
import com.easymin.carpooling.adapter.CusListAdapter;
import com.easymin.carpooling.flowmvp.ActFraCommBridge;

import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: CusListFragment
 *
 * @Author: hufeng
 * Date: 2018/12/24 下午1:10
 * Description: 客户列表界面
 * History:
 */
public class CusListFragment extends RxBaseFragment {

    TextView button;
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
        showList();
        CarpoolOrder current = null;
        for (int i = 0; i < carpoolOrderList.size(); i++) {
            CarpoolOrder carpoolOrder = carpoolOrderList.get(i);
            if (current == null) {
                if (carpoolOrder.customeStatus == 0 || carpoolOrder.customeStatus == 3) {
                    current = carpoolOrder;
                    break;
                }
            } else {
                if (current.customeStatus != 0 && current.customeStatus != 3) {
                    if (carpoolOrder.customeStatus == 0 || carpoolOrder.customeStatus == 3) {
                        current = carpoolOrder;
                        break;
                    }
                }
            }
            current = carpoolOrder;
            if (i == carpoolOrderList.size() - 1) {
                current = null;
            }
        }
        boolean isJie = false;
        if (flag == StaticVal.PLAN_ACCEPT) {
            bridge.changeToolbar(StaticVal.TOOLBAR_ACCEPT_ING);
            isJie = true;
        } else if (flag == StaticVal.PLAN_SEND) {
            bridge.changeToolbar(StaticVal.TOOLBAR_SEND_ING);
            isJie = false;
        }
        if (current != null) {
            button.setVisibility(View.VISIBLE);
            button.setText("正在"
                    + (isJie ? "接 " : "送 ")
                    + current.passengerName
                    + " "
                    + current.passengerPhone.replaceAll("(\\d{3})\\d{4}(\\d{4})",
                    "$1****$2"));
        } else {
            button.setVisibility(View.GONE);
        }
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
        return R.layout.cp_fragment_cus_list;
    }

    @Override
    public void finishCreateView(Bundle state) {
        button = $(R.id.bottom_btn);
        recyclerView = $(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        cusListAdapter = new CusListAdapter(getActivity(), 1);
        cusListAdapter.setOnCallClickListener((order, position) -> PhoneUtil.call(getActivity(), order.passengerPhone));
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
