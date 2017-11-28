package com.easymi.common.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.easymi.common.R;
import com.easymi.common.adapter.VpAdapter;
import com.easymi.component.base.RxBaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuzihao on 2017/11/16.
 */
@Route(path = "/common/CreateActivity")
public class CreateActivity extends RxBaseActivity {

    TabLayout tabLayout;
    ViewPager viewPager;

    private VpAdapter adapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_create_order;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        initTabLayout();
    }

    List<Fragment> fragments;

    private void initTabLayout() {
        fragments = new ArrayList<>();
        try {
            Class  clazz =  Class.forName("com.easymi.daijia.fragment.create.CreateDJFragment");
            fragments.add((Fragment) clazz.newInstance());
            fragments.add((Fragment) clazz.newInstance());
            fragments.add((Fragment) clazz.newInstance());
            fragments.add((Fragment) clazz.newInstance());
            fragments.add((Fragment) clazz.newInstance());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        }

        adapter = new VpAdapter(getSupportFragmentManager(), fragments);

        viewPager.setOffscreenPageLimit(5);
        adapter = new VpAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText(getString(R.string.create_daijia));
        tabLayout.getTabAt(1).setText(getString(R.string.create_zhuanche));
        tabLayout.getTabAt(2).setText(getString(R.string.create_paotui));
        tabLayout.getTabAt(3).setText(getString(R.string.create_huoyun));
        tabLayout.getTabAt(4).setText(getString(R.string.create_zhuanxian));

    }
}
