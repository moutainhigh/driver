package com.easymin.custombus.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.easymi.component.BusOrderStatus;
import com.easymi.component.Config;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.utils.CountDownUtils;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.TimeUtil;
import com.easymi.component.utils.ViewUtil;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.CustomSlideToUnlockView;
import com.easymi.component.widget.LoadingButton;
import com.easymi.component.widget.dialog.SureBaseDialog;
import com.easymin.custombus.R;
import com.easymin.custombus.adapter.StationAdapter;
import com.easymin.custombus.entity.CbBusOrder;
import com.easymin.custombus.entity.Customer;
import com.easymin.custombus.mvp.FlowContract;
import com.easymin.custombus.mvp.FlowPresenter;
import com.easymin.custombus.receiver.CancelOrderReceiver;
import com.easymin.custombus.receiver.ScheduleTurnReceiver;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.text.DecimalFormat;
import java.util.List;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: CbRunActivity
 * @Author: hufeng
 * @Date: 2019/2/14 下午1:55
 * @Description: 客运班车跑单主界面
 * @History:
 */
@Route(path = "/custombus/CbRunActivity")
public class CbRunActivity extends RxBaseActivity implements FlowContract.View,
        CancelOrderReceiver.OnCancelListener,
        ScheduleTurnReceiver.OnTurnListener {

    /**
     * 界面控件
     */
    CusToolbar cus_toolbar;
    LinearLayout lin_no_start;
    TextView tv_start_site;
    TextView tv_end_site;
    TextView tv_go_time;
    LinearLayout lin_running;
    TextView tv_stauts;
    TextView tv_station_name;
    RecyclerView recyclerView;
    LinearLayout lin_start_countdown;
    TextView tv_day;
    TextView tv_hour;
    TextView tv_fen;
    CustomSlideToUnlockView slider;
    ExpandableLayout expand;
    TextView tv_left;
    LinearLayout control_con;

    LoadingButton button_sure;

    /**
     * 倒计时工具类
     */
    public CountDownUtils countDownUtils;
    /**
     * 站点适配器
     */
    public StationAdapter stationAdapter;
    /**
     * 班次详情
     */
    CbBusOrder cbBusOrder;
    /**
     * 班次id
     */
    private Long scheduleId;

    /**
     * 当前处理站点序号
     */
    private int position;

    /**
     * 滑动类型(语音类型)
     */
    public int type;

    /**
     * 取消订单广播接收者
     */
    private CancelOrderReceiver cancelOrderReceiver;

    /**
     * 自动完成订单
     */
    private ScheduleTurnReceiver scheduleTurnReceiver;

    private FlowPresenter presenter;

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_custom_bus;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        findById();
        presenter = new FlowPresenter(this, this);
        initAdapter();
        scheduleId = getIntent().getLongExtra("scheduleId", 0);
        getData();
    }

    @Override
    public void initToolBar() {
        super.initToolBar();
        cus_toolbar.setLeftIcon(R.drawable.ic_arrow_back, v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();

        cancelOrderReceiver = new CancelOrderReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Config.BROAD_CANCEL_ORDER);
        filter.addAction(Config.BROAD_BACK_ORDER);
        registerReceiver(cancelOrderReceiver, filter, EmUtil.getBroadCastPermission(), null);

        scheduleTurnReceiver = new ScheduleTurnReceiver(this);
        IntentFilter filter1 = new IntentFilter();
        filter1.addAction(Config.SCHEDULE_FINISH);
        registerReceiver(scheduleTurnReceiver, filter1, EmUtil.getBroadCastPermission(), null);
    }

    /**
     * 获取班次信息
     */
    public void getData() {
        if (scheduleId != null && scheduleId != 0) {
            presenter.findBusOrderById(scheduleId);
        }
    }

    /**
     * 初始化控件
     */
    public void findById() {
        cus_toolbar = findViewById(R.id.cus_toolbar);
        lin_no_start = findViewById(R.id.lin_no_start);
        tv_start_site = findViewById(R.id.tv_start_site);
        tv_end_site = findViewById(R.id.tv_end_site);
        tv_go_time = findViewById(R.id.tv_go_time);
        lin_running = findViewById(R.id.lin_running);
        tv_stauts = findViewById(R.id.tv_stauts);
        tv_station_name = findViewById(R.id.tv_station_name);
        recyclerView = findViewById(R.id.recyclerView);
        lin_start_countdown = findViewById(R.id.lin_start_countdown);
        tv_day = findViewById(R.id.tv_day);
        tv_hour = findViewById(R.id.tv_hour);
        tv_fen = findViewById(R.id.tv_fen);
        slider = findViewById(R.id.slider);
        expand = findViewById(R.id.expand);
        tv_left = findViewById(R.id.tv_left);
        control_con = findViewById(R.id.control_con);
        button_sure = findViewById(R.id.button_sure);
    }

    /**
     * 初始化适配器
     */
    public void initAdapter() {
        stationAdapter = new StationAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(stationAdapter);
        stationAdapter.setOnItemClickListener(item -> {
            if (item == position) {
                if (cbBusOrder.arrivedTime == 0) {
                    toPassenger(0, position);
                } else {
                    toPassenger(cbBusOrder.arrivedTime, position);
                }
            } else {
                toPassenger(0, item);
            }
        });
    }

    /**
     * 初始化监听
     */
    public void initListenner() {
        //开始行程倒计时
        countDownUtils = new CountDownUtils(this, (cbBusOrder.time + 60) * 1000, (day, hour, minute) -> {
            tv_day.setText("" + day);
            tv_hour.setText("" + hour);
            tv_fen.setText("" + minute);
//            if (day == 0 && hour == 0 && minute == 0) {
//                control_con.setVisibility(View.VISIBLE);
//            }
        });

        slider.setmCallBack(new CustomSlideToUnlockView.CallBack() {
            @Override
            public void onSlide(int distance) {

            }

            @Override
            public void onUnlocked() {
                if (cbBusOrder.status == BusOrderStatus.SCHEDULE_STATUS_RUNNING) {
                    if (cbBusOrder.driverStationVos.get(position).status == 2) {
                        /**
                         *  到达站点
                         */
                        if (position != cbBusOrder.driverStationVos.size() - 1) {
                            type = 3;
                        } else {
                            type = 4;
                        }
                        presenter.arriveStation(cbBusOrder.id, cbBusOrder.driverStationVos.get(position).stationId,null);
                    } else if (cbBusOrder.driverStationVos.get(position).status == 3) {
                        if (position == cbBusOrder.driverStationVos.size() - 1) {
                            /**
                             * 终点站结束行程
                             */
                            presenter.endStation(cbBusOrder.id, null);
                        } else {
                            if ((cbBusOrder.driverStationVos.get(position).checkNumber + cbBusOrder.driverStationVos.get(position).unCheckNumber) == 0) {
                                /**
                                 * 本站没有票
                                 */
                                presenter.toNextStation(cbBusOrder.id, cbBusOrder.driverStationVos.get(position + 1).stationId,null);
                                type = 2;
                            } else {
                                /**
                                 * 验票
                                 */
                                if (cbBusOrder.arrivedTime == 0) {
                                    presenter.chechTickets(cbBusOrder.id,null);
                                } else {
                                    toPassenger(cbBusOrder.arrivedTime, position);
                                }
                            }
                        }
                    }
                } else if (cbBusOrder.status == BusOrderStatus.SCHEDULE_STATUS_NEW) {
                    /**
                     * 未开始状态（开始行程）
                     */
                    expand.collapse();
                    presenter.startStation(cbBusOrder.id, null);
                    type = 1;
                }
                slider.resetView();
            }
        });

        button_sure.setOnClickListener(v -> {
            if (cbBusOrder.status == BusOrderStatus.SCHEDULE_STATUS_RUNNING) {
                if (cbBusOrder.driverStationVos.get(position).status == 2) {
                    /**
                     *  到达站点
                     */
                    SureBaseDialog dialog = new SureBaseDialog(this,"确认到达站点么？");
                    dialog.setOnMyClickListener((view, string) -> {
                        if (view.getId() == R.id.tv_sure){
                            if (position != cbBusOrder.driverStationVos.size() - 1) {
                                type = 3;
                            } else {
                                type = 4;
                            }
                            presenter.arriveStation(cbBusOrder.id, cbBusOrder.driverStationVos.get(position).stationId,button_sure);
                        }
                    });
                    dialog.show();
                } else if (cbBusOrder.driverStationVos.get(position).status == 3) {
                    if (position == cbBusOrder.driverStationVos.size() - 1) {
                        /**
                         * 终点站结束行程
                         */
                        SureBaseDialog dialog = new SureBaseDialog(this,"确认结束行程么？");
                        dialog.setOnMyClickListener((view, string) -> {
                            if (view.getId() == R.id.tv_sure){
                                presenter.endStation(cbBusOrder.id, null);
                            }
                        });
                        dialog.show();
                    } else {
                        if ((cbBusOrder.driverStationVos.get(position).checkNumber + cbBusOrder.driverStationVos.get(position).unCheckNumber) == 0) {
                            /**
                             * 本站没有票
                             */
                            SureBaseDialog dialog = new SureBaseDialog(this,"确认前往下一站么？");
                            dialog.setOnMyClickListener((view, string) -> {
                                if (view.getId() == R.id.tv_sure){
                                    presenter.toNextStation(cbBusOrder.id, cbBusOrder.driverStationVos.get(position + 1).stationId,button_sure);
                                    type = 2;
                                }
                            });
                            dialog.show();
                        } else {
                            /**
                             * 验票
                             */
                            if (cbBusOrder.arrivedTime == 0) {
                                presenter.chechTickets(cbBusOrder.id,button_sure);
                            } else {
                                toPassenger(cbBusOrder.arrivedTime, position);
                            }
                        }
                    }
                }
            } else if (cbBusOrder.status == BusOrderStatus.SCHEDULE_STATUS_NEW) {
                /**
                 * 未开始状态（开始行程）
                 */
                SureBaseDialog dialog = new SureBaseDialog(this,"确认开始行程么？");
                dialog.setOnMyClickListener((view, string) -> {
                    if (view.getId() == R.id.tv_sure){
                        expand.collapse();
                        presenter.startStation(cbBusOrder.id, null);
                        type = 1;
                    }
                });
                dialog.show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownUtils != null) {
            countDownUtils.cancelTimer();
        }
    }

    /**
     * 跳转乘客信息列表
     */
    public void toPassenger(long time, int item) {
        //重复点击，事件不处理
        if (ViewUtil.isFastClick()) {
            Intent intent = new Intent(this, PassengerActivity.class);
            intent.putExtra("cbBusOrder", cbBusOrder);
            intent.putExtra("time", time);
            intent.putExtra("position", item);
            startActivityForResult(intent, 0x00);
        }

    }

    /**
     * 加载数据到布局
     */
    public void setData() {
        if (cbBusOrder.status == BusOrderStatus.SCHEDULE_STATUS_NEW) {
            lin_no_start.setVisibility(View.VISIBLE);
            tv_start_site.setText(cbBusOrder.getStartSite().name);
            tv_end_site.setText(cbBusOrder.getEndSite().name);
            tv_go_time.setText(TimeUtil.getTime("yyyy年MM月dd日 HH:mm", cbBusOrder.time * 1000) + "出发");
            lin_running.setVisibility(View.GONE);
            lin_start_countdown.setVisibility(View.VISIBLE);
            cus_toolbar.setTitle(R.string.cb_no_start);
//            if (System.currentTimeMillis() < (cbBusOrder.time + 60) * 1000) {
//                control_con.setVisibility(View.GONE);
//            }
        } else if (cbBusOrder.status == BusOrderStatus.SCHEDULE_STATUS_RUNNING) {
            lin_no_start.setVisibility(View.GONE);
            lin_running.setVisibility(View.VISIBLE);
            lin_start_countdown.setVisibility(View.GONE);
        }
        changeTop();
        stationAdapter.setDatas(cbBusOrder.driverStationVos);
        if (cbBusOrder.arrivedTime != 0) {
            stationAdapter.setCheckStatus(true);
        } else {
            stationAdapter.setCheckStatus(false);
        }

        if (XApp.getMyPreferences().getInt(Config.PC_IS_BUTTON,1) == 2){
            slider.setVisibility(View.GONE);
            button_sure.setVisibility(View.VISIBLE);
        }else {
            slider.setVisibility(View.VISIBLE);
            button_sure.setVisibility(View.GONE);
        }
    }

    /**
     * 根据站点状态显示顶部布局和文字
     */
    public void changeTop() {
        if (cbBusOrder.driverStationVos.get(position).status == 2) {
            tv_stauts.setText(getResources().getString(R.string.cb_go_to));
            tv_station_name.setText(cbBusOrder.driverStationVos.get(position).name);
            if (XApp.getMyPreferences().getInt(Config.PC_IS_BUTTON,1) == 2){
                button_sure.setText("到达站点");
            }else {
                slider.setHint(getResources().getString(R.string.cb_slider_arrive));
            }
            presenter.routePlanByNavi(cbBusOrder.driverStationVos.get(position).latitude, cbBusOrder.driverStationVos.get(position).longitude);
            cus_toolbar.setTitle(R.string.cb_running);
        } else if (cbBusOrder.driverStationVos.get(position).status == 3) {
            tv_stauts.setText(getResources().getString(R.string.cb_arrived));
            tv_station_name.setText(cbBusOrder.driverStationVos.get(position).name);

            if (position == cbBusOrder.driverStationVos.size() - 1) {
                tv_left.setText(getResources().getString(R.string.cb_arrive_end_station));
                if (XApp.getMyPreferences().getInt(Config.PC_IS_BUTTON,1) == 2){
                    button_sure.setText("结束行程");
                }else {
                    slider.setHint(getResources().getString(R.string.cb_finish));
                }
            } else {
                if ((cbBusOrder.driverStationVos.get(position).checkNumber + cbBusOrder.driverStationVos.get(position).unCheckNumber) == 0) {
                    tv_left.setText(getResources().getString(R.string.cb_not_check));
                    if (XApp.getMyPreferences().getInt(Config.PC_IS_BUTTON,1) == 2){
                        button_sure.setText("前往下一站");
                    }else {
                        slider.setHint(getResources().getString(R.string.cb_slider_go_next));
                    }
                } else {
                    tv_left.setText(getResources().getString(R.string.cb_please_check));
                    if (XApp.getMyPreferences().getInt(Config.PC_IS_BUTTON,1) == 2){
                        button_sure.setText("开始验票");
                    }else {
                        slider.setHint(getResources().getString(R.string.cb_slider_check));
                    }
                }
            }
            cus_toolbar.setTitle(R.string.cb_arrive_station);
        }
    }

    @Override
    public void showLeft(int dis, int time) {
        String disKm;
        int km = dis / 1000;
        if (km >= 1) {
            disKm = new DecimalFormat("#0.0").format((double) dis / 1000) + getResources().getString(R.string.cb_km);
        } else {
            disKm = dis + getResources().getString(R.string.cb_mi);
        }

        tv_left.setText(getResources().getString(R.string.cb_destance)
                + disKm
                + "  " + (time / 60)
                + getResources().getString(R.string.cb_minutes));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(cancelOrderReceiver);
        unregisterReceiver(scheduleTurnReceiver);
    }

    @Override
    public void showBusLineInfo(CbBusOrder cbBusOrder) {
        this.cbBusOrder = cbBusOrder;
        dealData();
        setData();
        initListenner();

    }

    @Override
    public void showcheckTime(long time) {
        toPassenger(time, position);
    }

    @Override
    public void showOrders(List<Customer> customers) {

    }

    @Override
    public void dealSuccese() {
        speekVoice(type);
        getData();
    }

    @Override
    public void succeseOrder(Customer customer) {

    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    public void errorCode() {

    }

    /**
     * 本地处理站点状态
     */
    public void dealData() {
        if (cbBusOrder.currentStationId == 0) {
            /**
             * 未开始行程的时候当前站点是空
             */
            for (int i = 0; i < cbBusOrder.driverStationVos.size(); i++) {
                position = 0;
                cbBusOrder.driverStationVos.get(i).status = 1;
            }
        } else {
            /**
             *  after 是否是当前站点之前的站点
             */
            boolean after = false;
            for (int i = 0; i < cbBusOrder.driverStationVos.size(); i++) {
                if (cbBusOrder.currentStationId == cbBusOrder.driverStationVos.get(i).stationId) {
                    /**
                     * 当前站点
                     */
                    if (cbBusOrder.currentStationStatus == 1) {
                        cbBusOrder.driverStationVos.get(i).status = 2;
                    } else if (cbBusOrder.currentStationStatus == 2) {
                        cbBusOrder.driverStationVos.get(i).status = 3;
                    }
                    after = true;
                    position = i;
                } else {
                    /**
                     * 非当前站点
                     */
                    if (!after) {
                        cbBusOrder.driverStationVos.get(i).status = 4;
                    } else {
                        cbBusOrder.driverStationVos.get(i).status = 1;
                    }
                }
            }
        }
    }

    @Override
    public RxManager getManager() {
        return mRxManager;
    }

    public void speekVoice(int type) {
        if (type == 1) {
            /**
             * 开始行程
             */
            XApp.getInstance().syntheticVoice("开始行程,本次起点站为" + cbBusOrder.driverStationVos.get(0).name + ",站点乘客" + cbBusOrder.driverStationVos.get(0).unCheckNumber + "位");
        } else if (type == 2) {
            /**
             * 前往站点
             */
            XApp.getInstance().syntheticVoice("前往站点" + cbBusOrder.driverStationVos.get(position + 1).name + ",站点乘客" + cbBusOrder.driverStationVos.get(position + 1).unCheckNumber + "位");
        } else if (type == 3) {
            /**
             * 到达站点
             */
            XApp.getInstance().syntheticVoice("车辆到达" + cbBusOrder.driverStationVos.get(position).name + ",本站乘客" + cbBusOrder.driverStationVos.get(position).unCheckNumber + "位");
        } else {
            /**
             * 终点站
             */
            XApp.getInstance().syntheticVoice("本次终点站" + cbBusOrder.driverStationVos.get(position).name + "已到达");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data != null && data.getIntExtra("type", 0) != 0) {
                type = data.getIntExtra("type", 0);
                speekVoice(type);
            }
            getData();
        }
    }

    @Override
    public void onCancelOrder(long orderId, String orderType, String msg) {
        getData();
    }

    @Override
    public void onTurnOrder(long id, String orderType, String msg) {
        if (id == scheduleId) {
            finish();
        }
    }
}
