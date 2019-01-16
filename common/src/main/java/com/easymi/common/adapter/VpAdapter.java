package com.easymi.common.adapter;

/**
 * Created by developerLzh on 2017/11/16 0016.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * view pager adapter
 * @author hufeng
 */
public class VpAdapter extends FragmentPagerAdapter {
    private List<Fragment> data;

    public VpAdapter(FragmentManager fm, List<Fragment> data) {
        super(fm);
        this.data = data;
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