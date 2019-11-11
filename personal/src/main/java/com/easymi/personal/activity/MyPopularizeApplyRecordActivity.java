package com.easymi.personal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.easymi.component.Config;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.HttpResultFunc2;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.TimeUtil;
import com.easymi.component.widget.CusErrLayout;
import com.easymi.component.widget.SwipeRecyclerView;
import com.easymi.personal.McService;
import com.easymi.personal.R;
import com.easymi.personal.entity.MyPopularizeRecordBean;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MyPopularizeApplyRecordActivity extends RxBaseActivity {

    private com.easymi.component.widget.CusToolbar myPopularizeApplyRecordCtb;
    private com.easymi.component.widget.SwipeRecyclerView myPopularizeApplyRecordRv;
    private BaseQuickAdapter<MyPopularizeRecordBean, BaseViewHolder> adapter;

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_my_popularize_apply_record;
    }


    @Override
    public void initToolBar() {
        super.initToolBar();
        myPopularizeApplyRecordCtb = findViewById(R.id.myPopularizeApplyRecordCtb);
        myPopularizeApplyRecordCtb.setTitle("申请记录").setLeftIcon(R.drawable.ic_arrow_back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        myPopularizeApplyRecordRv = findViewById(R.id.myPopularizeApplyRecordRv);
        myPopularizeApplyRecordRv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BaseQuickAdapter<MyPopularizeRecordBean, BaseViewHolder>(R.layout.item_my_popularize_apply_record) {
            @Override
            protected void convert(BaseViewHolder helper, MyPopularizeRecordBean item) {
                helper.setText(R.id.itemMyPopularizeApplyRecordTvTime, TimeUtil.getTime("yyyy-MM-dd HH:mm", item.created * 1000))
                        .setText(R.id.itemMyPopularizeApplyRecordTvPrize, "¥" + item.settlementTotal)
                        .setText(R.id.itemMyPopularizeApplyRecordTvStatus, item.status == 1 ? "审核中" : "已通过")
                        .setTextColor(R.id.itemMyPopularizeApplyRecordTvStatus, ContextCompat.getColor(MyPopularizeApplyRecordActivity.this, item.status == 1 ? R.color.colorYellow : R.color.colorBlackLight))
                        .setGone(R.id.itemMyPopularizeApplyRecordLl, !TextUtils.isEmpty(item.remark))
                        .setText(R.id.itemMyPopularizeApplyRecordTvContent, item.remark);
            }
        };
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(MyPopularizeApplyRecordActivity.this, MyPopularizeProcessActivity.class);
                intent.putExtra("data", (MyPopularizeRecordBean) adapter.getData().get(position));
                startActivity(intent);
            }
        });

        myPopularizeApplyRecordRv.getRecyclerView().setAdapter(adapter);
        myPopularizeApplyRecordRv.setOnLoadListener(new SwipeRecyclerView.OnLoadListener() {
            @Override
            public void onRefresh() {
                getData();
            }

            @Override
            public void onLoadMore() {

            }
        });
        myPopularizeApplyRecordRv.setRefreshing(true);
    }

    private void getData() {
        ApiManager.getInstance().createApi(Config.HOST, McService.class)
                .getPromoteRecords(EmUtil.getEmployInfo().phone, 2)
                .map(new HttpResultFunc2<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MySubscriber<List<MyPopularizeRecordBean>>(this, false, false, new HaveErrSubscriberListener<List<MyPopularizeRecordBean>>() {
                    @Override
                    public void onNext(List<MyPopularizeRecordBean> myPopularizeRecordBeans) {
                        adapter.setNewData(myPopularizeRecordBeans);
                        adapter.setEmptyView(new CusErrLayout(MyPopularizeApplyRecordActivity.this));
                        myPopularizeApplyRecordRv.complete();
                    }

                    @Override
                    public void onError(int code) {
                        myPopularizeApplyRecordRv.complete();
                    }
                }));
    }

}
