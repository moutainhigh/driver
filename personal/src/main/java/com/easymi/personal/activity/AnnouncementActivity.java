package com.easymi.personal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.easymi.component.Config;
import com.easymi.component.activity.WebActivity;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.widget.CusErrLayout;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.SwipeRecyclerView;
import com.easymi.personal.McService;
import com.easymi.personal.R;
import com.easymi.personal.adapter.AnnouncementAdapter;
import com.easymi.personal.entity.Announcement;
import com.easymi.personal.result.AnnouncementResult;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: AnnouncementActivity
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description: 公告列表
 * History:
 */

@Route(path = "/personal/AnnouncementActivity")
public class AnnouncementActivity extends RxBaseActivity {

    /**
     * 自定义标题栏
     */
    CusToolbar toolbar;

    /**
     * 列表控件
     */
    SwipeRecyclerView recyclerView;

    /**
     * 公告适配器
     */
    AnnouncementAdapter adapter;

    /**
     * 分页
     */
    private int page = 1;
    /**
     * 公告数据集合
     */
    private List<Announcement> notifities;

    /**
     * 错误显示布局
     */
    CusErrLayout errLayout;

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
        errLayout = findViewById(R.id.cus_err_layout);
        adapter = new AnnouncementAdapter(this);

        adapter.setListener(announcement -> {
            Intent intent = new Intent(AnnouncementActivity.this, ArticleActivity.class);
            intent.putExtra("title", announcement.message);
            intent.putExtra("annId", announcement.id);
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

    /**
     * 查询公告列表
     */
    private void queryData() {
        Observable<AnnouncementResult> observable = ApiManager.getInstance().createApi(Config.HOST, McService.class)
                .employAffiches(page, 10)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, false,
                false, new HaveErrSubscriberListener<AnnouncementResult>() {
            @Override
            public void onNext(AnnouncementResult announcementResult) {
                recyclerView.complete();
                if (page == 1) {
                    notifities.clear();
                }
                if(null != announcementResult.employAffiches){
                    notifities.addAll(announcementResult.employAffiches);
                }
                adapter.setList(notifities);
                if (announcementResult.total <= page * 10) {
                    recyclerView.setLoadMoreEnable(false);
                } else {
                    recyclerView.setLoadMoreEnable(true);
                }
                if(notifities.size() == 0){
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

    /**
     * 隐藏错误布局
     */
    private void hideErr() {
        errLayout.setVisibility(View.GONE);
    }

    @Override
    public boolean isEnableSwipe() {
        return true;
    }
}
