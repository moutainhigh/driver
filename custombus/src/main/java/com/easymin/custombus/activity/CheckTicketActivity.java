package com.easymin.custombus.activity;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.network.GsonUtil;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.CustomSlideToUnlockView;
import com.easymi.component.widget.LoadingButton;
import com.easymin.custombus.R;
import com.easymin.custombus.entity.Customer;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: CheckTicketActivity
 * @Author: hufeng
 * @Date: 2019/2/15 下午1:25
 * @Description:
 * @History:
 */
public class CheckTicketActivity extends RxBaseActivity {
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
                if (s.toString().length() == 0){
                    lin_ticket.setVisibility(View.GONE);
                }else {
                    getData(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btn_get_on.setOnClickListener(v -> {
            finish();
        });
    }

    private Customer customer;

    public void getData(String keyword){
        customer = GsonUtil.parseJson(json, Customer.class);

        lin_ticket.setVisibility(View.VISIBLE);
        tv_name.setText(customer.name);
        tv_start.setText(customer.startAddr);
        tv_end.setText(customer.endAddr);
        tv_number.setText(customer.tickets+"");
        tv_remark.setText(customer.remark);

    }


    public String json = "{\n" +
            "            \"id\":1,\n" +
            "            \"name\":\"珠江国际\",\n" +
            "            \"status\":1,\n" +
            "            \"tickets\":1,\n" +
            "            \"phone\":\"18180635910\",\n" +
            "            \"pic\":\"http://img1.3lian.com/img013/v5/21/d/84.jpg\",\n" +
            "            \"startAddr\":\"珠江国际写字楼\",\n" +
            "            \"endAddr\":\"时代金悦\",\n" +
            "            \"remark\":\"要小哥哥接\"\n" +
            "        }";

}
