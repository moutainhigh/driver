package com.easymi.daijia.flowMvp;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.rxmvp.RxManager;
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

        presenter = new FlowPresenter(this, this);

        nextPlace = findViewById(R.id.next_place);
        leftTimeText = findViewById(R.id.left_time);
        orderNumberText = findViewById(R.id.order_number_text);
        orderTypeText = findViewById(R.id.order_type);
        tagContainerLayout = findViewById(R.id.tag_container);
        drawerFrame = findViewById(R.id.drawer_frame);
        naviCon = findViewById(R.id.navi_con);
        expandableLayout = findViewById(R.id.expandable_layout);

        presenter.findOne(orderId);
    }

    @Override
    public void showTopView() {
        orderNumberText.setText(djOrder.orderNumber);
        orderTypeText.setText(djOrder.orderDetailType);
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
        if (djOrder.orderStatus == DJOrder.NEW_ORDER
                || djOrder.orderStatus == DJOrder.PAIDAN_ORDER
                || djOrder.orderStatus == DJOrder.TAKE_ORDER
                || djOrder.orderStatus == DJOrder.GOTO_BOOKPALCE_ORDER) {
            nextPlace.setText(djOrder.startPlace);
        }
    }

    @Override
    public void showBottomFragment(DJOrder djOrder) {
        if (djOrder.orderStatus == DJOrder.PAIDAN_ORDER || djOrder.orderStatus == DJOrder.NEW_ORDER) {
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
        } else if (djOrder.orderStatus == DJOrder.TAKE_ORDER) {
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
        } else if (djOrder.orderStatus == DJOrder.GOTO_BOOKPALCE_ORDER) {
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
        } else if (djOrder.orderStatus == DJOrder.ARRIVAL_BOOKPLACE_ORDER) {
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
        } else if (djOrder.orderStatus == DJOrder.ARRIVAL_BOOKPLACE_ORDER) {
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
        } else if (djOrder.orderStatus == DJOrder.GOTO_DESTINATION_ORDER) {
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
        } else if (djOrder.orderStatus == DJOrder.ARRIVAL_BOOKPLACE_ORDER) {
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
    public void showOrder(DJOrder djOrder) {
        if (null == djOrder) {
            finish();
        } else {
            this.djOrder = djOrder;
            showTopView();
            initBridge();
            showBottomFragment(djOrder);
        }
    }

    @Override
    public RxManager getManager() {
        return mRxManager;
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
                presenter.acceptOrder(djOrder.orderId);
            }

            @Override
            public void doRefuse() {
            }

            @Override
            public void doToStart() {
            }

            @Override
            public void doArriveStart() {
            }

            @Override
            public void doOutBeforeWait() {
            }

            @Override
            public void doOutAfterWait() {
            }

            @Override
            public void doStartDrive() {
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
