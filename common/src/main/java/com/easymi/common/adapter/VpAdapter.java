package com.easymi.common.adapter;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: FinishActivity
 *
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */


import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;


public class VpAdapter extends FragmentPagerAdapter {
    private List<Fragment> data;
    private List<String> title;

    public VpAdapter(FragmentManager fm, List<Fragment> data) {
        this(fm, data, null);
    }

    public VpAdapter(FragmentManager fm, List<Fragment> data, List<String> title) {
        super(fm);
        this.data = data;
        this.title = title;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (title == null) {
            return super.getPageTitle(position);
        } else {
            return title.get(position);
        }
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Fragment getItem(int position) {
        return data.get(position);
    }
}