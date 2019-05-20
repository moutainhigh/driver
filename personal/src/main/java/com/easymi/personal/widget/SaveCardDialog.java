package com.easymi.personal.widget;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.easymi.component.widget.BaseBottomDialog;
import com.easymi.personal.R;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: SaveCardDialog
 * @Author: hufeng
 * @Date: 2019/5/20 下午5:38
 * @Description:
 * @History:
 */
public class SaveCardDialog extends BaseBottomDialog {

    TextView tv_save_card;

    TextView tv_cancel;

    public SaveCardDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_sava_card);

        tv_save_card = findViewById(R.id.tv_save_card);
        tv_cancel = findViewById(R.id.tv_cancel);

        tv_save_card.setOnClickListener(view -> {
            if (onMyClickListener != null) {
                onMyClickListener.onItemClick(view);
            }
        });
        tv_cancel.setOnClickListener(view -> {
            dismiss();
        });
    }
}
