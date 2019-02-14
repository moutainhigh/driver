package com.easymin.custombus.flowmvp;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.easymi.component.base.RxBaseActivity;
import com.easymin.custombus.R;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: CbFlowActivity
 * @Author: hufeng
 * @Date: 2019/2/14 下午1:55
 * @Description:  定制班车跑单主界面
 * @History:
 */
@Route(path = "/custombus/CbFlowActivity")
public class CbFlowActivity extends RxBaseActivity {

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_custom_bus;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {

    }
}
