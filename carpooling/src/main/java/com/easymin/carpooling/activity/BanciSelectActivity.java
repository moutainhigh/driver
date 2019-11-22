package com.easymin.carpooling.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.easymi.component.Config;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.HttpResultFunc2;
import com.easymi.component.network.HttpResultFunc3;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.network.NoErrSubscriberListener;
import com.easymi.component.result.EmResult2;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.CusErrLayout;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.SwipeRecyclerView;
import com.easymin.carpooling.CarPoolApiService;
import com.easymin.carpooling.R;
import com.easymin.carpooling.adapter.BanciAdapter;
import com.easymin.carpooling.entity.LineBean;
import com.easymin.carpooling.entity.LineOffsetBean;
import com.easymin.carpooling.entity.PincheOrder;
import com.easymin.carpooling.entity.TimeSlotBean;
import com.easymin.carpooling.widget.BottomListDialog;
import com.easymin.carpooling.widget.TimePickerDialog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: BanciSelectActivity
 * @Author: hufeng
 * @Date: 2019/5/21 上午11:51
 * @Description:
 * @History:
 */

public class BanciSelectActivity extends RxBaseActivity implements View.OnClickListener {
    SwipeRecyclerView recyclerView;

    BanciAdapter adapter;

    CusToolbar toolbar;

    CusErrLayout errLayout;

    TextView tv_hint;

    TextView tv_line_name;
    TextView tv_time_sort;
    TextView tv_sure;


    private List<PincheOrder> orders;
    private LineOffsetBean lineOffsetBean;

    @Override
    public boolean isEnableSwipe() {
        return true;
    }

    @Override
    public int getLayoutId() {
        return R.layout.pc_activity_banci_select;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        recyclerView = findViewById(R.id.recyclerView);
        errLayout = findViewById(R.id.cus_err_layout);
        tv_hint = findViewById(R.id.tv_hint);

        tv_line_name = findViewById(R.id.tv_line_name);
        tv_time_sort = findViewById(R.id.tv_time_sort);
        tv_sure = findViewById(R.id.tv_sure);

        tv_sure.setOnClickListener(this);

        orders = new ArrayList<>();

        initToolBar();
        initRecycler();
        recyclerView.setRefreshing(true);

        getConfig();
    }

    /**
     * 初始化列表
     */
    private void initRecycler() {
        adapter = new BanciAdapter(this);
        adapter.setOnItemClickListener(pincheOrder -> {

            if (pincheOrder.status == PincheOrder.SCHEDULE_STATUS_FINISH) {
                ToastUtil.showMessage(BanciSelectActivity.this, "该次已完成，无法选择");
                return;
            }

            if (pincheOrder.seats == 0) {
                ToastUtil.showMessage(BanciSelectActivity.this, "该时段没有余票，不能补单");
                return;
            }

            Intent intent = new Intent();
            intent.putExtra("pincheOrder", pincheOrder);
            setResult(RESULT_OK, intent);
            finish();
        });

        recyclerView.getRecyclerView().setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setLoadMoreEnable(false);
        recyclerView.setAdapter(adapter);
        recyclerView.setOnLoadListener(new SwipeRecyclerView.OnLoadListener() {
            @Override
            public void onRefresh() {
                queryDriverSchedule();
            }

            @Override
            public void onLoadMore() {
                queryDriverSchedule();
            }
        });
    }

    @Override
    public void initToolBar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("选择时段");
        toolbar.setLeftIcon(R.drawable.ic_arrow_back, v -> finish());
    }

    /**
     * 查询司机班次
     */
    private void queryDriverSchedule() {
        Observable<EmResult2<List<PincheOrder>>> observable = ApiManager.getInstance().createApi(Config.HOST, CarPoolApiService.class)
                .queryDriverSchedule(EmUtil.getEmployId())
                .filter(new HttpResultFunc3<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(BanciSelectActivity.this,
                false,
                false,
                new HaveErrSubscriberListener<EmResult2<List<PincheOrder>>>() {
                    @Override
                    public void onNext(EmResult2<List<PincheOrder>> result2) {
                        recyclerView.complete();
                        orders.clear();
                        orders.addAll(result2.getData() == null ? new ArrayList<>() : result2.getData());
                        adapter.setBaseOrders(orders);

                        recyclerView.setLoadMoreEnable(false);

                        if (orders.size() == 0) {
                            errLayout.setVisibility(View.GONE);
                            tv_hint.setVisibility(View.GONE);
                        } else {
                            hideErr();
                        }
                    }

                    @Override
                    public void onError(int code) {
                        recyclerView.complete();
                        showErr(code);
                    }
                })));
    }

    /**
     * @param tag 0代表空数据  其他代表网络问题
     */
    private void showErr(int tag) {
        if (tag != 0) {
            errLayout.setErrText(tag);
            errLayout.setErrImg();
        }
        errLayout.setVisibility(View.VISIBLE);
        tv_hint.setVisibility(View.GONE);
        errLayout.setOnClickListener(v -> {
//            hideErr();
            recyclerView.setRefreshing(true);
        });
    }

    /**
     * 显示错误布局
     */
    private void hideErr() {
        errLayout.setVisibility(View.GONE);
        tv_hint.setVisibility(View.VISIBLE);
    }

    /**
     * 司机可补单线路的数据
     */
    List<LineBean> lineBeans = new ArrayList<>();

    /**
     * 选择线路的下标
     */
    int position = -1;

    TimeSlotBean selctTimeSort = null;

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tv_line_name) {
            BottomListDialog dialog = new BottomListDialog(this, lineBeans)
                    .setOnSelectListener(index -> {
                        if (index != position) {
                            this.position = index;
                            tv_line_name.setText(lineBeans.get(index).lineName);

                            selctTimeSort = null;
                            tv_time_sort.setText("");
                        }
                    });
            dialog.setTitle("选择路线");
            dialog.show();
        } else if (i == R.id.tv_time_sort) {
            if (position == -1) {
                ToastUtil.showMessage(this, "请先选择线路");
                return;
            }
            if (EmUtil.getEmployInfo().countNoSchedule > 0) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(this, lineOffsetBean.timeNo, lineOffsetBean.stopTimeNo, lineOffsetBean.spanNo);
                timePickerDialog.show();
            } else {
                BottomListDialog dialog = new BottomListDialog(this, lineBeans.get(position).timeSlotVoList)
                        .setOnSelectListener(index -> {
                            selctTimeSort = lineBeans.get(position).timeSlotVoList.get(index);
                            tv_time_sort.setText(selctTimeSort.day + " " + selctTimeSort.timeSlot + " 余票：" + (selctTimeSort.tickets == null ? "充足" : selctTimeSort.tickets));
                        });
                dialog.setTitle("用车时段");
                dialog.show();
            }
        } else if (i == R.id.tv_sure) {
            if (position == -1) {
                ToastUtil.showMessage(this, "请选择线路");
                return;
            }
            if (selctTimeSort == null) {
                ToastUtil.showMessage(this, "请选择时段");
                return;
            }

            Intent intent = new Intent();
            intent.putExtra("selctTimeSort", selctTimeSort);
            setResult(RESULT_OK, intent);
            finish();
        }
    }


    private void getLineOffset() {
        ApiManager.getInstance().createApi(Config.HOST, CarPoolApiService.class)
                .getLineOffset(EmUtil.getEmployId())
                .map(new HttpResultFunc2<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MySubscriber<LineOffsetBean>(this, true, false, new NoErrSubscriberListener<LineOffsetBean>() {
                    @Override
                    public void onNext(LineOffsetBean o) {
                        lineOffsetBean = o;
                        setViewStatus(false);
                        LineBean lineBean = new LineBean();
                        lineBean.lineName = o.name;
                        lineBeans.clear();
                        lineBeans.add(lineBean);
                    }
                }));
    }

    private void getConfig() {
        if (EmUtil.getEmployInfo().countNoSchedule > 0) {
            getLineOffset();
        } else {
            getLineDriverSchedule();
        }
    }

    /**
     * 查询专线订单
     */
    private void getLineDriverSchedule() {
        Observable<EmResult2<List<LineBean>>> observable = ApiManager.getInstance().createApi(Config.HOST, CarPoolApiService.class)
                .getLineDriverSchedule(EmUtil.getEmployId(), EmUtil.getEmployInfo().companyId)
                .filter(new HttpResultFunc3<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(BanciSelectActivity.this,
                true,
                true,
                new HaveErrSubscriberListener<EmResult2<List<LineBean>>>() {
                    @Override
                    public void onNext(EmResult2<List<LineBean>> result2) {
                        if (result2.getData() == null || result2.getData().size() == 0) {
                            setViewStatus(true);
                        } else {
                            setViewStatus(false);
                            removeEmptyTickets(result2.getData());
                        }
                    }

                    @Override
                    public void onError(int code) {

                    }
                })));
    }


    /**
     * 移除掉
     *
     * @param list
     */
    public void removeEmptyTickets(List<LineBean> list) {
        for (LineBean lineBean : list) {
            Iterator iterator = lineBean.timeSlotVoList.iterator();
            while (iterator.hasNext()) {
                TimeSlotBean timeSlotBean = (TimeSlotBean) iterator.next();
                if (timeSlotBean.tickets != null && timeSlotBean.tickets == 0) {
                    iterator.remove();
                }
            }
        }

        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            LineBean lineBean = (LineBean) iterator.next();
            if (lineBean.timeSlotVoList.size() == 0) {
                iterator.remove();
            }
        }

        lineBeans.clear();
        lineBeans.addAll(list);
    }

    //设置界面状态
    public void setViewStatus(boolean isEmpty) {
        if (isEmpty) {
            tv_line_name.setHint("没有可选择的线路");
            tv_line_name.setHintTextColor(getResources().getColor(R.color.color_999999));
            tv_line_name.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            tv_time_sort.setHint("没有可选择的时段");
            tv_time_sort.setHintTextColor(getResources().getColor(R.color.color_999999));
            tv_time_sort.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

            tv_sure.setBackgroundResource(R.drawable.shape_btn_gray_4dp_bg);
        } else {
            tv_line_name.setHint("请选择线路");
            tv_line_name.setHintTextColor(getResources().getColor(R.color.color_cccccc));
            tv_line_name.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.ic_right), null);
            tv_time_sort.setHint("请选择时段");
            tv_time_sort.setHintTextColor(getResources().getColor(R.color.color_cccccc));
            tv_time_sort.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.ic_right), null);
            tv_sure.setBackgroundResource(R.drawable.shape_btn_4dp_bg);

            tv_line_name.setOnClickListener(this);
            tv_time_sort.setOnClickListener(this);
        }
    }

}