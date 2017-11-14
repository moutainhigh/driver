package com.easymi.common.mvp.work;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.LinearLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.amap.api.maps.MapView;
import com.easymi.common.R;
import com.easymi.common.mvp.grab.GrabActivity;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.rxmvp.BaseView;
import com.easymi.component.widget.BottomBehavior;
import com.skyfishjy.library.RippleBackground;

/**
 * Created by developerLzh on 2017/11/3 0003.
 */

@Route(path = "/common/WorkActivity")
public class WorkActivity extends RxBaseActivity implements BaseView {
    @Override
    public int getLayoutId() {
        return R.layout.activity_work;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {

        bottomBar = findViewById(R.id.bottom_bar);
        mapView = findViewById(R.id.map_view);
        rippleBackground = findViewById(R.id.ripple_ground);


        behavior = BottomBehavior.from(bottomBar);

        mapView.onCreate(savedInstanceState);

        rippleBackground.startRippleAnimation();

        rippleBackground.setOnClickListener(v -> startActivity(new Intent(WorkActivity.this, GrabActivity.class)));
    }

    LinearLayout bottomBar;

    MapView mapView;

    RippleBackground rippleBackground;

    BottomBehavior behavior;

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mapView.onSaveInstanceState(outState);
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

    public void mapHideShow(View view) {
        if (behavior.getState() == BottomBehavior.STATE_COLLAPSED) {
            behavior.setState(BottomBehavior.STATE_EXPANDED);
        } else {
            behavior.setState(BottomBehavior.STATE_COLLAPSED);
        }
    }

}
