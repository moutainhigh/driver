package com.easymi.personal.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.GlideCircleTransform;
import com.easymi.component.utils.Log;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.widget.CusToolbar;
import com.easymi.personal.McService;
import com.easymi.personal.R;
import com.easymi.personal.result.LoginResult;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by developerLzh on 2017/11/3 0003.
 */
@Route(path = "/personal/PersonalActivity")
public class PersonalActivity extends RxBaseActivity {

    TextView driverName;
    TextView userName;
    TextView driverScore;
    TextView driverBalance;

    ImageView driverPhoto;
    ImageView driverTuiguang;

    ImageView back;

    @Override
    public int getLayoutId() {
        return R.layout.activity_personal;
    }

    @Override
    public void initToolBar() {
        super.initToolBar();
        back = findViewById(R.id.left_icon);
        back.setOnClickListener(view -> finish());
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        driverName = findViewById(R.id.real_name);
        userName = findViewById(R.id.user_name);
        driverScore = findViewById(R.id.driver_score);

        driverPhoto = findViewById(R.id.driver_photo);

        driverTuiguang = findViewById(R.id.driver_tuiguang);

        driverBalance = findViewById(R.id.driver_balance);

        driverTuiguang.setOnClickListener(v -> {
            Intent intent = new Intent(PersonalActivity.this, ShareActivity.class);
//            intent.putExtra("tag", "DriverPromotion");
//            intent.putExtra("title", getString(R.string.person_tuiguang));
            startActivity(intent);
        });

        Employ employ = EmUtil.getEmployInfo();
        showBase(employ);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDriverInfo(EmUtil.getEmployId());
    }

    private void getDriverInfo(Long driverId) {
        Observable<LoginResult> observable = ApiManager.getInstance().createApi(Config.HOST, McService.class)
                .getDriverInfo(driverId, EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, false, true, loginResult -> {
            Employ employ = loginResult.getEmployInfo();
            Log.e("okhttp", employ.toString());
            employ.saveOrUpdate();
            SharedPreferences.Editor editor = XApp.getPreferencesEditor();
            editor.putLong(Config.SP_DRIVERID, employ.id);
            editor.apply();

            showBase(employ);
        })));
    }

    private void showBase(Employ employ) {
        if (employ != null) {
            driverName.setText(employ.real_name);
            userName.setText(employ.user_name);
            driverScore.setText(String.valueOf(employ.score == 0 ? 5.0 : employ.score));
            driverBalance.setText(String.valueOf(employ.balance));
            if (StringUtils.isNotBlank(employ.portrait_path)) {
                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .transform(new GlideCircleTransform())
                        .placeholder(R.mipmap.photo_default)
                        .diskCacheStrategy(DiskCacheStrategy.ALL);
                Glide.with(PersonalActivity.this)
                        .load(Config.IMG_SERVER + employ.portrait_path + Config.IMG_PATH)
                        .apply(options)
                        .into(driverPhoto);
            }
        }
    }

    public void toLiushui(View view) {
        ARouter.getInstance()
                .build("/common/LiushuiActivity")
                .navigation();
    }

    public void toPocket(View view) {
        Intent intent = new Intent(this, PocketActivity.class);
        startActivity(intent);
    }

    public void toRefer(View view) {
        Intent intent = new Intent(this, RecommendMoneyActivity.class);
        startActivity(intent);
    }

    public void toEva(View view) {
        Intent intent = new Intent(this, EvaActivity.class);
        startActivity(intent);
    }

    public void toMessage(View view) {
        Intent intent = new Intent(this, MsgActivity.class);
        startActivity(intent);
    }

    public void toSet(View view) {
        Intent intent = new Intent(this, SetActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean isEnableSwipe() {
        return true;
    }
}
