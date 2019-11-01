package com.easymin.carpooling.flowmvp.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.easymi.common.entity.CarpoolOrder;
import com.easymi.component.Config;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseFragment;
//import com.easymi.component.entity.DymOrder;
import com.easymi.component.entity.ZCSetting;
import com.easymi.component.utils.GlideCircleTransform;
import com.easymi.component.utils.Log;
import com.easymi.component.utils.PhoneUtil;
import com.easymi.component.widget.CustomSlideToUnlockView;
import com.easymi.component.widget.LoadingButton;
import com.easymin.carpooling.R;
import com.easymin.carpooling.StaticVal;
import com.easymin.carpooling.entity.AllStation;
import com.easymin.carpooling.entity.MyStation;
import com.easymin.carpooling.flowmvp.ActFraCommBridge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: AcceptSendFragment
 *
 * @Author: hufeng
 * Date: 2018/12/24 下午1:10
 * Description: 接送客户界面
 * History:
 */

public class AcceptSendFragment extends RxBaseFragment {

    ImageView customerPhoto;
    TextView customerName;
    TextView customerPhone;
    ImageButton callPhone;
    TextView toPlace;
    ImageView naviBtn;
    LinearLayout countTimeCon;
    TextView countHint;
    TextView countTime;
    CustomSlideToUnlockView slider;
    LinearLayout sliderCon;
    LinearLayout chaoshiCon;
    Button jumpBtn;
    Button acceptedBtn;
    ImageView refreshImg;
    TextView back;

    LinearLayout ll_btn_con;
    TextView btn_back;
    LoadingButton btn_sure;

    /**
     * 订单id，类型
     */
    long orderId;
    String orderType;

    /**
     * 专线订单集
     */
    List<CarpoolOrder> carpoolOrders = new ArrayList<>();

    /**
     * 通信桥
     */
    ActFraCommBridge bridge;

    /**
     * 当前order
     */
    private CarpoolOrder current;
    private TextView tvDesc;
    private LinearLayout mainLlAction;
    private TextView mainCancel;
    private TextView mainPay;

    /**
     * 设置bridge
     *
     * @param bridge
     */
    public void setBridge(ActFraCommBridge bridge) {
        this.bridge = bridge;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        if (args == null) {
            return;
        }
        orderId = args.getLong("orderId", 0);
        orderType = args.getString("serviceType", "");
    }

    @Override
    public int getLayoutResId() {
        return R.layout.car_pool_fragment_accep_send;
    }

    @Override
    public void finishCreateView(Bundle state) {
        customerPhoto = getActivity().findViewById(R.id.customer_photo);
        customerName = getActivity().findViewById(R.id.customer_name);
        customerPhone = getActivity().findViewById(R.id.customer_phone);
        callPhone = getActivity().findViewById(R.id.img_call_phone);
        toPlace = getActivity().findViewById(R.id.to_place);
        naviBtn = getActivity().findViewById(R.id.navi_view);
        countTimeCon = getActivity().findViewById(R.id.count_time_con);
        countHint = getActivity().findViewById(R.id.count_hint);
        countTime = getActivity().findViewById(R.id.count_time);
        slider = getActivity().findViewById(R.id.slider);
        refreshImg = getActivity().findViewById(R.id.ic_refresh_cp);
        jumpBtn = getActivity().findViewById(R.id.jump_accept);
        acceptedBtn = getActivity().findViewById(R.id.accept_cus);
        chaoshiCon = getActivity().findViewById(R.id.chaoshi_con);
        back = getActivity().findViewById(R.id.tv_back);
        sliderCon = getActivity().findViewById(R.id.slider_con);
        tvDesc = getActivity().findViewById(R.id.tv_desc);
        mainLlAction = getActivity().findViewById(R.id.main_ll_action);
        mainCancel = getActivity().findViewById(R.id.main_cancel);
        mainPay = getActivity().findViewById(R.id.main_pay);

        ll_btn_con = getActivity().findViewById(R.id.ll_btn_con);
        btn_back = getActivity().findViewById(R.id.btn_back);
        btn_sure = getActivity().findViewById(R.id.btn_sure);

        refreshImg.setOnClickListener(v -> {
            bridge.doRefresh();
            refreshImg.setVisibility(View.GONE);
        });
        showWhatByStatus();
    }

    /**
     * 等待计时器
     */
    private Timer timer;
    private TimerTask timerTask;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cancelTimer();
    }

    /**
     * 取消计时器
     */
    public void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }


    public AllStation allStation;

    public void setOrders(AllStation allStation) {
        this.allStation = allStation;
        if (mainLlAction == null) {
            return;
        }
        showWhatByStatus();
    }

    /**
     * 获取当前执行站点的下标
     */
    public int getCurrentIndex() {
        int index = 0;
        for (int i = 0; i < allStation.scheduleStationVoList.size(); i++) {
            if (allStation.currentStationId == allStation.scheduleStationVoList.get(i).stationId) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * 根据订单状态显示对应数据
     */
    public void showWhatByStatus() {
        cancelTimer();
        MyStation myStation = allStation.scheduleStationVoList.get(getCurrentIndex());

        carpoolOrders.clear();
        carpoolOrders.addAll(myStation.stationOrderVoList);

        //根据index排序
        Collections.sort(carpoolOrders, (o1, o2) -> {
            if (o1.index < o2.index) {
                return -1;
            } else if (o1.index > o2.index) {
                return 1;
            } else {
                return 0;
            }
        });

        Iterator iterator = carpoolOrders.iterator();

        while (iterator.hasNext()) {
            CarpoolOrder carpoolOrder = (CarpoolOrder) iterator.next();
            if (carpoolOrder.startStationId == myStation.stationId && carpoolOrder.status > CarpoolOrder.CARPOOL_STATUS_ARRIVED) {
                iterator.remove();
            } else if (carpoolOrder.endStationId == myStation.stationId && carpoolOrder.status > CarpoolOrder.CARPOOL_STATUS_RUNNING) {
                iterator.remove();
            }
        }

        mainLlAction.setVisibility(View.GONE);
        if (carpoolOrders.size() != 0) {
            current = carpoolOrders.get(0);

            tvDesc.setText(!TextUtils.isEmpty(current.orderRemark) ? current.orderRemark : "暂无备注");

            if (current.advanceAssign == 1) {
                mainLlAction.setVisibility(View.VISIBLE);
                mainCancel.setOnClickListener(v -> bridge.onDialogClick(2, current.orderId, current.money));
                mainPay.setOnClickListener(v -> bridge.onDialogClick(3, current.orderId, current.money));
            } else {
                mainLlAction.setVisibility(View.GONE);
            }
            if (current.status < CarpoolOrder.CARPOOL_STATUS_RUNNING) {
                //未接
                if (current.status < CarpoolOrder.CARPOOL_STATUS_START) {
                    countTimeCon.setVisibility(View.GONE);
                    sliderCon.setVisibility(ZCSetting.findOne().operationMode == 1 ? View.VISIBLE : View.GONE);
                    ll_btn_con.setVisibility(ZCSetting.findOne().operationMode == 2 ? View.VISIBLE : View.GONE);
                    chaoshiCon.setVisibility(View.GONE);
                    back.setVisibility(View.GONE);
                    btn_back.setVisibility(View.GONE);

                    if (ZCSetting.findOne().operationMode == 1){
                        slider.setHint("滑动前往预约地");
                        slider.setmCallBack(new CustomSlideToUnlockView.CallBack() {
                            @Override
                            public void onSlide(int distance) {

                            }

                            @Override
                            public void onUnlocked() {
                                bridge.gotoStart(current);
                                resetView();
                            }
                        });
                    }else {
                        btn_sure.setText("前往预约地");
                        btn_sure.setOnClickListener(v -> {
                            bridge.gotoStart(current);
                        });
                    }
                } else if (current.status == CarpoolOrder.CARPOOL_STATUS_START) {
                    //未到达预约地
                    countTimeCon.setVisibility(View.GONE);
                    sliderCon.setVisibility(ZCSetting.findOne().operationMode == 1 ? View.VISIBLE : View.GONE);
                    ll_btn_con.setVisibility(ZCSetting.findOne().operationMode == 2 ? View.VISIBLE : View.GONE);
                    chaoshiCon.setVisibility(View.GONE);
                    back.setVisibility(View.GONE);
                    btn_back.setVisibility(View.GONE);

                    if (ZCSetting.findOne().operationMode == 1){
                        slider.setHint("滑动到达乘客位置");
                        slider.setmCallBack(new CustomSlideToUnlockView.CallBack() {
                            @Override
                            public void onSlide(int distance) {

                            }

                            @Override
                            public void onUnlocked() {
                                bridge.arriveStart(current);
                                resetView();
                            }
                        });
                    }else {
                        btn_sure.setText("到达乘客位置");
                        btn_sure.setOnClickListener(v -> {
                            bridge.arriveStart(current);
                        });
                    }
                } else if (current.status == CarpoolOrder.CARPOOL_STATUS_ARRIVED) {
                    //到达预约地
                    countTimeCon.setVisibility(View.VISIBLE);
                    sliderCon.setVisibility(View.GONE);
                    ll_btn_con.setVisibility(View.GONE);

                    chaoshiCon.setVisibility(View.GONE);
                    back.setVisibility(View.GONE);
                    btn_back.setVisibility(View.GONE);

                    if (ZCSetting.findOne().operationMode == 1){
                        slider.setHint("滑动确认接到乘客");
                        slider.setmCallBack(new CustomSlideToUnlockView.CallBack() {
                            @Override
                            public void onSlide(int distance) {

                            }

                            @Override
                            public void onUnlocked() {
                                if (current.advanceAssign == 1) {
                                    bridge.onDialogClick(1, current.orderId, current.money);
                                } else {
                                    bridge.acceptCustomer(current);
                                }
                                resetView();
                            }
                        });
                    }else {
                        btn_sure.setText("确认接到乘客");
                        btn_sure.setOnClickListener(v -> {
                            if (current.advanceAssign == 1) {
                                bridge.onDialogClick(1, current.orderId, current.money);
                            } else {
                                bridge.acceptCustomer(current);
                            }
                        });
                    }
                    initTimer(current);
                }
                showInMap(new LatLng(current.startLatitude, current.startLongitude), StaticVal.MARKER_FLAG_START);
            } else if (current.status == CarpoolOrder.CARPOOL_STATUS_RUNNING) {
                countTimeCon.setVisibility(View.GONE);
                sliderCon.setVisibility(ZCSetting.findOne().operationMode == 1 ? View.VISIBLE : View.GONE);
                ll_btn_con.setVisibility(ZCSetting.findOne().operationMode == 2 ? View.VISIBLE : View.GONE);
                chaoshiCon.setVisibility(View.GONE);
                back.setVisibility(View.GONE);
                btn_back.setVisibility(View.GONE);

                if (ZCSetting.findOne().operationMode == 1){
                    slider.setHint("滑动到达下车点");
                    slider.setmCallBack(new CustomSlideToUnlockView.CallBack() {
                        @Override
                        public void onSlide(int distance) {

                        }

                        @Override
                        public void onUnlocked() {
                            bridge.arriveEnd(current);
                            resetView();
                        }
                    });
                }else {
                    btn_sure.setText("到达下车点");
                    btn_sure.setOnClickListener(v -> {
                        bridge.arriveEnd(current);
                    });
                }
                showInMap(new LatLng(current.endLatitude, current.endLongitude), StaticVal.MARKER_FLAG_END);
            }
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .transform(new GlideCircleTransform())
                    .placeholder(R.mipmap.photo_default)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);

            Glide.with(this)
                    .load(Config.IMG_SERVER + current.avatar + Config.IMG_PATH)
                    .apply(options)
                    .into(customerPhoto);
            callPhone.setOnClickListener(view -> PhoneUtil.call(getActivity(), current.passengerPhone));

            customerName.setText(current.passengerName);
            String weihao;
            if (current.passengerPhone != null && current.passengerPhone.length() > 4) {
                weihao = current.passengerPhone.substring(current.passengerPhone.length() - 4, current.passengerPhone.length());
            } else {
                weihao = current.passengerPhone;
            }
            customerPhone.setText("手机尾号:" + weihao + "  购票数:" + current.ticketNumber);
            toPlace.setText(current.status < CarpoolOrder.CARPOOL_STATUS_ARRIVED ? current.startAddress : current.endAddress);
        } else {
            if (getCurrentIndex() == allStation.scheduleStationVoList.size() - 1) {
                bridge.finishTask(allStation.scheduleId);
            }
        }

        bridge.changeToolbar(StaticVal.TOOLBAR_FLOW, -1);

        naviBtn.setOnClickListener(view -> {
            if (current.status <= CarpoolOrder.CARPOOL_STATUS_ARRIVED) {
                bridge.navi(new LatLng(current.startLatitude, current.startLongitude), orderId);
            } else {
                bridge.navi(new LatLng(current.endLatitude, current.endLongitude), orderId);
            }
        });
    }

    /**
     * 添加marker和路线到地图
     *
     * @param toLatlng
     * @param flag
     */
    private void showInMap(LatLng toLatlng, int flag) {
        bridge.clearMap();
        bridge.addMarker(toLatlng, flag);
        bridge.routePath(toLatlng);
    }

    /**
     * 手动触摸过地图显示定位小按钮
     */
    public void mapStatusChanged() {
        refreshImg.setVisibility(View.VISIBLE);
    }

    /**
     * 等待时间
     */
    private long timeSeq = 0;

    /**
     * 根据订单信息设置等待倒计时
     *
     * @param carpoolOrder
     */
    private void initTimer(CarpoolOrder carpoolOrder) {
        if (null != timer) {
            timer.cancel();
            timer = null;
        }
        if (null != timerTask) {
            timerTask.cancel();
            timerTask = null;
        }
        long appoint = XApp.getMyPreferences().getLong(Config.PC_BOOKTIME, 0);
        timeSeq = (appoint - System.currentTimeMillis()) / 1000;
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (!isAdded()) {
                    return;
                }
                if (getActivity() == null) {
                    return;
                }
                timeSeq--;
                setTimeText();
            }
        };
        timer.schedule(timerTask, 0, 1000);
        setTimeText();
    }

    /**
     * 显示对应格式等待时间
     */
    private void setTimeText() {
        if (!isAdded() || getActivity() == null) {
            return;
        }
        getActivity().runOnUiThread(() -> {

            StringBuilder sb = new StringBuilder();
            int minute = (int) (Math.abs(timeSeq) / 60);
            int sec = (int) (Math.abs(timeSeq) % 60);
            if (minute < 10) {
                sb.append("0");
            }
            sb.append(minute).append("分");
            if (sec < 10) {
                sb.append("0");
            }
            sb.append(sec).append("秒");
            if (timeSeq < 0) {
                //超时
                countHint.setText("等候已超时");
                countTime.setText(sb.toString());
                countTime.setTextColor(getResources().getColor(R.color.color_red));
                sliderCon.setVisibility(View.GONE);
                ll_btn_con.setVisibility(View.GONE);
                chaoshiCon.setVisibility(View.VISIBLE);
                jumpBtn.setOnClickListener(view -> showConfirmFlag(true));
                acceptedBtn.setOnClickListener(view -> showConfirmFlag(false));
            } else {
                //正常计时
                countHint.setText("等候倒计时");
                countTime.setText(sb.toString());
                countTime.setTextColor(getResources().getColor(R.color.color_orange));
                sliderCon.setVisibility(ZCSetting.findOne().operationMode == 1 ? View.VISIBLE : View.GONE);
                ll_btn_con.setVisibility(ZCSetting.findOne().operationMode == 2 ? View.VISIBLE : View.GONE);
                back.setVisibility(View.GONE);
                btn_back.setVisibility(View.GONE);
                chaoshiCon.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 判断是正常接到乘客还是跳过乘客
     *
     * @param jump
     */
    private void showConfirmFlag(boolean jump) {
        cancelTimer();
        sliderCon.setVisibility(ZCSetting.findOne().operationMode == 1 ? View.VISIBLE : View.GONE);
        ll_btn_con.setVisibility(ZCSetting.findOne().operationMode == 2 ? View.VISIBLE : View.GONE);

        countTimeCon.setVisibility(View.GONE);
        chaoshiCon.setVisibility(View.GONE);
        back.setVisibility(View.VISIBLE);
        btn_back.setVisibility(View.VISIBLE);

        back.setOnClickListener(view -> showWhatByStatus());
        btn_back.setOnClickListener(view -> showWhatByStatus());

        if (jump) {
            if (ZCSetting.findOne().operationMode == 1){
                //滑动
                slider.setHint("滑动跳过乘客");
                slider.setmCallBack(new CustomSlideToUnlockView.CallBack() {
                    @Override
                    public void onSlide(int distance) {

                    }

                    @Override
                    public void onUnlocked() {
                        bridge.jumpAccept(current);
                        resetView();
                    }
                });
            }else {
                //按钮
                btn_sure.setText("跳过乘客");
                btn_sure.setOnClickListener(v -> {
                    bridge.jumpAccept(current);
                });
            }
        } else {
            if (ZCSetting.findOne().operationMode == 1){
                slider.setHint("滑动接到乘客");
                slider.setmCallBack(new CustomSlideToUnlockView.CallBack() {
                    @Override
                    public void onSlide(int distance) {

                    }

                    @Override
                    public void onUnlocked() {
                        if (current.advanceAssign == 1) {
                            bridge.onDialogClick(1, current.orderId, current.money);
                        } else {
                            bridge.acceptCustomer(current);
                        }
                        resetView();
                    }
                });
            }else {
                btn_sure.setText("接到乘客");
                btn_sure.setOnClickListener(v -> {
                    if (current.advanceAssign == 1) {
                        bridge.onDialogClick(1, current.orderId, current.money);
                    } else {
                        bridge.acceptCustomer(current);
                    }
                });
            }
        }
    }

    /**
     * 获取当前的订单
     *
     * @return
     */
    public CarpoolOrder getCurrent() {
        return current;
    }

    /**
     * 是否能语音播报
     */
    private boolean speakedHint = false;

    /**
     * 判断距离是否在200内。进行语音播报
     *
     * @param dis
     */
    public void showLeft(int dis) {
        if (!speakedHint && dis < 200) {
            //小于200米
            if (current.status == CarpoolOrder.CARPOOL_STATUS_START) {
                XApp.getInstance().syntheticVoice("距离上车点还有" + dis + "米");
                XApp.getInstance().shake();
            } else if (current.status == CarpoolOrder.CARPOOL_STATUS_RUNNING) {
                XApp.getInstance().syntheticVoice("距离下车点还有" + dis + "米");
                XApp.getInstance().shake();
            }
            speakedHint = true;
        }
    }

    /**
     * 重置语音播报状态
     */
    public void resetSpeakedHint() {
        speakedHint = false;
    }

    /**
     * 滑动重置handler
     */
    Handler handler = new Handler();

    /**
     * 重置slider
     */
    private void resetView() {
        //防止卡顿
        slider.setVisibility(View.GONE);

        handler.postDelayed(() -> getActivity().runOnUiThread(() -> {
            slider.resetView();
            slider.setVisibility(View.VISIBLE);
        }), 1000);
    }
}
