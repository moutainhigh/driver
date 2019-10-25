package com.easymi.common.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioGroup;

import com.easymi.common.CommApiService;
import com.easymi.common.R;
import com.easymi.component.Config;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.network.NoErrSubscriberListener;
import com.easymi.component.result.EmResult;
import com.easymi.component.utils.ToastUtil;

import java.util.HashMap;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PassengerAddActivity extends RxBaseActivity {

    private com.easymi.component.widget.CusToolbar passengerAddCtb;
    private android.widget.RadioGroup passengerAddRg;
    private android.widget.EditText passengerAddEtName;
    private android.widget.EditText passengerAddEtGuardianName;
    private android.widget.EditText passengerAddEtNum;
    private android.widget.EditText passengerAddEtId;
    private android.widget.TextView passengerAddTvSave;
    private android.widget.FrameLayout passengerAddFlGuardian;
    private android.widget.TextView passengerAddTvNum;
    private android.widget.TextView passengerAddTvId;
    private long passengerId;

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_passenger_add;
    }

    @Override
    public void initToolBar() {
        super.initToolBar();
        passengerAddCtb.setTitle("新增乘客")
                .setLeftIcon(R.drawable.ic_arrow_back, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        passengerId = getIntent().getLongExtra("passengerId", 0);

        passengerAddCtb = findViewById(R.id.passengerAddCtb);

        passengerAddRg = findViewById(R.id.passengerAddRg);
        passengerAddEtName = findViewById(R.id.passengerAddEtName);
        passengerAddEtGuardianName = findViewById(R.id.passengerAddEtGuardianName);
        passengerAddEtNum = findViewById(R.id.passengerAddEtNum);
        passengerAddEtId = findViewById(R.id.passengerAddEtId);
        passengerAddTvSave = findViewById(R.id.passengerAddTvSave);
        passengerAddFlGuardian = findViewById(R.id.passengerAddFlGuardian);
        passengerAddTvNum = findViewById(R.id.passengerAddTvNum);
        passengerAddTvId = findViewById(R.id.passengerAddTvId);

        passengerAddRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.passengerAddRbAdult) {
                    passengerAddFlGuardian.setVisibility(View.GONE);
                    passengerAddTvNum.setText("电话号码");
                    passengerAddEtNum.setHint("请输入电话号码");
                    passengerAddTvId.setText("身份证号码");
                    passengerAddEtId.setHint("请输入身份证号码");
                } else {
                    passengerAddFlGuardian.setVisibility(View.VISIBLE);
                    passengerAddTvNum.setText("监护人电话");
                    passengerAddEtNum.setHint("请输入监护人电话");
                    passengerAddTvId.setText("监护人身份证");
                    passengerAddEtId.setHint("请输入监护人身份证");
                }
            }
        });

        passengerAddTvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPassenger();
            }
        });

    }

    public void addPassenger() {
        if (TextUtils.isEmpty(passengerAddEtName.getText())) {
            ToastUtil.showMessage(PassengerAddActivity.this, "请输入乘客姓名");
            return;
        }
        if (passengerAddRg.getCheckedRadioButtonId() == R.id.passengerAddRbChild &&
                TextUtils.isEmpty(passengerAddEtGuardianName.getText())) {
            ToastUtil.showMessage(PassengerAddActivity.this, "请输入监护人姓名");
            return;
        }
        if (TextUtils.isEmpty(passengerAddEtNum.getText())) {
            ToastUtil.showMessage(PassengerAddActivity.this,
                    passengerAddRg.getCheckedRadioButtonId() == R.id.passengerAddRbChild ?
                            "请输入监护人电话" : "请输入电话号码");
            return;
        }
        if (TextUtils.isEmpty(passengerAddEtId.getText())) {
            ToastUtil.showMessage(PassengerAddActivity.this,
                    passengerAddRg.getCheckedRadioButtonId() == R.id.passengerAddRbChild ?
                            "请输入监护人身份证" : "请输入身份证号码");
            return;
        }

        Map<String, String> data = new HashMap<>();
        data.put("riderName", passengerAddEtName.getText().toString());
        boolean isChild = passengerAddRg.getCheckedRadioButtonId() == R.id.passengerAddRbChild;
        if (isChild) {
            data.put("riderType", String.valueOf(2));
            data.put("guardianName", passengerAddEtGuardianName.getText().toString());
            data.put("guardianCard", passengerAddEtId.getText().toString());
            data.put("guardianPhone", passengerAddEtNum.getText().toString());
        } else {
            data.put("riderType", String.valueOf(1));
            data.put("riderCard", passengerAddEtId.getText().toString());
            data.put("riderPhone", passengerAddEtNum.getText().toString());
        }
        data.put("passengerId", String.valueOf(passengerId));

        mRxManager.add(ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .addPassenger(data)
                .subscribeOn(Schedulers.io())
                .filter(new HttpResultFunc<>())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MySubscriber<EmResult>(PassengerAddActivity.this, true, false, new NoErrSubscriberListener<EmResult>() {
                    @Override
                    public void onNext(EmResult emResult) {
                        ToastUtil.showMessage(PassengerAddActivity.this, "新增乘客成功");
                        finish();
                    }
                })));
    }

}
