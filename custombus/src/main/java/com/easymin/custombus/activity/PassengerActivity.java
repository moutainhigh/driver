package com.easymin.custombus.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import com.easymi.component.Config;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.utils.DensityUtil;
import com.easymi.component.utils.Log;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.LoadingButton;
import com.easymin.custombus.MyScrollVIew;
import com.easymin.custombus.R;
import com.easymin.custombus.adapter.PassengerAdapter;
import com.easymin.custombus.dialog.GoNextDialog;
import com.easymin.custombus.entity.CbBusOrder;
import com.easymin.custombus.entity.Customer;
import com.easymin.custombus.mvp.FlowContract;
import com.easymin.custombus.mvp.FlowPresenter;
import com.easymin.custombus.receiver.CancelOrderReceiver;
import com.easymin.custombus.receiver.OrderFinishReceiver;
import com.easymin.custombus.receiver.ScheduleTurnReceiver;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: PassengerActivity
 * @Author: hufeng
 * @Date: 2019/2/15 下午1:22
 * @Description:
 * @History:
 */
public class PassengerActivity extends RxBaseActivity implements FlowContract.View,
        CancelOrderReceiver.OnCancelListener,
        ScheduleTurnReceiver.OnTurnListener {
    /**
     * 界面控件
     */
    CusToolbar cus_toolbar;
    TextView tv_station_name;
    TextView tv_status_yes;
    TextView tv_status_no;
    TextView tv_countdown;
    TextView tv_countdown_hint;
    LinearLayout lin_time_countdown;

    TextView tv_countdown1;
    TextView tv_countdown_hint1;
    LinearLayout lin_time_countdown1;

    RecyclerView recyclerView;
    LinearLayout lin_bottom;
    TextView tv_go_next;
    LinearLayout lin_go_next;
    MyScrollVIew scrollview;
    LinearLayout lin_head;
    View passenger_v;

    LinearLayout lin_check_top;
    ImageView iv_ticket;
    TextView tv_check_hint;

    TextView tv_check_btn;
    TextView tv_go_next_btn;

    LinearLayout lin_space;

    /**
     * 适配器
     */
    public PassengerAdapter adapter;

    /**
     * 开始验票时间戳
     */
    private long booktime;

    private FlowPresenter presenter;

    /**
     * 站点信息
     */
    private CbBusOrder cbBusOrder;

    /**
     * 当前站点下标
     */
    private int position;

    /**
     * 取消订单广播接收者
     */
    private CancelOrderReceiver cancelOrderReceiver;
    private LinearLayout passenger_ll_top;

    /**
     * 自动完成订单
     */
    private ScheduleTurnReceiver scheduleTurnReceiver;

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_passenger;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        cbBusOrder = (CbBusOrder) getIntent().getSerializableExtra("cbBusOrder");
        booktime = getIntent().getLongExtra("time", 0);
        position = getIntent().getIntExtra("position", 0);
        presenter = new FlowPresenter(this, this);
        findById();
        initAdapter();
        initListener();
        getData();
        setData();
        if (booktime != 0) {
            setWaitTime();
            passenger_v.setVisibility(View.VISIBLE);
        } else {
            passenger_v.setVisibility(View.GONE);
        }
    }

    public void setData() {
        tv_station_name.setText(cbBusOrder.driverStationVos.get(position).name);

    }

    @Override
    public void initToolBar() {
        super.initToolBar();
        cus_toolbar.setLeftIcon(R.drawable.ic_arrow_back, v -> {
            setResult(RESULT_OK);
            finish();
        });
        cus_toolbar.setTitle(R.string.cb_passenger_info);
    }

    /**
     * 加载控件
     */
    public void findById() {
        cus_toolbar = findViewById(R.id.cus_toolbar);
        tv_station_name = findViewById(R.id.tv_station_name);
        tv_status_yes = findViewById(R.id.tv_status_yes);
        tv_status_no = findViewById(R.id.tv_status_no);
        tv_countdown = findViewById(R.id.tv_countdown);
        tv_countdown_hint = findViewById(R.id.tv_countdown_hint);
        recyclerView = findViewById(R.id.recyclerView);
        lin_bottom = findViewById(R.id.lin_bottom);
        lin_time_countdown = findViewById(R.id.lin_time_countdown);
        tv_go_next = findViewById(R.id.tv_go_next);
        lin_go_next = findViewById(R.id.lin_go_next);
        scrollview = findViewById(R.id.scrollview);
        passenger_ll_top = findViewById(R.id.passenger_ll_top);
        lin_head = findViewById(R.id.lin_head);
        passenger_v = findViewById(R.id.passenger_v);

        tv_countdown1 = findViewById(R.id.tv_countdown1);
        tv_countdown_hint1 = findViewById(R.id.tv_countdown_hint1);
        lin_time_countdown1 = findViewById(R.id.lin_time_countdown1);

        lin_check_top = findViewById(R.id.lin_check_top);
        iv_ticket = findViewById(R.id.iv_ticket);
        tv_check_hint = findViewById(R.id.tv_check_hint);

        tv_check_btn = findViewById(R.id.tv_check_btn);
        tv_go_next_btn = findViewById(R.id.tv_go_next_btn);

        lin_space = findViewById(R.id.lin_space);
    }

    /**
     * 初始化适配器
     */
    public void initAdapter() {
        adapter = new PassengerAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        //禁止滑动
        recyclerView.setNestedScrollingEnabled(false);
    }


    /**
     * 初始化监听
     */
    public void initListener() {
        passenger_v.setOnTouchListener((v, event) -> passenger_ll_top.dispatchTouchEvent(event));

        //跳转验票
        tv_check_btn.setOnClickListener(v -> {
            Intent intent = new Intent(this, CheckTicketActivity.class);
            startActivityForResult(intent, 0x00);
        });
        //前往下一站
        tv_go_next.setOnClickListener(v -> {
            if (uncheck == 0) {
                presenter.toNextStation(cbBusOrder.id, cbBusOrder.driverStationVos.get(position + 1).stationId);
            } else {
                GoNextDialog dialog = new GoNextDialog(this);
                dialog.setOnMyClickListener((view, string) -> {
                    presenter.toNextStation(cbBusOrder.id, cbBusOrder.driverStationVos.get(position + 1).stationId);
                });
                dialog.show();
            }
        });
        tv_go_next_btn.setOnClickListener(v -> {
            if (uncheck == 0) {
                presenter.toNextStation(cbBusOrder.id, cbBusOrder.driverStationVos.get(position + 1).stationId);
            } else {
                GoNextDialog dialog = new GoNextDialog(this);
                dialog.setOnMyClickListener((view, string) -> {
                    presenter.toNextStation(cbBusOrder.id, cbBusOrder.driverStationVos.get(position + 1).stationId);
                });
                dialog.show();
            }
        });
        //视差和悬浮头部展示
        scrollview.setScrollListener(scrollY -> {
            passenger_ll_top.setTranslationY(-(float) (scrollY * 0.7));
            if (scrollY >= DensityUtil.dp2px(PassengerActivity.this, 280)) {
                lin_head.setVisibility(View.VISIBLE);
            } else {
                lin_head.setVisibility(View.GONE);
            }
        });

//        scrollview.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        Log.e("hufeng/getScrollY",scrollview.getScrollY()+"");
//                        Log.e("hufeng/dp2px",DensityUtil.dp2px(PassengerActivity.this, 140)+"");
//                        if (scrollview.getScrollY() >= DensityUtil.dp2px(PassengerActivity.this, 140)) {
//                            scrollview.scrollTo(0, 0);
//                        } else {
//                            scrollview.scrollTo(0, DensityUtil.dp2px(PassengerActivity.this, 280));
//                        }
//                        break;
//                }
//                return false;
//            }
//        });
    }

    /**
     * 倒计时计时器
     */
    Timer timer;
    TimerTask timerTask;

    /**
     * 取消定时器
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
     * 等待时间
     */
    private long timeSeq = 0;

    /**
     * 等待倒计时
     */
    public void setWaitTime() {
        lin_time_countdown.setVisibility(View.VISIBLE);
        lin_time_countdown1.setVisibility(View.VISIBLE);

        if (null != timer) {
            timer.cancel();
            timer = null;
        }
        if (null != timerTask) {
            timerTask.cancel();
            timerTask = null;
        }

        timeSeq = ((booktime + cbBusOrder.reciprocalMinute * 60) * 1000 - System.currentTimeMillis()) / 1000;
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
        runOnUiThread(() -> {
            StringBuilder sb = new StringBuilder();
            int minute = (int) (Math.abs(timeSeq) / 60);
            int sec = (int) (Math.abs(timeSeq) % 60);
            if (minute < 10) {
                sb.append("0");
            }
            sb.append(minute).append(":");
            if (sec < 10) {
                sb.append("0");
            }
            sb.append(sec);
            if (timeSeq < 0) {
                //超时
                tv_countdown_hint.setText(getResources().getString(R.string.cb_wait_timeout));
                tv_countdown_hint1.setText(getResources().getString(R.string.cb_wait_timeout));
                tv_countdown.setText(sb.toString());
                tv_countdown1.setText(sb.toString());
                tv_countdown.setTextColor(getResources().getColor(R.color.color_FF485E));
                tv_countdown1.setTextColor(getResources().getColor(R.color.color_FF485E));
                tv_countdown_hint.setTextColor(getResources().getColor(R.color.color_FF485E));
                tv_countdown_hint1.setTextColor(getResources().getColor(R.color.color_FF485E));

                //超时验完
                if (uncheck == 0) {
                    lin_go_next.setVisibility(View.GONE);

                    iv_ticket.setVisibility(View.GONE);
                    tv_check_hint.setText(getResources().getString(R.string.cb_go_next_station));

                    tv_go_next_btn.setVisibility(View.VISIBLE);
                    tv_check_btn.setVisibility(View.GONE);

                    tv_go_next_btn.setTextColor(getResources().getColor(R.color.white));
                    tv_go_next_btn.setBackground(getResources().getDrawable(R.drawable.corners_btn_4dp_bg));
                } else {
                    //超时未验票完
                    lin_go_next.setVisibility(View.VISIBLE);

                    iv_ticket.setVisibility(View.VISIBLE);
                    tv_check_hint.setText(getResources().getString(R.string.cb_check_ticket));

                    tv_go_next_btn.setVisibility(View.VISIBLE);
                    tv_check_btn.setVisibility(View.VISIBLE);

                    tv_go_next_btn.setTextColor(getResources().getColor(R.color.color_3C98E3));
                    tv_go_next_btn.setBackground(getResources().getDrawable(R.drawable.line_btn_4dp_3c98e3));
                    tv_check_btn.setTextColor(getResources().getColor(R.color.white));
                    tv_check_btn.setBackground(getResources().getDrawable(R.drawable.corners_btn_4dp_bg));
                }
            } else {
                //正常计时
                tv_countdown_hint.setText(getResources().getString(R.string.cb_wait_countdown));
                tv_countdown_hint1.setText(getResources().getString(R.string.cb_wait_countdown));
                tv_countdown.setText(sb.toString());
                tv_countdown1.setText(sb.toString());
                tv_countdown.setTextColor(getResources().getColor(R.color.color_089A55));
                tv_countdown1.setTextColor(getResources().getColor(R.color.color_089A55));
                tv_countdown_hint.setTextColor(getResources().getColor(R.color.color_999999));
                tv_countdown_hint1.setTextColor(getResources().getColor(R.color.color_999999));

                lin_go_next.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelTimer();
    }

    public void getData() {
        presenter.queryOrders(cbBusOrder.id, cbBusOrder.driverStationVos.get(position).stationId);
    }

    @Override
    public void showLeft(int dis, int time) {

    }

    @Override
    public void showBusLineInfo(CbBusOrder cbBusOrder) {

    }

    @Override
    public void showcheckTime(long time) {

    }

    @Override
    public void showOrders(List<Customer> customers) {
        adapter.setDatas(customers);
        checkNumber(customers);
    }

    @Override
    public void dealSuccese() {
        setMyResult();
    }

    @Override
    public void succeseOrder(Customer customer) {

    }

    @Override
    public void finishActivity() {

    }

    @Override
    public RxManager getManager() {
        return mRxManager;
    }


    int check = 0;
    int uncheck = 0;

    /**
     * 检查当前订单列表的验票情况
     *
     * @param customers
     */
    public void checkNumber(List<Customer> customers) {
        check = 0;
        uncheck = 0;
        for (int i = 0; i < customers.size(); i++) {
            if (customers.get(i).status <= Customer.CITY_COUNTRY_STATUS_ARRIVED || customers.get(i).status == Customer.CITY_COUNTRY_STATUS_INVALID) {
                uncheck = uncheck + customers.get(i).ticketNumber;
            } else if (customers.get(i).status > Customer.CITY_COUNTRY_STATUS_ARRIVED && customers.get(i).status != Customer.CITY_COUNTRY_STATUS_INVALID) {
                check = check + customers.get(i).ticketNumber;
            }
        }
        tv_status_yes.setText(getResources().getString(R.string.cb_alredy_check) + " " + check);
        tv_status_no.setText(getResources().getString(R.string.cb_no_check) + " " + uncheck);

        /**
         * 根据未验票数显示布局
         */
        if (uncheck == 0) {

            iv_ticket.setVisibility(View.GONE);
            tv_check_hint.setText(getResources().getString(R.string.cb_go_next_station));

            lin_go_next.setVisibility(View.GONE);
            tv_go_next_btn.setVisibility(View.VISIBLE);
            tv_check_btn.setVisibility(View.GONE);

            tv_go_next_btn.setTextColor(getResources().getColor(R.color.white));
            tv_go_next_btn.setBackground(getResources().getDrawable(R.drawable.corners_btn_4dp_bg));

            lin_check_top.setOnClickListener(v -> {
                if (uncheck == 0) {
                    presenter.toNextStation(cbBusOrder.id, cbBusOrder.driverStationVos.get(position + 1).stationId);
                }
            });
        } else {
            iv_ticket.setVisibility(View.VISIBLE);
            tv_check_hint.setText(getResources().getString(R.string.cb_check_ticket));

            tv_check_btn.setVisibility(View.VISIBLE);
            tv_check_btn.setTextColor(getResources().getColor(R.color.white));
            tv_check_btn.setBackground(getResources().getDrawable(R.drawable.corners_btn_4dp_bg));

            lin_check_top.setOnClickListener(v -> {
                Intent intent = new Intent(this, CheckTicketActivity.class);
                startActivityForResult(intent, 0x00);
            });
        }
    }

    public void setMyResult() {
        Intent intent = new Intent();
        intent.putExtra("type", 2);
        setResult(RESULT_OK, intent);
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            getData();
        }
    }

    @Override
    public void onBackPressed() {
        cus_toolbar.leftIcon.callOnClick();
    }

    @Override
    public void onCancelOrder(long orderId, String orderType, String msg) {
        getData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        cancelOrderReceiver = new CancelOrderReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Config.BROAD_CANCEL_ORDER);
        filter.addAction(Config.BROAD_BACK_ORDER);
        registerReceiver(cancelOrderReceiver, filter);

        scheduleTurnReceiver = new ScheduleTurnReceiver(this);
        IntentFilter filter1 = new IntentFilter();
        filter1.addAction(Config.SCHEDULE_FINISH);
        registerReceiver(scheduleTurnReceiver, filter1);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(cancelOrderReceiver);
        unregisterReceiver(scheduleTurnReceiver);
    }

    @Override
    public void onTurnOrder(long id, String orderType, String msg) {
        finish();
    }
}
