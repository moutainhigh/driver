package com.easymi.personal.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.easymi.common.CommApiService;
import com.easymi.common.entity.PushAnnouncement;
import com.easymi.component.Config;
import com.easymi.component.R;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.network.NoErrSubscriberListener;
import com.easymi.component.utils.EmUtil;
import com.easymi.personal.McService;
import com.easymi.personal.result.ArticleResult;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: ArticleActivity
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description: 文章相关的展示界面
 * History:
 */

@Route(path = "/personal/ArticleActivity")
public class ArticleActivity extends RxBaseActivity implements View.OnClickListener {
    /**
     * 浏览器加载控件
     */
    WebView webView;
    /**
     * 标题
     */
    TextView title;
    /**
     * 关闭按钮
     */
    TextView closeAll;

    /**
     * 加载进度条
     */
    private ProgressBar myProgressBar;

    @Override
    public int getLayoutId() {
        return R.layout.activity_web;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        init();
        initWeb();
    }


    @Override
    public void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (webView.canGoBack()) {
            webView.goBack();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        webView.loadData("about:blank", "text/html", "UTF-8");
        webView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        webView.clearCache(true);
        webView.destroy();
        super.onDestroy();
    }

    private String tag;
    private Long articleId;
    /**
     * 公告id
     */
    private Long annId;

    /**
     * 初始化数据和加载文章
     */
    public void init() {

        Intent localIntent = getIntent();

        tag = getIntent().getStringExtra("tag");

        String titleStr = localIntent.getStringExtra("title");

        articleId = getIntent().getLongExtra("articleId", -1);

        annId = getIntent().getLongExtra("annId", -1);

        title = findViewById(R.id.title);
        title.setText(titleStr);
        webView = findViewById(R.id.web_view);

        closeAll = findViewById(R.id.close_all);
        closeAll.setOnClickListener(this);

        myProgressBar = findViewById(R.id.myProgressBar);

        if (annId != -1) {
            getAnn(annId);
            return;
        }

        if (articleId != -1) {
            getArticle(articleId);
            return;
        }

        getArticle(tag);
    }

    /**
     * 获取公告信息
     * @param annId
     */
    private void getAnn(long annId) {
        Observable<PushAnnouncement> observable = ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .employAfficheById(annId, EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, true, true, new NoErrSubscriberListener<PushAnnouncement>() {
            @Override
            public void onNext(PushAnnouncement annResult) {
                String html = annResult.request.content;
                String css = "<style type=\"text/css\"> img {" +
                        "width:auto;" +//限定图片宽度填充屏幕
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


                webView.loadData(html, "text/html; charset=UTF-8", null);
            }
        })));
    }

    /**
     * 获取文章信息 通过文章id
     * @param articleId
     */
    private void getArticle(long articleId) {
        McService api = ApiManager.getInstance().createApi(Config.HOST, McService.class);

        Observable<ArticleResult> observable = api
                .getArticleById(EmUtil.getAppKey(), articleId)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, true,
                true, emResult -> {
            String html = emResult.data.content;

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


            webView.loadData(html, "text/html; charset=UTF-8", null);
        })));
    }
    /**
     * 获取文章信息 通过文章类型
     * @param alias
     */
    private void getArticle(String alias) {
        McService api = ApiManager.getInstance().createApi(Config.HOST, McService.class);

        Observable<ArticleResult> observable = api
//                .getArticle(EmUtil.getAppKey(), alias,
//                        EmUtil.getEmployInfo() == null ? null : EmUtil.getEmployInfo().company_id)
                .getArticle(alias)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, true,
                true, articleResult -> {
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


            webView.loadData(html, "text/html; charset=UTF-8", null);
        })));
    }

    /**
     * 显示关闭按钮
     */
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (webView.canGoBack()) {
                        closeAll.setVisibility(View.VISIBLE);
                    } else {
                        closeAll.setVisibility(View.GONE);
                    }
                    break;
            }
            return true;
        }
    });

    /**
     * 初始化webview
     */
    @SuppressLint("SetJavaScriptEnabled")
    public void initWeb() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setAllowFileAccess(false);
        webView.getSettings().setSavePassword(false);

        webView.removeJavascriptInterface("searchBoxJavaBridge_");
        webView.removeJavascriptInterface("accessibility");
        webView.removeJavascriptInterface("accessibilityTraversal");


        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);


        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                handler.sendEmptyMessage(0);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    myProgressBar.setVisibility(View.INVISIBLE);
                } else {
                    if (View.INVISIBLE == myProgressBar.getVisibility()) {
                        myProgressBar.setVisibility(View.VISIBLE);
                    }
                    myProgressBar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }
        });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.close_all) {
            finish();

        } else {
        }
    }

    public void backAction(View view) {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                finish();
            }
        }
        return false;
    }

    @Override
    public boolean isEnableSwipe() {
        return true;
    }
}
