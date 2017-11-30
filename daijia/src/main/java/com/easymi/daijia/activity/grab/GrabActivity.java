package com.easymi.daijia.activity.grab;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.amap.api.maps.MapView;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.HLoadView;
import com.easymi.component.widget.RotateImageView;
import com.easymi.daijia.R;
import com.easymi.daijia.adapter.GrabAdapter;
import com.easymi.daijia.entity.DJOrder;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by developerLzh on 2017/11/2 0002.
 */

@Route(path = "/daijia/GrabActivity")
public class GrabActivity extends RxBaseActivity implements GrabContract.View {

    LinearLayout expandBtnCon;
    MapView mapView;
    ExpandableLayout expandableLayout;
    ViewPager viewPager;

    FrameLayout shadeFrame;
    RotateImageView rotateImage;
    TextView shadeCountdown;

    HLoadView loadView;
    RelativeLayout grabCon;
    TextView grabCountdown;

    GrabAdapter adapter;
    GrabPresenter presenter;

    private long orderId = -1;

    @Override
    public int getLayoutId() {
        return R.layout.activity_grab;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        expandBtnCon = findViewById(R.id.expand_btn_con);
        mapView = findViewById(R.id.map_view);
        expandableLayout = findViewById(R.id.expand_layout);
        viewPager = findViewById(R.id.view_pager);

        shadeFrame = findViewById(R.id.shade_frame);
        rotateImage = findViewById(R.id.time_ring);
        shadeCountdown = findViewById(R.id.count_down_start);

        loadView = findViewById(R.id.load_view);
        grabCon = findViewById(R.id.grab_con);
        grabCountdown = findViewById(R.id.count_down_grab);

//        orderId = getIntent().getLongExtra("orderId", -1);
        orderId = 18;

        adapter = new GrabAdapter(this);
        viewPager.setAdapter(adapter);


        expandBtnCon.setOnClickListener(v -> {
            if (expandableLayout.isExpanded()) {
                expandableLayout.collapse();
            } else {
                expandableLayout.expand();
            }
        });
        mapView.onCreate(savedInstanceState);

        presenter = new GrabPresenter(this, this);
        presenter.queryOrder(orderId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void showBase(DJOrder djOrder) {
        if (djOrder == null) {
            ToastUtil.showMessage(this, getString(R.string.order_not_exist));
            finish();
        } else {
            List<DJOrder> djOrders = new ArrayList<>();
            djOrders.add(djOrder);
            adapter.setDJOrderList(djOrders);

            showShade();
        }
    }

    private int shadeTime = 3;
    private int countTime = 15;

    private Timer timer;
    private TimerTask timerTask;

    @Override
    public void showShade() {
        shadeFrame.setClickable(true);
        rotateImage.startRotate();
        countTimer(true);
    }

    private void countTimer(boolean shade) {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (shade) {
                    shadeTime--;
                    runOnUiThread(() -> {
                        shadeCountdown.setText("" + shadeTime);
                        if (shadeTime == 0) {
                            cancelTimer();
                            showGrabCountDown();
                        }
                    });
                } else {
                    countTime--;
                    runOnUiThread(() -> {
                        grabCountdown.setText("" + countTime);
                        if (countTime == 0) {
                            cancelTimer();
                            finish();
                        }
                    });
                }
            }
        };
        timer.schedule(timerTask, 1000, 1000);
    }

    private void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }

    @Override
    public void showGrabCountDown() {
        rotateImage.pauseRotate();
        shadeFrame.setVisibility(View.GONE);
        countTimer(false);
        grabCon.setOnClickListener(v -> presenter.grabOrder(orderId));
    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    public RxManager getManager() {
        return mRxManager;
    }
}
