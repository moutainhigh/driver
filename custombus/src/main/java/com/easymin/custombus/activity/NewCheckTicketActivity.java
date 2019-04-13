package com.easymin.custombus.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;

import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.CodeInput;
import com.easymi.component.widget.CusToolbar;
import com.easymin.custombus.R;
import com.easymin.custombus.entity.CbBusOrder;
import com.easymin.custombus.entity.Customer;
import com.easymin.custombus.mvp.FlowContract;
import com.easymin.custombus.mvp.FlowPresenter;

import java.util.List;

import kotlin.Unit;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: NewCheckTicketActivity
 * @Author: hufeng
 * @Date: 2019/4/9 下午3:41
 * @Description:  新验票界面
 * @History:
 */
public class NewCheckTicketActivity extends RxBaseActivity implements FlowContract.View {

    private CusToolbar cusToolbar;
    private CodeInput codeInput;

    private FlowPresenter presenter;

    private String imagecode;

    private int isUnCheck;

    @Override
    public void initToolBar() {
        super.initToolBar();
        cusToolbar.setTitle(R.string.cb_check_ticket);
        cusToolbar.setLeftIcon(R.drawable.ic_arrow_back, v -> {
            setMyResult();
        });
    }

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_new_check_ticket;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        cusToolbar = findViewById(R.id.cus_toolbar);
        codeInput = findViewById(R.id.input_code);

        presenter = new FlowPresenter(this, this);

        codeInput.postDelayed(() -> {
            InputMethodManager imm= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm !=null){
                codeInput.requestFocus();
                imm.showSoftInput(codeInput,0);
            }
        }, 100);

        codeInput.setOnCodeListener(s -> {
            imagecode = s.toString();
            presenter.queryByRideCode(imagecode);
            return Unit.INSTANCE;
        });

        isUnCheck = getIntent().getIntExtra("isUnCheck",0);
    }

    @Override
    public void showLeft(int dis, int time) {

    }

    @Override
    public void showBusLineInfo(CbBusOrder cbBusOrder) {

    }

    @Override
    public void showcheckTime(long time) {

    }

    @Override
    public void showOrders(List<Customer> customers) {

    }

    @Override
    public void dealSuccese() {
        isUnCheck--;
        if (isUnCheck != 0){
            codeInput.setCode("");
            ToastUtil.showMessage(this, "验票成功");
        }else {
            setMyResult();
        }
    }

    @Override
    public void succeseOrder(Customer customer) {
        if (customer == null) {
            ToastUtil.showMessage(this, "乘车码错误");
            codeInput.setCode("");
        }else {
            if (customer.status <= Customer.CITY_COUNTRY_STATUS_ARRIVED){
                presenter.checkRideCode(imagecode,null);
            }else {
                ToastUtil.showMessage(this, "该票已经验证啦");
                codeInput.setCode("");
            }
        }
    }

    @Override
    public void finishActivity() {

    }

    @Override
    public RxManager getManager() {
        return mRxManager;
    }

    public void setMyResult() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onBackPressed() {
        cusToolbar.leftIcon.callOnClick();
    }
}
