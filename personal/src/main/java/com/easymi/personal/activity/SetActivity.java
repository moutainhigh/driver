package com.easymi.personal.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;

import com.alibaba.android.arouter.facade.annotation.Route;
//import com.easymi.common.push.MQTTService;
import com.easymi.common.mvp.work.WorkPresenter;
import com.easymi.common.push.WorkTimeCounter;
import com.easymi.common.util.GPSSetting;
import com.easymi.component.Config;
import com.easymi.component.activity.WebActivity;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.result.EmResult;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.PhoneUtil;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.switchButton.SwitchButton;
import com.easymi.personal.McService;
import com.easymi.personal.R;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by developerLzh on 2017/11/6 0006.
 */
@Route(path = "/personal/SetActivity")
public class SetActivity extends RxBaseActivity {

    SwitchButton voiceAble;
    SwitchButton shakeAble;
    SwitchButton alwaysOren;
    SwitchButton defaultNavi;
    SwitchButton backRun;
    SwitchButton gpsFilter;

    private ScrollView scrollView;

    @Override
    public int getLayoutId() {
        return R.layout.activity_set;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        voiceAble = findViewById(R.id.voice_able_btn);
        shakeAble = findViewById(R.id.shake_btn);
        alwaysOren = findViewById(R.id.oren_btn);
        defaultNavi = findViewById(R.id.default_navi);
        backRun = findViewById(R.id.back_run);
        gpsFilter = findViewById(R.id.gps_filter);

        scrollView = findViewById(R.id.scroll_view);

        voiceAble.setChecked(XApp.getMyPreferences().getBoolean(Config.SP_VOICE_ABLE, true));
        shakeAble.setChecked(XApp.getMyPreferences().getBoolean(Config.SP_SHAKE_ABLE, true));
        alwaysOren.setChecked(XApp.getMyPreferences().getBoolean(Config.SP_ALWAYS_OREN, false));
        defaultNavi.setChecked(XApp.getMyPreferences().getBoolean(Config.SP_DEFAULT_NAVI, true));
        backRun.setChecked(XApp.getMyPreferences().getBoolean(Config.SP_PLAY_CLIENT_MUSIC, true));
        gpsFilter.setChecked(XApp.getMyPreferences().getBoolean(Config.SP_GPS_FILTER, false));

        initSwitch();

    }

    private void initSwitch() {
        voiceAble.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = XApp.getPreferencesEditor();
            editor.putBoolean(Config.SP_VOICE_ABLE, isChecked);
            editor.apply();
        });
        shakeAble.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = XApp.getPreferencesEditor();
            editor.putBoolean(Config.SP_SHAKE_ABLE, isChecked);
            editor.apply();
        });
        alwaysOren.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = XApp.getPreferencesEditor();
            editor.putBoolean(Config.SP_ALWAYS_OREN, isChecked);
            editor.apply();
        });
        defaultNavi.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = XApp.getPreferencesEditor();
            editor.putBoolean(Config.SP_DEFAULT_NAVI, isChecked);
            editor.apply();
        });
        backRun.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = XApp.getPreferencesEditor();
            editor.putBoolean(Config.SP_PLAY_CLIENT_MUSIC, isChecked);
            editor.apply();
        });
        gpsFilter.setOnCheckedChangeListener((buttonView, isChecked) -> {
            //打开时过滤掉网络类型的定位点,(即只上传GPS类型的定位点)
            GPSSetting.getInstance().setNetEnable(!isChecked);
        });
    }

    @Override
    public void initToolBar() {
        CusToolbar cusToolbar = findViewById(R.id.cus_toolbar);
        cusToolbar.setOnClickListener(v -> finish());
        cusToolbar.setTitle(R.string.person_set);
    }

    public void changePsw(View view) {
        Intent intent = new Intent(this, ChangeActivity.class);
        startActivity(intent);
    }

    public void choiceLanguage(View view) {
        Intent intent = new Intent(this, LanguageActivity.class);
        startActivity(intent);
    }

    public void toStats(View view) {
        Intent intent = new Intent(this, StatsActivity.class);
        startActivity(intent);
    }

    public void helpCenter(View view) {
//        Intent intent = new Intent(SetActivity.this, HelpCenterActivity.class);
//        startActivity(intent);
        Intent intent = new Intent(this, WebActivity.class);
        intent.putExtra("url", "http://h5.xiaokakj.com/#/protocol?articleName=driverHelp&appKey="+Config.APP_KEY);
        intent.putExtra("title", getString(R.string.set_about_us));
        startActivity(intent);
    }

    public void feedBack(View view) {
        Intent intent = new Intent(this, FeedbackActivity.class);
        startActivity(intent);
    }

    public void naviPrefence(View view) {
        Intent intent = new Intent(this, NaviSetActivity.class);
        startActivity(intent);
    }

    public void notifyHelp(View view) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse("http://help.xiaokayun.cn");
        intent.setData(content_url);
        startActivity(intent);
    }

    public void contractUs(View view) {
//        Intent intent = new Intent(SetActivity.this, ArticleActivity.class);
//        intent.putExtra("tag", "ContactUs");
//        intent.putExtra("title", getString(R.string.set_contract_us));
//        startActivity(intent);
//        PhoneUtil.call(SetActivity.this, EmUtil.getEmployInfo().company_phone);
        PhoneUtil.call(SetActivity.this,"11111111");
    }

    public void aboutUs(View view) {
//        Intent intent = new Intent(SetActivity.this, AboutUsActivity.class);
////        intent.putExtra("tag", "AboutUs");
////        intent.putExtra("title", getString(R.string.set_about_us));
//        startActivity(intent);
        Intent intent = new Intent(this, WebActivity.class);
        intent.putExtra("url", "http://h5.xiaokakj.com/#/protocol?articleName=driverAboutUs&appKey="+Config.APP_KEY);
        intent.putExtra("title", getString(R.string.set_about_us));
        startActivity(intent);
    }

    public void logOut(View view) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.set_hint))
                .setMessage(getString(R.string.set_sure_exit))
                .setPositiveButton(getString(R.string.set_sure), (dialogInterface, i) -> {
                    doLogOut();
//                    EmUtil.employLogout(SetActivity.this);
                })
                .setNegativeButton(getString(R.string.set_cancel), (dialogInterface, i) -> dialogInterface.dismiss())
                .create();
        dialog.show();

    }


    //注销接口没有，临时调用了下线接口，解决gu没删除的问题 // 被别人登录后就gg了，下线都报410
    private void doLogOut() {
        if (null != WorkPresenter.timeCounter) {
            WorkPresenter.timeCounter.forceUpload(-1);
        }
        McService mcService = ApiManager.getInstance().createApi(Config.HOST, McService.class);
        Observable<EmResult> observable = mcService
                .offline(EmUtil.getEmployId(), EmUtil.getEmployInfo().companyId)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, true,
                true, new HaveErrSubscriberListener<EmResult>() {
            @Override
            public void onNext(EmResult emResult) {
                EmUtil.employLogout(SetActivity.this);
            }

            @Override
            public void onError(int code) {
                EmUtil.employLogout(SetActivity.this);
            }
        })));
    }

    @Override
    public boolean isEnableSwipe() {
        return true;
    }
}
