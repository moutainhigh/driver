package com.easymin.carpooling.flowmvp.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
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
import com.easymi.common.entity.OrderCustomer;
import com.easymi.component.Config;
import com.easymi.component.ZXOrderStatus;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseFragment;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.utils.GlideCircleTransform;
import com.easymi.component.utils.PhoneUtil;
import com.easymi.component.widget.CustomSlideToUnlockView;
import com.easymin.carpooling.R;
import com.easymin.carpooling.StaticVal;
import com.easymin.carpooling.flowmvp.ActFraCommBridge;

import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: AcceptSendFragment
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

    /**
     * 订单id，类型
     */
    long orderId;
    String orderType;

    /**
     * 专线订单集
     */
    List<CarpoolOrder> carpoolOrders;

    /**
     * 本地数据库动态订单信息
     */
    DymOrder dymOrder;

    /**
     * 通信桥
     */
    ActFraCommBridge bridge;

    /**
     * 当前order
     */
    private CarpoolOrder current;

    /**
     * 设置bridge
     * @param bridge
     */
    public void setBridge(ActFraCommBridge bridge) {
        this.bridge = bridge;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            showWhatByStatus();
        }
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        if (args == null) {
            return;
        }
        orderId = args.getLong("orderId", 0);
        orderType = args.getString("orderType", "");
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_main_flow;
    }

    @Override
    public void finishCreateView(Bundle state) {
        customerPhoto = $(R.id.customer_photo);
        customerName = $(R.id.customer_name);
        customerPhone = $(R.id.customer_phone);
        callPhone = $(R.id.call_phone);
        toPlace = $(R.id.to_place);
        naviBtn = $(R.id.navi_view);
        countTimeCon = $(R.id.count_time_con);
        countHint = $(R.id.count_hint);
        countTime = $(R.id.count_time);
        slider = $(R.id.slider);
        refreshImg = $(R.id.ic_refresh);
        jumpBtn = $(R.id.jump_accept);
        acceptedBtn = $(R.id.accept_cus);
        chaoshiCon = $(R.id.chaoshi_con);
        back = $(R.id.back);
        sliderCon = $(R.id.slider_con);

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

    /**
     * 根据订单状态显示对应数据
     */
    public void showWhatByStatus() {
        cancelTimer();

        dymOrder = DymOrder.findByIDType(orderId, orderType);

        if (dymOrder.orderStatus == ZXOrderStatus.ACCEPT_ING) {
            carpoolOrders = CarpoolOrder.findByIDTypeOrderByAcceptSeq(orderId, orderType);
        } else {
            carpoolOrders = CarpoolOrder.findByIDTypeOrderBySendSeq(orderId, orderType);
        }
        Iterator iterator = carpoolOrders.iterator();
        while (iterator.hasNext()) {
            CarpoolOrder carpoolOrder = (CarpoolOrder) iterator.next();
            if (carpoolOrder.customeStatus != 0
                    && dymOrder.orderStatus == ZXOrderStatus.ACCEPT_ING) {//还在接人时，移除所有非0
                iterator.remove();
            } else if (carpoolOrder.customeStatus != 3
                    && dymOrder.orderStatus == ZXOrderStatus.SEND_ING) { //还在送人时，移除所有非3
                iterator.remove();
            }
        }
        if (carpoolOrders.size() != 0) {
            current = carpoolOrders.get(0);
            if (current.customeStatus == 0) {
                //未接
                if (current.subStatus == 0) {
                    countTimeCon.setVisibility(View.GONE);
                    sliderCon.setVisibility(View.VISIBLE);
                    chaoshiCon.setVisibility(View.GONE);
                    back.setVisibility(View.GONE);
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

                }else if (current.subStatus == 1) {
                    //未到达预约地
                    countTimeCon.setVisibility(View.GONE);
                    sliderCon.setVisibility(View.VISIBLE);
                    chaoshiCon.setVisibility(View.GONE);
                    back.setVisibility(View.GONE);
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
                } else if (current.subStatus == 2){
                    countTimeCon.setVisibility(View.VISIBLE);
                    sliderCon.setVisibility(View.GONE);
                    chaoshiCon.setVisibility(View.GONE);
                    back.setVisibility(View.GONE);
                    slider.setHint("滑动确认接到乘客");
                    slider.setmCallBack(new CustomSlideToUnlockView.CallBack() {
                        @Override
                        public void onSlide(int distance) {

                        }

                        @Override
                        public void onUnlocked() {
                            bridge.acceptCustomer(current);
                            resetView();
                        }
                    });
                    initTimer(current);
                }
                showInMap(new LatLng(current.startLat, current.startLng), StaticVal.MARKER_FLAG_END);
            } else if (current.customeStatus == 3) {
                countTimeCon.setVisibility(View.GONE);
                sliderCon.setVisibility(View.VISIBLE);
                chaoshiCon.setVisibility(View.GONE);
                back.setVisibility(View.GONE);
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
                showInMap(new LatLng(current.endLat, current.endLng), StaticVal.MARKER_FLAG_END);
            }

            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .transform(new GlideCircleTransform())
                    .placeholder(R.mipmap.photo_default)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(getActivity())
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
            customerPhone.setText("手机尾号：" + weihao);

            toPlace.setText(current.customeStatus < 3 ? current.startAddr : current.endAddr);
        }

        bridge.changeToolbar(StaticVal.TOOLBAR_FLOW);

        naviBtn.setOnClickListener(view -> {
            if (current.customeStatus == 0) {
                bridge.navi(new LatLng(current.startLat, current.startLng), orderId);
            } else {
                bridge.navi(new LatLng(current.endLat, current.endLng), orderId);
            }
        });
    }

    /**
     * 添加marker和路线到地图
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
        long appoint = carpoolOrder.bookTime;
        timeSeq = (appoint - System.currentTimeMillis()) / 1000;
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
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
                countHint.setText("等候已超时：");
                countTime.setText(sb.toString());
                countTime.setTextColor(getResources().getColor(R.color.color_red));
                sliderCon.setVisibility(View.GONE);
                chaoshiCon.setVisibility(View.VISIBLE);
                jumpBtn.setOnClickListener(view -> showConfirmFlag(true));
                acceptedBtn.setOnClickListener(view -> showConfirmFlag(false));
            } else {
                //正常计时
                countHint.setText("等候倒计时：");
                countTime.setText(sb.toString());
                countTime.setTextColor(getResources().getColor(R.color.color_orange));
                sliderCon.setVisibility(View.VISIBLE);
                back.setVisibility(View.GONE);
                chaoshiCon.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 判断是正常接到乘客还是跳过乘客
     * @param jump
     */
    private void showConfirmFlag(boolean jump) {
        cancelTimer();
        sliderCon.setVisibility(View.VISIBLE);
        countTimeCon.setVisibility(View.GONE);
        chaoshiCon.setVisibility(View.GONE);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(view -> showWhatByStatus());
        if (jump) {
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
        } else {
            slider.setHint("滑动接到乘客");
            slider.setmCallBack(new CustomSlideToUnlockView.CallBack() {
                @Override
                public void onSlide(int distance) {

                }

                @Override
                public void onUnlocked() {
                    bridge.acceptCustomer(current);
                    resetView();
                }
            });
        }
    }

    /**
     * 获取当前的订单
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
     * @param dis
     */
    public void showLeft(int dis) {
        if (!speakedHint && dis < 200) {
            //小于200米
            if (current.customeStatus == 0) {
                XApp.getInstance().syntheticVoice("距离上车点还有" + dis + "米");
                XApp.getInstance().shake();
            } else if (current.customeStatus == 3) {
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
        slider.setVisibility(View.GONE);

        handler.postDelayed(() -> getActivity().runOnUiThread(() -> {
            slider.resetView();
            slider.setVisibility(View.VISIBLE);
        }), 1000);
        //防止卡顿
    }

}
