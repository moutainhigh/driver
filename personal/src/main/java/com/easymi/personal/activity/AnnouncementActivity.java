package com.easymi.personal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.easymi.component.Config;
import com.easymi.component.activity.WebActivity;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.result.EmResult;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.SwipeRecyclerView;
import com.easymi.personal.McService;
import com.easymi.personal.R;
import com.easymi.personal.adapter.AnnouncementAdapter;
import com.easymi.personal.adapter.NotifityAdapter;
import com.easymi.personal.entity.Announcement;
import com.easymi.personal.result.AnnouncementResult;
import com.easymi.personal.result.NotifityResult;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by developerLzh on 2017/11/11 0011.
 */
@Route(path = "/personal/AnnouncementActivity")
public class AnnouncementActivity extends RxBaseActivity {

    CusToolbar toolbar;

    SwipeRecyclerView recyclerView;

    AnnouncementAdapter adapter;

    private int page = 1;

    private List<Announcement> notifities;

    @Override
    public int getLayoutId() {
        return R.layout.activity_notifity;
    }

    @Override
    public void initToolBar() {
        toolbar = findViewById(R.id.cus_toolbar);
        toolbar.setTitle(R.string.announcement);
        toolbar.setLeftBack(v -> finish());
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new AnnouncementAdapter(this);

        adapter.setListener(announcement -> {
            Intent intent = new Intent(AnnouncementActivity.this, WebActivity.class);
            intent.putExtra("title", announcement.message);
            intent.putExtra("url", announcement.url);
            startActivity(intent);
        });

        notifities = new ArrayList<>();

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
        Observable<AnnouncementResult> observable = ApiManager.getInstance().createApi(Config.HOST, McService.class)
                .employAffiches(EmUtil.getEmployInfo().company_id, Config.APP_KEY, page, 10)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, false,
                false, notifityResult -> {
            recyclerView.complete();
            if (page == 1) {
                notifities.clear();
            }
            notifities.addAll(notifityResult.employAffiches);
            adapter.setList(notifities);
            if (notifityResult.total <= page * 10) {
                recyclerView.setLoadMoreEnable(false);
            } else {
                recyclerView.setLoadMoreEnable(true);
            }
        })));
    }
}
