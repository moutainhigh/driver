package com.easymi.personal.activity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.easymi.component.Config;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.EmLoc;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.result.EmResult;
import com.easymi.component.utils.AesUtil;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.ToastUtil;
import com.easymi.personal.McService;
import com.easymi.personal.R;
import com.easymi.personal.entity.BigBusiness;
import com.easymi.personal.result.BusinessResult;
import com.easymi.personal.result.CompanyResult;
import com.easymi.personal.result.LoginResult;
import com.google.gson.Gson;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by developerLzh on 2017/5/4.
 */
@Route(path = "/component/WebActivity")
public class RegisterWebActivity extends RxBaseActivity implements View.OnClickListener {
    public String url;

    WebView webView;

    TextView title;

    TextView closeAll;

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

    public void init() {

//        url = "file:///android_asset/web.html";
        url = "http://192.168.0.106:3000";
        String titleStr = getString(R.string.register_title);
        title = findViewById(R.id.title);
        title.setText(titleStr);
        webView = findViewById(R.id.web_view);
        closeAll = findViewById(R.id.close_all);
        closeAll.setOnClickListener(this);
        myProgressBar = findViewById(R.id.myProgressBar);

    }

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

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    public void initWeb() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);

        webView.addJavascriptInterface(this, "xiaoka");

        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.loadUrl(url);


        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                handler.sendEmptyMessage(0);
                getCompanyId();
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
//		super.backAction(view);
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

    @JavascriptInterface
    public void finishActivity() {
        runOnUiThread(() -> finish());
    }

    public int reloadTime = 0;

    private void getCompanyId() {
        EmLoc emLoc = EmUtil.getLastLoc();
        if (null == emLoc) {
            ToastUtil.showMessage(RegisterWebActivity.this, getString(R.string.register_no_loc));
            return;
        }

        McService api = ApiManager.getInstance().createApi(Config.HOST, McService.class);

        Observable<CompanyResult> observable = api
                .getCompany(emLoc.cityCode, emLoc.adCode, Config.APP_KEY)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, true,
                true, new HaveErrSubscriberListener<CompanyResult>() {
            @Override
            public void onNext(CompanyResult emResult) {
                webView.loadUrl("javascript:getBusinesses(" + "'" + new Gson().toJson(emResult) + "'" + ")");
//                webView.loadUrl("javascript:getChildrenBusinesses(" + "'代驾'" + ")");
            }

            @Override
            public void onError(int code) {
                if (reloadTime < 3) {
                    ToastUtil.showMessage(RegisterWebActivity.this, getString(R.string.register_re_company));
                    getCompanyId();
                    reloadTime++;
                } else {
                    ToastUtil.showMessage(RegisterWebActivity.this, getString(R.string.register_finish));
                    finish();
                }
            }
        })));
    }

    @JavascriptInterface
    public void childrenBusiness(String obj) {

        Log.e("obj", obj);

        BigBusiness business = new Gson().fromJson(obj, BigBusiness.class);

        McService api = ApiManager.getInstance().createApi(Config.HOST, McService.class);

        Observable<BusinessResult> observable = api
                .getBusiness(business.companyId, business.business, Config.APP_KEY)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, true,
                true, new HaveErrSubscriberListener<BusinessResult>() {
            @Override
            public void onNext(BusinessResult emResult) {
//                webView.loadUrl("javascript:getChildrenBusinesses(" + "'代驾'" + ")");
                emResult.business = business.business;
                webView.loadUrl("javascript:getChildrenBusinesses(" + "'" + new Gson().toJson(emResult) + "'" + ")");
            }

            @Override
            public void onError(int code) {
                ToastUtil.showMessage(RegisterWebActivity.this, getString(R.string.register_re_company));
            }
        })));
    }
}
