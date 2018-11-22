package com.rvakva.travel.publicdriver;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.easymi.cityline.CLService;
import com.easymi.cityline.activity.CreateOrderActivity;
import com.easymi.cityline.entity.OrderCustomer;
import com.easymi.cityline.entity.ZXOrder;
import com.easymi.cityline.flowMvp.FlowActivity;
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
//        Intent intent = new Intent(this, SplashActivity.class);
//        startActivity(intent);
//        buildZxOrder();

        XApp.getInstance().startLocService();
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
        Intent intent = new Intent(this, CreateOrderActivity.class);
        startActivity(intent);

//        Observable<EmResult2<List<ZXOrder>>> observable = ApiManager.getInstance().createApi(Config.HOST, CLService.class)
//                .queryDriverSchedule()
//                .filter(new HttpResultFunc3<>())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//
//        mRxManager.add(observable.subscribe(new MySubscriber<EmResult2<List<ZXOrder>>>(this,
//                false,
//                false,
//                new NoErrSubscriberListener<EmResult2<List<ZXOrder>>>() {
//                    @Override
//                    public void onNext(EmResult2<List<ZXOrder>> emResult2) {
//                        ZXOrder zxOrder = emResult2.getData().get(0);
//                        zxOrder.orderType = Config.CITY_LINE;
//                        zxOrder.startOutTime = zxOrder.startOutTime * 1000;
//                        zxOrder.startJierenTime = zxOrder.startOutTime - zxOrder.minute * 60 * 1000;
//
//                        if (DymOrder.findByIDType(zxOrder.orderId, Config.CITY_LINE) == null) {
//                            DymOrder dymOrder = new DymOrder();
//                            dymOrder.orderStatus = ZXOrderStatus.WAIT_START;
//                            dymOrder.orderId = zxOrder.orderId;
//                            dymOrder.orderType = Config.CITY_LINE;
//                            dymOrder.saveOrUpdate();
//                        }
//
//                        getCustomers(zxOrder);
//                    }
//                })));
    }

    private void getCustomers(ZXOrder zxOrder) {
        Observable<EmResult2<List<OrderCustomer>>> observable = ApiManager.getInstance().createApi(Config.HOST, CLService.class)
                .getOrderCustomers(zxOrder.orderId)
                .filter(new HttpResultFunc3<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<EmResult2<List<OrderCustomer>>>(this, false, false, new NoErrSubscriberListener<EmResult2<List<OrderCustomer>>>() {
            @Override
            public void onNext(EmResult2<List<OrderCustomer>> result2) {
                List<OrderCustomer> orderCustomers = result2.getData();
                for (int i = 0; i < orderCustomers.size(); i++) {
                    OrderCustomer orderCustomer = orderCustomers.get(i);

                    orderCustomer.appointTime = orderCustomer.appointTime * 1000;
                    orderCustomer.num = i;
                    orderCustomer.acceptSequence = i;
                    orderCustomer.sendSequence = i;
                    orderCustomer.status = 0;
                    orderCustomer.subStatus = 0;

                    orderCustomer.orderId = zxOrder.orderId;
                    orderCustomer.orderType = zxOrder.orderType;

                    for (OrderCustomer.OrderAddressVo orderAddressVo : orderCustomer.orderAddressVos) {
                        if (orderAddressVo.type == 1) { //起点
                            orderCustomer.startAddr = orderAddressVo.address;
                            orderCustomer.startLat = orderAddressVo.latitude;
                            orderCustomer.startLng = orderAddressVo.longitude;
                        } else { //终点
                            orderCustomer.endAddr = orderAddressVo.address;
                            orderCustomer.endLat = orderAddressVo.latitude;
                            orderCustomer.endLng = orderAddressVo.longitude;
                        }
                    }
                    orderCustomer.saveOrUpdate();
                }

                Intent intent = new Intent(IndexActivity.this, FlowActivity.class);
                intent.putExtra("zxOrder", zxOrder);
                startActivity(intent);
                finish();

            }
        })));
    }

    private void buildZxOrder() {
        DecimalFormat df = new DecimalFormat("#0.000000");
        ZXOrder zxOrder = new ZXOrder();
        zxOrder.orderId = 1;
        zxOrder.orderType = Config.CITY_LINE;
        zxOrder.startSite = "温江站";
        zxOrder.endSite = "青白江站";
        zxOrder.startOutTime = System.currentTimeMillis() + 6 * 10 * 1000;
        zxOrder.startJierenTime = System.currentTimeMillis() + 20 * 1000;
        zxOrder.startLat = 30.687326;
        zxOrder.startLng = 103.864365;

        zxOrder.endLat = 30.66837;
        zxOrder.endLng = 103.813924;

        DymOrder dymOrder = new DymOrder();
        dymOrder.orderStatus = ZXOrderStatus.WAIT_START;
        dymOrder.orderId = zxOrder.orderId;
        dymOrder.orderType = Config.CITY_LINE;
        dymOrder.saveOrUpdate();

        if (OrderCustomer.findByIDTypeOrderByAcceptSeq(zxOrder.orderId, zxOrder.orderType).size() == 0) {
            for (int i = 0; i < 6; i++) {
                OrderCustomer orderCustomer = new OrderCustomer();
                orderCustomer.customerId = i;
                orderCustomer.orderId = zxOrder.orderId;
                orderCustomer.orderType = Config.CITY_LINE;
                orderCustomer.name = "刘子豪" + i;
                orderCustomer.phone = "18148140090";
                orderCustomer.photo = "xxxx";
                orderCustomer.startAddr = "起点" + i;
                orderCustomer.endAddr = "终点" + i;

                orderCustomer.startLat = Double.parseDouble(df.format(zxOrder.startLat + (i * 0.001)));
                orderCustomer.startLng = Double.parseDouble(df.format(zxOrder.startLng + (i * 0.001)));

                orderCustomer.endLat = Double.parseDouble(df.format(zxOrder.endLat + (i * 0.001)));
                orderCustomer.endLng = Double.parseDouble(df.format(zxOrder.endLng + (i * 0.001)));

                orderCustomer.appointTime = System.currentTimeMillis() + 2 * 1 * 1000 + i * 1 * 1000;
                orderCustomer.acceptSequence = i;
                orderCustomer.sendSequence = i;
                orderCustomer.num = i;
                orderCustomer.status = 0;
                orderCustomer.subStatus = 0;

                orderCustomer.save();
            }
        }

        Intent intent = new Intent(this, FlowActivity.class);
        intent.putExtra("zxOrder", zxOrder);
        startActivity(intent);
        finish();

    }

}
