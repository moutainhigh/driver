package com.easymi.common.register;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.easymi.common.R;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.widget.CusToolbar;

public class InfoActivity extends RxBaseActivity {

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.com_activity_info;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        findViewById(R.id.left_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (savedInstanceState == null) {
            Fragment fragment = new CarInfoFragment();
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ts = fm.beginTransaction();
            ts.add(R.id.container, fragment, fragment.getClass().getName());
            ts.commitAllowingStateLoss();
        }
    }


}
