package com.easymi.personal.activity.register;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.easymi.common.entity.CompanyList;
import com.easymi.common.register.AbsRegisterFragment;
import com.easymi.common.register.InfoFragment;
import com.easymi.common.register.PersonInfoFragment;
import com.easymi.common.register.RegisterRequest;
import com.easymi.component.Config;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.Employ;
import com.easymi.component.utils.CommonUtil;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.GlideCircleTransform;
import com.easymi.component.utils.PhoneUtil;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.utils.TimeUtil;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.TimeDialog;
import com.easymi.personal.R;
import com.easymi.personal.widget.CusImgHint;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;
//import com.yalantis.ucrop.UCrop;
//import com.zhihu.matisse.Matisse;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by developerLzh on 2017/11/7 0007.
 */

public class RegisterBaseActivity extends RxBaseActivity {

    CusToolbar toolbar;

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
    TextView tv_time_start;
    TextView tv_time_end;

    private boolean photoHintShowed = false;

    private String imgPath;  //头像地址
    private CompanyList.Company company; //选中公司

    Calendar calendar = Calendar.getInstance(Locale.CHINA);//获取日期格式器对象

    private Employ employ;

    @Override
    public int getLayoutId() {
        return R.layout.activity_register_base;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        findById();
        employ = getIntent().getParcelableExtra("employ");
        initLisenter();

//        employ = EmUtil.getEmployInfo();
//        if (employ != null) {
//            tvName.setText(employ.nickName);
//            tvPhone.setText(employ.phone);
//        }
    }

    @Override
    public void initToolBar() {
        super.initToolBar();
        toolbar.setLeftIcon(R.drawable.ic_arrow_back, v -> finish());
        toolbar.setTitle(R.string.register_become);
    }

    public void findById() {
        toolbar = findViewById(R.id.toolbar);
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
        tv_time_start = findViewById(R.id.tv_time_start);
        tv_time_end = findViewById(R.id.tv_time_end);
    }

    public void initLisenter() {
        tv_type.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterListActivity.class);
            intent.putExtra("type", 1);
            startActivity(intent);
        });

        tv_compney.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterListActivity.class);
            intent.putExtra("type", 2);
            startActivity(intent);
        });

        tv_time_start.setOnClickListener(v -> {
            //生成一个DatePickerDialog对象，并显示。显示的DatePickerDialog控件可以选择年月日，并设置
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                //修改日历控件的年，月，日
                //这里的year,monthOfYear,dayOfMonth的值与DatePickerDialog控件设置的最新值一致
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                tv_time_start.setText(TimeUtil.getTime(TimeUtil.YMD_4_CN, calendar.getTimeInMillis()));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });
        tv_time_end.setOnClickListener(v -> {
            //生成一个DatePickerDialog对象，并显示。显示的DatePickerDialog控件可以选择年月日，并设置
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                //修改日历控件的年，月，日
                //这里的year,monthOfYear,dayOfMonth的值与DatePickerDialog控件设置的最新值一致
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                tv_time_end.setText(TimeUtil.getTime(TimeUtil.YMD_4_CN, calendar.getTimeInMillis()));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });
        findViewById(R.id.photo_con).setOnClickListener(v -> {
            if (!photoHintShowed) {
                photoHintShowed = true;
                cusImgHint.setVisibility(View.VISIBLE);
                cusImgHint.setImageResource(R.mipmap.img_banshen);
                cusImgHint.setText(R.string.register_hint_photo);
                PhoneUtil.hideKeyboard(RegisterBaseActivity.this);
            } else {
                choicePic(1, 1);
            }
        });

        findViewById(R.id.next_step).setOnClickListener(v -> {
            if (!check()){
                next();
            }
            startActivity(new Intent(this, RegisterPhotoActivity.class));
        });
    }

    private boolean check() {
        if (TextUtils.isEmpty(imgPath)) {
            ToastUtil.showMessage(this, "未上传头像");
            return true;
        }
        if (TextUtils.isEmpty(et_name.getText().toString())) {
            ToastUtil.showMessage(this, "身份证号码错误");
            return true;
        }
        if (!StringUtils.isNotBlank(et_driver_phone.getText().toString()) && !CommonUtil.isMobileNO(et_driver_phone.getText().toString())) {
            ToastUtil.showMessage(this, getString(R.string.register_cheack_phone));
            return true;
        }
//        if (!StringUtils.isNotBlank(et_driver_phone.getText().toString())){
//            ToastUtil.showMessage(this, getString(R.string.login_pl_phone));
//            return true;
//        }
        if (et_idcard.getText().toString().length() != 18) {
            ToastUtil.showMessage(this, "身份证号码错误");
            return true;
        }
        if (et_idcard.getText().toString().length() != 18) {
            ToastUtil.showMessage(this, "身份证号码错误");
            return true;
        }
        if (et_contact.getText().length() <= 0) {
            ToastUtil.showMessage(this, "未填写紧急联系人");
            return true;
        }
        if (et_contact_phone.getText().length() <= 0) {
            ToastUtil.showMessage(this, "未填写紧急联系人电话");
            return true;
        }
        if (company == null) {
            ToastUtil.showMessage(this, "请选择所属分公司");
            return true;
        }
        if (company == null) {
            ToastUtil.showMessage(this, "请选择业务类型");
            return true;
        }
        if (company == null) {
            ToastUtil.showMessage(this, "请选择驾驶证有效开始时间");
            return true;
        }
        if (company == null) {
            ToastUtil.showMessage(this, "请选择驾驶证有效截止时间");
            return true;
        }
        return false;
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

                    Glide.with(RegisterBaseActivity.this)
                            .load(imgPath)
                            .apply(options)
                            .into(lPhotoImg);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void next() {
//        //个人信息参数
//        RegisterRequest registerRequest = new RegisterRequest();
//        registerRequest.driverId = employ.id;
//        registerRequest.idCard = idCard.getText().toString();
//        registerRequest.emergency = emergency.getText().toString();
//        registerRequest.emergencyPhone = emergencyPhone.getText().toString();
//        registerRequest.companyId = selectedCompany.id;
//        registerRequest.introducer = introducer.getText().toString();
//        registerRequest.portraitPath = headPath;
//        if (daijiaRb.isChecked()) {
//            registerRequest.serviceType = "daijia";
//        } else {
//            registerRequest.serviceType = Config.ZHUANCHE;
//        }

    }

    @Override
    public boolean isEnableSwipe() {
        return false;
    }
}
