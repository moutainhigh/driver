package com.easymi.common.mvp.order;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.alibaba.android.arouter.launcher.ARouter;
import com.easymi.common.R;
import com.easymi.common.activity.CreateActivity;
import com.easymi.common.adapter.VpAdapter;
import com.easymi.common.result.SettingResult;
import com.easymi.common.widget.CreatOrderDialog;
import com.easymi.common.widget.MakeOrderPopWindow;
import com.easymi.component.Config;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.ZCSetting;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.widget.CusToolbar;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: OrderActivity
 *
 * @Author: shine
 * Date: 2018/11/14 下午7:26
 * Description:
 * History:
 */
public class OrderActivity extends RxBaseActivity {

    TabLayout tabLayout;
    CusToolbar toolbar;
    ViewPager viewPager;
    List<Fragment> fragments;
    /**
     * fragment适配器
     */
    VpAdapter adapter;
//    /**
//     * 补单弹窗
//     */
//    MakeOrderPopWindow popWindow;

    /**
     * 补单弹窗
     */
    CreatOrderDialog creatOrderDialog;

    @Override
    public void initToolBar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.com_my_order);
        toolbar.setLeftIcon(R.drawable.ic_arrow_back, v -> finish());
        if (EmUtil.getEmployInfo().serviceType.equals(Config.CITY_LINE)) {
            toolbar.setRightText(R.string.com_make_order, v -> {
                creatOrderDialog = new CreatOrderDialog(this);
                creatOrderDialog.setOnMyClickListener(view -> {
                    ARouter.getInstance().build("/cityline/CreateOrderActivity").navigation();
                    creatOrderDialog.dismiss();
                });
                creatOrderDialog.show();
            });
        }else if (EmUtil.getEmployInfo().serviceType.equals(Config.ZHUANCHE)){
            toolbar.setRightText(R.string.com_make_order, v -> {
                creatOrderDialog = new CreatOrderDialog(this);
                creatOrderDialog.setOnMyClickListener(view -> {
                    Intent intent = new Intent(this, CreateActivity.class);
                    intent.putExtra("type",Config.ZHUANCHE);
                    startActivity(intent);
                    creatOrderDialog.dismiss();
                });
                creatOrderDialog.show();
            });
        }else if (EmUtil.getEmployInfo().serviceType.equals(Config.CARPOOL)){
            if (ZCSetting.findOne().isRepairOrder == 1){
                toolbar.setRightText(R.string.com_make_order, v -> {
                    creatOrderDialog = new CreatOrderDialog(this);
                    creatOrderDialog.setOnMyClickListener(view -> {
                        ARouter.getInstance().build("/carpooling/CreateOrderActivity").navigation();
                        creatOrderDialog.dismiss();
                    });
                    creatOrderDialog.show();
                });
            }
        }
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

//        popWindow = new MakeOrderPopWindow(this);

        initTabLayout();
    }


    private void initTabLayout() {
        fragments = new ArrayList<>();
        fragments.add(new AccpteFragment());
        if (EmUtil.getEmployInfo().serviceType.equals(Config.ZHUANCHE)
                || EmUtil.getEmployInfo().serviceType.equals(Config.TAXI)) {
            fragments.add(new AssignFragment());
        }

//        if (ZCSetting.findOne().grabOrder == 1){
//            fragments.add(new GrabFragment());
//        }
        viewPager.setOffscreenPageLimit(fragments.size());
        adapter = new VpAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText(getString(R.string.com_accept_order));

//        if (ZCSetting.findOne().grabOrder == 1){
//            tabLayout.getTabAt(1).setText(getString(R.string.com_assign_order));
//            tabLayout.getTabAt(2).setText(getString(R.string.com_grab_order));
//        }else {
        if (EmUtil.getEmployInfo().serviceType.equals(Config.ZHUANCHE)
                || EmUtil.getEmployInfo().serviceType.equals(Config.TAXI)) {
            tabLayout.getTabAt(1).setText(getString(R.string.com_assign_order));
        }
//        }

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//                if (ZCSetting.findOne().grabOrder == 1){
//                    if (position == 0){
//                        ((AccpteFragment)fragments.get(position)).setRefresh();
//                    }else if (position == 1){
//                        ((AssignFragment)fragments.get(position)).setRefresh();
//                    }else if (position == 2){
//                        ((GrabFragment)fragments.get(position)).setRefresh();
//                    }
//                }else {
                if (position == 0) {
                    ((AccpteFragment) fragments.get(position)).setRefresh();
                } else if (position == 1) {
                    ((AssignFragment) fragments.get(position)).setRefresh();
                }
//                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
