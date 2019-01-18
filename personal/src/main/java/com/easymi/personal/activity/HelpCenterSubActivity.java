package com.easymi.personal.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.easymi.component.Config;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.widget.CusToolbar;
import com.easymi.personal.McService;
import com.easymi.personal.R;
import com.easymi.personal.adapter.HelpSubAdapter;
import com.easymi.personal.entity.HelpMenu;
import com.easymi.personal.result.HelpMenuResult;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: HelpCenterSubActivity
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class HelpCenterSubActivity extends RxBaseActivity {

    private RecyclerView recyclerView;

    private HelpSubAdapter adapter;

    private List<HelpMenu> helpMenuList;
    private Long cateId;

    @Override
    public int getLayoutId() {
        return R.layout.activity_help_sub;
    }

    @Override
    public void initToolBar() {
        CusToolbar cusToolbar = findViewById(R.id.cus_toolbar);
        cusToolbar.setLeftBack(view -> finish());
        cusToolbar.setTitle(R.string.set_help);
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        adapter = new HelpSubAdapter(this);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        helpMenuList = new ArrayList<>();

        cateId = getIntent().getLongExtra("cateId", 2);

        getMenuData();
    }

    /**
     * 获取帮助中心数据
     */
    private void getMenuData() {
        McService api = ApiManager.getInstance().createApi(Config.HOST, McService.class);

        Observable<HelpMenuResult> observable = api
                .getHelpeSubMenu(EmUtil.getAppKey(),
                        (long) 0,
                        1,
                        100,
                        cateId)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, true, false, helpMenuResult -> {
            if (null != helpMenuResult && helpMenuResult.menus != null) {
                helpMenuList = helpMenuResult.menus;
                adapter.setMenus(helpMenuList);
            }
        })));
    }

    @Override
    public boolean isEnableSwipe() {
        return true;
    }
}
