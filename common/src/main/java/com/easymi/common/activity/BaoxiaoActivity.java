package com.easymi.common.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.easymi.common.CommApiService;
import com.easymi.common.R;
import com.easymi.component.Config;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.result.EmResult;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.LoadingButton;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by liuzihao on 2017/12/24.
 */

public class BaoxiaoActivity extends RxBaseActivity {

    CusToolbar cusToolbar;
    EditText editMoney;
    EditText editReason;

    LoadingButton applyBtn;

    private String baoxiaoReason;
    private double baoxiaoMoney = 0;

    private Long orderId;

    @Override
    public int getLayoutId() {
        return R.layout.activity_baoxiao;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        editMoney = findViewById(R.id.edit_baoxiao_money);
        editReason = findViewById(R.id.edit_baoxiao_reason);
        applyBtn = findViewById(R.id.apply_btn);

        orderId = getIntent().getLongExtra("orderId", -1);

        applyBtn.setOnClickListener(view -> baoxiaoMoney());

        editReason.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                baoxiaoReason = editable.toString();
                if (StringUtils.isNotBlank(editable.toString())) {
                    if (baoxiaoMoney != 0) {
                        setApplyBtnClickable(true);
                    } else {
                        setApplyBtnClickable(false);
                    }
                }
            }
        });

        editMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (StringUtils.isNotBlank(editable.toString())) {
                    baoxiaoMoney = Double.parseDouble(editable.toString());
                    if (null != baoxiaoReason && baoxiaoMoney != 0) {
                        setApplyBtnClickable(true);
                    } else {
                        setApplyBtnClickable(false);
                    }
                }
            }
        });
    }

    @Override
    public void initToolBar() {
        cusToolbar = findViewById(R.id.cus_toolbar);
        cusToolbar.setTitle(R.string.liushui_baoxiao);
        cusToolbar.setLeftBack(view -> finish());
    }

    private void baoxiaoMoney() {
        Observable<EmResult> observable = ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .baoxiao(orderId, baoxiaoMoney, baoxiaoReason, Config.APP_KEY)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, applyBtn, emResult -> {
            ToastUtil.showMessage(BaoxiaoActivity.this, getString(R.string.baoxiao_suc));
            finish();
        })));
    }

    private void setApplyBtnClickable(boolean clickable) {
        applyBtn.setClickable(clickable);
        if (clickable) {
            applyBtn.setBackgroundResource(R.drawable.corners_button_bg);
        } else {
            applyBtn.setBackgroundResource(R.drawable.corners_button_press_bg);
        }
    }
}
