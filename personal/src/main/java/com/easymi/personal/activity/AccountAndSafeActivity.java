package com.easymi.personal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easymi.common.entity.FaceAuthResult;
import com.easymi.common.entity.FaceConfigResult;
import com.easymi.common.authentication.AuthenticationActivity;
import com.easymi.component.Config;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.widget.CusToolbar;
import com.easymi.personal.McService;
import com.easymi.personal.R;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AccountAndSafeActivity extends RxBaseActivity {

    TextView tv_status;
    LinearLayout ll_face_check;
    ImageView iv_right;

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_p_account_sec;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        tv_status = findViewById(R.id.tv_status);
        ll_face_check = findViewById(R.id.ll_face_check);
        iv_right = findViewById(R.id.iv_right);

        findViewById(R.id.delete_account).setOnClickListener(v -> startActivity(new Intent(this, UnregisterActivity.class)));

        faceConfig();
    }

    @Override
    public void initToolBar() {
        super.initToolBar();
        CusToolbar cusToolbar = findViewById(R.id.cus_toolbar);
        cusToolbar.setLeftBack(v -> finish());
        cusToolbar.setTitle("账号与安全");
    }


    /**
     * 司机当前人脸认证状态
     */
    public void faceState(){
        Observable<FaceAuthResult> observable = ApiManager.getInstance()
                .createApi(Config.HOST, McService.class)
                .faceState()
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, false,
                false, faceAuth -> {
            if (faceAuth.data.state == 1){
                //已经认证
                tv_status.setText(getResources().getString(R.string.p_have_check));
                iv_right.setVisibility(View.GONE);
                tv_status.setTextColor(getResources().getColor(R.color.color_999999));


            }else {
                //未认证
                tv_status.setText(getResources().getString(R.string.p_no_check));
                iv_right.setVisibility(View.VISIBLE);
                tv_status.setTextColor(getResources().getColor(R.color.color_red));

                ll_face_check.setOnClickListener(v -> {
                    startActivity(new Intent(this,AuthenticationActivity.class));
                });
            }
        })));
    }

    /**
     * 获取人脸检测开关配置
     */
    public void faceConfig(){
        Observable<FaceConfigResult> observable = ApiManager.getInstance()
                .createApi(Config.HOST, McService.class)
                .faceConfig()
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, false,
                false, faceConfig -> {
            if (faceConfig.data.driverFaceState == 1){
                //开启
                ll_face_check.setVisibility(View.VISIBLE);
                faceState();
            }else {
                //关闭
                ll_face_check.setVisibility(View.GONE);
            }
        })));
    }


    @Override
    protected void onResume() {
        super.onResume();
        faceConfig();
    }
}
