package com.easymi.personal.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easymi.common.entity.FaceAuth;
import com.easymi.common.entity.FaceConfig;
import com.easymi.component.Config;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.common.faceCheck.RegisterAndRecognizeActivity;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.result.EmResult;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.widget.CusToolbar;
import com.easymi.personal.McService;
import com.easymi.personal.R;
import com.easymi.personal.result.LoginResult;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AccountAndSafeActivity extends RxBaseActivity {

    TextView tv_status;
    LinearLayout ll_face_check;

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
        Observable<FaceAuth> observable = ApiManager.getInstance()
                .createApi(Config.HOST, McService.class)
                .faceState()
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, false,
                false, faceAuth -> {
            if (faceAuth.state == 1){
                //已经认证
                tv_status.setText(getResources().getString(R.string.p_have_check));

                tv_status.setCompoundDrawables(null, null, null, null);

                tv_status.setTextColor(getResources().getColor(R.color.color_999999));
            }else {
                //未认证
                tv_status.setText(getResources().getString(R.string.p_no_check));

                Drawable drawable = getResources().getDrawable(R.mipmap.com_right_arrow);
                drawable.setBounds(5, 0, 0, 0);
                tv_status.setCompoundDrawables(null, null, drawable, null);

                tv_status.setTextColor(getResources().getColor(R.color.color_red));

                ll_face_check.setOnClickListener(v -> startActivity(new Intent(this,RegisterAndRecognizeActivity.class).putExtra("flag",0)));
            }
        })));
    }

    /**
     * 获取人脸检测开关配置
     */
    public void faceConfig(){
        Observable<FaceConfig> observable = ApiManager.getInstance()
                .createApi(Config.HOST, McService.class)
                .faceConfig()
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, false,
                false, faceConfig -> {
            if (faceConfig.driverFaceState == 1){
                //开启
                ll_face_check.setVisibility(View.VISIBLE);
                faceState();
            }else {
                //关闭
                ll_face_check.setVisibility(View.GONE);
            }
        })));
    }


}
