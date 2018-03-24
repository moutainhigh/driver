package com.easymi.personal.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.easymi.component.Config;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.Employ;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.Log;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.CusToolbar;
import com.easymi.personal.McService;
import com.easymi.personal.R;
import com.easymi.personal.entity.ShareInfo;
import com.easymi.personal.result.ArticleResult;
import com.easymi.personal.result.ShareResult;
import com.easymi.personal.util.QrCodeUtil;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.io.File;
import java.util.ArrayList;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by developerLzh on 2017/11/11 0011.
 */

public class ShareActivity extends RxBaseActivity {

    private CusToolbar cusToolbar;
    private ImageView qrCodeImg;
    private ImageView shareWechat;
    private ImageView shareCircle;
    private ImageView shareQQ;
    private ImageView shareQzone;

    private IWXAPI iwxapi;
    private Tencent mTencent;

    private ShareInfo shareInfo;

    private Bitmap bitmap;

    private BaseUiListener listener;

    @Override
    public void initToolBar() {
        super.initToolBar();
        cusToolbar = findViewById(R.id.cus_toolbar);
        cusToolbar.setLeftBack(view -> finish());
        cusToolbar.setTitle(R.string.person_tuiguang);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_tuiguang;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        qrCodeImg = findViewById(R.id.qr_code_img);


        shareWechat = findViewById(R.id.ic_wechat_share);
        shareWechat.setOnClickListener(view -> share2Wx());

        shareCircle = findViewById(R.id.ic_circle);
        shareCircle.setOnClickListener(view -> share2WxCircle());

        shareQQ = findViewById(R.id.ic_qq);
        shareQQ.setOnClickListener(view -> share2QQ());

        shareQzone = findViewById(R.id.ic_qzone);
        shareQzone.setOnClickListener(view -> share2Qzone());

        listener = new BaseUiListener();


        getShare();

        reg2Wx();
        reg2QQ();
    }


    /**
     * 加载二维码图片
     */
    private void initQrImg() {

        new Thread(() -> {
            int radius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());
            bitmap = QrCodeUtil.createQRImage(shareInfo.shareUrl, radius, radius);
            runOnUiThread(() -> {
                QrCodeUtil.saveBitmap(ShareActivity.this, QrCodeUtil.QR_NAME, bitmap);
                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL);
                Glide.with(ShareActivity.this)
                        .load(QrCodeUtil.QR_FULL_PATH)
                        .apply(options)
                        .into(qrCodeImg);
            });
        }).start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != bitmap) {
            bitmap.recycle();
        }

    }

    private void getShare() {
        Employ employ = EmUtil.getEmployInfo();

        McService api = ApiManager.getInstance().createApi(Config.HOST, McService.class);

        Observable<ShareResult> observable = api
                .shareLink(employ.id, employ.company_id, Config.APP_KEY, 1)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, false,
                false, shareResult -> {
            shareInfo = new ShareInfo();
            shareInfo.shareUrl = shareResult.url;
            shareInfo.shareTitle = "这是内置于APP内的一段标题";
            shareInfo.shareContent = "这是内置于APP内的一段内容";
            initQrImg();
        })));
    }

    /**
     * 注册到微信
     */
    private void reg2Wx() {
        iwxapi = WXAPIFactory.createWXAPI(this, Config.WX_APP_ID, true);
        iwxapi.registerApp(Config.WX_APP_ID);  //将应用app注册到微信
    }

    /**
     * 注册到QQ
     */
    private void reg2QQ() {
        mTencent = Tencent.createInstance(Config.QQ_APP_ID, this.getApplicationContext());
    }

    void share2QQ() {
        //分享到QQ好友
        if (bitmap == null) {
            ToastUtil.showMessage(ShareActivity.this, getString(R.string.loading_qr_code));
            return;
        }
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);  //图文模式
        params.putString(QQShare.SHARE_TO_QQ_TITLE, shareInfo.shareTitle);          //
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, shareInfo.shareContent);       //
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, shareInfo.shareUrl);
        if (QrCodeUtil.hasSdcard()) {
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, QrCodeUtil.QR_FULL_PATH); //图片地址
        }
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, getResources().getString(R.string.app_name));
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE);  //隐藏分享到QQ控件按钮

        mTencent.shareToQQ(ShareActivity.this, params, listener);
    }

    void share2Qzone() {
        //分享到QQ空间
        if (bitmap == null) {
            ToastUtil.showMessage(ShareActivity.this, getString(R.string.loading_qr_code));
            return;
        }
        ArrayList<String> urls = new ArrayList<>(); //图片地址，最多支持9张图片
        if (QrCodeUtil.hasSdcard()) {
            urls.add(QrCodeUtil.QR_FULL_PATH); //图片地址
        }

        final Bundle params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, shareInfo.shareTitle);//分享标题
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, shareInfo.shareContent);//分享摘要
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, shareInfo.shareUrl);//点击后跳转到的URL
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, urls);

        mTencent.shareToQzone(ShareActivity.this, params, listener);
    }

    void share2Wx() {
        //分享到聊天界面
        wxShare(SendMessageToWX.Req.WXSceneSession);
    }

    void share2WxCircle() {
        //分享到朋友圈
        wxShare(SendMessageToWX.Req.WXSceneTimeline);
    }

    /**
     * 分享到微信
     *
     * @param scene SendMessageToWX.Req.WXSceneSession分享到微信好友聊天 SendMessageToWX.Req.WXSceneTimeline分享到微信朋友圈
     */
    private void wxShare(int scene) {

        if (bitmap == null) {
            ToastUtil.showMessage(ShareActivity.this, getString(R.string.loading_qr_code));
            return;
        }
        WXWebpageObject webPage = new WXWebpageObject();
        webPage.webpageUrl = shareInfo.shareUrl;

        WXMediaMessage msg = new WXMediaMessage(webPage);
        msg.title = shareInfo.shareTitle;
        msg.description = shareInfo.shareContent;
        msg.thumbData = QrCodeUtil.bmpToByteArray(QrCodeUtil.createBitmapThumbnail(bitmap), false);

        //构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());   //该字段用于唯一标识一个请求
        req.message = msg;
        req.scene = scene; //设置分享到的位置

        // 调用api接口发送数据到微信
        iwxapi.sendReq(req);
    }


    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object o) {
            Toast.makeText(ShareActivity.this, getString(R.string.share_succeed), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(UiError uiError) {
            Log.e("UiError", uiError.errorMessage + " " + uiError.errorDetail + " " + uiError.errorCode);
            Toast.makeText(ShareActivity.this, getString(R.string.share_error), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(ShareActivity.this, getString(R.string.cancel_share), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean isEnableSwipe() {
        return true;
    }
}
