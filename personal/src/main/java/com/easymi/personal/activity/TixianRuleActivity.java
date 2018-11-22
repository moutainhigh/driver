package com.easymi.personal.activity;

import android.os.Bundle;
import android.text.Html;
import android.webkit.WebView;
import android.widget.TextView;

import com.easymi.component.Config;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.network.NoErrSubscriberListener;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.widget.CusToolbar;
import com.easymi.personal.McService;
import com.easymi.personal.R;
import com.easymi.personal.result.ArticleResult;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by developerLzh on 2017/11/11 0011.
 */

public class TixianRuleActivity extends RxBaseActivity {

    CusToolbar cusToolbar;

    WebView tixianRule;

    @Override
    public void initToolBar() {
        cusToolbar = findViewById(R.id.cus_toolbar);
        cusToolbar.setLeftBack(view -> finish());
        cusToolbar.setTitle(R.string.tixian_rule);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_tixian_rules;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        tixianRule = findViewById(R.id.titian_rule);

        tixianRule.getSettings().setJavaScriptEnabled(true);
        tixianRule.getSettings().setUseWideViewPort(true);
        tixianRule.getSettings().setLoadWithOverviewMode(true);

        tixianRule.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        getArticle("DriverRuleOfCash");
    }

    private void getArticle(String alias) {
        McService api = ApiManager.getInstance().createApi(Config.HOST, McService.class);

        Observable<ArticleResult> observable = api
//                .getArticle(EmUtil.getAppKey(), alias,
//                        EmUtil.getEmployInfo() == null ? null : EmUtil.getEmployInfo().company_id)
                .getArticle()
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, true,
                true, new NoErrSubscriberListener<ArticleResult>() {
            @Override
            public void onNext(ArticleResult articleResult) {
                String html = articleResult.data.content;
                String css = "<style type=\"text/css\"> img {" +
                        "width:auto" +//限定图片宽度填充屏幕
                        "height:auto;" +//限定图片高度自动
                        "}" +
                        "body {" +
                        "margin-right:15px;" +//限定网页中的文字右边距为15px(可根据实际需要进行行管屏幕适配操作)
                        "margin-left:15px;" +//限定网页中的文字左边距为15px(可根据实际需要进行行管屏幕适配操作)
                        "margin-top:15px;" +//限定网页中的文字上边距为15px(可根据实际需要进行行管屏幕适配操作)
                        "font-size:40px;" +//限定网页中文字的大小为40px,请务必根据各种屏幕分辨率进行适配更改
                        "word-wrap:break-word;" +//允许自动换行(汉字网页应该不需要这一属性,这个用来强制英文单词换行,类似于word/wps中的西文换行)
                        "}" +
                        "</style>";

                html = "<html><header>" + css + "</header>" + html + "</html>";

                tixianRule.loadData(html, "text/html; charset=UTF-8", null);
            }
        })));
    }

    @Override
    public boolean isEnableSwipe() {
        return true;
    }
}
