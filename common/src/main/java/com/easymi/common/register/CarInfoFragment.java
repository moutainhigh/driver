package com.easymi.common.register;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.easymi.common.R;
import com.easymi.common.widget.ImvDialog;
import com.easymi.component.utils.ToastUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 * @Author: hufeng
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */
public class CarInfoFragment extends AbsRegisterFragment {

    private static final int REQUEST_BRAND = 1;
    private static final int REQUEST_VEHICLE = 2;
    private static final int REQUEST_CAR = 3;
    private static final int REQUEST_LICENSE = 4;
    private static final int REQUEST_TRANS = 5;

    private RequestOptions options = new RequestOptions().centerCrop().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
    private RegisterRequest registerRequest;

    public static CarInfoFragment newInstance(RegisterRequest registerRequest) {
        CarInfoFragment fragment = new CarInfoFragment();
        Bundle arguments = new Bundle();
        arguments.putParcelable("registerRequest", registerRequest);
        fragment.setArguments(arguments);
        return fragment;
    }

    private OnSelectPicListener onSelectPicListener = new OnSelectPicListener() {
        @Override
        public void onSelectPicResult(int requestCode, Uri picUri) {
            if (registerRequest == null) {
                return;
            }
            ImageView imv;
            switch (requestCode) {
                case REQUEST_CAR:
                    imv = imvCar;
                    registerRequest.carPhoto = picUri.getPath();
                    break;
                case REQUEST_LICENSE:
                    imv = imvLicense;
                    registerRequest.drivingLicensePhoto = picUri.getPath();
                    break;
                case REQUEST_TRANS:
                    imv = imvTrans;
                    registerRequest.transPhoto = picUri.getPath();
                    break;
                default:
                    return;
            }
            Glide.with(CarInfoFragment.this)
                    .load(picUri)
                    .apply(options)
                    .into(imv);
        }
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.com_fragment_car_info, container, false);
        Bundle arguments = getArguments();
        if (arguments != null) {
            registerRequest = arguments.getParcelable("registerRequest");
        }
        initView(view);
        return view;
    }

    private long brandId = -1;

    private ImageView imvLicense, imvCar, imvTrans;
    private TextView tvBrand, tvVehicle, tvColor, tvCarColor, tvCarType,
            tvCarUse, tvOil, tvBuy, tvRegister, tvCheck;
    private EditText etCarNo, seatNum, mk, etVin;

    private void initView(View view) {
        imvLicense = view.findViewById(R.id.car_license);
        tvBrand = view.findViewById(R.id.tvBrand);
        tvVehicle = view.findViewById(R.id.tvVehicle);
        tvColor = view.findViewById(R.id.tvColor);
        tvCarColor = view.findViewById(R.id.tvCarColor);
        tvCarType = view.findViewById(R.id.tvCarType);
        tvCarUse = view.findViewById(R.id.tvCarUse);
        tvOil = view.findViewById(R.id.tvOil);
        imvCar = view.findViewById(R.id.imvCar);
        imvLicense = view.findViewById(R.id.car_license);
        imvTrans = view.findViewById(R.id.imvTrans);
        etCarNo = view.findViewById(R.id.etCarNo);
        seatNum = view.findViewById(R.id.seatNum);
        mk = view.findViewById(R.id.mk);
        etVin = view.findViewById(R.id.etVin);
        tvBuy = view.findViewById(R.id.tvBuy);
        tvRegister = view.findViewById(R.id.tvRegister);
        tvCheck = view.findViewById(R.id.tvCheck);

        TextView titleEtCarNo = view.findViewById(R.id.titleEtCarNo);
        TextView titleBrand = view.findViewById(R.id.titleBrand);
        TextView titleVehicle = view.findViewById(R.id.titleVehicle);
        TextView TitleColor = view.findViewById(R.id.TitleColor);
        TextView titleCarColor = view.findViewById(R.id.titleCarColor);
        TextView titleCarType = view.findViewById(R.id.titleCarType);
        TextView titleCarUse = view.findViewById(R.id.titleCarUse);
        TextView titleOil = view.findViewById(R.id.titleOil);
        TextView titleNum = view.findViewById(R.id.titleNum);
        TextView titleMK = view.findViewById(R.id.titleMK);
        TextView titleVIN = view.findViewById(R.id.titleVIN);
        TextView titleBuy = view.findViewById(R.id.titleBuy);
        TextView titleRegister = view.findViewById(R.id.titleRegister);
        TextView titleCheck = view.findViewById(R.id.titleCheck);
        InfoActivity.setTVs(titleEtCarNo, titleBrand, titleVehicle, TitleColor, titleCarColor, titleCarType,
                titleCarUse, titleOil, titleNum, titleMK, titleVIN, titleBuy, titleRegister, titleCheck);

        tvBrand.setOnClickListener(v ->
                startActivityForResult(new Intent(getContext(), BrandActivity.class), REQUEST_BRAND));
        tvVehicle.setOnClickListener(v -> {
            if (brandId == -1) {
                ToastUtil.showMessage(getActivity(), "未选择车厂牌");
                return;
            }
            Intent intent = new Intent(getContext(), VehicleActivity.class);
            intent.putExtra("brandId", brandId);
            startActivityForResult(intent, REQUEST_VEHICLE);
        });

        tvColor.setOnClickListener(v ->
                showStringDialog(tvColor, "黄色", "蓝色", "绿色"));
        tvCarColor.setOnClickListener(v ->
                showStringDialog(tvCarColor, "白色", "黑色", "红色", "蓝色", "黄色", "棕色", "橙色", "绿色", "紫色", "银色", "灰色"));
        tvCarType.setOnClickListener(v ->
                showStringDialog(tvCarType, "小型轿车", "中型轿车", "高级轿车"));
        tvCarUse.setOnClickListener(v ->
                showStringDialog(tvCarUse, "营运公路客运", "营运城市公交", "营运出租租赁", "非营运"));
        tvOil.setOnClickListener(v ->
                showStringDialog(tvOil, "汽油", "柴油", "电", "混合"));

        imvLicense.setOnClickListener(v -> choicePic(REQUEST_LICENSE, 2.5f, 1));
        imvCar.setOnClickListener(v -> choicePic(REQUEST_CAR, 1.6f, 1));
        imvTrans.setOnClickListener(v -> choicePic(REQUEST_TRANS, 1.5f, 1));

        view.findViewById(R.id.next).setOnClickListener(v -> next());

        tvBuy.setOnClickListener(v -> setDate(tvBuy));
        tvRegister.setOnClickListener(v -> setDate(tvRegister));
        tvCheck.setOnClickListener(v -> setDate(tvCheck));

        if (getFragmentManager() != null) {
            view.findViewById(R.id.com_car_license).setOnClickListener(v -> ImvDialog.newInstance(R.mipmap.com_driving).show(getFragmentManager(), ""));
            view.findViewById(R.id.com_transportation_license).setOnClickListener(v -> ImvDialog.newInstance(R.mipmap.com_trans).show(getFragmentManager(), ""));
        }

    }

    private void showStringDialog(TextView textView, String... str) {
        if (getContext() != null && textView != null && str != null && str.length > 0) {
            List<String> stringList = new ArrayList<>(Arrays.asList(str));
            new StringPicker(getContext(), stringList).setOnSelectListener(textView::setText).show();
        }
    }

    private void setDate(TextView tv) {
        if (getContext() == null) {
            return;
        }
        Calendar ca = Calendar.getInstance();
        int year = ca.get(Calendar.YEAR);
        int month = ca.get(Calendar.MONTH);
        int day = ca.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(getContext(), (view, year1, month1, dayOfMonth) ->
                tv.setText(String.format(Locale.CHINESE, "%4d-%02d-%02d", year1, month1 + 1, dayOfMonth)), year, month, day).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null || resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_BRAND:
                String brandName = data.getStringExtra("brandName");
                brandId = data.getLongExtra("brandId", -1);
                tvBrand.setText(brandName);
                break;
            case REQUEST_VEHICLE:
                String vehicleName = data.getStringExtra("vehicleName");
                tvVehicle.setText(vehicleName);
                break;
            default:
                handlePic(requestCode, resultCode, data, onSelectPicListener);
                break;
        }
    }

    /**
     * 提交注册。
     */
    private void next() {
        //check
        if (registerRequest == null) {
            ToastUtil.showMessage(getContext(), "参数错误");
            return;
        }
        if (registerRequest.carPhoto == null) {
            ToastUtil.showMessage(getContext(), "未上传车辆45度照片");
            return;
        }
        if (etCarNo.getText().length() <= 0) {
            ToastUtil.showMessage(getContext(), "请输入车牌号");
            return;
        }
        if (tvBrand.getText().length() <= 0) {
            ToastUtil.showMessage(getContext(), "请选择车辆厂牌");
            return;
        }
        if (tvVehicle.getText().length() <= 0) {
            ToastUtil.showMessage(getContext(), "请选择车辆型号");
            return;
        }
        if (tvColor.getText().length() <= 0) {
            ToastUtil.showMessage(getContext(), "请选择车牌颜色");
            return;
        }
        if (tvCarColor.getText().length() <= 0) {
            ToastUtil.showMessage(getContext(), "请选择车身颜色");
            return;
        }
        if (tvCarType.getText().length() <= 0) {
            ToastUtil.showMessage(getContext(), "请选择车辆类型");
            return;
        }
        if (tvCarUse.getText().length() <= 0) {
            ToastUtil.showMessage(getContext(), "请选择车辆性质");
            return;
        }
        if (tvOil.getText().length() <= 0) {
            ToastUtil.showMessage(getContext(), "请选择燃料类型");
            return;
        }


        int seat = 0;
        try {
            seat = Integer.valueOf(seatNum.getText().toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (seat <= 0) {
            ToastUtil.showMessage(getContext(), "请输入核定载客人数");
            return;
        }

        float mileage = 0;
        try {
            mileage = Float.valueOf(mk.getText().toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (mileage <= 0) {
            ToastUtil.showMessage(getContext(), "请输入车辆已行驶公里数");
            return;
        }

        String vin = etVin.getText().toString();
        if (vin.length() != 17) {
            ToastUtil.showMessage(getContext(), "请输入17位车辆识别VIN");
            return;
        }
        if (TextUtils.isEmpty(tvBuy.getText())) {
            ToastUtil.showMessage(getContext(), "请输入购车日期");
            return;
        }
        if (TextUtils.isEmpty(tvRegister.getText())) {
            ToastUtil.showMessage(getContext(), "请输入车辆注册日期");
            return;
        }
        if (TextUtils.isEmpty(tvCheck.getText())) {
            ToastUtil.showMessage(getContext(), "请输入车辆年检日期");
            return;
        }
        if (registerRequest.drivingLicensePhoto == null) {
            ToastUtil.showMessage(getContext(), "未上传行驶证");
            return;
        }

        registerRequest.brand = tvBrand.getText().toString();
        registerRequest.model = tvVehicle.getText().toString();
        registerRequest.plateColor = tvColor.getText().toString();
        registerRequest.vehicleNo = etCarNo.getText().toString();
        registerRequest.vehicleType = tvCarType.getText().toString();
        registerRequest.seats = seat;
        registerRequest.mileage = mileage;
        registerRequest.useProperty = tvCarUse.getText().toString();
        registerRequest.vin = etVin.getText().toString();
        registerRequest.fuelType = tvOil.getText().toString();

        registerRequest.buyDate = parseData(tvBuy.getText().toString());
        registerRequest.certifyDate = parseData(tvRegister.getText().toString());
        registerRequest.nextFixDate = parseData(tvCheck.getText().toString());
        registerRequest.vehicleColor = tvCarColor.getText().toString();

        uploadAllPicsAndCommit(registerRequest);
    }


    /**
     * 解析时间字符串成时间戳,例如yyyy-MM-dd HH:mm格式的字符串2000-10-10 12:00解析成时间戳.
     */
    private long parseData(String source) {
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date data = mFormat.parse(source);
            return data.getTime() / 1000;
        } catch (ParseException ex) {
            ex.printStackTrace();
            return -1;
        }
    }


}
