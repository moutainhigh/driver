package com.easymi.common.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;

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
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:CreateActivity
 * @Author: hufeng
 * Date: 2018/12/24 下午1:10
 * Description: 创建订单界面
 * History:
 */
@Route(path = "/common/CreateActivity")
public class CreateActivity extends RxBaseActivity {


    ViewPager viewPager;

    /**
     * 自定义标题栏
     */
    CusToolbar toolbar;

    /**
     * fragment适配器
     */
    private VpAdapter adapter;
    /**
     * 订单类型
     */
    private String orderType;

    @Override
    public boolean isEnableSwipe() {
        return true;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_com_create_order;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        viewPager = findViewById(R.id.create_view_pager);

        orderType = getIntent().getStringExtra("type");
        initTabLayout();
    }

    @Override
    public void initToolBar() {
        toolbar = findViewById(R.id.cus_toolbar);
        if (Config.ZHUANCHE.equals(orderType)) {
            toolbar.setTitle(pin2Hanzi(orderType));
        } else if (Config.TAXI.equals(orderType)) {
            toolbar.setTitle(pin2Hanzi(orderType));
        }
        toolbar.setLeftBack(v -> finish());
    }

    List<Fragment> fragments;

    private void initTabLayout() {
        fragments = new ArrayList<>();
        try {
//            String[] types = null;
//            if (!TextUtils.isEmpty(EmUtil.getEmployInfo().serviceType)) {
//                types = EmUtil.getEmployInfo().serviceType.split(",");
//            }
//            //按顺序加载
//            if (types != null && types.length > 0) {
//                for (String type : types) {
            if (Config.ZHUANCHE.equals(orderType)) {
                Class zhuanche = Class.forName("com.easymi.zhuanche.fragment.create.CreateZCFragment");
                fragments.add((Fragment) zhuanche.newInstance());
            } else if (Config.TAXI.equals(orderType)) {
                Class taxi = Class.forName("com.easymi.taxi.fragment.create.CreateTaxiFragment");
                fragments.add((Fragment) taxi.newInstance());
            }
//                }
//            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        viewPager.setOffscreenPageLimit(5);
        adapter = new VpAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);

    }

    private int pin2Hanzi(String type) {
        if (type.equals(Config.ZHUANCHE)) {
            return R.string.create_zhuanche;
        } else if (type.equals(Config.TAXI)) {
            return R.string.create_taxi;
        }
        return 0;
    }
}
