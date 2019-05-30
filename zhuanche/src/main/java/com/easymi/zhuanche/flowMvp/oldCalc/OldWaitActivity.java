package com.easymi.zhuanche.flowMvp.oldCalc;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import com.easymi.component.utils.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.services.route.DriveRouteResult;
import com.easymi.common.push.FeeChangeObserver;
import com.easymi.common.push.HandlePush;
import com.easymi.component.Config;
import com.easymi.component.ZCOrderStatus;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.widget.LoadingButton;
import com.easymi.zhuanche.R;
import com.easymi.zhuanche.entity.ConsumerInfo;
import com.easymi.zhuanche.entity.ZCOrder;
import com.easymi.zhuanche.flowMvp.FlowActivity;
import com.easymi.zhuanche.flowMvp.FlowContract;
import com.easymi.zhuanche.flowMvp.FlowPresenter;
import com.easymi.zhuanche.util.PhoneUtil;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: OldWaitActivity
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description: 未使用
 * History:
 */

public class OldWaitActivity extends RxBaseActivity implements FlowContract.View, FeeChangeObserver {
    private ZCOrder zcOrder;

    @Override
    public void feeChanged(long orderId, String orderType) {
        if (zcOrder == null) {
            return;
        }
        if (orderId == zcOrder.orderId && orderType.equals(Config.ZHUANCHE)) {
            showView();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        HandlePush.getInstance().addObserver(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        HandlePush.getInstance().deleteObserver(this);
    }

    @Override
    public void initToolbar() {

    }


    @Override
    public void showTopView() {

    }

    @Override
    public void showToPlace(String toPlace) {

    }

    @Override
    public void showLeftTime(String leftTime) {

    }

    @Override
    public void initBridge() {

    }

    @Override
    public void showBottomFragment(ZCOrder zcOrder) {

    }

    @Override
    public void showOrder(ZCOrder zcOrder) {
        if (null == zcOrder) {
            finish();
            return;
        }
        this.zcOrder = zcOrder;
        start_drive.setOnClickListener(view -> presenter.startDrive(orderId,zcOrder.version));
        if (zcOrder.orderStatus == ZCOrderStatus.GOTO_DESTINATION_ORDER) {
            Intent intent = new Intent(OldWaitActivity.this, OldRunningActivity.class);
            intent.putExtra("orderId", orderId);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void initMap() {

    }

    @Override
    public void showMapBounds() {

    }

    @Override
    public void cancelSuc() {

    }

    @Override
    public void refuseSuc() {

    }

    @Override
    public void showPath(int[] ints, AMapNaviPath path) {

    }

    @Override
    public void showPath(DriveRouteResult result) {

    }

    @Override
    public void showPayType(double money, ConsumerInfo consumerInfo) {

    }


    @Override
    public void paySuc() {

    }

    @Override
    public void showLeft(int dis, int time) {

    }

    @Override
    public void showReCal() {

    }

    @Override
    public void showToEndFragment() {

    }

    @Override
    public void showConsumer(ConsumerInfo consumerInfo) {

    }

    @Override
    public RxManager getManager() {
        return mRxManager;
    }

    @Override
    public void reRout() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        PhoneUtil.setHideVirtualKey(getWindow());//隐藏虚拟按键
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.zc_activity_old_wait;
    }

    private LinearLayout back;
    private TextView minute_bai;
    private TextView minute_shi;
    private TextView minute_ge;
    private LoadingButton start_drive;

    private long orderId;
    private boolean forceOre;

    private FlowPresenter presenter;

    @Override
    public void initViews(Bundle savedInstanceState) {
        // 屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

//        mSwipeBackHelper.setSwipeBackEnable(false);//横屏界面不允许侧滑返回

        orderId = getIntent().getLongExtra("orderId", -1);

        forceOre = XApp.getMyPreferences().getBoolean(Config.SP_ALWAYS_OREN, false);
        if (forceOre) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//动态设置为横屏
        }

        presenter = new FlowPresenter(this, this);

        back = findViewById(R.id.back);
        minute_bai = findViewById(R.id.minute_bai);
        minute_shi = findViewById(R.id.minute_shi);
        minute_ge = findViewById(R.id.minute_ge);
        start_drive = findViewById(R.id.start_drive);

        showView();

        presenter.findOne(orderId, false);

        back.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    private void showView() {
        DymOrder dymOrder = DymOrder.findByIDType(orderId, Config.ZHUANCHE);
        if (dymOrder == null) {
            return;
        }
        int waitMin = dymOrder.waitTime;
        minute_bai.setText(String.valueOf(waitMin / 100));
        minute_shi.setText(String.valueOf(waitMin % 100 / 10));
        minute_ge.setText(String.valueOf(waitMin % 100 % 10));
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(OldWaitActivity.this, FlowActivity.class);
        intent.putExtra("orderId", orderId);
        intent.putExtra("fromOld", true);
        startActivity(intent);
        finish();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.e("lifecycle", "onConfigurationChanged()");
        super.onConfigurationChanged(newConfig);
        if (System.currentTimeMillis() - lastChangeTime > 1000) {
            lastChangeTime = System.currentTimeMillis();
        } else {//有的胎神手机这个方法要回调两次
            return;
        }
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        if (width > height) {//横屏

        } else {//竖屏
            onBackPressed();
        }
    }

    @Override
    public boolean isEnableSwipe() {
        return false;
    }
}
