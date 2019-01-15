package com.easymin.daijia.driver.zyziyunsjdaijia;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.alibaba.android.arouter.launcher.ARouter;
import com.amap.api.navi.model.NaviLatLng;
import com.easymi.cityline.CLService;
import com.easymi.cityline.activity.CreateOrderActivity;
import com.easymi.common.entity.OrderCustomer;
import com.easymi.cityline.entity.ZXOrder;
import com.easymi.cityline.flowMvp.FlowActivity;
import com.easymi.common.activity.SplashActivity;
import com.easymi.component.Config;
import com.easymi.component.ZXOrderStatus;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc3;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.network.NoErrSubscriberListener;
import com.easymi.component.result.EmResult2;
import com.easymi.component.utils.EmUtil;
import com.easymi.zhuanche.activity.TestActivity;
import com.easymin.daijia.driver.zyziyunsjdaijia.R;

import java.text.DecimalFormat;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Copyright (C) , 2012-2018 , 四川小咖科技有限公司
 *
 * @author Lzh
 * @version 5.0.0.000
 * @date 2018/5/19
 * @since 5.0.0.000
 */
public class IndexActivity extends RxBaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, SplashActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_index;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {

    }
}
