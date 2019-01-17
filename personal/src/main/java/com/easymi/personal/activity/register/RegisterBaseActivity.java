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
import com.easymi.common.entity.Pic;
import com.easymi.common.entity.QiNiuToken;
import com.easymi.common.register.RegisterRequest;
import com.easymi.component.Config;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.Employ;
import com.easymi.component.network.GsonUtil;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.utils.CommonUtil;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.GlideCircleTransform;
import com.easymi.component.utils.Log;
import com.easymi.component.utils.PhoneUtil;
import com.easymi.component.utils.RsaUtils;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.utils.TimeUtil;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.TimeDialog;
import com.easymi.personal.R;
import com.easymi.personal.entity.BusinessType;
import com.easymi.personal.result.RegisterResult;
import com.easymi.personal.widget.CusImgHint;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;
//import com.yalantis.ucrop.UCrop;
//import com.zhihu.matisse.Matisse;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import rx.Observable;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: FinishActivity
 * Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class RegisterBaseActivity extends RxBaseActivity {

    CusToolbar toolbar;

    ImageView lPhotoImg;
    CusImgHint cusImgHint;
    EditText et_name;
    TextView et_driver_phone;
    EditText et_idcard;
    EditText et_contact;
    EditText et_contact_phone;
    EditText et_work_number;
    TextView tv_type;
    TextView tv_compney;
    TextView tv_time_start;
    TextView tv_time_end;

    RequestOptions options = new RequestOptions()
            .centerCrop()
            .placeholder(R.mipmap.register_photo)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .optionalTransform(new GlideCircleTransform());

    private boolean photoHintShowed = false;

    private String imgPath;  //头像地址
    private BusinessType selecType; //选中业务
    private CompanyList.Company company; //选中公司
    private long startTime;
    private long endTime;

    Calendar calendar = Calendar.getInstance(Locale.CHINA);//获取日期格式器对象

    private Employ employ;

    /**
     * 之前提交过的资料
     */
    private RegisterRequest registerInfo;

    @Override
    public int getLayoutId() {
        return R.layout.activity_register_base;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        findById();
        employ = getIntent().getParcelableExtra("employ");
        initLisenter();

        et_driver_phone.setText(employ.phone);

        if (employ.registerStatus != 1) {
            getDriverInfo();
        }
    }

    /**
     * 加载获取到的之前提交的资料
     * @param registerRequest
     */
    public void initData(RegisterRequest registerRequest) {
        registerInfo = registerRequest;

        photoHintShowed = true;

        Glide.with(RegisterBaseActivity.this)
                .load(Config.IMG_SERVER + registerInfo.portraitPath + Config.IMG_PATH)
                .apply(options)
                .into(lPhotoImg);

        et_name.setText(registerInfo.driverName);
        et_driver_phone.setText(registerInfo.driverPhone);
        et_idcard.setText(registerInfo.idCard);
        et_contact.setText(registerInfo.emergency);
        et_contact_phone.setText(registerInfo.emergencyPhone);

        tv_type.setText(setWorkType(registerInfo.serviceType));

        tv_compney.setText(registerInfo.companyName);

        tv_time_start.setText(TimeUtil.getTime(TimeUtil.YMD_4_CN, registerInfo.startTime));
        tv_time_end.setText(TimeUtil.getTime(TimeUtil.YMD_4_CN, registerInfo.endTime));
        et_work_number.setText(registerInfo.introducer);
    }

    /**
     * 获取对应业务名称
     * @param serviceType
     * @return
     */
    public String setWorkType(String serviceType) {
        String serviceName = null;
        if (TextUtils.equals(serviceType, Config.ZHUANCHE)) {
            serviceName = getResources().getString(R.string.create_zhuanche);
        } else if (TextUtils.equals(serviceType, Config.TAXI)) {
            serviceName = getResources().getString(R.string.create_taxi);
        } else if (TextUtils.equals(serviceType, Config.CITY_LINE)) {
            serviceName = getResources().getString(R.string.create_zhuanxian);
        }else if (TextUtils.equals(serviceType, Config.CHARTERED)) {
            serviceName = getResources().getString(R.string.create_chartered);
        } else if (TextUtils.equals(serviceType, Config.RENTAL)) {
            serviceName = getResources().getString(R.string.create_rental);
        }else if (TextUtils.equals(serviceType, Config.COUNTRY)) {
            serviceName = getResources().getString(R.string.create_bus_country);
        }
        return serviceName;
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
        /**
         * 业务类型选择
         */
        tv_type.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterListActivity.class);
            intent.putExtra("type", 1);
            startActivityForResult(intent, 0x00);
        });
        /**
         * 服务企业选择
         */
        tv_compney.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterListActivity.class);
            intent.putExtra("type", 2);
            startActivityForResult(intent, 0x00);
        });

        /**
         * 驾照有效起时间选择
         */
        tv_time_start.setOnClickListener(v -> {
            //生成一个DatePickerDialog对象，并显示。显示的DatePickerDialog控件可以选择年月日，并设置
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                //修改日历控件的年，月，日
                //这里的year,monthOfYear,dayOfMonth的值与DatePickerDialog控件设置的最新值一致
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                startTime = calendar.getTimeInMillis() / 1000;

                tv_time_start.setText(TimeUtil.getTime(TimeUtil.YMD_4_CN, calendar.getTimeInMillis()));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });
        /**
         * 驾照有效截止时间选择
         */
        tv_time_end.setOnClickListener(v -> {
            //生成一个DatePickerDialog对象，并显示。显示的DatePickerDialog控件可以选择年月日，并设置
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                //修改日历控件的年，月，日
                //这里的year,monthOfYear,dayOfMonth的值与DatePickerDialog控件设置的最新值一致
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                endTime = calendar.getTimeInMillis() / 1000;

                tv_time_end.setText(TimeUtil.getTime(TimeUtil.YMD_4_CN, calendar.getTimeInMillis()));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        /**
         * 头像点击选择
         */
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

        /**
         * 下一步
         */
        findViewById(R.id.next_step).setOnClickListener(v -> {
            if (!check()) {
                next();
            }
        });
    }

    /**
     * 参数检查
     * @return
     */
    private boolean check() {
        if (TextUtils.isEmpty(imgPath)) {
            if (registerInfo == null) {
                ToastUtil.showMessage(this, "未上传头像");
                return true;
            }
        }
        if (TextUtils.isEmpty(et_name.getText().toString())) {
            ToastUtil.showMessage(this, "请输入司机姓名");
            return true;
        }
        if (!StringUtils.isNotBlank(et_driver_phone.getText().toString()) && !CommonUtil.isMobileNO(et_driver_phone.getText().toString())) {
            ToastUtil.showMessage(this, getString(R.string.register_cheack_phone));
            return true;
        }
        if (et_idcard.getText().toString().length() != 18 && et_idcard.getText().toString().length() != 15) {
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
            if (registerInfo == null) {
                ToastUtil.showMessage(this, "请选择所属分公司");
                return true;
            }
        }
        if (selecType == null) {
            if (registerInfo == null) {
                ToastUtil.showMessage(this, "请选择业务类型");
                return true;
            }
        }
        if (TextUtils.isEmpty(tv_time_start.getText().toString())) {
            ToastUtil.showMessage(this, "请选择驾驶证有效开始时间");
            return true;
        }
        if (TextUtils.isEmpty(tv_time_end.getText().toString())) {
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

                    Glide.with(RegisterBaseActivity.this)
                            .load(imgPath)
                            .apply(options)
                            .into(lPhotoImg);

                    if (registerInfo != null) {
                        getQiniuToken(imgPath);
                    }
                }
            }
            if (requestCode == 0x00) {
                if (data.getSerializableExtra("selectType") != null) {
                    selecType = (BusinessType) data.getSerializableExtra("selectType");
                    tv_type.setText(selecType.name);
                }
                if (data.getSerializableExtra("selectComPany") != null) {
                    company = (CompanyList.Company) data.getSerializableExtra("selectComPany");
                    tv_compney.setText(company.companyName);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 参数赋值
     */
    private void next() {
        //个人信息参数
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.driverId = employ.id;
        registerRequest.driverName = et_name.getText().toString().trim();
        registerRequest.driverPhone = et_driver_phone.getText().toString().trim();
        registerRequest.idCard = et_idcard.getText().toString().trim();
        registerRequest.emergency = et_contact.getText().toString().trim();
        registerRequest.emergencyPhone = et_contact_phone.getText().toString().trim();
        if (company == null) {
            registerRequest.companyId = registerInfo.companyId;
        } else {
            registerRequest.companyId = company.id;
        }
        if (selecType == null) {
            registerRequest.serviceType = registerInfo.serviceType;
        } else {
            registerRequest.serviceType = selecType.type;
        }
        if (startTime == 0) {
            registerRequest.startTime = registerInfo.startTime;
        } else {
            registerRequest.startTime = startTime;
        }
        if (endTime == 0) {
            registerRequest.endTime = registerInfo.endTime;
        } else {
            registerRequest.endTime = endTime;
        }
        if (TextUtils.isEmpty(imgPath)) {
                registerRequest.portraitPath = registerInfo.portraitPath;
        } else {
            if (registerInfo != null) {
                registerRequest.portraitPath = registerInfo.portraitPath;
            }else {
                registerRequest.portraitPath = imgPath;
            }
        }

        registerRequest.introducer = et_work_number.getText().toString().trim();

        Intent intent = new Intent(this, RegisterPhotoActivity.class);
        intent.putExtra("registerRequest", registerRequest);
        intent.putExtra("registerInfo", registerInfo);
        startActivity(intent);
    }

    public void getQiniuToken(String imagPath) {
        Observable<QiNiuToken> observable = RegisterModel.getQiniuToken();
        mRxManager.add(observable.subscribe(new MySubscriber<>(this, false, false, qiNiuToken -> {
            if (qiNiuToken.getCode() == 1) {
                if (qiNiuToken.qiNiu == null) {
                    throw new IllegalArgumentException("token无效");
                }
                updateImage(new File(imagPath), qiNiuToken.qiNiu);
            }
        })));
    }

    public void updateImage(File file, String token) {
        Observable<Pic> observable = RegisterModel.putPic(file, token);
        mRxManager.add(observable.subscribe(new MySubscriber<>(this, false, false, pic -> {
            registerInfo.portraitPath = pic.hashCode;
        })));
    }

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    /**
     * 获取司机提交过的注册资料
     */
    public void getDriverInfo() {
        String id_rsa = RsaUtils.encryptAndEncode(this, employ.id + "");
        Observable<RegisterResult> observable = RegisterModel.getDriverInfo(id_rsa);
        mRxManager.add(observable.subscribe(new MySubscriber<>(this, false, false, emResult -> {
            if (emResult.getCode() == 1) {
                initData(emResult.data);
            }
        })));
    }
}
