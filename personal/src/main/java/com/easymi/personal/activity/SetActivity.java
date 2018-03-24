package com.easymi.personal.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.easymi.component.Config;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.network.ApiManager;
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

        scrollView = findViewById(R.id.scroll_view);
        swipeBackLayout.setScrollChild(scrollView);

        voiceAble.setChecked(XApp.getMyPreferences().getBoolean(Config.SP_VOICE_ABLE, true));
        shakeAble.setChecked(XApp.getMyPreferences().getBoolean(Config.SP_SHAKE_ABLE, true));
        alwaysOren.setChecked(XApp.getMyPreferences().getBoolean(Config.SP_ALWAYS_OREN, false));
        defaultNavi.setChecked(XApp.getMyPreferences().getBoolean(Config.SP_DEFAULT_NAVI, true));

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
        Intent intent = new Intent(SetActivity.this, HelpCenterActivity.class);
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

    public void contractUs(View view) {
//        Intent intent = new Intent(SetActivity.this, ArticleActivity.class);
//        intent.putExtra("tag", "ContactUs");
//        intent.putExtra("title", getString(R.string.set_contract_us));
//        startActivity(intent);
        PhoneUtil.call(SetActivity.this, EmUtil.getEmployInfo().company_phone);
    }

    public void aboutUs(View view) {
        Intent intent = new Intent(SetActivity.this, AboutUsActivity.class);
//        intent.putExtra("tag", "AboutUs");
//        intent.putExtra("title", getString(R.string.set_about_us));
        startActivity(intent);
    }

    public void logOut(View view) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.set_hint))
                .setMessage(getString(R.string.set_sure_exit))
                .setPositiveButton(getString(R.string.set_sure), (dialogInterface, i) -> {
                    doLogOut();
                })
                .setNegativeButton(getString(R.string.set_cancel), (dialogInterface, i) -> dialogInterface.dismiss())
                .create();
        dialog.show();

    }

    private void doLogOut() {
        McService mcService = ApiManager.getInstance().createApi(Config.HOST, McService.class);

        Observable<EmResult> observable = mcService
                .employLoginOut(EmUtil.getEmployId(), Config.APP_KEY)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, true,
                true, emResult -> EmUtil.employLogout(SetActivity.this))));
    }

    @Override
    public boolean isEnableSwipe() {
        return true;
    }
}
