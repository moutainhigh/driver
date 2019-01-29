package com.easymi.zhuanche.activity;

import android.os.Bundle;

import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.widget.CusToolbar;
import com.easymi.zhuanche.R;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: CancelNewActivity
 * @Author: hufeng
 * @Date: 2019/1/28 下午1:32
 * @Description: 专车优化迭代更改取消订单界面
 * @History:
 */
public class CancelNewActivity extends RxBaseActivity {


    CusToolbar cusToolbar;

    @Override
    public void initToolBar() {
        cusToolbar.setLeftBack(view -> finish());
        cusToolbar.setTitle(R.string.cancel_order);
    }

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_cancel_new;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        cusToolbar = findViewById(R.id.cus_toolbar);
    }
}
