package com.easymi.personal.activity.register;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.utils.GlideCircleTransform;
import com.easymi.component.utils.PhoneUtil;
import com.easymi.personal.R;
import com.easymi.personal.widget.CusImgHint;
//import com.luck.picture.lib.PictureSelector;
//import com.luck.picture.lib.config.PictureConfig;
//import com.luck.picture.lib.entity.LocalMedia;

import java.util.List;

/**
 * Created by developerLzh on 2017/11/7 0007.
 */

public class RegisterBaseActivity extends RxBaseActivity {

    ImageView lPhotoImg;
    CusImgHint cusImgHint;
    EditText et_name;
    EditText et_driver_phone;
    EditText et_idcard;
    EditText et_contact;
    EditText et_contact_phone;
    EditText et_work_number;
    TextView tv_type;
    TextView tv_compney;
    TextView tv_time;

    private boolean photoHintShowed = false;

    private String imgPath;

    @Override
    public int getLayoutId() {
        return R.layout.activity_register_base;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        findById();

        findViewById(R.id.photo_con).setOnClickListener(v -> {
            if (!photoHintShowed) {
                photoHintShowed = true;
                cusImgHint.setVisibility(View.VISIBLE);
                cusImgHint.setImageResource(R.mipmap.img_banshen);
                cusImgHint.setText(R.string.register_hint_photo);
                PhoneUtil.hideKeyboard(RegisterBaseActivity.this);
            } else {
                choosePic(1, 1);
            }
        });

        findViewById(R.id.next_step).setOnClickListener(v -> startActivity(new Intent(this, RegisterPhotoActivity.class)));
    }

    public void findById() {
        cusImgHint = findViewById(R.id.cus_hint);
        lPhotoImg = findViewById(R.id.photo_img);

        et_name = findViewById(R.id.et_name);
        et_driver_phone = findViewById(R.id.et_driver_phone);
        et_idcard = findViewById(R.id.et_idcard);
        et_contact = findViewById(R.id.et_contact);
        et_contact_phone = findViewById(R.id.et_contact_phone);
        et_work_number = findViewById(R.id.et_work_number);
        tv_type = findViewById(R.id.tv_type);
        tv_compney = findViewById(R.id.tv_compney);
        tv_time = findViewById(R.id.tv_time);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == RESULT_OK) {
//            if (requestCode == PictureConfig.CHOOSE_REQUEST) {
//                List<LocalMedia> images = PictureSelector.obtainMultipleResult(data);
//                if (images != null && images.size() > 0) {
//                    imgPath = images.get(0).getCutPath();
//                    RequestOptions options = new RequestOptions()
//                            .centerCrop()
//                            .placeholder(R.mipmap.register_photo)
//                            .diskCacheStrategy(DiskCacheStrategy.ALL)
//                            .optionalTransform(new GlideCircleTransform());
//
//                    Glide.with(RegisterBaseActivity.this)
//                            .load(imgPath)
//                            .apply(options)
//                            .into(lPhotoImg);
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
