package com.easymi.personal.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.easymi.component.Config;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.HttpResultFunc2;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.utils.AlbumNotifyHelper;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.QrUtils;
import com.easymi.component.utils.ToastUtil;
import com.easymi.personal.McService;
import com.easymi.personal.R;
import com.easymi.personal.entity.MyPopularizeUrlBean;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MyPopularizeCodeActivity extends RxBaseActivity implements View.OnClickListener {

    private com.easymi.component.widget.CusToolbar myPopularizeCodeCtb;
    private android.widget.ImageView myPopularizeCodeIv;
    private android.widget.TextView myPopularizeCodeTvWechat;
    private android.widget.TextView myPopularizeCodeTvWechatCircle;
    private android.widget.TextView myPopularizeCodeTvDownload;
    private MyPopularizeUrlBean myPopularizeUrlBean;
    private ImageView myPopularizeCodeIvIcon;
    private FrameLayout myPopularizeCodeFl;

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_my_popularize_code;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        myPopularizeCodeIv = findViewById(R.id.myPopularizeCodeIv);
        myPopularizeCodeTvWechat = findViewById(R.id.myPopularizeCodeTvWechat);
        myPopularizeCodeTvWechat.setOnClickListener(this);
        myPopularizeCodeTvWechatCircle = findViewById(R.id.myPopularizeCodeTvWechatCircle);
        myPopularizeCodeTvWechatCircle.setOnClickListener(this);
        myPopularizeCodeTvDownload = findViewById(R.id.myPopularizeCodeTvDownload);
        myPopularizeCodeTvDownload.setOnClickListener(this);
        myPopularizeCodeIvIcon = findViewById(R.id.myPopularizeCodeIvIcon);
        myPopularizeCodeFl = findViewById(R.id.myPopularizeCodeFl);
        loadUrl();
    }

    private void loadUrl() {
        ApiManager.getInstance().createApi(Config.HOST, McService.class)
                .getPromoteUrl(EmUtil.getEmployInfo().phone)
                .map(new HttpResultFunc2<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MySubscriber<MyPopularizeUrlBean>(this, true, false, new HaveErrSubscriberListener<MyPopularizeUrlBean>() {
                    @Override
                    public void onNext(MyPopularizeUrlBean myPopularizeUrlBean) {
                        MyPopularizeCodeActivity.this.myPopularizeUrlBean = myPopularizeUrlBean;
                        Bitmap bitmap = QrUtils.createQRCodeBitmap(myPopularizeUrlBean.shareUrl,
                                512,
                                512,
                                4);
                        myPopularizeCodeIv.setImageBitmap(bitmap);
                        myPopularizeCodeIvIcon.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(int code) {
                        ToastUtil.showMessage(MyPopularizeCodeActivity.this, "数据加载失败,请重试");
                        finish();
                    }
                }));
    }

    @Override
    public void initToolBar() {
        super.initToolBar();
        myPopularizeCodeCtb = findViewById(R.id.myPopularizeCodeCtb);
        myPopularizeCodeCtb.setTitle("我的推广码").setLeftIcon(R.drawable.ic_arrow_back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void shareToWx(boolean isCircle) {
        IWXAPI iwxapi = WXAPIFactory.createWXAPI(this, Config.WX_APP_ID, true);
        WXWebpageObject webPage = new WXWebpageObject();
        webPage.webpageUrl = myPopularizeUrlBean.shareUrl;

        WXMediaMessage msg = new WXMediaMessage(webPage);
        msg.title = myPopularizeUrlBean.shareTitle;
        msg.description = myPopularizeUrlBean.shareContent;

        msg.setThumbImage(BitmapFactory.decodeResource(getResources(), R.drawable.share_img_bg));
        //构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());   //该字段用于唯一标识一个请求
        req.message = msg;
        req.scene = isCircle ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession; //设置分享到的位置

        // 调用api接口发送数据到微信
        iwxapi.sendReq(req);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.myPopularizeCodeTvWechat) {
            shareToWx(false);
        } else if (v.getId() == R.id.myPopularizeCodeTvWechatCircle) {
            shareToWx(true);
        } else if (v.getId() == R.id.myPopularizeCodeTvDownload) {
            if (myPopularizeUrlBean != null) {
                myPopularizeCodeFl.buildDrawingCache();
                Bitmap bitmap = myPopularizeCodeFl.getDrawingCache();
                AlbumNotifyHelper.SavePictureFile(this, AlbumNotifyHelper.bitmapToFile(bitmap));
                ToastUtil.showMessage(this, "保存二维码成功,请进入相册查看");
            }
        }
    }
}
