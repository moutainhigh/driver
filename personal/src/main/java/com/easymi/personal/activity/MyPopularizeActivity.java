package com.easymi.personal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.easymi.component.Config;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.HttpResultFunc2;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.utils.CommonUtil;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.TimeUtil;
import com.easymi.component.utils.ToastUtil;
import com.easymi.personal.McService;
import com.easymi.personal.R;
import com.easymi.personal.entity.MyPopularizeBean;
import com.easymi.personal.entity.MyPopularizeMainBean;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MyPopularizeActivity extends RxBaseActivity implements View.OnClickListener {

    private com.easymi.component.widget.CusToolbar myPopularizeCtb;
    private android.support.v7.widget.RecyclerView myPopularizeRv;
    private BaseQuickAdapter<MyPopularizeBean, BaseViewHolder> adapter;
    private android.widget.TextView myPopularizeHeaderTvCount;
    private android.widget.TextView myPopularizeHeaderTvMoney;
    private android.widget.TextView myPopularizeHeaderTvRecord;
    private android.widget.TextView myPopularizeHeaderTvCountOn;


    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_my_popularize;
    }

    @Override
    public void initToolBar() {
        super.initToolBar();
        myPopularizeCtb = findViewById(R.id.myPopularizeCtb);
        myPopularizeCtb.setTitle("推广详情").setLeftIcon(R.drawable.ic_arrow_back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        myPopularizeHeaderTvCount = findViewById(R.id.myPopularizeHeaderTvCount);
        myPopularizeHeaderTvMoney = findViewById(R.id.myPopularizeHeaderTvMoney);
        myPopularizeHeaderTvRecord = findViewById(R.id.myPopularizeHeaderTvRecord);
        myPopularizeHeaderTvRecord.setOnClickListener(this);
        myPopularizeHeaderTvCountOn = findViewById(R.id.myPopularizeHeaderTvCountOn);
        myPopularizeHeaderTvCountOn.setOnClickListener(this);

        myPopularizeRv = findViewById(R.id.myPopularizeRv);
        myPopularizeRv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BaseQuickAdapter<MyPopularizeBean, BaseViewHolder>(R.layout.item_my_popularize) {
            @Override
            protected void convert(BaseViewHolder helper, MyPopularizeBean item) {
                helper.setText(R.id.itemMyPopularizeTvName, item.passengerName).setText(R.id.itemMyPopularizeTvPhone, item.passengerPhone)
                        .setText(R.id.itemMyPopularizeTvTime, TimeUtil.getTime("yyyy-MM-dd", item.created * 1000)).setText(R.id.itemMyPopularizeTvMoney, CommonUtil.d2s(item.commissionAmount, "0.00"));
            }
        };
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(MyPopularizeActivity.this, MyPopularizeFeeDetailActivity.class);
                intent.putExtra("data", (MyPopularizeBean) adapter.getData().get(position));
                startActivity(intent);
            }
        });

        myPopularizeRv.setAdapter(adapter);
        getData();
    }

    private void getData() {
        mRxManager.add(ApiManager.getInstance().createApi(Config.HOST, McService.class)
                .getPromoteAppList(EmUtil.getEmployInfo().phone)
                .map(new HttpResultFunc2<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MySubscriber<MyPopularizeMainBean>(this, true, false, new HaveErrSubscriberListener<MyPopularizeMainBean>() {
                    @Override
                    public void onNext(MyPopularizeMainBean myPopularizeMainBean) {
                        myPopularizeHeaderTvCount.setText(myPopularizeMainBean.passengerNum + "人推广总计");
                        myPopularizeHeaderTvMoney.setText("￥" + CommonUtil.d2s(myPopularizeMainBean.commissionAmount, "0.00"));
                        adapter.setNewData(myPopularizeMainBean.promoterPassengerVos);
                        myPopularizeRv.setVisibility(myPopularizeMainBean.promoterPassengerVos.size() > 0 ? View.VISIBLE : View.GONE);
                    }

                    @Override
                    public void onError(int code) {
                        ToastUtil.showMessage(MyPopularizeActivity.this, "数据出现错误,请重试...");
                        finish();
                    }
                })));

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        if (v.getId() == R.id.myPopularizeHeaderTvRecord) {
            intent.setClass(this, MyPopularizeApplyRecordActivity.class);
        } else if (v.getId() == R.id.myPopularizeHeaderTvCountOn) {
            intent.setClass(this, MyPopularizeCountOnActivity.class);
        }
        startActivity(intent);
    }
}
