package com.easymin.carpooling.flowmvp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.easymi.common.entity.CarpoolOrder;
import com.easymi.component.base.RxBaseFragment;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.Log;
import com.easymi.component.utils.ToastUtil;
import com.easymin.carpooling.R;
import com.easymin.carpooling.StaticVal;
import com.easymin.carpooling.adapter.SequenceAdapter;
import com.easymin.carpooling.entity.AllStation;
import com.easymin.carpooling.entity.MyStation;
import com.easymin.carpooling.entity.PincheOrder;
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
     * 排序站点的下标
     */
    private int flag;


    /**
     * 倒计时结束
     */
    private boolean countStratOver = false;

    /**
     * 班次的所有信息
     */
    private AllStation allStation;

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
     * @param flag   排序站点的下标
     */
    public void setParam(ActFraCommBridge bridge, int flag) {
        this.bridge = bridge;
        this.flag = flag;
        changeUi();
    }

    /**
     * 设置数据
     * @param allStation
     */
    public void setAllStation(AllStation allStation) {
        this.allStation = allStation;
    }

    public int getFlag(){
        return flag;
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

        bridge.changeToolbar(StaticVal.TOOLBAR_CHANGE_ACCEPT, flag);



        if(allStation.scheduleStatus == PincheOrder.SCHEDULE_STATUS_NEW){
            bottomBtn.setVisibility(View.VISIBLE);
        }else {
            bottomBtn.setVisibility(View.GONE);
        }

        if (flag == 0) {
            hintText.setText("接人路线规划：");
            bottomBtn.setText("下一步");

            bottomBtn.setOnClickListener(view -> {
                flag++;
                changeUi();
            });

        } else if (flag == allStation.scheduleStationVoList.size() - 1) {
            hintText.setText("送人路线规划：");
            bottomBtn.setText("行程开始");

            bottomBtn.setOnClickListener(view -> {
                bridge.startOutSet();
            });

        } else {
            hintText.setText("接送人路线规划：");
            bottomBtn.setText("下一步");

            bottomBtn.setOnClickListener(view -> {
                flag++;
                changeUi();
            });
        }


//        if (flag == StaticVal.PLAN_ACCEPT) {
//            bridge.changeToolbar(StaticVal.TOOLBAR_CHANGE_ACCEPT);
//            hintText.setText("接人路线规划：");
//            bottomBtn.setText("下一步");
//            bottomBtn.setOnClickListener(view -> {
//                flag = StaticVal.PLAN_SEND;
//                DymOrder dymOrder = DymOrder.findByIDType(orderId, orderType);
//                if (dymOrder != null && dymOrder.orderStatus == ZXOrderStatus.ACCEPT_PLAN) {
//                    dymOrder.orderStatus = ZXOrderStatus.SEND_PLAN;
//                    dymOrder.updateStatus();
//                }
//                changeUi();
//            });
//        } else {
//            bridge.changeToolbar(StaticVal.TOOLBAR_CHANGE_SEND);
//            hintText.setText("送人路线规划：");
//            bottomBtn.setText("行程开始");
//            DymOrder dymOrder = DymOrder.findByIDType(orderId, orderType);
//            if (null != dymOrder) {
//                if (dymOrder.orderStatus <= ZXOrderStatus.WAIT_START) {
//                    // TODO: 2019-08-30 暂时取消判断
////                    if (!countStratOver) {
////                        bottomBtn.setEnabled(false);
////                        bottomBtn.setBackgroundResource(R.drawable.corners_button_press_bg);
////                    } else {
//                    bottomBtn.setOnClickListener(view -> bridge.startOutSet());
////                    }
//                } else if (dymOrder.orderStatus == ZXOrderStatus.SEND_PLAN) {
//                    bottomBtn.setOnClickListener(view -> bridge.startOutSet());
//                } else {
//                    bottomBtn.setOnClickListener(view -> bridge.toAcSend());
//                }
//            }
//        }
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
            int i = 1;
            while (iterator.hasNext()) {
                Sequence sequence = (Sequence) iterator.next();
                if (sequence.type != 1) {
                    iterator.remove();//移除图片和出城
                } else {
                    for (CarpoolOrder orderCustomer : orderCustomers) {
                        if (orderCustomer.orderId == sequence.orderId) {
                            orderCustomer.beginIndex = i;
                        }
                    }
                    i++;
                }
            }
            //根据acceptSequence排序
            Collections.sort(orderCustomers, (o1, o2) -> {
                if (o1.beginIndex < o2.beginIndex) {
                    return -1;
                } else if (o1.beginIndex > o2.beginIndex) {
                    return 1;
                } else {
                    return 0;
                }
            });
            //将新顺序显示到地图上
            showInMap();

            //生成排序请求的字段
            String orderIdSequence = "";
            for (int index = 0; index < orderCustomers.size(); index++) {
                if (index == orderCustomers.size() - 1) {
                    orderIdSequence = orderIdSequence + orderCustomers.get(index).orderId + "-" + (index+1) +"-" +allStation.scheduleStationVoList.get(flag).stationId;
                } else {
                    orderIdSequence = orderIdSequence + orderCustomers.get(index).orderId + "-" + (index+1) +"-" +allStation.scheduleStationVoList.get(flag).stationId+  ",";
                }
            }
            //调用排序接口
            bridge.changeOrderSequence(orderIdSequence);
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

        MyStation currentStation = allStation.scheduleStationVoList.get(flag);
        orderCustomers = currentStation.stationOrderVoList;

        //根据index排序
        Collections.sort(orderCustomers, (o1, o2) -> {
            if (o1.index < o2.index) {
                return -1;
            } else if (o1.index > o2.index) {
                return 1;
            } else {
                return 0;
            }
        });

        for (int i = 0; i < orderCustomers.size(); i++) {
            CarpoolOrder customer = orderCustomers.get(i);
            customer.beginIndex = i;
            Sequence sequence = new Sequence();
            sequence.num = i;
            sequence.type = 1;
            sequence.text = "";
            sequence.orderId = customer.orderId;
            sequence.photo = customer.avatar;
            sequence.ticketNumber = customer.ticketNumber;
            sequence.status = customer.status;
            sequence.sort = customer.sequence;

            if (customer.startStationId == currentStation.stationId) {
                //上车点
                sequence.stationStatus = 1;

                if (customer.status >= CarpoolOrder.CARPOOL_STATUS_RUNNING) {
                    min = i + 1;
                }

            } else if (customer.endStationId == currentStation.stationId) {
                //下车点
                sequence.stationStatus = 2;

                if (customer.status >= CarpoolOrder.CARPOOL_STATUS_FINISH) {
                    min = i + 1;
                }

            } else {
                //途径点
                sequence.stationStatus = 3;
            }

            sequences.add(sequence);
        }

//        //根据acceptSequence排序
//        Collections.sort(sequences, (o1, o2) -> {
//            if (o1.beginIndex < o2.beginIndex) {
//                return -1;
//            } else if (o1.beginIndex > o2.beginIndex) {
//                return 1;
//            } else {
//                return 0;
//            }
//        });

        //车车
        Sequence car = new Sequence();
        car.type = 3;
        sequences.add(min, car);

        //出城
        Sequence data2 = new Sequence();
        data2.type = 2;
        data2.text = "出城";
        sequences.add(data2);

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
            LatLng latLng = null;
            if (orderCustomer.startStationId == allStation.scheduleStationVoList.get(flag).stationId) {
                latLng = new LatLng(orderCustomer.startLatitude, orderCustomer.startLongitude);
                bridge.addMarker(latLng, StaticVal.MARKER_FLAG_PASS_ENABLE, orderCustomer.sequence, orderCustomer.ticketNumber, orderCustomer.passengerPhone);
                latLngs.add(latLng);
            } else if (orderCustomer.endStationId == allStation.scheduleStationVoList.get(flag).stationId) {
                latLng = new LatLng(orderCustomer.endLatitude, orderCustomer.endLongitude);
                bridge.addMarker(latLng, StaticVal.MARKER_FLAG_PASS_DISABLE, orderCustomer.sequence, orderCustomer.ticketNumber, orderCustomer.passengerPhone);
                latLngs.add(latLng);
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
