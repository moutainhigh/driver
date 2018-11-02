package com.easymi.common.register;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.easymi.common.R;
import com.easymi.common.entity.BusinessList;
import com.easymi.common.entity.CompanyList;
import com.easymi.component.entity.Employ;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.GlideCircleTransform;
import com.easymi.component.utils.ToastUtil;

import java.util.List;

import rx.Observable;

public class InfoFragment extends AbsRegisterFragment {

    private static final int REQUEST_HEAD = 1;

    private RequestOptions options = new RequestOptions()
            .centerCrop()
            .transform(new GlideCircleTransform())
            .diskCacheStrategy(DiskCacheStrategy.ALL);

    private List<CompanyList.Company> companies;
    private CompanyList.Company selectedCompany;
    private String headPath;
    private Employ employ;

    private OnSelectPicListener onSelectPicListener = new OnSelectPicListener() {
        @Override
        public void onSelectPicResult(int requestCode, Uri picUri) {
            switch (requestCode) {
                case REQUEST_HEAD:
                    Glide.with(InfoFragment.this)
                            .load(picUri)
                            .apply(options)
                            .into(imvHead);
                    headPath = picUri.getPath();
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.com_fragment_info, container, false);
        getCompany();
        getBusinessList();
        bindViews(view);
        employ = EmUtil.getEmployInfo();
        if (employ != null) {
            tvName.setText(employ.name);
            tvPhone.setText(employ.phone);
        }
        return view;
    }

    private RadioButton daijiaRb;
    private RadioButton zhuancheRb;
    private RadioGroup zhuanceType;
    private TextView tvCompany;
    private ImageView imvHead;
    private EditText idCard, emergency, emergencyPhone, introducer;
    private TextView tvName, tvPhone;
    private CheckBox cbAgreement;

    private void bindViews(View view) {
        //选公司
        RadioGroup businessRG = view.findViewById(R.id.businessRG);
        daijiaRb = view.findViewById(R.id.daijia);
        zhuancheRb = view.findViewById(R.id.zhuanche);
        zhuanceType = view.findViewById(R.id.zhuanceType);
        tvCompany = view.findViewById(R.id.tvCompany);
        imvHead = view.findViewById(R.id.head);
        idCard = view.findViewById(R.id.idCard);
        emergency = view.findViewById(R.id.emergency);
        emergencyPhone = view.findViewById(R.id.emergencyPhone);
        introducer = view.findViewById(R.id.introducer);
        tvPhone = view.findViewById(R.id.tvPhone);
        tvName = view.findViewById(R.id.tvName);
        cbAgreement = view.findViewById(R.id.cbAgreement);

        //设置红色星号
        TextView titleName = view.findViewById(R.id.titleName);
        TextView titlePhone = view.findViewById(R.id.titlePhone);
        TextView titleIdCard = view.findViewById(R.id.titleIdCard);
        TextView titleEmergency = view.findViewById(R.id.titleEmergency);
        TextView titleEmergencyPhone = view.findViewById(R.id.titleEmergencyPhone);
        InfoActivity.setTVs(titleName,titlePhone,titleIdCard,titleEmergency,titleEmergencyPhone);

        view.findViewById(R.id.tvCompany).setOnClickListener(v -> {
            if (companies == null || getActivity() == null) {
                return;
            }
            new CompanyPicker(getActivity(), companies).setOnSelectListener(this::switchCompany).show();
        });

        businessRG.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.daijia) {
                zhuanceType.setVisibility(View.GONE);
            } else if (checkedId == R.id.zhuanche) {
                zhuanceType.setVisibility(View.VISIBLE);
            }
        });

        imvHead.setOnClickListener(v -> choicePic(REQUEST_HEAD, 1, 1));

        view.findViewById(R.id.tvAgreement).setOnClickListener(v -> ARouter.getInstance()
                .build("/personal/ArticleActivity")
                .withString("tag", "DriverServiceAgreement")
                .withString("title", "服务人员协议")
                .navigation());


        view.findViewById(R.id.next).setOnClickListener(v -> {
            if (!check()) {
                next();
            }
        });
    }

    private boolean check() {
        if (employ == null) {
            ToastUtil.showMessage(getActivity(), "服务人员信息异常");
            return true;
        }
        if (headPath == null) {
            ToastUtil.showMessage(getActivity(), "未上传头像");
            return true;
        }
        if (idCard.getText().toString().length() != 18) {
            ToastUtil.showMessage(getActivity(), "身份证号码错误");
            return true;
        }
        if (emergency.getText().length() <= 0) {
            ToastUtil.showMessage(getActivity(), "未填写紧急联系人");
            return true;
        }
        if (emergencyPhone.getText().length() <= 0) {
            ToastUtil.showMessage(getActivity(), "未填写紧急联系人电话");
            return true;
        }
        if (selectedCompany == null) {
            return true;
        }
        if (!daijiaRb.isChecked() && zhuanceType.getCheckedRadioButtonId() == -1) {
            ToastUtil.showMessage(getActivity(), "是否有车注册？");
            return true;
        }
        if (!cbAgreement.isChecked()) {
            ToastUtil.showMessage(getActivity(), "未同意《服务协议》");
            return true;
        }
        return false;
    }

    private void next() {
        FragmentManager fm = getFragmentManager();
        if (fm == null) {
            return;
        }

        //个人信息参数
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.driverId = employ.id;
        registerRequest.idCard = idCard.getText().toString();
        registerRequest.emergency = emergency.getText().toString();
        registerRequest.emergencyPhone = emergencyPhone.getText().toString();
        registerRequest.companyId = selectedCompany.id;
        registerRequest.introducer = introducer.getText().toString();
        registerRequest.portraitPath = headPath;
        if (daijiaRb.isChecked()) {
            registerRequest.serviceType = "daijia";
        } else {
            registerRequest.serviceType = "zhuanche";
        }

        if (zhuancheRb.isChecked() && zhuanceType.getCheckedRadioButtonId() == R.id.car) {
            //标记为有车注册
            registerRequest.needCarInfo = true;
        }

        AbsRegisterFragment fragment = PersonInfoFragment.newInstance(registerRequest);
        FragmentTransaction ts = fm.beginTransaction();
        ts.hide(InfoFragment.this).add(R.id.registerContainer, fragment, fragment.getClass().getName());
        ts.addToBackStack(null);
        ts.commitAllowingStateLoss();
    }

    /**
     * 初始化业务种类.
     */
    private void initBusiness(List<BusinessList.Business> businesses) {
        if (businesses == null || businesses.size() == 0) {
            return;
        }
        for (BusinessList.Business bs : businesses) {
            if ("daijia".equals(bs.business)) {
                daijiaRb.setVisibility(View.VISIBLE);
            } else if ("zhuanche".equals(bs.business)) {
                zhuancheRb.setVisibility(View.VISIBLE);
            }
        }
        //设置显示第一个view
        if (daijiaRb.getVisibility() == View.VISIBLE) {
            daijiaRb.setChecked(true);
        } else {
            zhuancheRb.setChecked(true);
        }
    }

    /**
     * 切换公司.
     */
    private void switchCompany(CompanyList.Company company) {
        if (company == null) {
            return;
        }
        tvCompany.setText(company.companyName);
        selectedCompany = company;
    }

    private void getCompany() {
        Observable<CompanyList> observable = RegisterModel.getCompany();
        mRxManager.add(observable.subscribe(new MySubscriber<>(getActivity(), false, false, companyList -> {
            companies = companyList.companies;
            if (companies != null && companies.size() > 0) {
                switchCompany(companies.get(0));
            }
        })));
    }

    private void getBusinessList() {
        Observable<BusinessList> observable = RegisterModel.getBusinessList();
        mRxManager.add(observable.subscribe(new MySubscriber<>(getActivity(), false, false, businessList ->
                initBusiness(businessList.businesses))));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null || resultCode != Activity.RESULT_OK) {
            return;
        }
        handlePic(requestCode, resultCode, data, onSelectPicListener);
    }

}
