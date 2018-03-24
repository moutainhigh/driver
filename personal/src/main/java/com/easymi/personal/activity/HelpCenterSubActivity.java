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
 * Created by liuzihao on 2018/3/1.
 */

public class HelpCenterSubActivity extends RxBaseActivity {

    private RecyclerView recyclerView;

    private HelpSubAdapter adapter;

    private List<HelpMenu> helpMenuList;

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

        getMenuData();
    }

    private void getMenuData() {
        McService api = ApiManager.getInstance().createApi(Config.HOST, McService.class);

        Observable<HelpMenuResult> observable = api
                .getHelpeSubMenu(Config.APP_KEY,
                        EmUtil.getEmployInfo().company_id,
                        1,
                        100,
                        (long) 2)
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
