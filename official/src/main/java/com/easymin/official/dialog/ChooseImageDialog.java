package com.easymin.official.dialog;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.easymi.component.widget.BaseBottomDialog;
import com.easymin.official.R;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: ChooseImageDialog
 * @Author: hufeng
 * @Date: 2019/3/29 上午9:33
 * @Description:
 * @History:
 */
public class ChooseImageDialog extends BaseBottomDialog {

    TextView tv_camera;
    TextView tv_photo;
    TextView tv_cancel;

    public ChooseImageDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_choose_image);

        tv_camera = findViewById(R.id.tv_camera);
        tv_photo = findViewById(R.id.tv_photo);
        tv_cancel = findViewById(R.id.tv_cancel);

        tv_camera.setOnClickListener(view -> {
            if (onMyClickListener != null) {
                onMyClickListener.onItemClick(view);
            }
        });
        tv_photo.setOnClickListener(view -> {
            if (onMyClickListener != null) {
                onMyClickListener.onItemClick(view);
            }
        });
        tv_cancel.setOnClickListener(view -> {
            dismiss();
        });

    }
}
