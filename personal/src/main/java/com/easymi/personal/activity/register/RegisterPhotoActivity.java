package com.easymi.personal.activity.register;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.easymi.common.entity.Pic;
import com.easymi.common.entity.QiNiuToken;
import com.easymi.common.entity.RegisterRes;
import com.easymi.common.register.RegisterRequest;
import com.easymi.component.Config;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.GlideRoundTransform;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.RxProgressHUD;
import com.easymi.personal.R;
import com.easymi.personal.activity.SetActivity;
import com.easymi.personal.widget.CusImgHint;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscription;


/**
 * Created by developerLzh on 2017/11/7 0007.
 */

public class RegisterPhotoActivity extends RxBaseActivity {

    CusToolbar toolbar;
    ImageView frontImg;
    ImageView backImg;
    ImageView drivingImg;
    FrameLayout frontCon;
    FrameLayout backCon;
    FrameLayout drivingCon;

    CusImgHint cusImgHint;

    Button applyBtn;

    private boolean frontHintShowed = false;
    private boolean backHintShowed = false;
    private boolean drivingHintShowed = false;

    private ImageView currentImg;

    private String[] imgPaths = new String[3];

    private RegisterRequest registerRequest;
    private RegisterRequest registerInfo;

    RequestOptions options = new RequestOptions()
            .centerCrop()
            .placeholder(R.mipmap.register_photo)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .transform(new GlideRoundTransform());

    private String qiniuToken;

    @Override
    public int getLayoutId() {
        return R.layout.activity_register_photo;
    }

    @Override
    public void initToolBar() {
        super.initToolBar();
        toolbar.setLeftIcon(R.drawable.ic_arrow_back, v -> finish());
        toolbar.setTitle(R.string.register_become);
    }

    public void findById() {
        toolbar = findViewById(R.id.toolbar);
        frontImg = findViewById(R.id.front_img);
        backImg = findViewById(R.id.back_img);
        drivingImg = findViewById(R.id.driving_img);
        frontCon = findViewById(R.id.front_con);
        backCon = findViewById(R.id.back_con);
        drivingCon = findViewById(R.id.driving_con);
        cusImgHint = findViewById(R.id.cus_hint);
        applyBtn = findViewById(R.id.apply);
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        findById();
        initLisenter();
        registerRequest = getIntent().getParcelableExtra("registerRequest");

        if (getIntent().getParcelableExtra("registerInfo") != null) {
            registerInfo = getIntent().getParcelableExtra("registerInfo");

            frontHintShowed = true;
            backHintShowed = true;
            drivingHintShowed = true;

            Glide.with(RegisterPhotoActivity.this)
                    .load(Config.IMG_SERVER + registerInfo.idCardPath + Config.IMG_PATH)
                    .apply(options)
                    .into(frontImg);

            Glide.with(RegisterPhotoActivity.this)
                    .load(Config.IMG_SERVER + registerInfo.idCardBackPath + Config.IMG_PATH)
                    .apply(options)
                    .into(backImg);

            Glide.with(RegisterPhotoActivity.this)
                    .load(Config.IMG_SERVER + registerInfo.driveLicensePath + Config.IMG_PATH)
                    .apply(options)
                    .into(drivingImg);

            getQiniuToken();
        }
    }

    public void initLisenter() {
        applyBtn.setOnClickListener(v -> {
            next();
        });

        frontCon.setOnClickListener(v -> {
            if (!frontHintShowed) {
                frontHintShowed = true;
                cusImgHint.setVisibility(View.VISIBLE);
                cusImgHint.setImageResource(R.mipmap.img_front);
                cusImgHint.setText(R.string.register_hint_id_card);
            } else {
                currentImg = frontImg;
                choicePic(4, 3);
            }
        });

        backCon.setOnClickListener(v -> {
            if (!backHintShowed) {
                backHintShowed = true;
                cusImgHint.setVisibility(View.VISIBLE);
                cusImgHint.setImageResource(R.mipmap.img_back);
                cusImgHint.setText(R.string.register_hint_id_card);
            } else {
                currentImg = backImg;
                choicePic(4, 3);
            }
        });

        drivingCon.setOnClickListener(v -> {
            if (!drivingHintShowed) {
                drivingHintShowed = true;
                cusImgHint.setVisibility(View.VISIBLE);
                cusImgHint.setImageResource(R.mipmap.img_driving);
                cusImgHint.setText(R.string.register_hint_driving);
            } else {
                currentImg = drivingImg;
                choicePic(8, 3);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == PictureConfig.CHOOSE_REQUEST) {
                List<LocalMedia> images = PictureSelector.obtainMultipleResult(data);
                if (images != null && images.size() > 0) {

                    Glide.with(RegisterPhotoActivity.this)
                            .load(images.get(0).getCutPath())
                            .apply(options)
                            .into(currentImg);
                    currentImg.setVisibility(View.VISIBLE);
                    int i = currentImg.getId();
                    if (i == R.id.front_img) {
                        imgPaths[0] = images.get(0).getCutPath();
                        if (registerInfo != null) {
                            updateImage(0,new File(imgPaths[0]));
                        }else {
                            registerRequest.idCardPath = imgPaths[0];
                        }
                    } else if (i == R.id.back_img) {
                        imgPaths[1] = images.get(0).getCutPath();
                        if (registerInfo != null) {
                            updateImage(1,new File(imgPaths[1]));
                        }else {
                            registerRequest.idCardBackPath = imgPaths[1];
                        }
                    } else if (i == R.id.driving_img) {
                        imgPaths[2] = images.get(0).getCutPath();
                        if (registerInfo != null) {
                            updateImage(2,new File(imgPaths[2]));
                        }else {
                            registerRequest.driveLicensePath = imgPaths[2];
                        }
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean isEnableSwipe() {
        return false;
    }


    /**
     * 先上传图片然后在处理，图片上传后的hash值存储在集合中.
     * 保存上传图片后的哈希值,存储值与{@link RegisterModel#uploadPics}顺序相同.
     */
    protected void uploadAllPicsAndCommit(RegisterRequest request) {
        showDialog();
        List<String> picHash = new ArrayList<>();
        Observable<Pic> pics = RegisterModel.uploadPics(request);
        Subscription d = pics.subscribe(new Observer<Pic>() {
            @Override
            public void onCompleted() {
                dismissDialog();
                Observable<RegisterRes> observable = RegisterModel.applyDriver(RegisterPhotoActivity.this, request, picHash);
                Subscription rd = observable.subscribe(new MySubscriber<>(RegisterPhotoActivity.this, false, false, registerRes -> {
                    ToastUtil.showMessage(RegisterPhotoActivity.this, "资料提交成功");
                    EmUtil.employLogout(RegisterPhotoActivity.this);
                }));
                mRxManager.add(rd);
            }

            @Override
            public void onError(Throwable e) {
                dismissDialog();
                ToastUtil.showMessage(RegisterPhotoActivity.this, "图片上传失败，请重试");
                mRxManager.clear();
            }

            @Override
            public void onNext(Pic pic) {
                picHash.add(pic.hashCode);
            }
        });
        mRxManager.add(d);
    }

    private void next() {
        //check
        if (registerInfo != null) {
//            registerRequest.portraitPath = registerInfo.portraitPath;
            registerRequest.idCardPath = registerInfo.idCardPath;
            registerRequest.idCardBackPath = registerInfo.idCardBackPath;
            registerRequest.driveLicensePath = registerInfo.driveLicensePath;
            uploadAllPicsAndUpdate(registerRequest);
        } else {
            if (registerRequest == null) {
                ToastUtil.showMessage(this, "参数异常");
                return;
            }
            if (registerRequest.idCardPath == null) {
                if (registerInfo == null) {
                    ToastUtil.showMessage(this, "未上传身份证正面");
                    return;
                }
            }
            if (registerRequest.idCardBackPath == null) {
                if (registerInfo == null) {
                    ToastUtil.showMessage(this, "未上传身份证反面");
                    return;
                }
            }
            if (registerRequest.driveLicensePath == null) {
                if (registerInfo == null) {
                    ToastUtil.showMessage(this, "未上传驾驶证");
                    return;
                }
            }

            uploadAllPicsAndCommit(registerRequest);
        }
    }

    /**
     * 更新修改信息
     */
    protected void uploadAllPicsAndUpdate(RegisterRequest request) {
        //todo 缺少更新图片操作
        Observable<RegisterRes> observable = RegisterModel.applyUpdate(RegisterPhotoActivity.this, request);
        mRxManager.add(observable.subscribe(new MySubscriber<>(this, false, false, RegisterRes -> {
            if (RegisterRes.getCode() == 1) {
                ToastUtil.showMessage(RegisterPhotoActivity.this, "资料提交成功");
                EmUtil.employLogout(RegisterPhotoActivity.this);
            }
        })));
    }

    public void getQiniuToken() {
        Observable<QiNiuToken> observable = RegisterModel.getQiniuToken();
        mRxManager.add(observable.subscribe(new MySubscriber<>(this, false, false, qiNiuToken -> {
            if (qiNiuToken.getCode() == 1) {
                qiniuToken = qiNiuToken.qiNiu;
                if (qiniuToken == null) {
                    throw new IllegalArgumentException("token无效");
                }
            }
        })));
    }

    public void updateImage(int type, File file) {
        Observable<Pic> observable = RegisterModel.putPic(file, qiniuToken);
        mRxManager.add(observable.subscribe(new MySubscriber<>(this, false, false, pic -> {
            if (type == 0){
                registerInfo.idCardPath = pic.hashCode;
            }else if (type == 1){
                registerInfo.idCardBackPath = pic.hashCode;
            }else if (type == 2){
                registerInfo.driveLicensePath = pic.hashCode;
            }
        })));
    }

    //加载pud
    private RxProgressHUD progressHUD;

    protected void showDialog() {
        if (progressHUD == null) {
            progressHUD = new RxProgressHUD.Builder(this)
                    .setTitle("")
                    .setMessage(this.getString(com.easymi.component.R.string.wait))
                    .setCancelable(true)
                    .create();
        }
        if (!progressHUD.isShowing()) {
            progressHUD.show();
        }
    }

    protected void dismissDialog() {
        if (progressHUD.isShowing()) {
            progressHUD.dismiss();
        }
    }
}
