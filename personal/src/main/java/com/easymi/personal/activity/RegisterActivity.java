package com.easymi.personal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.utils.GlideCircleTransform;
import com.easymi.component.utils.PhoneUtil;
import com.easymi.personal.R;
import com.easymi.personal.widget.CusImgHint;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.List;

/**
 * Created by developerLzh on 2017/11/7 0007.
 */

public class RegisterActivity extends RxBaseActivity {

    ImageView lPhotoImg;

    CusImgHint cusImgHint;

    private boolean photoHintShowed = false;

    private String imgPath;

    @Override
    public int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        cusImgHint = findViewById(R.id.cus_hint);

        lPhotoImg = findViewById(R.id.photo_img);

        findViewById(R.id.photo_con).setOnClickListener(v -> {
            if (!photoHintShowed) {
                photoHintShowed = true;
                cusImgHint.setVisibility(View.VISIBLE);
                cusImgHint.setImageResource(R.mipmap.img_banshen);
                cusImgHint.setText(R.string.register_hint_photo);
                PhoneUtil.hideKeyboard(RegisterActivity.this);
            } else {
                choosePic(1, 1);
            }
        });

        findViewById(R.id.next_step).setOnClickListener(v -> startActivity(new Intent(RegisterActivity.this, RegisterActivity2.class)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == PictureConfig.CHOOSE_REQUEST) {
                List<LocalMedia> images = PictureSelector.obtainMultipleResult(data);
                if (images != null && images.size() > 0) {
                    imgPath = images.get(0).getCutPath();
                    RequestOptions options = new RequestOptions()
                            .centerCrop()
                            .placeholder(R.mipmap.register_photo)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .optionalTransform(new GlideCircleTransform());

                    Glide.with(RegisterActivity.this)
                            .load(imgPath)
                            .apply(options)
                            .into(lPhotoImg);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
