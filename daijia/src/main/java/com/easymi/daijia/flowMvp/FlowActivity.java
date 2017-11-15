package com.easymi.daijia.flowMvp;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.utils.ToastUtil;
import com.easymi.daijia.R;
import com.easymi.daijia.entity.DJOrder;
import com.easymi.daijia.fragment.AcceptFragment;
import com.easymi.daijia.fragment.ArriveStartFragment;
import com.easymi.daijia.fragment.RunningFragment;
import com.easymi.daijia.fragment.SlideArriveStartFragment;
import com.easymi.daijia.fragment.ToStartFragment;
import com.easymi.daijia.fragment.WaitFragment;

import net.cachapa.expandablelayout.ExpandableLayout;

import co.lujun.androidtagview.TagContainerLayout;

/**
 * Created by developerLzh on 2017/11/13 0013.
 */
@Route(path = "/daijia/FlowActivity")
public class FlowActivity extends RxBaseActivity implements FlowContract.View {

    TextView nextPlace;
    TextView leftTimeText;
    TextView orderNumberText;
    TextView orderTypeText;
    TagContainerLayout tagContainerLayout;
    LinearLayout drawerFrame;

    LinearLayout naviCon;

    ExpandableLayout expandableLayout;

    private DJOrder djOrder;

    private FlowPresenter presenter;

    private ActFraCommBridge bridge;

    @Override
    public int getLayoutId() {
        return R.layout.activity_flow;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        long orderId = getIntent().getLongExtra("orderId", -1);
        if (orderId == -1) {
            finish();
            return;
        }

        djOrder = new DJOrder();
        djOrder.orderEndPlace = "锦绣大道南段";
        djOrder.subStatus = 0;
        djOrder.orderNumber = "DJ20171122";
        djOrder.orderPrice = 39.0;
        djOrder.orderStartPlace = "金马镇花样城";
        djOrder.orderStatus = "执行中";
        djOrder.orderTime = "2017-11-15 18:30";
        djOrder.orderType = "日常代驾";
        djOrder.passengerPhone = "18148140090";

        //TODO 从数据库查找订单

        presenter = new FlowPresenter(this, this);

        nextPlace = findViewById(R.id.next_place);
        leftTimeText = findViewById(R.id.left_time);
        orderNumberText = findViewById(R.id.order_number_text);
        orderTypeText = findViewById(R.id.order_type);
        tagContainerLayout = findViewById(R.id.tag_container);
        drawerFrame = findViewById(R.id.drawer_frame);
        naviCon = findViewById(R.id.navi_con);
        expandableLayout = findViewById(R.id.expandable_layout);

        showTopView();
        initBridge();
        showBottomFragment(djOrder);
    }

    @Override
    public void showTopView() {
        orderNumberText.setText(djOrder.orderNumber);
        orderTypeText.setText(djOrder.orderType);
        tagContainerLayout.addTag("嘻嘻");
        tagContainerLayout.addTag("哈哈");
        tagContainerLayout.addTag("傻逼");
        tagContainerLayout.addTag("智障");
        drawerFrame.setOnClickListener(view -> {
            if (expandableLayout.isExpanded()) {
                expandableLayout.collapse();
            } else {
                expandableLayout.expand();
            }
        });

        naviCon.setOnClickListener(view -> presenter.navi(djOrder));
    }

    @Override
    public void showBottomFragment(DJOrder djOrder) {
        if (djOrder.subStatus == 0) {
            AcceptFragment acceptFragment = new AcceptFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("djOrder", djOrder);
            acceptFragment.setArguments(bundle);
            acceptFragment.setBridge(bridge);

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
            transaction.replace(R.id.flow_frame, acceptFragment);
            transaction.commit();
        } else if (djOrder.subStatus == 1) {
            ToStartFragment toStartFragment = new ToStartFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("djOrder", djOrder);
            toStartFragment.setArguments(bundle);
            toStartFragment.setBridge(bridge);

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
            transaction.replace(R.id.flow_frame, toStartFragment);
            transaction.commit();
        } else if (djOrder.subStatus == 2) {
            SlideArriveStartFragment fragment = new SlideArriveStartFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("djOrder", djOrder);
            fragment.setArguments(bundle);
            fragment.setBridge(bridge);

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
            transaction.replace(R.id.flow_frame, fragment);
            transaction.commit();
        } else if (djOrder.subStatus == 3) {
            ArriveStartFragment fragment = new ArriveStartFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("djOrder", djOrder);
            fragment.setArguments(bundle);
            fragment.setBridge(bridge);

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
            transaction.replace(R.id.flow_frame, fragment);
            transaction.commit();
        } else if (djOrder.subStatus == 4) {
            WaitFragment fragment = new WaitFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("djOrder", djOrder);
            fragment.setArguments(bundle);
            fragment.setBridge(bridge);

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
            transaction.replace(R.id.flow_frame, fragment);
            transaction.commit();
        } else if (djOrder.subStatus == 5) {
            RunningFragment fragment = new RunningFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("djOrder", djOrder);
            fragment.setArguments(bundle);
            fragment.setBridge(bridge);

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
            transaction.replace(R.id.flow_frame, fragment);
            transaction.commit();
        } else if (djOrder.subStatus == 6) {
            WaitFragment fragment = new WaitFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("djOrder", djOrder);
            fragment.setArguments(bundle);
            fragment.setBridge(bridge);

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
            transaction.replace(R.id.flow_frame, fragment);
            transaction.commit();
        }
    }

    @Override
    public void showToPlace(String toPlace) {
        nextPlace.setText(toPlace);
    }

    @Override
    public void showLeftTime(String leftTime) {
        leftTimeText.setText(leftTime);
    }

    @Override
    public void initBridge() {
        bridge = new ActFraCommBridge() {
            @Override
            public void doAccept() {
                ToastUtil.showMessage(FlowActivity.this, "接单");
                djOrder.subStatus = 1;
                showBottomFragment(djOrder);
            }

            @Override
            public void doRefuse() {
                ToastUtil.showMessage(FlowActivity.this, "拒单");
                finish();
            }

            @Override
            public void doToStart() {
                ToastUtil.showMessage(FlowActivity.this, "前往预约地");
                djOrder.subStatus = 2;
                showBottomFragment(djOrder);
            }

            @Override
            public void doArriveStart() {
                ToastUtil.showMessage(FlowActivity.this, "到达预约地");
                djOrder.subStatus = 3;
                showBottomFragment(djOrder);
            }

            @Override
            public void doOutBeforeWait() {
                ToastUtil.showMessage(FlowActivity.this, "出发前等待");
                djOrder.subStatus = 4;
                showBottomFragment(djOrder);
            }

            @Override
            public void doOutAfterWait() {
                ToastUtil.showMessage(FlowActivity.this, "出发后等待");
                djOrder.subStatus = 6;
                showBottomFragment(djOrder);
            }

            @Override
            public void doStartDrive() {
                ToastUtil.showMessage(FlowActivity.this, "前往目的地");
                djOrder.subStatus = 5;
                showBottomFragment(djOrder);
            }

            @Override
            public void changeEnd() {

            }

            @Override
            public void showSettleDialog() {

            }
        };
    }
}
