package com.easymi.personal.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.easymi.component.Config;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.result.EmResult;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.widget.CusErrLayout;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.SwipeRecyclerView;
import com.easymi.personal.McService;
import com.easymi.personal.R;
import com.easymi.personal.adapter.NotifityAdapter;
import com.easymi.personal.entity.Notifity;
import com.easymi.personal.result.NotifityResult;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by developerLzh on 2017/11/11 0011.
 */
@Route(path = "/personal/NotifityActivity")
public class NotifityActivity extends RxBaseActivity {

    CusToolbar toolbar;

    SwipeRecyclerView recyclerView;

    NotifityAdapter adapter;

    private int page = 1;

    private List<Notifity> notifities;

    CusErrLayout errLayout;

    @Override
    public int getLayoutId() {
        return R.layout.activity_notifity;
    }

    @Override
    public void initToolBar() {
        toolbar = findViewById(R.id.cus_toolbar);
        toolbar.setLeftBack(v -> finish());
        toolbar.setTitle(R.string.msg_notify);
        toolbar.setRightText(R.string.all_read, v -> readAll());
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new NotifityAdapter(this);

        adapter.setListener(this::readOne);

        notifities = new ArrayList<>();

        errLayout = findViewById(R.id.cus_err_layout);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        recyclerView.setOnLoadListener(new SwipeRecyclerView.OnLoadListener() {
            @Override
            public void onRefresh() {
                page = 1;
                queryData();
            }

            @Override
            public void onLoadMore() {
                page++;
                queryData();
            }
        });
        recyclerView.setRefreshing(true);
    }

    private void queryData() {
        Observable<NotifityResult> observable = ApiManager.getInstance().createApi(Config.HOST, McService.class)
                .notices(EmUtil.getEmployId(), Config.APP_KEY, page, 10)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, false,
                false, new HaveErrSubscriberListener<NotifityResult>() {
            @Override
            public void onNext(NotifityResult notifityResult) {
                recyclerView.complete();
                if (page == 1) {
                    notifities.clear();
                }
                if (notifityResult.employNoticeRecords != null) {
                    notifities.addAll(notifityResult.employNoticeRecords);
                }
                adapter.setList(notifities);
                if (notifityResult.total <= page * 10) {
                    recyclerView.setLoadMoreEnable(false);
                } else {
                    recyclerView.setLoadMoreEnable(true);
                }
                if (notifities.size() == 0) {
                    showErr(0);
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

    private void readOne(long id, int position) {
        Observable<EmResult> observable = ApiManager.getInstance().createApi(Config.HOST, McService.class)
                .readNotice(id, Config.APP_KEY)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<EmResult>(NotifityActivity.this, false,
                false, emResult -> {
            notifities.get(position).state = 2;
            adapter.notifyItemChanged(position);
        })));
    }

    private void readAll() {
        Observable<EmResult> observable = ApiManager.getInstance().createApi(Config.HOST, McService.class)
                .readAll(EmUtil.getEmployId(), Config.APP_KEY)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<EmResult>(NotifityActivity.this, true,
                true, emResult -> {
            recyclerView.onRefresh();
            recyclerView.setRefreshing(true);
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
        errLayout.setOnClickListener(v -> {
            hideErr();
            recyclerView.setRefreshing(true);
        });
    }

    private void hideErr() {
        errLayout.setVisibility(View.GONE);
    }

    @Override
    public boolean isEnableSwipe() {
        return true;
    }

}
