package com.easymi.taxi.widget;

import android.content.Context;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.easymi.component.widget.BaseCenterDialog;
import com.easymi.taxi.R;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: TaxiSettleDialog
 * @Author: shine
 * Date: 2018/11/16 上午11:28
 * Description: 出租车结算弹窗
 * History:
 */
public class TaxiSettleDialog extends BaseCenterDialog {

    public TaxiSettleDialog(Context context) {
        super(context);
    }

    EditText et_input;
    TextView tv_sure;
    TextView tv_cancel;

    /**
     *  0普通文本 1纯数字 2价格
     */
    int inpuType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_taxi_settle);

        et_input = findViewById(R.id.et_input);
        tv_sure = findViewById(R.id.tv_sure);
        tv_cancel = findViewById(R.id.tv_cancel);

        et_input.setInputType(EditorInfo.TYPE_CLASS_NUMBER | EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);

        tv_sure.setOnClickListener(view -> {
            if (getOnMyClickListener() != null) {
                getOnMyClickListener().onItemClick(view, et_input.getText().toString());
            }
            dismiss();
        });
        tv_cancel.setOnClickListener(view -> {
            dismiss();
        });

    }
}
