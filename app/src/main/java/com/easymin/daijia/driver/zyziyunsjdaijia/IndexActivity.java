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
//        Intent intent= new Intent();
//        intent.setClassName(this,"com.easymin.passengerbus.flowmvp.BcFlowActivity");
//        startActivity(intent);
//        ARouter.getInstance().build("/passengerbus/BcFlowActivity").navigation();

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

//        initNaiv();
    }

    public void initNaiv(){
        NaviLatLng start = new NaviLatLng(30.857643, 103.834576);
        NaviLatLng end = new NaviLatLng(30.847643, 103.884576);
        Intent intent = new Intent(this, TestActivity.class);
        intent.putExtra("startLatlng", start);
        intent.putExtra("endLatlng", end);
        intent.putExtra("orderId", 1);
        intent.putExtra("orderType", Config.CITY_LINE);

        intent.putExtra(Config.NAVI_MODE, Config.DRIVE_TYPE);
        startActivity(intent);
    }

//    private void getCustomers(ZXOrder zxOrder) {
//        Observable<EmResult2<List<OrderCustomer>>> observable = ApiManager.getInstance().createApi(Config.HOST, CLService.class)
//                .getOrderCustomers(zxOrder.orderId)
//                .filter(new HttpResultFunc3<>())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//
//        mRxManager.add(observable.subscribe(new MySubscriber<EmResult2<List<OrderCustomer>>>(this, false, false, new NoErrSubscriberListener<EmResult2<List<OrderCustomer>>>() {
//            @Override
//            public void onNext(EmResult2<List<OrderCustomer>> result2) {
//                List<OrderCustomer> orderCustomers = result2.getData();
//                for (int i = 0; i < orderCustomers.size(); i++) {
//                    OrderCustomer orderCustomer = orderCustomers.get(i);
//
//                    orderCustomer.appointTime = orderCustomer.appointTime * 1000;
//                    orderCustomer.num = i;
//                    orderCustomer.acceptSequence = i;
//                    orderCustomer.sendSequence = i;
//                    orderCustomer.status = 0;
//                    orderCustomer.subStatus = 0;
//
//                    orderCustomer.orderId = zxOrder.orderId;
//                    orderCustomer.orderType = zxOrder.orderType;
//
//                    for (OrderCustomer.OrderAddressVo orderAddressVo : orderCustomer.orderAddressVos) {
//                        if (orderAddressVo.type == 1) { //起点
//                            orderCustomer.startAddr = orderAddressVo.address;
//                            orderCustomer.startLat = orderAddressVo.latitude;
//                            orderCustomer.startLng = orderAddressVo.longitude;
//                        } else { //终点
//                            orderCustomer.endAddr = orderAddressVo.address;
//                            orderCustomer.endLat = orderAddressVo.latitude;
//                            orderCustomer.endLng = orderAddressVo.longitude;
//                        }
//                    }
//                    orderCustomer.saveOrUpdate();
//                }
//
//                ARouter.getInstance().build("/cityline/FlowActivity").withSerializable("zxOrder",zxOrder).navigation();
//
//            }
//        })));
//    }

}
