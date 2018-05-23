package com.easymi.common.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.easymi.common.R;
import com.easymi.common.adapter.VpAdapter;
import com.easymi.component.Config;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.widget.CusToolbar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuzihao on 2017/11/16.
 */
@Route(path = "/common/CreateActivity")
public class CreateActivity extends RxBaseActivity {

    TabLayout tabLayout;
    ViewPager viewPager;

    CusToolbar toolbar;

    private VpAdapter adapter;

    @Override
    public boolean isEnableSwipe() {
        return true;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_create_order;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        tabLayout = findViewById(R.id.create_tab_layout);
        viewPager = findViewById(R.id.create_view_pager);

        initTabLayout();
    }

    @Override
    public void initToolBar() {
        toolbar = findViewById(R.id.cus_toolbar);
        toolbar.setTitle(R.string.work_create);
        toolbar.setLeftBack(v -> finish());
    }

    List<Fragment> fragments;

    private void initTabLayout() {
        fragments = new ArrayList<>();
        try {
            Class daijia = Class.forName("com.easymi.daijia.fragment.create.CreateDJFragment");
            Class zhuanche = Class.forName("com.easymi.zhuanche.fragment.create.CreateZCFragment");
            if (EmUtil.getEmployInfo().service_type.contains(Config.DAIJIA)) {
                fragments.add((Fragment) daijia.newInstance());
            }
            if (EmUtil.getEmployInfo().service_type.contains(Config.ZHUANCHE)) {
                fragments.add((Fragment) zhuanche.newInstance());
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        }

        viewPager.setOffscreenPageLimit(5);
        adapter = new VpAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);

        if (fragments.size() != 0) {
            String[] types = EmUtil.getEmployInfo().service_type.split(",");
            for (int i = 0; i < fragments.size(); i++) {
                tabLayout.getTabAt(i).setText(pin2Hanzi(types[i]));
            }
        }
    }

    private String pin2Hanzi(String type) {
        if (type.equals(Config.DAIJIA)) {
            return getString(R.string.create_daijia);
        } else if (type.equals(Config.ZHUANCHE)) {
            return getString(R.string.create_zhuanche);
        }
        return "";
    }
}
