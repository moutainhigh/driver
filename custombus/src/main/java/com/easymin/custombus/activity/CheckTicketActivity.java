package com.easymin.custombus.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.widget.LoadingButton;
import com.easymin.custombus.R;
import com.easymin.custombus.entity.CbBusOrder;
import com.easymin.custombus.entity.Customer;
import com.easymin.custombus.mvp.FlowContract;
import com.easymin.custombus.mvp.FlowPresenter;

import java.util.List;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: CheckTicketActivity
 * @Author: hufeng
 * @Date: 2019/2/15 下午1:25
 * @Description:
 * @History:
 */
public class CheckTicketActivity extends RxBaseActivity implements FlowContract.View{
    /**
     * 界面控件
     */
    EditText et_ticket;
    TextView tv_cancel;
    TextView tv_name;
    TextView tv_start;
    TextView tv_end;
    TextView tv_number;
    TextView tv_remark;
    LoadingButton btn_get_on;
    LinearLayout lin_ticket;

    private FlowPresenter presenter;

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_check_ticket;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        findById();
        initListener();
        presenter = new FlowPresenter(this,this);
    }

    /**
     * 加载控件
     */
    public void findById() {
        et_ticket = findViewById(R.id.et_ticket);
        tv_cancel = findViewById(R.id.tv_cancel);
        tv_name = findViewById(R.id.tv_name);
        tv_start = findViewById(R.id.tv_start);
        tv_end = findViewById(R.id.tv_end);
        tv_number = findViewById(R.id.tv_number);
        tv_remark = findViewById(R.id.tv_remark);
        btn_get_on = findViewById(R.id.btn_get_on);
        lin_ticket = findViewById(R.id.lin_ticket);
    }

    public void initListener() {
        tv_cancel.setOnClickListener(v -> {
            finish();
        });

        et_ticket.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() != 6){
                    lin_ticket.setVisibility(View.GONE);
                }else {
                    presenter.queryByRideCode(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btn_get_on.setOnClickListener(v -> {
            presenter.checkRideCode(et_ticket.getText().toString().trim(),btn_get_on);
        });
    }

    public void setData(Customer customer){
        lin_ticket.setVisibility(View.VISIBLE);

        tv_name.setText(customer.passengerName);
        tv_start.setText(customer.startStationName);
        tv_end.setText(customer.endStationName);
        tv_number.setText(customer.ticketNumber+"");
        tv_remark.setText(customer.orderRemark);

        if (customer.status <= Customer.CITY_COUNTRY_STATUS_ARRIVED){
            btn_get_on.setVisibility(View.VISIBLE);
            btn_get_on.setClickable(true);
            btn_get_on.setBackgroundResource(R.drawable.corners_button_bg);
            btn_get_on.setText(getResources().getString(R.string.cb_confirm_get_on));
        }else {
            btn_get_on.setBackgroundResource(R.drawable.cormers_btn_grey_bg);
            btn_get_on.setClickable(false);
            btn_get_on.setText(getResources().getString(R.string.cb_already_get_on));
        }
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
        setMyResult();
    }

    @Override
    public void succeseOrder(Customer customer) {
        setData(customer);
    }

    @Override
    public void finishActivity() {

    }

    @Override
    public RxManager getManager() {
        return mRxManager;
    }

    public void setMyResult(){
        setResult(RESULT_OK);
        finish();
    }
}
