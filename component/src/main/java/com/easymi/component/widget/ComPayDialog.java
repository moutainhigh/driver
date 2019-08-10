package com.easymi.component.widget;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easymi.component.R;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: ComPayDialog
 * @Author: hufeng
 * @Date: 2019/5/22 下午2:59
 * @Description:
 * @History:
 */
public class ComPayDialog extends BaseBottomDialog {

    TextView tv_cancel;

    RelativeLayout pay_wenXin;
    RelativeLayout pay_zfb;
    RelativeLayout pay_balance;
    private TextView tv_prise;
    private double money = -1;

    public ComPayDialog(Context context) {
        super(context);
    }

    public void setMoney(double money) {
        this.money = money;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.com_dialog_pay);
        pay_wenXin = findViewById(R.id.pay_wenXin);
        pay_zfb = findViewById(R.id.pay_zfb);
        pay_balance = findViewById(R.id.pay_balance);
        tv_prise = findViewById(R.id.tv_prise);
        tv_cancel = findViewById(R.id.tv_cancel);
        if (money >= 0) {
            tv_prise.setVisibility(View.VISIBLE);
            tv_prise.setText("￥" + money);
        }
        pay_wenXin.setOnClickListener(view -> {
            if (onMyClickListener != null) {
                onMyClickListener.onItemClick(view);
            }
        });
        pay_zfb.setOnClickListener(view -> {
            if (onMyClickListener != null) {
                onMyClickListener.onItemClick(view);
            }
        });
        pay_balance.setOnClickListener(view -> {
            if (onMyClickListener != null) {
                onMyClickListener.onItemClick(view);
            }
        });
        tv_cancel.setOnClickListener(view -> {
            dismiss();
        });
    }
}