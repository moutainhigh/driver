package com.easymi.personal.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.easymi.component.Config;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.Employ;
import com.easymi.component.entity.Vehicle;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.utils.CsEditor;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.GlideCircleTransform;
import com.easymi.component.utils.Log;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.RatingBar;
import com.easymi.personal.McService;
import com.easymi.personal.R;
import com.easymi.personal.activity.register.RegisterBaseActivity;
import com.easymi.personal.result.LoginResult;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: PersonalActivity
 *
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description: 个人中心接 main
 * History:
 */
@Route(path = "/personal/PersonalActivity")
public class PersonalActivity extends RxBaseActivity {

    TextView driverName;
    TextView userName;

    ImageView driverPhoto;

    /**
     * 自定义标题栏
     */
    CusToolbar cusToolbar;
    /**
     * 司机星级
     */
    TextView tv_star;
    /**
     * 司机车辆信息
     */
    TextView tv_car_info;

    /**
     * 我的名片
     */
    LinearLayout lin_card;

    @Override
    public int getLayoutId() {
        return R.layout.activity_person_center;
    }

    @Override
    public void initToolBar() {
        super.initToolBar();

        cusToolbar.setLeftBack(view -> finish());
        cusToolbar.setTitle(R.string.person_center);
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        driverName = findViewById(R.id.real_name);
        userName = findViewById(R.id.user_name);


        cusToolbar = findViewById(R.id.cus_toolbar);
        tv_star = findViewById(R.id.tv_star);
        tv_car_info = findViewById(R.id.tv_car_info);

        driverPhoto = findViewById(R.id.driver_photo);

        lin_card = findViewById(R.id.lin_card);

        Employ employ = EmUtil.getEmployInfo();

        if (employ.serviceType.equals(Config.CUSTOMBUS) ||
                employ.serviceType.equals(Config.COUNTRY)){
            lin_card.setVisibility(View.VISIBLE);
        }else {
            lin_card.setVisibility(View.GONE);
        }

        showBase(employ);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDriverInfo(EmUtil.getEmployId());
    }

    /**
     * 获取司机信息
     *
     * @param driverId
     */
    private void getDriverInfo(Long driverId) {
        Observable<LoginResult> observable = ApiManager.getInstance().createApi(Config.HOST, McService.class)
                .getDriverInfo(driverId, EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, false, true, loginResult -> {
            Employ employ = loginResult.data;
            employ.saveOrUpdate();
            CsEditor editor = new CsEditor();
            editor.putLong(Config.SP_DRIVERID, employ.id);
            editor.apply();

            showBase(employ);
        })));
    }

    /**
     * 展示司机基本信息
     *
     * @param employ
     */
    private void showBase(Employ employ) {
        if (employ != null) {
            driverName.setText(employ.realName);
            userName.setText( employ.userName );
            tv_star.setText((employ.star == 0 ? 5.0 : employ.star)+"");

            if (Vehicle.exists(employ.id)){
                Vehicle vehicle = Vehicle.findByEmployId(employ.id);
                tv_car_info.setText(vehicle.brand+" "+vehicle.plateColor+" "+vehicle.vehicleNo);
            }else {
                tv_car_info.setText("未绑定改业务车辆");
            }

            if (StringUtils.isNotBlank(employ.portraitPath)) {
                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .transform(new GlideCircleTransform())
                        .placeholder(R.mipmap.photo_default)
                        .diskCacheStrategy(DiskCacheStrategy.ALL);
                Glide.with(PersonalActivity.this)
                        .load(Config.IMG_SERVER + employ.portraitPath + Config.IMG_PATH)
                        .apply(options)
                        .into(driverPhoto);
            }
        }
    }

    /**
     * 跳转流水
     *
     * @param view
     */
    public void toLiushui(View view) {
        ARouter.getInstance()
                .build("/common/LiushuiActivity")
                .navigation();
    }

    /**
     * 跳转我的钱包
     *
     * @param view
     */
    public void toPocket(View view) {
        Intent intent = new Intent(this, PocketActivity.class);
        startActivity(intent);
    }

    /**
     * 跳转评价
     *
     * @param view
     */
    public void toEva(View view) {
        Intent intent = new Intent(this, EvaActivity.class);
        startActivity(intent);
    }

    /**
     * 消息中心
     *
     * @param view
     */
    public void toMessage(View view) {
        Intent intent = new Intent(this, MsgActivity.class);
        startActivity(intent);
    }

    /**
     * 设置
     *
     * @param view
     */
    public void toSet(View view) {
        Intent intent = new Intent(this, SetActivity.class);
        startActivity(intent);
    }

    /**
     * 我的名片
     *
     * @param view
     */
    public void toCard(View view) {
        Intent intent = new Intent(this, MyCardActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean isEnableSwipe() {
        return true;
    }
}
