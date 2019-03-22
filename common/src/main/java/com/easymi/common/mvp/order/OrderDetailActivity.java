package com.easymi.common.mvp.order;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easymi.common.CommApiService;
import com.easymi.common.R;
import com.easymi.common.entity.CarpoolOrder;
import com.easymi.common.result.PCOrderResult;
import com.easymi.component.Config;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.BaseOrder;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.result.EmResult;
import com.easymi.component.utils.PhoneUtil;
import com.easymi.component.widget.CusToolbar;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: OrderDetailActivity
 * @Author: hufeng
 * @Date: 2019/3/19 上午9:30
 * @Description: 拼车完成订单的订单详情
 * @History:
 */

public class OrderDetailActivity extends RxBaseActivity {

    CusToolbar toolbar;
    TextView tv_order_type;
    TextView tv_line_name;
    TextView tv_service_time;
    TextView tv_name;
    TextView tv_person_number;
    TextView tv_start_addrrs;
    TextView tv_end_addrrs;
    TextView tv_remark;
    TextView tv_call_me;
    ImageView iv_turn;

    private long orderId;

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_order_detail;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        orderId = getIntent().getLongExtra("orderId",0);
        findById();
        queryOrderDetail(orderId);
    }

    @Override
    public void initToolBar() {
        super.initToolBar();
        toolbar = findViewById(R.id.toolbar);
        toolbar.setLeftBack(view -> finish());
        toolbar.setTitle(R.string.finish_order);
    }

    public void findById(){
        tv_order_type = findViewById(R.id.tv_order_type);
        tv_line_name = findViewById(R.id.tv_line_name);
        tv_service_time = findViewById(R.id.tv_service_time);
        tv_name = findViewById(R.id.tv_name);
        tv_person_number = findViewById(R.id.tv_person_number);
        tv_start_addrrs = findViewById(R.id.tv_start_addrrs);
        tv_end_addrrs = findViewById(R.id.tv_end_addrrs);
        tv_remark = findViewById(R.id.tv_remark);
        tv_call_me = findViewById(R.id.tv_call_me);
        iv_turn = findViewById(R.id.iv_turn);
    }

    /**
     * 拼车完成订单详情
     */
    private void queryOrderDetail(long orderId) {
        Observable<PCOrderResult> observable = ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .queryOrderDetail(orderId)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, true, true, emResult -> {
            if (emResult.getCode() == 1 && emResult.data != null) {
                setData(emResult.data);
            }
        })));
    }

    public void setData(CarpoolOrder order){
        tv_order_type.setText(getResources().getString(R.string.create_carpool));
        tv_line_name.setText(order.startStationName + "到"+order.endStationName);
        tv_service_time.setText(order.day+"  "+order.timeSlot);
        tv_name.setText(order.passengerName);
        tv_person_number.setText(order.ticketNumber+"");
        tv_start_addrrs.setText(order.startAddress);
        tv_end_addrrs.setText(order.endAddress);
        tv_remark.setText(order.orderRemark);

        tv_call_me.setOnClickListener(view -> {
            PhoneUtil.call(this,order.companyPhone);
        });

        if (order.orderChange == 1){
            iv_turn.setVisibility(View.VISIBLE);
        }else {
            iv_turn.setVisibility(View.GONE);
        }
    }
}
