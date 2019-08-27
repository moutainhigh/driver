package com.easymin.carpooling.flowmvp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MotionEvent;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.easymi.common.entity.CarpoolOrder;
import com.easymi.component.ZXOrderStatus;
import com.easymi.component.base.RxBaseFragment;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.utils.EmUtil;
import com.easymin.carpooling.R;
import com.easymin.carpooling.StaticVal;
import com.easymin.carpooling.adapter.SequenceAdapter;
import com.easymin.carpooling.entity.Sequence;
import com.easymin.carpooling.flowmvp.ActFraCommBridge;
import com.easymin.carpooling.widget.ItemDragCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: ChangeSeqFragment
 *
 * @Author: hufeng
 * Date: 2018/12/24 下午1:10
 * Description: 接送排序界面
 * History:
 */

public class ChangeSeqFragment extends RxBaseFragment {

    /**
     * 订单id，类型
     */
    long orderId;
    String orderType;

    TextView hintText;
    RecyclerView recyclerView;
    TextView bottomBtn;

    /**
     * 本地数据库动态订单
     */
    DymOrder dymOrder;

    /**
     * 订单数据集
     */
    List<CarpoolOrder> orderCustomers;
    /**
     * 排序适配器
     */
    SequenceAdapter adapter;
    /**
     * 拖动帮助类
     */
    ItemTouchHelper itemTouchHelper;
    /**
     * 可排序的最小值
     */
    private int min = -1;
    /**
     * 可排序的最大值
     */
    private int max = -1;

    /**
     * 通信接口
     */
    ActFraCommBridge bridge;

    /**
     * 规划接人或者送人
     */
    private int flag;
    /**
     * 倒计时结束
     */
    private boolean countStratOver = false;

    /**
     * 倒计时结束操作
     *
     * @param countStratOver
     */
    public void setCountStratOver(boolean countStratOver) {
        this.countStratOver = countStratOver;
        changeUi();
    }

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
     * 根据接人送人状态展示对应信息
     */
    private void changeUi() {
        if (null == hintText) {
            return;
        }
        bottomBtn.setEnabled(true);
        bottomBtn.setBackgroundResource(R.drawable.corners_button_selector);
        if (flag == StaticVal.PLAN_ACCEPT) {
            bridge.changeToolbar(StaticVal.TOOLBAR_CHANGE_ACCEPT);
            hintText.setText("接人路线规划：");
            bottomBtn.setText("下一步");
            bottomBtn.setOnClickListener(view -> {
                flag = StaticVal.PLAN_SEND;
                DymOrder dymOrder = DymOrder.findByIDType(orderId, orderType);
                if (dymOrder != null && dymOrder.orderStatus == ZXOrderStatus.ACCEPT_PLAN) {
                    dymOrder.orderStatus = ZXOrderStatus.SEND_PLAN;
                    dymOrder.updateStatus();
                }
                changeUi();
            });
        } else {
            bridge.changeToolbar(StaticVal.TOOLBAR_CHANGE_SEND);
            hintText.setText("送人路线规划：");
            bottomBtn.setText("行程开始");
            DymOrder dymOrder = DymOrder.findByIDType(orderId, orderType);
            if (null != dymOrder) {
                if (dymOrder.orderStatus <= ZXOrderStatus.WAIT_START) {
                    if (!countStratOver) {
                        bottomBtn.setEnabled(false);
                        bottomBtn.setBackgroundResource(R.drawable.corners_button_press_bg);
                    } else {
                        bottomBtn.setOnClickListener(view -> bridge.startOutSet());
                    }
                } else if (dymOrder.orderStatus == ZXOrderStatus.SEND_PLAN) {
                    bottomBtn.setOnClickListener(view -> bridge.startOutSet());
                } else {
                    bottomBtn.setOnClickListener(view -> bridge.toAcSend());
                }
            }

        }
        adapter.setSequences(buildData());
        showInMap();
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        if (null == args) {
            return;
        }
        orderId = args.getLong("orderId", 0);
        orderType = args.getString("orderType", "");

        dymOrder = DymOrder.findByIDType(orderId, orderType);

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            changeUi();
        }
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragemnt_sequence;
    }

    @Override
    public void finishCreateView(Bundle state) {
        hintText = $(R.id.hint_text);
        recyclerView = $(R.id.recycler_view);
        bottomBtn = $(R.id.bottom_btn);

        initRecycler();
        changeUi();
    }

    /**
     * 初始化列表
     */
    private void initRecycler() {
        adapter = new SequenceAdapter(getActivity());
        LinearLayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(manager);

        recyclerView.setAdapter(adapter);

        recyclerView.setOnTouchListener((view, event) -> {
            if (event.getAction() != MotionEvent.ACTION_UP) {
                return false;
            }
            List<Sequence> list = new ArrayList<>();
            list.addAll(adapter.getLists());
            Iterator iterator = list.iterator();
            int i = 0;
            while (iterator.hasNext()) {
                Sequence sequence = (Sequence) iterator.next();
                if (sequence.type != 1) {
                    iterator.remove();//移除图片和出城
                } else {
                    for (CarpoolOrder orderCustomer : orderCustomers) {
                        if (orderCustomer.num == sequence.num) {
                            if (flag == StaticVal.PLAN_ACCEPT) {
                                orderCustomer.acceptSequence = i;
                                orderCustomer.updateAcceptSequence();
                            } else {
                                orderCustomer.sendSequence = i;
                                orderCustomer.updateSendSequence();
                            }

                        }
                    }
                    i++;
                }
            }
            if (flag == StaticVal.PLAN_ACCEPT) {
                //根据acceptSequence排序
                Collections.sort(orderCustomers, (o1, o2) -> {
                    if (o1.acceptSequence < o2.acceptSequence) {
                        return -1;
                    } else if (o1.acceptSequence > o2.acceptSequence) {
                        return 1;
                    } else {
                        return 0;
                    }
                });
            } else {
                //根据acceptSequence排序
                Collections.sort(orderCustomers, (o1, o2) -> {
                    if (o1.sendSequence < o2.sendSequence) {
                        return -1;
                    } else if (o1.sendSequence > o2.sendSequence) {
                        return 1;
                    } else {
                        return 0;
                    }
                });
            }
            showInMap();
            return false;
        });

        ItemDragCallback touchHelper = new ItemDragCallback(adapter);
        itemTouchHelper = new ItemTouchHelper(touchHelper);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        adapter.setItemTouchHelper(itemTouchHelper);

    }

    /**
     * 根据状态构造数据
     *
     * @return
     */
    private List<Sequence> buildData() {
        List<Sequence> sequences = new ArrayList<>();

        min = 0;
        if (flag == StaticVal.PLAN_ACCEPT) {
            orderCustomers = CarpoolOrder.findByIDTypeOrderByAcceptSeq(orderId, orderType);
            for (int i = 0; i < orderCustomers.size(); i++) {
                CarpoolOrder customer = orderCustomers.get(i);
                Sequence sequence = new Sequence();
                sequence.num = customer.num;
                sequence.type = 1;
                sequence.text = "";
                sequence.photo = customer.avatar;
                sequence.ticketNumber = customer.ticketNumber;
                sequence.status = customer.customeStatus;

                sequences.add(sequence);
                if (customer.customeStatus != 0) {
                    min = i + 1;
                }
            }
        } else {
            orderCustomers = CarpoolOrder.findByIDTypeOrderBySendSeq(orderId, orderType);
            for (int i = 0; i < orderCustomers.size(); i++) {
                CarpoolOrder customer = orderCustomers.get(i);
                Sequence sequence = new Sequence();
                sequence.num = customer.num;
                sequence.type = 1;
                sequence.text = "";
                sequence.photo = customer.avatar;
                sequence.ticketNumber = customer.ticketNumber;
                sequence.status = customer.customeStatus;

                sequences.add(sequence);
                if (customer.customeStatus > 3) {
                    min = i + 1;
                }
            }
        }

        Sequence car = new Sequence();
        car.type = 3;
        sequences.add(min, car);//车车

        Sequence data2 = new Sequence();
        data2.type = 2;
        data2.text = "出城";
        sequences.add(data2);//出城

        max = sequences.size() - 1;

        adapter.setMinAndMax(min, max);

        return sequences;
    }

    /**
     * 显示路径规划在地图上
     */
    private void showInMap() {
        if (bridge == null) {
            return;
        }
        bridge.clearMap();
        List<LatLng> latLngs = new ArrayList<>();
        for (CarpoolOrder orderCustomer : orderCustomers) {
            LatLng latLng;
            if (flag == StaticVal.PLAN_ACCEPT) {
                latLng = new LatLng(orderCustomer.startLat, orderCustomer.startLng);
            } else {
                latLng = new LatLng(orderCustomer.endLat, orderCustomer.endLng);
            }
            if (orderCustomer.customeStatus == 0 || orderCustomer.customeStatus == 3) {
                bridge.addMarker(latLng, StaticVal.MARKER_FLAG_PASS_ENABLE, orderCustomer.num, orderCustomer.ticketNumber, orderCustomer.avatar);
                latLngs.add(latLng);
            } else {
                bridge.addMarker(latLng, StaticVal.MARKER_FLAG_PASS_DISABLE, orderCustomer.num, orderCustomer.ticketNumber, orderCustomer.avatar);
            }
        }
        bridge.showBounds(latLngs);
        if (latLngs.size() != 0) {
            LatLng endLatlng = latLngs.remove(latLngs.size() - 1);
            bridge.routePath(new LatLng(EmUtil.getLastLoc().latitude, EmUtil.getLastLoc().longitude),
                    latLngs, endLatlng);
        }
    }
}
