package com.easymi.personal.activity.register;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.utils.GlideRoundTransform;
import com.easymi.personal.R;
import com.easymi.personal.widget.CusImgHint;
//import com.luck.picture.lib.PictureSelector;
//import com.luck.picture.lib.config.PictureConfig;
//import com.luck.picture.lib.entity.LocalMedia;

import java.util.List;

/**
 * Created by developerLzh on 2017/11/7 0007.
 */

public class RegisterPhotoActivity extends RxBaseActivity {

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

    @Override
    public int getLayoutId() {
        return R.layout.activity_register_photo;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        frontImg = findViewById(R.id.front_img);
        backImg = findViewById(R.id.back_img);
        drivingImg = findViewById(R.id.driving_img);

        frontCon = findViewById(R.id.front_con);
        backCon = findViewById(R.id.back_con);
        drivingCon = findViewById(R.id.driving_con);

        cusImgHint = findViewById(R.id.cus_hint);

        applyBtn = findViewById(R.id.apply);

        applyBtn.setOnClickListener(v -> finish());

        frontCon.setOnClickListener(v -> {
            if (!frontHintShowed) {
                frontHintShowed = true;
                cusImgHint.setVisibility(View.VISIBLE);
                cusImgHint.setImageResource(R.mipmap.img_front);
                cusImgHint.setText(R.string.register_hint_id_card);
            } else {
                currentImg = frontImg;
                choosePic(4, 3);
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
                choosePic(4, 3);
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
                choosePic(8, 3);
            }
        });
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == RESULT_OK) {
//            if (requestCode == PictureConfig.CHOOSE_REQUEST) {
//                List<LocalMedia> images = PictureSelector.obtainMultipleResult(data);
//                if (images != null && images.size() > 0) {
//
//                    RequestOptions options = new RequestOptions()
//                            .centerCrop()
//                            .placeholder(R.mipmap.register_photo)
//                            .diskCacheStrategy(DiskCacheStrategy.ALL)
//                            .transform(new GlideRoundTransform());
//
//                    Glide.with(RegisterPhotoActivity.this)
//                            .load(images.get(0).getCutPath())
//                            .apply(options)
//                            .into(currentImg);
//                    currentImg.setVisibility(View.VISIBLE);
//                    int i = currentImg.getId();
//                    if (i == R.id.front_img) {
//                        imgPaths[0] = images.get(0).getCutPath();
//                    } else if (i == R.id.back_img) {
//                        imgPaths[1] = images.get(0).getCutPath();
//                    } else if (i == R.id.driving_img) {
//                        imgPaths[2] = images.get(0).getCutPath();
//                    }
//                }
//            }
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }

    @Override
    public boolean isEnableSwipe() {
        return false;
    }
}
