package com.easymi.common.mvp.grab;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.amap.api.maps.MapView;
import com.easymi.common.R;
import com.easymi.common.adapter.GrabAdapter;
import com.easymi.common.entity.Order;
import com.easymi.component.base.RxBaseActivity;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by developerLzh on 2017/11/2 0002.
 */

@Route(path = "/common/GrabActivity")
public class GrabActivity extends RxBaseActivity {

    LinearLayout expandBtnCon;

    MapView mapView;

    ExpandableLayout expandableLayout;

    ViewPager viewPager;

    GrabAdapter adapter;

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

        adapter = new GrabAdapter(this);
        viewPager.setAdapter(adapter);

        List<Order> orders = new ArrayList<>();
        orders.add(new Order());
        orders.add(new Order());
        orders.add(new Order());
        orders.add(new Order());
        adapter.setOrderList(orders);

        expandBtnCon.setOnClickListener(v -> {
            if (expandableLayout.isExpanded()) {
                expandableLayout.collapse();
            } else {
                expandableLayout.expand();
            }
        });

        mapView.onCreate(savedInstanceState);
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
}
