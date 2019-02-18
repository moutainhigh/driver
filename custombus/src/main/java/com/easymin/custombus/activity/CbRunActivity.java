package com.easymin.custombus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.easymi.component.ZCOrderStatus;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.network.GsonUtil;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.utils.CountDownUtils;
import com.easymi.component.utils.DensityUtil;
import com.easymi.component.utils.DropDownAnim;
import com.easymi.component.utils.TimeUtil;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.CustomSlideToUnlockView;
import com.easymin.custombus.R;
import com.easymin.custombus.adapter.StationAdapter;
import com.easymin.custombus.entity.CbBusOrder;
import com.easymin.custombus.mvp.FlowContract;
import com.easymin.custombus.mvp.FlowPresenter;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.text.DecimalFormat;
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
public class CbRunActivity extends RxBaseActivity implements FlowContract.View {

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
    /**
     * 班次详情
     */
    CbBusOrder cbBusOrder;


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
        findById();
        presenter = new FlowPresenter(this, this);
        initListenner();
        initAdapter();
        getData();
    }

    @Override
    public void initToolBar() {
        super.initToolBar();
        cus_toolbar.setLeftIcon(R.drawable.ic_arrow_back, v -> finish());
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
    }

    /**
     * 初始化适配器
     */
    public void initAdapter() {
        stationAdapter = new StationAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(stationAdapter);
        stationAdapter.setOnItemClickListener(position -> {
            toPassenger(0);
        });
    }

    /**
     * 初始化监听
     */
    public void initListenner() {
        //开始行程倒计时
        countDownUtils = new CountDownUtils(this, System.currentTimeMillis() + 1 * 60 * 1000, (day, hour, minute) -> {
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
                if (cbBusOrder.status == 2) {
                    toPassenger(1);
                } else if (cbBusOrder.status == 1) {
                    expand.collapse();
                }
                slider.resetView();

                cbBusOrder.status = 2;
                cbBusOrder.siteList.get(0).status = 3;

                setData();
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
    public void toPassenger(int type) {
        Intent intent = new Intent(this, PassengerActivity.class);
        if (type == 1){
            intent.putExtra("booktime",cbBusOrder.booktime);
        }
        startActivity(intent);
    }

    public void getData() {
        cbBusOrder = GsonUtil.parseJson(json, CbBusOrder.class);
        setData();
    }

    /**
     * 加载数据到布局
     */
    public void setData() {
        if (cbBusOrder.status == 1) {
            lin_no_start.setVisibility(View.VISIBLE);
            tv_start_site.setText(cbBusOrder.startAddr);
            tv_end_site.setText(cbBusOrder.endAddr);
            tv_go_time.setText(TimeUtil.getTime("yyyy年MM月dd日 HH:mm", cbBusOrder.booktime * 1000) + "出发");
            lin_running.setVisibility(View.GONE);
            lin_start_countdown.setVisibility(View.VISIBLE);
            cus_toolbar.setTitle(R.string.cb_no_start);
        } else if (cbBusOrder.status == 2) {
            lin_no_start.setVisibility(View.GONE);
            lin_running.setVisibility(View.VISIBLE);
            lin_start_countdown.setVisibility(View.GONE);
        } else if (cbBusOrder.status == 3) {
            lin_no_start.setVisibility(View.GONE);
            lin_running.setVisibility(View.VISIBLE);
            lin_start_countdown.setVisibility(View.GONE);

            tv_stauts.setText(getResources().getString(R.string.cb_go_to));
            tv_station_name.setText(cbBusOrder.endAddr);
        }
        changeTop();
        stationAdapter.setDatas(cbBusOrder.siteList);
    }

    /**
     * 根据站点状态显示顶部布局和文字
     */
    public void changeTop() {
        for (int i = 0; i < cbBusOrder.siteList.size(); i++) {
            if (cbBusOrder.siteList.get(i).status == 2) {
                tv_stauts.setText(getResources().getString(R.string.cb_go_to));
                tv_station_name.setText(cbBusOrder.siteList.get(i).name);
                slider.setHint(getResources().getString(R.string.cb_slider_arrive));
                presenter.routePlanByNavi(cbBusOrder.siteList.get(i).lat, cbBusOrder.siteList.get(i).lng);
                cus_toolbar.setTitle(R.string.cb_running);
            } else if (cbBusOrder.siteList.get(i).status == 3) {
                tv_stauts.setText(getResources().getString(R.string.cb_arrived));
                tv_station_name.setText(cbBusOrder.siteList.get(i).name);
                tv_left.setText(getResources().getString(R.string.cb_please_check));
                slider.setHint(getResources().getString(R.string.cb_slider_check));
                cus_toolbar.setTitle(R.string.cb_arrive_station);
            }
        }
    }

    @Override
    public void showLeft(int dis, int time) {
        String disKm;
        int km = dis / 1000;
        if (km >= 1) {
            disKm = new DecimalFormat("#0.0").format((double) dis / 1000) + getResources().getString(R.string.cb_km);
        } else {
            disKm = dis + "米";
        }

        tv_left.setText(getResources().getString(R.string.cb_destance)
                + disKm
                + "  " + (time/60)
                + getResources().getString(R.string.cb_minutes));
    }

    @Override
    public RxManager getManager() {
        return mRxManager;
    }

    public String json = "{\"status\":1,\n" +
            "\"startAddr\":\"珠江国际\",\n" +
            "\"endAddr\":\"时代金悦\",\n" +
            "\"siteList\":[\n" +
            "{\n" +
            "            \"id\":1,\n" +
            "            \"name\":\"珠江国际\",\n" +
            "            \"status\":1,\n" +
            "            \"bookTime\":1550166000,\n" +
            "            \"address\":\"珠江国际中心写字楼\",\n" +
            "            \"lat\":30.456789,\n" +
            "            \"lng\":104.456789,\n" +
            "            \"number\":10        \n" +
            "        },\n" +
            "{\n" +
            "            \"id\":2,\n" +
            "            \"name\":\"丽景港\",\n" +
            "            \"status\":1,\n" +
            "            \"bookTime\":1550166000,\n" +
            "            \"address\":\"燎原路111号\",\n" +
            "            \"lat\":30.456790,\n" +
            "            \"lng\":104.456790,\n" +
            "            \"number\":11        \n" +
            "        },\n" +
            "{\n" +
            "            \"id\":3,\n" +
            "            \"name\":\"天来国际\",\n" +
            "            \"status\":1,\n" +
            "            \"bookTime\":1550166000,\n" +
            "            \"address\":\"光华大道天来国际\",\n" +
            "            \"lat\":30.456791,\n" +
            "            \"lng\":104.456791,\n" +
            "            \"number\":12        \n" +
            "        },\n" +
            "{\n" +
            "            \"id\":4,\n" +
            "            \"name\":\"时代金悦\",\n" +
            "            \"status\":1,\n" +
            "            \"bookTime\":1550166000,\n" +
            "            \"address\":\"金恒德时代\",\n" +
            "            \"lat\":30.456792,\n" +
            "            \"lng\":104.456792,\n" +
            "            \"number\":10        \n" +
            "        }\n" +
            "],\n" +
            "\"booktime\":1550470105\n" +
            "}";


}
