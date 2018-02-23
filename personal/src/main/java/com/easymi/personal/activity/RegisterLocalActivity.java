package com.easymi.personal.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.easymi.component.Config;
import com.easymi.component.utils.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.widget.MyWebChomeClient;
import com.easymi.personal.R;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.io.File;
import java.util.List;

/**
 * Created by developerLzh on 2017/5/4.
 */
public class RegisterLocalActivity extends RxBaseActivity implements View.OnClickListener {
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
//        webView.loadData("about:blank", "text/html", "UTF-8");
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
        url = Config.REGISTER_URL;
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
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setDomStorageEnabled(false);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setAllowContentAccess(true);

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

        webView.setWebChromeClient(new MyWebChomeClient(openFileChooserCallBack) {
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

    private ValueCallback<Uri> uploadMsg44;
    private ValueCallback<Uri[]> uploadMsg50;

    private MyWebChomeClient.OpenFileChooserCallBack openFileChooserCallBack
            = new MyWebChomeClient.OpenFileChooserCallBack() {
        @Override
        public void openFileChooserCallBack(ValueCallback<Uri> uploadMsg, String acceptType) {
            Log.e("tag", "openFileChooserCallBack");
            uploadMsg44 = uploadMsg;
            choosePic(1, 1);
        }

        @Override
        public boolean openFileChooserCallBackAndroid5(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
            Log.e("tag", "openFileChooserCallBackAndroid5");
            uploadMsg50 = filePathCallback;
            choosePic(1, 1);
            return true;
        }
    };

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
        runOnUiThread(this::finish);
    }

    public void choosePic(int x, int y) {
        // 进入相册 以下是例子：用不到的api可以不写
        PictureSelector.create(RegisterLocalActivity.this)
                .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
                .maxSelectNum(1)// 最大图片选择数量 int
                .minSelectNum(1)// 最小选择数量 int
                .imageSpanCount(4)// 每行显示个数 int
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .isCamera(true)// 是否显示拍照按钮 true or false
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                .setOutputCameraPath("/emdriver")// 自定义拍照保存路径,可不填
                .enableCrop(false)// 是否裁剪 true or false
                .compress(true)// 是否压缩 true or false
                .compressMode(PictureConfig.LUBAN_COMPRESS_MODE)//系统自带 or 鲁班压缩 PictureConfig.SYSTEM_COMPRESS_MODE or LUBAN_COMPRESS_MODE
                .withAspectRatio(x, y)// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                .hideBottomControls(false)// 是否显示uCrop工具栏，默认不显示 true or false
                .isGif(false)// 是否显示gif图片 true or false
                .freeStyleCropEnabled(true)// 裁剪框是否可拖拽 true or false
                .circleDimmedLayer(false)// 是否圆形裁剪 true or false
                .showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                .showCropGrid(true)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                .openClickSound(false)// 是否开启点击声音 true or false
                .previewEggs(false)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
                .rotateEnabled(true) // 裁剪是否可旋转图片 true or false
                .scaleEnabled(true)// 裁剪是否可放大缩小图片 true or false
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PictureConfig.CHOOSE_REQUEST) {
            if (resultCode == RESULT_OK) {
                List<LocalMedia> images = PictureSelector.obtainMultipleResult(data);
                if (images != null && images.size() > 0) {
                    String path = images.get(0).getCompressPath();
                    Uri uri = Uri.fromFile(new File(path));
                    if (null != uploadMsg44) {
                        uploadMsg44.onReceiveValue(uri);
                    } else if (null != uploadMsg50) {
                        uploadMsg50.onReceiveValue(new Uri[]{uri});
                    }
                }
            } else {
                if (null != uploadMsg44) {
                    uploadMsg44.onReceiveValue(null);
                } else if (null != uploadMsg50) {
                    uploadMsg50.onReceiveValue(null);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
