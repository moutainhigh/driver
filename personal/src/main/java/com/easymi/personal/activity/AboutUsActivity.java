package com.easymi.personal.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.easymi.component.Config;
import com.easymi.component.activity.WebActivity;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.network.NoErrSubscriberListener;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.GlideCircleTransform;
import com.easymi.component.utils.PhoneUtil;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.NoUnderLineSpan;
import com.easymi.personal.McService;
import com.easymi.personal.R;
import com.easymi.personal.result.ArticleResult;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: AboutUsActivity
 *
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description: 关于我们
 * History:
 */

public class AboutUsActivity extends RxBaseActivity {

    private TextView webSiteText;
    private TextView phoneText;
    private WebView webView;
    private TextView versionText;

    private CusToolbar cusToolbar;

    private ImageView logo;

    public String url;

    TextView bottomText;

    @Override
    public int getLayoutId() {
        return R.layout.activity_about_us;
    }

    @Override
    public void initToolBar() {
        super.initToolBar();
        cusToolbar = findViewById(R.id.cus_toolbar);
        cusToolbar.setLeftBack(view -> finish());
        cusToolbar.setTitle(R.string.set_about_us);
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        webSiteText = findViewById(R.id.website);
        phoneText = findViewById(R.id.phone);
        webView = findViewById(R.id.web_view);
        versionText = findViewById(R.id.version);

        logo = findViewById(R.id.image_view_logo);

        try {
            versionText.setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        phoneText.setOnClickListener(view -> {
            if (StringUtils.isNotBlank(phoneText.getText().toString())) {
                PhoneUtil.call(AboutUsActivity.this, phoneText.getText().toString());
            }
        });

        webSiteText.setOnClickListener(view -> {
            if (StringUtils.isNotBlank(webSiteText.getText().toString())) {
                Uri uri = Uri.parse(webSiteText.getText().toString());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        url = Config.H5_HOST + "#/protocol?articleName=driverAboutUs&appKey=" + Config.APP_KEY;

        initWeb();

        bottomText = findViewById(R.id.bottom_agreement_text);

        bottomText.setVisibility(View.VISIBLE);

        String s1 = "《隐私权政策》";

        NoUnderLineSpan noUnderLineSpan = new NoUnderLineSpan(this, WebActivity.IWebVariable.DRIVER_PRIVACY_POLICY, R.string.driver_policy);

        String s2 = "    和    ";

        String s3 = "《服务人员合作协议》";
        NoUnderLineSpan noUnderLineSpan3 = new NoUnderLineSpan(this, WebActivity.IWebVariable.DRIVER_LOGIN, R.string.driver_login);

        SpannableString text5 = new SpannableString(s1 + s2 + s3);
        text5.setSpan(noUnderLineSpan, 0, s1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        text5.setSpan(noUnderLineSpan3, s2.length() + s1.length(), s2.length() + s1.length() + s3.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        bottomText.setText(text5);
        bottomText.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * 初始化webview配置
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
        //解决部分H5中的一些控件标签可能使用后android中不支持 造成的白屏不显示问题
        webView.getSettings().setDomStorageEnabled(true);

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
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
        webView.loadUrl(url);
    }


    @Override
    public void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    public boolean isEnableSwipe() {
        return true;
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

//    /**
//     * 获取文章
//     */
//    private void getArticle() {
//        McService api = ApiManager.getInstance().createApi(Config.HOST, McService.class);
//
//        Observable<ArticleResult> observable = api
//                .getArticle(EmUtil.getAppKey(), "DriverAboutUs",
//                        EmUtil.getEmployInfo() == null ? null : EmUtil.getEmployInfo().companyId)
//                .filter(new HttpResultFunc<>())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//
//        mRxManager.add(observable.subscribe(new MySubscriber<>(this, true,
//                true, articleResult -> {
//            String html = articleResult.data.content;
//
//            String css = "<style type=\"text/css\"> img {" +
//                    "width:auto;" +//限定图片宽度填充屏幕
//                    "height:auto;" +//限定图片高度自动
//                    "}" +
//                    "body {" +
//                    "margin-right:15px;" +//限定网页中的文字右边距为15px(可根据实际需要进行行管屏幕适配操作)
//                    "margin-left:15px;" +//限定网页中的文字左边距为15px(可根据实际需要进行行管屏幕适配操作)
//                    "margin-top:15px;" +//限定网页中的文字上边距为15px(可根据实际需要进行行管屏幕适配操作)
//                    "font-size:40px;" +//限定网页中文字的大小为40px,请务必根据各种屏幕分辨率进行适配更改
//                    "word-wrap:break-word;" +//允许自动换行(汉字网页应该不需要这一属性,这个用来强制英文单词换行,类似于word/wps中的西文换行)
//                    "}" +
//                    "</style>";
//
//            html = "<html><header>" + css + "</header>" + html + "</html>";
//
//            webView.loadData(html, "text/html; charset=UTF-8", null);
//
//            phoneText.setText(articleResult.data.phone);
//            webSiteText.setText(articleResult.data.url);
//
//            if (StringUtils.isNotBlank(articleResult.data.logo)) {
//                if (StringUtils.isNotBlank(articleResult.data.logo)) {
//                    RequestOptions options = new RequestOptions()
//                            .placeholder(R.mipmap.ic_launcher)
//                            .diskCacheStrategy(DiskCacheStrategy.ALL);
//                    Glide.with(AboutUsActivity.this)
//                            .load(Config.IMG_SERVER + articleResult.data.logo + Config.IMG_PATH)
//                            .apply(options)
//                            .into(logo);
//                }
//            }
//        }
//        )));
//    }
}
