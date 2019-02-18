package com.easymin.custombus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.ZCSetting;
import com.easymi.component.network.GsonUtil;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.CustomSlideToUnlockView;
import com.easymi.component.widget.LoadingButton;
import com.easymin.custombus.R;
import com.easymin.custombus.adapter.PassengerAdapter;
import com.easymin.custombus.entity.Customer;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
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
public class PassengerActivity extends RxBaseActivity {
    /**
     * 界面控件
     */
    CusToolbar cus_toolbar;
    TextView tv_station_name;
    TextView tv_ticket_status;
    TextView tv_countdown;
    TextView tv_countdown_hint;
    RecyclerView recyclerView;
    LinearLayout lin_bottom;
    TextView tv_no_check;
    LoadingButton btn_go_next;
    LinearLayout lin_time;
    ExpandableLayout expand;
    TextView tv_go_next;
    /**
     * 适配器
     */
    public PassengerAdapter adapter;

    /**
     * 乘客数据源
     */
    public List<Customer> passList = new ArrayList<>();

    private long booktime;

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
        booktime = getIntent().getLongExtra("booktime", 0);
        findById();
        initAdapter();
        initListener();
        getData();
    }

    @Override
    public void initToolBar() {
        super.initToolBar();
        cus_toolbar.setLeftIcon(R.drawable.ic_arrow_back, v -> finish());
        cus_toolbar.setTitle(R.string.cb_passenger_info);
    }

    /**
     * 加载控件
     */
    public void findById() {
        cus_toolbar = findViewById(R.id.cus_toolbar);
        tv_station_name = findViewById(R.id.tv_station_name);
        tv_ticket_status = findViewById(R.id.tv_ticket_status);
        tv_countdown = findViewById(R.id.tv_countdown);
        tv_countdown_hint = findViewById(R.id.tv_countdown_hint);
        recyclerView = findViewById(R.id.recyclerView);
        lin_bottom = findViewById(R.id.lin_bottom);
        tv_no_check = findViewById(R.id.tv_no_check);
        btn_go_next = findViewById(R.id.btn_go_next);
        lin_time = findViewById(R.id.lin_time);
        expand = findViewById(R.id.expand);
        tv_go_next = findViewById(R.id.tv_go_next);
    }

    /**
     * 初始化适配器
     */
    public void initAdapter() {
        adapter = new PassengerAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

    }

    /**
     * 初始化监听
     */
    public void initListener() {
        tv_no_check.setOnClickListener(v -> {
            Intent intent = new Intent(this, CheckTicketActivity.class);
            startActivity(intent);
        });
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
        lin_time.setVisibility(View.VISIBLE);

        if (null != timer) {
            timer.cancel();
            timer = null;
        }
        if (null != timerTask) {
            timerTask.cancel();
            timerTask = null;
        }

        timeSeq = (booktime * 1000 - System.currentTimeMillis()) / 1000;
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
                tv_countdown_hint.setText(getResources().getString(R.string.cb_wait_countdown));
                tv_countdown.setText(sb.toString());
                tv_countdown.setTextColor(getResources().getColor(R.color.color_FF485E));
                tv_countdown_hint.setTextColor(getResources().getColor(R.color.color_FF485E));
            } else {
                //正常计时
                tv_countdown_hint.setText(getResources().getString(R.string.cb_wait_timeout));
                tv_countdown.setText(sb.toString());
                tv_countdown.setTextColor(getResources().getColor(R.color.color_089A55));
                tv_countdown_hint.setTextColor(getResources().getColor(R.color.color_999999));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelTimer();
    }

    public void getData(){
        passList = GsonUtil.parseToArrayList(json, Customer.class);
        adapter.setDatas(passList);
        if (booktime != 0){
            setWaitTime();
        }else {

        }
    }


    public String json = "[\n" +
            "{\n" +
            "            \"id\":1,\n" +
            "            \"name\":\"珠江国际\",\n" +
            "            \"status\":1,\n" +
            "            \"tickets\":1,\n" +
            "            \"phone\":\"18180635910\",\n" +
            "            \"pic\":\"http://img1.3lian.com/img013/v5/21/d/84.jpg\"      \n" +
            "        },\n" +
            "{\n" +
            "          \"id\":2,\n" +
            "            \"name\":\"胡峰\",\n" +
            "            \"status\":1,\n" +
            "            \"tickets\":2,\n" +
            "            \"phone\":\"18180635911\",\n" +
            "            \"pic\":\"http://img1.3lian.com/img013/v5/21/d/84.jpg\"         \n" +
            "        },\n" +
            "{\n" +
            "           \"id\":3,\n" +
            "            \"name\":\"胡大大\",\n" +
            "            \"status\":1,\n" +
            "            \"tickets\":3,\n" +
            "            \"phone\":\"18180635912\",\n" +
            "            \"pic\":\"http://img1.3lian.com/img013/v5/21/d/84.jpg\"   \n" +
            "        },\n" +
            "{\n" +
            "           \"id\":3,\n" +
            "            \"name\":\"胡小小\",\n" +
            "            \"status\":1,\n" +
            "            \"tickets\":4,\n" +
            "            \"phone\":\"18180635913\",\n" +
            "            \"pic\":\"http://img1.3lian.com/img013/v5/21/d/84.jpg\"   \n" +
            "        }\n" +
            "]";


}
