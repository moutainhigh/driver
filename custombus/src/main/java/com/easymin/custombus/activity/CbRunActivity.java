package com.easymin.custombus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.utils.CountDownUtils;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.CustomSlideToUnlockView;
import com.easymin.custombus.R;
import com.easymin.custombus.adapter.StationAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: CbRunActivity
 * @Author: hufeng
 * @Date: 2019/2/14 下午1:55
 * @Description: 定制班车跑单主界面
 * @History:
 */
@Route(path = "/custombus/CbRunActivity")
public class CbRunActivity extends RxBaseActivity {

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

    /**
     * 倒计时工具类
     */
    public CountDownUtils countDownUtils;

    /**
     * 站点适配器
     */
    public StationAdapter stationAdapter;

    /**
     * 站点数据源
     */
    private List<String> listStation = new ArrayList<>();

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
        findById();
        initListenner();
        initAdapter();
    }

    @Override
    public void initToolBar() {
        super.initToolBar();
        cus_toolbar.setLeftIcon(R.drawable.ic_arrow_back, v -> finish());
        cus_toolbar.setTitle(R.string.cb_no_start);
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
    }

    /**
     * 初始化适配器
     */
    public void initAdapter(){
        stationAdapter = new StationAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(stationAdapter);

        for (int i = 0;i<10;i++){
            listStation.add("");
        }
        stationAdapter.setDatas(listStation);
    }

    /**
     * 初始化监听
     */
    public void initListenner() {
        //开始行程倒计时
        countDownUtils =  new CountDownUtils(this, System.currentTimeMillis() + 10 * 60 * 1000, (day, hour, minute) -> {
            tv_day.setText("" + day);
            tv_hour.setText("" + hour);
            tv_fen.setText("" + minute);
        });

        slider.setmCallBack(new CustomSlideToUnlockView.CallBack() {
            @Override
            public void onSlide(int distance) {

            }

            @Override
            public void onUnlocked() {
                toPassenger();
                slider.resetView();
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        countDownUtils.cancelTimer();
    }

    /**
     * 跳转乘客信息列表
     */
    public void toPassenger(){
        Intent intent = new Intent(this,PassengerActivity.class);
        startActivity(intent);
    }
}
