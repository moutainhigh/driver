package com.easymi.cityline.flowMvp.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.easymi.cityline.R;
import com.easymi.cityline.StaticVal;
import com.easymi.cityline.adapter.SequenceAdapter;
import com.easymi.common.entity.OrderCustomer;
import com.easymi.cityline.entity.Sequence;
import com.easymi.cityline.flowMvp.ActFraCommBridge;
import com.easymi.cityline.widget.ItemDragCallback;
import com.easymi.component.ZXOrderStatus;
import com.easymi.component.base.RxBaseFragment;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by liuzihao on 2018/11/15.
 */

public class ChangeSeqFragment extends RxBaseFragment {

    long orderId;
    String orderType;

    TextView hintText;
    RecyclerView recyclerView;
    TextView bottomBtn;

    DymOrder dymOrder;

    List<OrderCustomer> orderCustomers;
    SequenceAdapter adapter;
    ItemTouchHelper itemTouchHelper;

    private int min = -1;//可排序的最小值
    private int max = -1;//可排序的最大值

    ActFraCommBridge bridge;

    private int flag;//规划接人或者送人
    private boolean countStratOver = false;

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
                    for (OrderCustomer orderCustomer : orderCustomers) {
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
//            adapter.setSequences(buildData());
            return false;
        });

        ItemDragCallback touchHelper = new ItemDragCallback(adapter);
        itemTouchHelper = new ItemTouchHelper(touchHelper);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        adapter.setItemTouchHelper(itemTouchHelper);

    }

    private List<Sequence> buildData() {
        List<Sequence> sequences = new ArrayList<>();

        min = 0;
        if (flag == StaticVal.PLAN_ACCEPT) {
            orderCustomers = OrderCustomer.findByIDTypeOrderByAcceptSeq(orderId, orderType);
            for (int i = 0; i < orderCustomers.size(); i++) {
                OrderCustomer customer = orderCustomers.get(i);
                Sequence sequence = new Sequence();
                sequence.num = customer.num;
                sequence.type = 1;
                sequence.text = "";
                sequences.add(sequence);
                if (customer.status != 0) {
                    min = i+1;
                }
            }
        } else {
            orderCustomers = OrderCustomer.findByIDTypeOrderBySendSeq(orderId, orderType);
            for (int i = 0; i < orderCustomers.size(); i++) {
                OrderCustomer customer = orderCustomers.get(i);
                Sequence sequence = new Sequence();
                sequence.num = customer.num;
                sequence.type = 1;
                sequence.text = "";
                sequences.add(sequence);
                if (customer.status > 3) {
                    min = i+1;
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

    private void showInMap() {
        if (bridge == null) {
            return;
        }
        bridge.clearMap();
        List<LatLng> latLngs = new ArrayList<>();
        for (OrderCustomer orderCustomer : orderCustomers) {
            LatLng latLng;
            if (flag == StaticVal.PLAN_ACCEPT) {
                latLng = new LatLng(orderCustomer.startLat, orderCustomer.startLng);
            } else {
                latLng = new LatLng(orderCustomer.endLat, orderCustomer.endLng);
            }
            if (orderCustomer.status == 0 || orderCustomer.status == 3) {
                bridge.addMarker(latLng, StaticVal.MARKER_FLAG_PASS_ENABLE, orderCustomer.num);
                latLngs.add(latLng);
            } else {
                bridge.addMarker(latLng, StaticVal.MARKER_FLAG_PASS_DISABLE, orderCustomer.num);
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
