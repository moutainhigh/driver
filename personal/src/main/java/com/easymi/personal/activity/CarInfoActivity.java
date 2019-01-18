package com.easymi.personal.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.easymi.component.Config;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.network.NoErrSubscriberListener;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.GlideCircleTransform;
import com.easymi.component.widget.CusToolbar;
import com.easymi.personal.McService;
import com.easymi.personal.R;
import com.easymi.personal.adapter.SameCarAdapter;
import com.easymi.personal.entity.CarInfo;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: CarInfoActivity
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description: 暂未使用
 * History:
 */

public class CarInfoActivity extends RxBaseActivity {

    private TextView carNo;
    private TextView carColor;
    private TextView carBrand;
    private TextView carModel;
    private TextView carTypeName;
    private ImageView carImv;
    private RecyclerView rv;


    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.p_activity_car_info;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        carNo = findViewById(R.id.carNo);
        carColor = findViewById(R.id.carColor);
        carBrand = findViewById(R.id.carBrand);
        carModel = findViewById(R.id.carModel);
        carTypeName = findViewById(R.id.carTypeName);
        carImv = findViewById(R.id.carImv);
        rv = findViewById(R.id.rv);

        getCarInfo();
    }

    @Override
    public void initToolBar() {
        CusToolbar toolbar = findViewById(R.id.cus_toolbar);
        toolbar.setLeftBack(view -> finish());
        toolbar.setTitle(R.string.p_car_info);
    }

    /**
     * 获取车辆信息
     */
    private void getCarInfo() {
        Observable<CarInfo> observable = ApiManager.getInstance().createApi(Config.HOST, McService.class)
                .getCarInfo(EmUtil.getEmployId(), EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, true, true, new NoErrSubscriberListener<CarInfo>() {
            @Override
            public void onNext(CarInfo carInfo) {
                showCarInfo(carInfo);
            }
        })));

    }

    /**
     * 暂时车辆信息
     * @param carInfo
     */
    private void showCarInfo(CarInfo carInfo) {
        if (carInfo == null) {
            return;
        }
        if (carInfo.vehicle != null) {
            carTypeName.setText(carInfo.vehicle.vehicleType);
            carNo.setText("车牌号：" + carInfo.vehicle.vehicleNo);
            carBrand.setText("车辆品牌：" + carInfo.vehicle.brand);
            carColor.setText("车身颜色：" + carInfo.vehicle.vehicleColor);
            carModel.setText("车辆型号：" + carInfo.vehicle.model);
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .transform(new GlideCircleTransform())
                    .placeholder(R.mipmap.p_default_car)
                    .error(R.mipmap.p_default_car)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(CarInfoActivity.this)
                    .load(Config.IMG_SERVER + carInfo.vehicle.photo)
                    .apply(options)
                    .into(carImv);
        }


        if (carInfo.employees != null) {
            SameCarAdapter adapter = new SameCarAdapter(this, carInfo.employees);
            rv.setLayoutManager(new LinearLayoutManager(this));
            rv.setAdapter(adapter);
        }
    }


}
