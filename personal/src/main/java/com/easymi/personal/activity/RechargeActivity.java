package com.easymi.personal.activity;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.easymi.component.base.RxBaseActivity;
import com.easymi.personal.R;

/**
 * Created by developerLzh on 2017/11/9 0009.
 */

public class RechargeActivity extends RxBaseActivity {

    CheckBox pay50;
    CheckBox pay100;
    CheckBox pay200;
    EditText payCus;

    RelativeLayout payWx;
    RelativeLayout payZfb;
    RelativeLayout payUnion;

    @Override
    public int getLayoutId() {
        return R.layout.activity_recharge;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {

    }
}
