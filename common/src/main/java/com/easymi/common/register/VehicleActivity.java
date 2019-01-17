package com.easymi.common.register;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.easymi.common.CommApiService;
import com.easymi.common.R;
import com.easymi.common.entity.Vehicles;
import com.easymi.component.Config;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.network.NoErrSubscriberListener;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author hufeng
 * 已废弃
 */
public class VehicleActivity extends RxBaseActivity {

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.com_activity_vehicle;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        bindViews();
        getVehichles();
    }

    private VehicleAdapter adapter;

    private void bindViews() {
        findViewById(R.id.left_icon).setOnClickListener(v -> finish());
        RecyclerView rv = findViewById(R.id.rv);
        adapter = new VehicleAdapter();
        adapter.setOnVehicleClickListener(vehicle -> {
            Intent intent = new Intent();
            intent.putExtra("vehicleName", vehicle.chinese);
            setResult(Activity.RESULT_OK, intent);
            finish();
        });
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
    }

    private void getVehichles() {
        long brandId = getIntent().getLongExtra("brandId", -1);
        Observable<Vehicles> observable = ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .getVehicles(brandId, 1, 80)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        Subscription d = observable.subscribe(new MySubscriber<Vehicles>(this, false, false, new NoErrSubscriberListener<Vehicles>() {
            @Override
            public void onNext(Vehicles vehicles) {
                adapter.setDatas(vehicles.vehicleList);
            }
        }));
        mRxManager.add(d);
    }

}
