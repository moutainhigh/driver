package com.easymi.personal.activity;

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
import com.easymi.component.utils.TimeUtil;
import com.easymi.component.widget.CusErrLayout;
import com.easymi.component.widget.SwipeRecyclerView;
import com.easymi.personal.McService;
import com.easymi.personal.R;
import com.easymi.personal.entity.MyPopularizeBean;
import com.easymi.personal.entity.MyPopularizeFeeDetailBean;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MyPopularizeFeeDetailActivity extends RxBaseActivity {

    private com.easymi.component.widget.CusToolbar myPopularizeFeeDetailCtb;
    private com.easymi.component.widget.SwipeRecyclerView myPopularizeFeeDetailRv;
    private android.widget.TextView itemMyPopularizeFeeDetailHeaderTvContent;
    private android.widget.TextView itemMyPopularizeFeeDetailHeaderTvPrise;
    private MyPopularizeBean myPopularizeBean;
    private BaseQuickAdapter<MyPopularizeFeeDetailBean, BaseViewHolder> adapter;

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_my_popularize_fee_detail;
    }


    private View getHeaderView() {
        View headerView = getLayoutInflater().inflate(R.layout.item_my_popularize_fee_detail_header, myPopularizeFeeDetailRv, false);
        itemMyPopularizeFeeDetailHeaderTvContent = headerView.findViewById(R.id.itemMyPopularizeFeeDetailHeaderTvContent);
        itemMyPopularizeFeeDetailHeaderTvPrise = headerView.findViewById(R.id.itemMyPopularizeFeeDetailHeaderTvPrise);
        itemMyPopularizeFeeDetailHeaderTvContent.setText(myPopularizeBean.passengerName + " " + new StringBuilder(myPopularizeBean.passengerPhone).replace(3, 7, "****"));
        itemMyPopularizeFeeDetailHeaderTvPrise.setText("¥" + CommonUtil.d2s(myPopularizeBean.commissionAmount, "0.00"));
        return headerView;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        myPopularizeBean = (MyPopularizeBean) getIntent().getSerializableExtra("data");
        myPopularizeFeeDetailRv = findViewById(R.id.myPopularizeFeeDetailRv);
        myPopularizeFeeDetailRv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BaseQuickAdapter<MyPopularizeFeeDetailBean, BaseViewHolder>(R.layout.item_my_popularize_fee_detail) {
            @Override
            protected void convert(BaseViewHolder helper, MyPopularizeFeeDetailBean item) {
                helper.setText(R.id.itemMyPopularizeFeeDetailTvTime, TimeUtil.getTime("yyyy-MM-dd HH:mm", item.finishTime * 1000))
                        .setText(R.id.itemMyPopularizeFeeDetailTvPrize, "¥" + CommonUtil.d2s(item.commissionFee, "0.00"));
            }
        };
        myPopularizeFeeDetailRv.getRecyclerView().setAdapter(adapter);
        myPopularizeFeeDetailRv.setOnLoadListener(new SwipeRecyclerView.OnLoadListener() {
            @Override
            public void onRefresh() {
                getData();
            }

            @Override
            public void onLoadMore() {

            }
        });

        myPopularizeFeeDetailRv.setRefreshing(true);

    }


    private void getData() {
        ApiManager.getInstance().createApi(Config.HOST, McService.class)
                .getPromoteDetail(myPopularizeBean.passengerId)
                .map(new HttpResultFunc2<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MySubscriber<List<MyPopularizeFeeDetailBean>>(this, false, false, new HaveErrSubscriberListener<List<MyPopularizeFeeDetailBean>>() {
                    @Override
                    public void onNext(List<MyPopularizeFeeDetailBean> myPopularizeFeeDetailBeans) {
                        adapter.setNewData(myPopularizeFeeDetailBeans);
                        if (myPopularizeFeeDetailBeans.isEmpty()) {
                            adapter.setEmptyView(new CusErrLayout(MyPopularizeFeeDetailActivity.this));
                        } else {
                            if (adapter.getHeaderLayoutCount() == 0) {
                                adapter.addHeaderView(getHeaderView());
                            }
                        }
                        myPopularizeFeeDetailRv.complete();
                    }

                    @Override
                    public void onError(int code) {
                        myPopularizeFeeDetailRv.complete();

                    }
                }));
    }


    @Override
    public void initToolBar() {
        super.initToolBar();
        myPopularizeFeeDetailCtb = findViewById(R.id.myPopularizeFeeDetailCtb);
        myPopularizeFeeDetailCtb.setTitle("推广消费明细").setLeftIcon(R.drawable.ic_arrow_back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
