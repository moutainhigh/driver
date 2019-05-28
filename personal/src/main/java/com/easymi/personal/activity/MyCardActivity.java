package com.easymi.personal.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.easymi.component.utils.GlideCircleTransform;
import com.easymi.component.utils.Log;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.CusToolbar;
import com.easymi.personal.McService;
import com.easymi.personal.R;
import com.easymi.personal.entity.ShareInfo;
import com.easymi.personal.result.CardHostResult;
import com.easymi.personal.result.LoginResult;
import com.easymi.personal.result.ShareResult;
import com.easymi.personal.util.QrCodeUtil;
import com.easymi.personal.widget.SaveCardDialog;
import com.google.zxing.WriterException;

import java.io.File;
import java.io.FileOutputStream;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: MyCardActivity
 * @Author: hufeng
 * @Date: 2019/5/20 下午4:41
 * @Description:
 * @History:
 */
public class MyCardActivity extends RxBaseActivity {

    /**
     * 自定义标题栏
     */
    CusToolbar cusToolbar;

    /**
     * 司机头像
     */
    ImageView iv_avater;

    /**
     * 司机名字
     */
    TextView tv_name;

    /**
     * 二维码展示
     */
    ImageView iv_qrcode;

    /**
     * 生成的二维码
     */
    private Bitmap bitmap;

    /**
     * 整个二维码名片
     */
    LinearLayout lin_card;

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_my_card;
    }

    @Override
    public void initToolBar() {
        super.initToolBar();
        cusToolbar.setLeftBack(view -> finish());
        cusToolbar.setTitle(R.string.person_card);
        cusToolbar.setRightIcon(R.mipmap.ic_my_car_save, view -> {
            SaveCardDialog dialog = new SaveCardDialog(this);
            dialog.setOnMyClickListener(view1 -> {
                dialog.dismiss();
                if (view1.getId() == R.id.tv_save_card) {
                    //todo
                    cutCard();
                }
            });
            dialog.show();
        });
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        cusToolbar = findViewById(R.id.cus_toolbar);
        iv_avater = findViewById(R.id.iv_avater);
        tv_name = findViewById(R.id.tv_name);
        iv_qrcode = findViewById(R.id.iv_qrcode);
        lin_card = findViewById(R.id.lin_card);

        Employ employ = EmUtil.getEmployInfo();

        tv_name.setText(employ.realName);

        if (StringUtils.isNotBlank(employ.portraitPath)) {
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .transform(new GlideCircleTransform())
                    .placeholder(R.mipmap.photo_default)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(this)
                    .load(Config.IMG_SERVER + employ.portraitPath + Config.IMG_PATH)
                    .apply(options)
                    .into(iv_avater);
        }

        queryQrImg();
    }

    /**
     * 查询二维码地址
     */
    public void queryQrImg() {

        McService api = ApiManager.getInstance().createApi(Config.HOST, McService.class);

        Observable<LoginResult> observable = api
                .queryCardHost(EmUtil.getEmployId() + "")
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, false,
                false, result -> {

            String qrcode = result.data.qrCodeUrl + "/?app_key=" + Config.APP_KEY + "#/home?driverId=" + EmUtil.getEmployId()+"&serviceType="+result.data.serviceType;
            initQrImg(qrcode);
        })));
    }


    /**
     * 加载二维码图片  https://m.xiaokakj.com/?app_key=对应的系统 appkey#/home?driverId=司机 id
     */
    private void initQrImg(String qrcode) {

        new Thread(() -> {
            int radius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 216, getResources().getDisplayMetrics());
            try {
                bitmap = QrCodeUtil.createCode(qrcode,  BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher),30);
            } catch (WriterException e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> {
                QrCodeUtil.saveBitmap(MyCardActivity.this, QrCodeUtil.QR_NAME, bitmap);
                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL);
                Glide.with(MyCardActivity.this)
                        .load(QrCodeUtil.QR_FULL_PATH)
                        .apply(options)
                        .into(iv_qrcode);
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


    public void cutCard() {

        lin_card.setDrawingCacheEnabled(true);
        lin_card.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(lin_card.getDrawingCache());

        if (bitmap != null) {
            try {
                // 获取内置SD卡路径
                String sdCardPath = Environment.getExternalStorageDirectory().getPath();
                // 图片文件路径
                String filePath = sdCardPath + File.separator;

                File appDir = new File(filePath);

                if (!appDir.exists()) {
                    appDir.mkdir();
                }
                String fileName = System.currentTimeMillis() + ".jpg";

                File file = new File(appDir, fileName);

                FileOutputStream os = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                os.flush();
                os.close();

                //保存图片后发送广播通知更新数据库
                Uri uri = Uri.fromFile(file);
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));

                Log.e("hufeng", "存储完成");
                ToastUtil.showMessage(this, "保存完成");
            } catch (Exception e) {
            }
        }
    }
}
