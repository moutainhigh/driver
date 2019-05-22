package com.easymi.common.widget;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easymi.common.R;
import com.easymi.component.Config;
import com.easymi.component.entity.Employ;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.widget.BaseBottomDialog;

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

    public ComPayDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.com_dialog_pay);

        pay_wenXin = findViewById(R.id.pay_wenXin);
        pay_zfb = findViewById(R.id.pay_zfb);
        pay_balance = findViewById(R.id.pay_balance);

        tv_cancel = findViewById(R.id.tv_cancel);

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