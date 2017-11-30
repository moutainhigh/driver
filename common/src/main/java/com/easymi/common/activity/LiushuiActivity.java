package com.easymi.common.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.easymi.common.CommApiService;
import com.easymi.common.R;
import com.easymi.common.adapter.LiuShuiAdapter;
import com.easymi.common.entity.BaseOrder;
import com.easymi.common.result.QueryOrdersResult;
import com.easymi.component.Config;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.network.NoErrSubscriberListener;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.TimeUtil;
import com.easymi.component.widget.CusBottomSheetDialog;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by liuzihao on 2017/11/14.
 */
@Route(path = "/common/LiushuiActivity")
public class LiushuiActivity extends RxBaseActivity {

    SwipeRecyclerView recyclerView;

    LiuShuiAdapter adapter;

    TabLayout tabLayout;

    CusToolbar toolbar;

    private String business;

    private Long startTime;
    private Long endTime;

    @Override
    public int getLayoutId() {
        return R.layout.activity_liushui;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        recyclerView = findViewById(R.id.recyclerView);
        tabLayout = findViewById(R.id.tab_layout);


        initToolBar();

        initTab();

        initRecycler();
    }

    private void initRecycler() {
        adapter = new LiuShuiAdapter(this);
        recyclerView.getRecyclerView().setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setLoadMoreEnable(false);
        recyclerView.setAdapter(adapter);
        recyclerView.setOnLoadListener(new SwipeRecyclerView.OnLoadListener() {
            @Override
            public void onRefresh() {
                queryOrders();
                recyclerView.complete();
            }

            @Override
            public void onLoadMore() {

            }
        });
    }

    private void initTab() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText().equals("代驾")) {
                    business = "daijia";
                    queryOrders();
                    recyclerView.setRefreshing(true);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        TabLayout.Tab daijiaTab = tabLayout.newTab();
        daijiaTab.setText(getString(R.string.create_daijia));
        daijiaTab.select();
        tabLayout.addTab(daijiaTab);
//        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.create_zhuanche)));
//        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.create_paotui)));
//        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.create_huoyun)));
//        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.create_zhuanxian)));
    }

    @Override
    public void initToolBar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setLeftIcon(View.VISIBLE, R.drawable.ic_arrow_back, v -> finish());
        toolbar.setRightIcon(View.VISIBLE, R.drawable.ic_more_horiz_white_24dp, v -> {
            CusBottomSheetDialog dialog = new CusBottomSheetDialog(LiushuiActivity.this);
            View view = LayoutInflater.from(LiushuiActivity.this).
                    inflate(R.layout.timezone_dialog, null, false);
            NumberPicker picker = view.findViewById(R.id.timezone_picker);
            picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
            String[] ls = new String[]{
                    getString(R.string.liushui_all),
                    getString(R.string.liushui_today),
                    getString(R.string.liushui_yesterday),
                    getString(R.string.liushui_this_week),
                    getString(R.string.liushui_this_month)
            };
            picker.setDisplayedValues(ls);
            picker.setMaxValue(ls.length - 1);
            picker.setMinValue(0);
            picker.setValue(0);
            TextView cancel = view.findViewById(R.id.cancel);
            TextView confirm = view.findViewById(R.id.confirm);
            cancel.setOnClickListener(v12 -> dialog.dismiss());
            confirm.setOnClickListener(v1 -> {
                dialog.dismiss();
                String time = picker.getDisplayedValues()[picker.getValue()];
                if (time.equals(getString(R.string.liushui_all))) {
                    startTime = null;
                    endTime = null;
                } else if (time.equals(getString(R.string.liushui_today))) {
                    startTime = TimeUtil.getDayBegin().getTime();
                    endTime = TimeUtil.getDayEnd().getTime();
                } else if (time.equals(getString(R.string.liushui_yesterday))) {
                    startTime = TimeUtil.getBeginDayOfYesterday().getTime();
                    endTime = TimeUtil.getEndDayOfYesterDay().getTime();
                } else if (time.equals(getString(R.string.liushui_this_week))) {
                    startTime = TimeUtil.getBeginDayOfWeek().getTime();
                    endTime = TimeUtil.getEndDayOfWeek().getTime();
                } else if (time.equals(getString(R.string.liushui_this_month))) {
                    startTime = TimeUtil.getBeginDayOfMonth().getTime();
                    endTime = TimeUtil.getEndDayOfMonth().getTime();
                }
                queryOrders();
            });
            dialog.setContentView(view);
            dialog.show();
        });
    }

    private void queryOrders() {
        Long driverId = EmUtil.getEmployId();

        Observable<QueryOrdersResult> observable = ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .queryOverOrdersByBunsiness(driverId, business, startTime, endTime, Config.APP_KEY, 1, 100)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(LiushuiActivity.this,
                false,
                false, new HaveErrSubscriberListener<QueryOrdersResult>() {
            @Override
            public void onNext(QueryOrdersResult queryOrdersResult) {
                recyclerView.complete();
                List<BaseOrder> orders = queryOrdersResult.orders;
                if (null != orders) {
                    adapter.setBaseOrders(orders);
                } else {
                    adapter.setBaseOrders(new ArrayList<>());
                }
            }

            @Override
            public void onError(int code) {
                recyclerView.complete();
            }
        })));
    }
}
