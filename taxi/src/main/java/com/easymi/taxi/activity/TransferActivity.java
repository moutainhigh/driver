package com.easymi.taxi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.easymi.common.entity.Address;
import com.easymi.component.Config;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.network.NoErrSubscriberListener;
import com.easymi.component.result.EmResult;
import com.easymi.component.utils.CommonUtil;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.MultiStateView;
import com.easymi.taxi.R;
import com.easymi.taxi.TaxiApiService;
import com.easymi.taxi.adapter.TransferDriverAdapter;
import com.easymi.taxi.entity.TaxiOrder;
import com.easymi.taxi.entity.TransferList;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TransferActivity extends RxBaseActivity {

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.taxi_activity_transfer;
    }

    private TaxiOrder order;
    private TransferDriverAdapter adapter;

    private TextView tvNo, tvType,tvTime, tvStart, tvEnd;
    private RecyclerView rv;
    private MultiStateView stateView;

    @Override
    public void initViews(Bundle savedInstanceState) {
        bindView();
        order = (TaxiOrder) getIntent().getSerializableExtra("order");
        if (order == null) {
            return;
        }
        tvNo.setText(order.orderNo);
        tvTime.setText(CommonUtil.dateFormat(order.bookTime * 1000, "yyyy-MM-dd HH:mm"));
        tvStart.setText(order.bookAddress);
        tvEnd.setText(order.destination);
        tvType.setText(order.orderDetailType);
        getTransferList();
    }

    @Override
    public void initToolBar() {
        CusToolbar cusToolbar = findViewById(R.id.cus_toolbar);
        cusToolbar.setLeftBack(view -> onBackPressed());
        cusToolbar.setTitle(R.string.order_transfer);
    }

    private void bindView() {
        tvNo = findViewById(R.id.order_no);
        tvTime = findViewById(R.id.order_time);
        tvStart = findViewById(R.id.order_start);
        tvEnd = findViewById(R.id.order_end);
        tvType = findViewById(R.id.order_type);
        rv = findViewById(R.id.rv);
        stateView = findViewById(R.id.state);
    }


    private void getTransferList() {
        Address start = getStartAddr();
        if (start == null) {
            return;
        }
        Observable<TransferList> observable = ApiManager.getInstance().createApi(Config.HOST, TaxiApiService.class)
                .getTransferList(start.latitude, start.longitude, 10, EmUtil.getAppKey()
//                        ,(long) EmUtil.getEmployInfo().vehicle.serviceType
                        ,'1'
                )
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<TransferList>(this, true, true, new HaveErrSubscriberListener<TransferList>() {
            @Override
            public void onNext(TransferList transferList) {
                if (transferList == null || transferList.emploies == null || transferList.emploies.isEmpty()) {
                    stateView.setStatus(MultiStateView.STATE_EMPTY);
                } else {
                    adapter = new TransferDriverAdapter(TransferActivity.this, transferList.emploies);
                    adapter.setOnTransferListener(new TransferDriverAdapter.OnTransferListener() {
                        @Override
                        public void onTransfer(TransferList.Emploie emploie) {
                            changeOrder(order.id, emploie.employId);
                        }
                    });
                    rv.setLayoutManager(new LinearLayoutManager(TransferActivity.this));
                    rv.setAdapter(adapter);
                }
            }

            @Override
            public void onError(int code) {
                stateView.setStatus(MultiStateView.STATE_EMPTY);
            }
        })));
    }

    private Address getStartAddr() {
        Address startAddress = null;
        if (order != null && order.orderAddressVos != null && order.orderAddressVos.size() != 0) {
            for (Address address : order.orderAddressVos) {
                if (address.type == 1) {
                    startAddress = address;
                    break;
                }
            }
        }
        return startAddress;
    }

    private void changeOrder(long orderId,long employId) {
        Observable<EmResult> observable = ApiManager.getInstance().createApi(Config.HOST, TaxiApiService.class)
                .changeOrder(orderId,employId, EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        mRxManager.add(observable.subscribe(new MySubscriber<EmResult>(this, false, false, new NoErrSubscriberListener<EmResult>() {
            @Override
            public void onNext(EmResult emResult) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        })));
    }

}
