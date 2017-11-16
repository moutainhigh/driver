package com.easymi.common.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;

import com.easymi.common.R;
import com.easymi.component.base.RxBaseActivity;

/**
 * Created by liuzihao on 2017/11/16.
 */

public class CreateActivity extends RxBaseActivity {

    TabLayout tabLayout;


    @Override
    public int getLayoutId() {
        return R.layout.activity_create_order;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {

    }
}
