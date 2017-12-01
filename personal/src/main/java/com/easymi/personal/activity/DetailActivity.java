package com.easymi.personal.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.TextView;

import com.easymi.component.Config;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.Employ;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.network.NoErrSubscriberListener;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.widget.SwipeRecyclerView;
import com.easymi.personal.McService;
import com.easymi.personal.R;
import com.easymi.personal.adapter.DetailAdapter;
import com.easymi.personal.entity.Detail;
import com.easymi.personal.result.LiushuiResult;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by developerLzh on 2017/11/11 0011.
 */

public class DetailActivity extends RxBaseActivity {

    SwipeRecyclerView recyclerView;

    DetailAdapter adapter;

    TextView balanceText;

    private int page = 1;

    private List<Detail> details = new ArrayList<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_detail;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        recyclerView = findViewById(R.id.recyclerView);
        balanceText = findViewById(R.id.balance_text);
        adapter = new DetailAdapter(this);

        Employ employ = EmUtil.getEmployInfo();
        balanceText.setText(String.valueOf("¥" + employ.balance));

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        recyclerView.setOnLoadListener(new SwipeRecyclerView.OnLoadListener() {
            @Override
            public void onRefresh() {
                page = 1;
                getLiushui();
            }

            @Override
            public void onLoadMore() {
                page++;
                getLiushui();
            }
        });

        getLiushui();
        recyclerView.setRefreshing(true);

    }

    private void getLiushui() {
        McService api = ApiManager.getInstance().createApi(Config.HOST, McService.class);

        Observable<LiushuiResult> observable = api
                .getLiushui(page, 10, null, null, Config.APP_KEY, EmUtil.getEmployInfo().company_id, EmUtil.getEmployId())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, false, false, new HaveErrSubscriberListener<LiushuiResult>() {
            @Override
            public void onNext(LiushuiResult liushuiResult) {
                recyclerView.complete();
                if (page == 1) {
                    details.clear();
                }
                details.addAll(liushuiResult.driverPreSaveSerials);
                if (liushuiResult.total > page * 10) {
                    recyclerView.setLoadMoreEnable(true);
                } else {
                    recyclerView.setLoadMoreEnable(false);
                }
                adapter.setList(details);
            }

            @Override
            public void onError(int code) {
                recyclerView.complete();
            }
        })));
    }
}
