package com.easymin.custombus.dialog;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.easymi.component.widget.BaseCenterDialog;
import com.easymin.custombus.R;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: GoNextDialog
 * @Author: hufeng
 * @Date: 2019/3/13 下午7:33
 * @Description:  前往下一站确认弹窗
 * @History:
 */
public class GoNextDialog extends BaseCenterDialog{


    TextView tv_sure;
    TextView tv_cancel;

    public GoNextDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_go_next);
        tv_sure = findViewById(R.id.tv_sure);
        tv_cancel = findViewById(R.id.tv_cancel);

        tv_sure.setOnClickListener(view -> {
            if (getOnMyClickListener() != null) {
                getOnMyClickListener().onItemClick(view, "");
            }
            dismiss();
        });
        tv_cancel.setOnClickListener(view -> {
            dismiss();
        });
    }
}
