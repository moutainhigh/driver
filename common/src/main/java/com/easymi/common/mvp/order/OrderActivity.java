package com.easymi.common.mvp.order;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.easymi.common.R;
import com.easymi.common.adapter.VpAdapter;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.widget.CusToolbar;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: OrderActivity
 * Author: shine
 * Date: 2018/11/14 下午7:26
 * Description:
 * History:
 */
public class OrderActivity extends RxBaseActivity {

    TabLayout tabLayout;
    CusToolbar toolbar;
    ViewPager viewPager;
    List<Fragment> fragments;
    VpAdapter adapter;
    boolean fastAssign = true;

    @Override
    public void initToolBar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.liushui_title);
        toolbar.setLeftIcon(R.drawable.ic_arrow_back, v -> finish());
        toolbar.setRightText(R.string.com_make_order, v -> {
            //todo 司机补单
        });
    }

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_order;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        initTabLayout();
    }


    private void initTabLayout() {
        fragments = new ArrayList<>();
        fragments.add(new AccpteFragment());
        if (fastAssign){
            fragments.add(new AssignFragment());
        }
        fragments.add(new GrabFragment());

        viewPager.setOffscreenPageLimit(fragments.size());
        adapter = new VpAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText(getString(R.string.com_accept_order));
        if (fastAssign){
            tabLayout.getTabAt(1).setText(getString(R.string.com_assign_order));
            tabLayout.getTabAt(2).setText(getString(R.string.com_grab_order));
        }else {
            tabLayout.getTabAt(1).setText(getString(R.string.com_grab_order));
        }

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
